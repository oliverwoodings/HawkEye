package uk.co.oliwali.DataLog;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum DataType {
	
	BLOCK_BREAK(0, "block-break", true),
	BLOCK_PLACE(1, "block-place", true),
	SIGN_PLACE(2, "sign-place"),
	CHAT(3, "chat"),
	COMMAND(4, "command"),
	JOIN(5, "join"),
    QUIT(6, "quit"),
    TELEPORT(7, "teleport"),
    LAVA_BUCKET(8, "lava-bucket", true),
    WATER_BUCKET(9, "water-bucket", true),
    OPEN_CHEST(10, "open-chest"),
    DOOR_INTERACT(11, "door-interact"),
    PVP_DEATH(12, "pvp-death"),
	FLINT_AND_STEEL(13, "flint-steel"),
	LEVER(14, "lever"),
	STONE_BUTTON(15, "button"),
	OTHER(16, "other");
	
	private int id;
	private String configName;
	private boolean canRollback;
	
	private static final Map<String, DataType> nameMapping = new HashMap<String, DataType>();
	private static final Map<Integer, DataType> idMapping = new HashMap<Integer, DataType>();
	
	static {
		for (DataType type : EnumSet.allOf(DataType.class)) {
			nameMapping.put(type.configName, type);
		}
		for (DataType type : EnumSet.allOf(DataType.class)) {
			idMapping.put(type.id, type);
		}
	}
	
	private DataType(int id, String configName) {
		this.id = id;
		this.configName = configName;
		this.canRollback = false;
	}
	private DataType(int id, String configName, boolean canRollback) {
		this.id = id;
		this.configName = configName;
		this.canRollback = canRollback;
	}
	
	public int getId() {
		return id;
	}
	
	public String getConfigName() {
		return configName;
	}
	
	public static DataType fromName(String name) {
		return nameMapping.get(name);
	}
	
	public static DataType fromId(int id) {
		return idMapping.get(id);
	}
	
	public boolean canRollback() {
		return canRollback;
	}

}
