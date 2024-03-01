package itoozh.core.command.team.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.session.Session;
import itoozh.core.session.timer.HQTimer;
import itoozh.core.team.Team;
import itoozh.core.team.claim.Claim;
import itoozh.core.team.claim.ClaimType;
import itoozh.core.util.LanguageUtils;

public class HQSubCommand extends BaseSubCommand {

    public HQSubCommand(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return true;
    }

    @Override
    public String[] getAliases() {
        return new String[] {"home"};
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        Session session = Main.getInstance().getSessionManager().getSession(player);
        Team team = session.getTeam();
        HQTimer timer = Main.getInstance().getTimerManager().getHqTimer();
        if (team == null) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.NOT_IN_TEAM")));
            return false;
        }
        if (team.getHq() == null) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_HQ.NO_HQ")));
            return false;
        }
        if (timer.hasTimer(player)) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_HQ.ALREADY_WARPING")));
            return false;
        }
        if (Main.getInstance().getConfig().getBoolean("COMBAT_TIMER.HQ_TELEPORT") && Main.getInstance().getTimerManager().getCombatTimer().hasTimer(player)) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_HQ.COMBAT_TAGGED")));
            return false;
        }
        Claim claimTeam = Main.getInstance().getTeamManager().getClaimManager().findClaim(player.getLocation());
        if (claimTeam != null) {
            if (!Main.getInstance().getConfig().getBoolean("HQ_TIMER.ALLOW_TP_OTHER_CLAIM") && claimTeam.getType() == ClaimType.TEAM) {
                player.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_HQ.NOT_ALLOWED")));
                return false;
            }
            if (claimTeam.getType() == ClaimType.SPAWN && Main.getInstance().getConfig().getBoolean("HQ_TIMER.INSTANT_TP_SPAWN")) {
                timer.tpHq(player);
                return false;
            }
        }
        timer.applyTimer(player);
        player.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_HQ.WARPING").replaceAll("%team%", team.getName())));
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }
}
