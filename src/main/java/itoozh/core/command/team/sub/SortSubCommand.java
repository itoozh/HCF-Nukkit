package itoozh.core.command.team.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseSubCommand;
import itoozh.core.session.Session;
import itoozh.core.session.settings.TeamListSettings;
import itoozh.core.util.LanguageUtils;
import me.iwareq.fakeinventories.FakeInventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SortSubCommand extends BaseSubCommand {

    private final List<TeamListSettings> settings;

    public SortSubCommand(String name) {
        super(name);
        this.settings = Arrays.asList(TeamListSettings.values());
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
        Player player = (Player) sender;
        sendSortMenu(player);
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }

    public void sendSortMenu(Player player) {
        Session user = Main.getInstance().getSessionManager().getSessionByUUID(player.getUniqueId());
        TeamListSettings setting = user.getTeamListSettings();

        FakeInventory inventory = new FakeInventory(InventoryType.HOPPER, TextFormat.colorize(LanguageUtils.getString("TEAM_COMMAND.TEAM_SORT.TITLE")));

        Item itemC = Item.fromString(LanguageUtils.getString("TEAM_COMMAND.TEAM_SORT.MATERIAL"));
        itemC.setCustomName(TextFormat.colorize("&r" + LanguageUtils.getString("TEAM_COMMAND.TEAM_SORT.NAME")));

        List<String> lore = new ArrayList<>();
        for (TeamListSettings listSetting : this.settings) {
            lore.add((setting == listSetting) ? TextFormat.colorize("&r" + LanguageUtils.getString("TEAM_COMMAND.TEAM_SORT.POINTER") + TextFormat.colorize("&r" + LanguageUtils.getString(listSetting.getConfigPath()))) : TextFormat.colorize("&r" + LanguageUtils.getString(listSetting.getConfigPath())));
        }


        itemC.setLore(String.join(System.lineSeparator(), lore));
        inventory.setItem(2, itemC, (item, event) -> {
            event.setCancelled(true);
            Player target = event.getTransaction().getSource();
            Session session = Main.getInstance().getSessionManager().getSessionByUUID(target.getUniqueId());
            TeamListSettings settings1 = getSetting(setting);
            session.setTeamListSettings(settings1);
            sendSortMenu(player);
        });
        inventory.setDefaultItemHandler((item, event) -> {
            event.setCancelled(true);
        });

        player.addWindow(inventory);
    }

    public TeamListSettings getSetting(TeamListSettings setting) {
        int i = this.settings.indexOf(setting);
        if (i == this.settings.size() - 1) {
            return this.settings.get(0);
        }
        return this.settings.get(i + 1);
    }

}
