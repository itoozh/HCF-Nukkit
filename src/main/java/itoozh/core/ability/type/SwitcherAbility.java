package itoozh.core.ability.type;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityEgg;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.projectile.EntitySnowball;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import itoozh.core.ability.Ability;
import itoozh.core.ability.AbilityManager;
import itoozh.core.util.LanguageUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SwitcherAbility  extends Ability implements Listener {
    private int distance;
    private Set<UUID> switchers;

    public SwitcherAbility(AbilityManager manager) {
        super(manager, null, "Switcher");
        this.switchers = new HashSet<>();
        this.distance = AbilityManager.dataFile.getInt("SWITCHER.DISTANCE");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }
        Player player = event.getPlayer();
        if (!this.hasAbilityInHand(player)) {
            return;
        }
        if (this.cannotUse(player) || this.hasCooldown(player)) {
            event.setCancelled(true);
            return;
        }
        this.applyCooldown(player);
        this.switchers.add(player.getUniqueId());
    }

    @EventHandler
    public void onHit(EntityDamageByChildEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (this.getItem().getId() == Item.fromString("SNOW_BALL").getId() && !(event.getDamager() instanceof EntitySnowball)) {
            return;
        }
        if (this.getItem().getId() == Item.fromString("EGG").getId() && !(event.getDamager() instanceof EntityEgg)) {
            return;
        }
        Player damager = getDamager(event.getDamager());
        Player player = (Player) event.getEntity();
        if (damager == null) {
            return;
        }
        if (!this.switchers.contains(damager.getUniqueId())) {
            return;
        }
        if (this.cannotHit(damager, player)) {
            damager.getInventory().addItem(this.item);
            return;
        }
        if (damager.getLocation().distance(player.getLocation()) > this.distance) {
            damager.getInventory().addItem(this.item);
            damager.sendMessage(LanguageUtils.getString("ABILITIES.SWITCHER.TOO_FAR"));
            return;
        }
        Location playerLocation = player.getLocation().clone();
        Location damagerLocation = damager.getLocation().clone();
        player.teleport(damagerLocation);
        damager.teleport(playerLocation);
        this.switchers.remove(damager.getUniqueId());
        for (String s : LanguageUtils.splitStringToList(LanguageUtils.getString("ABILITIES.SWITCHER.USED"))) {
            damager.sendMessage(s.replaceAll("%player%", player.getName()));
        }
        for (String s : LanguageUtils.splitStringToList(LanguageUtils.getString("ABILITIES.SWITCHER.BEEN_HIT"))) {
            player.sendMessage(s.replaceAll("%player%", damager.getName()));
        }
    }

    public static Player getDamager(Entity entity) {
        if (entity instanceof Player) {
            return (Player) entity;
        }
        if (entity instanceof EntityProjectile) {
            EntityProjectile projectile = (EntityProjectile) entity;
            if (projectile.shootingEntity instanceof Player) {
                return (Player) projectile.shootingEntity;
            }
        }
        return null;
    }
}
