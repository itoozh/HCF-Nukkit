package itoozh.core.command.rank;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import itoozh.core.command.BaseCommand;
import itoozh.core.command.rank.sub.*;

public class RankCommand extends BaseCommand {
    public RankCommand() {
        super("rank", "Use this command to manage the ranks", "&cUsage: /rank <set|info|create|addperm|removeperm|player|list>", new String[]{"ranks"});
        this.addSubCommand(new ListSubCommand("list"));
        this.addSubCommand(new SetSubCommand("set"));
        this.addSubCommand(new AddPermSubCommand("addperm"));
        this.addSubCommand(new CreateSubCommand("create"));
        this.addSubCommand(new InfoSubCommand("info"));
        this.setPermission("core.rank");
    }

    @Override
    public void sendUsageMessage(CommandSender sender) {
        sender.sendMessage(TextFormat.colorize(this.getUsage()));
    }
}
