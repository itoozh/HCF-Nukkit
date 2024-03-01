package itoozh.core.session.timer;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.timer.TimerManager;
import itoozh.core.timer.type.PlayerTimer;
import itoozh.core.util.LanguageUtils;

public class AppleTimer extends PlayerTimer implements Listener {
    public AppleTimer(TimerManager timerManager) {
        super(timerManager, false, "Apple", "PLAYER_TIMERS.APPLE", Main.getInstance().getConfig().getInt("TIMERS_COOLDOWN.APPLE"));
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Item stack = event.getItem();
        if (stack.getId() == Item.GOLDEN_APPLE) {
            if (this.hasTimer(player)) {
                event.setCancelled(true);
                player.sendMessage(TextFormat.colorize(LanguageUtils.getString("APPLE_TIMER.COOLDOWN").replaceAll("%seconds%", this.getRemainingString(player))));
                return;
            }
            this.applyTimer(player);
        }
    }
}
