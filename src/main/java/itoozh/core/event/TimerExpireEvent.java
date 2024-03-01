package itoozh.core.event;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import itoozh.core.timer.type.PlayerTimer;
import lombok.Getter;

import java.util.UUID;

public class TimerExpireEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private UUID player;
    @Getter
    private PlayerTimer playerTimer;

    public TimerExpireEvent(PlayerTimer playerTimer, UUID player) {
        this.playerTimer = playerTimer;
        this.player = player;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }
}
