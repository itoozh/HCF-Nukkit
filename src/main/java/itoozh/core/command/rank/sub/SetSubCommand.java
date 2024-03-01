package itoozh.core.command.rank.sub;

import cn.nukkit.IPlayer;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.ranks.Rank;
import itoozh.core.session.Session;

public class SetSubCommand extends BaseSubCommand {
    public SetSubCommand(String name) {
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
        if (args.length < 3) {
            sender.sendMessage(TextFormat.colorize("&cUsage: /" + label + " create <name> <player>"));
            return false;
        }
        String rankName = args[1];
        String playerName = args[2];

        IPlayer player = Server.getInstance().getOfflinePlayer(playerName);
        if (player.getUniqueId() == null) {
            sender.sendMessage(TextFormat.colorize("&cPlayer not found"));
            return false;
        }

        Rank rank = Main.getInstance().getRankManager().getRank(rankName);

        if (rank == null) {
            sender.sendMessage(TextFormat.colorize("&cRank not found"));
            return false;
        }

        Session session = Main.getInstance().getSessionManager().getSessionByUUID(player.getUniqueId());
        if (session == null) {
            sender.sendMessage(TextFormat.colorize("&cSession not found"));
            return false;
        }
        session.getRank().removePerm(player.getPlayer());
        session.setRank(rank);
        rank.setPerm(player.getPlayer());
        sender.sendMessage(TextFormat.colorize("&aRank has been updated in " + playerName + " to " + rankName + " Rank!"));
        return false;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }
}
