package itoozh.core.command.team;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Location;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseCommand;
import itoozh.core.session.Session;
import itoozh.core.team.Team;
import itoozh.core.util.LanguageUtils;

public class TlCommand extends BaseCommand {
    public TlCommand() {
        super("tl", "Use this command for send your location", "", new String[] {"telllocation", "tellloc"});
        this.setPermission("core.team");
    }

    @Override
    public void sendUsageMessage(CommandSender sender) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        Session session = Main.getInstance().getSessionManager().getSessionByUUID(player.getUniqueId());
        Team team = session.getTeam();
        if (team == null) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.NOT_IN_TEAM")));
            return;
        }
        team.broadcast(LanguageUtils.getString("TELL_LOC_COMMAND.FORMAT").replaceAll("%player%", player.getName()).replaceAll("%location%", formatLocation(player.getLocation())));
    }

    public static String formatLocation(Location location) {
        return location.getFloorX() + ", " + location.getFloorY() + ", " + location.getFloorZ();
    }
}
