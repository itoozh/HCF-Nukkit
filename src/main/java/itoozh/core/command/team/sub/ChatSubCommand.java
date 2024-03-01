package itoozh.core.command.team.sub;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import com.google.common.collect.ImmutableList;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.session.Session;
import itoozh.core.session.settings.ChatSettings;
import itoozh.core.team.Team;
import itoozh.core.team.player.Role;
import itoozh.core.util.LanguageUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class ChatSubCommand extends BaseSubCommand {

    private final List<ChatSettings> chatSettings;

    public static final CommandEnum ENUM_GAMEMODE = new CommandEnum("chatType",
            ImmutableList.of("p", "public", "faction", "f", "t", "team", "co_leader", "captain"));

    public ChatSubCommand(String name) {
        super(name);
        this.chatSettings = new ArrayList<>(Arrays.asList(ChatSettings.values()));
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public String[] getAliases() {
        return new String[] {"c"};
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        Session session = Main.getInstance().getSessionManager().getSession(player);
        Team team = Main.getInstance().getSessionManager().getSession(player).getTeam();

        if (args.length == 1) {
            if (team != null) {
                if (!team.checkRole(player, Role.CO_LEADER)) {
                    this.chatSettings.remove(ChatSettings.CO_LEADER);
                }
                if (!team.checkRole(player, Role.CAPTAIN)) {
                    this.chatSettings.remove(ChatSettings.CAPTAIN);
                }
                session.setChatSettings(this.getSetting(session.getChatSettings()));
                sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CHAT.CHAT_CHANGED").replaceAll("%chat%", session.getChatSettings().name().toLowerCase()))));
                return true;
            }
            sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.NOT_IN_TEAM"))));
            return false;
        } else {
            if (args.length < 2) {
                sender.sendMessage(TextFormat.colorize(Main.prefix + "&cUsage: /" + label + " " + args[0] + " <chat>"));
                return false;
            }

            if (team == null) {
                sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.NOT_IN_TEAM"))));
                return false;
            }

            switch (args[1].toLowerCase()) {
                case "p":
                case "global":
                case "public":
                    session.setChatSettings(ChatSettings.PUBLIC);
                    sender.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CHAT.CHAT_CHANGED").replaceAll("%chat%", ChatSettings.PUBLIC.name().toLowerCase())));
                    return true;
                case "f":
                case "faction":
                case "t":
                case "team":
                    session.setChatSettings(ChatSettings.TEAM);
                    sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CHAT.CHAT_CHANGED").replaceAll("%chat%", ChatSettings.TEAM.name().toLowerCase()))));
                    return true;
                case "co_leader":
                    if (!team.checkRole(player, Role.CO_LEADER)) {
                        sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.INSUFFICIENT_ROLE").replaceAll("%role%", Role.CO_LEADER.getName()))));
                        return false;
                    }
                    session.setChatSettings(ChatSettings.CO_LEADER);
                    sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CHAT.CHAT_CHANGED").replaceAll("%chat%", ChatSettings.CO_LEADER.name().toLowerCase()))));
                    return true;
                case "captain":
                    if (!team.checkRole(player, Role.CAPTAIN)) {
                        sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.INSUFFICIENT_ROLE").replaceAll("%role%", Role.CAPTAIN.getName()))));
                        return false;
                    }
                    session.setChatSettings(ChatSettings.CAPTAIN);
                    sender.sendMessage(TextFormat.colorize(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CHAT.CHAT_CHANGED").replaceAll("%chat%", ChatSettings.CAPTAIN.name().toLowerCase()))));
                    return true;
            }

            return true;
        }
    }

    private ChatSettings getSetting(ChatSettings setting) {
        int i = this.chatSettings.indexOf(setting);
        if (i == this.chatSettings.size() - 1) {
            return this.chatSettings.get(0);
        }
        return this.chatSettings.get(i + 1);
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] { CommandParameter.newEnum("chat", ENUM_GAMEMODE)};
    }
}
