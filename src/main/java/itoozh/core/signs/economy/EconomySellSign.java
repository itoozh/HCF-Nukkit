package itoozh.core.signs.economy;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.item.Item;
import itoozh.core.Main;
import itoozh.core.session.Session;
import itoozh.core.util.LanguageUtils;
import lombok.Getter;

@Getter
public class EconomySellSign extends EconomySign {
    private final int priceIndex;
    private final int materialIndex;
    private final int amountIndex;

    public EconomySellSign() {
        super(Main.getInstance().getConfig().getStringList("SIGNS_CONFIG.SELL_SIGN.LINES"));
        this.materialIndex = this.getIndex("%material%");
        this.amountIndex = this.getIndex("%amount%");
        this.priceIndex = this.getIndex("%price%");
    }

    @Override
    public void onClick(Player player, BlockEntitySign sign) {
        Session session = Main.getInstance().getSessionManager().getSession(player);
        Item stack = this.getItemStack(sign.getText()[this.materialIndex]);
        int amount = getAmountItems(player, stack);
        int price = Integer.parseInt(sign.getText()[this.priceIndex].replaceAll("\\$", ""));
        int amountIndex = Integer.parseInt(sign.getText()[this.amountIndex]);
        if (amount < amountIndex) {
            String[] lines = sign.getText().clone();
            lines[this.priceIndex] = LanguageUtils.getString("CUSTOM_SIGNS.ECONOMY_SIGNS.INSUFFICIENT_BLOCKS");
            // this.sendSignChange(player, sign, lines);
            return;
        }
        String[] lines = sign.getText().clone();
        lines[this.materialIndex + 1] = LanguageUtils.getString("CUSTOM_SIGNS.ECONOMY_SIGNS.SOLD");
        session.giveBalance(price);
        // this.sendSignChange(player, sign, lines);
        takeItems(player, stack, amountIndex);
    }

    public static int getAmountItems(Player player, Item stack) {
        Item[] contents = player.getInventory().getContents().values().toArray(new Item[0]);
        int i = 0;
        for (Item type : contents) {
            if (type == null || type.getId() != stack.getId() || type.getDamage() != stack.getDamage())
                continue;
            i += type.getCount();
        }
        return i;
    }

    public static void takeItems(Player player, Item type, int amount) {
        Item[] contents = player.getInventory().getContents().values().toArray(new Item[0]);
        for (int i = 0; i < player.getInventory().getContents().size(); ++i) {
            Item stack = contents[i];
            if (stack != null) {
                if (stack.getId() == type.getId()) {
                    if (stack.getDamage() == type.getDamage()) {
                        if (amount < stack.getCount()) {
                            stack.setCount(stack.getCount() - amount);
                            player.getInventory().setItem(i, stack);
                            break;
                        }
                        amount -= stack.getCount();
                        player.getInventory().clear(i, true);
                    }
                }
            }
        }
    }

}