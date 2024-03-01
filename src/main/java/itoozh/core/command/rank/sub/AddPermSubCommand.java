package itoozh.core.command.rank.sub;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.ranks.Rank;
import itoozh.core.session.Session;

public class AddPermSubCommand extends BaseSubCommand {
    public AddPermSubCommand(String name) {
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
            sender.sendMessage(TextFormat.colorize("&cUsage: /" + label + " addperm <name> <permission>"));
            return false;
        }
        String rankName = args[1];
        String permission = args[2];

        Rank rank = Main.getInstance().getRankManager().getRank(rankName);

        if (rank == null) {
            sender.sendMessage(TextFormat.colorize("&cRank not found"));
            return false;
        }

        rank.getPermissions().add(permission);
        for (Player online : Server.getInstance().getOnlinePlayers().values()) {
            Session onlineSession = Main.getInstance().getSessionManager().getSessionByUUID(online.getUniqueId());
            if (onlineSession.getRank() == rank) {
                online.addAttachment(Main.getInstance()).setPermission(permission, true);
            }
        }
        sender.sendMessage(TextFormat.colorize("&aPermission " + permission + " has been added to " + rankName + " Rank!"));
        return false;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }
}
