package itoozh.core.gkit;

import lombok.Data;

@Data
public class GKitUses {

    private final GKit gKit;
    private long amount;

    public GKitUses(GKit gKit, long amount) {
        this.gKit = gKit;
        this.amount = amount;
    }

    public GKit getGKit() {
        return gKit;
    }
}
