package itoozh.core.team.listener;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.*;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.item.Item;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.session.Session;
import itoozh.core.session.procces.ProcessType;
import itoozh.core.session.procces.type.ClaimProcess;
import itoozh.core.team.Team;
import itoozh.core.team.claim.Claim;
import itoozh.core.team.claim.ClaimType;
import itoozh.core.util.Cooldown;
import itoozh.core.util.LanguageUtils;

import java.util.Objects;

public class ClaimListener implements Listener {

    private final Cooldown cooldown;

    public ClaimListener() {
        this.cooldown = new Cooldown(Main.getInstance());
    }

    @EventHandler
    public void on(PlayerInteractEvent ev) {
        Player player = ev.getPlayer();
        Session session = Main.getInstance().getSessionManager().getSession(player);

        int separator = Main.getInstance().getConfig().getInt("CLAIMING.CLAIM_SEPARATOR", 2);

        int minSize = Main.getInstance().getConfig().getInt("CLAIMING.MIN_SIZE");
        int maxSize = Main.getInstance().getConfig().getInt("CLAIMING.MAX_SIZE");

        if (session.getProcess() == null) return;

        if (Objects.requireNonNull(session.getProcess().getProcessType()) == ProcessType.CLAIM_PROCESS) {
            ClaimProcess claimProcess = (ClaimProcess) session.getProcess();
            if (Objects.requireNonNull(claimProcess.state) == ClaimProcess.State.claiming) {
                if (!player.getInventory().getItemInHand().equals(Main.getInstance().getTeamManager().getClaimManager().item)) return;
                ev.setCancelled();
                if (ev.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
                    if (ev.getBlock() == null) {
                        return;
                    }
                    if (this.cooldown.hasCooldown(player)) {
                        return;
                    }
                    if (claimProcess.claimType == ClaimType.TEAM) {
                        Claim claim = Main.getInstance().getTeamManager().getClaimManager().findClaim((int) ev.getBlock().x, (int) ev.getBlock().z, separator);
                        if (claim != null) {
                            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CLAIM.CLAIMS_TOUCHING")));
                            return;
                        }
                        if (claimProcess.secondPos != null) {
                            if (claimProcess.secondPos.distance(ev.getBlock().asBlockVector3().asVector3()) < minSize) {
                                player.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CLAIM.SIZE_SMALL").replaceAll("%size%", minSize + "x" + minSize)));
                                return;
                            }
                            if (claimProcess.secondPos.distance(ev.getBlock().asBlockVector3().asVector3()) > maxSize) {
                                player.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CLAIM.SIZE_LARGE").replaceAll("%size%", maxSize + "x" + maxSize)));
                                return;
                            }
                        }
                    }
                    clearAllPillars(player, claimProcess);
                    claimProcess.resetExpireAt();
                    this.cooldown.applyCooldownTicks(player, 25);
                    if (claimProcess.secondPos != null) {
                        claimProcess.firstPos = ev.getBlock().asBlockVector3().asVector3();

                        buildPillar(player, (int) ev.getBlock().x, (int) ev.getBlock().y, (int) ev.getBlock().z, GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, BlockID.DIAMOND_BLOCK, 0),  GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, BlockID.GLASS, 0));
                        buildPillar(player, (int) claimProcess.secondPos.x, (int) claimProcess.secondPos.y, (int) claimProcess.secondPos.z, GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, BlockID.DIAMOND_BLOCK, 0),  GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, BlockID.GLASS, 0));

                        buildPillar(claimProcess.getPlayer(), (int) claimProcess.firstPos.x, (int) claimProcess.firstPos.y, (int) claimProcess.secondPos.z, GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, BlockID.DIAMOND_BLOCK, 0),  GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, BlockID.GLASS, 0));
                        buildPillar(claimProcess.getPlayer(), (int) claimProcess.secondPos.x, (int) claimProcess.firstPos.y, (int) claimProcess.firstPos.z, GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, BlockID.DIAMOND_BLOCK, 0),  GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, BlockID.GLASS, 0));

                        Main.getInstance().getTeamManager().getClaimManager().tryClaiming(claimProcess);
                        return;
                    }
                    claimProcess.firstPos = ev.getBlock().asBlockVector3().asVector3();
                    buildPillar(player, (int) ev.getBlock().x, (int) ev.getBlock().y, (int) ev.getBlock().z, GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, BlockID.DIAMOND_BLOCK, 0),  GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, BlockID.GLASS, 0));
                    player.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CLAIM.FIRST_CORNER")));
                } else if (ev.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
                    if (ev.getBlock() == null) {
                        return;
                    }
                    if (this.cooldown.hasCooldown(player)) {
                        return;
                    }
                    if (claimProcess.claimType == ClaimType.TEAM) {
                        Claim claim2 = Main.getInstance().getTeamManager().getClaimManager().findClaim((int) ev.getBlock().x, (int) ev.getBlock().z, separator);
                        if (claim2 != null) {
                            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CLAIM.CLAIMS_TOUCHING")));
                            return;
                        }
                        if (claimProcess.firstPos != null) {
                            if (claimProcess.firstPos.distance(ev.getBlock().asBlockVector3().asVector3()) < minSize) {
                                player.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CLAIM.SIZE_SMALL").replaceAll("%size%", minSize + "x" + minSize)));
                                return;
                            }
                            if (claimProcess.firstPos.distance(ev.getBlock().asBlockVector3().asVector3()) > maxSize) {
                                player.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CLAIM.SIZE_LARGE").replaceAll("%size%", maxSize + "x" + maxSize)));
                                return;
                            }
                        }
                    }
                    clearAllPillars(player, claimProcess);
                    if (claimProcess.secondPos != null) {
                        clearPillar(player, (int) claimProcess.secondPos.x, (int) claimProcess.secondPos.y, (int) claimProcess.secondPos.z);
                    }
                    claimProcess.resetExpireAt();
                    this.cooldown.applyCooldownTicks(player, 25);
                    if (claimProcess.firstPos != null) {
                        claimProcess.secondPos = ev.getBlock().asBlockVector3().asVector3();

                        buildPillar(player, (int) ev.getBlock().x, (int) ev.getBlock().y, (int) ev.getBlock().z, GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, BlockID.DIAMOND_BLOCK, 0),  GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, BlockID.GLASS, 0));
                        buildPillar(player, (int) claimProcess.firstPos.x, (int) claimProcess.firstPos.y, (int) claimProcess.firstPos.z, GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, BlockID.DIAMOND_BLOCK, 0),  GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, BlockID.GLASS, 0));

                        buildPillar(claimProcess.getPlayer(), (int) claimProcess.secondPos.x, (int) claimProcess.secondPos.y, (int) claimProcess.firstPos.z, GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, BlockID.DIAMOND_BLOCK, 0),  GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, BlockID.GLASS, 0));
                        buildPillar(claimProcess.getPlayer(), (int) claimProcess.firstPos.x, (int) claimProcess.secondPos.y, (int) claimProcess.secondPos.z, GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, BlockID.DIAMOND_BLOCK, 0),  GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, BlockID.GLASS, 0));

                        Main.getInstance().getTeamManager().getClaimManager().tryClaiming(claimProcess);
                        return;
                    }
                    claimProcess.secondPos = ev.getBlock().asBlockVector3().asVector3();
                    buildPillar(player, (int) ev.getBlock().x, (int) ev.getBlock().y, (int) ev.getBlock().z, GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, BlockID.DIAMOND_BLOCK, 0),  GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, BlockID.GLASS, 0));
                    player.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CLAIM.SECOND_CORNER")));
                }
            }
        }
    }

    @EventHandler
    public void on(PlayerChatEvent ev) {
        Player player = ev.getPlayer();
        Session session = Main.getInstance().getSessionManager().getSession(player);

        if (session.getProcess() == null) return;

        if (Objects.requireNonNull(session.getProcess().getProcessType()) == ProcessType.CLAIM_PROCESS) {
            ClaimProcess claimProcess = (ClaimProcess) session.getProcess();
            if (Objects.requireNonNull(claimProcess.state) == ClaimProcess.State.claiming) {
                if (ev.getMessage().equalsIgnoreCase("yes") || ev.getMessage().equalsIgnoreCase("accept")) {
                    ev.setCancelled(true);
                    if (claimProcess.firstPos == null || claimProcess.secondPos == null) {
                        player.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CLAIM.INSUFFICIENT_SELECTIONS")));
                        return;
                    }
                    if (Objects.requireNonNull(claimProcess.claimType) == ClaimType.TEAM) {
                        Team team = session.getTeam();
                        if (team == null) {
                            claimProcess.stop();
                            session.setProcess(null);
                            return;
                        }
                        if (team.getBalance() < claimProcess.price) {
                            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CLAIM.INSUFFICIENT_BALANCE")));
                        } else {
                            if (Main.getInstance().getTeamManager().getClaimManager().createClaim(claimProcess)) {
                                team.setBalance(team.getBalance() - claimProcess.price);
                                player.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CLAIM.PURCHASED_CLAIM")
                                        .replaceAll("%balance%", String.valueOf(team.getBalance()))
                                        .replaceAll("%price%", String.valueOf(claimProcess.price))));
                            }
                        }
                    } else {
                        Main.getInstance().getTeamManager().getClaimManager().createClaim(claimProcess);
                    }
                    if (player.getInventory().contains(Main.getInstance().getTeamManager().getClaimManager().item)) {
                        player.getInventory().remove(Main.getInstance().getTeamManager().getClaimManager().item);
                    }
                    clearAllPillars(player, claimProcess);
                    claimProcess.stop();
                    session.setProcess(null);
                } else if (ev.getMessage().equalsIgnoreCase("cancel")) {
                    ev.setCancelled(true);
                    clearAllPillars(player, claimProcess);
                    claimProcess.stop();
                    session.setProcess(null);
                    if (player.getInventory().contains(Main.getInstance().getTeamManager().getClaimManager().item)) {
                        player.getInventory().remove(Main.getInstance().getTeamManager().getClaimManager().item);
                    }
                    player.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CLAIM.CANCELLED_SELECTION")));
                }
            }
        }
    }

    public void clearAllPillars(Player player, ClaimProcess process) {
        if (process.firstPos != null ){
            clearPillar(player, (int) process.firstPos.x, (int) process.firstPos.y, (int) process.firstPos.z);
            if (process.secondPos != null ){
                clearPillar(player, (int) process.secondPos.x, (int) process.secondPos.y, (int) process.secondPos.z);
                clearPillar(player, (int) process.firstPos.x, (int) process.firstPos.y, (int) process.secondPos.z);
                clearPillar(process.getPlayer(), (int) process.secondPos.x, (int) process.firstPos.y, (int) process.firstPos.z);
            }
        }
    }



    public void clearPillar(Player player, int x, int y, int z) {
        for (int i = highestBlockAt(x, z); i < 128; i++) {

            if (player.getLevel().getBlock(x, i, z).equals(new BlockAir())) return;

            UpdateBlockPacket packet = new UpdateBlockPacket();
            packet.x = x;
            packet.y = i;
            packet.z = z;
            packet.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, BlockID.AIR, 0);

            player.dataPacket(packet);
        }
    }

    public int highestBlockAt(int x, int z) {
        Level level = Server.getInstance().getDefaultLevel();
        int y = 128;

        while (y > 0 && level.getBlockIdAt(x, y, z) == Block.AIR) {
            y--;
        }

        return y + 1;
    }



    public void buildPillar(Player player, int x, int y, int z, int firstBlockId, int secondBlockId) {
        int blocks = 0;
        for (int i = highestBlockAt(x, z); i < 128; i++) {

            if (player.getLevel().getBlock(x, i, z).equals(new BlockAir())) return;

            UpdateBlockPacket packet = new UpdateBlockPacket();
            packet.x = x;
            packet.y = i;
            packet.z = z;
            if (blocks == 4) {
                packet.blockRuntimeId = firstBlockId;
                blocks = 0;
            } else {
                packet.blockRuntimeId = secondBlockId;
            }
            player.dataPacket(packet);

            blocks++;
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.getUniqueId() == null) return;
        Session session = Main.getInstance().getSessionManager().getSessionByUUID(player.getUniqueId());
        if (session == null) return;
        if (session.getProcess() == null) return;
        if (session.getProcess() instanceof ClaimProcess) {
            ClaimProcess claimProcess = (ClaimProcess) session.getProcess();
            claimProcess.stop();
            session.setProcess(null);
        }
    }


    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItem();
        if (item.equals(Main.getInstance().getTeamManager().getClaimManager().item)) {
            event.setCancelled();
            player.getInventory().setItemInHand(Item.get(0));
            Session session = Main.getInstance().getSessionManager().getSession(player);
            if (session.getProcess() instanceof ClaimProcess) {
                ClaimProcess claimProcess = (ClaimProcess) session.getProcess();
                clearAllPillars(player, claimProcess);
                claimProcess.stop();
                session.setProcess(null);
                if (player.getInventory().contains(Main.getInstance().getTeamManager().getClaimManager().item)) {
                    player.getInventory().remove(Main.getInstance().getTeamManager().getClaimManager().item);
                }
                player.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CLAIM.CANCELLED_SELECTION")));
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().contains(Main.getInstance().getTeamManager().getClaimManager().item)) {
            player.getInventory().remove(Main.getInstance().getTeamManager().getClaimManager().item);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInHand().equals(Main.getInstance().getTeamManager().getClaimManager().item)) {
            event.setCancelled();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getFrom().getFloorX() == event.getTo().getFloorX() && event.getFrom().getFloorZ() == event.getTo().getFloorZ()) {
            return;
        }
        this.checkClaim(event.getPlayer(), event.getTo(), event.getFrom());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getFrom().getFloorX() == event.getTo().getFloorX() && event.getFrom().getFloorZ() == event.getTo().getFloorZ()) {
            return;
        }
        this.checkClaim(event.getPlayer(), event.getTo(), event.getFrom());
    }

    private void checkClaim(Player player, Location pos1, Location pos2) {
        Claim claim1 = Main.getInstance().getTeamManager().getClaimManager().findClaimPerType(pos1.getFloorX(), pos1.getFloorZ());
        Claim claim2 = Main.getInstance().getTeamManager().getClaimManager().findClaimPerType(pos2.getFloorX(), pos2.getFloorZ());

        Claim wilderness = new Claim(ClaimType.WILDERNESS, "Wilderness", new Vector3(0, 0, 0), new Vector3(0, 0, 0));

        if (claim1 == null) {
            claim1 = wilderness;
        }
        if (claim2 == null) {
            claim2 = wilderness;
        }
        if (claim1 == claim2) {
            return;
        }

        for (String s : LanguageUtils.splitStringToList(LanguageUtils.getString("TEAM_LISTENER.CLAIM_MESSAGE.MESSAGE"))) {
            player.sendMessage(TextFormat.colorize(s.replaceAll("%to-team%", claim1.getNameFormat(player)).replaceAll("%from-team%", claim2.getNameFormat(player)).replaceAll("%to-deathban%", claim1.getType() == ClaimType.SPAWN ? LanguageUtils.getString("TEAM_LISTENER.CLAIM_MESSAGE.DEATHBAN_FORMAT.NON_DEATHBAN") : LanguageUtils.getString("TEAM_LISTENER.CLAIM_MESSAGE.DEATHBAN_FORMAT.DEATHBAN")).replaceAll("%from-deathban%", claim2.getType() == ClaimType.SPAWN ? LanguageUtils.getString("TEAM_LISTENER.CLAIM_MESSAGE.DEATHBAN_FORMAT.NON_DEATHBAN") : LanguageUtils.getString("TEAM_LISTENER.CLAIM_MESSAGE.DEATHBAN_FORMAT.DEATHBAN"))));
        }
    }

}
