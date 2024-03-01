package itoozh.core.command.team.sub;

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
import itoozh.core.util.NameTags;

import java.util.regex.Pattern;

public class LeaveSubCommand extends BaseSubCommand {

    public LeaveSubCommand(String name) {
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
        Player player = (Player) sender;
        Session session = Main.getInstance().getSessionManager().getSession(player);
        Team team = session.getTeam();
        if (team == null) {
            sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.NOT_IN_TEAM"))));
            return false;
        }
        if (team.checkRole(player, Role.LEADER)) {
            sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_LEAVE.CANNOT_LEAVE_LEADER"))));
            return false;
        }
        Claim teamClaim = team.getClaim();
        Claim currentClaim = session.getCurrentClaim();

        if (currentClaim != null && currentClaim.equals(teamClaim)) {
            sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_LEAVE.CANNOT_LEAVE_IN_CLAIM"))));
            return false;
        }

        team.getMembers().remove(team.getMember(player.getUniqueId()));
        if (team.getDtr() > team.getMaxDtr()) {
            team.setDtr(team.getMaxDtr());
        }
        session.setTeam(null);
        Main.getInstance().getNameTags().update();
        team.broadcast(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_LEAVE.BROADCAST_TEAM").replaceAll("%player%", player.getName()))));

        sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_LEAVE.LEFT_MESSAGE"))));
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }

    public static boolean isNotAlphanumeric(String input) {
        return Pattern.compile("[^a-zA-Z0-9]").matcher(input).find();
    }
}
