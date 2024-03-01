package itoozh.core.command.gkit.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.gkit.GKit;

public class SetFreeUsesSubCommand extends BaseSubCommand {

    public SetFreeUsesSubCommand(String name) {
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
        if (args.length < 3) {
            sender.sendMessage(TextFormat.RED + "Usage: /gkit setfreeUses [name] [uses]");
            return true;

        }
        String name = args[1];

        int freeUses;
        try {
            freeUses = Integer.parseInt(args[2]);
            if (freeUses < 0) {
                sender.sendMessage(TextFormat.RED + "The free uses must be positive.");
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(TextFormat.RED + "The free uses must be a number.");
            return true;
        }



        if (!Main.getInstance().getGKitManager().doesGKitExist(name)) {
            sender.sendMessage(TextFormat.RED + "This gkit does not exist.");
            return true;
        }

        Player player = (Player) sender;

        GKit gKit = Main.getInstance().getGKitManager().getGKit(name);
        gKit.setFreeUses(freeUses);
        player.sendMessage(TextFormat.GREEN + "You have set the free uses " + "for the gkit called " + gKit.getName() + ".");
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] {CommandParameter.newType("name", CommandParamType.STRING), CommandParameter.newType("uses", CommandParamType.INT)};
    }
}
