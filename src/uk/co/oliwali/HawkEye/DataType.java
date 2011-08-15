package uk.co.oliwali.HawkEye;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration class representing all the different actions that HawkEye can handle
 * @author oliverw92
 */
public enum DataType {
	
	BLOCK_BREAK(0, "BlockEntry", true, "block-break", true),
	BLOCK_PLACE(1, "BlockChangeEntry", true, "block-place", true),
	SIGN_PLACE(2, "SignEntry", true, "sign-place", true),
	CHAT(3, "DataEntry", "chat"),
	COMMAND(4, "DataEntry", "command"),
	JOIN(5, "DataEntry", "join"),
    QUIT(6, "DataEntry", "quit"),
    TELEPORT(7, "DataEntry", "teleport"),
    LAVA_BUCKET(8, "SimpleRollbackEntry", true, "lava-bucket", true),
    WATER_BUCKET(9, "SimpleRollbackEntry", true, "water-bucket", true),
    OPEN_CONTAINER(10, "DataEntry", true, "open-container"),
    DOOR_INTERACT(11, "DataEntry", true, "door-interact"),
    PVP_DEATH(12, "DataEntry", "pvp-death"),
	FLINT_AND_STEEL(13, "SimpleRollbackEntry", true, "flint-steel"),
	LEVER(14, "DataEntry", true, "lever"),
	STONE_BUTTON(15, "DataEntry", true, "button"),
	OTHER(16, "DataEntry", "other"),
	EXPLOSION(17, "BlockEntry", true, "explosion", true),
	BLOCK_BURN(18, "BlockEntry", true, "block-burn", true),
	BLOCK_FORM(19, "BlockChangeEntry", true, "block-form", true),
	LEAF_DECAY(20, "BlockEntry", true, "leaf-decay", true),
	MOB_DEATH(21, "DataEntry", "mob-death"),
	OTHER_DEATH(22, "DataEntry", "other-death"),
	ITEM_DROP(23, "DataEntry", "item-drop"),
	ITEM_PICKUP(24, "DataEntry", "item-pickup"),
	BLOCK_FADE(25, "BlockChangeEntry", "block-fade", true),
	LAVA_FLOW(26, "BlockEntry", true, "lava-flow", true),
	WATER_FLOW(27, "BlockEntry", true, "water-flow", true),
	CONTAINER_TRANSACTION(28, "ContainerEntry", true, "container-transaction", true),
	SIGN_BREAK(29, "SignEntry", true, "sign-break", true),
	PAINTING_BREAK(30, "DataEntry", "painting-break"),
	PAINTING_PLACE(31, "DataEntry", "painting-place");
	
	private int id;
	private boolean canHere;
	private String configName;
	private boolean canRollback;
	private String entryClass;
	
	private static final Map<String, DataType> nameMapping = new HashMap<String, DataType>();
	private static final Map<Integer, DataType> idMapping = new HashMap<Integer, DataType>();
	
	static {
		//Mapping to enable quick finding of DataTypes by name or id
		for (DataType type : EnumSet.allOf(DataType.class)) {
			nameMapping.put(type.configName, type);
		}
		for (DataType type : EnumSet.allOf(DataType.class)) {
			idMapping.put(type.id, type);
		}
	}
	
	private DataType(int id, String entryClass, String configName) {
		this.id = id;
		this.entryClass = entryClass;
		this.canHere = false;
		this.configName = configName;
		this.canRollback = false;
	}
	private DataType(int id, String entryClass, String configName, boolean canRollback) {
		this.id = id;
		this.entryClass = entryClass;
		this.configName = configName;
		this.canRollback = canRollback;
	}
	private DataType(int id, String entryClass, boolean canHere, String configName) {
		this.id = id;
		this.entryClass = entryClass;
		this.canHere = canHere;
		this.configName = configName;
		this.canRollback = false;
	}
	private DataType(int id, String entryClass, boolean canHere, String configName, boolean canRollback) {
		this.id = id;
		this.entryClass = entryClass;
		this.canHere = canHere;
		this.configName = configName;
		this.canRollback = canRollback;
	}
	
	/**
	 * Get the id of the DataType
	 * @return int id of the DataType
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Get the config name of the DataType
	 * @return String config name
	 */
	public String getConfigName() {
		return configName;
	}
	
	/**
	 * Get the class to be used for DataEntry
	 * @return String name of entry class
	 */
	public String getEntryClass() {
		return "uk.co.oliwali.HawkEye.entry." + entryClass;
	}
	
	/**
	 * Get a matching DataType from the supplied config name
	 * @param name DataType config name to search for
	 * @return {@link DataType}
	 */
	public static DataType fromName(String name) {
		return nameMapping.get(name);
	}
	
	/**
	 * Get a matching DataType from the supplied  id
	 * @param id DataType id to search for
	 * @return {@link DataType}
	 */	
	public static DataType fromId(int id) {
		return idMapping.get(id);
	}
	
	/**
	 * Check if the DataType can be rolled back
	 * @return true if it can be, false if not
	 */
	public boolean canRollback() {
		return canRollback;
	}

	/**
	 * Check if the DataType can be used in 'here' searches
	 * @return true if it can be, false if not
	 */
	public boolean canHere() {
		return canHere;
	}

}
