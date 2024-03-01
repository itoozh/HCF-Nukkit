package itoozh.core.command.gkit.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.gkit.GKit;

public class SetDisplayNameSubCommand extends BaseSubCommand {

    public SetDisplayNameSubCommand(String name) {
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
            sender.sendMessage(TextFormat.RED + "Usage: /gkit setdisplayname [name] [displayname]");
            return true;

        }
        String name = args[1];
        String displayName = args[2];

        if (!Main.getInstance().getGKitManager().doesGKitExist(name)) {
            sender.sendMessage(TextFormat.RED + "This gkit does not exist.");
            return true;
        }

        Player player = (Player) sender;

        GKit gKit = Main.getInstance().getGKitManager().getGKit(name);
        gKit.setDisplayName(displayName);
        player.sendMessage(TextFormat.GREEN + "You have set the displayname " +
                "for the gkit called " + gKit.getName() + ".");
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] {CommandParameter.newType("name", CommandParamType.STRING), CommandParameter.newType("cooldown", CommandParamType.INT)};
    }
}
