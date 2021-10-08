package uk.co.oliwali.HawkEye.commands;

import uk.co.oliwali.HawkEye.Undo;
import uk.co.oliwali.HawkEye.util.Util;

public class UndoCommand extends BaseCommand {

   public UndoCommand() {
      this.name = "undo";
      this.permission = "rollback";
      this.usage = "<- reverses your previous rollback";
   }

   public boolean execute() {
      new Undo(this.session);
      return true;
   }

   public void moreHelp() {
      Util.sendMessage(this.sender, "&cReverses your previous rollback if you made a mistake with it");
   }
}
