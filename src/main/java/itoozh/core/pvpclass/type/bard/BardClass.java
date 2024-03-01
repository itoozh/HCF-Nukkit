package itoozh.core.pvpclass.type.bard;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerItemHeldEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import itoozh.core.Main;
import itoozh.core.pvpclass.PvPClass;
import itoozh.core.pvpclass.PvPClassManager;
import itoozh.core.pvpclass.cooldown.CustomCooldown;
import itoozh.core.pvpclass.cooldown.EnergyCooldown;
import itoozh.core.team.claim.ClaimType;
import itoozh.core.util.LanguageUtils;
import itoozh.core.util.TaskUtils;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class BardClass extends PvPClass implements Listener {
    private final Map<UUID, EnergyCooldown> bardEnergy;
    private final int knockbackItemEnergy;
    private final int bardCooldown;
    private final boolean knockbackItemEnabled;
    private final int knockbackItemRadius;
    private final Table<Integer, Short, BardEffect> clickableEffects;
    private final int knockbackItemData;
    private final Item knockbackItem;
    private final Table<Integer, Short, BardEffect> holdableEffects;
    private final int maxBardEnergy;
    private final CustomCooldown bardEffectCooldown;

    public BardClass(PvPClassManager manager) {
        super(manager, "Bard");
        this.bardEnergy = new HashMap<>();
        this.holdableEffects = HashBasedTable.create();
        this.clickableEffects = HashBasedTable.create();
        this.bardEffectCooldown = new CustomCooldown(this, Main.getInstance().getConfig().getString("BARD_CLASS.BARD_EFFECT"));
        this.knockbackItem = Item.fromString(Main.getInstance().getConfig().getString("BARD_CLASS.KNOCKBACK_ITEM.MATERIAL"));
        this.knockbackItemEnabled = Main.getInstance().getConfig().getBoolean("BARD_CLASS.KNOCKBACK_ITEM.ENABLED");
        this.knockbackItemRadius = Main.getInstance().getConfig().getInt("BARD_CLASS.KNOCKBACK_ITEM.RADIUS");
        this.knockbackItemData = Main.getInstance().getConfig().getInt("BARD_CLASS.KNOCKBACK_ITEM.DATA");
        this.knockbackItemEnergy = Main.getInstance().getConfig().getInt("BARD_CLASS.KNOCKBACK_ITEM.ENERGY_REQUIRED");
        this.maxBardEnergy = Main.getInstance().getConfig().getInt("BARD_CLASS.MAX_ENERGY");
        this.bardCooldown = Main.getInstance().getConfig().getInt("BARD_CLASS.BARD_COOLDOWN");
        this.load();
    }

    @EventHandler
    public void onHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        Item stack = player.getInventory().getItem(event.getSlot());
        if (stack != null && this.players.contains(player.getUniqueId())) {
            short data = (short) stack.getDamage();
            BardEffect effect = this.holdableEffects.get(stack.getId(), data);
            if (effect == null) {
                return;
            }
            if (this.checkBard(player)) {
                return;
            }
            effect.applyEffect(player);
        }
    }

    private void checkBardPlayers() {
        if (this.players.isEmpty()) {
            return;
        }
        for (UUID uuid : this.players) {
            Player player = Server.getInstance().getOfflinePlayer(uuid).getPlayer();
            Item stack = player.getInventory().getItemInHand();
            if (stack == null) {
                continue;
            }
            short data = (short) stack.getDamage();
            BardEffect effect = this.holdableEffects.get(stack.getId(), data);
            if (effect == null) {
                continue;
            }
            if (this.checkBard(player)) {
                continue;
            }
            effect.applyEffect(player);
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }
        Player player = event.getPlayer();
        Item stack = event.getItem();
        if (stack != null && this.players.contains(player.getUniqueId())) {
            EnergyCooldown cooldown = this.getEnergyCooldown(player);
            if (this.knockbackItemEnabled && stack.getId() == this.knockbackItem.getId() && stack.getDamage() == this.knockbackItemData) {
                if (this.bardEffectCooldown.hasCooldown(player)) {
                    player.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_CLASSES.BARD_CLASS.BUFF_COOLDOWN").replaceAll("%seconds%", this.bardEffectCooldown.getRemaining(player))));
                    return;
                }
                if (cooldown.checkEnergy(this.knockbackItemEnergy)) {
                    player.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_CLASSES.BARD_CLASS.INSUFFICIENT_ENERGY").replaceAll("%energy%", String.valueOf(this.knockbackItemEnergy))));
                    return;
                }
                if (this.checkBard(player)) {
                    return;
                }
                cooldown.takeEnergy(this.knockbackItemEnergy);
                this.bardEffectCooldown.applyCooldown(player, this.bardCooldown);
                Main.getInstance().getTimerManager().getCombatTimer().applyTimer(player);

                Item stackClone = stack.clone();
                stackClone.setCount(stackClone.getCount() - 1);
                player.getInventory().setItemInHand(stackClone);

                for (Player entity : player.getLevel().getPlayers().values()) {
                    if (entity != player && player.distance(entity) <= this.knockbackItemRadius) {
                        Location playerLocation = player.getLocation();
                        Location targetLocation = entity.getLocation();
                        double yOffset = player.getEyeHeight(); // Altura de los ojos del jugador

                        double x = targetLocation.getX() - playerLocation.getX();
                        double y = (targetLocation.getY() + entity.getEyeHeight()) - (playerLocation.getY() + yOffset);
                        double z = targetLocation.getZ() - playerLocation.getZ();
                        Vector3 vector = new Vector3(x, y, z);
                        entity.setMotion(vector.normalize().multiply(1.0).setComponents(vector.getX(), 0.4, vector.getZ()));
                    }
                }

            } else {
                short data = (short) stack.getDamage();
                BardEffect effect = this.clickableEffects.get(stack.getId(), data);
                if (effect == null) {
                    return;
                }
                if (this.bardEffectCooldown.hasCooldown(player)) {
                    player.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_CLASSES.BARD_CLASS.BUFF_COOLDOWN").replaceAll("%seconds%", this.bardEffectCooldown.getRemaining(player))));
                    return;
                }
                if (cooldown.checkEnergy(effect.getEnergyRequired())) {
                    player.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_CLASSES.BARD_CLASS.INSUFFICIENT_ENERGY").replaceAll("%energy%", String.valueOf(effect.getEnergyRequired()))));
                    return;
                }
                if (this.checkBard(player)) {
                    return;
                }
                cooldown.takeEnergy(effect.getEnergyRequired());
                Main.getInstance().getTimerManager().getCombatTimer().applyTimer(player);
                Item stackClone = stack.clone();
                stackClone.setCount(stackClone.getCount() - 1);
                player.getInventory().setItemInHand(stackClone);
                this.bardEffectCooldown.applyCooldown(player, this.bardCooldown);
                effect.applyEffect(player);
            }
        }
    }

    @Override
    public void load() {
        for (String s : Main.getInstance().getConfig().getSection("BARD_CLASS.CLICKABLE_EFFECTS").getKeys(false)) {
            String effect = "BARD_CLASS.CLICKABLE_EFFECTS." + s;
            String material = Main.getInstance().getConfig().getString(effect + ".MATERIAL");
            Map<String, Object> values = Main.getInstance().getConfig().getSection(effect).getAllMap();
            this.clickableEffects.put(Item.fromString(material).getId(), (short) Main.getInstance().getConfig().getInt(effect + ".DATA"), new BardEffect(values, true));
        }
        for (String s : Main.getInstance().getConfig().getSection("BARD_CLASS.HOLDABLE_EFFECTS").getKeys(false)) {
            String holdeable = "BARD_CLASS.HOLDABLE_EFFECTS." + s;
            String material = Main.getInstance().getConfig().getString(holdeable + ".MATERIAL");
            Map<String, Object> values = Main.getInstance().getConfig().getSection(holdeable).getAllMap();
            this.holdableEffects.put(Item.fromString(material).getId(), (short) Main.getInstance().getConfig().getInt(holdeable + ".DATA"), new BardEffect(values, false));
        }
        TaskUtils.executeScheduledAsync(Main.getInstance(), 20, this::checkBardPlayers);
    }

    public boolean checkBard(Player player) {
        if (Main.getInstance().getTimerManager().getPvPTimer().hasTimer(player) || Main.getInstance().getTimerManager().getInvincibilityTimer().hasTimer(player)) {
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_CLASSES.BARD_CLASS.CANNOT_BARD_PVPTIMER")));
            return true;
        }
        if (Main.getInstance().getTeamManager().getClaimManager().findClaim(player.getLocation()) != null && Main.getInstance().getTeamManager().getClaimManager().findClaim(player.getLocation()).getType() == ClaimType.SPAWN) {
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_CLASSES.BARD_CLASS.CANNOT_BARD_SAFEZONE")));
            return true;
        }
        return false;
    }

    public EnergyCooldown getEnergyCooldown(Player player) {
        return this.bardEnergy.get(player.getUniqueId());
    }

    @Override
    public void handleEquip(Player player) {
        this.bardEnergy.put(player.getUniqueId(), new EnergyCooldown(player.getUniqueId(), this.maxBardEnergy));
    }

    @Override
    public void handleUnequip(Player player) {
        this.bardEnergy.remove(player.getUniqueId());
    }
}
