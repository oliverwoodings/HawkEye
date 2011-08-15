-- LogBlock import script for HawkEye --
--           by oliverw92             --

-- If your table names are different use a text editor
-- to find and replace the table names with your own.
-- Because of the way LogBlock works, you need to run this script
-- as many times as you have separate world tables in your database.
-- Make sure you edit your world name in the 'world' section immediately below!

-- World name - EDIT THIS VALUE TO YOUR WORLD NAME OR THIS WILL BREAK
SET @world = 'world';

-- World
INSERT IGNORE INTO `hawk_worlds` (world)
VALUES (@world);
SET @worldID = (SELECT world_id FROM `hawk_worlds` WHERE world = @world);

-- Players
INSERT IGNORE INTO `lb-players` (playername)
VALUES ('Creeper'), ('TNT'), ('Fire'), ('WaterFlow'), ('LavaFlow'), ('LeavesDecay');
SET @env = (SELECT player_id FROM `hawk_players` WHERE player = 'Environment');
SET @creep = (SELECT playerid FROM `lb-players` WHERE playername = 'Creeper');
SET @tnt = (SELECT playerid FROM `lb-players` WHERE playername = 'TNT');
SET @fire = (SELECT playerid FROM `lb-players` WHERE playername = 'Fire');
SET @water = (SELECT playerid FROM `lb-players` WHERE playername = 'WaterFlow');
SET @lava = (SELECT playerid FROM `lb-players` WHERE playername = 'LavaFlow');
SET @leaf = (SELECT playerid FROM `lb-players` WHERE playername = 'LeavesDecay');
INSERT IGNORE INTO `hawk_players` (player)
SELECT DISTINCT `playername`
FROM `lb-players`
WHERE `lb-players`.playerid NOT IN (@leaf, @fire, @water, @lava, @tnt, @creep);

-- Block break
INSERT INTO `hawkeye` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT `lb-main`.date, `hawk_players`.player_id, 0, @worldID, `lb-main`.x,  `lb-main`.y,  `lb-main`.z, `lb-main`.replaced, 'HawkEye'
FROM `lb-main`, `hawk_players`, `lb-players`
WHERE `lb-main`.type = 0
AND `hawk_players`.player = `lb-players`.playername
AND `lb-players`.playerid = `lb-main`.playerid
AND `lb-players`.playerid NOT IN (@leaf, @fire, @water, @lava, @tnt, @creep);

-- Block place
INSERT INTO `hawkeye` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT `lb-main`.date, `hawk_players`.player_id, 1, @worldID, `lb-main`.x,  `lb-main`.y,  `lb-main`.z, `lb-main`.replaced + '-' + `lb-main`.type, 'HawkEye'
FROM `lb-main`, `hawk_players`, `lb-players`
WHERE `lb-main`.replaced IN (0, 8, 9, 10, 11)
AND `hawk_players`.player = `lb-players`.playername
AND `lb-players`.playerid = `lb-main`.playerid
AND `lb-players`.playerid NOT IN (@leaf, @fire, @water, @lava, @tnt, @creep);

-- Leaf decay
INSERT INTO `hawkeye` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT `lb-main`.date, @env, 20, @worldID, `lb-main`.x,  `lb-main`.y,  `lb-main`.z, `lb-main`.replaced, 'HawkEye'
FROM `lb-main`, `hawk_players`, `lb-players`
WHERE `lb-main`.replaced = 18
AND `lb-players`.playerid = `lb-main`.playerid
AND `lb-players`.playerid = @leaf;

-- Explosion
INSERT INTO `hawkeye` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT `lb-main`.date, @env, 17, @worldID, `lb-main`.x,  `lb-main`.y,  `lb-main`.z, `lb-main`.replaced, 'HawkEye'
FROM `lb-main`, `lb-players`
WHERE `lb-main`.replaced IN (0, 8, 9, 10, 11)
AND `lb-main`.playerid = `lb-players`.playerid
AND `lb-players`.playerid IN (@creep, @tnt);

-- Sign Place
INSERT INTO `hawkeye` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT `lb-main`.date, `hawk_players`.player_id, 2, @worldID, `lb-main`.x,  `lb-main`.y,  `lb-main`.z, `lb-main-sign`.signtext, 'HawkEye'
FROM `lb-main`, `lb-main-sign`, `hawk_players`, `lb-players`
WHERE `lb-main`.replaced IN (0, 8, 9, 10, 11)
AND `lb-main`.id = `lb-main-sign`.id
AND `hawk_players`.player = `lb-players`.playername
AND `lb-players`.playerid = `lb-main`.playerid;

-- Sign break
INSERT INTO `hawkeye` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT `lb-main`.date, `hawk_players`.player_id, 29, @worldID, `lb-main`.x,  `lb-main`.y,  `lb-main`.z, `lb-main-sign`.signtext, 'HawkEye'
FROM `lb-main`, `lb-main-sign`, `hawk_players`, `lb-players`
WHERE `lb-main`.type = 0
AND `lb-main`.id = `lb-main-sign`.id
AND `hawk_players`.player = `lb-players`.playername
AND `lb-players`.playerid = `lb-main`.playerid;

-- Chat
INSERT INTO `hawkeye` (date, player_id, action, world_id, x, y, z, data, plugin)
SELECT `lb-chat`.date, `hawk_players`.player_id, 3, @worldID, 0,  0,  0, `lb-chat`.message, 'HawkEye'
FROM `hawk_players`, `lb-players`, `lb-chat`
WHERE `hawk_players`.player = `lb-players`.playername
AND `lb-players`.playerid = `lb-chat`.playerid;