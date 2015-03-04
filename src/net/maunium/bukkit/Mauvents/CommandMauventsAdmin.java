package net.maunium.bukkit.Mauvents;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import net.maunium.bukkit.MauBukLib.IngameCommandExecutor;

public class CommandMauventsAdmin extends IngameCommandExecutor {
	private Mauvents plugin;
	
	public CommandMauventsAdmin(Mauvents plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(Player p, Command cmd, String label, String[] args) {
		// TODO Auto-generated method stub
		return false;
	}
}
