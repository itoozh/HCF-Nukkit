package itoozh.core.session.timer;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.item.ItemEnderPearl;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.event.TimerExpireEvent;
import itoozh.core.session.Session;
import itoozh.core.team.Team;
import itoozh.core.team.claim.Claim;
import itoozh.core.team.claim.ClaimType;
import itoozh.core.timer.TimerManager;
import itoozh.core.timer.type.PlayerTimer;
import itoozh.core.util.LanguageUtils;

public class InvincibilityTimer extends PlayerTimer implements Listener {
    public InvincibilityTimer(TimerManager timerManager) {
        super(timerManager, true, "Invincibility", "PLAYER_TIMERS.INVINCIBILITY", Main.getInstance().getConfig().getInt("TIMERS_COOLDOWN.INVINCIBILITY"));
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }

        Player player = event.getPlayer();
        Session session = Main.getInstance().getSessionManager().getSessionByUUID(player.getUniqueId());
        Claim team = Main.getInstance().getTeamManager().getClaimManager().findClaim(event.getTo());
        Team playerTeam = session.getTeam();
        if (this.hasTimer(player)) {
            if (playerTeam != null && team != null && playerTeam.getClaim().getName().equals(team.getName()) && Main.getInstance().getConfig().getBoolean("INVINCIBILITY.ENTER_OWN_CLAIM")) {
                return;
            }
            if (team != null) {
                if (team.getType() == ClaimType.TEAM || team.getType() == ClaimType.KOTH) {
                    event.setCancelled(true);
                    Main.getInstance().getTimerManager().getEnderpearlTimer().removeTimer(player);
                    player.getInventory().addItem(new ItemEnderPearl());
                    player.sendMessage(TextFormat.colorize(LanguageUtils.getString("INVINCIBILITY.CANNOT_TELEPORT").replaceAll("%claim%", team.getNameFormat(player))));
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Claim team = Main.getInstance().getTeamManager().getClaimManager().findClaim(player.getLocation());
        if (team != null && team.getType() == ClaimType.SPAWN) {
            this.pauseTimer(player);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getFrom().getFloorX() == event.getTo().getFloorX() && event.getFrom().getFloorZ() == event.getTo().getFloorZ()) {
            return;
        }
        if (this.hasTimer(player)) {
            Claim team1 = Main.getInstance().getTeamManager().getClaimManager().findClaim(event.getTo());
            Claim team2 = Main.getInstance().getTeamManager().getClaimManager().findClaim(event.getFrom());

            if (team1 == null) {
                team1 = new Claim(ClaimType.WILDERNESS, "Wilderness", player.getLocation(), player.getLocation());
            }
            if (team2 == null) {
                team2 = new Claim(ClaimType.WILDERNESS, "Wilderness", player.getLocation(), player.getLocation());
            }

            if (team1.getType() == ClaimType.SPAWN) {
                if (!this.getPausedCache().containsKey(player.getUniqueId())) {
                    this.pauseTimer(player);
                }
                if (team1 == team2) {
                    return;
                }
                this.pauseTimer(player);
                player.setHealth(player.getMaxHealth());
                player.getFoodData().setLevel(20);
                player.getFoodData().setFoodSaturationLevel(20.0f);
            } else if (team2.getType() == ClaimType.SPAWN) {
                this.unpauseTimer(player);
            }
            if (this.checkEntry(player, team1)) {
                event.setTo(event.getFrom());
                player.sendMessage(TextFormat.colorize(LanguageUtils.getString("INVINCIBILITY.CANNOT_ENTER").replaceAll("%claim%", team1.getNameFormat(player))));
            }
        }
    }

    @Override
    public void applyTimer(Player player) {
        super.applyTimer(player);
        Claim team = Main.getInstance().getTeamManager().getClaimManager().findClaim(player.getLocation());
        if (team != null && team.getType() == ClaimType.SPAWN) {
            this.pauseTimer(player);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (this.hasTimer(player)) {
            this.removeTimer(player);
        }
    }

    @EventHandler
    public void onFood(PlayerFoodLevelChangeEvent event) {
        Player player = event.getPlayer();
        if (this.hasTimer(player)) {
            event.setCancelled(true);
            player.getFoodData().reset();
        }
    }

    @Override
    public void applyTimer(Player player, long time) {
        super.applyTimer(player, time);
        Claim team = Main.getInstance().getTeamManager().getClaimManager().findClaim(player.getLocation());
        if (team != null && team.getType() == ClaimType.SPAWN) {
            this.pauseTimer(player);
        }
    }

    public boolean checkEntry(Player player, Claim team) {
        Session session = Main.getInstance().getSessionManager().getSessionByUUID(player.getUniqueId());
        if (!this.hasTimer(player)) {
            return false;
        }
        if (team.getType() == ClaimType.TEAM) {
            boolean ownTeam = Main.getInstance().getConfig().getBoolean("INVINCIBILITY.ENTER_OWN_CLAIM");
            Team team1 = Main.getInstance().getTeamManager().getTeam(team.getName());
            if (ownTeam && team1.getMembers().contains(session.getMember())) {
                return false;
            }
        }
        return (team.getType() != ClaimType.WILDERNESS) && (team.getType() != ClaimType.ROAD) && (team.getType() != ClaimType.SPAWN) && (team.getType() != ClaimType.WARZONE);
    }

    @EventHandler
    public void onExpire(TimerExpireEvent event) {
        if (event.getPlayerTimer() != this) {
            return;
        }
        Main.getInstance().getNameTags().update();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (this.hasTimer(player)) {
            event.setCancelled(true);
        }
    }
}
