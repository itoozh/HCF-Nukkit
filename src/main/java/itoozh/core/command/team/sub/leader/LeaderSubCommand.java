package itoozh.core.command.team.sub.leader;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.session.Session;
import itoozh.core.team.Team;
import itoozh.core.team.player.Role;
import itoozh.core.util.LanguageUtils;


public class LeaderSubCommand extends BaseSubCommand {

    public LeaderSubCommand(String name) {
        super(name);
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

        if (args.length < 2) {
            sender.sendMessage(TextFormat.colorize(Main.prefix + "&cUsage: /" + label + " " + args[0] + " <player>"));
            return false;
        }

        Player player = (Player) sender;

        Session playerSession = Main.getInstance().getSessionManager().getSession(player);

        if (playerSession == null) {
            playerSession = Main.getInstance().getSessionManager().createSession(player);
        }

        Team team = playerSession.getTeam();

        if (team == null) {
            sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.NOT_IN_TEAM"))));
            return false;
        }

        if (!team.checkRole(player, Role.LEADER)) {
            sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.INSUFFICIENT_ROLE").replaceAll("%role%", Role.LEADER.getName()))));
            return false;
        }

        IPlayer targetPlayer = Main.getInstance().getServer().getOfflinePlayer(args[1]);
        if (targetPlayer.getUniqueId() == null) {
            sender.sendMessage(TextFormat.colorize(Main.prefix + "&cPlayer not found."));
            return false;
        }

        Session targetSession = Main.getInstance().getSessionManager().getSessionByUUID(targetPlayer.getUniqueId());
        if (targetSession == null) {
            sender.sendMessage(TextFormat.colorize(Main.prefix + "&cPlayer not found."));
            return false;
        }

        if (!team.getMembers().contains(targetSession.getMember())) {
            sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_LEADER.NOT_IN_TEAM"))));
            return false;
        }
        if (player.getUniqueId().equals(targetPlayer.getUniqueId())) {
            sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_LEADER.ALREADY_LEADER"))));
            return false;
        }
        team.getMember(player.getUniqueId()).setRole(Role.CO_LEADER);
        team.getMember(targetPlayer.getUniqueId()).setRole(Role.LEADER);
        team.setLeader(targetPlayer.getUniqueId());
        team.broadcast(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_LEADER.LEADER_CHANGED").replaceAll("%player%", targetPlayer.getName())));
        return  true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] { CommandParameter.newType("player", CommandParamType.TARGET)};
    }
}
