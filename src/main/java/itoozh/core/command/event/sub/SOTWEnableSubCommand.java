package itoozh.core.command.event.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.timer.server.SOTWTimer;
import itoozh.core.util.LanguageUtils;

public class SOTWEnableSubCommand extends BaseSubCommand {
    public SOTWEnableSubCommand(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        SOTWTimer timer = Main.getInstance().getTimerManager().getSotwTimer();
        if (!(sender instanceof Player)) {
            sender.sendMessage(TextFormat.colorize("&cYou must be a player to use this command"));
            return false;
        }
        if (!timer.isActive()) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("SOTW_COMMAND.NOT_ACTIVE")));
            return false;
        }
        Player player = (Player) sender;
        if (timer.getEnabled().contains(player.getUniqueId())) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("SOTW_COMMAND.SOTW_ENABLE.ALREADY_ENABLED")));
            return false;
        }
        timer.getEnabled().add(player.getUniqueId());
        sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("SOTW_COMMAND.SOTW_ENABLE.ENABLED")));
        Main.getInstance().getNameTags().update();
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }
}
