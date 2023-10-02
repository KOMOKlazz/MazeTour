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

public class candyWarStartCommand implements CommandExecutor {
    private Plugin plugin = MazeTour.getInstance();
    private int taskId;
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Bukkit.broadcastMessage(ChatColor.RED + "Конфетные войны" + ChatColor.YELLOW + "начнутся через 30 секунд!");
        taskId = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            int time = 0;
            @Override
            public void run() {
                switch (time) {
                    case 20:
                        Bukkit.broadcastMessage(ChatColor.RED + "Конфетные войны " + ChatColor.YELLOW + "начнутся через 10 секунд!");
                        break;
                    case 0:
                        World world = (World) Bukkit.getServer().getWorld("world");
                        Location location =  new Location(world, 1500, 63, 0);
                        for (Player player: Bukkit.getOnlinePlayers()) {
                            player.teleport(location);
                        }
                        cancelTask(taskId);
                        game();
                        break;
                }
                time++;
            }
        }, 0, 20L).getTaskId();
        return false;
    }

    private void game() {
        taskId = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            int time = 0;
            @Override
            public void run() {
                switch (time) {
                    case 20:
                        Bukkit.broadcastMessage(ChatColor.RED + "Конфетные войны" + ChatColor.YELLOW + "начнутся через 10 секунд!");
                        break;
                    case 0:
                        World world = (World) Bukkit.getServer().getWorld("world");
                        Location location =  new Location(world, 1500, 63, 0);
                        for (Player player: Bukkit.getOnlinePlayers()) {
                            player.teleport(location);
                        }
                        cancelTask(taskId);
                        break;
                }
                time++;
            }
        }, 0, 20L).getTaskId();
    }

    private void cancelTask(int taskId) {
        Bukkit.getScheduler().cancelTask(this.taskId);
    }
}
