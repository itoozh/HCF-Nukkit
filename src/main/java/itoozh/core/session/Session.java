package itoozh.core.session;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.*;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.Level;
import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.crate.Crate;
import itoozh.core.ranks.Rank;
import itoozh.core.scoreboard.Scoreboard;
import itoozh.core.session.procces.Process;
import itoozh.core.session.settings.ChatSettings;
import itoozh.core.session.settings.TeamListSettings;
import itoozh.core.team.Team;
import itoozh.core.team.claim.Claim;
import itoozh.core.team.listener.ClaimListener;
import itoozh.core.team.player.Member;
import itoozh.core.util.LanguageUtils;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class Session {

    private UUID uuid;

    private boolean foundDiamondAlerts = true;

    public Crate placingCrate = null;

    public boolean removingCrate = false;
    private Process process = null;
    @Getter
    private boolean scoreboardActive = true;
    @Getter
    private boolean scoreboardClaim = true;
    @Getter
    private Scoreboard scoreboard = null;
    private int kills = 0;
    private int diamonds = 0;
    private int deaths = 0;
    private int balance = 200;
    private int killStreak = 0;
    private int highestKillStreak = 0;

    private Rank rank;

    private boolean publicChat = true;

    private boolean mapShown = false;

    public Claim getCurrentClaim() {
        return currentClaim;
    }

    public long getKDR() {
        if (deaths == 0) {
            return kills * 100L;
        } else {
            float kdr = ((float) kills) / deaths;
            return (long) (kdr * 100);
        }
    }

    public void setCurrentClaim(Claim currentClaim) {
        this.currentClaim = currentClaim;
    }

    private Claim currentClaim = null;

    private ChatSettings chatSettings = ChatSettings.PUBLIC;

    private TeamListSettings teamListSettings = TeamListSettings.ONLINE_HIGH;

    private Team team = null;

    public Session(UUID uuid) {
        this.uuid = uuid;
        this.rank = Main.getInstance().getRankManager().getDefaultRank();
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public Member getMember() {
        Team team = this.getTeam();
        if (team == null) {
            return null;
        }

        List<Member> members = team.getMembers();

        for (Member member : members) {
            if (member.getUniqueID().equals(this.uuid)) {
                return member;
            }
        }

        return null;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getKDRString() {
        return String.format("%.2f", (double) getKDR() / 100);
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getKillStreak() {
        return killStreak;
    }

    public void setKillStreak(int killStreak) {
        this.killStreak = killStreak;
    }

    public int getHighestKillStreak() {
        return highestKillStreak;
    }

    public ChatSettings getChatSettings() {
        return chatSettings;
    }

    public void setChatSettings(ChatSettings chatSettings) {
        this.chatSettings = chatSettings;
    }

    public void setHighestKillStreak(int highestKillStreak) {
        this.highestKillStreak = highestKillStreak;
    }

    public void addKill() {
        kills++;
        killStreak++;
        if (killStreak > highestKillStreak) {
            highestKillStreak = killStreak;
        }
    }

    public void addDeath() {
        deaths++;
        killStreak = 0;
    }

    public void giveBalance(int amount) {
        balance += amount;
    }

    public void takeBalance(int amount) {
        balance -= amount;
    }

    private TaskHandler mapTask = null;
    public void showMap(Player player) {

        Block[] blocks = new Block[] {
                new BlockTerracottaGlazedPurple(),
                new BlockDiamond(),
                new BlockGold(),
                new BlockTerracottaGlazedYellow(),
                new BlockEmerald(),
                new BlockBricks(),
                new BlockTerracottaGlazedGreen(),
                new BlockTNT(),
                new BlockIron(),
                new BlockTerracottaGlazedPink(),
                new BlockRedstone(),
                new BlockWood(),
                new BlockTerracottaGlazedLightBlue(),
                new BlockStone(),
                new BlockTerracottaGlazedRed(),
                new BlockPiston(),
                new BlockLapis()
        };

        int radius = Main.getInstance().getConfig().getInt("TEAM_MAP.RADIUS");

        int lastId = 0;
        ArrayList<Claim> claims =Main.getInstance().getTeamManager().getClaimManager().claims(player.getFloorX() - radius, player.getFloorX() + radius, player.getFloorZ() - radius, player.getFloorZ() + radius);

        if (claims.isEmpty()) {
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_MAP.NO_TEAMS").replaceAll("%radius%", String.valueOf(radius))));
            return;
        }

        ArrayList<String> list = new ArrayList<>();
        for (Claim claim : claims) {
            new ClaimListener().buildPillar(player, claim.getX1(), highestBlockAt(claim.getX1(), claim.getZ1()), claim.getZ1(), GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, blocks[lastId].getId(), 0), GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, BlockID.GLASS, 0));
            new ClaimListener().buildPillar(player, claim.getX2(), highestBlockAt(claim.getX2(), claim.getZ2()), claim.getZ2(), GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, blocks[lastId].getId(), 0), GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, BlockID.GLASS, 0));
            new ClaimListener().buildPillar(player, claim.getX1(), highestBlockAt(claim.getX1(), claim.getZ2()), claim.getZ2(), GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, blocks[lastId].getId(), 0), GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, BlockID.GLASS, 0));
            new ClaimListener().buildPillar(player, claim.getX2(), highestBlockAt(claim.getX2(), claim.getZ1()), claim.getZ1(), GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, blocks[lastId].getId(), 0), GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, BlockID.GLASS, 0));
            list.add(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_MAP.CLAIM_FORMAT").replaceAll("%material%", String.valueOf(blocks[lastId].getName())).replaceAll("%team%", claim.getNameFormat(player))));
            lastId++;
        }
        for (String s : LanguageUtils.splitStringToList(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_MAP.MAP_SHOWN")))) {
            if (!s.equalsIgnoreCase("%claims%")) {
                player.sendMessage(s);
                continue;
            }
            for (String ss : list) {
                player.sendMessage(ss);
            }
            list.clear();
        }

        mapShown = true;
        mapTask = player.getServer().getScheduler().scheduleDelayedTask(Main.getInstance(), () -> {
            hideMap(player);
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_MAP.MAP_HIDDEN")));
        }, 20 * 30);
    }

    public int highestBlockAt(int x, int z) {
        Level level = Server.getInstance().getDefaultLevel();
        int y = 128;

        while (y > 0 && level.getBlockIdAt(x, y, z) == Block.AIR) {
            y--;
        }

        return y + 1;
    }

    public void hideMap(Player player) {
        if (mapTask != null) {
            mapTask.cancel();
            mapTask = null;
        }
        for (Claim claim : Main.getInstance().getTeamManager().getClaimManager().claims(player.getFloorX() - 50, player.getFloorX() + 50, player.getFloorZ() - 50, player.getFloorZ() + 50)) {
            new ClaimListener().clearPillar(player, claim.getX1(), highestBlockAt(claim.getX1(), claim.getZ1()), claim.getZ1());
            new ClaimListener().clearPillar(player, claim.getX2(), highestBlockAt(claim.getX2(), claim.getZ2()), claim.getZ2());
            new ClaimListener().clearPillar(player, claim.getX1(), highestBlockAt(claim.getX1(), claim.getZ2()), claim.getZ2());
            new ClaimListener().clearPillar(player, claim.getX2(), highestBlockAt(claim.getX2(), claim.getZ1()), claim.getZ1());
        }
        mapShown = false;
    }

    public boolean isMapShown() {
        return mapShown;
    }

    public void setMapShown(boolean mapShown) {
        this.mapShown = mapShown;
    }

    public TeamListSettings getTeamListSettings() {
        return teamListSettings;
    }

    public void setTeamListSettings(TeamListSettings teamListSettings) {
        this.teamListSettings = teamListSettings;
    }

    public String getPrefix(Player player) {

        List<Session> users = new ArrayList<>(Main.getInstance().getSessionManager().getSessions().values());
        users.sort(Comparator.comparingInt(Session::getKills).reversed());

        int kills = users.stream().limit(20L).collect(Collectors.toList()).indexOf(Main.getInstance().getSessionManager().getSessionByUUID(player.getUniqueId()));
        switch (kills) {
            case 0: {
                return Main.getInstance().getConfig().getString("LUNAR_PREFIXES.PLAYER.ONE");
            }
            case 1: {
                return Main.getInstance().getConfig().getString("LUNAR_PREFIXES.PLAYER.TWO");
            }
            case 2: {
                return Main.getInstance().getConfig().getString("LUNAR_PREFIXES.PLAYER.THREE");
            }
            default: {
                return null;
            }
        }
    }

    public void setScoreboardActive(boolean scoreboardActive) {
        this.scoreboardActive = scoreboardActive;
    }

    public void setScoreboardClaim(boolean scoreboardClaim) {
        this.scoreboardClaim = scoreboardClaim;
    }

    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public int getDiamonds() {
        return diamonds;
    }

    public void setDiamonds(int diamonds) {
        this.diamonds = diamonds;
    }

    public boolean isFoundDiamondAlerts() {
        return foundDiamondAlerts;
    }

    public void setFoundDiamondAlerts(boolean foundDiamondAlerts) {
        this.foundDiamondAlerts = foundDiamondAlerts;
    }

    public boolean isPublicChat() {
        return publicChat;
    }

    public void setPublicChat(boolean publicChat) {
        this.publicChat = publicChat;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }
}
