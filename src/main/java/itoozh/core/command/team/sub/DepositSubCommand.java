package itoozh.core.command.team.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.session.Session;
import itoozh.core.team.Team;
import itoozh.core.util.LanguageUtils;

import java.util.regex.Pattern;

public class DepositSubCommand extends BaseSubCommand {

    public DepositSubCommand(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public String[] getAliases() {
        return new String[] {"d"};
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(TextFormat.colorize(Main.prefix + "&cUsage: /" + label + " " + args[0] + " <cant/all>"));
            return false;
        }
        Player player = (Player) sender;
        Session session = Main.getInstance().getSessionManager().getSession(player);
        Team team = session.getTeam();
        int balance = session.getBalance();
        if (team == null) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.NOT_IN_TEAM")));
            return false;
        }

        if (args[1].equalsIgnoreCase("all")) {
            if (balance <= 0) {
                sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_DEPOSIT.DEPOSIT_ZERO")));
                return false;
            }
            team.setBalance(team.getBalance() + balance);
            session.setBalance(0);
            team.broadcast(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_DEPOSIT.DEPOSITED").replaceAll("%player%", player.getName()).replaceAll("%amount%", String.valueOf(balance))));
        } else {
            int amount;
            try {
                amount = Integer.parseInt(args[1]);
                if (amount < 0) {
                    sender.sendMessage(TextFormat.colorize(Main.prefix + "&cInvalid amount. Please enter a valid number."));
                    return false;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(TextFormat.colorize(Main.prefix + "&cInvalid amount. Please enter a valid number."));
                return false;
            }

            if (balance < amount) {
                sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_DEPOSIT.INSUFFICIENT_BAL").replaceAll("%amount%", String.valueOf(amount)).replaceAll("%balance%", String.valueOf(balance))));
                return false;
            }
            session.takeBalance(amount);
            team.setBalance(team.getBalance() + amount);
            team.broadcast(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_DEPOSIT.DEPOSITED").replaceAll("%player%", player.getName()).replaceAll("%amount%", String.valueOf(amount))));
        }
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] { CommandParameter.newType("cant", CommandParamType.INT)};
    }

    public static boolean isNotAlphanumeric(String input) {
        return Pattern.compile("[^a-zA-Z0-9]").matcher(input).find();
    }
}
