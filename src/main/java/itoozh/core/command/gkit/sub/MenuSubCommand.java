package itoozh.core.command.gkit.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.utils.TextFormat;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.gkit.InventoryEditGKit;

public class MenuSubCommand extends BaseSubCommand {

    public MenuSubCommand(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return sender.hasPermission("core.gkit");
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        InventoryEditGKit inventory = new InventoryEditGKit(InventoryType.DOUBLE_CHEST, TextFormat.colorize("&6GKits &8(Edit Menu)"));
        player.addWindow(inventory);
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }
}
