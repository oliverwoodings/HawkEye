-- BigBrother import script for HawkEye --
--            by oliverw92              --

-- If your table names are different use a text editor
-- to find and replace the table names with your own

-- Players
INSERT IGNORE INTO `dl_players` (player)
SELECT DISTINCT `name`
FROM `bbusers`;

-- Worlds
INSERT IGNORE INTO `dl_worlds` (world)
SELECT DISTINCT `name`
FROM `bbworlds`;

-- Block break
INSERT INTO `datalog` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `dl_players`.player_id, 0, `dl_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.type, 'HawkEye'
FROM `bbdata`, `dl_players`, `dl_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 1
AND `dl_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `dl_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Block place
INSERT INTO `datalog` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `dl_players`.player_id, 1, `dl_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.type, 'HawkEye'
FROM `bbdata`, `dl_players`, `dl_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 2
AND `dl_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `dl_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Teleport
INSERT INTO `datalog` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `dl_players`.player_id, 7, `dl_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.type, 'HawkEye'
FROM `bbdata`, `dl_players`, `dl_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 4
AND `dl_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `dl_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Command
INSERT INTO `datalog` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `dl_players`.player_id, 4, `dl_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.data, 'HawkEye'
FROM `bbdata`, `dl_players`, `dl_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 6
AND `dl_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `dl_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Command
INSERT INTO `datalog` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `dl_players`.player_id, 3, `dl_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.data, 'HawkEye'
FROM `bbdata`, `dl_players`, `dl_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 7
AND `dl_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `dl_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Disconnect
INSERT INTO `datalog` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `dl_players`.player_id, 6, `dl_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.data, 'HawkEye'
FROM `bbdata`, `dl_players`, `dl_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 8
AND `dl_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `dl_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Join
INSERT INTO `datalog` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `dl_players`.player_id, 5, `dl_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.data, 'HawkEye'
FROM `bbdata`, `dl_players`, `dl_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 9
AND `dl_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `dl_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Door
INSERT INTO `datalog` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `dl_players`.player_id, 11, `dl_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.data, 'HawkEye'
FROM `bbdata`, `dl_players`, `dl_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 10
AND `dl_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `dl_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Button
INSERT INTO `datalog` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `dl_players`.player_id, 15, `dl_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.data, 'HawkEye'
FROM `bbdata`, `dl_players`, `dl_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 11
AND `dl_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `dl_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Lever
INSERT INTO `datalog` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `dl_players`.player_id, 13, `dl_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.data, 'HawkEye'
FROM `bbdata`, `dl_players`, `dl_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 14
AND `dl_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `dl_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Sign place
INSERT INTO `datalog` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `dl_players`.player_id, 2, `dl_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, REPLACE(`bbdata`.data, '`', '|'), 'HawkEye'
FROM `bbdata`, `dl_players`, `dl_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 13
AND `dl_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `dl_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Leaf decay
INSERT INTO `datalog` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `dl_players`.player_id, 20, `dl_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.data, 'HawkEye'
FROM `bbdata`, `dl_players`, `dl_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 14
AND `dl_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `dl_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Flint steel
INSERT INTO `datalog` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `dl_players`.player_id, 13, `dl_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.data, 'HawkEye'
FROM `bbdata`, `dl_players`, `dl_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 15
AND `dl_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `dl_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Explosions
INSERT INTO `datalog` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `dl_players`.player_id, 17, `dl_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.type, 'HawkEye'
FROM `bbdata`, `dl_players`, `dl_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action IN (16,17,18)
AND `dl_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `dl_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Chest Open
INSERT INTO `datalog` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `dl_players`.player_id, 10, `dl_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.data, 'HawkEye'
FROM `bbdata`, `dl_players`, `dl_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 19
AND `dl_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `dl_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Block burn
INSERT INTO `datalog` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `dl_players`.player_id, 18, `dl_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.type, 'HawkEye'
FROM `bbdata`, `dl_players`, `dl_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 20
AND `dl_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `dl_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;