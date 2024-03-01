package itoozh.core.signs.economy;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.item.Item;
import cn.nukkit.scheduler.Task;
import cn.nukkit.scheduler.TaskHandler;
import itoozh.core.signs.CustomSign;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class EconomySign  extends CustomSign {
    private final Map<UUID, TaskHandler> taskCache;
    private final Map<String, Item> cache;

    public EconomySign(List<String> lines) {
        super(lines);
        this.cache = new HashMap<>();
        this.taskCache = new HashMap<>();
    }

    public Item getItemStack(String stack) {
        if (this.cache.containsKey(stack)) {
            return this.cache.get(stack);
        }
        if (stack.contains(":")) {
            String[] lines = stack.split(":");
            Item itemStack = Item.get(Integer.parseInt(lines[0]), Integer.valueOf(lines[1]));
            this.cache.put(stack, itemStack);
            return itemStack;
        }
        Item material = Item.fromString(stack);
        this.cache.put(stack, material);
        return material;
    }

    @Override
    public void onClick(Player player, BlockEntitySign sign) {
    }

    public void sendSignChange(Player player, BlockEntitySign sign, String[] lines) {
        if (this.taskCache.containsKey(player.getUniqueId())) {
            return;
        }

        String[] oldLines = sign.getText();
        sign.setText(lines);

        TaskHandler task = Server.getInstance().getScheduler().scheduleDelayedTask(new Task() {
            @Override
            public void onRun(int currentTick) {
                sign.setText(oldLines);
                EconomySign.this.taskCache.remove(player.getUniqueId());
            }
        }, 40);

        this.taskCache.put(player.getUniqueId(), task);
    }
}
