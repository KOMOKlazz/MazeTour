package komok.org.example.mazetour.commands;

import komok.org.example.mazetour.MazeTour;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class boatRaceCommand implements CommandExecutor {
    private Plugin plugin = MazeTour.getInstance();
    private int taskId;
    public static boolean run;

    public static boolean isRun() {return run;}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Выбери действие!");
            return false;
        }
        switch (args[0]) {
            case "start":
                if (run) {
                    sender.sendMessage(ChatColor.RED + "Испытание уже начато!");
                    break;
                }
                run = true;
                World world = (World) Bukkit.getServer().getWorld("world");
                Bukkit.broadcastMessage(ChatColor.YELLOW + "Другое испытание начнется через 30 секунд!");
                taskId = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                    int time = 0;

                    @Override
                    public void run() {
                        switch (time) {
                                                //case 20:
                                                  //  Bukkit.broadcastMessage(ChatColor.YELLOW + "Перемещение на другое испытание через 10 секунд!");
                                                    //break;
                            case 0:
                                World world = (World) Bukkit.getServer().getWorld("world");
                                Location location = new Location(world, 1500, 63, 0);
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    player.teleport(location);
                                }
                                break;
                            case 17:
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    player.sendTitle(ChatColor.YELLOW + "3", "");
                                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                                }
                                break;
                            case 18:
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    player.sendTitle(ChatColor.YELLOW + "2", "");
                                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                                }
                                break;
                            case 19:
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    player.sendTitle(ChatColor.YELLOW + "1", "");
                                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                                }
                                break;
                            case 20:
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    Location boatRaceStartLocation = new Location(MazeTour.getWorld("world"), -3000, 100, 0);
                                    Boat boat = (Boat) player.getWorld().spawnEntity(player.getLocation(), EntityType.BOAT);
                                    boat.addPassenger(player);
                                    player.sendTitle(ChatColor.RED + "Старт", "");

                                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                                    player.playSound(player.getLocation(), Sound.ENTITY_WITCH_CELEBRATE, 1, 1);
                                    run = true;
                                }
                                break;
                        }
                        time++;
                    }
                }, 0, 20L).getTaskId();
        }
       World boatRaceWorld = Bukkit.getServer().getWorld("boat_race");
        Location boatRaceStartLocation = new Location(boatRaceWorld, 0, 100, 0);
                return false;

    }
}

