package itoozh.core.command.gkit;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import itoozh.core.command.BaseCommand;
import itoozh.core.command.gkit.sub.*;

public class KitCommand extends BaseCommand {
    public KitCommand() {
        super("kit", "Use this command to manage the kits", "", new String[0]);
        this.addSubCommand(new MenuSubCommand("menu"));
        this.addSubCommand(new CreateSubCommand("create"));
        this.addSubCommand(new CooldownResetSubCommand("cooldownreset"));
        this.addSubCommand(new ResetAllCooldownsSubCommand("resetallcooldowns"));
        this.addSubCommand(new SetCooldownSubCommand("setcooldown"));
        this.addSubCommand(new SetDisplayNameSubCommand("setdisplayname"));
        this.addSubCommand(new SetIconSubCommand("seticon"));
        this.addSubCommand(new SetItemsSubCommand("setitems"));
        this.addSubCommand(new UsesGiveSubCommand("usesgive"));
        this.addSubCommand(new UsesRemoveSubCommand("usesremove"));
        this.addSubCommand(new SetFreeUsesSubCommand("setfreeuses"));
        this.setPermission("core.gkit");
    }

    @Override
    public void sendUsageMessage(CommandSender sender) {
        if (!(sender instanceof Player)) return;
        sender.sendMessage(TextFormat.RED + "Usage: /kit <menu|create|cooldownreset|resetallcooldowns|setcooldown|setdisplayname|seticon|setitems>");
    }
}
