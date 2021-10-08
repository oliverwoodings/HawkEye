package uk.co.oliwali.HawkEye.commands;

import uk.co.oliwali.HawkEye.ToolManager;
import uk.co.oliwali.HawkEye.util.Util;

public class ToolResetCommand extends BaseCommand {

   public ToolResetCommand() {
      this.name = "tool reset";
      this.argLength = 0;
      this.permission = "tool.bind";
      this.usage = " <- resets tool to default properties";
   }

   public boolean execute() {
      ToolManager.resetTool(this.session);
      Util.sendMessage(this.player, "&cTool reset to default parameters");
      return true;
   }

   public void moreHelp() {
      Util.sendMessage(this.sender, "&cReset HawkEye tool to default properties");
      Util.sendMessage(this.sender, "&cSee &7/hawk tool bind help");
   }
}
