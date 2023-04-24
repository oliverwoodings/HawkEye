package uk.co.oliwali.HawkEye.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.util.Util;

public class TpEntityCommand extends BaseCommand {

	   public TpEntityCommand() {
	      this.name = "tpe";
	      this.argLength = 1;
	      this.permission = "tpto";
	      this.usage = "<idOfEntity> <- teleport to specified entity";
	   }

	   public boolean execute() {
	      Entity entity = null;
	      int idWorld = 0;
	      List<World> worlds = Bukkit.getWorlds();
	      
	      String uuid = DataManager.getEntityUuid(Integer.valueOf(Integer.parseInt((String) this.args.get(0), 16)));
	      if(uuid == null) {
	    	  Util.sendMessage(this.sender, "&cEntity not found");
	    	  return true;
	      }
	      
	      while(entity == null & idWorld < worlds.size()) {
	    	  int idEntity = 0;
	    	  List<Entity> entitys = worlds.get(idWorld).getEntities();
	    	  while(entity == null & idEntity < entitys.size()) {
	    		  if(entitys.get(idEntity).getUniqueId().equals(UUID.fromString(uuid))) {
	    			  entity = entitys.get(idEntity);
	    		  }
	    		  idEntity++;
	    	  }
	    	  idWorld++;
	      }
	      
	      if(entity != null) {
	    	  this.player.teleport(entity);
	    	  Util.sendMessage(this.sender, "&cTeleport to entity: &7" + (String) this.args.get(0));
	      }
	      else {
	    	  Util.sendMessage(this.sender, "&cEntity not found");
	      }
	      
	      return true;
	   }

	   public void moreHelp() {
	      Util.sendMessage(this.sender, "&cTakes you to the location of the entity with the specified ID of entity");
	      Util.sendMessage(this.sender, "&cThe ID of entity can be found in either the DataLog interface or when you do a search command");
	   }
}
