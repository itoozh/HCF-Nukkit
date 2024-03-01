package itoozh.core.session.timer;

import itoozh.core.Main;
import itoozh.core.timer.TimerManager;
import itoozh.core.timer.type.PlayerTimer;

public class ArcherTagTimer extends PlayerTimer {
    public ArcherTagTimer(TimerManager timerManager) {
        super(timerManager, false, "ArcherTag", "PLAYER_TIMERS.ARCHER_TAG", Main.getInstance().getConfig().getInt("TIMERS_COOLDOWN.ARCHER_TAG"));
    }
}
