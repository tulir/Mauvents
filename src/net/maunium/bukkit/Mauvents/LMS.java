package net.maunium.bukkit.Mauvents;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.maunium.bukkit.Maussentials.Utils.IngameCommandExecutor;
import net.maunium.bukkit.Maussentials.Utils.MetadataUtils;
import net.maunium.bukkit.Maussentials.Utils.SerializableLocation;
import net.maunium.bukkit.Maussentials.Utils.DelayedActions.DelayedTeleport;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * LMS (Last Man Standing) handler
 * 
 * @author Tulir293
 * @since 1.0
 */
public class LMS implements Listener, IngameCommandExecutor {
	public static final String IN_LMS = "MauventsLMSInEvent";
	// Mauvents instance
	private Mauvents plugin;
	// Arena and lobby spawn points
	private Location arena = null, lobby = null;
	// Minimum number of players to start
	private int minPlayers = 5;
	// List of players in game
	private Set<UUID> players = new HashSet<UUID>();
	// If the game is started or not
	private boolean started = false;

	public LMS(Mauvents plugin) {
		this.plugin = plugin;
		// Register listeners
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
		// Set command executor
		this.plugin.getCommand("maulastmanstanding").setExecutor(this);
		// Load configuration
		if (plugin.getConfig().contains("lms.arena")) arena = SerializableLocation.fromString(plugin.getConfig().getString("lms.arena")).toLocation();
		if (plugin.getConfig().contains("lms.lobby")) lobby = SerializableLocation.fromString(plugin.getConfig().getString("lms.lobby")).toLocation();
		minPlayers = plugin.getConfig().getInt("lms.min-players", 3);
	}

	/**
	 * @return True if LMS is in-game.
	 */
	public boolean hasStarted() {
		return started;
	}

	/**
	 * @return True if there are enough players to start a game.
	 */
	public boolean enoughPlayers() {
		return players.size() >= minPlayers;
	}

	/**
	 * @return True if all the necessary settings are correctly set up.
	 */
	public boolean isSetUp() {
		return arena != null && lobby != null && minPlayers > 1;
	}

	/**
	 * Set the arena spawn point to the given location.
	 */
	public void setArena(Location l) {
		arena = l;
		plugin.getConfig().set("lms.arena", new SerializableLocation(l).toString());
	}

	/**
	 * Set the lobby spawn point to the given location.
	 * 
	 * @param l
	 */
	public void setLobby(Location l) {
		lobby = l;
		plugin.getConfig().set("lms.lobby", new SerializableLocation(l).toString());
	}

	/**
	 * Start the match
	 */
	public void start() {
		// Loop through UUIDs
		for (UUID u : players) {
			// Get the player instances
			Player p = plugin.getServer().getPlayer(u);
			// If found, teleport to arena
			if (p != null && p.isOnline()) p.teleport(arena);
			// If not found, remove from match
			else players.remove(u);
		}
		// Set the started flag
		started = true;
	}

