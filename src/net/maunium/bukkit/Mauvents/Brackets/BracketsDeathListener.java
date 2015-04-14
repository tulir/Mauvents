package net.maunium.bukkit.Mauvents.Brackets;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BracketsDeathListener implements Listener {
	private Brackets host;
	
	public BracketsDeathListener(Brackets host) {
		this.host = host;
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent evt) {
		if (evt.getEntity().hasMetadata(Brackets.OPPONENT)) host.matchend(evt.getEntity(), false);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent evt) {
		host.leave(evt.getPlayer());
	}
}
