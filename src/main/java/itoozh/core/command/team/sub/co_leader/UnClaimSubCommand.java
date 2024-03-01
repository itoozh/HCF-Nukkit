package itoozh.core.command.team.sub.co_leader;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.session.Session;
import itoozh.core.session.procces.type.ClaimProcess;
import itoozh.core.team.Team;
import itoozh.core.team.claim.Claim;
import itoozh.core.team.claim.ClaimManager;
import itoozh.core.team.claim.ClaimType;
import itoozh.core.team.player.Role;
import itoozh.core.util.LanguageUtils;


public class UnClaimSubCommand extends BaseSubCommand {

    public UnClaimSubCommand(String name) {
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

        Session playerSession = Main.getInstance().getSessionManager().getSession(player);

        if (playerSession == null) {
            playerSession = Main.getInstance().getSessionManager().createSession(player);
        }

        Team team = playerSession.getTeam();

        if (team == null) {
            sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.NOT_IN_TEAM"))));
            return false;
        }

        if (!team.checkRole(player, Role.CO_LEADER)) {
            sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.INSUFFICIENT_ROLE").replaceAll("%role%", Role.CO_LEADER.getName()))));
            return false;
        }

        if (team.getClaim() == null) {
            sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_UNCLAIM.NO_CLAIMS"))));
            return false;
        }

        if (team.isRaidable()) {
            sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_UNCLAIM.RAIDABLE"))));
            return false;
        }

        int price = team.getClaim().getPrice();
        Claim claim = team.getClaim();
        Main.getInstance().getTeamManager().getClaimManager().deleteFactionClaim(team);
        team.setHq(null);
        team.setBalance(team.getBalance() + price);
        team.setClaim(null);
        team.broadcast(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_UNCLAIM.UNCLAIMED_LAND").replaceAll("%player%", player.getName()).replaceAll("%x1%", String.valueOf(claim.getX1())).replaceAll("%z1%", String.valueOf(claim.getZ1())).replaceAll("%x2%", String.valueOf(claim.getX2())).replaceAll("%z2%", String.valueOf(claim.getZ2())).replaceAll("%balance%", String.valueOf(price))));


        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }


}
