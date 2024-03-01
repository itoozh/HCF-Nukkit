package itoozh.core.hologram;

import cn.nukkit.entity.mob.EntitySilverfish;
import cn.nukkit.level.Location;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.TextFormat;

import java.util.Objects;

public class Hologram extends EntitySilverfish {

    public String getIdentifierName() {
        return identifierName;
    }

    private String identifierName;

    private String text;

    public Hologram(Location location, String text, String name) {
        super(location.getChunk(), new CompoundTag()
                .putList(new ListTag<>("Pos")
                        .add(new DoubleTag("0", location.x))
                        .add(new DoubleTag("1", location.y))
                        .add(new DoubleTag("2", location.z)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("0", 0))
                        .add(new DoubleTag("1", 0))
                        .add(new DoubleTag("2", 0)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("0", (float) 0))
                        .add(new FloatTag("1", (float) 0))));
        this.setPositionAndRotation(location, 0, 0, 0);
        this.text = text;
        this.identifierName = name;
        this.setImmobile(true);
        this.setNameTagAlwaysVisible(true);
        this.setScale(0.0001F);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (Objects.equals(text, "")) {
            this.despawnFromAll();
        }
        this.setNameTag(getText());
        return super.onUpdate(currentTick);
    }

    private String getText() {
        return TextFormat.colorize(text);
    }

    public void setText(String text) {
        this.text = text;
    }
}
