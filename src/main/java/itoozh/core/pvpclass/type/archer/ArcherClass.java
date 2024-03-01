package itoozh.core.pvpclass.type.archer;

import cn.nukkit.Player;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.TextFormat;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import itoozh.core.Main;
import itoozh.core.event.TimerExpireEvent;
import itoozh.core.pvpclass.PvPClass;
import itoozh.core.pvpclass.PvPClassManager;
import itoozh.core.pvpclass.cooldown.ClassBuff;
import itoozh.core.session.timer.ArcherTagTimer;
import itoozh.core.util.ItemUtil;
import itoozh.core.util.LanguageUtils;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ArcherClass extends PvPClass implements Listener {

    private final Map<EntityArrow, Double> arrowForce;
    private final ArcherTagTimer archerTag;
    private final Table<Integer, Short, ClassBuff> buffs;

    public ArcherClass(PvPClassManager manager) {
        super(manager, "Archer");
        this.buffs = HashBasedTable.create();
        this.arrowForce = new HashMap<>();
        this.archerTag = Main.getInstance().getTimerManager().getArcherTagTimer();
        this.load();
    }

    @Override
    public void load() {
        for (String s : Main.getInstance().getConfig().getSection("ARCHER_CLASS.ARCHER_BUFFS").getKeys(false)) {
            String name = "ARCHER_CLASS.ARCHER_BUFFS." + s + ".";
            String material = Main.getInstance().getConfig().getString(name + "MATERIAL");
            String displayName = Main.getInstance().getConfig().getString(name + "DISPLAY_NAME");
            Effect effect = ItemUtil.getEffect(Main.getInstance().getConfig().getString(name + "EFFECT"));
            if (effect == null) {
                Main.getInstance().getServer().getLogger().info("Effect not found: " + Main.getInstance().getConfig().getString(name + "EFFECT"));
                continue;
            }
            int data = Main.getInstance().getConfig().getInt(name + "DATA");
            int cooldown = Main.getInstance().getConfig().getInt(name + "COOLDOWN");
            this.buffs.put(Item.fromString(material).getId(), (short) data, new ClassBuff(this, displayName, effect, cooldown));
        }
    }

    @EventHandler
    public void onExpire(TimerExpireEvent event) {
        if (!(event.getPlayerTimer() instanceof ArcherTagTimer)) {
            return;
        }
        Main.getInstance().getNameTags().update();
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }
        Player player = event.getPlayer();
        Item stack = player.getInventory().getItemInHand();
        if (stack.getId() == Item.AIR) {
            return;
        }
        if (!this.players.contains(player.getUniqueId())) {
            return;
        }

        ClassBuff classBuff = this.buffs.get(stack.getId(), (short) stack.getDamage());
        if (classBuff != null) {
            if (classBuff.hasCooldown(player)) {
                player.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_CLASSES.ARCHER_CLASS.BUFF_COOLDOWN").replaceAll("%seconds%", classBuff.getRemaining(player))));
                return;
            }
            classBuff.applyCooldown(player, classBuff.getCooldown());
            Main.getInstance().getPvPClassManager().addEffect(player, classBuff.getEffect().clone());
            stack.setCount(stack.getCount() - 1);
            player.getInventory().setItemInHand(stack);
        }
    }

    @Override
    public void handleUnequip(Player player) {
    }

    @Override
    public void handleEquip(Player player) {
    }

    @EventHandler
    public void onArrow(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
                Player shooter = (Player) event.getDamager();
                Player hitPlayer = (Player) event.getEntity();

                if (!this.players.contains(shooter.getUniqueId())) {
                    return;
                }

                float damage = event.getDamage();

                this.archerTag(shooter, hitPlayer, damage);
            }
        }
    }

    public void archerTag(Player damager, Player damaged, float damage) {
        if (!Main.getInstance().getTeamManager().canHit(damager, damaged, false)) {
            return;
        }
        if (this.players.contains(damaged.getUniqueId())) {
            damager.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_CLASSES.ARCHER_CLASS.CANNOT_MARK")));
            return;
        }

        int distance = (int) damaged.getLocation().distance(damager.getLocation());

        damager.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_CLASSES.ARCHER_CLASS.MARKED_PLAYER").replaceAll("%distance%", String.valueOf(distance)).replaceAll("%seconds%", String.valueOf(this.archerTag.getSeconds())).replaceAll("%damage%", damage + " heart" + ((damage > 1.0) ? "s" : ""))));
        if (!this.archerTag.hasTimer(damaged)) {
            damaged.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_CLASSES.ARCHER_CLASS.PLAYER_MARKED").replaceAll("%seconds%", String.valueOf(this.archerTag.getSeconds()))));
            this.archerTag.applyTimer(damaged);
            Main.getInstance().getNameTags().update();
            return;
        }
        this.archerTag.applyTimer(damaged);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (this.archerTag.hasTimer(player)) {
            event.setDamage(event.getDamage() * Float.valueOf(Main.getInstance().getConfig().getString("ARCHER_CLASS.TAGGED_DAMAGE_MULTIPLIER")));
        }
    }
}
