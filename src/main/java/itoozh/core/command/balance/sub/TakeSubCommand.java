package itoozh.core.command.balance.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.session.Session;

public class TakeSubCommand extends BaseSubCommand {

    public TakeSubCommand(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return sender.hasPermission("core.balance.take");
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(TextFormat.colorize(Main.prefix + "&cUsage: /" + label + " "  + args[0] + " <player> <amount>"));
            return false;
        }

        Player targetPlayer = Main.getInstance().getServer().getPlayer(args[1].replace("@s", sender.getName()));
        if (targetPlayer == null) {
            sender.sendMessage(TextFormat.colorize(Main.prefix + "&cPlayer not found."));
            return false;
        }

        Session targetSession = Main.getInstance().getSessionManager().getSession(targetPlayer);
        if (targetSession == null) {
            sender.sendMessage(TextFormat.colorize(Main.prefix + "&cPlayer not found."));
            return false;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
            if (amount < 0) {
                sender.sendMessage(TextFormat.colorize(Main.prefix + "&cInvalid amount. Please enter a valid number."));
                return false;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(TextFormat.colorize(Main.prefix + "&cInvalid amount. Please enter a valid number."));
            return false;
        }

        if (amount > targetSession.getBalance()) {
            sender.sendMessage(TextFormat.colorize(Main.prefix + "&cThe player does not have enough balance."));
            return false;
        }

        targetSession.takeBalance(amount);
        sender.sendMessage(TextFormat.colorize(Main.prefix + "&aYou have taken &l" + amount + "$&r&a to " + targetPlayer.getName()));
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] { CommandParameter.newType("player", CommandParamType.TARGET), CommandParameter.newType("amount", CommandParamType.INT) };
    }
}
