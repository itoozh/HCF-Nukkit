package itoozh.core.command.team.sub.captain;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.session.Session;
import itoozh.core.team.Team;
import itoozh.core.team.player.Role;
import itoozh.core.util.LanguageUtils;

public class WithdrawSubCommand extends BaseSubCommand {

    public WithdrawSubCommand(String name) {
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
        if (args.length < 2) {
            sender.sendMessage(TextFormat.colorize(Main.prefix + "&cUsage: /" + label + " " + args[0] + " <cant>"));
            return false;
        }

        Player player = (Player) sender;

        Session playerSession = Main.getInstance().getSessionManager().getSession(player);

        if (playerSession == null) {
            playerSession = Main.getInstance().getSessionManager().createSession(player);
        }

        Team team = playerSession.getTeam();

        if (team == null) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.NOT_IN_TEAM")));
            return false;
        }

        if (!team.checkRole(player, Role.CAPTAIN)) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.INSUFFICIENT_ROLE").replaceAll("%role%", Role.CAPTAIN.getName())));
            return false;
        }
        Integer money = this.getInt(args[1]);
        if (money == null) {
            sender.sendMessage(TextFormat.colorize("&cUse a valid amount!"));
            return false;
        }
        if (team.getBalance() < money) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_WITHDRAW.INSUFFICIENT_BAL")));
            return false;
        }
        playerSession.giveBalance(money);
        team.setBalance(team.getBalance() - money);
        team.broadcast(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_WITHDRAW.WITHDREW").replaceAll("%player%", player.getName()).replaceAll("%amount%", String.valueOf(money))));
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] { CommandParameter.newType("cant", CommandParamType.INT)};
    }

    public Integer getInt(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

}
