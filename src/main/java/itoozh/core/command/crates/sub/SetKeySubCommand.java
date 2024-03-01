package itoozh.core.command.crates.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.crate.Crate;

public class SetKeySubCommand extends BaseSubCommand {
    public SetKeySubCommand(String name) {
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
            sender.sendMessage(TextFormat.colorize("&cUsage: /place setkey <name>"));
            return false;
        }

        String name = args[1];

        Crate crate = Main.getInstance().getCrateManager().getCrate(name);

        if (crate == null) {
            sender.sendMessage(TextFormat.colorize("&cCrate not found."));
            return false;
        }

        Player player = (Player) sender;
        crate.setItemKey(player.getInventory().getItemInHand());
        sender.sendMessage(TextFormat.colorize("&aKey in " + crate.getName() + "crate has been updated!"));
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] { CommandParameter.newType("name", CommandParamType.STRING)};
    }


}
