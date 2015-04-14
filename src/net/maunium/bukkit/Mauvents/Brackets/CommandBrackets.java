package net.maunium.bukkit.Mauvents.Brackets;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandBrackets implements CommandExecutor {
	private Brackets host;
	
	public CommandBrackets(Brackets host) {
		this.host = host;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		return true;
	}
}
