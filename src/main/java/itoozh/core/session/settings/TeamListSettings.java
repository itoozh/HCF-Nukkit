package itoozh.core.session.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TeamListSettings {
    ONLINE_HIGH("TEAM_COMMAND.TEAM_SORT.ONLINE_HIGH"),
    ONLINE_LOW("TEAM_COMMAND.TEAM_SORT.ONLINE_LOW"),
    LOWEST_DTR("TEAM_COMMAND.TEAM_SORT.LOWEST_DTR"),
    HIGHEST_DTR("TEAM_COMMAND.TEAM_SORT.HIGHEST_DTR");

    private final String configPath;
}
