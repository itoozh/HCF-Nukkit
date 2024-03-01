package itoozh.core.ability.type;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.potion.Effect;
import itoozh.core.Main;
import itoozh.core.ability.Ability;
import itoozh.core.ability.AbilityManager;
import itoozh.core.util.LanguageUtils;
import itoozh.core.util.TaskUtils;

import java.util.*;

public class ComboAbility extends Ability implements Listener {
    private int seconds;
    private int amountPerHit;
    private Set<UUID> combo;
    private Map<UUID, Integer> hits;
    private int maxHits;

    public ComboAbility(AbilityManager manager) {
        super(manager, AbilityManager.AbilityUseType.INTERACT, "Combo Ability");
        this.combo = new HashSet<>();
        this.hits = new HashMap<>();
        this.maxHits = AbilityManager.dataFile.getInt("COMBO_ABILITY.MAX_HITS");
        this.amountPerHit = AbilityManager.dataFile.getInt("COMBO_ABILITY.AMOUNT_PER_HIT");
        this.seconds = AbilityManager.dataFile.getInt("COMBO_ABILITY.SECONDS");
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        Player target = (Player) event.getDamager();
        UUID targetUUID = target.getUniqueId();
        if (this.cannotHit(target, player)) {
            return;
        }
        if (this.combo.contains(targetUUID)) {
            this.hits.putIfAbsent(targetUUID, 0);
            int hit = this.hits.get(targetUUID);
            if (hit < this.maxHits) {
                this.hits.put(targetUUID, hit + this.amountPerHit);
            }
        }
    }

    private void handleEffect(Player player) {
        Integer hit = this.hits.remove(player.getUniqueId());
        this.combo.remove(player.getUniqueId());
        if (hit != null) {
            String[] effects = AbilityManager.dataFile.getString("COMBO_ABILITY.EFFECT").split(", ");
            Effect effect = Effect.getEffectByName(effects[0]);
            int amplifier = Integer.parseInt(effects[1]) - 1;
            effect.setAmplifier(amplifier);
            effect.setDuration(20 * hit);
            Main.getInstance().getPvPClassManager().addEffect(player, effect);
            for (String s : LanguageUtils.splitStringToList(LanguageUtils.getString("ABILITIES.COMBO_ABILITY.GAINED_EFFECT"))) {
                player.sendMessage(s.replaceAll("%amount%", String.valueOf(hit)));
            }
        }
    }

    @Override
    public void onClick(Player player) {
        if (this.cannotUse(player)) {
            return;
        }
        if (this.hasCooldown(player)) {
            return;
        }
        this.combo.add(player.getUniqueId());
        this.takeItem(player);
        this.applyCooldown(player);
        TaskUtils.executeLater(20 * this.seconds, () -> this.handleEffect(player));
        for (String s : LanguageUtils.splitStringToList(LanguageUtils.getString("ABILITIES.COMBO_ABILITY.USED"))) {
            player.sendMessage(s);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        this.hits.remove(player.getUniqueId());
        this.combo.remove(player.getUniqueId());
    }
}
