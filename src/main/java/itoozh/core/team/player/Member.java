package itoozh.core.team.player;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Member {
    private UUID uniqueID;
    private Role role;

    public Member(UUID uniqueID, Role role) {
        this.uniqueID = uniqueID;
        this.role = role;
    }

    public String getAsterisk() {
        return (this.role == Role.LEADER) ? "***" : ((this.role == Role.CO_LEADER) ? "**" : ((this.role == Role.CAPTAIN) ? "*" : ""));
    }
}
