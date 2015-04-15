package net.maunium.bukkit.Mauvents;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import net.maunium.bukkit.Maussentials.Utils.IngameCommandExecutor;
import net.maunium.bukkit.Maussentials.Utils.SerializableLocation;

public class CommandMauventsAdmin implements IngameCommandExecutor {
	private Mauvents plugin;
	
	public CommandMauventsAdmin(Mauvents plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(Player p, Command cmd, String label, String[] args) {
		if (args.length > 1) {
			if (args[0].equalsIgnoreCase("brackets")) {
				if (args[1].equalsIgnoreCase("start")) {
					if (plugin.getBrackets().hasStarted()) p.sendMessage(plugin.translateErr("brackets.alreadyingame"));
					else plugin.getBrackets().start();
				} else if (args[1].equalsIgnoreCase("setspawn") && args.length > 2) {
					p.sendMessage(plugin.translateErr("brackets.nyi.setspawn"));
				} else return false;
				return true;
			} else if (args[0].equalsIgnoreCase("lms")) {
				if (args[1].equalsIgnoreCase("start")) {
					if (plugin.getLMS().hasStarted()) p.sendMessage(plugin.translateErr("lms.alreadystarted"));
					else if (!plugin.getLMS().enoughPlayers()) p.sendMessage(plugin.translateErr("lms.toolittleplayers"));
					else if (!plugin.getLMS().isSetUp()) p.sendMessage(plugin.translateErr("lms.notsetup"));
					else {
						plugin.getLMS().start();
						p.sendMessage(plugin.translateStd("lms.started"));
					}
				} else if (args[1].equalsIgnoreCase("setspawn") && args.length > 2) {
					if (args[2].equalsIgnoreCase("arena")) {
						plugin.getLMS().setArena(p.getLocation());
						p.sendMessage(plugin.translateStd("lms.set.arena", new SerializableLocation(p.getLocation()).toReadableString()));
					} else if (args[2].equalsIgnoreCase("lobby")) {
						plugin.getLMS().setLobby(p.getLocation());
						p.sendMessage(plugin.translateStd("lms.set.lobby", new SerializableLocation(p.getLocation()).toReadableString()));
					} else p.sendMessage(plugin.translateErr("lms.set.notfound", args[2]));
				} else return false;
				return true;
			} else if (args[0].equalsIgnoreCase("lts")) {
				if (args[1].equalsIgnoreCase("start")) {
					if (plugin.getLTS().hasStarted()) p.sendMessage(plugin.translateErr("lts.alreadystarted"));
					else if (!plugin.getLTS().enoughPlayers()) p.sendMessage(plugin.translateErr("lts.toolittleplayers"));
					else if (!plugin.getLTS().isSetUp()) p.sendMessage(plugin.translateErr("lts.notsetup"));
					else {
						plugin.getLTS().start();
						p.sendMessage(plugin.translateStd("lts.started"));
					}
				} else if (args[1].equalsIgnoreCase("setspawn") && args.length > 2) {
					if (args[2].equalsIgnoreCase("team1")) {
						plugin.getLTS().setTeam1(p.getLocation());
						p.sendMessage(plugin.translateStd("lts.set.team1", new SerializableLocation(p.getLocation()).toReadableString()));
					} else if (args[2].equalsIgnoreCase("team2")) {
						plugin.getLTS().setTeam2(p.getLocation());
						p.sendMessage(plugin.translateStd("lts.set.team2", new SerializableLocation(p.getLocation()).toReadableString()));
					} else if (args[2].equalsIgnoreCase("lobby")) {
						plugin.getLTS().setLobby(p.getLocation());
						p.sendMessage(plugin.translateStd("lts.set.lobby", new SerializableLocation(p.getLocation()).toReadableString()));
					} else p.sendMessage(plugin.translateErr("lts.set.notfound", args[2]));
				} else return false;
				return true;
			} else return false;
		} else return false;
	}
}
