package itoozh.core.command.team.sub.co_leader;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.session.Session;
import itoozh.core.team.Team;
import itoozh.core.team.player.Member;
import itoozh.core.team.player.Role;
import itoozh.core.util.LanguageUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PromoteSubCommand extends BaseSubCommand {

    private final List<Role> roles;

    public PromoteSubCommand(String name) {
        super(name);
        this.roles = new ArrayList<>(Arrays.asList(Role.values()));
        this.roles.remove(Role.LEADER);
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
            sender.sendMessage(TextFormat.colorize(Main.prefix + "&cUsage: /" + label + " " + args[0] + " <player>"));
            return false;
        }

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

        IPlayer targetPlayer = Main.getInstance().getServer().getOfflinePlayer(args[1]);
        if (targetPlayer.getUniqueId() == null) {
            sender.sendMessage(TextFormat.colorize(Main.prefix + "&cPlayer not found."));
            return false;
        }

        Session targetSession = Main.getInstance().getSessionManager().getSessionByUUID(targetPlayer.getUniqueId());
        if (targetSession == null) {
            sender.sendMessage(TextFormat.colorize(Main.prefix + "&cPlayer not found."));
            return false;
        }

        if (!team.getMembers().contains(targetSession.getMember())) {
            sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_PROMOTE.NOT_IN_TEAM").replaceAll("%player%", targetPlayer.getName()))));
            return false;
        }
        if (player.getUniqueId().equals(targetPlayer.getUniqueId())) {
            sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_PROMOTE.PROMOTE_SELF"))));
            return false;
        }

        Member member = targetSession.getMember();
        Role role = this.getRole(member);
        if (member.getRole() == role) {
            sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_PROMOTE.HIGHEST_ROLE"))));
            return false;
        }
        if (role == Role.CO_LEADER && !team.checkRole(player, Role.LEADER)) {
            sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_PROMOTE.HIGHEST_ROLE"))));
            return false;
        }
        member.setRole(role);
        sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_PROMOTE.PROMOTED_BROADCAST").replaceAll("%player%", targetPlayer.getName()).replaceAll("%role%", member.getRole().getName()))));

        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] { CommandParameter.newType("player", CommandParamType.TARGET)};
    }

    private Role getRole(Member member) {
        if (this.roles.indexOf(member.getRole()) == this.roles.size() - 1 || member.getRole() == Role.LEADER) {
            return member.getRole();
        }
        return this.roles.get(this.roles.indexOf(member.getRole()) + 1);
    }

}
