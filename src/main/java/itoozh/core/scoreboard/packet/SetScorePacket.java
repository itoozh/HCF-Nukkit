package itoozh.core.scoreboard.packet;

import cn.nukkit.network.protocol.DataPacket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import itoozh.core.scoreboard.packet.data.ScorerInfo;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class SetScorePacket extends DataPacket {

	public static final byte NETWORK_ID = 0x6c;

	private final Action action;
	@Getter
	private final List<ScorerInfo> infos = new ArrayList<>();

	@Override
	public byte pid() {
		return NETWORK_ID;
	}

	@Override
	public void decode() {/**/}

	@Override
	public void encode() {
		this.reset();

		this.putByte((byte) this.action.ordinal());
		this.putUnsignedVarInt(this.infos.size());
		for (ScorerInfo info : this.infos) {
			this.putVarLong(info.getScoreboardId());
			this.putString(info.getObjectiveId());
			this.putLInt(info.getScore());
			if (this.action == Action.SET) {
				this.putByte((byte) info.getType().ordinal());
				switch (info.getType()) {
					case ENTITY:
					case PLAYER:
						this.putUnsignedVarLong(info.getEntityId());
						break;
					case FAKE:
						this.putString(info.getName());
						break;
					default:
						throw new IllegalArgumentException("Invalid score type received");
				}
			}
		}
	}

	public enum Action {

		SET,
		REMOVE
	}
}
