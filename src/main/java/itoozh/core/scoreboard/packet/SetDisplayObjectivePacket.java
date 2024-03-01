package itoozh.core.scoreboard.packet;

import cn.nukkit.network.protocol.DataPacket;
import lombok.Setter;
import itoozh.core.scoreboard.packet.data.DisplaySlot;
import itoozh.core.scoreboard.packet.data.SortOrder;

@Setter
public class SetDisplayObjectivePacket extends DataPacket {

	public static final byte NETWORK_ID = 0x6b;

	private DisplaySlot displaySlot;
	private String objectiveId;
	private String displayName;
	private String criteria;
	private SortOrder sortOrder;

	@Override
	public byte pid() {
		return NETWORK_ID;
	}

	@Override
	public void decode() {/**/}

	@Override
	public void encode() {
		this.reset();

		this.putString(this.displaySlot.getName());
		this.putString(this.objectiveId);
		this.putString(this.displayName);
		this.putString(this.criteria);
		this.putVarInt(this.sortOrder.ordinal());
	}
}
