package itoozh.core.command.team.sub;

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
import itoozh.core.util.LanguageUtils;
import itoozh.core.util.NameTags;

public class FocusSubCommand extends BaseSubCommand {

    public FocusSubCommand(String name) {
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
            sender.sendMessage(TextFormat.colorize(Main.prefix + "&cUsage: /" + label + " " + args[0] + " <team/player>"));
            return false;
        }
        Team senderTeam = Main.getInstance().getSessionManager().getSession((Player) sender).getTeam();

        if (senderTeam == null){
            sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.NOT_IN_TEAM"))));
            return false;
        }

        IPlayer target = Main.getInstance().getServer().getOfflinePlayer(args[1]);
        Team team = Main.getInstance().getTeamManager().getTeam(args[1]);

        Session targetSession = null;
        if (target.getUniqueId() != null) {
            targetSession = Main.getInstance().getSessionManager().getSessionByUUID(target.getUniqueId());
        }


        Team targetTeam = null;
        if (targetSession != null) {
            targetTeam = targetSession.getTeam();
        }

        if (targetTeam == null && team == null) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_NOT_FOUND").replaceAll("%team%", args[1])));
            return false;
        }

        if (targetTeam != null) {
            team = targetTeam;
        }

        if (team.equals(senderTeam)) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_FOCUS.FOCUS_SELF")));
            return false;
        }

        if (senderTeam.getFocusedTeam() != null && senderTeam.getFocusedTeam().equals(team)) {
            senderTeam.setFocusedTeam(null);
            senderTeam.broadcast(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_UNFOCUS.FOCUS_CLEARED")));
            Main.getInstance().getNameTags().update();
            return false;
        }

        senderTeam.setFocusedTeam(team);
        Main.getInstance().getNameTags().update();
        senderTeam.broadcast(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_FOCUS.FOCUS_UPDATED").replaceAll("%team%", team.getName())));
        return true;

    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] { CommandParameter.newType("name", CommandParamType.STRING)};
    }
}
