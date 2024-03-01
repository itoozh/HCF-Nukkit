package itoozh.core.command.rank.sub;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.ranks.Rank;

import java.util.ArrayList;

public class CreateSubCommand extends BaseSubCommand {
    public CreateSubCommand(String name) {
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
        if (args.length < 5) {
            sender.sendMessage(TextFormat.colorize("&cUsage: /" + label + " create <name> <color> <prefix> <suffix>"));
            return false;
        }
        String rankName = args[1];
        String color = args[2];
        String prefix = args[3];
        String suffix = args[4];

        if (Main.getInstance().getRankManager().getRank(rankName) != null) {
            sender.sendMessage(TextFormat.colorize("&cRank already exists"));
            return false;
        }

        Rank rank = new Rank(rankName, color, prefix, suffix, new ArrayList<>());
        Main.getInstance().getRankManager().addRank(rank);
        sender.sendMessage(TextFormat.colorize("&aRank " + rankName + " has been created!"));
        return false;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }
}
