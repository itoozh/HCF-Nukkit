package itoozh.core.gkit;

import cn.nukkit.Player;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.scheduler.TaskHandler;
import itoozh.core.Main;
import me.iwareq.fakeinventories.FakeInventory;

public class InventoryGKit extends FakeInventory {

    public TaskHandler getTaskHandler() {
        return taskHandler;
    }

    public void setTaskHandler(TaskHandler taskHandler) {
        this.taskHandler = taskHandler;
    }

    private TaskHandler taskHandler = null;

    public InventoryGKit(InventoryType inventoryType) {
        super(inventoryType);
    }

    public InventoryGKit(InventoryType inventoryType, String title) {
        super(inventoryType, title);
    }

    @Override
    public void onClose(Player player) {
        super.onClose(player);
        if (taskHandler != null) {
            taskHandler.cancel();
        }
    }

    public void update(Player player) {
        this.clearAll();
        for (GKit gkit : Main.getInstance().getGKitManager().getKitMap().values()) {
            if (gkit.getSlot() == -1) {
                Item item = gkit.getIcon(player);
                this.addItem(item);
                continue;
            }
            this.setItem(gkit.getSlot(), gkit.getIcon(player));
        }
    }
}
