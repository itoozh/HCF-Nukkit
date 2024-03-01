package itoozh.core.command.crates.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.crate.Crate;

import java.util.Map;

public class ListSubCommand extends BaseSubCommand {
    public ListSubCommand(String name) {
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

        Map<String, Crate> crates = Main.getInstance().getCrateManager().getCrates();
        if (crates.isEmpty()) {
            sender.sendMessage(TextFormat.RED + "There are no crates.");
            return true;
        }
        sender.sendMessage(TextFormat.colorize("&aCrates list:"));
        int i = 1;
        for (Crate crate : crates.values()) {
            sender.sendMessage(TextFormat.colorize("  &r" + i + "&7. " + crate.getColor() + crate.getName()));
            i++;
        }
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }
}
