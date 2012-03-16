package uk.co.oliwali.HawkEye;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import uk.co.oliwali.HawkEye.entry.BlockChangeEntry;
import uk.co.oliwali.HawkEye.entry.BlockEntry;
import uk.co.oliwali.HawkEye.entry.ContainerEntry;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.entry.SignEntry;
import uk.co.oliwali.HawkEye.entry.SimpleRollbackEntry;

/**
 * Enumeration class representing all the different actions that HawkEye can handle
 * @author oliverw92
 */
public enum DataType {

	BLOCK_BREAK(0, BlockEntry.class, "block-break", true, true),
	BLOCK_PLACE(1, BlockChangeEntry.class, "block-place", true, true),
	SIGN_PLACE(2, SignEntry.class, "sign-place", true, true),
	CHAT(3, DataEntry.class, "chat", false, false),
	COMMAND(4, DataEntry.class, "command", false, false),
	JOIN(5, DataEntry.class, "join", false, false),
	QUIT(6, DataEntry.class, "quit", false, false),
	TELEPORT(7, DataEntry.class, "teleport", false, false),
	LAVA_BUCKET(8, SimpleRollbackEntry.class, "lava-bucket", true, true),
	WATER_BUCKET(9, SimpleRollbackEntry.class, "water-bucket", true, true),
	OPEN_CONTAINER(10, DataEntry.class, "open-container", true, false),
	DOOR_INTERACT(11, DataEntry.class, "door-interact", true, false),
	PVP_DEATH(12, DataEntry.class, "pvp-death", false, false),
	FLINT_AND_STEEL(13, SimpleRollbackEntry.class, "flint-steel", true, true),
	LEVER(14, DataEntry.class, "lever", true, false),
	STONE_BUTTON(15, DataEntry.class, "button", true, false),
	OTHER(16, DataEntry.class, "other", false, false),
	EXPLOSION(17, BlockEntry.class, "explosion", true, true),
	BLOCK_BURN(18, BlockEntry.class, "block-burn", true, true),
	BLOCK_FORM(19, BlockChangeEntry.class, "block-form", true, true),
	LEAF_DECAY(20, BlockEntry.class, "leaf-decay", true, true),
	MOB_DEATH(21, DataEntry.class, "mob-death", false, false),
	OTHER_DEATH(22, DataEntry.class, "other-death", false, false),
	ITEM_DROP(23, DataEntry.class, "item-drop", true, false),
	ITEM_PICKUP(24, DataEntry.class, "item-pickup", true, false),
	BLOCK_FADE(25, BlockChangeEntry.class, "block-fade", true, true),
	LAVA_FLOW(26, BlockChangeEntry.class, "lava-flow", true, true),
	WATER_FLOW(27, BlockChangeEntry.class, "water-flow", true, true),
	CONTAINER_TRANSACTION(28, ContainerEntry.class, "container-transaction", true, true),
	SIGN_BREAK(29, SignEntry.class, "sign-break", true, true),
	PAINTING_BREAK(30, DataEntry.class, "painting-break", true, false),
	PAINTING_PLACE(31, DataEntry.class, "painting-place", true, false),
	ENDERMAN_PICKUP(32, BlockEntry.class, "enderman-pickup", true, true),
	ENDERMAN_PLACE(33, BlockChangeEntry.class, "enderman-place", true, true),
    TREE_GROW(34, BlockChangeEntry.class, "tree-grow", true, true),
    MUSHROOM_GROW(35, BlockChangeEntry.class, "mushroom-grow", true, true),
	ENTITY_KILL(36, DataEntry.class, "entity-kill", false, false);

	private int id;
	private boolean canHere;
	private String configName;
	private boolean canRollback;
	private Class<?> entryClass;

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

	private DataType(int id, Class<?> entryClass, String configName, boolean canHere, boolean canRollback) {
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
	public Class<?> getEntryClass() {
		return entryClass;
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
