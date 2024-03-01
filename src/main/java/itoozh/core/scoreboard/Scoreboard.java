package itoozh.core.scoreboard;

import cn.nukkit.Player;
import cn.nukkit.Server;
import itoozh.core.Main;
import lombok.Getter;
import itoozh.core.scoreboard.line.ScoreboardLine;
import itoozh.core.scoreboard.manager.ScoreboardManager;
import itoozh.core.scoreboard.packet.RemoveObjectivePacket;
import itoozh.core.scoreboard.packet.SetDisplayObjectivePacket;
import itoozh.core.scoreboard.packet.SetScorePacket;
import itoozh.core.scoreboard.packet.data.DisplaySlot;
import itoozh.core.scoreboard.packet.data.ScorerInfo;
import itoozh.core.scoreboard.packet.data.SortOrder;
import itoozh.core.scoreboard.updater.ScoreboardUpdater;

import java.util.*;
import java.util.function.Consumer;

public class Scoreboard {

	public String displayName;
	private final DisplaySlot displaySlot;
	private final ScoreboardManager manager;
	private long titleChanger;

	@Getter
	private final Set<Player> viewers = new HashSet<>();
	public final Map<Integer, ScoreboardLine> lines = new HashMap<>();

	private Consumer<Player> consumer = p -> {};
	private int lastIndex;
	private int titleIndex;

	public Scoreboard(String displayName, DisplaySlot displaySlot) {
		this(displayName, displaySlot, Integer.MIN_VALUE);
		this.titleChanger = System.currentTimeMillis();
		this.titleIndex = 0;
	}

	public Scoreboard(String displayName, DisplaySlot displaySlot, int updateTime) {
		this.displayName = displayName;
		this.displaySlot = displaySlot;
		this.manager = Main.getInstance().getScoreboardManager();

		if (updateTime != Integer.MIN_VALUE) {
			Server.getInstance().getScheduler().scheduleRepeatingTask(new ScoreboardUpdater(this), updateTime, true);
		}
	}

	public void setHandler(Consumer<Player> consumer) {
		this.consumer = consumer;
	}

	public void setLine(int index, String text) {
		this.checkLineIndex(index);

		this.lastIndex = index;

		ScoreboardLine line = new ScoreboardLine(this, text);
		this.lines.put(index, line);
	}

	public void addLine(String text) {
		this.lastIndex++;

		this.setLine(this.lastIndex, text);
	}

	private void checkLineIndex(int index) {
		if (index < 1 || index > 15) {
			throw new IllegalArgumentException("The line index value should be from 1 to 15, your index: " + index);
		}
	}

	public void refresh() {
		this.lines.clear();
		this.lastIndex = 0;
		this.viewers.removeIf(viewer -> {
			boolean canBeRemoved = !viewer.isOnline();
			if (canBeRemoved) {
				this.manager.removeScoreboard(viewer);
			}

			return canBeRemoved;
		});
		this.viewers.forEach(viewer -> {

				this.hide(viewer, false);
				this.show(viewer, false);

		});

		tickTitle();
	}

	public void show(Player player) {
		this.show(player, true);
	}

	private void show(Player player, boolean add) {
		if (!add || this.viewers.add(player)) {
			this.consumer.accept(player);

			SetDisplayObjectivePacket objectivePacket = new SetDisplayObjectivePacket();
			objectivePacket.setDisplaySlot(this.displaySlot);
			objectivePacket.setObjectiveId("objective");
			objectivePacket.setDisplayName(this.displayName);
			// dummy is the only criterion supported. As such, score can only be changed by commands.
			objectivePacket.setCriteria("dummy");
			objectivePacket.setSortOrder(SortOrder.ASCENDING);

			player.dataPacket(objectivePacket);

			SetScorePacket scorePacket = new SetScorePacket(SetScorePacket.Action.SET);
			if (this.lines.size() <= 3) {
				RemoveObjectivePacket packet = new RemoveObjectivePacket();
				packet.setObjectiveId("objective");
				player.dataPacket(packet);
			} else {
				this.lines.forEach((index, line) ->
						scorePacket.getInfos().add(
								new ScorerInfo(index, "objective", index, line.getText())
						)
				);
				player.dataPacket(scorePacket);
			}
		}
	}

	public void hide(Player player) {
		this.hide(player, true);
	}

	private void hide(Player player, boolean remove) {
		if (!remove || this.viewers.remove(player)) {
			RemoveObjectivePacket packet = new RemoveObjectivePacket();
			packet.setObjectiveId("objective");

			player.dataPacket(packet);

			if (remove) {
				this.manager.removeScoreboard(player);
			}
		}
	}

	private void tickTitle() {
		if (ScoreboardUtils.scoreboardConfig.getBoolean("TITLE_CONFIG.CHANGER_ENABLED")) {
			if (this.titleChanger < System.currentTimeMillis()) {
				this.titleChanger = System.currentTimeMillis() + ScoreboardUtils.scoreboardConfig.getLong("TITLE_CONFIG.CHANGER_TICKS");
				this.displayName = this.getTitle();
			}
		}
	}

	private String getTitle() {
		List<String> lines = ScoreboardUtils.scoreboardConfig.getStringList("TITLE_CONFIG.CHANGES");
		if (this.titleIndex == lines.size()) {
			this.titleIndex = 0;
			return lines.get(0);
		}
		String s = lines.get(this.titleIndex);
		++this.titleIndex;
		return s;
	}
}
