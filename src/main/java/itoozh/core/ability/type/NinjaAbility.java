package itoozh.core.ability.type;

import cn.nukkit.Player;
import cn.nukkit.event.Listener;
import cn.nukkit.scheduler.Task;
import itoozh.core.Main;
import itoozh.core.ability.Ability;
import itoozh.core.ability.AbilityManager;
import itoozh.core.util.LanguageUtils;

public class NinjaAbility extends Ability  implements Listener {
    private int seconds;
    private int hitsValid;

    public NinjaAbility(AbilityManager manager) {
        super(manager, AbilityManager.AbilityUseType.INTERACT, "Ninja Ability");
        this.seconds = manager.getDataFile().getInt("NINJA_ABILITY.SECONDS");
        this.hitsValid = manager.getDataFile().getInt("NINJA_ABILITY.HITS_VALID");
    }

    @Override
    public void onClick(Player player) {
        if (this.cannotUse(player)) {
            return;
        }
        if (this.hasCooldown(player)) {
            return;
        }
        Player manager = ((FocusModeAbility) Main.getInstance().getAbilityManager().getAbility("FocusMode")).getDamager(player, this.hitsValid);
        if (manager == null) {
            player.sendMessage(LanguageUtils.getString("ABILITIES.NINJA_ABILITY.NO_LAST_HIT"));
            return;
        }
        this.takeItem(player);
        this.applyCooldown(player);


        Main.getInstance().getServer().getScheduler().scheduleRepeatingTask(Main.getInstance(), new Task() {
            private int i = 0;
            @Override
            public void onRun(int currentTick) {
                if (this.i == NinjaAbility.this.seconds) {
                    player.teleport(manager);
                    this.cancel();
                    for (String s : LanguageUtils.splitStringToList(LanguageUtils.getString("ABILITIES.NINJA_ABILITY.TELEPORTED_SUCCESSFULLY"))) {
                        player.sendMessage(s.replaceAll("%player%", manager.getName()));
                    }
                    return;
                }
                for (String s : LanguageUtils.splitStringToList(LanguageUtils.getString("ABILITIES.NINJA_ABILITY.TELEPORTING"))) {
                    player.sendMessage(s.replaceAll("%player%", manager.getName()).replaceAll("%seconds%", String.valueOf(NinjaAbility.this.seconds - this.i)));
                }
                for (String s : LanguageUtils.splitStringToList(LanguageUtils.getString("ABILITIES.NINJA_ABILITY.TELEPORTING_ATTACKER"))) {
                    manager.sendMessage(s.replaceAll("%player%", player.getName()).replaceAll("%seconds%", String.valueOf(NinjaAbility.this.seconds - this.i)));
                }
                ++this.i;
            }
        }, 20);
    }
}
