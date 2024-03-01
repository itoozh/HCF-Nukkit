package itoozh.core.command.ability;

import cn.nukkit.command.CommandSender;
import itoozh.core.command.BaseCommand;
import itoozh.core.command.ability.sub.GetAllSubCommand;

public class AbilityCommand extends BaseCommand {
    public AbilityCommand() {
        super("ability", "Use this command to give abilities", "&cUsage: /ability <getall>", new String[0]);
        this.addSubCommand(new GetAllSubCommand("getall"));
        this.setPermission("core.ability");
    }

    @Override
    public void sendUsageMessage(CommandSender sender) {
        sender.sendMessage(usageMessage);
    }
}
