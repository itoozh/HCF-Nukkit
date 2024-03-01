package itoozh.core.command.pvp.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.session.timer.InvincibilityTimer;
import itoozh.core.session.timer.PvPTimer;
import itoozh.core.util.LanguageUtils;

public class EnableSubCommand extends BaseSubCommand {

    public EnableSubCommand(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public String[] getAliases() {
        return new String[] {"clear"};
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        InvincibilityTimer inviTimer = Main.getInstance().getTimerManager().getInvincibilityTimer();
        PvPTimer pvpTimer = Main.getInstance().getTimerManager().getPvPTimer();
        if (!pvpTimer.hasTimer(player) && !inviTimer.hasTimer(player)) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVPTIMER_COMMAND.NO_TIMERS")));
            return false;
        }
        if (inviTimer.hasTimer(player)) {
            inviTimer.removeTimer(player);
        }
        if (pvpTimer.hasTimer(player)) {
            pvpTimer.removeTimer(player);
        }
        Main.getInstance().getNameTags().update();
        sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVPTIMER_COMMAND.ENABLED")));
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }
}
