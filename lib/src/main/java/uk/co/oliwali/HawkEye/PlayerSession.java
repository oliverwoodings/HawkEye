package uk.co.oliwali.HawkEye;

import java.util.List;
import org.bukkit.command.CommandSender;
import uk.co.oliwali.HawkEye.util.Config;

public class PlayerSession {

   private CommandSender sender;
   private List searchResults = null;
   private List rollbackResults = null;
   private Rollback.RollbackType rollbackType = null;
   private boolean usingTool = false;
   private boolean doingRollback = false;
   private String[] toolCommand;
   private boolean inPreview;


   public PlayerSession(CommandSender sender) {
      this.toolCommand = Config.DefaultToolCommand;
      this.inPreview = false;
      this.sender = sender;
   }

   public CommandSender getSender() {
      return this.sender;
   }

   public void setSender(CommandSender sender) {
      this.sender = sender;
   }

   public List getSearchResults() {
      return this.searchResults;
   }

   public void setSearchResults(List searchResults) {
      this.searchResults = searchResults;
   }

   public List getRollbackResults() {
      return this.rollbackResults;
   }

   public void setRollbackResults(List rollbackResults) {
      this.rollbackResults = rollbackResults;
   }

   public void setRollbackType(Rollback.RollbackType rollbackType) {
      this.rollbackType = rollbackType;
   }

   public Rollback.RollbackType getRollbackType() {
      return this.rollbackType;
   }

   public boolean isUsingTool() {
      return this.usingTool;
   }

   public void setUsingTool(boolean usingTool) {
      this.usingTool = usingTool;
   }

   public boolean doingRollback() {
      return this.doingRollback;
   }

   public void setDoingRollback(boolean doingRollback) {
      this.doingRollback = doingRollback;
   }

   public String[] getToolCommand() {
      return this.toolCommand;
   }

   public void setToolCommand(String[] toolCommand) {
      this.toolCommand = toolCommand;
   }

   public boolean isInPreview() {
      return this.inPreview;
   }

   public void setInPreview(boolean inPreview) {
      this.inPreview = inPreview;
   }
}
