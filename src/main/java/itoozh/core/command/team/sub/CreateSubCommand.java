package itoozh.core.command.team.sub;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.session.Session;
import itoozh.core.util.Cooldown;
import itoozh.core.util.LanguageUtils;

import java.util.regex.Pattern;

public class CreateSubCommand extends BaseSubCommand {
    private final Cooldown createCooldown;

    public CreateSubCommand(String name) {
        super(name);
        this.createCooldown = new Cooldown(Main.getInstance());
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
        String factionName = args[1];

        Player player = (Player) sender;

        Session playerSession = Main.getInstance().getSessionManager().getSession(player);

        if (playerSession == null) {
            playerSession = Main.getInstance().getSessionManager().createSession(player);
        }

        if (playerSession.getTeam() != null) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.ALREADY_IN_TEAM")));
            return false;
        }

        if (Main.getInstance().getTeamManager().getTeam(factionName) != null) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_ALREADY_EXISTS").replaceAll("%team%", factionName)));
            return false;
        }

        if (isNotAlphanumeric(factionName)) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CREATE.NOT_ALPHANUMERICAL")));
            return false;
        }
        if (factionName.length() < Main.getInstance().getConfig().getInt("TEAM_NAME.MIN_LENGTH")) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CREATE.MIN_LENGTH").replaceAll("%amount%", String.valueOf(Main.getInstance().getConfig().getInt("TEAM_NAME.MIN_LENGTH")))));
            return false;
        }
        if (factionName.length() > Main.getInstance().getConfig().getInt("TEAM_NAME.MAX_LENGTH")) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CREATE.MAX_LENGTH").replaceAll("%amount%", String.valueOf(Main.getInstance().getConfig().getInt("TEAM_NAME.MAX_LENGTH")))));
            return false;
        }
        if (this.createCooldown.hasCooldown(player)) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CREATE.CREATE_COOLDOWN").replaceAll("%seconds%", this.createCooldown.getRemaining(player))));
            return false;
        }

        Main.getInstance().getTeamManager().createTeam(factionName, player);
        sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CREATE.CREATED")));
        Server.getInstance().broadcastMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CREATE.CREATED_BROADCAST")).replaceAll("%team%", factionName).replaceAll("%player%", player.getName()));
        this.createCooldown.applyCooldown(player, Main.getInstance().getConfig().getInt("TIMERS_COOLDOWN.TEAM_CREATE_CD"));
        Main.getInstance().getNameTags().update();
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] { CommandParameter.newType("name", CommandParamType.STRING)};
    }

    public static boolean isNotAlphanumeric(String input) {
        return Pattern.compile("[^a-zA-Z0-9]").matcher(input).find();
    }
}
