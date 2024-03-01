package itoozh.core.team.listener;

import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.player.PlayerBucketEmptyEvent;
import cn.nukkit.event.player.PlayerBucketFillEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.level.Location;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.team.claim.Claim;
import itoozh.core.util.LanguageUtils;

import java.util.ArrayList;
import java.util.List;

public class TeamListener implements Listener {

    private List<Integer> deniedInteract = new ArrayList<>();

    public TeamListener() {
        this.deniedInteract.addAll(Main.getInstance().getConfig().getIntegerList("SYSTEM_TEAMS.DENIED_INTERACT"));
    }

    @EventHandler
    public void handler(PlayerDeathEvent ev) {
        Player player = ev.getEntity();
        Player killer = null;
        if (ev.getEntity().getKiller() instanceof Player) killer = (Player) ev.getEntity().getKiller();
        Main.getInstance().getTeamManager().handleDeath(player, killer);
    }

    @EventHandler
    public void handler(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player damager = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();
        if (!Main.getInstance().getTeamManager().canHit(damager, damaged, true)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlockReplace().getLocation();
        Claim claim = Main.getInstance().getTeamManager().getClaimManager().findClaimPerType(location.getFloorX(), location.getFloorZ());
        if (!Main.getInstance().getTeamManager().canBuild(player, location)) {
            event.setCancelled(true);
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_LISTENER.BLOCK_PLACE").replaceAll("%team%", claim.getNameFormat(player))));
        }
    }

    @EventHandler
    public void onFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlockClicked().getLocation();
        Claim claim = Main.getInstance().getTeamManager().getClaimManager().findClaimPerType(location.getFloorX(), location.getFloorZ());
        if (!Main.getInstance().getTeamManager().canBuild(player, location)) {
            event.setCancelled(true);
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_LISTENER.BLOCK_INTERACT").replaceAll("%team%", claim.getNameFormat(player))));
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();
        Claim claim = Main.getInstance().getTeamManager().getClaimManager().findClaimPerType(location.getFloorX(), location.getFloorZ());
        if (!Main.getInstance().getTeamManager().canBuild(player, location)) {
            event.setCancelled(true);
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_LISTENER.BLOCK_DIG").replaceAll("%team%", claim.getNameFormat(player))));
        }
    }

    @EventHandler
    public void onEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlockClicked().getLocation();
        Claim claim = Main.getInstance().getTeamManager().getClaimManager().findClaimPerType(location.getFloorX(), location.getFloorZ());
        if (!Main.getInstance().getTeamManager().canBuild(player, location)) {
            event.setCancelled(true);
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_LISTENER.BLOCK_INTERACT").replaceAll("%team%", claim.getNameFormat(player))));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getBlock() == null) {
            return;
        }
        if (event.getAction() != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (!this.deniedInteract.contains(event.getBlock().getId())) {
            return;
        }
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();
        Claim claim = Main.getInstance().getTeamManager().getClaimManager().findClaimPerType(location.getFloorX(), location.getFloorZ());
        if (!Main.getInstance().getTeamManager().canBuild(player, location)) {
            event.setCancelled(true);
            if (!(event.getBlock().getId() == BlockID.CHEST)) {
                player.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_LISTENER.BLOCK_INTERACT").replaceAll("%team%", claim.getNameFormat(player))));
            }
        }
    }
}
