package itoozh.core.pvpclass.task;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.potion.Effect;
import cn.nukkit.scheduler.Task;
import itoozh.core.event.PotionEffectExpireEvent;

public class ExecuteExpireTask extends Task {

    public Effect effect;
    public Player player;

    public ExecuteExpireTask(Effect effect, Player player) {
        this.effect = effect;
        this.player = player;
    }

    @Override
    public void onRun(int currentTick) {
        Server.getInstance().getPluginManager().callEvent(new PotionEffectExpireEvent(effect, player));
    }
}
