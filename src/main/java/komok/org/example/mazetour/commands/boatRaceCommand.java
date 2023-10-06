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
                World world = (World) Bukkit.getServer().getWorld("mountains");
                Bukkit.broadcastMessage(ChatColor.RED + "Лодочные гонки " + ChatColor.YELLOW + "через 30 секунд!");
                taskId = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                    int time = 0;

                    @Override
                    public void run() {
                        if (run == false) {
                            cancelTask(taskId);
                        }
                        switch (time) {
                            case 0:
                                World world = (World) Bukkit.getServer().getWorld("mountains");
                                Location location = new Location(world, -37, 145, 186);
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    player.teleport(location);
                                }
                                break;
                            case 10:
                                //Спавн лодок
                                int x = 1;
                                int z = 0;
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    Location boatRaceStartLocation = new Location(MazeTour.getWorld("mountains"), -40 + x, 145, 180 + z);
                                    player.teleport(boatRaceStartLocation);
                                    Boat boat = (Boat) player.getWorld().spawnEntity(player.getLocation(), EntityType.BOAT);
                                    boat.addPassenger(player);

                                    //Выдача предметов
                                    Location chestLocation = new Location(MazeTour.getWorld("mountains"), -100, 300, 0);
                                    Chest chest = (Chest) chestLocation.getBlock().getState();
                                    ItemStack redWool = chest.getInventory().getItem(4);
                                    Inventory takerInventory = player.getInventory();
                                    takerInventory.setItem(4, redWool);

                                    //Визуал
                                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                                    player.playSound(player.getLocation(), Sound.ITEM_AXE_SCRAPE, 1, 1);
                                    if (x < 5) {x += 2;}
                                    else {x = 0;}
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
                        }
                        time++;
                    }
                }, 0, 20L).getTaskId();
                break;
            case "stop":
                if (!run) {sender.sendMessage(ChatColor.RED + "Испытание и так не работает. Тебе нечего останавливать!"); break;}
                run = false;
                sender.sendMessage(ChatColor.RED + "Испытание остановлено");
                break;
        }

        return false;
    }
    public static void teleportToStart(Player player) {
        //Телепорт
        Location boatRaceStartLocation = new Location(MazeTour.getWorld("mountains"), -37, 150, 185);
        try {
            player.getVehicle().remove();
        } catch (Exception exception) {}
        player.teleport(boatRaceStartLocation);
        Boat boat = (Boat) player.getWorld().spawnEntity(player.getLocation(), EntityType.BOAT);
        boat.addPassenger(player);

        //Визуал
        player.sendMessage(ChatColor.RED + "Вы были перемещены на старт");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_EYE_LAUNCH, 1, 1);
    }
    private void cancelTask(int taskId) {
        Bukkit.getScheduler().cancelTask(this.taskId);
    }

    ///give KOMOKgg red_wool{display:{Name:'{"text":"На старт","color":"red","italic":"false"}'}}
}