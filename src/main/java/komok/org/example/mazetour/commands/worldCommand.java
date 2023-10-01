package komok.org.example.mazetour.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class worldCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player player = (Player) sender;
        System.out.println(args[0]);
        Location location =  new Location(Bukkit.getWorld(args[0]), 0, 65, 0);
        player.teleport(location);
        return false;
    }
}
