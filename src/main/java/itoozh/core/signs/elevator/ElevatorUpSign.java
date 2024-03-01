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
public class ElevatorUpSign extends CustomSign {
    private final int elevatorIndex;
    private final int upIndex;

    public ElevatorUpSign() {
        super(Main.getInstance().getConfig().getStringList("SIGNS_CONFIG.UP_SIGN.LINES"));
        this.elevatorIndex = this.getIndex("elevator");
        this.upIndex = this.getIndex("up");
    }

    @Override
    public void onClick(Player player, BlockEntitySign sign) {
        Location signLocation = sign.getLocation().clone();
        Block signBlock = signLocation.getLevelBlock().clone();
        if (GlitchListener.getHitCooldown().hasCooldown(player)) {
            return;
        }
        if (signBlock.getSide(BlockFace.UP).getId() == Block.AIR && signBlock.getSide(BlockFace.UP, 2).getId() == Block.AIR) {
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("CUSTOM_SIGNS.ELEVATOR_SIGNS.INVALID_SIGN")));
            return;
        }
        for (int i = signLocation.getFloorY(); i <= highestBlockAt(signLocation.getFloorX(), signLocation.getFloorY()); ++i) {
            if (i == highestBlockAt(signLocation.getFloorX(), signLocation.getFloorY())) {
                player.sendMessage(TextFormat.colorize(LanguageUtils.getString("CUSTOM_SIGNS.ELEVATOR_SIGNS.CANNOT_FIND_LOCATION")));
                break;
            }
            Block block = signLocation.getLevel().getBlock(signLocation.getFloorX(), i, signLocation.getFloorZ());
            Block relative = block.getSide(BlockFace.UP);
            if (block.getId() == Block.AIR && relative.getId() == Block.AIR) {
                Location location = block.getLocation().add(0.5, 0.0, 0.5);
                // location.setYaw(yaw);
                // location.setPitch(pitch);
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
