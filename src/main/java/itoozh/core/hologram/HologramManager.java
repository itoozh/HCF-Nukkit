package itoozh.core.hologram;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import itoozh.core.Main;
import itoozh.core.crate.Crate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HologramManager {

    public HologramManager(Main plugin) {
        Entity.registerEntity("Hologram", Hologram.class);
    }

    public void makeHologram(List<String> text, Location location, String name) {
        float y = 0.28F;
        location.getLevel().loadChunk(location.getFloorX(), location.getFloorZ());
        location.add(0.5, y, 0.5);
        List<String> textCopy = new ArrayList<>(text);
        Collections.reverse(textCopy);
        for (String line : textCopy) {
            y+=0.28F;
            if (location.getLevel().isChunkLoaded(location.getFloorX(), location.getFloorZ())) {
                Hologram hologram = new Hologram(location.add(0.5, y, 0.5), line, name);
                hologram.spawnToAll();
            }
        }
    }

    public void removeHologram(String name) {
        for (Entity entity : Server.getInstance().getDefaultLevel().getEntities()) {
            if (!(entity instanceof Hologram)) continue;
            Hologram hologram = (Hologram) entity;
            if (hologram.getIdentifierName().equals(name)) {
                hologram.despawnFromAll();
            }
        }
    }

    public void restartHologram() {
        for (Entity entity : Server.getInstance().getDefaultLevel().getEntities()) {
            if (!(entity instanceof Hologram)) continue;
            entity.despawnFromAll();
        }
        for (Map.Entry<Location, Crate> crate : Main.getInstance().getCrateManager().getPlacedCrates().entrySet()) {
            makeHologram(crate.getValue().getHologramText(), crate.getKey(), crate.getValue().getName());
        }
    }

    public void reloadHologram(String name, List<String> text) {
        for (Entity entity : Server.getInstance().getDefaultLevel().getEntities()) {
            if (!(entity instanceof Hologram)) continue;
            Hologram hologram = (Hologram) entity;
            Location location = hologram.getLocation();
            if (hologram.getIdentifierName().equals(name)) {
                removeHologram(name);
                makeHologram(text, location, name);
            }
        }
    }
}
