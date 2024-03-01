package itoozh.core.session.timer;

import cn.nukkit.event.Listener;
import itoozh.core.ability.Ability;
import itoozh.core.ability.AbilityManager;
import itoozh.core.timer.TimerManager;
import itoozh.core.timer.type.PlayerTimer;

public class AbilityTimer extends PlayerTimer implements Listener {
    private final Ability ability;
    public AbilityTimer(TimerManager timerManager, Ability ability, String name) {
        super(timerManager, false, ability.getName().replaceAll(" ", ""), name, AbilityManager.dataFile.getInt(ability.getNameConfig() + ".COOLDOWN"));
        this.ability = ability;
    }
    public Ability getAbility() {
        return this.ability;
    }
}
