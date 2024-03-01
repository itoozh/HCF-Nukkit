package itoozh.core.team.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    MEMBER("Member"),
    CAPTAIN("Captain"),
    CO_LEADER("Co-Leader"),
    LEADER("Leader");

    private final String name;
}


