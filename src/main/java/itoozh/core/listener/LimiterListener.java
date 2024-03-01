package itoozh.core.listener;

import cn.nukkit.event.Listener;

public class LimiterListener implements Listener {
    /*private static Method GET_POTION_DATA;

    static {
        LimiterListener.GET_POTION_DATA = null;
    }

    private final Map<Enchantment, Integer> enchantmentLimits;
    private final Map<Effect, PotionLimit> potionLimits;

    public LimiterListener() {
        this.potionLimits = new HashMap<>();
        this.enchantmentLimits = new HashMap<>();
        this.load();
    }

    private void cancelBookEnchant(EnchantmentStorageMeta storageMeta) {
        for (Map.Entry<Enchantment, Integer> entry : storageMeta.getStoredEnchants().entrySet()) {
            Enchantment enchantment = entry.getKey();
            Integer limit = this.enchantmentLimits.get(enchantment);
            Integer value = entry.getValue();
            if (limit != null && limit != -1) {
                if (limit == 0) {
                    storageMeta.removeStoredEnchant(enchantment);
                } else {
                    if (value <= limit) {
                        continue;
                    }
                    storageMeta.addStoredEnchant(enchantment, limit, true);
                }
            }
        }
    }

    private void cancelItemEnchant(Item stack) {
        for (Map.Entry<Enchantment, Integer> entry : stack.getEnchantments().entrySet()) {
            Enchantment enchantment = entry.getKey();
            Integer value = entry.getValue();
            Integer limit = this.enchantmentLimits.get(enchantment);
            if (limit != null && limit != -1) {
                if (limit == 0) {
                    stack.removeEnchantment(enchantment);
                } else {
                    if (value <= limit) {
                        continue;
                    }
                    stack.addUnsafeEnchantment(enchantment, limit);
                }
            }
        }
    }

    @EventHandler
    public void onAnvil(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory == null || !inventory.getType().equals(InventoryType.ANVIL)) {
            return;
        }
        if (event.getSlotType() != InventoryType.SlotType.RESULT) {
            return;
        }
        ItemStack stack = event.getCurrentItem();
        if (stack.hasMeta() && stack.getMeta() instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) stack.getMeta();
            this.cancelBookEnchant(storageMeta);
            stack.setMeta(storageMeta);
            return;
        }
        if (stack.hasMeta() && stack.getMeta().hasEnchants()) {
            this.cancelItemEnchant(stack);
        }
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (!(event.getCaught() instanceof Item)) {
            return;
        }
        ItemStack stack = ((Item) event.getCaught()).getItem();
        if (stack.hasMeta() && stack.getMeta() instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) stack.getMeta();
            this.cancelBookEnchant(storageMeta);
            stack.setMeta(storageMeta);
            return;
        }
        this.cancelItemEnchant(stack);
    }

    private void load() {
        for (Enchantment enchantment : Enchantment.values()) {
            if (enchantment == null) continue;
            int limit = this.getLimitersConfig().getInt("ENCHANTMENTS." + enchantment.getName());
            this.enchantmentLimits.put(enchantment, limit);
        }
        for (Effect type : Effect.values()) {
            if (type == null) continue;
            boolean enabled = this.getLimitersConfig().getBoolean("POTIONS." + type.getName() + ".ENABLED");
            boolean upgradeable = this.getLimitersConfig().getBoolean("POTIONS." + type.getName() + ".UPGRADABLE");
            boolean extend = this.getLimitersConfig().getBoolean("POTIONS." + type.getName() + ".EXTENDED");
            this.potionLimits.put(type, new PotionLimit(enabled, upgradeable, extend));
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        Map<Enchantment, Integer> enchants = event.getEnchantsToAdd();
        Iterator<Map.Entry<Enchantment, Integer>> iterator = enchants.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Enchantment, Integer> entry = iterator.next();
            Enchantment enchantment = entry.getKey();
            Integer value = entry.getValue();
            Integer limit = this.enchantmentLimits.get(entry.getKey());
            if (limit != null && limit != -1) {
                if (limit == 0) {
                    iterator.remove();
                } else {
                    if (value <= limit) {
                        continue;
                    }
                    enchants.put(enchantment, limit);
                }
            }
        }
    }

    @EventHandler
    public void onBrew(BrewEvent event) {
        BrewerInventory inventory = event.getContents();
        Tasks.execute(this.getManager(), () -> {
            for (int i = 0; i < 3; ++i) {
                ItemStack stack = inventory.getItem(i);
                if (stack == null || !this.cancelPotion(stack)) continue;
                inventory.setItem(i, ItemStack.get(Item.AIR));
            }
        });
    }

    @SneakyThrows
    public boolean cancelPotion(ItemStack stack) {
        if (!stack.getName().contains("POTION")) {
            return false;
        }
        if (this.getInstance().getVersionManager().isVer16()) {
            PotionMeta meta;
            PotionData data;
            PotionLimit limit;
            if (!stack.hasMeta() || !(stack.getMeta() instanceof PotionMeta)) {
                return false;
            }
            if (GET_POTION_DATA == null) {
                GET_POTION_DATA = ReflectionUtils.accessMethod(PotionMeta.class, "getBasePotionData");
            }
            if ((limit = this.potionLimits.get((data = (PotionData) GET_POTION_DATA.invoke(stack.getMeta(), new Object[0])).getEffectType())) == null) {
                return false;
            }
            if (!limit.isEnabled()) {
                return true;
            }
            if (!limit.isUpgradable() && data.isUpgraded()) {
                return true;
            }
            return !limit.isExtended() && data.isExtended();
        }
        Potion potion = Potion.fromItemStack(stack);
        for (PotionEffect effect : potion.getEffects()) {
            PotionLimit limit = this.potionLimits.get(effect.getType());
            if (limit == null) continue;
            if (!limit.isEnabled()) {
                return true;
            }
            if (!limit.isUpgradable() && effect.getAmplifier() >= 1) {
                return true;
            }
            return !limit.isExtended() && potion.hasExtendedDuration();
        }
        return false;
    }

    @EventHandler
    public void onEffect(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Potion)) {
            return;
        }
        Potion potion = (Potion) event.getEntity();
        Player player = Utils.getDamager(potion);
        ItemStack stack = potion.getItem();
        if (player == null) {
            return;
        }
        if (stack == null) {
            return;
        }
        if (this.cancelPotion(stack)) {
            event.setCancelled();
            player.sendMessage(this.getLanguageConfig().getString("LIMITER_LISTENER.DENIED_POTION"));
        }
    }

    @Data
    private static class PotionLimit {
        private final boolean enabled;
        private final boolean extended;
        private final boolean upgradable;
    }*/
}
