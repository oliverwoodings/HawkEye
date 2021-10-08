package uk.co.oliwali.HawkEye.commands;

import uk.co.oliwali.HawkEye.ToolManager;
import uk.co.oliwali.HawkEye.util.Util;

public class ToolBindCommand extends BaseCommand {

   public ToolBindCommand() {
      this.name = "tool bind";
      this.argLength = 1;
      this.permission = "tool.bind";
      this.usage = " <- bind custom parameters to the tool";
   }

   public boolean execute() {
      ToolManager.bindTool(this.player, this.session, this.args);
      return true;
   }

   public void moreHelp() {
      Util.sendMessage(this.sender, "&cAllows you to bind custom search parameters onto the tool");
      Util.sendMessage(this.sender, "&cSee &7/hawk search help for info on parameters");
   }
}
