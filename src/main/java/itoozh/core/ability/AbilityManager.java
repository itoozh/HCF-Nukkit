package itoozh.core.ability;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.ability.listener.AbilityListener;
import itoozh.core.ability.type.*;
import itoozh.core.util.Cooldown;
import lombok.Getter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Getter
public class AbilityManager {
    private Cooldown globalCooldown;
    private Map<String, Ability> abilities;

    public Config getDataFile() {
        return dataFile;
    }

    public static Config dataFile;

    public AbilityManager(Main plugin) {
        this.abilities = new HashMap<>();
        this.globalCooldown = new Cooldown(plugin);

        plugin.saveResource("abilities.yml", true);
        dataFile = new Config(new File(plugin.getDataFolder(), "abilities.yml"), Config.YAML);

        plugin.getServer().getPluginManager().registerEvents(new AbilityListener(), plugin);

        plugin.getServer().getPluginManager().registerEvents(new AntiBuildAbility(this), plugin);
        plugin.getServer().getPluginManager().registerEvents(new FocusModeAbility(this), plugin);
        plugin.getServer().getPluginManager().registerEvents(new NinjaAbility(this), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ComboAbility(this), plugin);
        plugin.getServer().getPluginManager().registerEvents(new SwitcherAbility(this), plugin);

    }

    public Ability getAbility(String ability) {
        return this.abilities.get(ability.toUpperCase());
    }

    public String getStatus(Player player) {
        if (getGlobalCooldown().getRemaining(player).contains("10.")) {
            return TextFormat.RED + "▊▊▊▊▊▊▊";
        } else if (getGlobalCooldown().getRemaining(player).contains("9.")) {
            return TextFormat.GREEN + "▊" + TextFormat.RED + "▊▊▊▊▊▊";
        } else if (getGlobalCooldown().getRemaining(player).contains("8.")) {
            return TextFormat.GREEN + "▊" + TextFormat.RED + "▊▊▊▊▊▊";
        } else if (getGlobalCooldown().getRemaining(player).contains("7.")) {
            return TextFormat.GREEN + "▊▊" + TextFormat.RED + "▊▊▊▊▊";
        } else if (getGlobalCooldown().getRemaining(player).contains("6.")) {
            return TextFormat.GREEN + "▊▊" + TextFormat.RED + "▊▊▊▊▊";
        } else if (getGlobalCooldown().getRemaining(player).contains("5.")) {
            return TextFormat.GREEN + "▊▊▊" + TextFormat.RED + "▊▊▊▊";
        } else if (getGlobalCooldown().getRemaining(player).contains("4.")) {
            return TextFormat.GREEN + "▊▊▊▊" + TextFormat.RED + "▊▊▊";
        } else if (getGlobalCooldown().getRemaining(player).contains("3.")) {
            return TextFormat.GREEN + "▊▊▊▊" + TextFormat.RED + "▊▊▊";
        } else if (getGlobalCooldown().getRemaining(player).contains("2.")) {
            return TextFormat.GREEN + "▊▊▊▊▊" + TextFormat.RED + "▊▊";
        } else if (getGlobalCooldown().getRemaining(player).contains("1.")) {
            return TextFormat.GREEN + "▊▊▊▊▊▊" + TextFormat.RED + "▊";
        } else if (getGlobalCooldown().getRemaining(player).contains("0.")) {
            return TextFormat.GREEN + "▊▊▊▊▊▊▊";
        }
        return null;
    }

    public enum AbilityUseType {
        INTERACT,
        HIT_PLAYER;
    }
}
