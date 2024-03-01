package itoozh.core.team.regen;

import cn.nukkit.scheduler.NukkitRunnable;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.team.Team;
import itoozh.core.team.TeamManager;
import itoozh.core.util.LanguageUtils;
import itoozh.core.util.TaskUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TeamRegenManager {

    public Map<Team, Long> getTeamsRegenerating() {
        return teamsRegenerating;
    }

    private final Map<Team, Long> teamsRegenerating;

    private int seconds;

    public TeamRegenManager(Main plugin) {
        this.seconds =  plugin.getConfig().getInt("TEAM_DTR.REGEN_TIMER") * 60;
        this.teamsRegenerating = new ConcurrentHashMap<>();
        TaskUtils.executeScheduledAsync(plugin, 20, this::tick);
    }

    public void applyTimer(Team team) {
        this.teamsRegenerating.put(team, System.currentTimeMillis() + this.seconds * 1000L);
        team.setMinuteRegen(false);

    }

    public void applyTimer(Team team, long time) {
        this.teamsRegenerating.put(team, System.currentTimeMillis() + time);
        team.setMinuteRegen(false);
    }

    public boolean startMinuteRegen(Team team) {
        if (team.isMinuteRegen()) {
            new MinuteRegenTask(Main.getInstance(), team);
        }
        return team.isMinuteRegen();
    }

    public long getRemaining(Team team) {
        return this.teamsRegenerating.containsKey(team) ? (this.teamsRegenerating.get(team) - System.currentTimeMillis()) : 0L;
    }

    private void tick() {
        Iterator<Team> teams = this.teamsRegenerating.keySet().iterator();
        TeamManager manager = Main.getInstance().getTeamManager();
        while (teams.hasNext()) {
            Team team = teams.next();
            if (!manager.getTeams().containsKey(team.getName())) {
                teams.remove();
            } else {
                if (this.hasTimer(team)) {
                    continue;
                }
                teams.remove();
                if (team.isMinuteRegen()) return;
                new MinuteRegenTask(Main.getInstance(), team);
            }
        }
    }

    public boolean hasTimer(Team team) {
        return this.getRemaining(team) > 0L;
    }

    private static class MinuteRegenTask extends NukkitRunnable {
        private final Team pt;
        private final Main instance;

        public MinuteRegenTask(Main plugin, Team team) {
            this.instance = plugin;
            this.pt = team;
            this.runTaskTimer(plugin, 0, 1200);
        }

        public void run() {
            if (!this.instance.getTeamManager().getTeams().containsKey(this.pt.getName())) {
                this.cancel();
                return;
            }
            if (Main.getInstance().getTeamManager().getTeamRegenManager().hasTimer(this.pt)) {
                this.cancel();
                this.pt.setMinuteRegen(false);
                return;
            }
            this.pt.setMinuteRegen(true);
            this.pt.setDtr(this.pt.getDtr() + Main.getInstance().getConfig().getDouble("TEAM_DTR.REGEN_PER_MIN"));
            this.pt.broadcast(TextFormat.colorize(LanguageUtils.getString("TEAM_REGEN_TIMER.REGENERATING").replaceAll("%dtr%", String.valueOf(Main.getInstance().getConfig().getDouble("TEAM_DTR.REGEN_PER_MIN")))));

            BigDecimal dtrDecimal = BigDecimal.valueOf(this.pt.getDtr()).setScale(1, RoundingMode.HALF_UP);
            BigDecimal maxDtrDecimal = BigDecimal.valueOf(this.pt.getMaxDtr()).setScale(1, RoundingMode.HALF_UP);

            if (dtrDecimal.compareTo(maxDtrDecimal) >= 0) {
                this.cancel();
                this.pt.setMinuteRegen(false);
                this.pt.setDtr(this.pt.getMaxDtr());
                this.pt.broadcast(TextFormat.colorize(LanguageUtils.getString("TEAM_REGEN_TIMER.FINISHED_REGENERATING")));
            }
            Main.getInstance().getLogger().info(TextFormat.GREEN + "Faction " + TextFormat.WHITE  + this.pt.getName() + TextFormat.GREEN + " is regenerating dtr, new dtr: " +  TextFormat.WHITE + this.pt.getDtr());
        }
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
}
