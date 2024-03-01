package itoozh.core.listener;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityPortalEnterEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.team.claim.Claim;
import itoozh.core.team.claim.ClaimType;
import itoozh.core.util.LanguageUtils;

public class PortalListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPortal(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            return;
        }
        Location from = event.getFrom();
        System.out.println("ejecutado");
        if (from.getLevel().getDimension() == Level.DIMENSION_OVERWORLD) {
            System.out.println("teleport al nether");
            Location location = from.clone();
            location.setX(location.getX() / Main.getInstance().getConfig().getInt("MULTIPLIERS.NETHER_MULTIPLIER"));
            location.setZ(location.getZ() / Main.getInstance().getConfig().getInt("MULTIPLIERS.NETHER_MULTIPLIER"));
            location.setLevel(event.getTo().getLevel());
            event.getPlayer().teleport(location);
        } else if (from.getLevel().getDimension() == Level.DIMENSION_NETHER) {
            System.out.println("teleport al overworld");
            Location location = new Location(0.497, 68, 0.508);
            location.setLevel(Server.getInstance().getDefaultLevel());
            event.getPlayer().teleport(location);
        }
    }

    @EventHandler
    public void onEntity(EntityPortalEnterEvent event) {
        if (event.getEntity() instanceof Player) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            return;
        }
        Player player = event.getPlayer();
        Claim team = Main.getInstance().getTeamManager().getClaimManager().findClaim(event.getFrom());
        if (team != null && team.getType() == ClaimType.SPAWN) {
            Location location = new Location(0.497, 68, 0.508);
            location.setLevel(Server.getInstance().getDefaultLevel());
            player.teleport(location);
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("PORTAL_LISTENER.TELEPORTED_SPAWN")));
        }
    }


}
