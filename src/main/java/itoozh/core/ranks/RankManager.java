package itoozh.core.ranks;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RankManager {
    private static Map<String, Rank> ranksMap;
    private static Config dataFile;
    private static Rank defaultRank;

    public RankManager(Main plugin){
        dataFile = new Config(new File(plugin.getDataFolder(), "ranks.yml"), Config.YAML);
        ranksMap = new ConcurrentHashMap<>();
        load();
    }

    public Rank getDefaultRank() {
        return defaultRank;
    }

    public void load(){
        Set<String> config = dataFile.getKeys(false);
        for (String rankName : config) {
            boolean IsDefaultRank = dataFile.getBoolean(rankName + ".default", false);
            String prefix = dataFile.getString(rankName + ".prefix");
            String suffix = dataFile.getString(rankName + ".suffix");
            String color = dataFile.getString(rankName + ".color");
            List<String> perms = dataFile.getStringList(rankName + ".permissions");
            Rank rank = new Rank(rankName, color, prefix, suffix, perms);
            ranksMap.put(rankName, rank);
            if (IsDefaultRank){
                defaultRank = rank;
            }
        }
        Main.getInstance().getLogger().info(TextFormat.GREEN + "Loaded " + config.size() + " ranks.");
    }

    public void save(){
        dataFile.setAll(new LinkedHashMap<>());

        for (Map.Entry<String, Rank> entry : ranksMap.entrySet()) {
            String name = entry.getKey();
            Rank rank = entry.getValue();

            if (Objects.equals(name, defaultRank.getName())) {
                dataFile.set(name + ".default", true);
            }

            dataFile.set(name + ".prefix", rank.getPrefix());
            dataFile.set(name + ".suffix", rank.getSuffix());
            dataFile.set(name + ".color", rank.getColor());
            dataFile.set(name + ".permissions", rank.getPermissions());
        }
        dataFile.save();
    }

    public void addRank(Rank rank){
        ranksMap.put(rank.getName(), rank);
    }

    public Rank getRank(String rankName){
        return ranksMap.get(rankName);
    }

    public Map<String, Rank> getRanks(){
        return ranksMap;
    }
}
