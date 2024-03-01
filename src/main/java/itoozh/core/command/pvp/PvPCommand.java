package itoozh.core.command.pvp;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import itoozh.core.command.BaseCommand;
import itoozh.core.command.pvp.sub.EnableSubCommand;
import itoozh.core.util.LanguageUtils;

public class PvPCommand extends BaseCommand {

    public PvPCommand() {
        super("pvp", "Use this command to manage your pvp timer", LanguageUtils.getString("PVPTIMER_COMMAND.USAGE"), new String[0]);
        this.addSubCommand(new EnableSubCommand("enable"));
        this.setPermission("core.pvp");
    }

    @Override
    public void sendUsageMessage(CommandSender sender) {
        if (!(sender instanceof Player)) return;
        sender.sendMessage(TextFormat.colorize(this.getUsage()));
    }
}
