package itoozh.core.entities;

import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.entity.ItemDespawnEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class ItemDrop extends EntityItem {
    public ItemDrop(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.age > 1200) {
            ItemDespawnEvent ev = new ItemDespawnEvent(this);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                this.age = 0;
            } else {
                this.close();
                return true;
            }
        }

        return super.onUpdate(currentTick);
    }
}