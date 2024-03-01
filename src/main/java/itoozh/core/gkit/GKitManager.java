package itoozh.core.gkit;

import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.util.ItemUtil;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GKitManager {

    private final Main plugin;

    private final Map<String, GKit> kitMap;
    private Config dataFile;
    public GKitManager(Main plugin) {
        dataFile = new Config(new File(plugin.getDataFolder(), "data/gkits.yml"), Config.YAML);
        this.plugin = plugin;
        kitMap = new HashMap<>();
        this.loadKits();
    }


    public void deleteGkit(GKit gKit) {
        kitMap.remove(gKit.getName());
    }

    public GKit createGkit(String name) {
        GKit gKit = new GKit(name);
        kitMap.put(name, gKit);
        return gKit;
    }

    public GKit getGKit(String name) {
        return kitMap.get(name);
    }

    public boolean doesGKitExist(String name) {
        return getGKit(name) != null;
    }

    public Map<String, GKit> getKitMap() {
        return kitMap;
    }

    public void saveKits() {
        dataFile.setAll(new LinkedHashMap<>());
        for (GKit gKit : kitMap.values()) {
            dataFile.set(gKit.getName() + ".name", gKit.getName());
            dataFile.set(gKit.getName() + ".displayName", gKit.getDisplayName());
            dataFile.set(gKit.getName() + ".slot", gKit.getSlot());
            dataFile.set(gKit.getName() + ".coolDown", gKit.getCoolDown());
            dataFile.set(gKit.getName() + ".icon", new ItemUtil().itemToString(gKit.getpureIcon()));
            dataFile.set(gKit.getName() + ".description", gKit.getDescription());
            dataFile.set(gKit.getName() + ".freeUses", gKit.getFreeUses());

            dataFile.set(gKit.getName() + ".armor", new ItemUtil().itemsToString(gKit.getArmor()));

            dataFile.set(gKit.getName() + ".contents", new ItemUtil().itemsToString(gKit.getContents()));

            dataFile.save();
        }
    }

    public void loadKits() {

        for (String kitName : dataFile.getKeys(false)) {
            String name = dataFile.getString(kitName + ".name");
            String displayName = dataFile.getString(kitName + ".displayName");
            int slot = dataFile.getInt(kitName + ".slot");
            long coolDown = dataFile.getLong(kitName + ".coolDown");
            Item item = new ItemUtil().itemFromStringI(dataFile.getString(kitName + ".icon"));
            List<String> description = dataFile.getStringList(kitName + ".description");
            int freeUses = dataFile.getInt(kitName + ".freeUses");

            Item[] armor = new ItemUtil().itemsFromString(dataFile.getString(kitName + ".armor"));

            Item[] contents = new ItemUtil().itemsFromString(dataFile.getString(kitName + ".contents"));

            GKit gKit = new GKit(kitName);
            gKit.setName(name);
            gKit.setDisplayName(displayName);
            gKit.setSlot(slot);
            gKit.setCoolDown(coolDown);
            gKit.setIcon(item);
            gKit.setDescription(description);
            gKit.setFreeUses(freeUses);
            gKit.setArmor(armor);
            gKit.setContents(contents);

            kitMap.put(kitName, gKit);
        }
        Main.getInstance().getLogger().info(TextFormat.GREEN + "Loaded " + kitMap.size() + " gkits.");
    }


}
