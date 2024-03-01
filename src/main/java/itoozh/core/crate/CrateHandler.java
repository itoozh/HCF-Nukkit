package itoozh.core.crate;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.session.Session;

import java.util.Objects;

public class CrateHandler implements Listener {

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Item item = player.getInventory().getItemInHand();
        if (event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK || event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) {
            if (player.isSneaking()){
                if (item.getNamedTagEntry("crate") != null) {
                    String crateKey = item.getNamedTag().getString("crate");
                    Crate crate = Main.getInstance().getCrateManager().getCrate(crateKey);
                    if (crate != null) {
                        item.setCount(item.getCount() - 1);
                        crate.giveRewards(player);
                        player.sendMessage(TextFormat.GREEN + "You have opened a " + crate.getDisplayName() + TextFormat.GREEN + " crate.");
                        player.getInventory().setItemInHand(item);
                        return;
                    }
                }
            }
            if (event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
                if (event.getBlock() == null) {
                    return;
                }
                Location location = event.getBlock().getLocation();
                Crate crate = Main.getInstance().getCrateManager().getCrate(location);
                if (crate != null) {
                    event.setCancelled();
                    crate.open(player);
                }
            }
        } else if (event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            if (event.getBlock() == null) {
                return;
            }
            Location location = event.getBlock().getLocation();
            Crate crate = Main.getInstance().getCrateManager().getCrate(location);
            Session session = Main.getInstance().getSessionManager().getSession(player);
            if (session.placingCrate != null) {
                event.setCancelled();
                if (Main.getInstance().getCrateManager().getCrate(location) != null) {
                    player.sendMessage(TextFormat.colorize("&cCrate in this location already exists!"));
                    return;
                }
                Main.getInstance().getCrateManager().placeCrate(location, session.placingCrate);
                player.sendMessage(TextFormat.colorize("&aCrate " + session.placingCrate.getName() + " placed!"));
                session.placingCrate = null;
            } else if (session.removingCrate) {
                event.setCancelled();
                if (crate == null) {
                    player.sendMessage(TextFormat.colorize("&cCrate in this location doesn't exist!"));
                    return;
                }
                Main.getInstance().getCrateManager().removeCrate(location);
                player.sendMessage( TextFormat.colorize("&aCrate removed success!"));
                Main.getInstance().getHologramManager().restartHologram();
                session.removingCrate = false;
            } else {
                if (crate != null) {
                    event.setCancelled();
                    if (item.getNamedTagEntry("crate") != null) {
                        String crateKey = item.getNamedTag().getString("crate");
                        if (Objects.equals(crateKey, crate.getName())) {
                            item.setCount(item.getCount() - 1);
                            crate.giveRewards(player);
                            player.sendMessage(TextFormat.GREEN + "You have opened a " + crate.getDisplayName() + TextFormat.GREEN + " crate.");
                            player.getInventory().setItemInHand(item);
                        } else {
                            player.sendMessage(TextFormat.colorize(TextFormat.RED + "You do not have a "+ crate.getDisplayName() + TextFormat.RED + " key."));
                        }
                    } else {
                        player.sendMessage(TextFormat.colorize(TextFormat.RED + "You do not have a "+ crate.getDisplayName() + TextFormat.RED + " key."));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();
        Session session = Main.getInstance().getSessionManager().getSession(player);
        Crate crate = Main.getInstance().getCrateManager().getCrate(location);
        if (session.removingCrate) {
            event.setCancelled();
            session.removingCrate = false;
        }
        if (session.placingCrate != null) {
            event.setCancelled();
            session.placingCrate = null;
        }
        if (crate != null) {
            event.setCancelled();
        }
    }
}
