package itoozh.core.listener;

import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.level.Location;
import itoozh.core.Main;
import itoozh.core.util.Cooldown;

public class GlitchListener implements Listener {

    private static Cooldown hitCooldown;

    public GlitchListener() {
        hitCooldown = new Cooldown(Main.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof  Player)) {
            return;
        }
        Player player = (Player) event.getDamager();
        if (hitCooldown.hasCooldown(player) && !player.hasLineOfSight(event.getEntity())) {
            hitCooldown.removeCooldown(player);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getBlock().getId() == BlockID.AIR) {
            return;
        }
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();
        if (!Main.getInstance().getTeamManager().canBuild(player, location)) {
            hitCooldown.applyCooldownTicks(player, 950);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        hitCooldown.removeCooldown(event.getPlayer());
    }

    public static Cooldown getHitCooldown() {
        return hitCooldown;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();
        if (event.isCancelled() || !Main.getInstance().getTeamManager().canBuild(player, location)) {
            hitCooldown.applyCooldownTicks(player, 950);
        }
    }
}
