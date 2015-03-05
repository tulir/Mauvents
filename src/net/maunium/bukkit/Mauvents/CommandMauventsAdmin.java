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
		if(args.length > 1){
			if(args[0].equalsIgnoreCase("brackets")){
				if(args[0].equalsIgnoreCase("start")){
					if(plugin.getBrackets().hasStarted()) p.sendMessage(plugin.errtag + plugin.translate("brackets.alreadyingame"));
					else plugin.getBrackets().start();
				}
			}
		}
		return false;
	}
}
