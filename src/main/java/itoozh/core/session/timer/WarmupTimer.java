package itoozh.core.session.timer;

import cn.nukkit.Player;
import itoozh.core.Main;
import itoozh.core.pvpclass.PvPClass;
import itoozh.core.timer.TimerManager;
import itoozh.core.timer.type.PlayerTimer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarmupTimer extends PlayerTimer{
    private final Map<UUID, PvPClass> warmups;
    public WarmupTimer(TimerManager timerManager) {
        super(timerManager, false, "Warmup", "PLAYER_TIMERS.WARMUP", Main.getInstance().getConfig().getInt("TIMERS_COOLDOWN.WARMUP"));
        this.warmups = new HashMap<>();
    }

    public void putTimerWithClass(Player player, PvPClass pvpClass) {
        super.applyTimer(player);
        this.warmups.put(player.getUniqueId(), pvpClass);
    }

    public Map<UUID, PvPClass> getWarmups() {
        return this.warmups;
    }

    @Override
    public void removeTimer(Player player) {
        super.removeTimer(player);
        this.warmups.remove(player.getUniqueId());
    }
}
