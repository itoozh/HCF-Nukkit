package itoozh.core.util;

import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.potion.Effect;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ItemUtil {
    public String inventoryToString(Map<Integer, Item> inventory) {
        StringBuilder builder = new StringBuilder();
        inventory.forEach(((slot, item) -> builder.append(itemToString(slot, item)).append(";")));

        return builder.substring(0, builder.toString().length() - 1);
    }

    public static Location deserializeLoc(String input) {
        String[] array = input.split(", ");
        return new Location(parseDouble(array[1]), parseDouble(array[2]), parseDouble(array[3]), parseFloat(array[4]), parseFloat(array[5]), Server.getInstance().getLevelByName(array[0]));
    }

    private static Double parseDouble(String input) {
        return Double.parseDouble(input);
    }

    private static Float parseFloat(String input) {
        return Float.parseFloat(input);
    }

    public String itemsToString(Item[] items) {
        StringBuilder builder = new StringBuilder();
        for (Item item : items) {
            if (item == null) continue;
            builder.append(itemToString(item)).append(";");
        }
        if (builder.length() > 0) {
            return builder.substring(0, builder.length() - 1);
        } else {
            return "empty";
        }
    }

    public Item[] itemsFromString(String itemsString) {
        if(!itemsString.equalsIgnoreCase("empty")) {
            String[] itemStrings = itemsString.split(";");
            final Item[] backpackInv = new Item[itemStrings.length];

            for(int i = 0; i < itemStrings.length; i++) {
                Item itemWithSlot = itemFromStringI(itemStrings[i]);
                backpackInv[i] = itemWithSlot;
            }
            return backpackInv;
        } else return new Item[36];
    }

    public Map<Integer, Item> inventoryFromString(String invString) {
        if(!invString.equalsIgnoreCase("empty")) {
            String[] itemStrings = invString.split(";");
            final Map<Integer, Item> backpackInv = new HashMap<>();

            for(String itemString : itemStrings) {
                ItemWithSlot itemWithSlot = itemFromString(itemString);
                backpackInv.put(itemWithSlot.getSlot(), itemWithSlot.getItem());
            }

            return backpackInv;
        } else return new HashMap<>();
    }

    public String itemToString(int slot, Item item) {
        return slot + ":" +
                item.getId() + ":" +
                item.getDamage() + ":" +
                item.getCount() + ":" +
                (item.hasCompoundTag() ? this.bytesToBase64(item.getCompoundTag()) : "not");
    }

    public String itemToString(Item item) {
        return item.getId() + ":" +
                item.getDamage() + ":" +
                item.getCount() + ":" +
                (item.hasCompoundTag() ? this.bytesToBase64(item.getCompoundTag()) : "not");
    }

    public Item itemFromStringI(String itemString) throws NumberFormatException {
        String[] info = itemString.split(":");

        Item item = Item.get(
                Integer.parseInt(info[0]),
                Integer.parseInt(info[1]),
                Integer.parseInt(info[2])
        );

        if(!info[3].equals("not")) item.setCompoundTag(base64ToBytes(info[3]));

        return item;
    }

    public ItemWithSlot itemFromString(String itemString) throws NumberFormatException {
        String[] info = itemString.split(":");
        int slot = Integer.parseInt(info[0]);

        Item item = Item.get(
                Integer.parseInt(info[1]),
                Integer.parseInt(info[2]),
                Integer.parseInt(info[3])
        );

        if(!info[4].equals("not")) item.setCompoundTag(base64ToBytes(info[4]));

        return new ItemWithSlot(slot, item);
    }

    private String bytesToBase64(byte[] bytes) {
        if(bytes == null || bytes.length <= 0) return "not";

        return Base64.getEncoder().encodeToString(bytes);
    }

    private byte[] base64ToBytes(String hexString) {
        if(hexString == null || hexString.equals("")) return null;

        return Base64.getDecoder().decode(hexString);
    }

    @RequiredArgsConstructor
    @Getter
    public static class ItemWithSlot {
        private final int slot;
        private final Item item;
    }

    public static Effect getEffect(String input) {
        String[] array = input.split(", ");
        int max = array[1].equals("MAX_VALUE") ? Integer.MAX_VALUE : (20 * parseInt(array[1].replaceAll("s", "")));
        Effect effect = null;
        try {
            effect = Effect.getEffectByName(array[0]);
        } catch (Exception e) {
            System.out.println("El codigo de efecto " + array[0] + " no existe");
        }
        if (effect == null) {
            System.out.println("esta retornando a null el efecto del string");
            return null;
        }
        effect.setDuration(max);
        effect.setAmplifier(parseInt(array[2]) - 1);
        return effect;
    }

    private static Integer parseInt(String input) {
        return Integer.parseInt(input);
    }
}
