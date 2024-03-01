package itoozh.core.command.ability.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.ability.Ability;
import itoozh.core.command.BaseSubCommand;

public class GetAllSubCommand extends BaseSubCommand {
    public GetAllSubCommand(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return true;
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(TextFormat.colorize("&cOnly players can use this command"));
            return false;
        }
        for (Ability ability : Main.getInstance().getAbilityManager().getAbilities().values()) {
            ((Player) sender).getInventory().addItem(ability.getItem());
        }
        return false;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }
}
