package itoozh.core.team.claim;

import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.session.Session;
import itoozh.core.session.procces.type.ClaimProcess;
import itoozh.core.team.Team;
import itoozh.core.util.LanguageUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClaimManager  {

    public Map<String, Claim> getClaims() {
        return claims;
    }

    private static Map<String, Claim> claims;

    private static Config dataFile;

    public Item item;

    public ClaimManager(Main plugin) {
        claims = new ConcurrentHashMap<>();
        plugin.saveResource("data/claims.yml", false);
        dataFile = new Config(new File(plugin.getDataFolder(), "data/claims.yml"), Config.YAML);
        load();

        item = Item.fromString(plugin.getConfig().getString("CLAIMING.CLAIM_WAND.TYPE"));
        item.setCustomName(TextFormat.colorize("&r" + plugin.getConfig().getString("CLAIMING.CLAIM_WAND.NAME")));

        item.setLore(plugin.getConfig().getStringList("CLAIMING.CLAIM_WAND.LORE")
                .stream()
                .map(TextFormat::colorize).toArray(String[]::new));
    }

    public Claim getClaim(String name) {
        return claims.get(name);
    }

    public void load() {
        Set<String> config = dataFile.getKeys(false);
        for (String claimName : config) {
            String name = dataFile.get(claimName + ".name", "");
            ClaimType type = ClaimType.valueOf(dataFile.get(claimName + ".type", ""));
            int x1 = dataFile.get(claimName + ".x1", 0);
            int x2 = dataFile.get(claimName + ".x2", 0);
            int z1 = dataFile.get(claimName + ".z1", 0);
            int z2 = dataFile.get(claimName + ".z2", 0);
            Claim claim = new Claim(type, name, new Vector3(x1, 0, z1), new Vector3(x2, 0, z2));
            claims.put(name, claim);
        }
        Main.getInstance().getLogger().info(TextFormat.GREEN + "Loaded " + claims.size() + " claims.");

    }

    public void save() {
        dataFile.setAll(new LinkedHashMap<>());
        for (Map.Entry<String, Claim> entry : claims.entrySet()) {

            String name = entry.getKey();
            Claim claim = entry.getValue();

            dataFile.set(name + ".name", claim.getName());
            dataFile.set(name+ ".type", claim.getType().name());
            dataFile.set(name + ".x1", claim.getX1());
            dataFile.set(name + ".x2", claim.getX2());
            dataFile.set(name + ".z1", claim.getZ1());
            dataFile.set(name + ".z2", claim.getZ2());
        }
        dataFile.save();
    }

    public boolean checkInterference(int x1, int x2, int z1, int z2, int separation) {
        for (Claim existingClaim : claims.values()) {
            int existingX1 = existingClaim.getX1();
            int existingX2 = existingClaim.getX2();
            int existingZ1 = existingClaim.getZ1();
            int existingZ2 = existingClaim.getZ2();

            int separatedExistingX1 = existingX1 - separation;
            int separatedExistingX2 = existingX2 + separation;
            int separatedExistingZ1 = existingZ1 - separation;
            int separatedExistingZ2 = existingZ2 + separation;

            if (x2 + separation >= separatedExistingX1 && x1 - separation <= separatedExistingX2 && z2 + separation >= separatedExistingZ1 && z1 - separation <= separatedExistingZ2) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Claim> claims(int x1, int x2, int z1, int z2) {
        ArrayList<Claim> found = new ArrayList<>();

        for (int i = x1; i < x2; i++) {
            for (int i2 = z1; i2 < z2; i2++) {
                Claim claim = findClaim(i, i2, 0);
                if (claim != null) {
                    if (!found.contains(claim)) {
                        found.add(claim);
                    }
                }
            }
        }


        return found;
    }

    public Claim findClaim(Location location) {
        return  findClaimPerType(location.getFloorX(), location.getFloorZ());
    }

    public Claim findClaim(int x, int z, int separation) {
        for (Claim claim : claims.values()) {
            int minX = claim.getX1() - separation;
            int maxX = claim.getX2() + separation;
            int minZ = claim.getZ1() - separation;
            int maxZ = claim.getZ2() + separation;

            if ((minX <= x && maxX >= x) && (minZ <= z && maxZ >= z)) {
                return claim;
            }
        }
        return null;
    }

    public void tryClaiming(ClaimProcess process) {
        int x1 = Math.min((int) process.firstPos.x, (int) process.secondPos.x);
        int z1 = Math.min((int) process.firstPos.z, (int) process.secondPos.z);
        int x2 = Math.max((int) process.firstPos.x, (int) process.secondPos.x);
        int z2 = Math.max((int) process.firstPos.z, (int) process.secondPos.z);

        process.price = ((x2 - x1 + 1) * (z2 - z1 + 1)) * Main.getInstance().getConfig().getInt("CLAIMING.PRICE_PER_BLOCK");

        if (process.claimType == ClaimType.TEAM) {
            process.getPlayer().sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CLAIM.SET_LOCATIONS").replaceAll("%price%", String.valueOf(process.price)).replaceAll("%length%", String.valueOf(getLength(x1, x2, z1, z2))).replaceAll("%width%", String.valueOf(getWidth(x1, x2, z1, z2))).replaceAll("%blocks%", String.valueOf(getArea(x1, x2, z1, z2)))));
        } else {
            process.getPlayer().sendMessage(TextFormat.colorize("&aPositions first and second has been marked! \nWrite 'accept' or 'yes' in th chat to confirm the claim!"));
        }
    }

    public boolean createClaim(ClaimProcess process) {
        int x1 = Math.min((int) process.firstPos.x, (int) process.secondPos.x);
        int z1 = Math.min((int) process.firstPos.z, (int) process.secondPos.z);
        int x2 = Math.max((int) process.firstPos.x, (int) process.secondPos.x);
        int z2 = Math.max((int) process.firstPos.z, (int) process.secondPos.z);

        process.firstPos.x = x1;
        process.firstPos.z = z1;
        process.secondPos.x = x2;
        process.secondPos.z = z2;

        int separator = Main.getInstance().getConfig().getInt("CLAIMING.CLAIM_SEPARATOR") - 1;

        Claim claim;
        if (process.claimType == ClaimType.TEAM) {
            if (checkInterference(x1, x2, z1, z2, separator)) {
                process.getPlayer().sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_CLAIM.TOO_CLOSE").replaceAll("%amount%", String.valueOf(separator))));
                return false;
            }
            Session session = Main.getInstance().getSessionManager().getSession(process.getPlayer());
            Team team = session.getTeam();

            if (team == null) {
                process.getPlayer().sendMessage(TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.NOT_IN_TEAM")));
                return false;
            }
            claim = new Claim(process.claimType, process.getName(), process.firstPos, process.secondPos);
            team.setClaim(claim);
        } else {
            claim = new Claim(process.claimType, process.getName(), process.firstPos, process.secondPos);
            process.getPlayer().sendMessage(TextFormat.colorize("&aClaim has been created successfully!"));
        }

        claims.put(claim.getName(), claim);
        return true;
    }

    public void addClaim(Claim claim) {
        claims.put(claim.getName(), claim);
    }

    public boolean checkInterferenceWithSpawnCenter(int x1, int x2, int z1, int z2, int radius) {
        Location spawnCenter = Server.getInstance().getDefaultLevel().getSpawnLocation().getLocation();
        int spawnX = spawnCenter.getFloorX();
        int spawnZ = spawnCenter.getFloorZ();

        int centerX = (x1 + x2) / 2;
        int centerZ = (z1 + z2) / 2;

        int distanceX = Math.abs(spawnX - centerX);
        int distanceZ = Math.abs(spawnZ - centerZ);

        return distanceX <= radius && distanceZ <= radius;
    }

    public void deleteFactionClaim(Team faction) {
        claims.remove(faction.getName());
    }

    public void deleteClaim(Claim claim) {
        claims.remove(claim.getName());
    }

    public int getLength(int x1, int x2, int z1, int z2) {
        return this.getMaximumPoint(x1, x2, z1, z2).getFloorZ() - this.getMinimumPoint(x1, x2, z1, z2).getFloorZ();
    }

    public int getWidth(int x1, int x2, int z1, int z2) {
        return this.getMaximumPoint(x1, x2, z1, z2).getFloorX() - this.getMinimumPoint(x1, x2, z1, z2).getFloorX();
    }

    public Location getMaximumPoint(int x1, int x2, int z1, int z2) {
        return new Location(Math.max(x1, x2), Math.max(0, 256), Math.max(z1, z2));
    }

    public Location getMinimumPoint(int x1, int x2, int z1, int z2) {
        return new Location(Math.min(x1, x2), 0, Math.min(z1, z2));
    }



    public int getArea(int x1, int x2, int z1, int z2) {
        Location min = this.getMinimumPoint(x1, x2, z1, z2);
        Location max = this.getMaximumPoint(x1, x2, z1, z2);
        return (max.getFloorX() - min.getFloorX() + 1) * (max.getFloorZ() - min.getFloorZ() + 1);
    }

    public Claim findClaimPerType(int x, int z) {
        List<Claim> claimsEnPosicion = encontrarClaimsEnPosicion(x, z);
        return obtenerClaimConPrioridad(claimsEnPosicion);
    }

    private List<Claim> encontrarClaimsEnPosicion(int x, int z) {
        List<Claim> claimsEnPosicion = new ArrayList<>();

        for (Claim claim : claims.values()) {
            int minX = claim.getX1();
            int maxX = claim.getX2();
            int minZ = claim.getZ1();
            int maxZ = claim.getZ2();

            if ((minX <= x && maxX >= x) && (minZ <= z && maxZ >= z)) {
                claimsEnPosicion.add(claim);
            }
        }

        return claimsEnPosicion;
    }

    private Claim obtenerClaimConPrioridad(List<Claim> claimsEnPosicion) {
        Claim claimPrioritario = null;

        for (Claim claim : claimsEnPosicion) {
            if (claimPrioritario == null || claim.getType().ordinal() > claimPrioritario.getType().ordinal()) {
                claimPrioritario = claim;
            }
        }

        return claimPrioritario;
    }

}

