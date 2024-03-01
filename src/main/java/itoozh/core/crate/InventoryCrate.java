package itoozh.core.crate;

import cn.nukkit.Player;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import me.iwareq.fakeinventories.FakeInventory;

public class InventoryCrate extends FakeInventory {

    private Crate crate;
    public InventoryCrate(InventoryType inventoryType, Crate crate) {
        super(inventoryType);
        this.crate = crate;
    }

    public InventoryCrate(InventoryType inventoryType, String title, Crate crate) {
        super(inventoryType, title);
        this.crate = crate;
    }

    @Override
    public void onClose(Player player) {
        super.onClose(player);
        crate.setRewards(this.getContents());
        Main.getInstance().getCrateManager().addCrate(crate);
        player.sendMessage(TextFormat.colorize("&aSuccessfully configuration " + crate.getName() + " crate!"));
    }
}
