package net.maunium.bukkit.Mauvents;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.maunium.bukkit.Maussentials.Utils.IngameCommandExecutor;
import net.maunium.bukkit.Maussentials.Utils.MetadataUtils;
import net.maunium.bukkit.Maussentials.Utils.SerializableLocation;
import net.maunium.bukkit.Maussentials.Utils.DelayedActions.DelayedTeleport;

public class LMS implements Listener, IngameCommandExecutor {
	public static final String IN_LMS = "MauventsLMSInEvent";
	private Mauvents plugin;
	private Location arena = null, lobby = null;
	private int minPlayers = 5;
	private Set<UUID> players = new HashSet<UUID>();
	private boolean started = false;
	
	public LMS(Mauvents plugin) {
		this.plugin = plugin;
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin.getCommand("maulastmanstanding").setExecutor(this);
		if (plugin.getConfig().contains("lms.arena")) arena = SerializableLocation.fromString(plugin.getConfig().getString("lms.arena")).toLocation();
		if (plugin.getConfig().contains("lms.lobby")) lobby = SerializableLocation.fromString(plugin.getConfig().getString("lms.lobby")).toLocation();
		minPlayers = plugin.getConfig().getInt("lms.minplayers", 3);
	}
	
	public boolean hasStarted() {
		return started;
	}
	
	public boolean enoughPlayers() {
		return players.size() >= minPlayers;
	}
	
	public boolean isSetUp() {
		return arena != null && lobby != null && minPlayers > 1;
	}
	
	public void setArena(Location l) {
		arena = l;
		plugin.getConfig().set("lms.arena", new SerializableLocation(l).toString());
	}
	
	public void setLobby(Location l) {
		lobby = l;
		plugin.getConfig().set("lms.lobby", new SerializableLocation(l).toString());
	}
	
	public void start() {
		for (UUID u : players) {
			Player p = plugin.getServer().getPlayer(u);
			if (p != null && p.isOnline()) p.teleport(arena);
			else players.remove(u);
		}
		started = true;
	}
	
	public void end() {
		if (players.size() == 1) {
			UUID u = players.iterator().next();
			if (u != null) {
				Player pp = plugin.getServer().getPlayer(u);
				if (pp != null) {
					plugin.getServer().broadcastMessage(plugin.translateStd("lms.win", pp.getName()));
					pp.teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());
					MetadataUtils.removeMetadata(pp, IN_LMS, plugin);
				} else plugin.getServer().broadcastMessage(plugin.translateStd("lms.win.null"));
			} else plugin.getServer().broadcastMessage(plugin.translateStd("lms.win.null"));
		} else plugin.getServer().broadcastMessage(plugin.translateStd("lms.win.null"));
		players.clear();
		started = false;
	}
	
	public boolean join(Player p) {
		if (started || players.contains(p.getUniqueId())) return false;
		else {
			players.add(p.getUniqueId());
			MetadataUtils.setFixedMetadata(p, IN_LMS, true, plugin);
			p.sendMessage(plugin.translateStd("lms.lobby.tping"));
			new DelayedTeleport(p, 100, lobby, plugin.translateStd("lms.lobby.tped"), plugin.translateErr("lms.lobby.tpfail"), 15, 0).start();
			return true;
		}
	}
	
	public boolean leave(Player p, boolean death) {
		if (!players.contains(p.getUniqueId()) && !p.hasMetadata(IN_LMS)) return false;
		MetadataUtils.removeMetadata(p, IN_LMS, plugin);
		players.remove(p.getUniqueId());
		if (!death) p.teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());
		if (started && players.size() < 2) end();
		return true;
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent evt) {
		if (evt.getPlayer().hasMetadata(IN_LMS) || players.contains(evt.getPlayer().getUniqueId())) {
			leave(evt.getPlayer(), false);
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent evt) {
		if (evt.getEntity().hasMetadata(IN_LMS) || players.contains(evt.getEntity().getUniqueId())) {
			leave(evt.getEntity(), true);
		}
	}
	
	@Override
	public boolean onCommand(Player sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			if (!players.contains(sender.getUniqueId())) {
				if (join(sender)) sender.sendMessage(plugin.translateStd("lms.join"));
				else sender.sendMessage(plugin.translateErr("lms.alreadystarted"));
			} else {
				if (leave(sender, false)) sender.sendMessage(plugin.translateStd("lms.leave"));
				else sender.sendMessage(plugin.translateErr("lms.notin"));
			}
		} else {
			if (args[0].equalsIgnoreCase("join")) {
				if (!players.contains(sender.getUniqueId())) {
					if (join(sender)) sender.sendMessage(plugin.translateStd("lms.join"));
					else sender.sendMessage(plugin.translateErr("lms.alreadystarted"));
				} else sender.sendMessage(plugin.translateErr("lms.alreadyin"));
			} else if (args[0].equalsIgnoreCase("leave")) {
				if (players.contains(sender.getUniqueId())) {
					if (leave(sender, false)) sender.sendMessage(plugin.translateStd("lms.leave"));
					else sender.sendMessage(plugin.translateErr("lms.notin"));
				} else sender.sendMessage(plugin.translateErr("lms.notin"));
			}
		}
		return true;
	}
}
