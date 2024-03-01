package itoozh.core.gkit.profile;

import itoozh.core.gkit.GKit;
import itoozh.core.gkit.GKitCooldown;
import itoozh.core.gkit.GKitUses;
import itoozh.core.util.LanguageUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Profile {

    private Set<GKitCooldown> gKitCooldowns = new HashSet<>();

    private Set<GKitUses> gKitUsesMap = new HashSet<>();

    public Set<GKitCooldown> getgKitCooldowns() {
        return gKitCooldowns;
    }

    public Set<GKitUses> getgKitUsesMap() {
        return gKitUsesMap;
    }

    public UUID getUuid() {
        return uuid;
    }

    private final UUID uuid;

    public Profile(UUID uuid) {
        this.uuid = uuid;
    }

    public void resetCooldowns() {
        gKitCooldowns = new HashSet<>();
    }

    public void applyCooldown(GKit gKit) {
        GKitCooldown existingCooldown = getCooldownFor(gKit);
        if (existingCooldown != null) {
            existingCooldown.setRemaining(System.currentTimeMillis());
        } else {
            gKitCooldowns.add(new GKitCooldown(gKit, System.currentTimeMillis()));
        }
    }

    public void setUses(GKit gKit, long uses) {
        GKitUses existingCooldown = getUsesFor(gKit);
        if (existingCooldown != null) {
            existingCooldown.setAmount(uses);
        } else {
            gKitUsesMap.add(new GKitUses(gKit, uses));
        }
    }

    public long getUses(GKit gKit) {
        GKitUses gKitUses1 = getUsesFor(gKit);
        if (gKitUses1 == null)
            return gKit.getFreeUses();

        return gKitUses1.getAmount();
    }

    public String formatRemaining(GKit gKit) {
        if (!isOnCooldown(gKit))
            return "No cooldown";

        GKitCooldown gKitCooldown = getCooldownFor(gKit);
        if (gKitCooldown != null)
            return LanguageUtils.formatTimeShort(gKitCooldown.getRemaining() + gKit.getCoolDown()
                    - System.currentTimeMillis());

        return "0s";
    }

    public long getCooldown(GKit gKit) {
        GKitCooldown gKitCooldown = getCooldownFor(gKit);
        if (gKitCooldown == null)
            return Long.MAX_VALUE;

        return gKitCooldown.getRemaining() + gKit.getCoolDown()
                - System.currentTimeMillis();
    }

    public boolean isOnCooldown(GKit gkit) {
        GKitCooldown gKitCooldown = getCooldownFor(gkit);
        return gKitCooldown != null && gKitCooldown.getRemaining() + gkit.getCoolDown() > System.currentTimeMillis();
    }

    public GKitCooldown getCooldownFor(GKit gKit) {
        for (GKitCooldown gKitCooldown : gKitCooldowns) {
            if (gKitCooldown.getGKit() == null)
                continue;

            if (gKitCooldown.getGKit().getName().equals(gKit.getName()))
                return gKitCooldown;
        }
        return null;
    }

    public GKitUses getUsesFor(GKit gKit) {
        for (GKitUses gKitUsesPer : gKitUsesMap) {
            if (gKitUsesPer.getGKit() == null)
                continue;

            if (gKitUsesPer.getGKit().getName().equals(gKit.getName()))
                return gKitUsesPer;
        }
        GKitUses gKitUses = new GKitUses(gKit, gKit.getFreeUses());
        gKitUsesMap.add(gKitUses);
        return gKitUses;
    }

    public void setgKitCooldowns(Set<GKitCooldown> gKitCooldowns) {
        this.gKitCooldowns = gKitCooldowns;
    }

    public void setgKitUsesMap(Set<GKitUses> gKitUsesMap) {
        this.gKitUsesMap = gKitUsesMap;
    }
}
