package net.maunium.bukkit.Mauvents;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import net.maunium.bukkit.MauBukLib.MauUtils;

public class Brackets {
	public final String IN_BRACKETS = "MauventsBracketsInBrackets", OPPONENT = "MauventsBracketsOpponent", ROUND = "MauventsBracketsOnRound";
	private Mauvents plugin;
	private Location[] spawnpoints;
	private List<UUID> players;
	private int currentRound = 0;
	private Random r;
	private boolean inGame = false;
	
	public Brackets(Mauvents plugin) {
		this.plugin = plugin;
		r = new Random(plugin.getServer().getWorlds().get(0).getSeed() + System.currentTimeMillis());
		
		plugin.getServer().getPluginManager().registerEvents(new BracketsDeathListener(this), plugin);
		
		FileConfiguration conf = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "brackets.yml"));
		List<String> tmp = conf.getStringList("spawn-points");
		
		for (int i = 0; i < tmp.size(); i++)
			spawnpoints[i] = MauUtils.parseLocation(tmp.get(i));
		
		players = new ArrayList<UUID>(spawnpoints.length);
	}
	
	public void start() {
		inGame = true;
		currentRound = 1;
		for (UUID u : players) {
			Player p = plugin.getServer().getPlayer(u);
			p.setMetadata(IN_BRACKETS, new FixedMetadataValue(plugin, true));
			p.setMetadata(ROUND, new FixedMetadataValue(plugin, 1));
		}
	}
	
	public void matchend(Player p, boolean quit) {
		players.remove(p.getUniqueId());
		p.teleport(p.getWorld().getSpawnLocation());
		Player o = plugin.getServer().getPlayer((UUID) MauUtils.getMetadata(p, OPPONENT, plugin).value());
		o.teleport(spawnpoints[players.indexOf(o)]);
		broadcast(plugin.stag + plugin.translate("brackets.matchend." + (quit ? "quit" : "death"), p.getName(), o.getName()));
		matchstart();
	}
	
	public void matchstart() {
		if (players.isEmpty()) {
			// TODO: End?
			return;
		}
		List<UUID> l = getPlayersOnRound(currentRound);
		if (l.size() == 0) {
			currentRound++;
			broadcast(plugin.stag + plugin.translate("brackets.nextround", currentRound));
			matchstart();
			return;
		}
		
		UUID p = l.get(r.nextInt(l.size()));
		l.remove(p);
		UUID o;
		if (l.size() == 0) {
			List<UUID> l2 = getPlayersOnRound(currentRound + 1);
			o = l2.get(r.nextInt(l2.size()));
		} else o = l.get(r.nextInt(l.size()));
		
		// TODO: Start match between players p and o
	}
	
	public List<UUID> getPlayersOnRound(int round) {
		List<UUID> rtrn = new ArrayList<UUID>();
		for (UUID u : players) {
			int i = MauUtils.getMetadata(plugin.getServer().getPlayer(u), ROUND, plugin).asInt();
			if (i == round) rtrn.add(u);
		}
		return rtrn;
	}
	
	public void join(Player p) {
		
	}
	
	public void leave(Player p) {
		
	}
	
	private void broadcast(String msg) {
		for (UUID u : players) {
			plugin.getServer().getPlayer(u).sendMessage(msg);
		}
	}
}
