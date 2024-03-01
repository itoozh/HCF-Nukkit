package itoozh.core.pvpclass.type.rogue;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.TextFormat;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import itoozh.core.Main;
import itoozh.core.pvpclass.PvPClass;
import itoozh.core.pvpclass.PvPClassManager;
import itoozh.core.pvpclass.cooldown.ClassBuff;
import itoozh.core.pvpclass.cooldown.CustomCooldown;
import itoozh.core.scoreboard.ScoreboardUtils;
import itoozh.core.util.ItemUtil;
import itoozh.core.util.LanguageUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class RogueClass extends PvPClass implements Listener {
    private final CustomCooldown backstabCooldown;
    private final Table<Integer, Short, ClassBuff> buffs;
    private final List<Effect> backstabEffects;
    private final Item backstabItem;

    public RogueClass(PvPClassManager manager) {
        super(manager, "Rogue");
        this.buffs = HashBasedTable.create();
        this.backstabEffects = new ArrayList<>();
        this.backstabCooldown = new CustomCooldown(this, ScoreboardUtils.scoreboardConfig.getString("ROGUE_CLASS.BACKSTAB"));
        this.backstabItem = Item.fromString(Main.getInstance().getConfig().getString("ROGUE_CLASS.BACKSTAB_ITEM"));
        this.load();
    }

    @EventHandler
    public void onBackstab(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player damager = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();
        if (damager.getInventory().getItemInHand().getId() != this.backstabItem.getId()) {
            return;
        }
        if (!this.players.contains(damager.getUniqueId())) {
            return;
        }
        if (!Main.getInstance().getTeamManager().canHit(damager, damaged, false)) {
            return;
        }

        double jugador1Yaw = damager.getYaw() % 360;
        double jugador2Yaw = damaged.getYaw() % 360;
        double diferenciaYaw = Math.abs(jugador1Yaw - jugador2Yaw);
        float configAngle = (float) (Main.getInstance().getConfig().getDouble("ROGUE_CLASS.BACKSTAB_DAMAGE") * 2.0);
        float maxAllowedAngle = 45.0f;

        if (diferenciaYaw < maxAllowedAngle || diferenciaYaw > 360 - maxAllowedAngle) { // Cambiamos la condición
            if (this.backstabCooldown.hasCooldown(damager)) {
                damager.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_CLASSES.ROGUE_CLASS.BACKSTAB_COOLDOWN").replaceAll("%seconds%", this.backstabCooldown.getRemaining(damager))));
                return;
            }
            this.backstabCooldown.applyCooldown(damager, Main.getInstance().getConfig().getInt("ROGUE_CLASS.BACKSTAB_COOLDOWN"));
            damager.getInventory().setItemInHand(Item.get(Item.AIR));
            damaged.getLevel().addSound(damaged.getLocation(), Sound.valueOf(Main.getInstance().getConfig().getString("ROGUE_CLASS.BACKSTAB_SOUND")), 1.0f, 1.0f);
            damaged.getLevel().addParticle(new DestroyBlockParticle(damaged.getLocation().add(0, 1, 0), Item.fromString(Main.getInstance().getConfig().getString("ROGUE_CLASS.BACKSTAB_EFFECT")).getBlock()));
            damaged.setHealth((float) Math.max(damaged.getHealth() - configAngle, 0.0));
            for (Effect effect : this.backstabEffects) {
                damager.addEffect(effect.clone());
            }
        } else {
            damager.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_CLASSES.ROGUE_CLASS.BACKSTAB_FAILED")));
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }
        Player player = event.getPlayer();
        Item stack = player.getInventory().getItemInHand();
        if (stack == null) {
            return;
        }
        if (!this.players.contains(player.getUniqueId())) {
            return;
        }

        ClassBuff classBuff = this.buffs.get(stack.getId(), (short) stack.getDamage());
        if (classBuff != null) {
            if (classBuff.hasCooldown(player)) {
                player.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_CLASSES.ROGUE_CLASS.BUFF_COOLDOWN").replaceAll("%seconds%", classBuff.getRemaining(player))));
                return;
            }
            classBuff.applyCooldown(player, classBuff.getCooldown());
            Main.getInstance().getPvPClassManager().addEffect(player, classBuff.getEffect().clone());

            Item stackClone = stack.clone();
            stackClone.setCount(stackClone.getCount() - 1);
            player.getInventory().setItemInHand(stackClone);
        }
    }

    @Override
    public void handleEquip(Player player) {
    }

    @Override
    public void handleUnequip(Player player) {
    }

    @Override
    public void load() {
        this.backstabEffects.addAll(Main.getInstance().getConfig().getStringList("ROGUE_CLASS.BACKSTAB_EFFECTS").stream().map(ItemUtil::getEffect).collect(Collectors.toList()));
        for (String s : Main.getInstance().getConfig().getSection("ROGUE_CLASS.ROGUE_BUFFS").getKeys(false)) {
            String path = "ROGUE_CLASS.ROGUE_BUFFS." + s + ".";
            String material = Main.getInstance().getConfig().getString(path + "MATERIAL");
            String displayName = Main.getInstance().getConfig().getString(path + "DISPLAY_NAME");
            Effect effect = ItemUtil.getEffect(Main.getInstance().getConfig().getString(path + "EFFECT"));
            int data = Main.getInstance().getConfig().getInt(path + "DATA");
            int cooldown = Main.getInstance().getConfig().getInt(path + "COOLDOWN");
            this.buffs.put(Item.fromString(material).getId(), (short) data, new ClassBuff(this, displayName, effect, cooldown));
        }
    }
}
