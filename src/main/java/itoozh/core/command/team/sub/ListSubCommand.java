package itoozh.core.command.team.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.session.Session;
import itoozh.core.session.settings.TeamListSettings;
import itoozh.core.team.Team;
import itoozh.core.util.Formatter;
import itoozh.core.util.LanguageUtils;

import java.util.*;

public class ListSubCommand extends BaseSubCommand {

    public ListSubCommand(String name) {
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
        Session session = Main.getInstance().getSessionManager().getSessionByUUID(player.getUniqueId());
        Map<Integer, List<Team>> map = new HashMap<>();

        List<Team> teams = new ArrayList<>(Main.getInstance().getTeamManager().getTeams().values());

        TeamListSettings setting = session.getTeamListSettings();

        switch (setting) {
            case ONLINE_HIGH:
                teams.sort(Comparator.comparingInt(Team::getOnlinesSize).reversed());
                break;
            case ONLINE_LOW:
                teams.sort(Comparator.comparingInt(Team::getOnlinesSize));
                break;
            case HIGHEST_DTR:
                teams.sort(Comparator.comparingDouble(Team::getDtr).reversed());
                break;
            case LOWEST_DTR:
                teams.sort(Comparator.comparingDouble(Team::getDtr));
        }

        int pos = 1;
        if (teams.isEmpty()) {
            sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_LIST.NO_TEAMS_ONLINE")));
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
                sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_LIST.PAGE_NOT_FOUND").replaceAll("%number%", args[1])));
                return false;
            }
            pos = j;
        }
        List<String> lines = LanguageUtils.splitStringToList(LanguageUtils.getString("TEAM_COMMAND.TEAM_LIST.LIST_SHOWN"));
        for (String s : lines) {
            if (!s.equals("%team_list%")) {
                sender.sendMessage(TextFormat.colorize(s.replaceAll("%page%", String.valueOf(pos)).replaceAll("%max-pages%", String.valueOf(i))));
            } else {
                List<Team> tops = map.get(pos);
                for (int j = 0; j < tops.size(); ++j) {
                    Team topTeam = tops.get(j);
                    int fList = (pos - 1) * 10 + j;
                    String format;
                    if (setting.name().contains("DTR")) {
                        format = LanguageUtils.getString("TEAM_COMMAND.TEAM_LIST.FORMAT_DTR")
                                .replaceAll("%team%", topTeam.getDisplayName((Player) sender))
                                .replaceAll("%online%", String.valueOf(topTeam.getOnlinePlayers().size()))
                                .replaceAll("%number%", String.valueOf(fList + 1))
                                .replaceAll("%dtr%", topTeam.getDtrColor() + topTeam.getDtrString())
                                .replaceAll("%max-dtr%", String.valueOf(Formatter.formatDtr(topTeam.getMaxDtr())));
                    } else {
                        format = LanguageUtils.getString("TEAM_COMMAND.TEAM_LIST.FORMAT_ONLINE")
                                .replaceAll("%team%", topTeam.getDisplayName((Player) sender))
                                .replaceAll("%online%", String.valueOf(topTeam.getOnlinePlayers().size()))
                                .replaceAll("%number%", String.valueOf(fList + 1))
                                .replaceAll("%max-online%", String.valueOf(topTeam.getMembers().size()));
                    }
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
