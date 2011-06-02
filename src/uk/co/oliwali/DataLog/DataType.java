package uk.co.oliwali.DataLog;

public enum DataType {
	
	BLOCK_BREAK(0, "block-break"),
	BLOCK_PLACE(1, "block-place"),
	SIGN_PLACE(2, "sign-place"),
	CHAT(3, "chat"),
	COMMAND(4, "command"),
	JOIN(5, "join"),
    QUIT(6, "quit"),
    TELEPORT(7, "teleport"),
    LAVA_BUCKET(8, "lava-bucket"),
    WATER_BUCKET(9, "water-bucket"),
    OPEN_CHEST(10, "open-chest"),
    DOOR_INTERACT(11, "door-interact"),
    PVP_DEATH(12, "pvp-death"),
	FLINT_AND_STEEL(13, "flint-steel"),
	LEVER(14, "lever"),
	STONE_BUTTON(15, "button");
	
	private int id;
	private String configName;
	
	private DataType(int id, String configName) {
		this.id = id;
		this.configName = configName;
	}
	
	public int getId() {
		return id;
	}
	
	public String getConfigName() {
		return configName;
	}

}
