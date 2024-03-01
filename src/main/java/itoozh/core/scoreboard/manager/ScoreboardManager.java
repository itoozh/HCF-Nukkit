package itoozh.core.scoreboard.manager;

import cn.nukkit.Player;
import lombok.Getter;
import itoozh.core.scoreboard.Scoreboard;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreboardManager {

	@Getter
	private final Map<String, Scoreboard> scoreboards = new ConcurrentHashMap<>();

	public Scoreboard getScoreboard(Player player) {
		return this.scoreboards.get(player.getName());
	}

	public void setScoreboard(Player player, Scoreboard scoreboard) {
		this.scoreboards.put(player.getName(), scoreboard);
	}

	public void removeScoreboard(Player player) {
		this.scoreboards.remove(player.getName());
	}
}
