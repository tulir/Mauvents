package net.maunium.bukkit.Mauvents.Brackets;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class BracketsMoveListener implements Listener {
	private Brackets host;
	
	public BracketsMoveListener(Brackets host) {
		this.host = host;
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent evt) {
		if (evt.getPlayer().hasMetadata(host.STARTING)) evt.setCancelled(true);
	}
}
