package itoozh.core.command.team.sub;

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

import java.util.*;

public class TopSubCommand extends BaseSubCommand {

    public TopSubCommand(String name) {
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
        Map<Integer, List<Team>> map = new HashMap<>();

        List<Team> teams = new ArrayList<>(Main.getInstance().getTeamManager().getTeams().values());
        teams.sort(Comparator.comparingInt(Team::getPoints).reversed());

        int pos = 1;
        if (teams.isEmpty()) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_TOP.NO_TEAMS_ONLINE")));
            return false;
        }
        int i = 0;
        for (int f = 0; f < teams.size(); ++f) {
            if (f % 10 == 0) {
                ++i;
            }
            Team targetTeam = teams.get(f);
            if (!map.containsKey(i)) {
                map.put(i, new ArrayList<>());
            }
            map.get(i).add(targetTeam);
        }
        if (args.length > 1) {
            Integer j = this.getInt(args[1]);
            if (j == null) {
                sender.sendMessage(TextFormat.colorize("&cNo valid number!"));
                return false;
            }
            if (!map.containsKey(j)) {
                sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_TOP.PAGE_NOT_FOUND").replaceAll("%number%", args[1])));
                return false;
            }
            pos = j;
        }
        List<String> lines = LanguageUtils.splitStringToList(LanguageUtils.getString("TEAM_COMMAND.TEAM_TOP.TOP_SHOWN"));
        for (String s : lines) {
            if (!s.equals("%team_top%")) {
                sender.sendMessage(TextFormat.colorize(s.replaceAll("%page%", String.valueOf(pos)).replaceAll("%max-pages%", String.valueOf(i))));
            } else {
                List<Team> tops = map.get(pos);
                for (int j = 0; j < tops.size(); ++j) {
                    Team topTeam = tops.get(j);
                    int fTop = (pos - 1) * 10 + j;
                    String format = LanguageUtils.getString("TEAM_COMMAND.TEAM_TOP.FORMAT_TEAM").replaceAll("%team%", (sender instanceof Player) ? topTeam.getDisplayName((Player) sender) : topTeam.getName()).replaceAll("%points%", String.valueOf(topTeam.getPoints())).replaceAll("%number%", String.valueOf(fTop + 1));
                    sender.sendMessage(TextFormat.colorize(format));
                }
            }
        }
        map.clear();
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] { CommandParameter.newType("page", CommandParamType.INT) };
    }

    public Integer getInt(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
