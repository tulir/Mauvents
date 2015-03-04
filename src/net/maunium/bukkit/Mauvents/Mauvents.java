package net.maunium.bukkit.Mauvents;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Mauvents extends JavaPlugin {
	
	public String version;
	public final String name = "Mauvents", author = "Tulir293", stag = ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + name + ChatColor.DARK_GREEN + "] " + ChatColor.GRAY,
			errtag = ChatColor.DARK_RED + "[" + ChatColor.RED + name + ChatColor.DARK_RED + "] " + ChatColor.RED;
	
	@Override
	public void onEnable() {
		long st = System.currentTimeMillis();
		version = this.getDescription().getVersion();
		// this.saveDefaultConfig();
		
		this.getCommand("mauvents").setExecutor(new CommandMauvents(this));
		this.getCommand("mauventsadmin").setExecutor(new CommandMauventsAdmin(this));
		int et = (int) (System.currentTimeMillis() - st);
		getLogger().info(name + " v" + version + " by " + author + " enabled in " + et + "ms.");
	}
	
	@Override
	public void onDisable() {
		long st = System.currentTimeMillis();
		
		// TODO: Disable code
		
		int et = (int) (System.currentTimeMillis() - st);
		getLogger().info(name + " v" + version + " by " + author + " disabled in " + et + "ms.");
	}
}