package itoozh.core.command.crates.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.crate.Crate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetHologramSubCommand extends BaseSubCommand {
    public SetHologramSubCommand(String name) {
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
            sender.sendMessage(TextFormat.colorize("&cUsage: /place hologram <name> <hologram>"));
            return false;
        }

        String name = args[1];
        String hologram = args[2];

        Crate crate = Main.getInstance().getCrateManager().getCrate(name);

        if (crate == null) {
            sender.sendMessage(TextFormat.colorize("&cCrate not found."));
            return false;
        }

        crate.setHologramText(splitString(hologram));
        Main.getInstance().getHologramManager().restartHologram();
        sender.sendMessage(TextFormat.colorize("&aHologram in " + crate.getName() + " updated! If you need updated the holograms you need restart the server."));
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] { CommandParameter.newType("name", CommandParamType.STRING), CommandParameter.newType("hologram", CommandParamType.TEXT)};
    }

    public static List<String> splitString(String input) {

        String[] parts = input.split("\\{n\\}");

        return new ArrayList<>(Arrays.asList(parts));
    }

}
