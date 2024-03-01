package itoozh.core.ability;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.listener.GlitchListener;
import itoozh.core.session.timer.AbilityTimer;
import itoozh.core.team.claim.Claim;
import itoozh.core.team.claim.ClaimType;
import itoozh.core.util.LanguageUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public abstract class Ability {
    protected Item item;
    protected AbilityTimer abilityCooldown;
    protected boolean enabled;
    protected String nameConfig;
    protected String displayName;
    protected AbilityManager.AbilityUseType useType;
    protected String name;

    public Ability(AbilityManager manager, AbilityManager.AbilityUseType userType, String name) {
        this.name = name;
        this.useType = userType;
        this.nameConfig = name.replace(" ", "_").toUpperCase();
        this.displayName = TextFormat.colorize(AbilityManager.dataFile.getString(this.nameConfig + ".DISPLAY_NAME"));
        this.abilityCooldown = new AbilityTimer(Main.getInstance().getTimerManager(), this, "PLAYER_TIMERS.ABILITIES");
        this.item = this.loadItem();
        this.enabled = AbilityManager.dataFile.getBoolean(this.nameConfig + ".ENABLED");
        manager.getAbilities().put(name.toUpperCase().replace(" ", ""), this);
    }

    public Item item() {
        return this.getItem().clone();
    }

    public boolean hasAbilityInHand(Player player) {
        Item stack = player.getInventory().getItemInHand();
        if (stack.getId() == Item.AIR) {
            return false;
        }
        if (!stack.hasCustomName()) {
            return false;
        }
        return stack.getCustomName().equals(this.item.getCustomName()) && Arrays.equals(stack.getLore(), this.item.getLore());
    }

    public void applyCooldown(Player player) {
        if (AbilityManager.dataFile.getBoolean("GLOBAL_ABILITY.ENABLED")) {
            Main.getInstance().getAbilityManager().getGlobalCooldown().applyCooldown(player, AbilityManager.dataFile.getInt("GLOBAL_ABILITY.COOLDOWN"));
        }
        this.abilityCooldown.applyTimer(player);
    }

    public void onClick(Player player) {
    }

    private Item loadItem() {
        Item builder = Item.fromString(AbilityManager.dataFile.getString(this.nameConfig + ".MATERIAL"));
        builder.setCustomName(TextFormat.colorize("&r&l" + AbilityManager.dataFile.getString(this.nameConfig + ".DISPLAY_NAME")));


        builder.setLore(AbilityManager.dataFile.getStringList(this.nameConfig + ".LORE")
                .stream()
                .map(TextFormat::colorize).toArray(String[]::new));

        builder.setDamage(AbilityManager.dataFile.getInt(this.nameConfig + ".DATA"));
        if (AbilityManager.dataFile.exists(this.nameConfig + ".DURABILITY")) {
            short data = (short) AbilityManager.dataFile.getInt(this.nameConfig + ".DURABILITY");
            // builder.setDurability(this.getManager(), (short) (material.getMaxDurability() - data));
        }
        if (AbilityManager.dataFile.exists(this.nameConfig + ".ENCHANTS")) {
            for (String s : AbilityManager.dataFile.getStringList(this.nameConfig + ".ENCHANTS")) {
                String[] enchants = s.split(", ");
                // builder.addEnchantment(Enchantment.get(Integer.parseInt(enchants[0])).setLevel(Integer.parseInt(enchants[1])));
            }
        }
        return builder.clone();
    }

    public boolean cannotHit(Player player, Player target) {
        return GlitchListener.getHitCooldown().hasCooldown(player) || !Main.getInstance().getTeamManager().canHit(player, target, false);
    }

    public void onHit(Player player, Player target) {
    }

    public boolean hasCooldown(Player player) {
        if (Main.getInstance().getAbilityManager().getGlobalCooldown().hasCooldown(player)) {
            player.sendMessage(LanguageUtils.getString("ABILITIES.GLOBAL_COOLDOWN").replaceAll("%time%", Main.getInstance().getAbilityManager().getGlobalCooldown().getRemaining(player)));
            return true;
        }
        if (this.abilityCooldown.hasTimer(player)) {
            player.sendMessage(LanguageUtils.getString("ABILITIES.COOLDOWN").replaceAll("%ability%", this.displayName).replaceAll("%time%", this.abilityCooldown.getRemainingString(player)));
            return true;
        }
        return false;
    }

    public boolean cannotUse(Player player) {
        if (!this.enabled) {
            player.sendMessage(LanguageUtils.getString("ABILITIES.DISABLED"));
            return true;
        }
        Claim team = Main.getInstance().getTeamManager().getClaimManager().findClaim(player.getLocation());

        if (AbilityManager.dataFile.getBoolean("GLOBAL_ABILITY.DISABLE_IN_EVENTS") && team != null && team.getType() == ClaimType.KOTH) {
            player.sendMessage(LanguageUtils.getString("ABILITIES.DISABLED_EVENT"));
            return true;
        }
        return false;
    }

    public void takeItem(Player player) {
        if (AbilityManager.dataFile.getBoolean(this.nameConfig + ".TAKE_ITEM")) {
            Item item = player.getInventory().getItemInHand();
            item.setCount(item.getCount() - 1);
            player.getInventory().setItemInHand(item);
        }
    }

    public boolean cannotUse(Player player, Player target) {
        if (!this.enabled) {
            player.sendMessage(LanguageUtils.getString("ABILITIES.DISABLED"));
            return true;
        }
        Claim team = Main.getInstance().getTeamManager().getClaimManager().findClaim(player.getLocation());
        if (AbilityManager.dataFile.getBoolean("GLOBAL_ABILITY.DISABLE_IN_EVENTS") && team != null && team.getType() == ClaimType.KOTH) {
            player.sendMessage(LanguageUtils.getString("ABILITIES.DISABLED_EVENT"));
            return true;
        }
        return this.cannotHit(player, target);
    }
}
