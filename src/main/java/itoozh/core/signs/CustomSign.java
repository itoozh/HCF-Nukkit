package itoozh.core.signs;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntitySign;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class CustomSign {
    protected List<String> lines;

    public CustomSign(List<String> lines) {
        this.lines = lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public abstract void onClick(Player player, BlockEntitySign sign);

    public Integer getIndex(String input) {
        return this.lines.indexOf(this.lines.stream().filter(s -> s.toLowerCase().contains(input)).findFirst().orElse(null));
    }
}
