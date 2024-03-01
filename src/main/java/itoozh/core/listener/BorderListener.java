package itoozh.core.listener;

import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.CreatureSpawnEvent;
import cn.nukkit.event.entity.EntityPortalEnterEvent;
import cn.nukkit.event.entity.EntitySpawnEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.item.ItemEnderPearl;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.util.LanguageUtils;

public class BorderListener implements Listener {
    private final int netherBorder;
    private final int worldBorder;

    public BorderListener() {
        this.worldBorder = Main.getInstance().getConfig().getInt("BORDERS.WORLD");
        this.netherBorder = Main.getInstance().getConfig().getInt("BORDERS.NETHER");
    }

    @EventHandler
    public void onFill(PlayerBucketFillEvent event) {
        Location location = event.getBlockClicked().getLocation();
        Player player = event.getPlayer();
        if (!this.inBorder(location)) {
            event.setCancelled();
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("BORDER_LISTENER.CANNOT_INTERACT")));
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        Player player = event.getPlayer();
        if (!this.inBorder(location)) {
            event.setCancelled();
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("BORDER_LISTENER.CANNOT_BREAK")));
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (!this.inBorder(event.getPosition().getLocation())) {
            event.setCancelled();
        }
    }

    @EventHandler
    public void onEmpty(PlayerBucketEmptyEvent event) {
        Location location = event.getBlockClicked().getLocation();
        Player player = event.getPlayer();
        if (!this.inBorder(location)) {
            event.setCancelled();
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("BORDER_LISTENER.CANNOT_PLACE")));
        }
    }

    public boolean inBorder(Location location) {
        int x = location.getFloorX();
        int z = location.getFloorZ();
        if (location.getLevel().getDimension() == Level.DIMENSION_OVERWORLD) {
            return Math.abs(x) <= this.worldBorder && Math.abs(z) <= this.worldBorder;
        }
        return location.getLevel().getDimension() != Level.DIMENSION_NETHER || (Math.abs(x) <= this.netherBorder && Math.abs(z) <= this.netherBorder);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Location location = event.getBlock().getLocation();
        Player player = event.getPlayer();
        if (!this.inBorder(location)) {
            event.setCancelled();
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("BORDER_LISTENER.CANNOT_PLACE")));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getBlock().getId() == BlockID.AIR) {
            return;
        }
        Location location = event.getBlock().getLocation();
        Player player = event.getPlayer();
        if (!this.inBorder(location)) {
            event.setCancelled();
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("BORDER_LISTENER.CANNOT_INTERACT")));
        }
    }

    @EventHandler
    public void onPortal(EntityPortalEnterEvent event) {
        Location location = event.getEntity().getLocation();
        if (this.inBorder(location)) {
            return;
        }
        if (location.getLevel().getDimension() != Level.DIMENSION_OVERWORLD) {
            return;
        }
        int x = location.getFloorX();
        int z = location.getFloorZ();
        boolean b = false;
        if (Math.abs(x) > this.worldBorder) {
            location.setX((x > 0) ? ((double) (this.worldBorder - 50)) : ((double) (-this.worldBorder + 50)));
            b = true;
        }
        if (Math.abs(z) > this.worldBorder) {
            location.setZ((z > 0) ? ((double) (this.worldBorder - 50)) : ((double) (-this.worldBorder + 50)));
            b = true;
        }
        if (b) {
            location.add(0.5, 0.0, 0.5);
            event.getEntity().teleport(location);
        }
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (!this.inBorder(entity.getLocation())) {
            event.setCancelled();
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getFrom().getFloorX() == event.getTo().getFloorX() && event.getFrom().getFloorZ() == event.getTo().getFloorZ()) {
            return;
        }
        if (!this.inBorder(event.getTo())) {
            event.setTo(event.getFrom());
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("BORDER_LISTENER.CANNOT_WALK")));
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Location location = event.getTo();
        Player player = event.getPlayer();
        if (this.inBorder(location)) {
            return;
        }
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }
        event.setCancelled();
        Main.getInstance().getTimerManager().getEnderpearlTimer().removeTimer(player);
        player.getInventory().addItem(new ItemEnderPearl());
        player.sendMessage(TextFormat.colorize(LanguageUtils.getString("BORDER_LISTENER.CANNOT_TELEPORT")));
    }
}

