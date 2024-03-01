package itoozh.core.signs;

import itoozh.core.Main;
import itoozh.core.signs.economy.EconomyBuySign;
import itoozh.core.signs.economy.EconomySellSign;
import itoozh.core.signs.elevator.ElevatorDownSign;
import itoozh.core.signs.elevator.ElevatorUpSign;
import itoozh.core.signs.listener.CustomSignListener;

public class CustomSignManager {
    private EconomySellSign sellSign;
    private ElevatorUpSign upSign;
    private EconomyBuySign buySign;
    private ElevatorDownSign downSign;

    public CustomSignManager(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(new CustomSignListener(), plugin);
        if (Main.getInstance().getConfig().getBoolean("SIGNS_CONFIG.UP_SIGN.ENABLED")) {
            this.upSign = new ElevatorUpSign();
        }
        if (Main.getInstance().getConfig().getBoolean("SIGNS_CONFIG.DOWN_SIGN.ENABLED")) {
            this.downSign = new ElevatorDownSign();
        }
        if (Main.getInstance().getConfig().getBoolean("SIGNS_CONFIG.BUY_SIGN.ENABLED")) {
            this.buySign = new EconomyBuySign();
        }
        if (Main.getInstance().getConfig().getBoolean("SIGNS_CONFIG.SELL_SIGN.ENABLED")) {
            this.sellSign = new EconomySellSign();
        }
    }

    public CustomSign getSign(String[] lines) {
        if (lines[0].toLowerCase().contains("buy")) {
            return this.buySign;
        }
        if (lines[0].toLowerCase().contains("sell")) {
            return this.sellSign;
        }
        if (lines[this.upSign.getElevatorIndex()].toLowerCase().contains("elevator")) {
            if (lines[this.upSign.getUpIndex()].toLowerCase().contains("up")) {
                return this.upSign;
            }
            if (lines[this.downSign.getDownIndex()].toLowerCase().contains("down")) {
                return this.downSign;
            }
        }
        return null;
    }
}
