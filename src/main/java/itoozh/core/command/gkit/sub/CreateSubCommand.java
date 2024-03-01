package itoozh.core.command.gkit.sub;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.gkit.GKit;

public class CreateSubCommand extends BaseSubCommand {

    public CreateSubCommand(String name) {
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
        if (args.length < 2) {
            sender.sendMessage(TextFormat.RED + "Usage: /gkit create [name]");
            return true;

        }
        String name = args[1];
        if (Main.getInstance().getGKitManager().doesGKitExist(name)) {
            sender.sendMessage(TextFormat.RED + "This gkit already exists.");
            return true;
        }

        GKit gkit = Main.getInstance().getGKitManager().createGkit(name);
        sender.sendMessage(TextFormat.GREEN + "You have created a gkit called " + gkit.getName() + ".");
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] {CommandParameter.newType("name", CommandParamType.STRING)};
    }
}
