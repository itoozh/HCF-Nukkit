package itoozh.core.session.timer;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.Location;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.event.TimerExpireEvent;
import itoozh.core.timer.TimerManager;
import itoozh.core.timer.type.PlayerTimer;
import itoozh.core.util.LanguageUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StuckTimer extends PlayerTimer implements Listener {

    private final Map<UUID, Location> locations;
    private final int maxMoveBlocks;
    public StuckTimer(TimerManager timerManager) {
        super(timerManager, false, "Stuck", "PLAYER_TIMERS.STUCK", Main.getInstance().getConfig().getInt("TIMERS_COOLDOWN.STUCK"));
        this.locations = new HashMap<>();
        this.maxMoveBlocks = Main.getInstance().getConfig().getInt("TEAMS.F_STUCK_MAX_MOVE");
    }

    public int getMaxMoveBlocks() {
        return this.maxMoveBlocks;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getFrom().getFloorX() == event.getTo().getFloorX() && event.getFrom().getFloorZ() == event.getTo().getFloorZ()) {
            return;
        }
        if (this.hasTimer(player)) {
            this.check(player, event.getTo());
        }
    }

    @Override
    public void applyTimer(Player player, long time) {
        this.locations.put(player.getUniqueId(), player.getLocation());
        super.applyTimer(player, time);
    }

    public Map<UUID, Location> getLocations() {
        return this.locations;
    }

    @Override
    public void applyTimer(Player player) {
        this.locations.put(player.getUniqueId(), player.getLocation());
        super.applyTimer(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.removeTimer(event.getPlayer());
    }

    @EventHandler
    public void onExpire(TimerExpireEvent event) {
        if (event.getPlayerTimer() != this) {
            return;
        }
        Player player = Server.getInstance().getPlayer(event.getPlayer()).get();

        Main.getInstance().getTeamManager().teleportToSafe(player, 1);
        player.sendMessage(TextFormat.colorize(LanguageUtils.getString("STUCK_TIMER.TELEPORTED")));
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (this.hasTimer(player)) {
            this.removeTimer(player);
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("STUCK_TIMER.DAMAGED")));
        }
    }

    @Override
    public void removeTimer(Player player) {
        this.locations.remove(player.getUniqueId());
        super.removeTimer(player);
    }

    private void check(Player player, Location location) {
        if (!this.hasTimer(player)) {
            return;
        }
        Location playerLocation = this.locations.get(player.getUniqueId());
        int x = Math.abs(playerLocation.getFloorX() - location.getFloorX());
        int y = Math.abs(playerLocation.getFloorY() - location.getFloorY());
        int z = Math.abs(playerLocation.getFloorZ() - location.getFloorZ());
        if (x > this.maxMoveBlocks || y > this.maxMoveBlocks || z > this.maxMoveBlocks) {
            this.removeTimer(player);
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("STUCK_TIMER.MOVED").replaceAll("%amount%", String.valueOf(this.maxMoveBlocks))));
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (this.hasTimer(player)) {
            this.check(player, event.getTo());
        }
    }
}
