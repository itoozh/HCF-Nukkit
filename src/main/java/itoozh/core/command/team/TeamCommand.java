package itoozh.core.command.team;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import itoozh.core.command.BaseCommand;
import itoozh.core.command.team.sub.*;
import itoozh.core.command.team.sub.captain.InviteSubCommand;
import itoozh.core.command.team.sub.captain.KickSubCommand;
import itoozh.core.command.team.sub.captain.UnInviteSubCommand;
import itoozh.core.command.team.sub.captain.WithdrawSubCommand;
import itoozh.core.command.team.sub.co_leader.*;
import itoozh.core.command.team.sub.leader.DisbandSubCommand;
import itoozh.core.command.team.sub.leader.LeaderSubCommand;
import itoozh.core.command.team.sub.leader.RenameSubCommand;
import itoozh.core.util.LanguageUtils;

public class TeamCommand extends BaseCommand {

    public TeamCommand() {
        super("team", "Use this command to manage your team", "", new String[]{"faction", "f", "t"});
        this.addSubCommand(new CreateSubCommand("create"));
        this.addSubCommand(new DisbandSubCommand("disband"));
        this.addSubCommand(new InviteSubCommand("invite"));
        this.addSubCommand(new JoinSubCommand("join"));
        this.addSubCommand(new UnInviteSubCommand("uninvite"));
        this.addSubCommand(new PromoteSubCommand("promote"));
        this.addSubCommand(new DemoteSubCommand("demote"));
        this.addSubCommand(new InfoSubCommand("info"));
        this.addSubCommand(new LeaderSubCommand("leader"));
        this.addSubCommand(new LeaveSubCommand("leave"));
        this.addSubCommand(new KickSubCommand("kick"));
        this.addSubCommand(new ChatSubCommand("chat"));
        this.addSubCommand(new DepositSubCommand("deposit"));
        this.addSubCommand(new ClaimSubCommand("claim"));
        this.addSubCommand(new UnClaimSubCommand("unclaim"));
        this.addSubCommand(new MapSubCommand("map"));
        this.addSubCommand(new FocusSubCommand("focus"));
        this.addSubCommand(new TopSubCommand("top"));
        this.addSubCommand(new ListSubCommand("list"));
        this.addSubCommand(new SortSubCommand("sort"));
        this.addSubCommand(new WithdrawSubCommand("withdraw"));
        this.addSubCommand(new SetHQSubCommand("sethq"));
        this.addSubCommand(new UnFocusSubCommand("unfocus"));
        this.addSubCommand(new RenameSubCommand("rename"));
        this.addSubCommand(new StuckSubCommand("stuck"));
        this.addSubCommand(new RallySubCommand("rally"));
        this.addSubCommand(new UnRallySubCommand("unrally"));
        this.addSubCommand(new HQSubCommand("hq"));
        this.addSubCommand(new LockSubCommand("lockclaim"));
        this.setPermission("core.team");
    }
    @Override
    public void sendUsageMessage(CommandSender sender) {
        sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.USAGE")));
    }
}
