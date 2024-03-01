package itoozh.core.util;

import itoozh.core.team.Team;

import java.util.HashMap;
import java.util.Map;

public class TeamCooldown {

    private final Map<String, Long> cooldowns;

    public TeamCooldown() {
        this.cooldowns = new HashMap<>();
    }

    public void removeCooldown(Team playerteam) {
        this.cooldowns.remove(playerteam.getName());
    }

    public void applyCooldown(Team playerteam, int time) {
        this.cooldowns.put(playerteam.getName(), System.currentTimeMillis() + time * 1000L);
    }

    public boolean hasCooldown(Team playerteam) {
        return this.cooldowns.containsKey(playerteam.getName()) && this.cooldowns.get(playerteam.getName()) >= System.currentTimeMillis();
    }

    public String getRemaining(Team playerteam) {
        long time = this.cooldowns.get(playerteam.getName()) - System.currentTimeMillis();
        return Formatter.getRemaining(time, true);
    }
}
