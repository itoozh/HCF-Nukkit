package itoozh.core.gkit.profile;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import itoozh.core.Main;
import itoozh.core.gkit.GKit;
import itoozh.core.gkit.GKitCooldown;
import itoozh.core.gkit.GKitUses;
import itoozh.core.gkit.profile.listener.ProfileListener;

import java.io.File;
import java.util.*;

public class ProfileManager {
    private final Main plugin;

    private final Map<UUID, Profile> profileMap;

    private Config dataFile;

    public ProfileManager(Main plugin) {
        this.plugin = plugin;
        profileMap = new HashMap<>();
        dataFile = new Config(new File(plugin.getDataFolder(), "data/gkitsProfiles.yml"), Config.YAML);

        plugin.getServer().getPluginManager().registerEvents(new ProfileListener(plugin), plugin);
        this.loadProfiles();
    }

    public Profile getProfile(UUID uuid) {
        if (profileMap.containsKey(uuid))
            return profileMap.get(uuid);

        return profileMap.put(uuid, new Profile(uuid));
    }

    public Profile loadOrCreate(UUID uuid) {
        return profileMap.computeIfAbsent(uuid, k -> new Profile(uuid));
    }

    public Map<UUID, Profile> getProfiles() {
        return profileMap;
    }

    public void saveProfiles() {
        dataFile.setAll(new LinkedHashMap<>());
        for (Profile profile : profileMap.values()) {
            dataFile.set(profile.getUuid().toString() + ".uuid", profile.getUuid());

            for (GKitUses uses : profile.getgKitUsesMap()) {
                dataFile.set(profile.getUuid().toString() + ".uses." + uses.getGKit().getName() + ".amount", uses.getAmount());
            }

            for (GKitCooldown cooldown : profile.getgKitCooldowns()) {
                if (cooldown.getGKit() == null) continue;
                dataFile.set(profile.getUuid().toString() + ".cooldowns." + cooldown.getGKit().getName() + ".remaining", cooldown.getRemaining());
            }

            dataFile.save();
        }
    }

    public void loadProfiles() {

        for (String uuid : dataFile.getKeys(false)) {
            UUID k = UUID.fromString(uuid);

            Set<GKitUses> gKitUses = new HashSet<>();
            ConfigSection usesSection = dataFile.getSection(uuid + ".uses");
            if (usesSection != null) {
                for (String gkitName : usesSection.getKeys(false)) {
                    GKit gKit = Main.getInstance().getGKitManager().getGKit(gkitName);
                    int amount = dataFile.getInt(k + ".uses." + gkitName + ".amount");

                    GKitUses gkitUses = new GKitUses(gKit, amount);
                    gKitUses.add(gkitUses);
                }
            }

            Set<GKitCooldown> gKitCooldowns = new HashSet<>();
            ConfigSection cooldownSection = dataFile.getSection(uuid + ".cooldowns");
            if (cooldownSection != null) {
                for (String gkitName : cooldownSection.getKeys(false)) {
                    GKit gKit = Main.getInstance().getGKitManager().getGKit(gkitName);
                    long remaining = dataFile.getLong(k + ".cooldowns." + gkitName + ".remaining");

                    GKitCooldown gKitCooldown = new GKitCooldown(gKit, remaining);
                    gKitCooldowns.add(gKitCooldown);
                }
            }

            Profile profile = new Profile(k);
            profile.setgKitUsesMap(gKitUses);
            profile.setgKitCooldowns(gKitCooldowns);

            profileMap.put(k, profile);
        }
    }
}
