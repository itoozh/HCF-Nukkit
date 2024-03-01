package itoozh.core.util;

import cn.nukkit.plugin.Plugin;
import cn.nukkit.scheduler.NukkitRunnable;
import cn.nukkit.scheduler.Task;
import cn.nukkit.scheduler.TaskHandler;
import itoozh.core.Main;

public final class TaskUtils {

    public static void executeScheduled(int HCF, Runnable runnable) {

        Main.getInstance().getServer().getScheduler().scheduleDelayedTask(Main.getInstance(), new Task() {
            @Override
            public void onRun(int currentTick) {
                runnable.run();
            }
        }, HCF);
    }

    public static void executeLater(int HCF, Runnable runnable) {
        Main.getInstance().getServer().getScheduler().scheduleDelayedTask(Main.getInstance(), new Task() {
            @Override
            public void onRun(int currentTick) {
                runnable.run();
            }
        }, HCF);
    }

    public static void executeScheduledAsync(Plugin plugin, int HCF, Runnable runnable) {
        TaskHandler taskHandler = plugin.getServer().getScheduler().scheduleRepeatingTask(plugin, new NukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }, HCF, false);
    }



}
