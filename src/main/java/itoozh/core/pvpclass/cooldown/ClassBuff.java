package itoozh.core.pvpclass.cooldown;

import cn.nukkit.potion.Effect;
import itoozh.core.pvpclass.PvPClass;
import lombok.Getter;

@Getter
public class ClassBuff extends CustomCooldown {
    private final Effect effect;
    private final int cooldown;

    public ClassBuff(PvPClass pvpClass, String name, Effect effect, int cooldown) {
        super(pvpClass, name);
        this.effect = effect;
        this.cooldown = cooldown;
    }

}