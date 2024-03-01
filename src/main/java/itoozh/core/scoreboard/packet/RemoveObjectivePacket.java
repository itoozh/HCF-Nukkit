package itoozh.core.scoreboard.packet;

import cn.nukkit.network.protocol.DataPacket;
import lombok.Setter;

@Setter
public class RemoveObjectivePacket extends DataPacket {

	public static final byte NETWORK_ID = 0x6a;

	private String objectiveId;

	@Override
	public byte pid() {
		return NETWORK_ID;
	}

	@Override
	public void decode() {/**/}

	@Override
	public void encode() {
		this.reset();

		this.putString(this.objectiveId);
	}
}
