package itoozh.core.session.timer;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.item.ItemEnderPearl;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.team.claim.Claim;
import itoozh.core.team.claim.ClaimType;
import itoozh.core.timer.TimerManager;
import itoozh.core.timer.type.PlayerTimer;
import itoozh.core.util.Formatter;
import itoozh.core.util.LanguageUtils;
import itoozh.core.util.TaskUtils;

public class CombatTimer extends PlayerTimer implements Listener {
    public CombatTimer(TimerManager timerManager) {
        super(timerManager, false, "Combat", "PLAYER_TIMERS.COMBAT_TAG", Main.getInstance().getConfig().getInt("TIMERS_COOLDOWN.COMBAT_TAG"));
    }

    @Override
    public String getRemainingString(Player player) {
        if (this.pausedCache.containsKey(player.getUniqueId())) {
            return Formatter.formatHHMMSS(this.pausedCache.get(player.getUniqueId()));
        }
        long time = this.timerCache.get(player.getUniqueId()) - System.currentTimeMillis();
        return Formatter.formatHHMMSS(time);
    }

    @EventHandler
    public void onDie(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (this.hasTimer(player)) {
            TaskUtils.executeLater(10, () -> this.removeTimer(player));
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Claim claim = Main.getInstance().getTeamManager().getClaimManager().findClaim(event.getTo());
        if (event.getFrom().getFloorX() == event.getTo().getFloorX() && event.getFrom().getFloorZ() == event.getTo().getFloorZ()) {
            return;
        }
        if (this.hasTimer(player) && claim != null && claim.getType() == ClaimType.SPAWN) {
            event.setTo(event.getFrom());
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("COMBAT_TIMER.CANNOT_ENTER")));
        }
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
            if (!this.hasTimer(damager)) {
                damager.sendMessage(TextFormat.colorize(LanguageUtils.getString("COMBAT_TIMER.TAGGED").replaceAll("%seconds%", String.valueOf(this.seconds))));
            }
            if (!this.hasTimer(damaged)) {
                damaged.sendMessage(TextFormat.colorize(LanguageUtils.getString("COMBAT_TIMER.TAGGED").replaceAll("%seconds%", String.valueOf(this.seconds))));
            }
            this.applyTimer(damager);
            this.applyTimer(damaged);
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }
        Player player = event.getPlayer();
        Claim claim = Main.getInstance().getTeamManager().getClaimManager().findClaim(event.getTo());
        if (this.hasTimer(player) && claim.getType() == ClaimType.SPAWN) {
            event.setCancelled(true);
            Main.getInstance().getTimerManager().getEnderpearlTimer().removeTimer(player);
            player.getInventory().addItem(new ItemEnderPearl());
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("COMBAT_TIMER.CANNOT_TELEPORT")));
        }
    }
}
