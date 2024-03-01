package itoozh.core.command.team.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.session.Session;
import itoozh.core.team.Team;
import itoozh.core.team.player.Member;
import itoozh.core.team.player.Role;
import itoozh.core.util.LanguageUtils;
import itoozh.core.util.NameTags;

public class JoinSubCommand extends BaseSubCommand {

    public JoinSubCommand(String name) {
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
            sender.sendMessage(TextFormat.colorize(Main.prefix + "&cUsage: /" + label + " " + args[0] + " <factionName>"));
            return false;
        }
        String factionName = args[1];

        Team team = Main.getInstance().getTeamManager().getTeam(factionName);

        Player player = (Player) sender;

        Session playerSession = Main.getInstance().getSessionManager().getSession(player);

        if (playerSession == null) {
            playerSession = Main.getInstance().getSessionManager().createSession(player);
        }

        if (playerSession.getTeam() != null) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.ALREADY_IN_TEAM")));
            return false;
        }

        if (team == null) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_NOT_FOUND").replaceAll("%team%", factionName)));
            return false;
        }

        if (!team.getInvitedPlayers().contains(player.getUniqueId()) && !team.isOpen()) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_JOIN.NOT_INVITED").replaceAll("%team%", factionName)));
            return false;
        }

        if (team.getMembers().size() == Main.getInstance().getConfig().getInt("TEAMS.TEAM_SIZE")) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_JOIN.TEAM_FULL")));
            return false;
        }

        if (team.hasRegen()) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_JOIN.CANNOT_JOIN_FREEZE")));
            return false;
        }

        playerSession.setTeam(team);
        team.getMembers().add(new Member(player.getUniqueId(), Role.MEMBER));
        team.getInvitedPlayers().remove(player.getUniqueId());
        team.setDtr(team.getMaxDtr());
        team.broadcast(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_JOIN.BROADCAST_JOIN").replaceAll("%player%", player.getName())));
        Main.getInstance().getNameTags().update();
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] { CommandParameter.newType("name", CommandParamType.STRING) };
    }
}
