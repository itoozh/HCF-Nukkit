package itoozh.core.command.gkit.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.gkit.GKit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetCooldownSubCommand extends BaseSubCommand {

    public SetCooldownSubCommand(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return sender.hasPermission("core.gkit") && sender instanceof Player;
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(TextFormat.RED + "Usage: /gkit setcooldown [name] [cooldown]");
            return true;

        }
        String name = args[1];

        long cooldown = convertToMilliseconds(args[2]);
        if (!Main.getInstance().getGKitManager().doesGKitExist(name)) {
            sender.sendMessage(TextFormat.RED + "This gkit does not exist.");
            return true;
        }

        Player player = (Player) sender;

        GKit gKit = Main.getInstance().getGKitManager().getGKit(name);
        gKit.setCoolDown(cooldown);
        player.sendMessage(TextFormat.GREEN + "You have set the cooldown " + "for the gkit called " + gKit.getName() + ".");
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] {CommandParameter.newType("name", CommandParamType.STRING), CommandParameter.newType("cooldown", CommandParamType.INT)};
    }

    public static long convertToMilliseconds(String input) {
        long totalMilliseconds = 0;

        Pattern pattern = Pattern.compile("(\\d+)([dhms])");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "d":
                    totalMilliseconds += (long) value * 24 * 60 * 60 * 1000;
                    break;
                case "h":
                    totalMilliseconds += (long) value * 60 * 60 * 1000;
                    break;
                case "m":
                    totalMilliseconds += (long) value * 60 * 1000;
                    break;
                case "s":
                    totalMilliseconds += value * 1000L;
                    break;
            }
        }

        return totalMilliseconds;
    }
}
