package itoozh.core.timer.server;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.item.ItemEnderPearl;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.team.Team;
import itoozh.core.team.claim.Claim;
import itoozh.core.team.claim.ClaimType;
import itoozh.core.util.Formatter;
import itoozh.core.util.LanguageUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SOTWTimer implements Listener {
    private List<UUID> enabled;
    private boolean active;
    private Long remaining;

    protected int seconds;
    protected String name;
    protected String scoreboardPath;

    public SOTWTimer() {
        this.name = "SOTW";
        this.scoreboardPath = TextFormat.colorize("");
        this.seconds = 0;
        this.enabled = new ArrayList<>();
        this.remaining = 0L;
        this.active = false;
    }

    public String getRemainingString() {
        long time = this.remaining - System.currentTimeMillis();
        if (time < 0L) {
            this.endSOTW();
        }
        return Formatter.formatHHMMSS(time);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!this.isActive()) {
            return;
        }
        Player player = event.getPlayer();
        Claim time = Main.getInstance().getTeamManager().getClaimManager().findClaim(event.getTo());
        if (time != null && time.isLocked()) {
            if (time.getType() == ClaimType.TEAM) {
                Team team = Main.getInstance().getTeamManager().getTeam(time.getName());
                if (team.getPlayers().contains(player.getUniqueId())) {
                    return;
                }
                event.setTo(event.getFrom());
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (!this.isActive()) {
            return;
        }
        Player player = event.getPlayer();
        Claim claim = Main.getInstance().getTeamManager().getClaimManager().findClaim(event.getTo());
        if (claim != null && claim.isLocked()) {
            Team team = Main.getInstance().getTeamManager().getTeam(claim.getName());
            if (claim.getType() == ClaimType.TEAM) {
                if (team.getPlayers().contains(player.getUniqueId())) {
                    return;
                }
                event.setCancelled(true);
                Main.getInstance().getTimerManager().getEnderpearlTimer().removeTimer(player);
                player.getInventory().addItem(new ItemEnderPearl());
            }
        }
    }

    public void extendSOTW(long time) {
        this.active = true;
        this.remaining = this.getRemaining() + time;
        for (String s : LanguageUtils.splitStringToList(LanguageUtils.getString("SOTW_TIMER.STARTED_SOTW"))) {
            Server.getInstance().broadcastMessage(TextFormat.colorize(s.replaceAll("%time%", Formatter.formatDetailed(time))));
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (this.active && !this.enabled.contains(player.getUniqueId())) {
            event.setCancelled(true);
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                player.teleport(player.getLevel().getSpawnLocation());
            }
        }
    }

    public void startSOTW(long time) {
        this.active = true;
        this.remaining = System.currentTimeMillis() + time;
        for (String s : LanguageUtils.splitStringToList(LanguageUtils.getString("SOTW_TIMER.STARTED_SOTW"))) {
            Server.getInstance().broadcastMessage(TextFormat.colorize(s));
        }
    }

    public void endSOTW() {
        this.active = false;
        this.remaining = 0L;
        for (String s : LanguageUtils.splitStringToList(LanguageUtils.getString("SOTW_TIMER.ENDED_SOTW"))) {
            Server.getInstance().broadcastMessage(TextFormat.colorize(s));
        }
    }
}
