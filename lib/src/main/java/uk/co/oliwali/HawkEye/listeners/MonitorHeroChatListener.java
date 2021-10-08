package uk.co.oliwali.HawkEye.listeners;

import com.dthielke.herochat.ChannelChatEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEvent;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.DataEntry;

public class MonitorHeroChatListener extends HawkEyeListener {

   public MonitorHeroChatListener(HawkEye HawkEye) {
      super(HawkEye);
   }

   @HawkEvent(
      dataType = {DataType.HEROCHAT}
   )
   public void onChannelChatEvent(ChannelChatEvent event) {
      Player player = event.getSender().getPlayer();
      Location loc = player.getLocation();
      DataManager.addEntry(new DataEntry(player, DataType.HEROCHAT, loc, event.getChannel().getName() + ": " + event.getMessage()));
   }
}
