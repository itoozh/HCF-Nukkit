package itoozh.core.signs.listener;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.SignChangeEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.signs.CustomSign;
import itoozh.core.signs.economy.EconomyBuySign;
import itoozh.core.signs.economy.EconomySellSign;
import itoozh.core.util.LanguageUtils;

import java.util.List;

public class CustomSignListener implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        CustomSign sign = Main.getInstance().getCustomSignManager().getSign(event.getLines());
        if (sign != null && player.hasPermission("core.customsigns")) {
            List<String> lines = sign.getLines();
            if (sign instanceof EconomyBuySign) {
                EconomyBuySign buySign = (EconomyBuySign) sign;
                for (int i = 0; i < lines.size(); ++i) {
                    event.setLine(i, lines.get(i).replaceAll("%amount%", event.getLine(buySign.getAmountIndex()))
                            .replace("%material%", event.getLine(buySign.getMaterialIndex()))
                            .replace("%price%", event.getLine(buySign.getPriceIndex())));
                }
                return;
            } else if (sign instanceof EconomySellSign) {
                EconomySellSign sellSign = (EconomySellSign) sign;
                for (int i = 0; i < lines.size(); ++i) {
                    event.setLine(i, lines.get(i).replaceAll("%amount%", event.getLine(sellSign.getAmountIndex()))
                            .replace("%material%", event.getLine(sellSign.getMaterialIndex()))
                            .replace("%price%", event.getLine(sellSign.getPriceIndex())));
                }
                return;
            }
            for (int i = 0; i < lines.size(); ++i) {
                event.setLine(i, lines.get(i));
            }
        }
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent event) {
        if (event.getAction() != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getBlock().getId() == Block.AIR) {
            return;
        }

        Player player = event.getPlayer();

        Block block = event.getBlock();
        BlockEntity blockEntity = block.getLevel().getBlockEntity(block);
        if (!(blockEntity instanceof BlockEntitySign)) {
            return;
        }
        BlockEntitySign sign = (BlockEntitySign) block.getLevel().getBlockEntity(block);
        if (sign == null) {
            return;
        }

        if (sign.getText() == null) {
            return;
        }

        CustomSign customSign = Main.getInstance().getCustomSignManager().getSign(sign.getText());
        if (customSign == null) {
            return;
        }
        if (customSign instanceof EconomySellSign) {
            EconomySellSign sellSIgn = (EconomySellSign) customSign;
            String material = sign.getText()[sellSIgn.getMaterialIndex()];
            String amount = sign.getText()[sellSIgn.getAmountIndex()];
            String price = sign.getText()[sellSIgn.getPriceIndex()];
            if (sellSIgn.getItemStack(material).getId() == Item.AIR) {
                player.sendMessage(TextFormat.colorize(LanguageUtils.getString("CUSTOM_SIGNS.ECONOMY_SIGNS.WRONG_MAT")));
                return;
            }
            if (isntNumber(amount)) {
                player.sendMessage(TextFormat.colorize(LanguageUtils.getString("CUSTOM_SIGNS.ECONOMY_SIGNS.WRONG_AMOUNT")));
                return;
            }
            if (isntNumber(price.replaceAll("\\$", ""))) {
                player.sendMessage(TextFormat.colorize(LanguageUtils.getString("CUSTOM_SIGNS.ECONOMY_SIGNS.WRONG_PRICE")));
                return;
            }
            customSign.onClick(player, sign);
        } else {
            if (!(customSign instanceof EconomyBuySign)) {

                customSign.onClick(player, sign);

                return;
            }
            EconomyBuySign buySign = (EconomyBuySign) customSign;

            String material = sign.getText()[buySign.getMaterialIndex()];
            String amount = sign.getText()[buySign.getAmountIndex()];
            String price = sign.getText()[buySign.getPriceIndex()];
            if (buySign.getItemStack(material) == null) {
                player.sendMessage(TextFormat.colorize(LanguageUtils.getString("CUSTOM_SIGNS.ECONOMY_SIGNS.WRONG_MAT")));
                return;
            }
            if (isntNumber(amount)) {
                player.sendMessage(TextFormat.colorize(LanguageUtils.getString("CUSTOM_SIGNS.ECONOMY_SIGNS.WRONG_AMOUNT")));
                return;
            }
            if (isntNumber(price.replaceAll("\\$", ""))) {
                player.sendMessage(TextFormat.colorize(LanguageUtils.getString("CUSTOM_SIGNS.ECONOMY_SIGNS.WRONG_PRICE")));
                return;
            }
            customSign.onClick(player, sign);
        }
    }

    public static boolean isntNumber(String input) {
        try {
            Integer.parseInt(input);
            return false;
        } catch (NumberFormatException ignored) {
            return true;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignTranslate(SignChangeEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("core.customsigns")) {
            for (int i = 0; i < event.getLines().length; ++i) {
                event.setLine(i, TextFormat.colorize(event.getLine(i)));
            }
        }
    }
}