	/**
	 * End the match
	 */
	public void end() {
		// Make sure that there is less than two players left
		if (players.size() < 2) {
			// Get the UUID remaining player
			UUID u = players.iterator().next();
			// Make sure that the UUID is not null.
			if (u != null) {
				// Get the player instance.
				Player pp = plugin.getServer().getPlayer(u);
				// Make sure the player is online
				if (pp != null && pp.isOnline()) {
					// Broadcast win message
					plugin.getServer().broadcastMessage(plugin.translateStd("lms.win", pp.getName()));
					// Teleport to world spawn
					pp.teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());
					// Remove metadata
					MetadataUtils.removeMetadata(pp, IN_LMS, plugin);
				} else plugin.getServer().broadcastMessage(plugin.translateStd("lms.win.null"));
			} else plugin.getServer().broadcastMessage(plugin.translateStd("lms.win.null"));
		} else plugin.getServer().broadcastMessage(plugin.translateStd("lms.win.null"));
		// Clear player list
		players.clear();
		// Set started flag to false
		started = false;
	}

	/**
	 * Join the given player to the match.
	 */
	public boolean join(Player p, Boolean delayedTP) {
		// Make sure the player is not in-game already and that the match hasn't started.
		if (started || players.contains(p.getUniqueId())) return false;
		else {
			// Add the players UUID to the player list.
			players.add(p.getUniqueId());
			// Add the LMS metadata
			MetadataUtils.setFixedMetadata(p, IN_LMS, true, plugin);
			// Send a "please stand by for tp" message
			p.sendMessage(plugin.translateStd("lms.lobby.tping"));
			// Create a Maussentials delayed teleport to teleport the player to the lobby.
			new DelayedTeleport(p, plugin.getConfig().getInt("tpdelay") * 20, lobby, plugin.translateStd("lms.lobby.tped"),
					plugin.translateErr("lms.lobby.tpfail"), 15, 0).start();
			return true;
		}
	}

	/**
	 * Make the given player leave the match
	 * 
	 * @param death True if this was called from a death event.
	 */
	public boolean leave(Player p, boolean death) {
		// Make sure that the player is in the game
		if (!players.contains(p.getUniqueId()) && !p.hasMetadata(IN_LMS)) return false;
		// Remove the LMS metadata
		MetadataUtils.removeMetadata(p, IN_LMS, plugin);
		// Remove the UUID from the player list
		players.remove(p.getUniqueId());
		// If the leaving was not caused by a death, teleport the player to the spawn.
		if (!death) p.teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());
		// If this was within the last two players, end the game.
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

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPreCommand(PlayerCommandPreprocessEvent evt) {
		if (evt.getPlayer().hasMetadata(IN_LMS)) {
			if (!evt.getMessage().startsWith("/lms") && !evt.getMessage().startsWith("/maulms") && !evt.getMessage().startsWith("/maulastmanstanding")
					&& !evt.getMessage().startsWith("/lastmanstanding") && !evt.getMessage().startsWith("/mauvents")) {
				evt.setCancelled(true);
				evt.getPlayer().sendMessage(plugin.translateErr("lts.commandinmatch"));
			}
		}
	}

	@Override
	public boolean onCommand(Player sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			if (!players.contains(sender.getUniqueId())) {
				if (join(sender, true)) sender.sendMessage(plugin.translateStd("lms.join"));
				else sender.sendMessage(plugin.translateErr("lms.alreadystarted"));
			} else {
				if (leave(sender, false)) sender.sendMessage(plugin.translateStd("lms.leave"));
				else sender.sendMessage(plugin.translateErr("lms.notin"));
			}
		} else {
			if (args[0].equalsIgnoreCase("join")) {
				if (!players.contains(sender.getUniqueId())) {
					if (join(sender, true)) sender.sendMessage(plugin.translateStd("lms.join"));
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

	// e > evt murr
	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {
		// if lms is not setup and action is not the right 1 -> stop
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK || !isSetUp()) { return; }
		if (e.getClickedBlock().getState() instanceof Sign) {
			Sign s = (Sign) e.getClickedBlock().getState();
			// get line 1
			if (s.getLine(0).contains(ChatColor.DARK_BLUE + "[LMS]")) {
				if (s.getLine(1).contains(ChatColor.RED + "Started")) {
					// set line 1 actually (2) to joined players.size() >_>
					if(started) {
						s.setLine(1, ChatColor.RED + "Started");
						s.setLine(3, ChatColor.DARK_AQUA + "Click to update!");
					}
					else  {
						s.setLine(1, ChatColor.GREEN + "Players: " + players.size());
						s.setLine(3, ChatColor.DARK_AQUA + "Click to join");
					}
					s.update();
				}
				else {
					// no delayed teleport
					if (join(e.getPlayer(), false)) {
						if (enoughPlayers()) {
							// start lms
							start();
							// set line 1 to started
							s.setLine(1, ChatColor.RED + "Started");
							s.setLine(3, ChatColor.DARK_AQUA + "Click to update!");
							// update the sign
							s.update();
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCreateSign(SignChangeEvent e) {
		String[] lines = e.getLines();
		if (lines.length == 0 || !e.getPlayer().isOp()) { return; }
		if (lines[0].toLowerCase().contains("[lms]")) {
			e.setLine(0, ChatColor.DARK_BLUE + "[LMS]");
			if (started) {
				e.setLine(1, ChatColor.RED + "Started");
				e.setLine(3, ChatColor.DARK_AQUA + "Click to update!");
			}
			else {
				e.setLine(1, ChatColor.GREEN + "Players: 0");
				e.setLine(3, ChatColor.DARK_AQUA + "Click to join");
			}
		}
	}

	/*
	 * NOT READY YET!!!!! set location of sign cuz after a game the second line needs to be Players: 0
	 */
}
