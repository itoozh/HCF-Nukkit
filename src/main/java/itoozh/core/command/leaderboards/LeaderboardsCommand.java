package itoozh.core.command.leaderboards;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseCommand;
import itoozh.core.session.Session;
import itoozh.core.team.Team;
import me.iwareq.fakeinventories.FakeInventory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LeaderboardsCommand extends BaseCommand {

    public LeaderboardsCommand() {
        super("leaderboards", "Use this command to see the server leaderboards.", "", new String[] {"leaderboard"});
        this.setPermission("core.free");
    }

    @Override
    public void sendUsageMessage(CommandSender sender) {
        if (!(sender instanceof Player)) return;
        sendLeaderboardsMenu((Player) sender);
    }
    public void sendLeaderboardsMenu(Player player) {
        FakeInventory inventory = new FakeInventory(InventoryType.CHEST, TextFormat.colorize(Main.getInstance().getConfig().getString("LEADERBOARDS_COMMAND.TITLE")));

        List<Team> topTeams = new ArrayList<>(Main.getInstance().getTeamManager().getTeams().values());
        topTeams.sort(Comparator.comparingInt(Team::getPoints).reversed());

        List<Session> topKills = new ArrayList<>(Main.getInstance().getSessionManager().getSessions().values());
        topKills.sort(Comparator.comparingInt(Session::getKills).reversed());

        List<Session> topDeaths = new ArrayList<>(Main.getInstance().getSessionManager().getSessions().values());
        topDeaths.sort(Comparator.comparingInt(Session::getDeaths).reversed());

        List<Session> topKDR = new ArrayList<>(Main.getInstance().getSessionManager().getSessions().values());
        topKDR.sort(Comparator.comparingLong(Session::getKDR).reversed());

        List<Session> topKillStreak = new ArrayList<>(Main.getInstance().getSessionManager().getSessions().values());
        topKillStreak.sort(Comparator.comparingInt(Session::getKillStreak).reversed());

        for (String si : Main.getInstance().getConfig().getSection("LEADERBOARDS_COMMAND.ITEMS").getKeys(false)) {
            String ss = "LEADERBOARDS_COMMAND.ITEMS." + si + ".";

            Item builder = Item.fromString(Main.getInstance().getConfig().getString(ss + "MATERIAL"));
            builder.setCustomName(TextFormat.colorize(Main.getInstance().getConfig().getString(ss + "NAME")));
            List<String> lore = Main.getInstance().getConfig().getStringList(ss + "LORE");
            lore.replaceAll(s -> {
                for (int i = 0; i < topTeams.size() && i != 10; ++i) {
                    Team team = topTeams.get(i);
                    s = s.replaceAll("%team_top" + (i + 1) + "%", Main.getInstance().getConfig().getString(ss + "FORMAT").replaceAll("%team%", team.getName()).replaceAll("%points%", String.valueOf(team.getPoints())));
                }
                for (int i = 0; i < topKills.size() && i != 10; ++i) {
                    Session user = topKills.get(i);
                    s = s.replaceAll("%kills_top" + (i + 1) + "%", Main.getInstance().getConfig().getString(ss + "FORMAT").replaceAll("%player%", Server.getInstance().getOfflinePlayer(user.getUUID()).getName()).replaceAll("%kills%", String.valueOf(user.getKills())));
                }
                for (int i = 0; i < topDeaths.size() && i != 10; ++i) {
                    Session user = topDeaths.get(i);
                    s = s.replaceAll("%deaths_top" + (i + 1) + "%", Main.getInstance().getConfig().getString(ss + "FORMAT").replaceAll("%player%", Server.getInstance().getOfflinePlayer(user.getUUID()).getName()).replaceAll("%deaths%", String.valueOf(user.getDeaths())));
                }
                for (int i = 0; i < topKDR.size() && i != 10; ++i) {
                    Session user = topKDR.get(i);
                    s = s.replaceAll("%kdr_top" + (i + 1) + "%", Main.getInstance().getConfig().getString(ss + "FORMAT").replaceAll("%player%", Server.getInstance().getOfflinePlayer(user.getUUID()).getName()).replaceAll("%kdr%", user.getKDRString()));
                }
                for (int i = 0; i < topKillStreak.size() && i != 10; ++i) {
                    Session user = topKillStreak.get(i);
                    s = s.replaceAll("%killstreaks_top" + (i + 1) + "%", Main.getInstance().getConfig().getString(String.valueOf(new StringBuilder().append(ss).append("FORMAT"))).replaceAll("%player%", Server.getInstance().getOfflinePlayer(user.getUUID()).getName()).replaceAll("%killstreak%", String.valueOf(user.getKillStreak())));
                }
                if (s.contains("%deaths_top") || s.contains("%kills_top") || s.contains("%team_top") || s.contains("%killstreaks_top") || s.contains("%kdr_top")) {
                    s = Main.getInstance().getConfig().getString("LEADERBOARDS_COMMAND.NONE_MESSAGE");
                }
                return s;
            });

            builder.setLore(lore
                    .stream()
                    .map(TextFormat::colorize).toArray(String[]::new));

            inventory.setItem(Main.getInstance().getConfig().getInt(ss + "SLOT"), builder);
        }




        inventory.setDefaultItemHandler((item, event) -> {
            event.setCancelled(true);
        });

        player.addWindow(inventory);
    }
}
