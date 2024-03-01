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
import itoozh.core.team.claim.Claim;
import itoozh.core.team.claim.ClaimType;
import itoozh.core.util.LanguageUtils;

public class RemoveSubCommand extends BaseSubCommand {
    public RemoveSubCommand(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public String[] getAliases() {
        return new String[] {"delete", "unclaim"};
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {

        if (args.length < 2) {
            sender.sendMessage(TextFormat.colorize("&cUsage /f remove <name>"));
            return false;
        }

        Claim claim = Main.getInstance().getTeamManager().getClaimManager().getClaim(args[1]);
        if (claim == null) {
            sender.sendMessage(TextFormat.colorize("&cThis claim does not exist!"));
            return false;
        }

        if (claim.getType() == ClaimType.TEAM) {
            Team team = Main.getInstance().getTeamManager().getTeam(claim.getName());
            if (team != null) {
                team.setClaim(null);
            }
        }
        Main.getInstance().getTeamManager().getClaimManager().deleteClaim(claim);
        sender.sendMessage(TextFormat.colorize("&aClaim " + claim.getName() + " has been removed!"));
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] {CommandParameter.newType("name", CommandParamType.STRING)};
    }
}
