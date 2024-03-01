package itoozh.core.team;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Location;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.session.Session;
import itoozh.core.team.claim.Claim;
import itoozh.core.team.player.Member;
import itoozh.core.team.player.Role;
import itoozh.core.util.Formatter;
import itoozh.core.util.LanguageUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class Team {

    private UUID leader;
    private Claim claim = null;
    private Location rallyPoint = null;
    private String name;
    private Date createdAt;
    private Location hq = null;
    private int balance = 0;
    private int points = 0;
    private int kills = 0;
    private int deaths = 0;
    private double dtr = 1.1;
    private int kothCaptures = 0;
    private boolean raidable = false;
    private boolean open = false;
    private boolean minuteRegen = false;
    private Team focusedTeam = null;
    private List<Member> members = new ArrayList<>();
    private List<UUID> invitedPlayers = new ArrayList<>();


    public Team(String name, UUID leader) {
        this.leader = leader;
        this.name = name;
        this.createdAt = new Date();
    }

    public boolean checkRole(Player player, Role role) {
        Session playerSession = Main.getInstance().getSessionManager().getSession(player);
        return playerSession.getMember().getRole().ordinal() >= role.ordinal();
    }

    public void broadcast(String message) {
        for (Member member : members) {
            IPlayer player = Server.getInstance().getOfflinePlayer(member.getUniqueID());
            if (player.isOnline()) {
                player.getPlayer().sendMessage(TextFormat.colorize(message));
            }
        }
    }

    public Member getMember(UUID uuid) {
        for (Member member : this.members) {
            if (member.getUniqueID().equals(uuid)) {
                return member;
            }
        }
        return null;
    }

    public boolean isRaidable() {
        return this.getDtr() < 0.0;
    }

    public void addMember(Member member) {
        members.add(member);
    }

    public double getMaxDtr() {
        if (this.getMembers().size() == 1) {
            return Main.getInstance().getConfig().getDouble("TEAM_DTR.SOLO_DTR");
        }
        double dtrPerPlayer = this.getMembers().size() * Main.getInstance().getConfig().getDouble("TEAM_DTR.PER_PLAYER");
        double maxDtr = Main.getInstance().getConfig().getDouble("TEAM_DTR.MAX_DTR");
        return Math.min(dtrPerPlayer, maxDtr);
    }

    private String makeMemberNice(UUID uuid) {
        IPlayer player = Server.getInstance().getOfflinePlayer(uuid);
        Session playerSession = Main.getInstance().getSessionManager().getSessionByUUID(uuid);
        return player.isOnline() ? TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_INFO.MEMBER_FORMAT.ONLINE").replaceAll("%player%", player.getName()).replaceAll("%kills%", String.valueOf(playerSession.getKills()))) : TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_INFO.MEMBER_FORMAT.OFFLINE").replaceAll("%player%", player.getName()).replaceAll("%kills%", String.valueOf(playerSession.getKills())));
    }

    public List<String> getTeamInfo(CommandSender sender) {
        List<String> coLeaders = this.members.stream().filter(m -> m.getRole() == Role.CO_LEADER).map(m -> this.makeMemberNice(m.getUniqueID())).collect(Collectors.toList());
        List<String> captains = this.members.stream().filter(m -> m.getRole() == Role.CAPTAIN).map(m -> this.makeMemberNice(m.getUniqueID())).collect(Collectors.toList());
        List<String> members = this.members.stream().filter(m -> m.getRole() == Role.MEMBER).map(m -> this.makeMemberNice(m.getUniqueID())).collect(Collectors.toList());
        List<String> lines = LanguageUtils.splitStringToList(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_INFO.FORMAT")));
        lines.removeIf(s -> {
            if (s.contains("%co-leaders%") && coLeaders.isEmpty()) {
                return true;
            }
            if (s.contains("%captains%") && captains.isEmpty()) {
                return true;
            }
            if (s.contains("%members%") && members.isEmpty()) {
                return true;
            }
            return s.contains("%regen%") && !this.hasRegen();
        });
        lines.replaceAll(s -> s.replaceAll("%balance%", String.valueOf(this.balance))
                .replaceAll("%name%", this.name)
                .replaceAll("%online%", String.valueOf(this.getOnlinePlayers().size()))
                .replaceAll("%max-online%", String.valueOf(this.getPlayers().size()))
                .replaceAll("%hq%", this.getHQFormatted())
                .replaceAll("%leader%", this.makeMemberNice(this.leader))
                .replaceAll("%co-leaders%", LanguageUtils.join(coLeaders, ", "))
                .replaceAll("%captains%", LanguageUtils.join(captains, ", "))
                .replaceAll("%members%", LanguageUtils.join(members, ", "))
                .replaceAll("%points%", String.valueOf(this.points))
                .replaceAll("%kothCaptures%", String.valueOf(this.kothCaptures))
                .replaceAll("%balance%", String.valueOf(this.balance))
                .replaceAll("%dtr%", this.getDtrString())
                .replaceAll("%dtr-color%", this.getDtrColor())
                .replaceAll("%dtr-symbol%", this.getDtrSymbol())
                .replaceAll("%regen%", Formatter.formatDetailed(this.getRegen())));
        return lines;
    }

    public String getDtrString() {
        return Formatter.formatDtr(this.dtr);
    }

    public String getDtrColor() {
        if (this.isRaidable()) {
            return TextFormat.colorize(Main.getInstance().getConfig().getString("TEAM_DTR.COLOR.RAIDABLE"));
        }
        if (this.dtr <= Main.getInstance().getConfig().getDouble("TEAM_DTR.LOW_DTR")) {
            return TextFormat.colorize(Main.getInstance().getConfig().getString("TEAM_DTR.COLOR.LOW_DTR"));
        }
        return TextFormat.colorize(Main.getInstance().getConfig().getString("TEAM_DTR.COLOR.NORMAL"));
    }

    public String getDtrSymbol() {
        if (this.minuteRegen) {
            return TextFormat.colorize(Main.getInstance().getConfig().getString("TEAM_DTR.SYMBOL.REGENERATING"));
        }
        if (this.hasRegen()) {
            return TextFormat.colorize(Main.getInstance().getConfig().getString("TEAM_DTR.SYMBOL.FREEZE"));
        }
        return TextFormat.colorize(Main.getInstance().getConfig().getString("TEAM_DTR.SYMBOL.NORMAL"));
    }

    public List<Player> getOnlinePlayers(){
        List<Player> players = new ArrayList<>();
        for (Member member : members){
            IPlayer player = Server.getInstance().getOfflinePlayer(member.getUniqueID());
            if (player.isOnline()){
                players.add(player.getPlayer());
            }
        }
        return players;
    }

    public long getRegen() {
        return Main.getInstance().getTeamManager().getTeamRegenManager().getRemaining(this);
    }

    public boolean hasRegen() {
        return Main.getInstance().getTeamManager().getTeamRegenManager().hasTimer(this);
    }

    public List<UUID> getPlayers(){
        List<UUID> players = new ArrayList<>();
        for (Member member : members){
            players.add(member.getUniqueID());
        }
        return players;
    }

    public String getHQFormatted() {
        if (this.hq == null) {
            return LanguageUtils.getString("TEAM_COMMAND.TEAM_INFO.HQ_FORMAT.NONE");
        }
        return LanguageUtils.getString("TEAM_COMMAND.TEAM_INFO.HQ_FORMAT.SET").replaceAll("%world%", this.hq.getLevel().getName()).replaceAll("%x%", String.valueOf(Math.abs(this.hq.getFloorX()))).replaceAll("%y%", String.valueOf(Math.abs(this.hq.getFloorY()))).replaceAll("%z%", String.valueOf(Math.abs(this.hq.getFloorZ())));
    }

    public String getDisplayColor(Player player) {
        Session session = Main.getInstance().getSessionManager().getSession(player);
        if (session.getTeam() != null && session.getTeam().getFocusedTeam() != null && session.getTeam().getFocusedTeam() == this) {
            return TextFormat.colorize(Main.getInstance().getConfig().getString("RELATION_COLOR.FOCUSED"));
        }
        return TextFormat.colorize(this.getMember(player.getUniqueId()) != null ? Main.getInstance().getConfig().getString("RELATION_COLOR.TEAMMATE") : Main.getInstance().getConfig().getString("RELATION_COLOR.ENEMY"));
    }

    public int getOnlinesSize(){
        return getOnlinePlayers().size();
    }

    public String getDisplayName(Player player) {
        return this.getDisplayColor(player) + name;
    }

    public boolean isFocused(Player player) {
        Session session = Main.getInstance().getSessionManager().getSession(player);
        Team team = session.getTeam();
        if (focusedTeam != null && team != null) {
            return focusedTeam == team;
        }
        return false;
    }

    public void setDtr(double dtr) {
        if (dtr > this.getMaxDtr()) {
            this.dtr = this.getMaxDtr();
        } else {
            this.dtr = Math.max(dtr, -0.9);
        }
        Main.getInstance().getNameTags().update();
    }

    public String getTeamPosition() {

        Map<Integer, List<Team>> map = new HashMap<>();

        List<Team> teams = new ArrayList<>(Main.getInstance().getTeamManager().getTeams().values());
        teams.sort(Comparator.comparingInt(Team::getPoints).reversed());

        int pos = teams.stream().limit(3L).collect(Collectors.toList()).indexOf(this);
        switch (pos) {
            case 0: {
                return Main.getInstance().getConfig().getString("LUNAR_PREFIXES.TEAMS.ONE");
            }
            case 1: {
                return Main.getInstance().getConfig().getString("LUNAR_PREFIXES.TEAMS.TWO");
            }
            case 2: {
                return Main.getInstance().getConfig().getString("LUNAR_PREFIXES.TEAMS.THREE");
            }
            default: {
                return null;
            }
        }
    }
}
