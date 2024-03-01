package itoozh.core.command.team.sub;

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
import itoozh.core.util.LanguageUtils;

import java.util.regex.Pattern;

public class InfoSubCommand extends BaseSubCommand {

    public InfoSubCommand(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public String[] getAliases() {
        return new String[] {"i", "who"};
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            Player player = (Player) sender;
            Team team = Main.getInstance().getSessionManager().getSession(player).getTeam();
            if (team != null) {
                for (String s : team.getTeamInfo(player)) {
                    sender.sendMessage(TextFormat.colorize(s));
                }
                return true;
            }
            sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.NOT_IN_TEAM"))));
            return false;
        } else {
            if (args.length < 2) {
                sender.sendMessage(TextFormat.colorize(Main.prefix + "&cUsage: /" + label + " " + args[0] + " <team/player>"));
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
            for (String s : team.getTeamInfo(sender)) {
                sender.sendMessage(TextFormat.colorize(s));
            }
            return true;
        }
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] { CommandParameter.newType("name", CommandParamType.STRING)};
    }
}
