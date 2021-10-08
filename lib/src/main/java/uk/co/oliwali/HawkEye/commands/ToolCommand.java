package uk.co.oliwali.HawkEye.commands;

import uk.co.oliwali.HawkEye.ToolManager;
import uk.co.oliwali.HawkEye.util.Util;

public class ToolCommand extends BaseCommand {

   public ToolCommand() {
      this.name = "tool";
      this.argLength = 0;
      this.permission = "tool";
      this.usage = " <- enables/disables the searching tool";
   }

   public boolean execute() {
      if(!this.session.isUsingTool()) {
         ToolManager.enableTool(this.session, this.player);
      } else {
         ToolManager.disableTool(this.session, this.player);
      }

      return true;
   }

   public void moreHelp() {
      Util.sendMessage(this.sender, "&cGives you the HawkEye tool. You can use this to see changes at specific places");
      Util.sendMessage(this.sender, "&cLeft click a block or place the tool to get information");
   }
}
