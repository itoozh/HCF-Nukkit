package itoozh.core.scoreboard;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.pvpclass.PvPClass;
import itoozh.core.pvpclass.cooldown.CustomCooldown;
import itoozh.core.pvpclass.type.bard.BardClass;
import itoozh.core.pvpclass.type.mage.MageClass;
import itoozh.core.pvpclass.type.miner.MinerClass;
import itoozh.core.scoreboard.packet.data.DisplaySlot;
import itoozh.core.session.Session;
import itoozh.core.session.timer.AbilityTimer;
import itoozh.core.team.Team;
import itoozh.core.team.claim.Claim;
import itoozh.core.team.claim.ClaimType;
import itoozh.core.timer.type.PlayerTimer;
import itoozh.core.util.Cooldown;
import itoozh.core.util.Formatter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ScoreboardUtils {

    private long lastMillisFooter = System.currentTimeMillis();
    private int iFooter = 0;
    public static Config scoreboardConfig = new Config(new File(Main.getInstance().getDataFolder(), "scoreboard.yml"), Config.YAML);

    public List<String> getLines(Player player) {
        List<String> toReturn = new ArrayList<>();
        String lines = scoreboardConfig.getString("SCOREBOARD_INFO.LINES");
        Session session = Main.getInstance().getSessionManager().getSessionByUUID(player.getUniqueId());
        Cooldown cooldown = Main.getInstance().getAbilityManager().getGlobalCooldown();
        Team team = session.getTeam();
        toReturn.add("&r&c" + lines);
        if (session.getScoreboard() == null) {
            return null;
        }

        String claim = scoreboardConfig.getString("PLAYER_TIMERS.CLAIM");
        Claim claim1 = Main.getInstance().getTeamManager().getClaimManager().findClaim(player.getLocation()) != null ? Main.getInstance().getTeamManager().getClaimManager().findClaim(player.getLocation()) : new Claim(ClaimType.WILDERNESS, "Wilderness", player.getLocation(), player.getLocation());
        if (session.isScoreboardClaim()) {
            toReturn.add(claim + claim1.getNameFormat(player));
        }
        if (cooldown.hasCooldown(player)) {
            String abilities = scoreboardConfig.getString("PLAYER_TIMERS.GLOBAL_ABILITIES");
            if (abilities != null) {
                toReturn.add(abilities + cooldown.getRemaining(player));
            }
        }
        for (PlayerTimer playerTimer : Main.getInstance().getTimerManager().getPlayerTimers().values()) {
            String timerText = scoreboardConfig.getString(playerTimer.getScoreboardPath());
            if (!playerTimer.hasTimer(player)) {
                continue;
            }
            if (timerText == null) {
                continue;
            }

            if (playerTimer instanceof AbilityTimer) {
                AbilityTimer abilityTimer = (AbilityTimer) playerTimer;
                toReturn.add(timerText.replaceAll("%ability%", abilityTimer.getAbility().getDisplayName()) + playerTimer.getRemainingString(player));
            } else {
                toReturn.add(timerText + playerTimer.getRemainingString(player));
            }
        }

        if (Main.getInstance().getTimerManager().getSotwTimer().isActive()) {
            if (Main.getInstance().getTimerManager().getSotwTimer().getEnabled().contains(player.getUniqueId())) {
                String sotw = scoreboardConfig.getString("PLAYER_TIMERS.SOTW_OFF");
                if (sotw != null) {
                    toReturn.add(sotw + Main.getInstance().getTimerManager().getSotwTimer().getRemainingString());
                }
            } else {
                String sotw = scoreboardConfig.getString("PLAYER_TIMERS.SOTW");
                if (sotw != null) {
                    toReturn.add(sotw + Main.getInstance().getTimerManager().getSotwTimer().getRemainingString());
                }
            }
        }

        PvPClass pvpClass = Main.getInstance().getPvPClassManager().getActiveClasses().get(player.getUniqueId());
        if (pvpClass != null) {
            String activeClass = scoreboardConfig.getString("PLAYER_TIMERS.ACTIVE_CLASS");
            if (activeClass != null) {
                toReturn.add(activeClass + pvpClass.getName());
            }
            if (pvpClass instanceof BardClass) {
                BardClass bardClass = (BardClass) pvpClass;
                String bardEnergy = scoreboardConfig.getString("BARD_CLASS.BARD_ENERGY");
                if (bardEnergy != null) {
                    toReturn.add(bardEnergy + Formatter.formatBardEnergy(bardClass.getEnergyCooldown(player).getEnergy()));
                }
            } else if (pvpClass instanceof MinerClass) {
                MinerClass minerClass = (MinerClass) pvpClass;
                String minerInvs = scoreboardConfig.getString("MINER_CLASS.INVIS");
                String minerDiamonds = scoreboardConfig.getString("MINER_CLASS.DIAMONDS");
                if (minerInvs != null) {
                    toReturn.add(minerInvs + (minerClass.getInvisible().contains(player.getUniqueId()) ? "true" : "false"));
                }
                if (minerDiamonds != null) {
                    toReturn.add(minerDiamonds + session.getDiamonds());
                }
            } else if (pvpClass instanceof MageClass) {
                MageClass mageClass = (MageClass) pvpClass;
                String mageEnergy = scoreboardConfig.getString("MAGE_CLASS.MAGE_ENERGY");
                if (mageEnergy != null) {
                    toReturn.add(mageEnergy + Formatter.formatBardEnergy(mageClass.getEnergyCooldown(player).getEnergy()));
                }
            }
            for (CustomCooldown customCooldown : pvpClass.getCustomCooldowns()) {
                String cooldownName = customCooldown.getDisplayName();
                if (cooldownName == null) {
                    continue;
                }
                if (!customCooldown.hasCooldown(player)) {
                    continue;
                }
                toReturn.add(cooldownName + customCooldown.getRemaining(player));
            }
        }

        if (team != null && team.getFocusedTeam() != null) {
            if (toReturn.size() < 2) {
                toReturn.remove(0);
            }
            Team focused = team.getFocusedTeam();
            List<String> list = new ArrayList<>(scoreboardConfig.getStringList("TEAM_FOCUS.LINES"));
            list.replaceAll(s -> s.replaceAll("%team%", focused.getName()).replaceAll("%hq%", focused.getHQFormatted()).replaceAll("%online%", String.valueOf(focused.getOnlinePlayers().size())).replaceAll("%dtr-color%", focused.getDtrColor()).replaceAll("%dtr%", focused.getDtrString()).replaceAll("%dtr-symbol%", focused.getDtrSymbol()));
            toReturn.addAll(list);
        }

        if (team != null && team.getRallyPoint() != null) {
            if (toReturn.size() < 2) {
                toReturn.remove(0);
            }
            List<String> list = new ArrayList<>(scoreboardConfig.getStringList("TEAM_RALLY.LINES"));
            list.replaceAll(s -> s.replaceAll("%pos%", team.getRallyPoint().getFloorX() + ", " + team.getRallyPoint().getFloorY() + ", " + team.getRallyPoint().getFloorZ()));
            toReturn.addAll(list);
        }

        toReturn.add(lines);
        if (scoreboardConfig.getBoolean("FOOTER_CONFIG.CHANGER_ENABLED")) {
            toReturn.add(footer());
        }
        return toReturn;
    }

    private String footer() {
        List<String> footers = scoreboardConfig.getStringList("FOOTER_CONFIG.CHANGES");
        long time = System.currentTimeMillis();
        long interval = TimeUnit.MILLISECONDS.toMillis(scoreboardConfig.getInt("FOOTER_CONFIG.CHANGER_TICKS"));

        if (lastMillisFooter + interval <= time) {
            if (iFooter != footers.size() - 1) {
                iFooter++;
            } else {
                iFooter = 0;
            }
            lastMillisFooter = time;
        }
        return footers.get(iFooter);
    }

    public Scoreboard getScoreboard() {
        Scoreboard scoreboard = new Scoreboard(TextFormat.colorize(scoreboardConfig.getString("SCOREBOARD_INFO.TITLE")), DisplaySlot.SIDEBAR);
        scoreboard.setHandler(pl -> {
            for (String s : getLines(pl)) {
                scoreboard.addLine(TextFormat.colorize(s));
            }
        });
        return scoreboard;
    }

}
