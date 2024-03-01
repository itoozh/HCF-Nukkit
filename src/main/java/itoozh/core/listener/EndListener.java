package itoozh.core.listener;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.level.Level;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.util.ItemUtil;
import itoozh.core.util.LanguageUtils;

import java.util.Objects;


public class EndListener implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Block block = event.getTo().getLevelBlock();

        if (player == null || block == null) {
            return; // Return early if player or block is null
        }

        Level playerLevel = player.getLevel();

        if (playerLevel == null) {
            return; // Return early if player's level is null
        }
        if (ItemUtil.deserializeLoc(Main.getInstance().getConfig().getString("LOCATIONS.END_EXITS.END_EXIT")).getLevel() == null) {
            return;
        }

        if (Objects.equals(playerLevel.getName(), ItemUtil.deserializeLoc(Main.getInstance().getConfig().getString("LOCATIONS.END_EXITS.END_EXIT")).getLevel().getName()) && block.getId() == Block.WATER) {
            player.teleport(ItemUtil.deserializeLoc(Main.getInstance().getConfig().getString("LOCATIONS.END_EXITS.WORLD_EXIT")));
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("END_LISTENER.ENTERED")));
        } else if (playerLevel.getDimension() == Level.DIMENSION_OVERWORLD && block.getId() == Block.END_PORTAL) {
            player.teleport(ItemUtil.deserializeLoc(Main.getInstance().getConfig().getString("LOCATIONS.END_EXITS.END_EXIT")));
        }
    }
}
