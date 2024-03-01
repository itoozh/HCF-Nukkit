package itoozh.core.command.crates.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.crate.Crate;

public class KeySubCommand extends BaseSubCommand {
    public KeySubCommand(String name) {
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
            sender.sendMessage(TextFormat.colorize("&cUsage: /place key <name> <amount>"));
            return false;
        }

        String name = args[1];
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

        Crate crate = Main.getInstance().getCrateManager().getCrate(name);

        if (crate == null) {
            sender.sendMessage(TextFormat.colorize("&cCrate not found."));
            return false;
        }
        Player player = (Player) sender;
        Item item = crate.getKeyItem(amount);
        if (player.getInventory().canAddItem(item)) {
            player.getInventory().addItem(item);
        } else {
            player.dropItem(item);
        }
        player.sendTitle(TextFormat.colorize("§6§lKEYS"), TextFormat.colorize("§6§l|§r §fYou have received §6x" + amount + " " + crate.getDisplayName() + " §fkeys."));
        player.sendMessage(TextFormat.colorize("&aSuccessfully has been given x" + amount + " keys."));
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] { CommandParameter.newType("name", CommandParamType.STRING), CommandParameter.newType("amount", CommandParamType.INT)};
    }

}
