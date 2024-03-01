package itoozh.core.command.rank.sub;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.ranks.Rank;

import java.util.Map;

public class ListSubCommand extends BaseSubCommand {
    public ListSubCommand(String name) {
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
        Map<String, Rank> ranks = Main.getInstance().getRankManager().getRanks();
        if (ranks.isEmpty()) {
            sender.sendMessage(TextFormat.colorize("&cNo ranks valuables!"));
            return false;
        }
        int i = 1;
        sender.sendMessage(TextFormat.colorize("&7-----------------------\n&l&aRanks:\n"));
        for (Rank rank : ranks.values()) {
            sender.sendMessage(TextFormat.colorize("&r  " + i + ". " + rank.getColor() + rank.getName()));
            i++;
        }
        sender.sendMessage(TextFormat.colorize("&7-----------------------"));
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }
}
