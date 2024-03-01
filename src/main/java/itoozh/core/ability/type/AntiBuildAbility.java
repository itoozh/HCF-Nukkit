package itoozh.core.ability.type;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.potion.Effect;
import itoozh.core.Main;
import itoozh.core.ability.Ability;
import itoozh.core.ability.AbilityManager;
import itoozh.core.util.Cooldown;
import itoozh.core.util.ItemUtil;
import itoozh.core.util.LanguageUtils;

import java.util.*;

public class AntiBuildAbility extends Ability implements Listener {
    private List<Effect> effects;
    private int antiBuildTime;
    private List<Integer> deniedInteract;
    private Map<UUID, Integer> hits;
    private int maxHits;
    private Cooldown antiBuild;
    public AntiBuildAbility(AbilityManager manager) {
        super(manager, AbilityManager.AbilityUseType.HIT_PLAYER, "Anti Build");
        this.hits = new HashMap<>();
        this.deniedInteract = new ArrayList<>();
        this.effects = new ArrayList<>();
        this.antiBuild = new Cooldown(Main.getInstance());
        this.maxHits = AbilityManager.dataFile.getInt(this.nameConfig + ".HITS_REQUIRED");
        this.antiBuildTime = AbilityManager.dataFile.getInt(this.nameConfig + ".ANTI_BUILD_TIME");
        this.load();
    }
    private void load() {
        for (String s : AbilityManager.dataFile.getStringList(String.valueOf(new StringBuilder().append(this.nameConfig).append(".DISABLED_INTERACT")))) {
            this.deniedInteract.add(Integer.valueOf(s));
        }
        for (String s : AbilityManager.dataFile.getStringList(String.valueOf(new StringBuilder().append(this.nameConfig).append(".EFFECTS_DAMAGER")))) {
            this.effects.add(ItemUtil.getEffect(s));
        }
    }

    @EventHandler
    public void onBreak(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (this.antiBuild.hasCooldown(player) && Main.getInstance().getTeamManager().canBuild(player, event.getBlock().getLocation())) {
            event.setCancelled(true);
            player.sendMessage(LanguageUtils.getString("ABILITIES.ANTI_BUILD.DENIED_BUILD").replaceAll("%seconds%", this.antiBuild.getRemaining(player)));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (block == null) {
            return;
        }
        if (!this.antiBuild.hasCooldown(player)) {
            return;
        }
        if (event.getAction() == PlayerInteractEvent.Action.PHYSICAL) {
            event.setCancelled(true);
            return;
        }
        if (event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && this.deniedInteract.contains(block.getId())) {
            event.setCancelled(true);
            player.sendMessage(LanguageUtils.getString("ABILITIES.ANTI_BUILD.DENIED_BUILD").replaceAll("%seconds%", this.antiBuild.getRemaining(player)));
        }
    }

    @EventHandler
    public void onBuild(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (this.antiBuild.hasCooldown(player) && Main.getInstance().getTeamManager().canBuild(player, event.getBlock().getLocation())) {
            event.setCancelled(true);
            player.sendMessage(LanguageUtils.getString("ABILITIES.ANTI_BUILD.DENIED_BUILD").replaceAll("%seconds%", this.antiBuild.getRemaining(player)));
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        this.hits.remove(player.getUniqueId());
        this.antiBuild.removeCooldown(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.hits.remove(player.getUniqueId());
    }

    @Override
    public void onHit(Player player, Player target) {
        UUID targetUUID = player.getUniqueId();
        if (this.cannotUse(player, target)) {
            return;
        }
        if (this.hasCooldown(player)) {
            return;
        }
        if (!this.hits.containsKey(targetUUID)) {
            this.hits.put(targetUUID, 0);
        }
        int hit = this.hits.get(targetUUID) + 1;
        this.hits.put(targetUUID, hit);
        if (hit == this.maxHits) {
            this.hits.remove(player.getUniqueId());
            this.antiBuild.applyCooldown(target, this.antiBuildTime);
            this.takeItem(player);
            this.applyCooldown(player);
            for (Effect effect : this.effects) {
                Main.getInstance().getPvPClassManager().addEffect(player, effect);
            }
            for (String s : LanguageUtils.splitStringToList(LanguageUtils.getString("ABILITIES.ANTI_BUILD.USED"))) {
                player.sendMessage(s.replaceAll("%player%", target.getName()).replaceAll("%seconds%", String.valueOf(this.antiBuildTime)));
            }
            for (String s : LanguageUtils.splitStringToList(LanguageUtils.getString("ABILITIES.ANTI_BUILD.BEEN_HIT"))) {
                target.sendMessage(s.replaceAll("%player%", player.getName()).replaceAll("%seconds%", String.valueOf(this.antiBuildTime)));
            }
        }
    }
}
