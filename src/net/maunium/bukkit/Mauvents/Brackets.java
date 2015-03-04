package net.maunium.bukkit.Mauvents;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.maunium.bukkit.MauBukLib.MauUtils;

public class Brackets {
	private Mauvents plugin;
	private Location[] spawnpoints;
	private List<UUID> players;
	private boolean inGame = false;
	
	public Brackets(Mauvents plugin) {
		this.plugin = plugin;
		
		FileConfiguration conf = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "brackets.yml"));
		List<String> tmp = conf.getStringList("spawn-points");
		
		for (int i = 0; i < tmp.size(); i++)
			spawnpoints[i] = MauUtils.parseLocation(tmp.get(i));
		
		players = new ArrayList<UUID>(spawnpoints.length);
	}
	
	public void join(Player p){
		
	}
	
	public void leave(Player p){
		
	}
}
