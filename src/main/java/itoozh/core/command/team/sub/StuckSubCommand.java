package itoozh.core.command.team.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Level;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.session.timer.StuckTimer;
import itoozh.core.team.claim.Claim;
import itoozh.core.team.claim.ClaimType;
import itoozh.core.util.LanguageUtils;

public class StuckSubCommand extends BaseSubCommand {

    public StuckSubCommand(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return true;
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        StuckTimer timer = Main.getInstance().getTimerManager().getStuckTimer();
        if (player.getLevel().getDimension() != Level.DIMENSION_OVERWORLD) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_STUCK.CANNOT_STUCK")));
            return false;
        }
        Claim claim = Main.getInstance().getTeamManager().getClaimManager().findClaim(player.getFloorX(), player.getFloorZ(), 0);

        if (claim == null || claim.getType() != ClaimType.TEAM) {
            sender.sendMessage(TextFormat.colorize("&eYou can only use this in the team claims!"));
            return false;
        }

        if (timer.hasTimer(player)) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_STUCK.ALREADY_STUCKING")));
            return false;
        }

        if (!Main.getInstance().getConfig().getBoolean("COMBAT_TIMER.STUCK_TELEPORT") && Main.getInstance().getTimerManager().getCombatTimer().hasTimer(player)) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_STUCK.COMBAT_TAGGED")));
            return false;
        }

        sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_STUCK.STARTED_STUCK").replaceAll("%seconds%", String.valueOf(timer.getSeconds())).replaceAll("%blocks%", String.valueOf(timer.getMaxMoveBlocks()))));
        timer.applyTimer(player);
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }

}
