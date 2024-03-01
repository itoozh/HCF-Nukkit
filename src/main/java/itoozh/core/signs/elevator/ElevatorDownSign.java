package itoozh.core.signs.elevator;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.listener.GlitchListener;
import itoozh.core.signs.CustomSign;
import itoozh.core.util.LanguageUtils;
import lombok.Getter;

@Getter
public class ElevatorDownSign  extends CustomSign {
    private final int elevatorIndex;
    private final int downIndex;

    public ElevatorDownSign() {
        super(Main.getInstance().getConfig().getStringList("SIGNS_CONFIG.DOWN_SIGN.LINES"));
        this.elevatorIndex = this.getIndex("elevator");
        this.downIndex = this.getIndex("down");
    }

    @Override
    public void onClick(Player player, BlockEntitySign sign) {
        Location signLocation = sign.getLocation();
        Location playerLocation = player.getLocation();
        Block signBlock = signLocation.getLevelBlock();
        Block playerBlock = player.getTargetBlock(10);
        if (GlitchListener.getHitCooldown().hasCooldown(player)) {
            return;
        }
        if (signBlock.getSide(BlockFace.DOWN).getId() == Block.AIR && signBlock.getSide(BlockFace.DOWN, 2).getId() == Block.AIR) {
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("CUSTOM_SIGNS.ELEVATOR_SIGNS.INVALID_SIGN")));
            return;
        }
        for (int i = highestBlockAt(signLocation.getFloorX(), signLocation.getFloorY()); i >= 0; --i) {
            if (i == 0) {
                player.sendMessage(TextFormat.colorize(LanguageUtils.getString("CUSTOM_SIGNS.ELEVATOR_SIGNS.CANNOT_FIND_LOCATION")));
                break;
            }
            Block blockAt = signLocation.getLevel().getBlock(signLocation.getFloorX(), i, signLocation.getFloorZ());
            Block relative = blockAt.getSide(BlockFace.DOWN);
            if (blockAt.getId() == Block.AIR && relative.getId() == Block.AIR) {
                Location location = blockAt.getLocation().add(0.5, 0.0, 0.5);
                // location.setYaw(playerLocation.getYaw());
                // location.setPitch(playerLocation.getPitch());
                player.teleport(location);
                break;
            }
        }
    }

    public int highestBlockAt(int x, int z) {
        Level level = Server.getInstance().getDefaultLevel();
        int y = 128;

        while (y > 0 && level.getBlockIdAt(x, y, z) == Block.AIR) {
            y--;
        }

        return y + 3;
    }

}