package itoozh.core.command.gkit;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.scheduler.Task;
import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseCommand;
import itoozh.core.gkit.GKit;
import itoozh.core.gkit.InventoryGKit;
import itoozh.core.gkit.profile.Profile;
import itoozh.core.util.LanguageUtils;

public class GKitCommand extends BaseCommand {
    public GKitCommand() {
        super("gkit", "Use this command to manage the kits", "", new String[0]);
        this.setPermission("core.gkit.menu");
    }

    @Override
    public void sendUsageMessage(CommandSender sender) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        InventoryGKit inventory = new InventoryGKit(InventoryType.DOUBLE_CHEST, TextFormat.colorize("&6GKits"));
        Profile profile = Main.getInstance().getProfileManager().getProfile(player.getUniqueId());

        inventory.setDefaultItemHandler((item, event) -> {
            event.setCancelled(true);
            if (item.getNamedTagEntry("kitName") != null) {
                String kitName = item.getNamedTag().getString("kitName");
                GKit gKit = Main.getInstance().getGKitManager().getGKit(kitName);
                if (player.hasPermission("gkit.cooldown.bypass")) {
                    Server.getInstance().getLogger().info(player.getName() + " has bypassed " + gKit.getName() + " cooldown.");
                    gKit.apply(player);
                    return;
                }
                boolean reduceUses = false;
                if (!player.hasPermission("use.gkit.all")) {
                    if (!player.hasPermission("use.gkit." + this.getName())) {
                        if (profile.getUsesFor(gKit).getAmount() <= 0) {
                            player.sendMessage(TextFormat.RED + "You do not have permission to use "
                                    + this.getName() + " gkit.");
                            return;
                        } else {
                            reduceUses = true;
                        }
                    }
                }

                if (profile.isOnCooldown(gKit)) {
                    player.sendMessage(TextFormat.RED + "You are still on cooldown " +
                            "on this gkit for another " + TextFormat.BOLD
                            + LanguageUtils.formatDetailed(profile.getCooldown(gKit)) + TextFormat.RED + ".");
                    return;
                }

                inventory.close(player);
                profile.applyCooldown(gKit);
                gKit.apply(player);
                if (reduceUses) profile.setUses(gKit, profile.getUsesFor(gKit).getAmount() - 1);
            }
        });


        TaskHandler task = Main.getInstance().getServer().getScheduler().scheduleRepeatingTask(Main.getInstance(), new Task() {
            @Override
            public void onRun(int currentTick) {
                inventory.update(player);
            }
        }, 20);

        inventory.setTaskHandler(task);

        player.addWindow(inventory);
    }
}
