package itoozh.core.command;

import itoozh.core.Main;
import itoozh.core.command.ability.AbilityCommand;
import itoozh.core.command.balance.BalanceCommand;
import itoozh.core.command.claim.ClaimCommand;
import itoozh.core.command.crates.CrateCommand;
import itoozh.core.command.event.SOTWCommand;
import itoozh.core.command.gkit.GKitCommand;
import itoozh.core.command.gkit.KitCommand;
import itoozh.core.command.pvp.PvPCommand;
import itoozh.core.command.rank.RankCommand;
import itoozh.core.command.settings.SettingsCommand;
import itoozh.core.command.leaderboards.LeaderboardsCommand;
import itoozh.core.command.team.TeamCommand;
import itoozh.core.command.team.TlCommand;

import java.util.Arrays;
import java.util.List;

public class CommandManager {


    public CommandManager(Main plugin) {

        List<BaseCommand> commands =
                Arrays.asList(
                        new ClaimCommand(),
                        new TeamCommand(),
                        new BalanceCommand(),
                        new SettingsCommand(),
                        new PvPCommand(),
                        new CrateCommand(),
                        new GKitCommand(),
                        new KitCommand(),
                        new LeaderboardsCommand(),
                        new SOTWCommand(),
                        new RankCommand(),
                        new TlCommand(),
                        new AbilityCommand()
                );

        plugin.getServer().getCommandMap().registerAll("HCFactions", commands);
    }

}
