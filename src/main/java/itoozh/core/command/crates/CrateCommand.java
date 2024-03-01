package itoozh.core.command.crates;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import itoozh.core.command.BaseCommand;
import itoozh.core.command.crates.sub.*;

public class CrateCommand extends BaseCommand {

    public CrateCommand() {
        super("crates", "Use this command to manage the crates server.", "", new String[0]);
        this.addSubCommand(new CreateSubCommand("create"));
        this.addSubCommand(new KeySubCommand("key"));
        this.addSubCommand(new PlaceSubCommand("place"));
        this.addSubCommand(new ListSubCommand("list"));
        this.addSubCommand(new UnPlaceSubCommand("unplace"));
        this.addSubCommand(new DeleteSubCommand("delete"));
        this.addSubCommand(new SetContentSubCommand("setcontent"));
        this.addSubCommand(new SetKeySubCommand("setkey"));
        this.addSubCommand(new SetRewardsAmountSubCommand("setrewardsamount"));
        this.addSubCommand(new SetNameSubCommand("setname"));
        this.addSubCommand(new SetHologramSubCommand("sethologram"));
        this.addSubCommand(new KeyAllSubCommand("keyall"));
        this.addSubCommand(new SetEffectSubCommand("seteffect"));
        this.setPermission("core.crates");
    }

    @Override
    public void sendUsageMessage(CommandSender sender) {
        if (!(sender instanceof Player)) return;
        sender.sendMessage(TextFormat.colorize("&cUse /crates <create/place/list>"));
    }
}
