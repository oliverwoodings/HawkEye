package uk.co.oliwali.HawkEye.commands;

import uk.co.oliwali.HawkEye.Rollback;
import uk.co.oliwali.HawkEye.Undo;
import uk.co.oliwali.HawkEye.util.Util;

public class PreviewCancelCommand extends BaseCommand {

   public PreviewCancelCommand() {
      this.name = "preview cancel";
      this.argLength = 0;
      this.permission = "preview";
      this.usage = "<- cancel rollback preview";
   }

   public boolean execute() {
      if(this.session.isInPreview() && this.session.getRollbackType() == Rollback.RollbackType.LOCAL) {
         new Undo(this.session);
         Util.sendMessage(this.sender, "&cPreview rollback cancelled");
         this.session.setInPreview(false);
         return true;
      } else {
         Util.sendMessage(this.sender, "&cNo preview to cancel!");
         return true;
      }
   }

   public void moreHelp() {
      Util.sendMessage(this.sender, "&cCancels results of a &7/hawk preview");
      Util.sendMessage(this.sender, "&cOnly affects you - no changes are seen by anyony else");
   }
}
