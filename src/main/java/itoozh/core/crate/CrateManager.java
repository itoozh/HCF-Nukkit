package itoozh.core.crate;

import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.crate.effect.CrateEffect;
import itoozh.core.util.ItemUtil;
import itoozh.core.util.TaskUtils;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CrateManager {

    private static final Map<String, Crate> crates = new HashMap<>();
    private static final Map<Location, Crate> placedCrates = new HashMap<>();

    private final Config dataFilePlaced;

    private final Config dataFile;

    public CrateManager(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(new CrateHandler(), plugin);
        dataFile = new Config(new File(plugin.getDataFolder(), "data/crates.yml"), Config.YAML);
        dataFilePlaced = new Config(new File(plugin.getDataFolder(), "data/cratesPlaced.yml"), Config.YAML);
        loadCrates();
        TaskUtils.executeScheduledAsync(plugin, 20, this::tick);
    }

    public Crate getCrate(String name) {
        return crates.get(name);
    }

    public Crate getCrate(Location location) {
        return placedCrates.get(location);
    }

    public void addCrate(Crate crate) {
        crates.put(crate.getName(), crate);
    }

    public void removeCrate(Crate crate) {
        for (Map.Entry<Location, Crate> entry : placedCrates.entrySet()) {
            if (entry.getValue().equals(crate)) {
                if (entry.getKey() != null) {
                    placedCrates.remove(entry.getKey());
                }
                break;
            }
        }
        crates.remove(crate.getName());
    }

    public void removeCrate(Location location) {
        placedCrates.remove(location);
    }

    public void placeCrate(Location location, Crate crate) {
        Main.getInstance().getHologramManager().makeHologram(crate.getHologramText(), location, crate.getName());
        placedCrates.put(location, crate);
    }

    public Map<String, Crate> getCrates() {
        return crates;
    }

    public void savePlacedCrates() {
        dataFilePlaced.setAll(new LinkedHashMap<>());
        for (Map.Entry<Location, Crate> entry : placedCrates.entrySet()) {
            Location location = entry.getKey();
            String locationString = location.getFloorX() + "," + location.getFloorY() + "," + location.getFloorZ() + "," + location.getLevel().getName();
            dataFilePlaced.set(locationString, entry.getValue().getName());
        }

        dataFilePlaced.save();
    }

    public void loadPlacedCrates() {
        for (String locationString : dataFilePlaced.getKeys(false)) {
            String[] parts = locationString.split(",");
            if (parts.length == 4) {
                double x = Double.parseDouble(parts[0]);
                double y = Double.parseDouble(parts[1]);
                double z = Double.parseDouble(parts[2]);
                String levelName = parts[3];
                Location location = new Location(x, y, z, Main.getInstance().getServer().getLevelByName(levelName));
                String crateName = dataFilePlaced.getString(locationString);
                Crate crate = getCrate(crateName);
                if (crate != null) {
                    placeCrate(location, crate);
                }
            }
        }
        Main.getInstance().getLogger().info(TextFormat.GREEN + "Loaded " + placedCrates.size() + " placed crates.");
    }

    public void saveCrates() {
        savePlacedCrates();
        dataFile.setAll(new LinkedHashMap<>());

        for (Map.Entry<String, Crate> entry : crates.entrySet()) {
            String crateName = entry.getKey();
            Crate crate = entry.getValue();

            dataFile.set(crateName + ".color", crate.getColor());
            dataFile.set(crateName + ".rewardAmount", crate.getRewardAmount());
            dataFile.set(crateName + ".itemKey", new ItemUtil().itemToString(crate.getItemKey()));
            dataFile.set(crateName + ".rewards", new ItemUtil().inventoryToString(crate.getRewards()));
            dataFile.set(crateName + ".hologramText", crate.getHologramText());
            dataFile.set(crateName + ".effect", crate.getEffect().name());
        }

        dataFile.save();
    }

    public void loadCrates() {
        for (String crateName : dataFile.getKeys(false)) {
            String color = dataFile.getString(crateName + ".color");
            int rewardAmount = dataFile.getInt(crateName + ".rewardAmount");
            Item itemKey = new ItemUtil().itemFromStringI(dataFile.getString(crateName + ".itemKey"));
            Map<Integer, Item> rewards = new ItemUtil().inventoryFromString(dataFile.getString(crateName + ".rewards"));
            List<String> hologramText = dataFile.getStringList(crateName + ".hologramText");
            CrateEffect effect = CrateEffect.valueOf(dataFile.getString(crateName + ".effect", CrateEffect.HEARTH_EFFECT.name()));

            Crate crate = new Crate(crateName, color);
            crate.setRewardAmount(rewardAmount);
            crate.setItemKey(itemKey);
            crate.setRewards(rewards);
            crate.setHologramText(hologramText);
            crate.setEffect(effect);

            addCrate(crate);
        }
        Main.getInstance().getLogger().info(TextFormat.GREEN + "Loaded " + crates.size() + " crates.");
        loadPlacedCrates();
    }

    public void updateCrateHolograms(String crateName) {
        for (Map.Entry<Location, Crate> entry : placedCrates.entrySet()) {
            Location location = entry.getKey();
            Crate crate = entry.getValue();

            if (crate.getName().equalsIgnoreCase(crateName)) {
                Main.getInstance().getHologramManager().removeHologram(crate.getName());
                Main.getInstance().getHologramManager().makeHologram(crate.getHologramText(), location, crate.getName());
            }
        }
    }

    public Map<Location, Crate> getPlacedCrates() {
        return placedCrates;
    }

    private void tick() {
        for (Map.Entry<Location, Crate> crate : getPlacedCrates().entrySet()) {
            if (crate.getValue().getEffect() == null)
                continue;

            crate.getValue().getEffect().tick(crate.getKey().add(0.5, 1, 0.5));
        }
    }

}
