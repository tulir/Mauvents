package net.maunium.bukkit.Mauvents;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import net.maunium.bukkit.Maussentials.Utils.I18n;
import net.maunium.bukkit.Mauvents.Brackets.Brackets;

/**
 * Maunium Events plugin main class.
 * 
 * @author Tulir293
 * @since 1.0
 */
public class Mauvents extends JavaPlugin {
	private String stag = ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "Mauvents" + ChatColor.DARK_GREEN + "] " + ChatColor.GRAY,
			errtag = ChatColor.DARK_RED + "[" + ChatColor.RED + "Mauvents" + ChatColor.DARK_RED + "] " + ChatColor.RED;
	// Internationalization instance
	private I18n i18n;
	// Game handler instances
	private Brackets b;
	private LMS lms;
	private LTS lts;
	
	@Override
	public void onEnable() {
		long st = System.currentTimeMillis();
		// Save the default config.
		saveDefaultConfig();
		// Save and override the default language(s)
		saveResource("en_US.lang", true);
		
		// Load I18n.
		try {
			i18n = I18n.createInstance(getDataFolder(), getConfig().getString("language", "en_US"));
		} catch (IOException e) {
			// Shit happens.
			e.printStackTrace();
		}
		
		// Use translated message prefixes
		stag = i18n.translate("stag");
		errtag = i18n.translate("errtag");
		
		// Set executor for admin command
		getCommand("mauvents").setExecutor(new CommandMauventsAdmin(this));
		// Create the game instances
		b = new Brackets(this);
		lms = new LMS(this);
		lts = new LTS(this);
		
		int et = (int) (System.currentTimeMillis() - st);
		getLogger().info("Mauvents v" + getDescription().getVersion() + " by Tulir293 enabled in " + et + "ms.");
	}
	
	@Override
	public void onDisable() {
		long st = System.currentTimeMillis();
		
		// Save configuration
		saveConfig();
		
		int et = (int) (System.currentTimeMillis() - st);
		getLogger().info("Mauvents v" + getDescription().getVersion() + " by Tulir293 disabled in " + et + "ms.");
	}
	
	/**
	 * Get the Brackets handler for this Mauvents instance.
	 */
	public Brackets getBrackets() {
		return b;
	}
	
	/**
	 * Get the LMS (Last Man Standing) handler for this Mauvents instance.
	 */
	public LMS getLMS() {
		return lms;
	}
	
	/**
	 * Get the LTS (Last Team Standing) handler for this Mauvents instance.
	 */
	public LTS getLTS() {
		return lts;
	}
	
	/**
	 * Translate the given I18n node with the given arguments and prepend the standard output tag to it.
	 * 
	 * @return The translated message or the node, if translation not found.
	 */
	public String translateStd(String node, Object... arguments) {
		return stag + i18n.translate(node, arguments);
	}
	
	/**
	 * Translate the given I18n node with the given arguments and prepend the error output tag to it.
	 * 
	 * @return The translated message or the node, if translation not found.
	 */
	public String translateErr(String node, Object... arguments) {
		return errtag + i18n.translate(node, arguments);
	}
	
	/**
	 * Translate the given I18n node with the given arguments.
	 * 
	 * @return The translated message or the node, if translation not found.
	 */
	public String translatePlain(String node, Object... arguments) {
		return i18n.translate(node, arguments);
	}
}