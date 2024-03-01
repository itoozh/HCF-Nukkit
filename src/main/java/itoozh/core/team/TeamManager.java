package itoozh.core.team;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.session.Session;
import itoozh.core.session.settings.ChatSettings;
import itoozh.core.team.claim.Claim;
import itoozh.core.team.claim.ClaimManager;
import itoozh.core.team.claim.ClaimType;
import itoozh.core.team.listener.ClaimListener;
import itoozh.core.team.listener.TeamListener;
import itoozh.core.team.player.Member;
import itoozh.core.team.player.Role;
import itoozh.core.team.regen.TeamRegenManager;
import itoozh.core.timer.TimerManager;
import itoozh.core.util.LanguageUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TeamManager {

    private static Map<String, Team> teams;

    private static TeamRegenManager teamRegenManager;

    public ClaimManager getClaimManager() {
        return claimManager;
    }

    private static ClaimManager claimManager;

    private static Config dataFile;

    private static Config regenDataFile;

    public TeamManager(Main plugin) {
        teams = new ConcurrentHashMap<>();
        teamRegenManager = new TeamRegenManager(plugin);
        claimManager = new ClaimManager(plugin);
        plugin.saveResource("data/teams.yml", false);
        plugin.saveResource("data/regenTeams.yml", false);
        dataFile = new Config(new File(plugin.getDataFolder(), "data/teams.yml"), Config.YAML);
        regenDataFile = new Config(new File(plugin.getDataFolder(), "data/regenTeams.yml"), Config.YAML);
        load();
        plugin.getServer().getPluginManager().registerEvents(new TeamListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ClaimListener(), plugin);
    }

    public Team createTeam(String name, Player player) {
        Session playerSession = Main.getInstance().getSessionManager().getSession(player);
        Team team = teams.computeIfAbsent(name, k -> new Team(name, player.getUniqueId()));
        team.getMembers().add(new Member(player.getUniqueId(), Role.LEADER));
        playerSession.setTeam(team);
        return team;
    }

    public Team getTeam(String name) {
        return teams.get(name);
    }

    public void load() {
        Set<String> config = dataFile.getKeys(false);
        int i = 0;
        for (String teamName : config) {
            String name = dataFile.get(teamName + ".name", "");
            String leader = dataFile.get(teamName + ".leader", "");

            Date createdAt = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                createdAt = dateFormat.parse(dataFile.get(teamName + ".createdAt", ""));
            } catch (ParseException e) {
                System.err.println("Error al analizar la fecha: " + e.getMessage());
            }
            Location hq = null;
            if (dataFile.get(teamName + ".hq") != null) {
                hq = new Location(dataFile.getDouble(teamName + ".hq.x", 0), dataFile.getDouble(teamName + ".hq.y", 0), dataFile.getDouble(teamName + ".hq.z", 0));
                hq.setLevel(Server.getInstance().getLevelByName(dataFile.getString(teamName + ".hq.level", "")));
            }
            int balance = dataFile.getInt(teamName + ".balance", 0);
            int points = dataFile.getInt(teamName + ".points", 0);
            int kills = dataFile.getInt(teamName + ".kills", 0);
            int deaths = dataFile.getInt(teamName + ".deaths", 0);
            int kothCaptures = dataFile.getInt(teamName + ".kothCaptures", 0);
            boolean minuteRegen = dataFile.getBoolean(teamName + ".minuteRegen", false);
            boolean raidable = dataFile.getBoolean(teamName + ".raidable", false);

            Claim claim = null;
            if (dataFile.get(teamName + ".claim") != null) {
                claim = claimManager.getClaim(dataFile.getString(teamName + ".claim"));
            }

            double dtr = dataFile.getDouble(teamName + ".dtr", 1.1);
            boolean open = dataFile.getBoolean(teamName + ".open", false);

            List<Member> members = new ArrayList<>();
            ConfigSection membersSection = dataFile.getSection(teamName + ".members");
            if (membersSection != null) {
                for (String memberUUIDString : membersSection.getKeys(false)) {
                    UUID memberUUID = UUID.fromString(memberUUIDString);
                    String roleString = dataFile.getString(teamName + ".members." + memberUUIDString + ".role");
                    Role role = Role.valueOf(roleString);

                    Member member = new Member(memberUUID, role);
                    members.add(member);
                }
            }

            Team team = new Team(teamName, UUID.fromString(leader));
            team.setName(name);
            team.setLeader(UUID.fromString(leader));
            team.setCreatedAt(createdAt);
            team.setHq(hq);
            team.setKothCaptures(kothCaptures);
            team.setBalance(balance);
            team.setPoints(points);
            team.setKills(kills);
            team.setRaidable(raidable);
            team.setMinuteRegen(minuteRegen);
            team.setDeaths(deaths);
            team.setClaim(claim);
            team.setOpen(open);
            team.setMembers(members);
            team.setDtr(dtr);
            teams.put(teamName, team);
            if (getTeamRegenManager().startMinuteRegen(team)) {
                i++;
            }
        }
        Main.getInstance().getLogger().info(TextFormat.GREEN + "Loaded " + config.size() + " teams.");
        Main.getInstance().getLogger().info(TextFormat.GREEN + "Loaded " + i + " teams with regen per minute.");
        loadRegen();
    }

    public TeamRegenManager getTeamRegenManager() {
        return teamRegenManager;
    }

    public void save() {
        dataFile.setAll(new LinkedHashMap<>());
        for (Map.Entry<String, Team> entry : teams.entrySet()) {
            String name = entry.getKey();
            Team team = entry.getValue();

            dataFile.set(name + ".name", team.getName());
            dataFile.set(name + ".leader", team.getLeader().toString());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = dateFormat.format(team.getCreatedAt());
            dataFile.set(name + ".createdAt", dateString);

            if (team.getHq() != null) {
                dataFile.set(name + ".hq.x", team.getHq().getX());
                dataFile.set(name + ".hq.y", team.getHq().getY());
                dataFile.set(name + ".hq.z", team.getHq().getZ());
                dataFile.set(name + ".hq.level", team.getHq().getLevel().getName());
            } else {
                dataFile.set(name + ".hq", null);
            }

            dataFile.set(name + ".balance", team.getBalance());
            dataFile.set(name + ".points", team.getPoints());
            dataFile.set(name + ".kills", team.getKills());
            dataFile.set(name + ".deaths", team.getDeaths());

            dataFile.set(name + ".dtr", team.getDtr());
            dataFile.set(name + ".open", team.isOpen());

            dataFile.set(name + ".kothCaptures", team.getKothCaptures());
            dataFile.set(name + ".minuteRegen", team.isMinuteRegen());
            dataFile.set(name + ".raidable", team.isRaidable());
            if (team.getClaim() != null) {
                dataFile.set(name + ".claim", team.getClaim().getName());
            } else {
                dataFile.set(name + ".claim", null);
            }

            for (Member member : team.getMembers()) {
                dataFile.set(name + ".members." + member.getUniqueID() + ".role", member.getRole().name());
            }
        }

        dataFile.save();

        saveRegen();

        claimManager.save();
    }

    public void disbandTeam(String name) {
        Team team = teams.get(name);
        for (Member member : team.getMembers()) {
            Session session = Main.getInstance().getSessionManager().getSessionByUUID(member.getUniqueID());
            session.setTeam(null);
            for (Team t : teams.values()) {
                if (t.getFocusedTeam() == team) t.setFocusedTeam(null);
            }
            Main.getInstance().getNameTags().update();
            session.setChatSettings(ChatSettings.PUBLIC);
        }
        teams.remove(name);
    }

    public Map<String, Team> getTeams() {
        return teams;
    }

    public Config getConfig() {
        return dataFile;
    }

    public void handleDeath(Player from, Player to) {
        Session user = Main.getInstance().getSessionManager().getSessionByUUID(from.getUniqueId());
        Team team = user.getTeam();
        int deaths = Main.getInstance().getConfig().getInt("TEAM_POINTS.DEATH");
        int kills = Main.getInstance().getConfig().getInt("TEAM_POINTS.KILLS");
        user.addDeath();
        if (team != null) {
            team.setPoints(Math.max(team.getPoints() - deaths, 0));
            team.setDeaths(team.getDeaths() + 1);
            team.setDtr(team.getDtr() - Main.getInstance().getConfig().getInt("TEAM_DTR.DEATH_DTR"));
            Main.getInstance().getTeamManager().getTeamRegenManager().applyTimer(team);
            team.broadcast(TextFormat.colorize(LanguageUtils.getString("PLAYER_TEAM_LISTENER.MEMBER_DEATH").replaceAll("%player%", from.getName()).replaceAll("%dtr%", team.getDtrString())));
            team.broadcast(TextFormat.colorize(LanguageUtils.getString("DEATH_LISTENER.TEAMS_MESSAGES.LOST_POINTS").replaceAll("%points%", String.valueOf(deaths)).replaceAll("%player%", from.getName())));
        }
        if (to != null && to != from) {
            Session killedUser = Main.getInstance().getSessionManager().getSessionByUUID(to.getUniqueId());
            Team killedTeam = killedUser.getTeam();
            killedUser.addKill();
            if (killedTeam != null) {
                killedTeam.setPoints(killedTeam.getPoints() + kills);
                killedTeam.setKills(killedTeam.getKills() + 1);
                killedTeam.broadcast(TextFormat.colorize(LanguageUtils.getString("DEATH_LISTENER.TEAMS_MESSAGES.GAINED_POINTS").replaceAll("%points%", String.valueOf(kills)).replaceAll("%player%", from.getName())));
            }
        }
    }

    public void loadRegen() {
        Main.getInstance().getLogger().info(TextFormat.GREEN + "Loaded " + regenDataFile.getKeys(false).size() + " factions with regen time.");
        for (String teamName : regenDataFile.getKeys(false)) {
            long regenTime = regenDataFile.getLong(teamName, 0L);
            Team teamR = getTeam(teamName);
            if (teamR == null) continue;
            if (teamR.isMinuteRegen()) continue;
            getTeamRegenManager().getTeamsRegenerating().put(teamR, regenTime);
        }
    }

    public void saveRegen() {
        regenDataFile.setAll(new LinkedHashMap<>());

        for (Map.Entry<Team, Long> entry : getTeamRegenManager().getTeamsRegenerating().entrySet()) {
            regenDataFile.set(entry.getKey().getName(), entry.getValue());
            System.out.println(entry.getKey().getName());
        }

        regenDataFile.save();
    }

    public boolean canHit(Player from, Player to, boolean online) {
        if (from == null || to == null) return false;
        Session fromSession = Main.getInstance().getSessionManager().getSession(from);
        Session toSession = Main.getInstance().getSessionManager().getSession(to);

        TimerManager timerManager = Main.getInstance().getTimerManager();

        Claim wilderness = new Claim(ClaimType.WILDERNESS, "Wilderness", new Vector3(0, 0, 0), new Vector3(0, 0, 0));

        Team fromTeam = fromSession.getTeam();
        Team toTeam = toSession.getTeam();

        Claim claimFrom = claimManager.findClaimPerType(from.getLocation().getFloorX(), from.getLocation().getFloorZ());
        Claim claimTo = claimManager.findClaimPerType(to.getLocation().getFloorX(), to.getLocation().getFloorZ());

        if (claimFrom == null) {
            claimFrom = wilderness;
        }
        if (claimTo == null) {
            claimTo = wilderness;
        }
        if (timerManager.getSotwTimer().isActive() && !timerManager.getSotwTimer().getEnabled().contains(to.getUniqueId())) {
            if (!online) {
                return false;
            }
            from.sendMessage(TextFormat.colorize(LanguageUtils.getString("SOTW_TIMER.DAMAGED_ATTACK").replaceAll("%player%", to.getName())));
            return false;
        } else if (timerManager.getSotwTimer().isActive() && !timerManager.getSotwTimer().getEnabled().contains(from.getUniqueId())) {
            if (!online) {
                return false;
            }
            from.sendMessage(TextFormat.colorize(LanguageUtils.getString("SOTW_TIMER.DAMAGER_ATTACK").replaceAll("%player%", to.getName())));
            return false;
        } else if (timerManager.getInvincibilityTimer().hasTimer(from)) {
            if (!online) {
                return false;
            }
            from.sendMessage(TextFormat.colorize(LanguageUtils.getString("INVINCIBILITY.DAMAGER_ATTACK").replaceAll("%player%", to.getName())));
            return false;
        } else if (timerManager.getInvincibilityTimer().hasTimer(to)) {
            if (!online) {
                return false;
            }
            from.sendMessage(TextFormat.colorize(LanguageUtils.getString("INVINCIBILITY.DAMAGED_ATTACK").replaceAll("%player%", to.getName())));
            return false;
        } else if (timerManager.getPvPTimer().hasTimer(from)) {
            if (!online) {
                return false;
            }
            from.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_TIMER.DAMAGER_ATTACK").replaceAll("%player%", to.getName())));
            return false;
        } else if (timerManager.getPvPTimer().hasTimer(to)) {
            if (!online) {
                return false;
            }
            from.sendMessage(TextFormat.colorize(LanguageUtils.getString("PVP_TIMER.DAMAGED_ATTACK").replaceAll("%player%", to.getName())));
            return false;
        } else if (claimFrom.getType() == ClaimType.SPAWN) {
            from.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_LISTENER.DAMAGED_ATTACK").replaceAll("%player%", to.getName())));
        } else {
            if (claimTo.getType() != ClaimType.SPAWN) {
                if (fromTeam != null && toTeam != null) {
                    if (fromTeam == toTeam) {
                        if (!online) {
                            return false;
                        }
                        from.sendMessage(TextFormat.colorize(LanguageUtils.getString("PLAYER_TEAM_LISTENER.MEMBER_HURT").replaceAll("%player%", to.getName()).replaceAll("%role%", toTeam.getMember(to.getUniqueId()).getAsterisk())));
                        return false;
                    }
                }
                return true;
            }
            if (!online) {
                return false;
            }
            from.sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_LISTENER.DAMAGER_ATTACK").replaceAll("%player%", to.getName())));
        }
        return false;
    }

    public boolean canBuild(Player player, Location location) {
        Claim claim = getClaimManager().findClaimPerType(location.getFloorX(), location.getFloorZ());
        Team team = null;
        if (claim != null) {
            if (claim.getType() == ClaimType.TEAM) {
                team = getTeam(claim.getName());
            }
        }
        if (player.getGamemode() == Player.CREATIVE) {
            return true;
        }
        if (team != null) {
            if (team.getOnlinePlayers().contains(player)) {
                return true;
            }
            if (team.isRaidable()) {
                return true;
            }
        }
        if (claim != null) {
            if (claim.getType() == ClaimType.WARZONE) {
                return false;
            }
        }
        return claim == null;
    }

    public void teleportToSafe(Player player, int separationDistance) {
        Session session = Main.getInstance().getSessionManager().getSession(player);
        if (session == null) {
            return;
        }

        Claim claim = getClaimManager().findClaim(player.getLocation().getFloorX(), player.getLocation().getFloorZ(), 0);

        if (claim == null) return;

        ClaimType claimType = claim.getType();
        if (claimType != ClaimType.TEAM) {
            return;
        }

        Location playerLocation = player.getLocation();
        int playerX = playerLocation.getFloorX();
        int playerZ = playerLocation.getFloorZ();

        int claimX1 = claim.getX1();
        int claimX2 = claim.getX2();
        int claimZ1 = claim.getZ1();
        int claimZ2 = claim.getZ2();

        int minX = Math.min(claimX1, claimX2);
        int maxX = Math.max(claimX1, claimX2);
        int minZ = Math.min(claimZ1, claimZ2);
        int maxZ = Math.max(claimZ1, claimZ2);

        // Calculate distances to each side of the claim
        double distanceToLeft = playerX - minX;
        double distanceToRight = maxX - playerX;
        double distanceToFront = playerZ - minZ;
        double distanceToBack = maxZ - playerZ;

        // Find the side (left, right, front, back) closest to the player
        double minDistance = Math.min(distanceToLeft, Math.min(distanceToRight, Math.min(distanceToFront, distanceToBack)));

        // Calculate the teleportation location based on the closest side
        int teleportX = playerX;
        int teleportZ = playerZ;

        if (minDistance == distanceToLeft) {
            teleportX = minX - separationDistance;
        } else if (minDistance == distanceToRight) {
            teleportX = maxX + separationDistance;
        } else if (minDistance == distanceToFront) {
            teleportZ = minZ - separationDistance;
        } else if (minDistance == distanceToBack) {
            teleportZ = maxZ + separationDistance;
        }

        // Teleport the player to the calculated location
        Location teleportLocation = new Location(teleportX, highestBlockAt(teleportX, teleportZ), teleportZ, playerLocation.getLevel());
        player.teleport(teleportLocation);
        player.sendMessage(TextFormat.colorize("&aYou have been teleported to a safe location outside of your team's claim."));
    }

    public void teleportToSafe2(Player player, int separationDistance) {
        Session session = Main.getInstance().getSessionManager().getSession(player);
        if (session == null) {
            return;
        }

        ClaimManager claimManager = Main.getInstance().getTeamManager().getClaimManager();
        Claim playerClaim = claimManager.findClaim(player.getLocation().getFloorX(), player.getLocation().getFloorZ(), 0);
        if (playerClaim == null) {
            // Player is not in any claim, teleport to the highest block at player's location
            player.teleport(player.getLocation().add(0.5, 1.0, 0.5));
            return;
        }

        boolean teleportSuccessful = false;
        for (int i = separationDistance; i < 250; i += separationDistance) {
            for (int t = separationDistance; t < 250; t += separationDistance) {
                Location loc = player.getLocation().clone().add(i, 0.0, t);
                Claim locClaim = claimManager.findClaim(loc.getFloorX(), loc.getFloorZ(), 0);
                if (locClaim == null) {
                    teleportSuccessful = true;
                    loc.setY(highestBlockAt(loc.getFloorX(), loc.getFloorZ()));
                    player.teleport(loc.add(0.5, 0.1, 0.5));
                    break;
                }
                Location locClone = player.getLocation().clone().add(-i, 0.0, -t);
                Claim locCloneClaim = claimManager.findClaim(locClone.getFloorX(), locClone.getFloorZ(), 0);
                if (locCloneClaim == null) {
                    teleportSuccessful = true;
                    locClone.setY(highestBlockAt(locClone.getFloorX(), locClone.getFloorZ()));
                    player.teleport(locClone.add(0.5, 1.0, 0.5));
                    break;
                }
            }
            if (teleportSuccessful) {
                break;
            }
        }
    }




    private Location findSafeLocationOutsideClaim(int playerX, int playerZ, Claim claim) {
        int claimMinX = claim.getX1();
        int claimMaxX = claim.getX2();
        int claimMinZ = claim.getZ1();
        int claimMaxZ = claim.getZ2();

        int safeX = playerX;
        int safeZ = playerZ;

        if (playerX >= claimMinX && playerX <= claimMaxX) {
            safeX = (playerX < (claimMinX + claimMaxX) / 2) ? claimMinX - 1 : claimMaxX + 1;
        }

        if (playerZ >= claimMinZ && playerZ <= claimMaxZ) {
            safeZ = (playerZ < (claimMinZ + claimMaxZ) / 2) ? claimMinZ - 1 : claimMaxZ + 1;
        }

        return new Location(safeX, 0, safeZ);
    }

    public int highestBlockAt(int x, int z) {
        Level level = Server.getInstance().getDefaultLevel();
        int y = 128;

        while (y > 0 && level.getBlockIdAt(x, y, z) == Block.AIR) {
            y--;
        }

        return y + 1;
    }

}
