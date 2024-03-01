package itoozh.core.command.rank.sub;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.ranks.Rank;

public class InfoSubCommand extends BaseSubCommand {
    public InfoSubCommand(String name) {
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
        if (args.length < 2) {
            sender.sendMessage(TextFormat.colorize("&cUsage: /" + label + " create <name>"));
            return false;
        }
        String rankName = args[1];

        Rank rank = Main.getInstance().getRankManager().getRank(rankName);

        if (rank == null) {
            sender.sendMessage(TextFormat.colorize("&cRank not found"));
            return false;
        }
        String[] text1 = {
                "&r&7-----------------------",
                "&a&lRank Info:",
                "&fName: &a" + rank.getName(),
                "&fColor: &a" + rank.getColor() + "■",
                "&fPrefix: &a" + rank.getPrefix(),
                "&fSuffix: &a" + rank.getSuffix(),
                "&fPermissions: "
        };
        for (String line : text1) {
            sender.sendMessage(TextFormat.colorize(line));
        }
        for (String permission : rank.getPermissions()) {
            sender.sendMessage(TextFormat.colorize("  &7- &f" + permission));
        }
        sender.sendMessage(TextFormat.colorize("&r&7-----------------------"));

        return false;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }
}
