package itoozh.core.command.gkit.sub;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.gkit.GKit;
import itoozh.core.gkit.GKitCooldown;
import itoozh.core.gkit.profile.Profile;

public class CooldownResetSubCommand extends BaseSubCommand {

    public CooldownResetSubCommand(String name) {
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
            sender.sendMessage(TextFormat.RED + "Usage: /gkit cooldownreset [name] [player]");
            return true;

        }
        String name = args[1];

        IPlayer targetPlayer = Main.getInstance().getServer().getOfflinePlayer(args[2]);
        if (targetPlayer.getUniqueId() == null) {
            sender.sendMessage(TextFormat.colorize("&cPlayer not found."));
            return false;
        }

        if (!Main.getInstance().getGKitManager().doesGKitExist(name)) {
            sender.sendMessage(TextFormat.RED + "This gkit does not exist.");
            return true;
        }

        GKit gkit = Main.getInstance().getGKitManager().getGKit(name);

        Profile profile = Main.getInstance().getProfileManager().getProfile(targetPlayer.getUniqueId());
        GKitCooldown gKitCooldown = profile.getCooldownFor(gkit);
        if (gKitCooldown == null) {
            sender.sendMessage(TextFormat.RED + "The player has no cooldown for this gkit.");
            return true;
        }
        gKitCooldown.setRemaining(1L);
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] {CommandParameter.newType("name", CommandParamType.STRING), CommandParameter.newType("cooldown", CommandParamType.INT)};
    }
}
