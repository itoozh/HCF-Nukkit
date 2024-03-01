package itoozh.core.command.gkit.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.gkit.GKit;

import java.util.Map;

public class SetItemsSubCommand extends BaseSubCommand {

    public SetItemsSubCommand(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return sender.hasPermission("core.gkit") && sender instanceof Player;
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(TextFormat.RED + "Usage: /gkit setitems [name]");
            return true;

        }
        String name = args[1];
        if (!Main.getInstance().getGKitManager().doesGKitExist(name)) {
            sender.sendMessage(TextFormat.RED + "This gkit does not exist.");
            return true;
        }

        Player player = (Player) sender;

        GKit gKit = Main.getInstance().getGKitManager().getGKit(name);
        gKit.setArmor(player.getInventory().getArmorContents());

        Item[] itemsArray = new Item[player.getInventory().getContents().size()];
        for (Map.Entry<Integer, Item> item : player.getInventory().getContents().entrySet()) {
            if (item.getKey() == 36 || item.getKey() == 37 || item.getKey() == 38 || item.getKey() == 39) continue;
            itemsArray[item.getKey()] = item.getValue();
        }
        gKit.setContents(itemsArray);
        player.sendMessage(TextFormat.GREEN + "You have set the items for the gkit called " + gKit.getName() + ".");
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] {CommandParameter.newType("name", CommandParamType.STRING)};
    }
}
