package itoozh.core.timer;

import cn.nukkit.utils.Config;
import itoozh.core.Main;
import itoozh.core.session.timer.*;
import itoozh.core.timer.server.SOTWTimer;
import itoozh.core.timer.type.PlayerTimer;
import lombok.Getter;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class TimerManager {

    private final Config dataFile;

    private final Map<String, PlayerTimer> playerTimers;
    private final EnderPearlTimer enderpearlTimer;
    private final CombatTimer combatTimer;
    private final HQTimer hqTimer;
    private final StuckTimer stuckTimer;
    private final PvPTimer pvPTimer;
    private final InvincibilityTimer invincibilityTimer;
    private final WarmupTimer warmupTimer;
    private final ArcherTagTimer archerTagTimer;
    private final AppleTimer appleTimer;
    private final GappleTimer gappleTimer;
    private final SOTWTimer sotwTimer;

    public TimerManager(Main plugin) {
        this.playerTimers = new LinkedHashMap<>();

        this.enderpearlTimer = new EnderPearlTimer(this);
        this.combatTimer = new CombatTimer(this);
        this.hqTimer = new HQTimer(this);
        this.stuckTimer = new StuckTimer(this);
        this.pvPTimer = new PvPTimer(this);
        this.invincibilityTimer = new InvincibilityTimer(this);
        this.warmupTimer = new WarmupTimer(this);
        this.archerTagTimer = new ArcherTagTimer(this);
        this.appleTimer = new AppleTimer(this);
        this.gappleTimer = new GappleTimer(this);
        this.sotwTimer = new SOTWTimer();


        plugin.getServer().getPluginManager().registerEvents(this.enderpearlTimer, plugin);
        plugin.getServer().getPluginManager().registerEvents(this.combatTimer, plugin);
        plugin.getServer().getPluginManager().registerEvents(this.hqTimer, plugin);
        plugin.getServer().getPluginManager().registerEvents(this.stuckTimer, plugin);
        plugin.getServer().getPluginManager().registerEvents(this.pvPTimer, plugin);
        plugin.getServer().getPluginManager().registerEvents(this.invincibilityTimer, plugin);
        plugin.getServer().getPluginManager().registerEvents(this.appleTimer, plugin);
        plugin.getServer().getPluginManager().registerEvents(this.gappleTimer, plugin);
        plugin.getServer().getPluginManager().registerEvents(this.sotwTimer, plugin);

        dataFile = new Config(new File(plugin.getDataFolder(), "data/timers.yml"), Config.YAML);

        load();
    }

    public PlayerTimer getPlayerTimer(String name) {
        return this.playerTimers.get(name);
    }

    public void load() {
        for (PlayerTimer timer : getPlayerTimers().values()) {
            String normalKey = (timer.isPausable() ? "Normal:" : "") + timer.getName();
            String pausedKey = "Paused:" + timer.getName();

            if (dataFile.exists(normalKey)) {
                Map<String, Object> normal = dataFile.getSection(normalKey);
                timer.getTimerCache().putAll(normal.entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> UUID.fromString(entry.getKey()),
                                entry -> Long.parseLong(entry.getValue().toString()))
                        ));
            }

            if (dataFile.exists(pausedKey)) {
                Map<String, Object> paused = dataFile.getSection(pausedKey);
                timer.getPausedCache().putAll(paused.entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> UUID.fromString(entry.getKey()),
                                entry -> Long.parseLong(entry.getValue().toString()))
                        ));
            }
        }

        String sotwKey = "SOTW_ENABLED";
        String sotwKeyII = "SOTW";

        if (dataFile.exists(sotwKey)) {
            List<String> sotw = dataFile.getStringList(sotwKey);
            if (!sotw.isEmpty()) {
                getSotwTimer().getEnabled().addAll(sotw.stream().map(UUID::fromString).collect(Collectors.toList()));
            }
        }

        if (dataFile.exists(sotwKeyII)) {
            long sotwTime = Long.parseLong(dataFile.getString(sotwKeyII));
            if (sotwTime > 0L) {
                SOTWTimer timerS = getSotwTimer();
                timerS.setActive(true);
                timerS.setRemaining(sotwTime);
            }
        }

    }

    public void save() {
        Map<String, Object> map = dataFile.getAll();
        map.clear();
        for (PlayerTimer timer : getPlayerTimers().values()) {
            Map<String, String> timerMap = timer.getTimerCache().entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> entry.getKey().toString(),
                            entry -> entry.getValue().toString())
                    );

            if (timer.isPausable()) {
                Map<String, String> pausedTimerMap = timer.getPausedCache().entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> entry.getKey().toString(),
                                entry -> entry.getValue().toString())
                        );

                map.put("Paused:" + timer.getName(), pausedTimerMap);
            }

            map.put((timer.isPausable() ? "Normal:" : "") + timer.getName(), timerMap);
        }

        map.put("SOTW", String.valueOf(Main.getInstance().getTimerManager().getSotwTimer().getRemaining()));
        map.put("SOTW_ENABLED", Main.getInstance().getTimerManager().getSotwTimer().getEnabled().stream().map(UUID::toString).collect(Collectors.toList()));
        dataFile.setAll((LinkedHashMap<String, Object>) map);
        dataFile.save();
    }
}
