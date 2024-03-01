package itoozh.core;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import itoozh.core.ability.AbilityManager;
import itoozh.core.command.CommandManager;
import itoozh.core.crate.CrateManager;
import itoozh.core.entities.ItemDrop;
import itoozh.core.gkit.GKitManager;
import itoozh.core.gkit.profile.ProfileManager;
import itoozh.core.hologram.HologramManager;
import itoozh.core.listener.BorderListener;
import itoozh.core.listener.EndListener;
import itoozh.core.listener.GlitchListener;
import itoozh.core.listener.PortalListener;
import itoozh.core.pvpclass.PvPClassManager;
import itoozh.core.ranks.RankManager;
import itoozh.core.scoreboard.Scoreboard;
import itoozh.core.scoreboard.manager.ScoreboardManager;
import itoozh.core.session.SessionManager;
import itoozh.core.signs.CustomSignManager;
import itoozh.core.team.TeamManager;
import itoozh.core.timer.TimerManager;
import itoozh.core.timer.type.PlayerTimer;
import itoozh.core.util.NameTags;
import itoozh.core.util.TaskUtils;
import me.iwareq.fakeinventories.FakeInventories;

import java.io.File;

public class Main extends PluginBase {

    private static Main instance;

    private static SessionManager sessionManager;

    private ProfileManager profileManager;
    private GKitManager gKitManager;

    private static CommandManager commandManager;

    private static TeamManager teamManager;

    private static TimerManager timerManager;

    private static NameTags nameTags;

    private Gson gson;

    private static CrateManager crateManager;

    public static final String prefix = "§6§lGREEK §r§7| §r";

    public TimerManager getTimerManager() {
        return timerManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    private ScoreboardManager scoreboardManager;

    private PvPClassManager pvpClassManager;

    private HologramManager hologramManager;
    private CustomSignManager customSignManager;
    private RankManager rankManager;
    private AbilityManager abilityManager;

    @Override
    public void onEnable() {
        instance = this;

        File additionalFolder = new File(getDataFolder(), "data");

        if (!additionalFolder.exists()) {
            additionalFolder.mkdirs();
        }

        saveResource("config.yml", true);
        saveResource("language.yml", true);
        saveResource("scoreboard.yml", true);
        saveResource("ranks.yml", true);

        nameTags = new NameTags();

        gson = new GsonBuilder().setPrettyPrinting().create();

        rankManager = new RankManager(this);

        teamManager = new TeamManager(this);

        sessionManager = new SessionManager(this);

        timerManager = new TimerManager(this);

        pvpClassManager = new PvPClassManager(this);

        hologramManager = new HologramManager(this);

        crateManager = new CrateManager(this);

        gKitManager = new GKitManager(this);

        profileManager = new ProfileManager(this);

        commandManager = new CommandManager(this);
        customSignManager = new CustomSignManager(this);
        abilityManager = new AbilityManager(this);

        Entity.registerEntity("Item", ItemDrop.class, true);

        getServer().getPluginManager().registerEvents(new EventsListener(), this);
        getServer().getPluginManager().registerEvents(new EndListener(), this);
        getServer().getPluginManager().registerEvents(new PortalListener(), this);
        getServer().getPluginManager().registerEvents(new BorderListener(), this);
        getServer().getPluginManager().registerEvents(new GlitchListener(), this);
        getServer().getPluginManager().registerEvents(new FakeInventories(), this);

        getLogger().info(TextFormat.GREEN + "Enabled HCFactions by itoozh!");

        this.scoreboardManager = new ScoreboardManager();

        TaskUtils.executeScheduledAsync(this, 20/10, this::tick);
    }

    @Override
    public void onLoad() {
        FakeInventories.load();
    }

    public NameTags getNameTags() {
        return nameTags;
    }

    @Override
    public void onDisable() {
        sessionManager.save();
        teamManager.save();
        // hologramManager.stop();
        timerManager.save();
        crateManager.saveCrates();
        profileManager.saveProfiles();
        gKitManager.saveKits();
    }

    public Gson getGson() {
        return gson;
    }

    public static Main getInstance() {
        return instance;
    }

    public PvPClassManager getPvPClassManager() {
        return pvpClassManager;
    }
    public AbilityManager getAbilityManager() {
        return abilityManager;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public CrateManager getCrateManager() {
        return crateManager;
    }

    public GKitManager getGKitManager() {
        return gKitManager;
    }
    public RankManager getRankManager () {
        return rankManager;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }
    public CustomSignManager getCustomSignManager() {
        return customSignManager;
    }

    private void tick() {
        for (PlayerTimer timer : timerManager.getPlayerTimers().values()) {
            try {
                timer.tick();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Player player : this.getServer().getOnlinePlayers().values()) {
            Scoreboard board = this.getSessionManager().getSessionByUUID(player.getUniqueId()).getScoreboard();
            if (board == null) {
                continue;
            }
            try {
                board.refresh();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}