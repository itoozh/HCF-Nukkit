package itoozh.core.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseCommand extends Command {

    private final ArrayList<BaseSubCommand> subCommand = new ArrayList<>();
    private final ConcurrentHashMap<String, Integer> subCommands = new ConcurrentHashMap<>();
    protected Main main = Main.getInstance();

    public BaseCommand(String name, String description, String usageMessage, String[] aliases) {
        super(name, description, usageMessage, aliases);
    }

    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(this.getPermission());
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (hasPermission(sender)) {
            if (args.length > 0) {
                String subCommand = args[0].toLowerCase();
                if (subCommands.containsKey(subCommand)) {
                    BaseSubCommand command = this.subCommand.get(this.subCommands.get(subCommand));
                    if (command.canUser(sender)) {
                        return command.execute(sender, s, args);
                    } else if (sender.isPlayer()) {
                        sender.sendMessage(TextFormat.colorize(Main.prefix + "&cYou don't have permission to use this command."));
                    } else {
                        sender.sendMessage(TextFormat.colorize(Main.prefix + "&cYou can only use this command by a player."));
                    }
                } else {
                    this.sendUsageMessage(sender);
                }
            } else {
                this.sendUsageMessage(sender);
            }
            return true;
        }
        sender.sendMessage(TextFormat.colorize(Main.prefix + "&cYou don't have permission to use this command."));
        return true;
    }

    public abstract void sendUsageMessage(CommandSender sender);

    protected void addSubCommand(BaseSubCommand cmd) {
        this.subCommand.add(cmd);
        int commandId = (this.subCommand.size()) - 1;
        this.subCommands.put(cmd.getName().toLowerCase(), commandId);
        for (String alias : cmd.getAliases()) {
            this.subCommands.put(alias.toLowerCase(), commandId);
        }
        this.loadCommandBase();
    }

    private void loadCommandBase(){
        this.commandParameters.clear();
        for(BaseSubCommand subCommand : this.subCommand) {
            LinkedList<CommandParameter> parameters = new LinkedList<>();
            parameters.add(CommandParameter.newEnum(subCommand.getName(), new String[]{subCommand.getName()}));
            parameters.addAll(Arrays.asList(subCommand.getParameters()));
            this.commandParameters.put(subCommand.getName(),parameters.toArray(new CommandParameter[0]));
        }
    }
}
