package itoozh.core.pvpclass.type.mage;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import itoozh.core.Main;
import itoozh.core.pvpclass.PvPClass;
import itoozh.core.pvpclass.PvPClassManager;
import itoozh.core.pvpclass.cooldown.CustomCooldown;
import itoozh.core.pvpclass.cooldown.EnergyCooldown;
import itoozh.core.scoreboard.ScoreboardUtils;
import itoozh.core.team.claim.ClaimType;
import itoozh.core.timer.TimerManager;
import itoozh.core.util.LanguageUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MageClass extends PvPClass implements Listener {
    private final CustomCooldown mageEffectCooldown;
    private final Map<UUID, EnergyCooldown> mageEnergy;
    private final int maxMageEnergy;
    private final int mageCooldown;
    private final Table<Integer, Short, MageEffect> clickableEffects;

    public MageClass(PvPClassManager manager) {
        super(manager, "Mage");
        this.mageEnergy = new HashMap<>();
        this.clickableEffects = HashBasedTable.create();
        this.mageEffectCooldown = new CustomCooldown(this, ScoreboardUtils.scoreboardConfig.getString("MAGE_CLASS.MAGE_EFFECT"));
        this.maxMageEnergy = Main.getInstance().getConfig().getInt("MAGE_CLASS.MAX_ENERGY");
        this.mageCooldown = Main.getInstance().getConfig().getInt("MAGE_CLASS.MAGE_COOLDOWN");
        this.load();
    }

    @Override
    public void handleEquip(Player player) {
        this.mageEnergy.put(player.getUniqueId(), new EnergyCooldown(player.getUniqueId(), this.maxMageEnergy));
    }

    @Override
    public void handleUnequip(Player player) {
        this.mageEnergy.remove(player.getUniqueId());
    }

    public EnergyCooldown getEnergyCooldown(Player player) {
        return this.mageEnergy.get(player.getUniqueId());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }
        Player player = event.getPlayer();
        Item stack = event.getItem();
        if (stack != null && this.players.contains(player.getUniqueId())) {
            short data = (short) stack.getDamage();
            MageEffect effect = this.clickableEffects.get(stack.getId(), data);

            if (effect == null) {
                return;
            }
            if (this.mageEffectCooldown.hasCooldown(player)) {
                player.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_CLASSES.MAGE_CLASS.BUFF_COOLDOWN").replaceAll("%seconds%", this.mageEffectCooldown.getRemaining(player))));
                return;
            }
            if (this.getEnergyCooldown(player).checkEnergy(effect.getEnergyRequired())) {
                player.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_CLASSES.MAGE_CLASS.INSUFFICIENT_ENERGY").replaceAll("%energy%", String.valueOf(effect.getEnergyRequired()))));
                return;
            }
            if (this.checkMage(player)) {
                return;
            }
            Main.getInstance().getTimerManager().getCombatTimer().applyTimer(player);
            this.getEnergyCooldown(player).takeEnergy(effect.getEnergyRequired());

            Item stackClone = stack.clone();
            stackClone.setCount(stackClone.getCount() - 1);
            player.getInventory().setItemInHand(stackClone);

            this.mageEffectCooldown.applyCooldown(player, this.mageCooldown);
            effect.applyEffect(player);
        }
    }

    @Override
    public void load() {
        Main.getInstance().getConfig().getSection("MAGE_CLASS.CLICKABLE_EFFECTS").getKeys(false).forEach(ef -> {
            String effect = "MAGE_CLASS.CLICKABLE_EFFECTS." + ef;
            String material = Main.getInstance().getConfig().getString(effect + ".MATERIAL");
            Map<String, Object> map = Main.getInstance().getConfig().getSection(effect).getAllMap();
            this.clickableEffects.put(Item.fromString(material).getId(), (short) Main.getInstance().getConfig().getInt(effect + ".DATA"), new MageEffect(map));
        });
    }

    private boolean checkMage(Player player) {
        TimerManager manager = Main.getInstance().getTimerManager();
        if (manager.getPvPTimer().hasTimer(player) || manager.getInvincibilityTimer().hasTimer(player)) {
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_CLASSES.MAGE_CLASS.CANNOT_MAGE_PVPTIMER")));
            return true;
        }
        if (Main.getInstance().getTeamManager().getClaimManager().findClaim(player.getLocation()) != null && Main.getInstance().getTeamManager().getClaimManager().findClaim(player.getLocation()).getType() == ClaimType.SPAWN) {
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_CLASSES.MAGE_CLASS.CANNOT_MAGE_SAFEZONE")));
            return true;
        }
        return false;
    }
}