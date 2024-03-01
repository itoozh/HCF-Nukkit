package itoozh.core.command.crates.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.crate.Crate;
import itoozh.core.crate.effect.CrateEffect;

public class SetEffectSubCommand extends BaseSubCommand {
    public SetEffectSubCommand(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(TextFormat.colorize("&cUsage: /place setname <name> <newName>"));
            return false;
        }

        String name = args[1];
        CrateEffect crateEffect;

        try {
            crateEffect = CrateEffect.valueOf(args[2]);
        }catch (IllegalArgumentException e) {
            sender.sendMessage(TextFormat.colorize("&cInvalid effect. Please enter a valid effect. Available effects:"));
            for (CrateEffect effect : CrateEffect.values()) {
                sender.sendMessage(TextFormat.colorize("&c" + effect.name()));
            }
            return false;
        }

        Crate crate = Main.getInstance().getCrateManager().getCrate(name);

        if (crate == null) {
            sender.sendMessage(TextFormat.colorize("&cCrate not found."));
            return false;
        }


        crate.setEffect(crateEffect);
        sender.sendMessage(TextFormat.colorize("&aEffect in " + crate.getName() + "crate has been updated!"));
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] { CommandParameter.newType("name", CommandParamType.STRING), CommandParameter.newType("newName", CommandParamType.STRING)};
    }


}
