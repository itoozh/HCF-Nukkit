package itoozh.core.command.crates.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.session.Session;

public class UnPlaceSubCommand extends BaseSubCommand {
    public UnPlaceSubCommand(String name) {
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
        Player player = (Player) sender;
        if (player.getGamemode() != Player.CREATIVE) {
            player.sendMessage(TextFormat.colorize("&cYou can't use this in this gamemode! You need use creative mode!"));
            return true;
        }
        player.sendMessage(TextFormat.colorize("&aUse Left-Click in a block to un place the crate."));
        Session session = Main.getInstance().getSessionManager().getSession(player);
        session.removingCrate = true;
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] { CommandParameter.newType("name", CommandParamType.STRING)};
    }

}
