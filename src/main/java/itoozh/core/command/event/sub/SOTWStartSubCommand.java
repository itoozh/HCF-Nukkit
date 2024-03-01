package itoozh.core.command.event.sub;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.timer.server.SOTWTimer;
import itoozh.core.util.Formatter;
import itoozh.core.util.LanguageUtils;

public class SOTWStartSubCommand extends BaseSubCommand {
    public SOTWStartSubCommand(String name) {
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
        SOTWTimer timer = Main.getInstance().getTimerManager().getSotwTimer();
        if (!sender.hasPermission("core.sotw")) {
            sender.sendMessage(TextFormat.colorize("&cYou don't have permission to use this command"));
            return false;
        }
        if (args.length < 2) {
            sender.sendMessage(TextFormat.colorize("&cUsage: /" + label + " " + args[0] + " <time>"));
            return false;
        }
        Long time = Formatter.parse(args[1]);
        if (time == null) {
            sender.sendMessage(TextFormat.colorize("Use a valid time"));
            return false;
        }
        if (timer.isActive()) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("SOTW_COMMAND.SOTW_START.ALREADY_ACTIVE")));
            return false;
        }
        timer.startSOTW(time);
        sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("SOTW_COMMAND.SOTW_START.STARTED")));
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }
}
