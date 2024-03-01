package itoozh.core.pvpclass.listener;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.potion.PotionApplyEvent;
import cn.nukkit.potion.Effect;
import itoozh.core.Main;
import itoozh.core.event.TimerExpireEvent;
import itoozh.core.pvpclass.PvPClass;
import itoozh.core.session.timer.WarmupTimer;
import itoozh.core.util.TaskUtils;

public class PvPClassListener implements Listener {
    public PvPClassListener() {
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPlayedBefore()) {
            Main.getInstance().getPvPClassManager().checkArmor(player);
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        PvPClass pvpClass = Main.getInstance().getPvPClassManager().getActiveClass(player);
        if (pvpClass != null) {
            pvpClass.addEffects(player);
        }
    }

    @EventHandler
    public void onEffectAdded(PotionApplyEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) return;
        Player player = (Player) entity;

        Effect effect = event.getApplyEffect();
        if (effect != null && effect.getId() == Effect.INVISIBILITY)
            TaskUtils.executeLater(10, () -> Main.getInstance().getNameTags().updateInvisTag(player));

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PvPClass pvpClass = Main.getInstance().getPvPClassManager().getActiveClass(player);
        if (pvpClass != null) {
            pvpClass.unEquip(player);
        }
    }

    @EventHandler
    public void onExpire(TimerExpireEvent event) {
        if (!(event.getPlayerTimer() instanceof WarmupTimer)) {
            return;
        }
        try {
            Player player = Server.getInstance().getPlayer(event.getPlayer()).get();
            WarmupTimer timer = (WarmupTimer) event.getPlayerTimer();
            PvPClass pvpClass = timer.getWarmups().get(player.getUniqueId());
            if (pvpClass != null) {
                timer.getWarmups().remove(player.getUniqueId()).equip(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
