package itoozh.core.session;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.ranks.Rank;
import itoozh.core.session.settings.TeamListSettings;
import itoozh.core.team.Team;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static Map<UUID, Session> sessions;

    private static Config dataFile;



    public SessionManager(Main plugin) {
        sessions = new ConcurrentHashMap<>();

        plugin.saveResource("data/sessions.yml", false);
        dataFile = new Config(new File(plugin.getDataFolder(), "data/sessions.yml"), Config.YAML);
        load();
    }

    public Session createSession(Player player) {
        UUID uuid = player.getUniqueId();
        return sessions.computeIfAbsent(uuid, k -> new Session(uuid));
    }

    public Session getSessionByUUID(UUID uuid) {
        return sessions.get(uuid);
    }

    public Session getSession(Player player) {
        UUID uuid = player.getUniqueId();
        return sessions.get(uuid); // Simply get the session from the map (maybe null)
    }

    public void load() {
        Set<String> config = dataFile.getKeys(false);
        Main.getInstance().getLogger().info(TextFormat.GREEN + "Loaded " + config.size() + " sessions.");
        for (String uuidStr : config) {
            UUID uuid = UUID.fromString(uuidStr);
            String rankName = dataFile.getString(uuidStr + ".rank", Main.getInstance().getRankManager().getDefaultRank().getName());
            int kills = dataFile.getInt(uuidStr + ".kills", 0);
            int deaths = dataFile.getInt(uuidStr + ".deaths", 0);
            int killStreak = dataFile.getInt(uuidStr + ".killStreak", 0);
            int highestKillStreak = dataFile.getInt(uuidStr + ".highestKillStreak", 0);
            int balance = dataFile.getInt(uuidStr + ".balance", 0);
            int diamonds = dataFile.getInt(uuidStr + ".diamonds", 0);
            TeamListSettings listSettings = TeamListSettings.valueOf(dataFile.getString(uuidStr + ".teamListConfig", "ONLINE_HIGH"));
            Team team = null;
            String teamName = dataFile.getString(uuidStr + ".team", "");
            if (teamName != null) {
                team = Main.getInstance().getTeamManager().getTeam(teamName);
            }
            Session session = new Session(uuid);
            Rank rank = Main.getInstance().getRankManager().getRank(rankName);
            if (rank != null) session.setRank(Main.getInstance().getRankManager().getRank(rankName));
            session.setKills(kills);
            session.setTeamListSettings(listSettings);
            session.setDeaths(deaths);
            session.setKillStreak(killStreak);
            session.setHighestKillStreak(highestKillStreak);
            session.setBalance(balance);
            session.setTeam(team);
            session.setDiamonds(diamonds);
            sessions.put(uuid, session);
        }
    }

    public void save() {
        dataFile.setAll(new LinkedHashMap<>());

        for (Map.Entry<UUID, Session> entry : sessions.entrySet()) {
            UUID uuid = entry.getKey();
            Session session = entry.getValue();

            String uuidStr = uuid.toString();
            dataFile.set(uuidStr + ".rank", session.getRank().getName());
            dataFile.set(uuidStr + ".kills", session.getKills());
            dataFile.set(uuidStr + ".deaths", session.getDeaths());
            dataFile.set(uuidStr + ".killStreak", session.getKillStreak());
            dataFile.set(uuidStr + ".highestKillStreak", session.getHighestKillStreak());
            dataFile.set(uuidStr + ".balance", session.getBalance());
            dataFile.set(uuidStr + ".teamListConfig", session.getTeamListSettings().name());
            dataFile.set(uuidStr + ".diamonds", session.getDiamonds());
            String team = null;
            if (session.getTeam() != null) {
                team = session.getTeam().getName();
            }
            dataFile.set(uuidStr + ".team", team);
        }

        dataFile.save();
    }

    public Map<UUID, Session> getSessions() {
        return sessions;
    }

    public Config getConfig() {
        return dataFile;
    }
}
