package net.maunium.bukkit.Mauvents.Brackets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.maunium.bukkit.Maussentials.Utils.MetadataUtils;
import net.maunium.bukkit.Maussentials.Utils.SerializableLocation;
import net.maunium.bukkit.Mauvents.Mauvents;

public class Brackets {
	public static final String IN_BRACKETS = "MauventsBracketsInBrackets", OPPONENT = "MauventsBracketsOpponent", ROUND = "MauventsBracketsOnRound",
			STARTING = "MauventsBracketsMatchStarting";
	private Mauvents plugin;
	private Location[] spawnpoints;
	private Location p1, p2;
	private PlayerList players;
	private int currentRound = 0;
	private Random r;
	private boolean inGame = false;
	
	public Brackets(Mauvents plugin) {
		this.plugin = plugin;
		r = new Random(plugin.getServer().getWorlds().get(0).getSeed() + System.currentTimeMillis());
		
		plugin.getServer().getPluginManager().registerEvents(new BracketsDeathListener(this), plugin);
		plugin.getCommand("maubrackets").setExecutor(new CommandBrackets(this));
		
		reloadConfig();
	}
	
	public void start() {
		inGame = true;
		currentRound = 1;
		if (players.size() < 2) return;
		for (UUID u : players) {
			Player p = plugin.getServer().getPlayer(u);
			MetadataUtils.setFixedMetadata(p, IN_BRACKETS, true, plugin);
			MetadataUtils.setFixedMetadata(p, ROUND, 1, plugin);
		}
		matchstart();
	}
	
	public void end() {
		if (players.isEmpty()) inGame = false;
		else if (players.size() == 1) {
			
		}
	}
	
	public void setspawn(Player p, String[] args) {
		
	}
	
	public void matchend(Player p, boolean quit) {
		players.remove(p.getUniqueId());
		Player o = plugin.getServer().getPlayer((UUID) MetadataUtils.getMetadata(p, OPPONENT, plugin).value());
		
		p.teleport(p.getWorld().getSpawnLocation());
		o.teleport(spawnpoints[players.indexOf(o.getUniqueId())]);
		
		int round = MetadataUtils.getMetadata(o, ROUND, plugin).asInt() + 1;
		MetadataUtils.setFixedMetadata(o, ROUND, round, plugin);
		MetadataUtils.removeMetadata(o, ROUND, plugin);
		MetadataUtils.removeMetadata(o, STARTING, plugin);
		MetadataUtils.removeMetadata(p, IN_BRACKETS, plugin);
		MetadataUtils.removeMetadata(p, OPPONENT, plugin);
		MetadataUtils.removeMetadata(p, ROUND, plugin);
		MetadataUtils.removeMetadata(p, STARTING, plugin);
		p.teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());
		
		broadcast(plugin.translateStd("brackets.matchend." + (quit ? "quit" : "death"), p.getName(), o.getName()));
		
		if (players.size() < 2) end();
		else matchstart();
	}
	
	public void matchstart() {
		if (players.size() < 2) {
			end();
			return;
		}
		List<UUID> l = getPlayersOnRound(currentRound);
		if (l.size() == 0) {
			currentRound++;
			broadcast(plugin.translateStd("brackets.nextround", currentRound));
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
		
		Player pc = plugin.getServer().getPlayer(p);
		Player po = plugin.getServer().getPlayer(o);
		
		if (pc.isOnline() && po.isOnline()) {
			MetadataUtils.setFixedMetadata(pc, OPPONENT, po.getUniqueId(), plugin);
			MetadataUtils.setFixedMetadata(po, OPPONENT, pc.getUniqueId(), plugin);
			MetadataUtils.setFixedMetadata(pc, STARTING, true, plugin);
			MetadataUtils.setFixedMetadata(po, STARTING, true, plugin);
			
			pc.teleport(p1);
			po.teleport(p2);
			countdown(pc, po);
		} else {
			if (players.size() < 2) end();
			else matchstart();
		}
	}
	
	public void countdown(Player pc, Player po) {
		plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				if (!pc.isOnline() || !po.isOnline()) return;
				broadcast(plugin.translatePlain("brackets.match.startingin", 4));
			}
		}, 20);
		plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				if (!pc.isOnline() || !po.isOnline()) return;
				broadcast(plugin.translatePlain("brackets.match.startingin", 3));
			}
		}, 40);
		plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				if (!pc.isOnline() || !po.isOnline()) return;
				broadcast(plugin.translatePlain("brackets.match.startingin", 2));
			}
		}, 60);
		plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				if (!pc.isOnline() || !po.isOnline()) return;
				broadcast(plugin.translatePlain("brackets.match.startingin", 1));
			}
		}, 80);
		plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				if (!pc.isOnline() || !po.isOnline()) return;
				broadcast(plugin.translatePlain("brackets.match.startingnow"));
				MetadataUtils.removeMetadata(pc, STARTING, plugin);
				MetadataUtils.removeMetadata(po, STARTING, plugin);
			}
		}, 100);
		broadcast(plugin.translateStd("brackets.match", pc.getName(), po.getName()));
	}
	
	public List<UUID> getPlayersOnRound(int round) {
		List<UUID> rtrn = new ArrayList<UUID>();
		for (UUID u : players) {
			int i = MetadataUtils.getMetadata(plugin.getServer().getPlayer(u), ROUND, plugin).asInt();
			if (i == round) rtrn.add(u);
		}
		return rtrn;
	}
	
	public boolean hasStarted() {
		return inGame;
	}
	
	public int join(Player p) {
		if (!inGame) {
			if (players.size() < spawnpoints.length) {
				players.add(p.getUniqueId());
				p.teleport(spawnpoints[players.indexOf(p.getUniqueId())]);
				return 0;
			} else return 2;
		} else return 1;
	}
	
	public void leave(Player p) {
		if (players.contains(p.getUniqueId())) {
			if (p.hasMetadata(OPPONENT) && inGame) matchend(p, true);
			else players.remove(p.getUniqueId());
			p.teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());
		}
	}
	
	private void broadcast(String msg) {
		for (UUID u : players)
			plugin.getServer().getPlayer(u).sendMessage(msg);
	}
	
	public void reloadConfig() {
		File f = new File(plugin.getDataFolder(), "brackets.yml");
		if (!f.exists()) {
			spawnpoints = new Location[0];
			p1 = null;
			p2 = null;
		} else {
			FileConfiguration conf = YamlConfiguration.loadConfiguration(f);
			List<String> tmp = conf.getStringList("spawn.waiting");
			
			for (int i = 0; i < tmp.size(); i++)
				spawnpoints[i] = SerializableLocation.fromString(tmp.get(i)).toLocation();
			
			p1 = SerializableLocation.fromString(conf.getString("spawn.p1")).toLocation();
			p2 = SerializableLocation.fromString(conf.getString("spawn.p2")).toLocation();
		}
		
		players = new PlayerList(spawnpoints.length);
	}
}
