package itoozh.core;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.utils.TextFormat;
import itoozh.core.hologram.Hologram;
import itoozh.core.scoreboard.Scoreboard;
import itoozh.core.scoreboard.ScoreboardUtils;
import itoozh.core.session.Session;
import itoozh.core.session.settings.ChatSettings;
import itoozh.core.team.Team;
import itoozh.core.team.player.Role;
import itoozh.core.util.Cooldown;
import itoozh.core.util.LanguageUtils;

public class EventsListener implements Listener {
    private final Cooldown chatCooldown;
    public EventsListener() {
        this.chatCooldown = new Cooldown(Main.getInstance());
    }

    @EventHandler
    public void handler(PlayerJoinEvent ev) {
        Player player = ev.getPlayer();
        Session session = Main.getInstance().getSessionManager().createSession(player);
        String name = session.getRank().getColor() + player.getName();
        String title = "§fWelcome, " + name + " §6(Factions)";
        String subtitle = "§6§l| §r§fstore.orange.cc";
        player.sendTitle(TextFormat.colorize(title), TextFormat.colorize(subtitle));
        Scoreboard scoreboard = new ScoreboardUtils().getScoreboard();
        session.setScoreboard(scoreboard);
        scoreboard.show(player);
        ev.setJoinMessage(TextFormat.colorize("&7[&a+&7] &f" + session.getRank().getColor() + player.getName()));
        if (!player.hasPlayedBefore()) {
            Main.getInstance().getTimerManager().getInvincibilityTimer().applyTimer(player);
        }
        session.getRank().setPerm(player);
        Main.getInstance().getNameTags().update();
    }

    @EventHandler
    public void handler(PlayerQuitEvent ev) {
        Player player = ev.getPlayer();
        Session session = Main.getInstance().getSessionManager().getSessionByUUID(player.getUniqueId());
        Scoreboard scoreboard = session.getScoreboard();
        scoreboard.hide(player);
        session.setScoreboard(null);
        ev.setQuitMessage(TextFormat.colorize("&7[&c-&7] &f" + player.getName()));
    }

    @EventHandler
    public void handler(PlayerDeathEvent ev) {
        Player player = ev.getEntity();

        Entity killerEntity = player.getKiller();
        Session playerSession = Main.getInstance().getSessionManager().getSession(player);

        if (playerSession == null) playerSession = Main.getInstance().getSessionManager().createSession(player);

        if (killerEntity != null) {
            if (killerEntity instanceof Player) {
                Player killer = (Player) killerEntity;
                Session killerSession = Main.getInstance().getSessionManager().getSession(killer);
                killerSession.addKill();
                playerSession.addDeath();
                ev.setDeathMessage(TextFormat.colorize("&c" + player.getName() + "&4[" + playerSession.getKills() + "] &ewas slain by &c" + killer.getName() + "&4[" + killerSession.getKills() + "]&e."));
                return;
            }
        }

        playerSession.addDeath();
        ev.setDeathMessage(TextFormat.colorize("&c" + player.getName() + "&4[" + playerSession.getKills() + "] &edied."));
    }

