package itoozh.core.command.gkit.sub;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.gkit.profile.Profile;

public class ResetAllCooldownsSubCommand extends BaseSubCommand {

    public ResetAllCooldownsSubCommand(String name) {
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
        for (Profile profile : Main.getInstance().getProfileManager().getProfiles().values()) {
            profile.resetCooldowns();
        }
        sender.sendMessage(TextFormat.GREEN + "ALL COOLDOWNS HAS BEEN RESET.");
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }
}
