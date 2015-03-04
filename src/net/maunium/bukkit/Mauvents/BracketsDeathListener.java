package net.maunium.bukkit.Mauvents;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BracketsDeathListener implements Listener {
	private Brackets b;
	
	public BracketsDeathListener(Brackets b) {
		this.b = b;
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent evt) {
		if (evt.getEntity().hasMetadata(b.OPPONENT)) b.matchend(evt.getEntity(), false);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent evt) {
		if (evt.getPlayer().hasMetadata(b.OPPONENT)) b.matchend(evt.getPlayer(), true);
	}
}
