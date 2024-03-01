package itoozh.core.team.claim;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.session.Session;
import itoozh.core.team.Team;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Claim {

    private ClaimType type;

    private String name;

    private boolean locked = false;

    private int x1, x2, z1, z2;


    public Claim(ClaimType type, String name, Vector3 pos1, Vector3 pos2) {
        this.type = type;
        this.name = name;
        this.x1 = pos1.getFloorX();
        this.x2 = pos2.getFloorX();
        this.z1 = pos1.getFloorZ();
        this.z2 = pos2.getFloorZ();
    }

    public int getPrice() {
        int x1P = Math.min(x1, x2);
        int z1P = Math.min(z1, z2);
        int x2P = Math.max(x1, x2);
        int z2P = Math.max(z1, z2);

        return ((x2P - x1P + 1) * (z2P - z1P + 1)) * Main.getInstance().getConfig().getInt("CLAIMING.PRICE_PER_BLOCK");
    }

    public String getNameFormat(Player player) {

        switch (type){
            case WARZONE:
                return Main.getInstance().getConfig().getString("SYSTEM_TEAMS.WARZONE") + name;
            case SPAWN:
                return Main.getInstance().getConfig().getString("SYSTEM_TEAMS.SAFEZONE") + name;
            case ROAD:
                return Main.getInstance().getConfig().getString("SYSTEM_TEAMS.ROADS") + name;
            case KOTH:
                return Main.getInstance().getConfig().getString("SYSTEM_TEAMS.EVENT") + name;
            case WILDERNESS:
                return Main.getInstance().getConfig().getString("SYSTEM_TEAMS.WILDERNESS") + name;
            case TEAM:
                Session session = Main.getInstance().getSessionManager().getSession(player);
                if (session.getTeam() != null) {
                    if (session.getTeam().getClaim() != null) {
                        if (session.getTeam().getClaim().equals(this)) {
                            return TextFormat.colorize(Main.getInstance().getConfig().getString("RELATION_COLOR.TEAMMATE") + name);
                        }
                    }
                    Team focusedTeam = session.getTeam().getFocusedTeam();
                    if (focusedTeam != null) {
                        if (focusedTeam.getClaim() != null) {
                            if (focusedTeam.getClaim().equals(this)) {
                                return TextFormat.colorize(Main.getInstance().getConfig().getString("RELATION_COLOR.FOCUSED") + name);
                            }
                        }
                    }
                }
                return TextFormat.colorize(Main.getInstance().getConfig().getString("RELATION_COLOR.ENEMY") + name);
            default:
                return TextFormat.colorize( Main.getInstance().getConfig().getString("RELATION_COLOR.ENEMY") + name);
        }
    }

    public List<Player> getPlayersInsideClaim() {
        List<Player> playersInside = new ArrayList<>();

        Level level = Server.getInstance().getDefaultLevel(); // Cambia "world" al nombre correcto del mundo
        if (level == null) {
            return playersInside;
        }

        for (Player player : level.getPlayers().values()) {
            Position playerPosition = player.getPosition();
            if (isInsideClaim(playerPosition)) {
                playersInside.add(player);
            }
        }

        return playersInside;
    }

    private boolean isInsideClaim(Position position) {
        double playerX = position.getX();
        double playerZ = position.getZ();

        int minX = Math.min(this.getX1(), this.getX2());
        int maxX = Math.max(this.getX1(), this.getX2());
        int minZ = Math.min(this.getZ1(), this.getZ2());
        int maxZ = Math.max(this.getZ1(), this.getZ2());

        return playerX >= minX && playerX <= maxX && playerZ >= minZ && playerZ <= maxZ;
    }
}
