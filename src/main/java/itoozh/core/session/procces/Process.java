package itoozh.core.session.procces;

import cn.nukkit.Player;

public abstract class Process {

    public long expireAt;

    public long fullExpireTime;

    public Player player;

    public ProcessType processType;


    public Process(long expireAt, Player player, ProcessType processType) {
        this.processType = processType;
        this.fullExpireTime = expireAt;
        this.expireAt = (System.currentTimeMillis() / 1000L) + expireAt;
        this.player = player;
    }

    public ProcessType getProcessType() {
        return processType;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(long expireAt) {
        this.expireAt = expireAt;
    }

    public void resetExpireAt() {
        this.expireAt = (System.currentTimeMillis() / 1000L) + fullExpireTime;
    }

    public boolean expired() {
        return (System.currentTimeMillis() / 1000L) >= expireAt;
    }

    public long getFullExpireTime() {
        return fullExpireTime;
    }

    public void setFullExpireTime(long fullExpireTime) {
        this.fullExpireTime = fullExpireTime;
    }
}
