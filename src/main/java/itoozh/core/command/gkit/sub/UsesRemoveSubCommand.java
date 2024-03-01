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
import itoozh.core.gkit.profile.Profile;

public class UsesRemoveSubCommand extends BaseSubCommand {

    public UsesRemoveSubCommand(String name) {
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
        if (args.length < 4) {
            sender.sendMessage(TextFormat.RED + "Usage: /gkit usesremove [name] [player] [amount]");
            return true;

        }
        String name = args[1];

        int amount;
        try {
            amount = Integer.parseInt(args[3]);
            if (amount <= 0) {
                sender.sendMessage(TextFormat.RED + "Use a valid number!");
                return false;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(TextFormat.RED + "Use a valid number!");
            return false;
        }

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

        if (profile == null) {
            sender.sendMessage(TextFormat.RED + "This player does not have a profile.");
            return true;
        }

        if (profile.getUses(gkit) < amount) {
            sender.sendMessage(TextFormat.RED + "That player does not have enough uses.");
            return true;
        }

        profile.setUses(gkit, profile.getUsesFor(gkit).getAmount() - amount);
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] {CommandParameter.newType("name", CommandParamType.STRING), CommandParameter.newType("cooldown", CommandParamType.INT)};
    }
}
