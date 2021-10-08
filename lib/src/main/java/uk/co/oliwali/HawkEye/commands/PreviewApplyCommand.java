package uk.co.oliwali.HawkEye.commands;

import uk.co.oliwali.HawkEye.Rollback;
import uk.co.oliwali.HawkEye.util.Util;

public class PreviewApplyCommand extends BaseCommand {

   public PreviewApplyCommand() {
      this.name = "preview apply";
      this.argLength = 0;
      this.permission = "preview";
      this.usage = "<- apply rollback preview";
   }

   public boolean execute() {
      if(!this.session.isInPreview()) {
         Util.sendMessage(this.sender, "&cNo preview to apply!");
         return true;
      } else {
         Util.sendMessage(this.sender, "&cAttempting to apply rollback to world...");
         new Rollback(Rollback.RollbackType.GLOBAL, this.session);
         this.session.setInPreview(false);
         return true;
      }
   }

   public void moreHelp() {
      Util.sendMessage(this.sender, "&cApplies the results of a &7/hawk preview&c globally");
      Util.sendMessage(this.sender, "&cUntil this command is called, the preview is only visible to you");
   }
}
