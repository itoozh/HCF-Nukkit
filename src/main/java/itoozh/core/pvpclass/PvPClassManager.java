package itoozh.core.pvpclass;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.potion.Effect;
import cn.nukkit.scheduler.TaskHandler;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import itoozh.core.Main;
import itoozh.core.pvpclass.listener.ArmorLegacyListener;
import itoozh.core.pvpclass.listener.PvPClassListener;
import itoozh.core.pvpclass.type.archer.ArcherClass;
import itoozh.core.pvpclass.type.bard.BardClass;
import itoozh.core.pvpclass.type.mage.MageClass;
import itoozh.core.pvpclass.type.miner.MinerClass;
import itoozh.core.pvpclass.type.rogue.RogueClass;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class PvPClassManager {
    private final Table<UUID, Integer, Effect> restores;
    private final Map<String, PvPClass> classes;
    private final Map<UUID, PvPClass> activeClasses;
    private ArcherClass archerClass;
    private BardClass bardClass;
    private MinerClass minerClass;
    private MageClass mageClass;
    private RogueClass rogueClass;
    private final Table<UUID, Integer, TaskHandler> endEffectTasks;

    public PvPClassManager(Main plugin) {
        this.restores = HashBasedTable.create();
        this.endEffectTasks = HashBasedTable.create();
        this.activeClasses = new HashMap<>();
        this.classes = new HashMap<>();
        this.load(plugin);
        plugin.getServer().getPluginManager().registerEvents(new PvPClassListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ArmorLegacyListener(), plugin);
    }

    private void load(Main plugin) {

        if (Main.getInstance().getConfig().getBoolean("BARD_CLASS.ENABLED")) {
            this.bardClass = new BardClass(this);
            plugin.getServer().getPluginManager().registerEvents(this.bardClass, plugin);
        }

        if (Main.getInstance().getConfig().getBoolean("MAGE_CLASS.ENABLED")) {
            this.mageClass = new MageClass(this);
            plugin.getServer().getPluginManager().registerEvents(this.mageClass, plugin);
        }
        if (Main.getInstance().getConfig().getBoolean("ARCHER_CLASS.ENABLED")) {
            this.archerClass = new ArcherClass(this);
            plugin.getServer().getPluginManager().registerEvents(this.archerClass, plugin);
        }

        if (Main.getInstance().getConfig().getBoolean("MINER_CLASS.ENABLED")) {
            this.minerClass = new MinerClass(this);
            plugin.getServer().getPluginManager().registerEvents(this.minerClass, plugin);
        }

        if (Main.getInstance().getConfig().getBoolean("ROGUE_CLASS.ENABLED")) {
            this.rogueClass = new RogueClass(this);
            plugin.getServer().getPluginManager().registerEvents(this.rogueClass, plugin);
        }

    }

    public void checkArmor(Player player) {
        for (PvPClass pvpClass : this.getClasses().values()) {
            pvpClass.checkArmor(player);
        }
    }

    public void addEffect(Player player, Effect effect1) {
        Effect effect = effect1.clone();
        if (!player.getEffects().containsValue(effect)) {
            player.addEffect(effect);
            return;
        }
        for (Effect activeEffect : player.getEffects().values()) {
            if (activeEffect.getId() != effect.getId()) {
                continue;
            }
            if (activeEffect.getAmplifier() > effect.getAmplifier()) {
                break;
            }
            if (activeEffect.getAmplifier() == effect.getAmplifier() && activeEffect.getDuration() > effect.getDuration()) {
                break;
            }
            if (activeEffect.getDuration() > effect.getDuration()) {
                this.restores.put(player.getUniqueId(), activeEffect.getId(), activeEffect.clone());
                player.removeEffect(activeEffect.getId());
            }
            player.addEffect(effect);
            break;
        }
    }

    public void disable() {
        for (Player online : Server.getInstance().getOnlinePlayers().values()) {
            PvPClass pvpClass = this.getActiveClass(online);
            if (pvpClass == null) {
                continue;
            }
            pvpClass.unEquip(online);
        }
    }

    public PvPClass getActiveClass(Player player) {
        return this.activeClasses.get(player.getUniqueId());
    }

}