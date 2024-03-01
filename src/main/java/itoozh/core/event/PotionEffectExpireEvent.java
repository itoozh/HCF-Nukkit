package itoozh.core.event;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.potion.Effect;
import lombok.Getter;

@Getter
public class PotionEffectExpireEvent extends Event {
    @Getter
    private static final HandlerList handlers = new HandlerList();
    private Entity entity;
    @Getter
    private Effect effect;

    public PotionEffectExpireEvent(Effect effect, Entity entity) {
        this.effect = effect;
        this.entity = entity;
    }

}
