package itoozh.core.ranks;

import cn.nukkit.Player;
import itoozh.core.Main;

import java.util.List;

public class Rank {

    private String name;
    private String color = "";
    private String prefix = "";
    private String suffix = "";
    private List<String> permissions;

    public Rank(String name, String color, String prefix, String suffix, List<String> permissions) {
        this.name = name;
        this.color = color;
        this.prefix = prefix;
        this.suffix = suffix;
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPerm(Player player) {
        if (!player.isOnline()) return;
        for (String permission : permissions) {
            player.addAttachment(Main.getInstance()).setPermission(permission, true);
        }
    }
    public void removePerm(Player player) {
        if (!player.isOnline()) return;
        for ( String permission : permissions ) {
            player.addAttachment(Main.getInstance()).unsetPermission(permission, false);
        }
    }
}
