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
import itoozh.core.team.claim.Claim;
import itoozh.core.team.claim.ClaimType;
import itoozh.core.util.LanguageUtils;

import java.util.Map;

public class ListSubCommand extends BaseSubCommand {
    public ListSubCommand(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return true;
    }

    @Override
    public String[] getAliases() {
        return new String[] {"claims"};
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {

        Map<String, Claim> claims = Main.getInstance().getTeamManager().getClaimManager().getClaims();

        if (claims.isEmpty()) {
            sender.sendMessage(TextFormat.colorize("&cNo claims found! Use /claim create"));
            return false;
        }

        sender.sendMessage(TextFormat.colorize("&7-----------------------\n&aClaims List:"));
        int i = 1;
        for (Claim claim : claims.values()) {
            if (claim.getType() == ClaimType.TEAM) continue;

            String claimName = sender instanceof Player ? claim.getNameFormat((Player) sender) : claim.getName();
            sender.sendMessage(TextFormat.colorize("  &d" + i + "&f. &e" + claimName + " &f(&e" + claim.getX1() + "&f, &e" + claim.getZ1() + "&f)"));
            i++;
        }
        sender.sendMessage(TextFormat.colorize("&7-----------------------"));

        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }
}
