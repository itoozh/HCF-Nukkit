package itoozh.core.gkit;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDiamond;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.gkit.profile.Profile;
import itoozh.core.util.LanguageUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GKit {

    private String name;
    private String displayName;
    private int slot = -1;
    private long coolDown;

    private Item[] armor = new Item[4];
    private Item[] contents = new Item[36];
    private Item icon;

    private long freeUses = 3;

    private List<String> description;

    public GKit(String name) {
        this.name = name;
        this.displayName = TextFormat.AQUA + name;
        this.icon = new ItemDiamond();
        this.coolDown = -1;
        this.description = Arrays.asList("Default description", "please change!");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return TextFormat.colorize(displayName);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public long getCoolDown() {
        return coolDown;
    }

    public void setCoolDown(long coolDown) {
        this.coolDown = coolDown;
    }

    public Item getIcon(Player player) {
        Profile profile = Main.getInstance().getProfileManager().getProfile(player.getUniqueId());

        Item item = this.icon.clone();
        item.setCustomName(TextFormat.colorize("&r" + this.displayName));
        item.getNamedTag().putString("kitName", this.name);
        List<String> lore = new ArrayList<>();
        lore.add(TextFormat.colorize("&r&7---------------------"));
        lore.add("&r&dKit Cooldown &6» &r" + LanguageUtils.formatTimeShort(this.getCoolDown()));
        lore.add(" ");
        if (!player.hasPermission("use.gkit." + this.getName())) {
            lore.add("&r&dYour uses &6» &r" + profile.getUses(this));
            lore.add(" ");
            lore.add("&r&cYou don't own this kit");
            lore.add("&r&cPurchase at &e&ostore.orange.cc");
            lore.add(" ");
        }
        lore.add("&r&dCooldown &6» &r" + profile.formatRemaining(this));
        lore.add("");
        lore.add("&r&7&l(&6&l!&7&l)&r &dRight click to claim");
        lore.add(TextFormat.colorize("&r&7---------------------"));
        item.setLore(lore
                .stream()
                .map(TextFormat::colorize).toArray(String[]::new));
        return item;
    }

    public Item getpureIcon() {
        return icon;
    }

    public void setIcon(Item icon) {
        this.icon = icon;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public void apply(Player player) {
        List<Item> allItems = new ArrayList<>(Arrays.asList(contents));

        int countArmor = 0;
        for (int i = 0; i < armor.length; i++) {
            Item itemStack = player.getInventory().getArmorContents()[i];
            if (armor[i] == null || armor[i].getId() == Item.AIR) continue;
            if (itemStack != null && itemStack.getId() != Item.AIR) {
                if (armor[i].getId() == itemStack.getId()) countArmor++;
                player.dropItem(armor[i]);
                continue;
            }
            player.getInventory().setArmorItem(i, armor[i]);
            countArmor++;
        }

        if (countArmor == 4) Main.getInstance().getPvPClassManager().checkArmor(player);

        for (Item itemStack : allItems) {
            if (itemStack == null || itemStack.getId() == Item.AIR) continue;
            if (player.getInventory().firstEmpty(itemStack) == -1) {
                player.dropItem(itemStack);
                continue;
            }
            if (player.getInventory().getItem(player.getInventory().firstEmpty(itemStack)) != null && player.getInventory().getItem(player.getInventory().firstEmpty(itemStack)).getId() != Item.AIR) {
                player.dropItem(itemStack);
            } else {
                player.getInventory().setItem(player.getInventory().firstEmpty(itemStack), itemStack);}
        }
    }

    public void clearDescription() {
        this.description = new ArrayList<>();
    }

    public void setArmor(Item[] armor) {
        this.armor = armor;
    }

    public void setContents(Item[] contents) {
        this.contents = contents;
    }

    public void setFreeUses(long freeUses) {
        this.freeUses = freeUses;
    }

    public long getFreeUses() {
        return this.freeUses;
    }

    public Item[] getArmor() {
        return armor;
    }

    public Item[] getContents() {
        return contents;
    }
}
