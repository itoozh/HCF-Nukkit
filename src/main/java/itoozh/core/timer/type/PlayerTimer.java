package itoozh.core.timer.type;

import cn.nukkit.Player;
import cn.nukkit.Server;
import itoozh.core.event.TimerExpireEvent;
import itoozh.core.timer.TimerManager;
import itoozh.core.util.Formatter;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class PlayerTimer {
    protected int seconds;
    protected String name;
    protected String scoreboardPath;
    protected Map<UUID, Long> timerCache;
    protected Map<UUID, Long> pausedCache;
    protected boolean pausable;

    public PlayerTimer(TimerManager manager, boolean pausable, String name, String text, int seconds) {
        this.seconds = seconds;
        this.name = name;
        this.scoreboardPath = text;
        this.timerCache = new ConcurrentHashMap<>();
        this.pausedCache = new ConcurrentHashMap<>();
        this.pausable = pausable;
        manager.getPlayerTimers().put(name, this);
    }

    public void removeTimer(Player player) {
        UUID uuid = player.getUniqueId();
        this.pausedCache.remove(uuid);
        this.timerCache.remove(uuid);
    }

    public void applyTimer(Player player, long time) {
        this.timerCache.put(player.getUniqueId(), System.currentTimeMillis() + time);
    }

    public void unpauseTimer(Player player) {
        if (!this.pausedCache.containsKey(player.getUniqueId())) {
            return;
        }
        this.timerCache.put(player.getUniqueId(), System.currentTimeMillis() + this.pausedCache.get(player.getUniqueId()));
        this.pausedCache.remove(player.getUniqueId());
    }

    public void pauseTimer(Player player) {
        if (!this.timerCache.containsKey(player.getUniqueId())) {
            return;
        }
        this.pausedCache.put(player.getUniqueId(), this.timerCache.get(player.getUniqueId()) - System.currentTimeMillis());
        this.timerCache.remove(player.getUniqueId());
    }

    public void applyTimer(Player player) {
        this.timerCache.put(player.getUniqueId(), System.currentTimeMillis() + 1000L * this.seconds);
    }

    public String getRemainingString(Player player) {
        if (this.pausedCache.containsKey(player.getUniqueId())) {
            return Formatter.getRemaining(this.pausedCache.get(player.getUniqueId()), true);
        }
        long time = this.timerCache.get(player.getUniqueId()) - System.currentTimeMillis();
        return Formatter.getRemaining(time, true);
    }

    public boolean hasTimer(Player player) {
        UUID uuid = player.getUniqueId();
        if (this.pausedCache.containsKey(uuid)) {
            return true;
        }
        Long time = this.timerCache.get(uuid);
        return time != null && time >= System.currentTimeMillis();
    }

    public void tick() {
        this.timerCache.entrySet().removeIf(t -> {
            if (t.getValue() - System.currentTimeMillis() < 0L) {
                Server.getInstance().getPluginManager().callEvent(new TimerExpireEvent(this, t.getKey()));
                return true;
            } else {
                return false;
            }
        });
    }
}
