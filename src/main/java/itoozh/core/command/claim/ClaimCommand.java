package itoozh.core.command.claim;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseCommand;
import itoozh.core.command.balance.sub.GiveSubCommand;
import itoozh.core.command.balance.sub.TakeSubCommand;
import itoozh.core.command.claim.sub.CreateSubCommand;
import itoozh.core.command.claim.sub.ListSubCommand;
import itoozh.core.command.claim.sub.RemoveSubCommand;
import itoozh.core.session.Session;

public class ClaimCommand extends BaseCommand {

    public ClaimCommand() {
        super("claim", "Use this command to manage servers claims", "/claim <create/delete>", new String[]{"protections"});
        this.addSubCommand(new CreateSubCommand("create"));
        this.addSubCommand(new RemoveSubCommand("remove"));
        this.addSubCommand(new ListSubCommand("list"));
        this.setPermission("core.claim");
    }
    @Override
    public void sendUsageMessage(CommandSender sender) {
        sender.sendMessage(TextFormat.colorize("&cUsage: " + getUsage()));
    }
}
