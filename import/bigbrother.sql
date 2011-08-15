-- BigBrother import script for HawkEye --
--            by oliverw92              --

-- If your table names are different use a text editor
-- to find and replace the table names with your own

-- Players
INSERT IGNORE INTO `hawk_players` (player)
SELECT DISTINCT `name`
FROM `bbusers`;

-- Worlds
INSERT IGNORE INTO `hawk_worlds` (world)
SELECT DISTINCT `name`
FROM `bbworlds`;

-- Block break
INSERT INTO `hawkeye` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `hawk_players`.player_id, 0, `hawk_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.type, 'HawkEye'
FROM `bbdata`, `hawk_players`, `hawk_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 1
AND `hawk_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `hawk_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Block place
INSERT INTO `hawkeye` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `hawk_players`.player_id, 1, `hawk_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.type, 'HawkEye'
FROM `bbdata`, `hawk_players`, `hawk_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 2
AND `hawk_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `hawk_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Teleport
INSERT INTO `hawkeye` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `hawk_players`.player_id, 7, `hawk_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.type, 'HawkEye'
FROM `bbdata`, `hawk_players`, `hawk_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 4
AND `hawk_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `hawk_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Command
INSERT INTO `hawkeye` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `hawk_players`.player_id, 4, `hawk_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.data, 'HawkEye'
FROM `bbdata`, `hawk_players`, `hawk_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 6
AND `hawk_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `hawk_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Command
INSERT INTO `hawkeye` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `hawk_players`.player_id, 3, `hawk_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.data, 'HawkEye'
FROM `bbdata`, `hawk_players`, `hawk_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 7
AND `hawk_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `hawk_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Disconnect
INSERT INTO `hawkeye` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `hawk_players`.player_id, 6, `hawk_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.data, 'HawkEye'
FROM `bbdata`, `hawk_players`, `hawk_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 8
AND `hawk_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `hawk_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Join
INSERT INTO `hawkeye` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `hawk_players`.player_id, 5, `hawk_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.data, 'HawkEye'
FROM `bbdata`, `hawk_players`, `hawk_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 9
AND `hawk_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `hawk_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Door
INSERT INTO `hawkeye` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `hawk_players`.player_id, 11, `hawk_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.data, 'HawkEye'
FROM `bbdata`, `hawk_players`, `hawk_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 10
AND `hawk_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `hawk_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Button
INSERT INTO `hawkeye` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `hawk_players`.player_id, 15, `hawk_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.data, 'HawkEye'
FROM `bbdata`, `hawk_players`, `hawk_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 11
AND `hawk_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `hawk_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Lever
INSERT INTO `hawkeye` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `hawk_players`.player_id, 13, `hawk_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.data, 'HawkEye'
FROM `bbdata`, `hawk_players`, `hawk_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 14
AND `hawk_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `hawk_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Sign place
INSERT INTO `hawkeye` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `hawk_players`.player_id, 2, `hawk_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, REPLACE(`bbdata`.data, '`', '|'), 'HawkEye'
FROM `bbdata`, `hawk_players`, `hawk_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 13
AND `hawk_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `hawk_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Leaf decay
INSERT INTO `hawkeye` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `hawk_players`.player_id, 20, `hawk_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.data, 'HawkEye'
FROM `bbdata`, `hawk_players`, `hawk_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 14
AND `hawk_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `hawk_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Flint steel
INSERT INTO `hawkeye` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `hawk_players`.player_id, 13, `hawk_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.data, 'HawkEye'
FROM `bbdata`, `hawk_players`, `hawk_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 15
AND `hawk_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `hawk_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Explosions
INSERT INTO `hawkeye` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `hawk_players`.player_id, 17, `hawk_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.type, 'HawkEye'
FROM `bbdata`, `hawk_players`, `hawk_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action IN (16,17,18)
AND `hawk_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `hawk_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Chest Open
INSERT INTO `hawkeye` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `hawk_players`.player_id, 10, `hawk_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.data, 'HawkEye'
FROM `bbdata`, `hawk_players`, `hawk_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 19
AND `hawk_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `hawk_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;

-- Block burn
INSERT INTO `hawkeye` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT FROM_UNIXTIME(`bbdata`.date), `hawk_players`.player_id, 18, `hawk_worlds`.world_id, `bbdata`.x,  `bbdata`.y,  `bbdata`.z, `bbdata`.type, 'HawkEye'
FROM `bbdata`, `hawk_players`, `hawk_worlds`, `bbusers`, `bbworlds`
WHERE `bbdata`.action = 20
AND `hawk_players`.player = `bbusers`.name
AND `bbusers`.id = `bbdata`.player
AND `hawk_worlds`.world = `bbworlds`.name
AND `bbworlds`.id = `bbdata`.world;