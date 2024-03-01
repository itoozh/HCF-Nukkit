package itoozh.core.command.crates.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.crate.Crate;
import itoozh.core.crate.InventoryCrate;

import java.util.Map;

public class SetContentSubCommand extends BaseSubCommand {
    public SetContentSubCommand(String name) {
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
        if (args.length < 2) {
            sender.sendMessage(TextFormat.colorize("&cUsage: /place setcontent <name>"));
            return false;
        }

        String name = args[1];

        Crate crate = Main.getInstance().getCrateManager().getCrate(name);

        if (crate == null) {
            sender.sendMessage(TextFormat.colorize("&cCrate not found."));
            return false;
        }

        Player player = (Player) sender;
        openEditMenu(player, crate);
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] { CommandParameter.newType("name", CommandParamType.STRING)};
    }

    public void openEditMenu(Player player, Crate crate) {
        InventoryCrate inventory = new InventoryCrate(InventoryType.DOUBLE_CHEST, TextFormat.colorize("&r" + crate.getName() + " &eEdit Content"), crate);
        for (Map.Entry<Integer, Item> entry : crate.getRewards().entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue());
        }
        player.addWindow(inventory);
    }

}
