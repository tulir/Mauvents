package net.maunium.bukkit.Mauvents;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BracketsDeathListener implements Listener {
	private Mauvents plugin;
	private Brackets b;
	
	public BracketsDeathListener(Mauvents plugin, Brackets b) {
		this.plugin = plugin;
		this.b = b;
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent evt) {
		
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent evt) {
		
	}
}
