package itoozh.core.signs.economy;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.item.Item;
import itoozh.core.Main;
import itoozh.core.session.Session;
import itoozh.core.util.LanguageUtils;
import lombok.Getter;

@Getter
public class EconomyBuySign extends EconomySign {
    private final int amountIndex;
    private final int priceIndex;
    private final int materialIndex;

    public EconomyBuySign() {
        super(Main.getInstance().getConfig().getStringList("SIGNS_CONFIG.BUY_SIGN.LINES"));
        this.materialIndex = this.getIndex("%material%");
        this.amountIndex = this.getIndex("%amount%");
        this.priceIndex = this.getIndex("%price%");
    }

    @Override
    public void onClick(Player player, BlockEntitySign sign) {
        Item stack = this.getItemStack(sign.getText()[this.materialIndex]);
        Session session = Main.getInstance().getSessionManager().getSession(player);
        int price = Integer.parseInt(sign.getText()[this.priceIndex].replaceAll("\\$", ""));
        int amount = Integer.parseInt(sign.getText()[this.amountIndex]);
        if (session.getBalance() < price) {
            String[] lines = sign.getText().clone();
            lines[this.priceIndex] = LanguageUtils.getString("CUSTOM_SIGNS.ECONOMY_SIGNS.INSUFFICIENT_MONEY");
            // this.sendSignChange(player, sign, lines);
            return;
        }
        String[] lines = sign.getText().clone();
        lines[this.materialIndex + 1] = LanguageUtils.getString("CUSTOM_SIGNS.ECONOMY_SIGNS.PURCHASED");
        session.takeBalance(price);
        stack.setCount(amount);
        // this.sendSignChange(player, sign, lines);
        if (player.getInventory().firstEmpty(stack) == -1) {
            player.dropItem(stack);
        } else {
            player.getInventory().addItem(stack);
        }
    }
}
