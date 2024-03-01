package itoozh.core.pvpclass.type.rogue;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;

import javax.annotation.Nonnull;

public class RogueBackstabEvent extends EntityDamageByEntityEvent {
    public RogueBackstabEvent(@Nonnull Entity damager, @Nonnull Entity entity, @Nonnull DamageCause cause, float damage) {
        super(damager, entity, cause, damage);
    }
}
