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
import itoozh.core.team.claim.ClaimType;
import itoozh.core.team.player.Role;
import itoozh.core.util.LanguageUtils;


public class SetHQSubCommand extends BaseSubCommand {

    public SetHQSubCommand(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public String[] getAliases() {
        return new String[] {"sethome"};
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

        Claim claim = Main.getInstance().getTeamManager().getClaimManager().findClaim(((Player) sender).getPosition().getFloorX(), ((Player) sender).getPosition().getFloorZ(), 0);

        if (team.getClaim() != null && team.getClaim() != claim) {
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_SETHQ.CANNOT_SET")));
            return false;
        }

        team.setHq(player.getLocation());
        team.broadcast(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_SETHQ.SETHQ").replaceAll("%player%", player.getName())));
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }


}
