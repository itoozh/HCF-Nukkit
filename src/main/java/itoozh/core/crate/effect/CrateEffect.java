package itoozh.core.crate.effect;

import cn.nukkit.level.Location;
import cn.nukkit.level.particle.*;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CrateEffect {

    RED_EFFECT("Red Effect") {
        @Override
        public void tick(Location location) {
            double radius = 0.8;

            for (int i = 0; i < 360; i += 360 / 8) {
                double angle = Math.toRadians(i);
                double xOffset = radius * Math.cos(angle);
                double zOffset = radius * Math.sin(angle);

                Location particleLocation = location.clone().add(xOffset, 0, zOffset);
                location.getLevel().addParticle(new RedstoneParticle(particleLocation));
            }
        }
    },

    ENCHANT_CIRCLE("Enchant Circle") {
        @Override
        public void tick(Location location) {
            double radius = 0.8;

            for (int i = 0; i < 360; i += 360 / 8) {
                double angle = Math.toRadians(i);
                double xOffset = radius * Math.cos(angle);
                double zOffset = radius * Math.sin(angle);

                Location particleLocation = location.clone().add(xOffset, 0, zOffset);
                location.getLevel().addParticle(new EnchantmentTableParticle(particleLocation));
            }
        }
    },

    HEARTH_EFFECT("Heart Circle") {
        @Override
        public void tick(Location location) {
            double radius = 0.8;

            for (int i = 0; i < 360; i += 360 / 8) {
                double angle = Math.toRadians(i);
                double xOffset = radius * Math.cos(angle);
                double zOffset = radius * Math.sin(angle);

                Location particleLocation = location.clone().add(xOffset, 0, zOffset);
                location.getLevel().addParticle(new HeartParticle(particleLocation));
            }
        }
    },

    WEATHER_EFFECT("Weather Effect") {
        @Override
        public void tick(Location location) {
            double radius = 0.8;

            for (int i = 0; i < 360; i += 360 / 8) {
                double angle = Math.toRadians(i);
                double xOffset = radius * Math.cos(angle);
                double zOffset = radius * Math.sin(angle);

                Location particleLocation = location.clone().add(xOffset, 0, zOffset);
                location.getLevel().addParticle(new WaterDripParticle(particleLocation));
            }
        }
    },

    LAVA_RINGS("Lava Rings") {
        @Override
        public void tick(Location location) {
            for (int i = 0; i < 360; i += 360 / 4) {
                double angle = Math.toRadians(i);
                double x = 0.4 * Math.cos(angle);
                double z = 0.4 * Math.sin(angle);

                location.add(x, 0, z);
                location.getLevel().addParticle(new LavaParticle(location));
                location.subtract(x, 0, z);
            }
        }
    };



    public final String name;

    public abstract void tick(Location location);
}
