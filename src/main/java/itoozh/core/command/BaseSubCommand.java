package itoozh.core.command;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import itoozh.core.Main;

public abstract class BaseSubCommand {

    private final String name;

    protected BaseSubCommand(String name) {
        this.name = name.toLowerCase();
    }

    public abstract boolean canUser(CommandSender sender);

    public String getName(){
        return name;
    }

    public abstract String[] getAliases();

    public abstract boolean execute(CommandSender sender, String label, String[] args);
    public abstract CommandParameter[] getParameters();
}
