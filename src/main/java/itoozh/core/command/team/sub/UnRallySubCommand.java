package itoozh.core.command.team.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.session.Session;
import itoozh.core.team.Team;
import itoozh.core.util.LanguageUtils;

public class UnRallySubCommand extends BaseSubCommand {

    public UnRallySubCommand(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return true;
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
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.NOT_IN_TEAM")));
            return false;
        }

        if (team.getRallyPoint() == null) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_UNRALLY.NO_RALLY")));
            return false;
        }

        team.setRallyPoint(null);
        team.broadcast(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_UNRALLY.UNRALLIED")));
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }
}
