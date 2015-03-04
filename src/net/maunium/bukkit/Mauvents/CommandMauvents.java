package net.maunium.bukkit.Mauvents;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import net.maunium.bukkit.MauBukLib.IngameCommandExecutor;

public class CommandMauvents extends IngameCommandExecutor {
	private Mauvents plugin;
	
	public CommandMauvents(Mauvents plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(Player p, Command cmd, String label, String[] args) {
		
		return false;
	}
}
