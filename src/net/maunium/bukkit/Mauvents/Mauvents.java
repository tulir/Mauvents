package net.maunium.bukkit.Mauvents;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import net.maunium.bukkit.Maussentials.Utils.I18n;
import net.maunium.bukkit.Mauvents.Brackets.Brackets;

public class Mauvents extends JavaPlugin {
	private String stag = ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "Mauvents" + ChatColor.DARK_GREEN + "] " + ChatColor.GRAY, errtag = ChatColor.DARK_RED
			+ "[" + ChatColor.RED + "Mauvents" + ChatColor.DARK_RED + "] " + ChatColor.RED;
	private I18n i18n;
	private Brackets b;
	private LMS lms;
	private LTS lts;
	
	@Override
	public void onEnable() {
		long st = System.currentTimeMillis();
		saveDefaultConfig();
		saveResource("en_US.lang", true);
		
		try {
			i18n = I18n.createInstance(getDataFolder(), getConfig().getString("language", "en_US"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		stag = i18n.translate("stag");
		errtag = i18n.translate("errtag");
		
		getCommand("mauvents").setExecutor(new CommandMauventsAdmin(this));
		b = new Brackets(this);
		lms = new LMS(this);
		lts = new LTS(this);
		
		int et = (int) (System.currentTimeMillis() - st);
		getLogger().info("Mauvents v" + getDescription().getVersion() + " by Tulir293 enabled in " + et + "ms.");
	}
	
	@Override
	public void onDisable() {
		long st = System.currentTimeMillis();
		
		saveConfig();
		
		int et = (int) (System.currentTimeMillis() - st);
		getLogger().info("Mauvents v" + getDescription().getVersion() + " by Tulir293 disabled in " + et + "ms.");
	}
	
	public Brackets getBrackets() {
		return b;
	}
	
	public LMS getLMS() {
		return lms;
	}
	
	public LTS getLTS() {
		return lts;
	}
	
	public String translateStd(String node, Object... arguments) {
		return stag + i18n.translate(node, arguments);
	}
	
	public String translateErr(String node, Object... arguments) {
		return errtag + i18n.translate(node, arguments);
	}
	
	public String translatePlain(String node, Object... arguments) {
		return i18n.translate(node, arguments);
	}
}