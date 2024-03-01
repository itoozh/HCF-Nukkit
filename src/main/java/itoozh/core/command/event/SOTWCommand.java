package itoozh.core.command.event;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import itoozh.core.command.BaseCommand;
import itoozh.core.command.event.sub.SOTWEnableSubCommand;
import itoozh.core.command.event.sub.SOTWExtendSubCommand;
import itoozh.core.command.event.sub.SOTWStartSubCommand;
import itoozh.core.command.event.sub.SOTWStopSubCommand;
import itoozh.core.util.LanguageUtils;

public class SOTWCommand extends BaseCommand {
    public SOTWCommand() {
        super("sotw", "Use this command to manage SOTW", "", new String[0]);
        this.addSubCommand(new SOTWEnableSubCommand("enable"));
        this.addSubCommand(new SOTWStartSubCommand("start"));
        this.addSubCommand(new SOTWStopSubCommand("stop"));
        this.addSubCommand(new SOTWExtendSubCommand("extend"));
        this.setPermission("core.sotw.enable");
    }

    @Override
    public void sendUsageMessage(CommandSender sender) {
        if (sender.hasPermission("core.sotw")) {
            for (String s : LanguageUtils.splitStringToList(LanguageUtils.getString("SOTW_COMMAND.USAGE_ADMIN"))) {
                sender.sendMessage(TextFormat.colorize(s));
            }
        } else {
            for (String s : LanguageUtils.splitStringToList(LanguageUtils.getString("SOTW_COMMAND.USAGE_DEFAULT"))) {
                sender.sendMessage(TextFormat.colorize(s));
            }
        }
    }
}
