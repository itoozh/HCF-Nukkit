package itoozh.core.command.team.sub.leader;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.session.Session;
import itoozh.core.team.Team;
import itoozh.core.team.claim.Claim;
import itoozh.core.team.player.Role;
import itoozh.core.util.Cooldown;
import itoozh.core.util.LanguageUtils;

import java.util.regex.Pattern;


public class RenameSubCommand extends BaseSubCommand {
    private final Cooldown renameCooldown;

    public RenameSubCommand(String name) {
        super(name);
        this.renameCooldown = new Cooldown(Main.getInstance());
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
            sender.sendMessage(TextFormat.colorize(Main.prefix + "&cUsage: /" + label + " " + args[0] + " <name>"));
            return false;
        }

        Player player = (Player) sender;

        String name = args[1];

        Session playerSession = Main.getInstance().getSessionManager().getSession(player);

        if (playerSession == null) {
            playerSession = Main.getInstance().getSessionManager().createSession(player);
        }

        Team team = playerSession.getTeam();

        if (team == null) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.NOT_IN_TEAM")));
            return false;
        }

        if (!team.checkRole(player, Role.LEADER)) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.INSUFFICIENT_ROLE").replaceAll("%role%", Role.LEADER.getName())));
            return false;
        }

        if (team.getName().equals(name)) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_RENAME.ALREADY_NAME").replaceAll("%name%", name)));
            return false;
        }

        if (Main.getInstance().getTeamManager().getTeam(name) != null) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_ALREADY_EXISTS").replaceAll("%team%", name)));
            return false;
        }

        if (isNotAlphanumeric(name)) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CREATE.NOT_ALPHANUMERICAL")));
            return false;
        }
        if (name.length() < Main.getInstance().getConfig().getInt("TEAM_NAME.MIN_LENGTH")) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CREATE.MIN_LENGTH").replaceAll("%amount%", String.valueOf(Main.getInstance().getConfig().getInt("TEAM_NAME.MIN_LENGTH")))));
            return false;
        }
        if (name.length() > Main.getInstance().getConfig().getInt("TEAM_NAME.MAX_LENGTH")) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CREATE.MAX_LENGTH").replaceAll("%amount%", String.valueOf(Main.getInstance().getConfig().getInt("TEAM_NAME.MAX_LENGTH")))));
            return false;
        }
        if (this.renameCooldown.hasCooldown(player)) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CREATE.CREATE_COOLDOWN").replaceAll("%seconds%", this.renameCooldown.getRemaining(player))));
            return false;
        }

        Server.getInstance().broadcastMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_RENAME.RENAMED").replaceAll("%team%", team.getName()).replaceAll("%name%", name)));
        Main.getInstance().getTeamManager().getTeams().remove(team.getName());
        Main.getInstance().getTeamManager().getTeams().put(name, team);
        this.renameCooldown.applyCooldown(player, Main.getInstance().getConfig().getInt("TIMERS_COOLDOWN.TEAM_RENAME_CD"));
        team.setName(name);
        Main.getInstance().getNameTags().update();


        Claim claim = team.getClaim();
        if (claim != null) {
            Main.getInstance().getTeamManager().getClaimManager().getClaims().remove(claim.getName());
            Main.getInstance().getTeamManager().getClaimManager().getClaims().put(name, claim);
            claim.setName(name);
        }

        return true;
    }

    public static boolean isNotAlphanumeric(String input) {
        return Pattern.compile("[^a-zA-Z0-9]").matcher(input).find();
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] { CommandParameter.newType("name", CommandParamType.STRING)};
    }
}
