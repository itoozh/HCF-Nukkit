package itoozh.core.scoreboard.line;

import lombok.AllArgsConstructor;
import lombok.Getter;
import itoozh.core.scoreboard.Scoreboard;

@Getter
@AllArgsConstructor
public class ScoreboardLine {

	private final Scoreboard scoreboard;
	private final String text;
}
