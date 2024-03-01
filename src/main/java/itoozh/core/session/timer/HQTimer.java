package itoozh.core.session.timer;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.event.TimerExpireEvent;
import itoozh.core.session.Session;
import itoozh.core.team.Team;
import itoozh.core.timer.TimerManager;
import itoozh.core.timer.type.PlayerTimer;
import itoozh.core.util.LanguageUtils;

public class HQTimer extends PlayerTimer implements Listener {
    public HQTimer(TimerManager timerManager) {
        super(timerManager, false, "HQ", "PLAYER_TIMERS.HQ", Main.getInstance().getConfig().getInt("TIMERS_COOLDOWN.HQ"));
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player damager = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();
        if (Main.getInstance().getTeamManager().canHit(damager, damaged, false)) {
            if (this.hasTimer(damaged)) {
                this.removeTimer(damaged);
                damaged.sendMessage(TextFormat.colorize(LanguageUtils.getString("HQ_TIMER.DAMAGED")));
            }
            if (this.hasTimer(damager)) {
                this.removeTimer(damager);
                damager.sendMessage(TextFormat.colorize(LanguageUtils.getString("Q_TIMER.DAMAGED")));
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getFrom().getFloorX() == event.getTo().getFloorX() && event.getFrom().getFloorZ() == event.getTo().getFloorZ()) {
            return;
        }
        if (this.hasTimer(player)) {
            this.removeTimer(player);
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("HQ_TIMER.MOVED")));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.removeTimer(event.getPlayer());
    }

    public void tpHq(Player player) {
        Session session = Main.getInstance().getSessionManager().getSessionByUUID(player.getUniqueId());
        Team team = session.getTeam();
        if (team == null) {
            return;
        }
        if (team.getHq() == null) {
            return;
        }
        player.teleport(team.getHq().add(0.5, 1.0, 0.5));
        player.sendMessage(TextFormat.colorize(LanguageUtils.getString("HQ_TIMER.WARPED").replaceAll("%team%", team.getName())));
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (this.hasTimer(player)) {
            this.removeTimer(player);
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("HQ_TIMER.MOVED")));
        }
    }

    @EventHandler
    public void onExpire(TimerExpireEvent event) {
        if (event.getPlayerTimer() != this) {
            return;
        }
        Player player = Server.getInstance().getPlayer(event.getPlayer()).get();
        this.tpHq(player);
    }
}
