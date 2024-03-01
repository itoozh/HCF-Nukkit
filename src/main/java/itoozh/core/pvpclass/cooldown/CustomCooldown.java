package itoozh.core.pvpclass.cooldown;

import itoozh.core.Main;
import itoozh.core.pvpclass.PvPClass;
import itoozh.core.util.Cooldown;
import lombok.Getter;

@Getter
public class CustomCooldown extends Cooldown {
    private final String displayName;

    public CustomCooldown(PvPClass pvpClass, String name) {
        super(Main.getInstance());
        this.displayName = (name.isEmpty() ? null : name);
        pvpClass.getCustomCooldowns().add(this);
    }

}