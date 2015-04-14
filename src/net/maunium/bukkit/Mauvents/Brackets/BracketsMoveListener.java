package net.maunium.bukkit.Mauvents.Brackets;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class BracketsMoveListener implements Listener {
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent evt) {
		if (evt.getPlayer().hasMetadata(Brackets.STARTING)) evt.setCancelled(true);
	}
}
