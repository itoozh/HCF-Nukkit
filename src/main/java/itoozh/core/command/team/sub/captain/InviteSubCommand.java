package itoozh.core.command.team.sub.captain;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.session.Session;
import itoozh.core.team.Team;
import itoozh.core.team.player.Role;
import itoozh.core.util.LanguageUtils;
import itoozh.core.util.TaskUtils;

import java.util.regex.Pattern;

public class InviteSubCommand extends BaseSubCommand {

    public InviteSubCommand(String name) {
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

        if (!team.checkRole(player, Role.CAPTAIN)) {
            sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.INSUFFICIENT_ROLE").replaceAll("%role%", Role.CAPTAIN.getName()))));
            return false;
        }

        Player targetPlayer = Main.getInstance().getServer().getPlayer(args[1]);
        if (targetPlayer.getUniqueId() == null) {
            sender.sendMessage(TextFormat.colorize(Main.prefix + "&cPlayer not found."));
            return false;
        }

        Session targetSession = Main.getInstance().getSessionManager().getSession(targetPlayer);
        if (targetSession == null) {
            sender.sendMessage(TextFormat.colorize(Main.prefix + "&cPlayer not found."));
            return false;
        }

        if (targetSession.getTeam() != null) {
            sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_INVITE.ALREADY_IN_TEAM"))));
            return false;
        }

        if (team.getInvitedPlayers().contains(targetPlayer.getUniqueId())) {
            sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_INVITE.ALREADY_INVITED"))));
            return false;
        }
        team.broadcast(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_INVITE.BROADCAST_INVITE").replaceAll("%player%", targetPlayer.getName())));
        targetPlayer.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_INVITE.MESSAGE_INVITE").replaceAll("%team%", team.getName()).replaceAll("%player%", player.getName()))));
        team.getInvitedPlayers().add(targetPlayer.getUniqueId());
        TaskUtils.executeLater(20 * 180, () -> team.getInvitedPlayers().remove(targetPlayer.getUniqueId()));
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] { CommandParameter.newType("player", CommandParamType.TARGET)};
    }

}