    @EventHandler
    public void handler(PlayerChatEvent ev) {
        if (ev.isCancelled()) return;
        ev.setCancelled();
        Player player = ev.getPlayer();
        Session playerSession = Main.getInstance().getSessionManager().getSession(player);

        String prefix = playerSession.getPrefix(player);
        String kTop = "";
        String fTop = "";

        String pPrefix = TextFormat.colorize(playerSession.getRank().getPrefix());
        String pSuffix = TextFormat.colorize(playerSession.getRank().getSuffix());
        String pColor = TextFormat.colorize(playerSession.getRank().getColor());

        boolean bypass = player.hasPermission("core.chat.bypass");

        Team team = playerSession.getTeam();
        ChatSettings chatSettings = playerSession.getChatSettings();

        if (prefix != null) {
            kTop = Main.getInstance().getConfig().getString("CHAT_FORMAT.KILL_TOP_FORMAT").replace("%killtop%", prefix);
        }
        if (team != null) {
            String position = team.getTeamPosition();
            fTop = ((position == null) ? "" : Main.getInstance().getConfig().getString("CHAT_FORMAT.FTOP_FORMAT").replace("%ftop%", position));
        }
        if (this.chatCooldown.hasCooldown(player) && !bypass) {
            ev.setCancelled(true);
            player.sendMessage(TextFormat.colorize(LanguageUtils.getString("CHAT_LISTENER.COOLDOWN").replaceAll("%seconds%", this.chatCooldown.getRemaining(player))));
            return;
        }
        if (!bypass && chatSettings == ChatSettings.PUBLIC) {
            this.chatCooldown.applyCooldown(player, Main.getInstance().getConfig().getInt("CHAT_FORMAT.COOLDOWN"));
        }
        switch (chatSettings) {
            case PUBLIC:
                String msg = "";
                for (CommandSender online : ev.getRecipients()) {
                    if (!(online instanceof Player)) continue;
                    Session sessionOn = Main.getInstance().getSessionManager().getSession((Player) online);
                    if (!sessionOn.isPublicChat()) {
                        msg = Main.getInstance().getConfig().getString("CHAT_FORMAT.PUBLIC_NO_TEAM").replace("%prefix%", pPrefix).replace("%suffix%", pSuffix).replace("%color%", pColor).replace("%ftop%", fTop).replace("%killtop%", kTop).replace("%player%", player.getName()).replace("%s", ev.getMessage());
                        continue;
                    }
                    if (team == null) {
                        msg = Main.getInstance().getConfig().getString("CHAT_FORMAT.PUBLIC_NO_TEAM").replace("%prefix%", pPrefix).replace("%suffix%", pSuffix).replace("%color%", pColor).replace("%ftop%", fTop).replace("%killtop%", kTop).replace("%player%", player.getName()).replace("%s", ev.getMessage());
                        online.sendMessage(TextFormat.colorize(msg));
                    } else {
                        msg = Main.getInstance().getConfig().getString("CHAT_FORMAT.PUBLIC_TEAM").replace("%prefix%", pPrefix).replace("%suffix%", pSuffix).replace("%color%", pColor).replace("%ftop%", fTop).replace("%killtop%", kTop).replace("%player%", player.getName()).replace("%team%", team.getDisplayName((Player) online)).replace("%s", ev.getMessage());
                        online.sendMessage(TextFormat.colorize(msg));
                    }
                }

                Main.getInstance().getLogger().info(TextFormat.colorize(msg));
                break;
            case TEAM:
                if (team == null) {
                    playerSession.setChatSettings(ChatSettings.PUBLIC);
                    break;
                }

                String messageT = TextFormat.colorize(Main.getInstance().getConfig().getString("CHAT_FORMAT.TEAM_CHAT.FORMAT").replaceAll("%player%", player.getName()).replaceAll("%message%", ev.getMessage()));

                for (Player playerTeam : team.getOnlinePlayers()) {
                    playerTeam.sendMessage(messageT);
                }
                Main.getInstance().getLogger().info(messageT);
                break;
            case CAPTAIN:
                if (team == null) {
                    playerSession.setChatSettings(ChatSettings.PUBLIC);
                    break;
                }
                String messageC = TextFormat.colorize(Main.getInstance().getConfig().getString("CHAT_FORMAT.OFFICER_CHAT.FORMAT").replaceAll("%player%", player.getName()).replaceAll("%message%", ev.getMessage()));

                for (Player playerTeam : team.getOnlinePlayers()) {
                    if (!team.checkRole(playerTeam, Role.CAPTAIN)) continue;
                    playerTeam.sendMessage(messageC);
                }
                Main.getInstance().getLogger().info(messageC);
                break;
            case CO_LEADER:
                if (team == null) {
                    playerSession.setChatSettings(ChatSettings.PUBLIC);
                    break;
                }
                String messageCL = TextFormat.colorize(Main.getInstance().getConfig().getString("CHAT_FORMAT.CO_LEADER_CHAT.FORMAT").replaceAll("%player%", player.getName()).replaceAll("%message%", ev.getMessage()));

                for (Player playerTeam : team.getOnlinePlayers()) {
                    if (!team.checkRole(playerTeam, Role.CO_LEADER)) continue;
                    playerTeam.sendMessage(messageCL);
                }
                Main.getInstance().getLogger().info(messageCL);
                break;
        }
    }

    @EventHandler
    public void onRespawn(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Main.getInstance().getTimerManager().getPvPTimer().applyTimer(player);
        Main.getInstance().getNameTags().update();
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Hologram) {
            event.setCancelled(true); // Prevent the Hologram from taking damage
        }
    }

    // EntityArmorChangeEvent

}
