package itoozh.core.command.claim.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import com.google.common.collect.ImmutableList;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.session.Session;
import itoozh.core.session.procces.type.ClaimProcess;
import itoozh.core.team.Team;
import itoozh.core.team.claim.ClaimType;
import itoozh.core.team.player.Role;
import itoozh.core.util.LanguageUtils;

public class CreateSubCommand extends BaseSubCommand {

    public static final CommandEnum ENUM_CLAIM_TYPE = new CommandEnum("claimTypes",
            ImmutableList.of("spawn", "road", "warzone", "koth"));
    public CreateSubCommand(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public String[] getAliases() {
        return new String[] {"make"};
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {

        if (args.length < 3) {
            sender.sendMessage(TextFormat.colorize("&cUsage /f claim <name> <type>"));
            return false;
        }

        Player player = (Player) sender;

        Session playerSession = Main.getInstance().getSessionManager().getSession(player);

        if (playerSession == null) {
            playerSession = Main.getInstance().getSessionManager().createSession(player);
        }

        if (Main.getInstance().getTeamManager().getClaimManager().getClaim(args[1]) != null) {
            sender.sendMessage(TextFormat.colorize("&cThis claim already exists! use /claim remove <name>"));
            return false;
        }

        if (playerSession.getProcess() != null) {
            sender.sendMessage(TextFormat.colorize("&cYou are already in a claim process!"));
            return false;
        }

        try {
            playerSession.setProcess(new ClaimProcess(60, player, ClaimType.valueOf(args[2].toUpperCase()), args[1]));
            player.getInventory().addItem(Main.getInstance().getTeamManager().getClaimManager().item);
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CLAIM.START_SELECTION")));
            return true;
        } catch (IllegalArgumentException e) {
            player.sendMessage(TextFormat.colorize("&cThe type is not valid! Types valuables: KOTH, SPAWN, ROAD, WARZONE"));
            return true;
        }
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] {CommandParameter.newType("name", CommandParamType.STRING), CommandParameter.newEnum("claimType", ENUM_CLAIM_TYPE)};
    }
}
