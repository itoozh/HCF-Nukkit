package itoozh.core.pvpclass;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.pvpclass.cooldown.CustomCooldown;
import itoozh.core.session.timer.WarmupTimer;
import itoozh.core.util.ItemUtil;
import itoozh.core.util.LanguageUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public abstract class PvPClass {
    protected List<Item> armor;
    protected List<Effect> effects;
    protected String name;
    protected List<UUID> players;
    protected List<CustomCooldown> customCooldowns;

    public PvPClass(PvPClassManager manager, String name) {
        this.name = name;
        this.players = new ArrayList<>();
        this.customCooldowns = new ArrayList<>();
        this.armor = new ArrayList<>();
        this.effects = new ArrayList<>();
        this.loadEffectsArmor();
        manager.getClasses().put(name, this);
    }

    public void checkArmor(Player player) {
        WarmupTimer timer = Main.getInstance().getTimerManager().getWarmupTimer();
        if (this.hasArmor(player) && !this.players.contains(player.getUniqueId())) {
            timer.putTimerWithClass(player, this);
        } else if (this.players.contains(player.getUniqueId())) {
            this.unEquip(player);
        } else if (timer.hasTimer(player) && timer.getWarmups().get(player.getUniqueId()) == this) {
            timer.removeTimer(player);
        }
    }

    public abstract void handleEquip(Player p0);

    public void equip(Player player) {
        player.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_CLASSES.EQUIPPED").replaceAll("%class%", this.name)));
        this.players.add(player.getUniqueId());
        this.handleEquip(player);
        this.addEffects(player);
        Main.getInstance().getPvPClassManager().getActiveClasses().put(player.getUniqueId(), this);
    }

    public void addEffects(Player player) {
        for (Effect effect : this.effects) {
            player.addEffect(effect);
        }
    }

    private void loadEffectsArmor() {
        this.armor.addAll(Arrays.asList(Item.fromString(Main.getInstance().getConfig().getString(this.name.toUpperCase() + "_CLASS.ARMOR.HELMET")), Item.fromString(Main.getInstance().getConfig().getString(this.name.toUpperCase() + "_CLASS.ARMOR.CHESTPLATE")), Item.fromString(Main.getInstance().getConfig().getString(this.name.toUpperCase() + "_CLASS.ARMOR.LEGGINGS")), Item.fromString(Main.getInstance().getConfig().getString(this.name.toUpperCase() + "_CLASS.ARMOR.BOOTS"))));
        this.effects.addAll(Main.getInstance().getConfig().getStringList(this.name.toUpperCase() + "_CLASS.PASSIVE_EFFECTS").stream().map(ItemUtil::getEffect).collect(Collectors.toList()));
    }

    public void removeEffects(Player player) {
        for (Effect effect : this.effects) {
            if (effect.getDuration() > (540 * 20)) {
                player.removeEffect(effect.getId());
                continue;
            }
            if (!player.getEffects().containsValue(effect)) {
                continue;
            }
            player.removeEffect(effect.getId());
        }
    }

    public abstract void handleUnequip(Player p0);

    public abstract void load();

    private boolean hasArmor(Player player) {
        Item helmet = player.getInventory().getHelmet();
        Item chestplate = player.getInventory().getChestplate();
        Item leggings = player.getInventory().getLeggings();
        Item boots = player.getInventory().getBoots();
        return helmet != null && chestplate != null && leggings != null && boots != null && helmet.getId() == this.armor.get(0).getId() && chestplate.getId() == this.armor.get(1).getId() && leggings.getId() == this.armor.get(2).getId() && boots.getId() == this.armor.get(3).getId();
    }

    public void unEquip(Player player) {
        player.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_CLASSES.UNEQUIPPED").replaceAll("%class%", this.name)));
        this.players.remove(player.getUniqueId());
        this.handleUnequip(player);
        this.removeEffects(player);
        Main.getInstance().getPvPClassManager().getRestores().rowKeySet().remove(player.getUniqueId());
        Main.getInstance().getPvPClassManager().getActiveClasses().remove(player.getUniqueId());
    }
}
