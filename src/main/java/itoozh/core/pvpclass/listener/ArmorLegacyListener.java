package itoozh.core.pvpclass.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityArmorChangeEvent;
import cn.nukkit.event.entity.EntityEffectRemoveEvent;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import cn.nukkit.potion.Effect;
import cn.nukkit.scheduler.Task;
import cn.nukkit.scheduler.TaskHandler;
import itoozh.core.Main;
import itoozh.core.event.PotionEffectExpireEvent;
import itoozh.core.pvpclass.PvPClass;
import itoozh.core.util.TaskUtils;

import java.util.HashMap;
import java.util.Map;

public class ArmorLegacyListener implements Listener {

    public Map<Player, TaskHandler> effectTasks = new HashMap<>();

    @EventHandler
    public void onInventoryTransaction(InventoryTransactionEvent event) {
        for (InventoryAction action : event.getTransaction().getActions()) {
            if (action instanceof SlotChangeAction) {
                Item sourceItem = action.getSourceItem();
                Item targetItem = action.getTargetItem();
                int slot = ((SlotChangeAction) action).getSlot();
                if (slot == 36 || slot == 37 || slot == 38 || slot == 39) {
                    if (sourceItem != null && targetItem != null && sourceItem.getId() == targetItem.getId()) {
                        return;
                    }
                    Main.getInstance().getServer().getScheduler().scheduleDelayedTask(new Task() {
                        @Override
                        public void onRun(int currentTick) {
                            Main.getInstance().getPvPClassManager().checkArmor(event.getTransaction().getSource());
                        }
                    }, 1);
                }
            }
        }
    }

    @EventHandler
    public void onEquip(EntityArmorChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getNewItem() != null && event.getOldItem() != null && event.getNewItem().getId() == event.getOldItem().getId()) {
            return;
        }
        Main.getInstance().getServer().getScheduler().scheduleDelayedTask(new Task() {
            @Override
            public void onRun(int currentTick) {
                Main.getInstance().getPvPClassManager().checkArmor((Player) event.getEntity());
            }
        }, 1);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEquipFix(EntityArmorChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        Item stack = event.getOldItem();
        if (stack == null) {
            return;
        }
        Main.getInstance().getServer().getScheduler().scheduleDelayedTask(new Task() {
            @Override
            public void onRun(int currentTick) {
                Main.getInstance().getPvPClassManager().getRestores().rowKeySet().remove(player.getUniqueId());
            }
        }, 1);
    }

    @EventHandler
    public void onPotionRemove(PotionEffectExpireEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (!player.isOnline()) return;

        Effect effect = event.getEffect();
        if (effect == null) {
            return;
        }

        TaskUtils.executeLater(5, () -> {
            PvPClass pvpClass = Main.getInstance().getPvPClassManager().getActiveClass(player);
            if (pvpClass != null) {
                for (Effect effect1 : pvpClass.getEffects()) {
                    if (effect1.getId() == effect.getId()) {
                        player.addEffect(effect1);
                    }
                }
            }
        });
    }

    @EventHandler
    public void onEffect(EntityEffectRemoveEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        Effect effect = event.getRemoveEffect();
        if (effect == null) {
            return;
        }
        Effect effect2 = Main.getInstance().getPvPClassManager().getRestores().remove(player.getUniqueId(), effect.getId());
        if (effect2 != null) {
            player.addEffect(effect2);
        }
    }
}
