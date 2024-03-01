package itoozh.core.scoreboard.packet.data;

import lombok.Getter;

@Getter
public class ScorerInfo {

	private final long scoreboardId;
	private final String objectiveId;
	private final int score;
	private final ScorerType type;
	private final String name;
	private final long entityId;

	public ScorerInfo(long scoreboardId, String objectiveId, int score) {
		this.scoreboardId = scoreboardId;
		this.objectiveId = objectiveId;
		this.score = score;
		this.type = ScorerType.INVALID;
		this.name = null;
		this.entityId = -1;
	}

	public ScorerInfo(long scoreboardId, String objectiveId, int score, String name) {
		this.scoreboardId = scoreboardId;
		this.objectiveId = objectiveId;
		this.score = score;
		this.type = ScorerType.FAKE;
		this.name = name;
		this.entityId = -1;
	}

	public ScorerInfo(long scoreboardId, String objectiveId, int score, ScorerType type, long entityId) {
		this.scoreboardId = scoreboardId;
		this.objectiveId = objectiveId;
		this.score = score;
		this.type = type;
		this.entityId = entityId;
		this.name = null;
	}

	public enum ScorerType {

		INVALID,
		PLAYER,
		ENTITY,
		FAKE
	}
}
