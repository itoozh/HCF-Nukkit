package itoozh.core.command.team.sub.co_leader;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.DimensionEnum;
import cn.nukkit.level.Level;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.session.Session;
import itoozh.core.session.procces.type.ClaimProcess;
import itoozh.core.team.Team;
import itoozh.core.team.claim.ClaimType;
import itoozh.core.team.player.Role;
import itoozh.core.util.Cooldown;
import itoozh.core.util.LanguageUtils;


public class ClaimSubCommand extends BaseSubCommand {

    public ClaimSubCommand(String name) {
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
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.NOT_IN_TEAM")));
            return false;
        }

        if (player.getLevel().getDimension() != Level.DIMENSION_OVERWORLD) {
            sender.sendMessage(TextFormat.colorize("&eYou can only use this in the overworld!"));
            return false;
        }

        if (!team.checkRole(player, Role.CO_LEADER)) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.INSUFFICIENT_ROLE").replaceAll("%role%", Role.CO_LEADER.getName())));
            return false;
        }

        if (team.getClaim() != null) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CLAIM.MAX_CLAIMS")));
            return false;
        }

        if (player.getInventory().isFull()) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CLAIM.INVENTORY_FULL")));
            return false;
        }

        if (playerSession.getProcess() != null) {
            sender.sendMessage(TextFormat.colorize("&cYou are already in a claim process!"));
            return false;
        }

        playerSession.setProcess(new ClaimProcess(60, player, ClaimType.TEAM, team.getName()));
        player.getInventory().addItem(Main.getInstance().getTeamManager().getClaimManager().item);
        player.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CLAIM.START_SELECTION")));
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }


}
