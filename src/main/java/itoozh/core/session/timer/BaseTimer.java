package itoozh.core.session.timer;

import cn.nukkit.event.Listener;
import itoozh.core.Main;
import itoozh.core.timer.TimerManager;
import itoozh.core.timer.type.PlayerTimer;

public class BaseTimer extends PlayerTimer implements Listener {
    public BaseTimer(TimerManager timerManager) {
        super(timerManager, false, "Combat", "PLAYER_TIMERS.COMBAT_TAG", Main.getInstance().getConfig().getInt("TIMERS_COOLDOWN.COMBAT_TAG"));
    }
}
