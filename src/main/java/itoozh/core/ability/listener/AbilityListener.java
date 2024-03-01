package itoozh.core.ability.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import itoozh.core.Main;
import itoozh.core.ability.Ability;
import itoozh.core.ability.AbilityManager;
import itoozh.core.util.LanguageUtils;

public class AbilityListener implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Item stack = player.getInventory().getItemInHand();
        for (Ability ability : Main.getInstance().getAbilityManager().getAbilities().values()) {
            if (!ability.hasAbilityInHand(player)) {
                continue;
            }
            event.setCancelled(true);
            break;
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player damager = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();
        Item stack = damager.getInventory().getItemInHand();
        if (stack.getId() == Item.AIR) {
            return;
        }
        if (!stack.hasCustomName()) {
            return;
        }
        for (Ability ability : Main.getInstance().getAbilityManager().getAbilities().values()) {
            if (!ability.hasAbilityInHand(damager)) {
                continue;
            }
            if (ability.getUseType() != AbilityManager.AbilityUseType.HIT_PLAYER) {
                continue;
            }
            ability.onHit(damager, damaged);
            break;
        }
    }

    @EventHandler
    public void onCooldown(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Item stack = player.getInventory().getItemInHand();
        if (stack.getId() == Item.AIR) {
            return;
        }
        if (!stack.hasCustomName()) {
            return;
        }
        if (event.getAction().name().contains("RIGHT")) {
            for (Ability ability : Main.getInstance().getAbilityManager().getAbilities().values()) {
                if (!ability.hasAbilityInHand(player)) {
                    continue;
                }
                if (ability.getUseType() != AbilityManager.AbilityUseType.INTERACT) {
                    continue;
                }
                event.setCancelled(true);
                ability.onClick(player);
                break;
            }
        } else if (event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            for (Ability ability : Main.getInstance().getAbilityManager().getAbilities().values()) {
                /*if (ability instanceof PocketBardAbility) {
                    PocketBardAbility pockedBardAbility = (PocketBardAbility) ability;
                    if (pockedBardAbility.getPocketBardInHand(player) != null && pockedBardAbility.getAbilityCooldown().hasTimer(player)) {
                        player.sendMessage(this.getLanguageConfig().getString("ABILITIES.COOLDOWN").replaceAll("%ability%", ability.getDisplayName()).replaceAll("%time%", ability.getAbilityCooldown().getRemainingString(player)));
                        break;
                    }
                }*/
                if (ability.hasAbilityInHand(player) && ability.getAbilityCooldown().hasTimer(player)) {
                    player.sendMessage(LanguageUtils.getString("ABILITIES.COOLDOWN").replaceAll("%ability%", ability.getDisplayName()).replaceAll("%time%", ability.getAbilityCooldown().getRemainingString(player)));
                    break;
                }
            }
        }
    }
}
