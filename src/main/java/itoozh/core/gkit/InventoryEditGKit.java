package itoozh.core.gkit;

import cn.nukkit.Player;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import me.iwareq.fakeinventories.FakeInventory;

import java.util.Map;

public class InventoryEditGKit extends FakeInventory {

    public InventoryEditGKit(InventoryType inventoryType) {
        super(inventoryType);
    }

    public InventoryEditGKit(InventoryType inventoryType, String title) {
        super(inventoryType, title);
    }

    @Override
    public void onOpen(Player player) {
        super.onOpen(player);
        for (GKit gkit : Main.getInstance().getGKitManager().getKitMap().values()) {
            if (gkit.getSlot() == -1) {
                Item item = gkit.getIcon(player);
                this.addItem(item);
                continue;
            }
            this.setItem(gkit.getSlot(), gkit.getIcon(player));
        }

    }

    @Override
    public void onClose(Player player) {
        super.onClose(player);
        for (Map.Entry<Integer, Item> entry : this.getContents().entrySet()) {
            if (entry.getValue().getNamedTagEntry("kitName") != null) {
                String kitName = entry.getValue().getNamedTag().getString("kitName");
                Main.getInstance().getGKitManager().getGKit(kitName).setSlot(entry.getKey());
            }
        }
        player.sendMessage(TextFormat.colorize("&aKits organization has been updated!"));
    }
}
