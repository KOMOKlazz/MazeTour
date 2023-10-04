package komok.org.example.mazetour.commands;

import komok.org.example.mazetour.MazeTour;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class boatRaceCommand implements CommandExecutor {
    private Plugin plugin = MazeTour.getInstance();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Другое испытание начнется через 30 секунд!");
        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            int time = 0;
            @Override
            public void run() {
                switch (time) {
//                    case 20:
//                        Bukkit.broadcastMessage(ChatColor.YELLOW + "Перемещение на другое испытание через 10 секунд!");
//                        break;
                    case 0:
                        World world = (World) Bukkit.getServer().getWorld("world");
                        Location location =  new Location(world, 1500, 63, 0);
                        for (Player player: Bukkit.getOnlinePlayers()) {
                            player.teleport(location);
                        }
                        break;
                }
                time++;
            }
        }, 0, 20L);
//        World boatRaceWorld = Bukkit.getServer().getWorld("boat_race");
//        Location boatRaceStartLocation = new Location(boatRaceWorld, 0, 100, 0);
        return false;
    }
}
