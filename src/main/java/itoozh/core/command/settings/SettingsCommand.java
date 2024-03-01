package itoozh.core.command.settings;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import itoozh.core.Main;
import itoozh.core.command.BaseCommand;
import itoozh.core.scoreboard.Scoreboard;
import itoozh.core.scoreboard.ScoreboardUtils;
import itoozh.core.session.Session;
import me.iwareq.fakeinventories.FakeInventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SettingsCommand extends BaseCommand {
    private final List<String> userSettings;
    private final Set<String> keys;

    public SettingsCommand() {
        super("settings", "Customize your preferences with /settings.", "", new String[0]);
        this.keys = Main.getInstance().getConfig().getSection("SETTINGS_COMMAND.ITEMS").getKeys(true);
        this.userSettings = new ArrayList<>(Arrays.asList(UserSetting.values())).stream().map(Enum::name).collect(Collectors.toList());
        this.setPermission("core.free");
    }

    @Override
    public void sendUsageMessage(CommandSender sender) {
        if (!(sender instanceof Player)) return;

        Main.getInstance().getNameTags().showInvisTag((Player)sender, (Player)sender);
        showSettingsMenu((Player) sender);
    }

    private String convert(Session user, String input) {
        switch (input) {
            case "SCOREBOARD": {
                return this.convertBoolean(user.getScoreboard() != null);
            }
            case "SCOREBOARD_CLAIM": {
                return this.convertBoolean(user.isScoreboardClaim());
            }
            case "PUBLIC_CHAT": {
                return this.convertBoolean(user.isPublicChat());
            }
            case "FOUND_DIAMOND": {
                return this.convertBoolean(user.isFoundDiamondAlerts());
            }
        }
        return "";
    }

    private String convertBoolean(boolean value) {
        return value ? "ENABLED" : "DISABLED";
    }


    public void showSettingsMenu(Player player) {
        FakeInventory inventory = new FakeInventory(InventoryType.CHEST, TextFormat.colorize(Main.getInstance().getConfig().getString("SETTINGS_COMMAND.TITLE")));
        Session user = Main.getInstance().getSessionManager().getSessionByUUID(player.getUniqueId());

        for (String s : this.userSettings) {

            if(!this.keys.contains(s)) continue;

            Item itemS = Item.fromString(Main.getInstance().getConfig().getString("SETTINGS_COMMAND.ITEMS." + s + ".MATERIAL"));
            itemS.setCustomName(TextFormat.colorize(Main.getInstance().getConfig().getString("SETTINGS_COMMAND.ITEMS." + s + ".NAME")));

            itemS.setLore(Main.getInstance().getConfig().getStringList("SETTINGS_COMMAND.ITEMS." + s + ".LORE_" + this.convert(user, s))
                    .stream()
                    .map(TextFormat::colorize).toArray(String[]::new));

            inventory.setItem(Main.getInstance().getConfig().getInt("SETTINGS_COMMAND.ITEMS." + s + ".SLOT"), itemS, (item, event) -> {
                event.setCancelled(true);
                switch (s) {
                    case "SCOREBOARD": {
                        if (user.getScoreboard() == null) {
                            Scoreboard scoreboard = new ScoreboardUtils().getScoreboard();
                            user.setScoreboard(scoreboard);
                            scoreboard.show(player);
                        } else {
                            Scoreboard scoreboard = user.getScoreboard();
                            user.setScoreboard(null);
                            scoreboard.hide(player);
                        }
                        break;
                    }
                    case "SCOREBOARD_CLAIM": {
                        user.setScoreboardClaim(!user.isScoreboardClaim());
                        break;
                    }
                    case "PUBLIC_CHAT": {
                        user.setPublicChat(!user.isPublicChat());
                        break;
                    }
                    case "FOUND_DIAMOND": {
                        user.setFoundDiamondAlerts(!user.isFoundDiamondAlerts());
                    }
                }
                showSettingsMenu(player);
            });
        }
        inventory.setDefaultItemHandler((item, event) -> {
            event.setCancelled(true);
        });
        player.addWindow(inventory);
    }
}
