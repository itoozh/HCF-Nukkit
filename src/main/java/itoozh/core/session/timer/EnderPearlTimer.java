package itoozh.core.session.timer;

import cn.nukkit.Player;
import cn.nukkit.entity.projectile.EntityEnderPearl;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.timer.TimerManager;
import itoozh.core.timer.type.PlayerTimer;
import itoozh.core.util.LanguageUtils;

public class EnderPearlTimer extends PlayerTimer implements Listener {
    public EnderPearlTimer(TimerManager timerManager) {
        super(timerManager, false, "EnderPearl", "PLAYER_TIMERS.ENDER_PEARL", Main.getInstance().getConfig().getInt("TIMERS_COOLDOWN.ENDER_PEARL"));
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof EntityEnderPearl)) {
            return;
        }

        if (!(event.getShooter() instanceof Player)) {
            return;
        }
        Player shooter = (Player) event.getShooter();
        if (!this.hasTimer(shooter)) {
            this.applyTimer(shooter);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) {
            return;
        }
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }
        if (event.getItem().getId() != Item.ENDER_PEARL) {
            return;
        }
        Player player = event.getPlayer();
        if (this.hasTimer(player)) {
            event.setCancelled(true);
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("ENDERPEARL_TIMER.COOLDOWN").replaceAll("%seconds%", this.getRemainingString(player))));
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }
        Player player = event.getPlayer();
        if (!this.hasTimer(player)) {
            event.setCancelled(true);
        }
    }
}
