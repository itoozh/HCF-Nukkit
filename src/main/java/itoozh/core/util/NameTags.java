package itoozh.core.util;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.StringEntityData;
import cn.nukkit.network.protocol.SetEntityDataPacket;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.session.Session;
import itoozh.core.team.Team;
import itoozh.core.timer.server.SOTWTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NameTags {
    private final ExecutorService executor;

    public NameTags() {
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void setNameTag(Player player, String nameTag, Player viewer) {
        SetEntityDataPacket pk = new SetEntityDataPacket();
        pk.eid = player.getId();
        pk.metadata = player.getDataProperties().put(new StringEntityData(4, nameTag));
        viewer.dataPacket(pk);
    }

    public void updateInvisTag(Player player) {
        Session session = Main.getInstance().getSessionManager().getSession(player);
        Team team = session.getTeam();
        if (team == null) return;
        for (Player viewer : Server.getInstance().getOnlinePlayers().values())
        {
            if (team.getOnlinePlayers().contains(viewer) || player == viewer) this.showInvisTag(player, viewer);
        }
    }

    public void showInvisTag(Player player, Player viewer) {
        SetEntityDataPacket pk = new SetEntityDataPacket();
        pk.eid = player.getId();
        pk.metadata = player.getDataProperties().put(new ByteEntityData(14, 1));
        viewer.dataPacket(pk);
        System.out.println("El nametag de " + player.getName() + " es visible para " + viewer.getName());
    }


    public void update() {
        for (Player player : Server.getInstance().getOnlinePlayers().values()) {
            for (Player target : Server.getInstance().getOnlinePlayers().values()) {
                this.executor.execute(() -> {
                    String update = getAndUpdate(player, target);
                    this.updateLunarTags(player, target, update);
                });
            }
        }
    }

    private void updateLunarTags(Player from, Player to, String update) {
        List<String> lines = new ArrayList<>();
        Session toSession = Main.getInstance().getSessionManager().getSession(to);
        String prefix = toSession.getPrefix(to);
        Team team = toSession.getTeam();
        if (team != null) {
            String teamPosition = team.getTeamPosition();
            if (teamPosition != null) {
                lines.add(TextFormat.colorize(Main.getInstance().getConfig().getString("NAMETAGS.TEAM_TOP").replaceAll("%pos%", teamPosition).replaceAll("%name%", team.getDisplayName(from)).replaceAll("%dtr-color%", team.getDtrColor()).replaceAll("%dtr%", team.getDtrString()).replaceAll("%dtr-symbol%", team.getDtrSymbol())));
            } else {
                lines.add(TextFormat.colorize(Main.getInstance().getConfig().getString("NAMETAGS.NORMAL").replaceAll("%name%", team.getDisplayName(from)).replaceAll("%dtr-color%", team.getDtrColor()).replaceAll("%dtr%", team.getDtrString()).replaceAll("%dtr-symbol%", team.getDtrSymbol())));
            }
        }
        lines.add(TextFormat.colorize(((prefix != null) ? prefix + " " : "") + update + to.getName()));
        String name = LanguageUtils.convertListToString(lines);
        setNameTag(to, name, from);
    }

    public String getAndUpdate(Player from, Player to) {
        Session fromSession = Main.getInstance().getSessionManager().getSessionByUUID(from.getUniqueId());
        SOTWTimer sotwTimer = Main.getInstance().getTimerManager().getSotwTimer();
        Team team = fromSession.getTeam();

        if ((team != null && team.getOnlinePlayers().contains(to)) || from == to) {
            return TextFormat.colorize(Main.getInstance().getConfig().getString("RELATION_COLOR.TEAMMATE"));
        }
        if (sotwTimer.isActive() && !sotwTimer.getEnabled().contains(to.getUniqueId())) {
            return TextFormat.colorize(Main.getInstance().getConfig().getString("RELATION_COLOR.SOTW"));
        }
        if (team != null && team.isFocused(to)) {
            return TextFormat.colorize(Main.getInstance().getConfig().getString("RELATION_COLOR.FOCUSED"));
        }
        if (Main.getInstance().getTimerManager().getArcherTagTimer().hasTimer(to)) {
            return TextFormat.colorize(Main.getInstance().getConfig().getString("RELATION_COLOR.ARCHER_TAG"));
        }
        if (Main.getInstance().getTimerManager().getPvPTimer().hasTimer(to)) {
            return TextFormat.colorize(Main.getInstance().getConfig().getString("RELATION_COLOR.PVP_TIMER"));
        }
        if (Main.getInstance().getTimerManager().getInvincibilityTimer().hasTimer(to)) {
            return TextFormat.colorize(Main.getInstance().getConfig().getString("RELATION_COLOR.INVINCIBILITY"));
        }
        return TextFormat.colorize(Main.getInstance().getConfig().getString("RELATION_COLOR.ENEMY"));
    }
}
