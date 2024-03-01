package itoozh.core.command.team.sub.captain;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
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
import itoozh.core.util.NameTags;

public class KickSubCommand extends BaseSubCommand {

    public KickSubCommand(String name) {
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

        if (!team.getMembers().contains(team.getMember(targetSession.getUUID()))) {
            sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_KICK.NOT_IN_TEAM"))));
            return false;
        }

        if (player.getUniqueId() == targetPlayer.getUniqueId()) {
            sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_KICK.CANNOT_KICK_SELF"))));
            return false;
        }

        if (!team.checkRole(player, team.getMember(targetPlayer.getUniqueId()).getRole())) {
            sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_KICK.HIGHER_ROLE").replaceAll("%player%", targetPlayer.getName()))));
            return false;
        }

        team.getMembers().remove(team.getMember(targetPlayer.getUniqueId()));
        if (team.getDtr() > team.getMaxDtr()) {
            team.setDtr(team.getMaxDtr());
        }
        team.broadcast(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_KICK.BROADCAST_TEAM").replaceAll("%player%", targetPlayer.getName()))));
        targetSession.setTeam(null);
        if (targetPlayer.isOnline()) {
            Main.getInstance().getNameTags().update();
            targetPlayer.getPlayer().sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_KICK.KICKED_MESSAGE").replaceAll("%team%", team.getName())));
        }
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] { CommandParameter.newType("player", CommandParamType.TARGET)};
    }

}
