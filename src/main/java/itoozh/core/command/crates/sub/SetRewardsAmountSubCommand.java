package itoozh.core.command.crates.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.crate.Crate;

public class SetRewardsAmountSubCommand extends BaseSubCommand {
    public SetRewardsAmountSubCommand(String name) {
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
        if (args.length < 3) {
            sender.sendMessage(TextFormat.colorize("&cUsage: /place setrewardsamount <name> <amount>"));
            return false;
        }

        String name = args[1];
        int amount;

        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(TextFormat.colorize("&cUse a valid number!"));
            return false;
        }

        Crate crate = Main.getInstance().getCrateManager().getCrate(name);

        if (crate == null) {
            sender.sendMessage(TextFormat.colorize("&cCrate not found."));
            return false;
        }

        crate.setRewardAmount(amount);
        sender.sendMessage(TextFormat.colorize("&aRewards amount in " + crate.getName() + " is updated!"));
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] { CommandParameter.newType("name", CommandParamType.STRING), CommandParameter.newType("amount", CommandParamType.INT)};
    }


}
