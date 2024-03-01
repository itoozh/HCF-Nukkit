package itoozh.core.command.team.sub.co_leader;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.session.Session;
import itoozh.core.team.Team;
import itoozh.core.team.claim.Claim;
import itoozh.core.team.player.Role;
import itoozh.core.util.LanguageUtils;
import itoozh.core.util.TeamCooldown;


public class LockSubCommand extends BaseSubCommand {
    private final TeamCooldown lockCooldown;

    public LockSubCommand(String name) {
        super(name);
        this.lockCooldown = new TeamCooldown();
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {

        Player player = (Player) sender;

        Session playerSession = Main.getInstance().getSessionManager().getSession(player);

        if (playerSession == null) {
            playerSession = Main.getInstance().getSessionManager().createSession(player);
        }

        Team team = playerSession.getTeam();

        if (team == null) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.NOT_IN_TEAM")));
            return false;
        }

        if (!Main.getInstance().getTimerManager().getSotwTimer().isActive()) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_LOCKCLAIM.ONLY_SOTW")));
            return false;
        }

        if (!team.checkRole(player, Role.CO_LEADER)) {
            sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.INSUFFICIENT_ROLE").replaceAll("%role%", Role.CO_LEADER.getName()))));
            return false;
        }

        if (team.getClaim() == null) {
            sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_UNCLAIM.NO_CLAIMS"))));
            return false;
        }
        Claim claim = team.getClaim();
        if (claim.isLocked()) {
            claim.setLocked(false);
            team.broadcast(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_LOCKCLAIM.UNLOCKED").replaceAll("%player%", player.getName())));
            return true;
        }
        if (this.lockCooldown.hasCooldown(team)) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_LOCKCLAIM.COOLDOWN").replaceAll("%seconds%", this.lockCooldown.getRemaining(team))));
            return false;
        }
        this.lockCooldown.applyCooldown(team, Main.getInstance().getConfig().getInt("TIMERS_COOLDOWN.LOCK_CLAIM"));
        claim.setLocked(true);
        team.broadcast(LanguageUtils.getString("TEAM_COMMAND.TEAM_LOCKCLAIM.LOCKED").replaceAll("%player%", player.getName()));
        for (Player online : claim.getPlayersInsideClaim()) {
            if (team.getPlayers().contains(online.getUniqueId())) {
                continue;
            }
            Main.getInstance().getTeamManager().teleportToSafe(online, 1);
            online.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_LOCKCLAIM.TELEPORTED_SAFE")));
        }
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }


}
