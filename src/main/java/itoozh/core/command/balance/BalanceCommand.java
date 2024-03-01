package itoozh.core.command.balance;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseCommand;
import itoozh.core.command.balance.sub.TakeSubCommand;
import itoozh.core.command.balance.sub.GiveSubCommand;
import itoozh.core.session.Session;

public class BalanceCommand extends BaseCommand {

    public BalanceCommand() {
        super("balance", "Use this command to see your balance", "", new String[]{"money", "bal"});
        this.addSubCommand(new TakeSubCommand("take"));
        this.addSubCommand(new GiveSubCommand("give"));
        this.setPermission("core.balance");
    }
    @Override
    public void sendUsageMessage(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Session sessionPlayer = Main.getInstance().getSessionManager().getSession(player);
            player.sendMessage(TextFormat.colorize(Main.prefix + "&aYour balance is &l&2" + sessionPlayer.getBalance() + "$"));
        } else {
            sender.sendMessage(TextFormat.colorize(Main.prefix + "&cYou must be a player to use this command."));
        }
    }
}
