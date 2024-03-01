package itoozh.core.pvpclass.type.miner;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.math.BlockFace;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.pvpclass.PvPClass;
import itoozh.core.pvpclass.PvPClassManager;
import itoozh.core.session.Session;
import itoozh.core.util.ItemUtil;
import itoozh.core.util.LanguageUtils;
import lombok.Getter;

import java.util.*;

@Getter
public class MinerClass extends PvPClass implements Listener {

    private final List<BlockFace> faces;
    private final List<UUID> invisible;
    private final Map<Integer, Effect> minerEffects;
    private final int minerInvisibilityLevel;

    public MinerClass(PvPClassManager manager) {
        super(manager, "Miner");
        this.minerEffects = new HashMap<>();
        this.invisible = new ArrayList<>();
        this.minerInvisibilityLevel = Main.getInstance().getConfig().getInt("MINER_CLASS.MINER_INVISIBILITY");
        this.faces = Arrays.asList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN);
        this.load();
    }

    private void checkInvis(Player player) {
        int y = player.getLocation().getFloorY();
        if (!this.invisible.contains(player.getUniqueId()) && y <= this.minerInvisibilityLevel) {
            if (player.hasEffect(Effect.INVISIBILITY)) {
                return;
            }
            player.addEffect(Effect.getEffect(Effect.INVISIBILITY).setDuration(Integer.MAX_VALUE));
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_CLASSES.MINER_CLASS.INVIS_ENABLED")));
            this.invisible.add(player.getUniqueId());
        } else if (this.invisible.contains(player.getUniqueId()) && y > this.minerInvisibilityLevel) {
            if (!player.hasEffect(Effect.INVISIBILITY)) {
                return;
            }
            player.removeEffect(Effect.INVISIBILITY);
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_CLASSES.MINER_CLASS.INVIS_DISABLED")));
            this.invisible.remove(player.getUniqueId());
        }
    }

    @Override
    public void load() {
        Main.getInstance().getConfig().getSection("MINER_CLASS.MINER_EFFECTS").getKeys(false).forEach(s -> {
            Integer i = Integer.valueOf(s);
            String effect = Main.getInstance().getConfig().getString("MINER_CLASS.MINER_EFFECTS." + s);
            this.minerEffects.put(i, ItemUtil.getEffect(effect));
        });
    }

    @Override
    public void handleUnequip(Player player) {
        if (this.invisible.contains(player.getUniqueId())) {
            this.invisible.remove(player.getUniqueId());
            player.removeEffect(Effect.INVISIBILITY);
        }
    }

    @Override
    public void handleEquip(Player player) {
        this.checkInvis(player);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getFrom().getFloorY() == event.getTo().getFloorY()) {
            return;
        }
        if (!this.players.contains(player.getUniqueId())) {
            return;
        }
        this.checkInvis(player);
    }

    @Override
    public void removeEffects(Player player) {
        super.removeEffects(player);
        int diamonds = Main.getInstance().getSessionManager().getSessionByUUID(player.getUniqueId()).getDiamonds();
        for (Integer i : this.minerEffects.keySet()) {
            if (i > diamonds) {
                continue;
            }
            player.removeEffect(this.minerEffects.get(i).getId());
        }
    }

    @Override
    public void addEffects(Player player) {
        super.addEffects(player);
        int diamonds = Main.getInstance().getSessionManager().getSessionByUUID(player.getUniqueId()).getDiamonds();
        for (Integer i : this.minerEffects.keySet()) {
            if (i > diamonds) {
                continue;
            }
            Main.getInstance().getPvPClassManager().addEffect(player, this.minerEffects.get(i));
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) throws Exception {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (block.getId() != Block.DIAMOND_ORE) {
            return;
        }
        Session user = Main.getInstance().getSessionManager().getSessionByUUID(player.getUniqueId());
        user.setDiamonds(user.getDiamonds() + 1);
        MinerClass minerClass = Main.getInstance().getPvPClassManager().getMinerClass();
        if (minerClass.getPlayers().contains(player.getUniqueId())) {
            minerClass.addEffects(player);
        }
        if (block.hasMetadata("exception")) {
            block.removeMetadata("exception", Main.getInstance());
            return;
        }
        block.setMetadata("exception", new MetadataValue(Main.getInstance()) {
            @Override
            public Object value() {
                return null;
            }

            @Override
            public void invalidate() {

            }
        });
        String s = TextFormat.colorize(LanguageUtils.getString("DIAMOND_LISTENER.FD_MESSAGE").replaceAll("%player%", event.getPlayer().getName()).replaceAll("%amount%", String.valueOf(this.count(block))));
        for (Player online : Server.getInstance().getOnlinePlayers().values()) {
            Session userOnline = Main.getInstance().getSessionManager().getSessionByUUID(online.getUniqueId());
            if (userOnline.isFoundDiamondAlerts()) {
                online.sendMessage(s);
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) throws Exception {
        Block block = event.getBlock();
        if (block.getId() == Block.DIAMOND_ORE) {
            block.setMetadata("exception", new MetadataValue(Main.getInstance()) {
                @Override
                public Object value() {
                    return null;
                }

                @Override
                public void invalidate() {

                }
            });
        }
    }

    private int count(Block block) throws Exception {
        int i = 1;
        for (BlockFace face : this.faces) {
            Block relative = block.getSide(face);
            if (relative.hasMetadata("exception")) {
                continue;
            }
            if (relative.getId() != Block.DIAMOND_ORE) {
                continue;
            }
            relative.setMetadata("exception", new MetadataValue(Main.getInstance()) {
                @Override
                public Object value() {
                    return null;
                }

                @Override
                public void invalidate() {

                }
            });
            i += this.count(relative);
        }
        return i;
    }
}
