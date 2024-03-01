package itoozh.core.entities;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDoor;
import cn.nukkit.block.BlockFenceGate;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Iterator;

public class EnderPearl extends EntityProjectile {
    public static final int NETWORK_ID = 87;
    private BlockVector3 last;
    private boolean itemGivenBack = false;

    public EnderPearl(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public int getNetworkId() {
        return 87;
    }

    public float getWidth() {
        return 0.25F;
    }

    public float getLength() {
        return 0.25F;
    }

    public float getHeight() {
        return 0.25F;
    }

    protected float getGravity() {
        return 0.03F;
    }

    public EnderPearl(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    protected float getDrag() {
        return 0.01F;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) return false;

        if (last == null) {
            Block collided = this.getLevel().getBlock(getPosition());
            last = collided.asBlockVector3();
            if (collided instanceof BlockFenceGate) {
                if (!((BlockFenceGate) collided).isOpen()) {
                    this.level.addSound(this, Sound.MOB_ENDERMEN_PORTAL);
                    this.close();

                    return false;
                }
            }
            if (collided instanceof BlockDoor) {
                if (!((BlockDoor) collided).isOpen()) {
                    this.level.addSound(this, Sound.MOB_ENDERMEN_PORTAL);
                    this.close();

                    return false;
                }
            }
        }
        if (last.distanceSquared(asBlockVector3()) >= 1) {
            Block collided = this.getLevel().getBlock(getPosition());
            if (collided.getId() == 90) {
                teleport();
                this.close();

                return false;
            }
            if (collided instanceof BlockFenceGate) {
                if (((BlockFenceGate) collided).isOpen()) {
                    teleport();
                } else {
                    teleport(last);
                }
                this.close();

                return false;
            }

            if (collided instanceof BlockDoor) {
                if (((BlockDoor) collided).isOpen()) {
                    teleport();
                } else {
                    teleport(last);
                }
                this.close();

                return false;
            }

            last = collided.asBlockVector3();
        }

        return doUpdate(currentTick);
    }

    protected boolean doUpdate(int currentTick) {
        if (this.closed) {
            return false;
        } else {
            boolean hasUpdate = super.onUpdate(currentTick);
            if (this.isCollided && this.shootingEntity instanceof Player) {
                boolean portal = false;
                Iterator var4 = getCollisionBlocks().iterator();

                while (var4.hasNext()) {
                    Block collided = (Block) var4.next();
                    if (collided.getId() == 90) {
                        portal = true;
                    }
                }

                if (!portal) {
                    teleport();
                }
            }

            if (this.age > 1200 || this.isCollided) {
                kill();
                hasUpdate = true;
            }

            return hasUpdate;
        }
    }

    @Override
    public void onCollideWithEntity(Entity entity) {
        teleport();
        super.onCollideWithEntity(entity);
    }

    private void teleport() {
        this.shootingEntity.teleport(new Vector3((double) NukkitMath.floorDouble(this.x) + 0.5D, this.y, (double) NukkitMath.floorDouble(this.z) + 0.5D), PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
        if ((((Player) this.shootingEntity).getGamemode() & 1) == 0) {
            this.shootingEntity.resetFallDistance();
            this.shootingEntity.attack(new EntityDamageByEntityEvent(this, this.shootingEntity, EntityDamageEvent.DamageCause.PROJECTILE, 5.0F, 0.0F));
        }

        this.level.addSound(this, Sound.MOB_ENDERMEN_PORTAL);
    }

    private void teleport(BlockVector3 last) {
        this.shootingEntity.teleport(new Vector3((double) NukkitMath.floorDouble(last.x) + 0.5D, last.y, (double) NukkitMath.floorDouble(last.z) + 0.5D), PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
        if ((((Player) this.shootingEntity).getGamemode() & 1) == 0) {
            this.shootingEntity.resetFallDistance();
            this.shootingEntity.attack(new EntityDamageByEntityEvent(this, this.shootingEntity, EntityDamageEvent.DamageCause.PROJECTILE, 5.0F, 0.0F));
        }

        this.level.addSound(this, Sound.MOB_ENDERMEN_PORTAL);
    }
}