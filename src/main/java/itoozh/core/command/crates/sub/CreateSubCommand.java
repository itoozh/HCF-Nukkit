package itoozh.core.command.crates.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.crate.Crate;
import itoozh.core.crate.InventoryCrate;

public class CreateSubCommand extends BaseSubCommand {
    public CreateSubCommand(String name) {
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
        if (args.length < 5) {
            sender.sendMessage(TextFormat.colorize("&cUsage: /crates create <name> <item_key> <color> <reward_amount>"));
            return false;
        }
        Player player = (Player) sender;

        String name = args[1];
        Item itemKey = Item.fromString(args[2]);
        String color = args[3];

        int amount;
        try {
            amount = Integer.parseInt(args[4]);
            if (amount < 0) {
                sender.sendMessage(TextFormat.colorize(Main.prefix + "&cInvalid amount. Please enter a valid number."));
                return false;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(TextFormat.colorize(Main.prefix + "&cInvalid amount. Please enter a valid number."));
            return false;
        }

        if (Main.getInstance().getCrateManager().getCrate(name) != null) {
            sender.sendMessage(TextFormat.colorize("&cThis crate already exists!"));
            return false;
        }

        Crate crate = new Crate(name, color);
        crate.setItemKey(itemKey);
        crate.setRewardAmount(amount);
        openCreateMenu(player, crate);
        return false;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] { CommandParameter.newType("name", CommandParamType.STRING), CommandParameter.newEnum("item_key", CommandEnum.ENUM_ITEM), CommandParameter.newType("color", CommandParamType.STRING), CommandParameter.newType("reward_amount", CommandParamType.INT) };
    }

    public void openCreateMenu(Player player, Crate crate) {
        InventoryCrate inventory = new InventoryCrate(InventoryType.DOUBLE_CHEST, TextFormat.colorize("&r" + crate.getName() + " &eEdit Content"), crate);
        player.addWindow(inventory);
    }
}
