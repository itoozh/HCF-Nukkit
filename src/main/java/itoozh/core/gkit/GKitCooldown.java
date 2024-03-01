package itoozh.core.gkit;

public class GKitCooldown {

    private final GKit gKit;
    private long remaining;

    public GKitCooldown(GKit gKit, long remaining) {
        this.gKit = gKit;
        this.remaining = remaining;
    }

    public GKit getGKit() {
        return gKit;
    }

    public long getRemaining() {
        return remaining;
    }

    public void setRemaining(long remaining) {
        this.remaining = remaining;
    }
}
