package itoozh.core.crate;

import cn.nukkit.Player;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import itoozh.core.crate.effect.CrateEffect;
import lombok.Getter;
import me.iwareq.fakeinventories.FakeInventory;

import java.util.*;

public class Crate {

    private String name;

    private List<String> hologramText;

    @Getter
    private String color = "";

    public Item getItemKey() {
        return itemKey;
    }

    private Item itemKey;

    private CrateEffect effect = CrateEffect.WEATHER_EFFECT;

    private int rewardAmount = 3;

    private Map<Integer, Item> rewards = new HashMap<>();

    public Crate(String name, String color) {
        this.name = name;
        this.color = color;
        this.hologramText = Arrays.asList(
                "&l" + this.color + this.name.toUpperCase(),
                "",
                "&o" + this.color + "Left&7 click to inspect crate.",
                "&o" + this.color + "Right&7 click to open.",
                "",
                "&7&oTake a glance at our store!",
                "&c&ostore.orange.cc"
        );
    }

    public String getName() {
        return name;
    }

    public int getRewardAmount() {
        return rewardAmount;
    }

    public  Map<Integer, Item> getItems() {
        return rewards;
    }

    public void setItemKey(Item itemKey) {
        this.itemKey = itemKey;
    }

    public void setRewardAmount(int rewardAmount) {
        this.rewardAmount = rewardAmount;
    }

    public Item getKeyItem(int amount) {
        Item key = itemKey.clone();
        key.setCount(amount);
        key.setCustomName(TextFormat.colorize("&r&l" + color + name.toUpperCase() + " KEY"));
        key.getNamedTag().putString("crate", name);
        key.setLore(TextFormat.colorize("\n§r§7Redeem this §c" + getDisplayName() + " §7key at our spawn, or\nsimply §cshift §7+ §cright click§7."));
        return key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return TextFormat.colorize(color + name + "&r");
    }

    public void open(Player player) {
        FakeInventory inventory = new FakeInventory(InventoryType.DOUBLE_CHEST, TextFormat.colorize("&r" + color + getName() + " &r&8Contents"));

        for (Map.Entry<Integer, Item> entry : rewards.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue());
        }

        inventory.setDefaultItemHandler((item, event) -> {
            event.setCancelled(true);
        });

        player.addWindow(inventory);
    }

    public void giveRewards(Player player) {
        List<Integer> rewardIndices = new ArrayList<>(rewards.keySet());
        int numRewards = Math.min(rewardAmount, rewardIndices.size());
        Random random = new Random();

        for (int i = 0; i < numRewards; i++) {
            int randomIndex = rewardIndices.remove(random.nextInt(rewardIndices.size()));
            Item rewardItem = rewards.get(randomIndex).clone();
            if (player.getInventory().canAddItem(rewardItem)) {
                player.getInventory().addItem(rewardItem);
            } else {
                player.dropItem(rewardItem);
            }
        }
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public Map<Integer, Item> getRewards() {
        return rewards;
    }

    public void setRewards(Map<Integer, Item> rewards) {
        this.rewards = rewards;
    }

    public List<String> getHologramText() {
        List<String> hologramTextReformat = this.hologramText;
        hologramTextReformat.replaceAll(string -> string.replace("%name%", this.name).replace("%color%", this.color).replace("%nameUpper%", this.name.toUpperCase()));
        return hologramTextReformat;
    }

    public void setHologramText(List<String> hologramText) {
        this.hologramText = hologramText;
    }

    public void setEffect(CrateEffect effect) {
        this.effect = effect;
    }

    public CrateEffect getEffect() {
        return effect;
    }

}
