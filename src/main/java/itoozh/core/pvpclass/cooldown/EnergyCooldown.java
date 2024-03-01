package itoozh.core.pvpclass.cooldown;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EnergyCooldown {
    private UUID player;
    private long energy;
    private long maxEnergy;

    public EnergyCooldown(UUID uuid, int maxEnergy) {
        this.player = uuid;
        this.energy = System.currentTimeMillis() - 1000L;
        this.maxEnergy = maxEnergy;
    }

    public boolean checkEnergy(int energy) {
        return this.getEnergy() < energy;
    }

    public void takeEnergy(int energy) {
        long localEnergy = this.getEnergy();
        long time = (localEnergy - energy) * 1000L;
        this.energy = System.currentTimeMillis() - time;
    }

    public long getEnergy() {
        long time = (System.currentTimeMillis() - this.energy) / 1000L;
        return Math.min(this.maxEnergy, time);
    }
}
