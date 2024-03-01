package itoozh.core.command.event.sub;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.timer.server.SOTWTimer;
import itoozh.core.util.LanguageUtils;

public class SOTWStopSubCommand extends BaseSubCommand {
    public SOTWStopSubCommand(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return true;
    }

    @Override
    public String[] getAliases() {
        return new String[] {"end"};
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        SOTWTimer timer = Main.getInstance().getTimerManager().getSotwTimer();
        if (!sender.hasPermission("core.sotw")) {
            sender.sendMessage(TextFormat.colorize("&cYou don't have permission to use this command"));
            return false;
        }
        if (!timer.isActive()) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("SOTW_COMMAND.NOT_ACTIVE")));
            return false;
        }
        timer.endSOTW();
        timer.getEnabled().clear();
        sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("SOTW_COMMAND.SOTW_END.ENDED")));
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }
}
