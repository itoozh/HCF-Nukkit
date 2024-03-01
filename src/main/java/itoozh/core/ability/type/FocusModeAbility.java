package itoozh.core.ability.type;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import itoozh.core.ability.Ability;
import itoozh.core.ability.AbilityManager;
import itoozh.core.ability.util.Pair;
import itoozh.core.util.LanguageUtils;
import itoozh.core.util.TaskUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class FocusModeAbility extends Ability implements Listener {
    private double multiplier;
    private Map<UUID, UUID> focusMode;
    private int hitsValid;
    private Map<UUID, Pair<UUID, Long>> lastDamage = new HashMap<>();
    private int seconds;

    public FocusModeAbility(AbilityManager manager) {
        super(manager, AbilityManager.AbilityUseType.INTERACT, "Focus Mode");
        this.focusMode = new HashMap<>();
        this.multiplier = AbilityManager.dataFile.getDouble("FOCUS_MODE.DAMAGE_MULTIPLIER");
        this.seconds = AbilityManager.dataFile.getInt("FOCUS_MODE.SECONDS");
        this.hitsValid = AbilityManager.dataFile.getInt("FOCUS_MODE.HITS_VALID");
        TaskUtils.executeScheduled(6000, this::cleanDamageStore);
    }

    @Override
    public void onClick(Player player) {
        if (this.cannotUse(player)) {
            return;
        }
        if (this.hasCooldown(player)) {
            return;
        }
        Player damager = this.getDamager(player, this.hitsValid);
        if (damager == null) {
            player.sendMessage(LanguageUtils.getString("ABILITIES.FOCUS_MODE.NO_LAST_HIT"));
            return;
        }
        this.focusMode.put(player.getUniqueId(), damager.getUniqueId());
        this.takeItem(player);
        this.applyCooldown(player);
        TaskUtils.executeLater(20 * this.seconds, () -> {
            this.focusMode.remove(player.getUniqueId());
            for (String s : LanguageUtils.splitStringToList(LanguageUtils.getString("ABILITIES.FOCUS_MODE.EXPIRED"))) {
                player.sendMessage(s);
            }
        });
        for (String s : LanguageUtils.splitStringToList(LanguageUtils.getString("ABILITIES.FOCUS_MODE.USED"))) {
            player.sendMessage(s.replaceAll("%player%", damager.getName()));
        }
        for (String s : LanguageUtils.splitStringToList(LanguageUtils.getString("ABILITIES.FOCUS_MODE.BEEN_HIT"))) {
            damager.sendMessage(s.replaceAll("%player%", player.getName()));
        }
    }

    private void cleanDamageStore() {
        Iterator<Map.Entry<UUID, Pair<UUID, Long>>> iterator = this.lastDamage.entrySet().iterator();
        while (iterator.hasNext()) {
            Pair<UUID, Long> pair = iterator.next().getValue();
            boolean b = (System.currentTimeMillis() - pair.getValue() <= 60000L);
            if (!b)
                iterator.remove();
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();
        if (!this.focusMode.containsKey(damager.getUniqueId())) {
            return;
        }
        if (!this.focusMode.get(damager.getUniqueId()).equals(player.getUniqueId())) {
            return;
        }
        if (this.cannotHit(damager, player)) {
            return;
        }
        event.setDamage((float) (event.getFinalDamage() * this.multiplier));
    }

    @EventHandler
    public void onDamageStore(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        Player damager = null;
        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        }
        if (damager == null) {
            return;
        }
        if (damager == player) {
            return;
        }
        if (this.cannotHit(damager, player)) {
            return;
        }
        this.lastDamage.put(player.getUniqueId(), new Pair<>(damager.getUniqueId(), System.currentTimeMillis()));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        this.focusMode.remove(player.getUniqueId());
    }

    public Player getDamager(Player player, int time) {
        Pair<UUID, Long> pair = this.lastDamage.get(player.getUniqueId());
        if (pair != null) {
            Player target = Server.getInstance().getOfflinePlayer(pair.getKey()).getPlayer();
            boolean b = System.currentTimeMillis() - pair.getValue() <= time * 1000L;
            if (target != null && b) {
                return target;
            }
        }
        return null;
    }
}
