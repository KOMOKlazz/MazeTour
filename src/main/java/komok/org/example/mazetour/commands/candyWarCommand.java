package komok.org.example.mazetour.commands;

import komok.org.example.mazetour.MazeTour;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.*;

public class candyWarCommand implements CommandExecutor {
    private Plugin plugin = MazeTour.getInstance();
    private int taskId;
    public static boolean run;

    public static boolean isRun() {
        return run;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Выбери действие!");
            return false;
        }
        switch (args[0]) {
            case "start":
                if (run) {sender.sendMessage(ChatColor.RED + "Испытание уже начато!"); break;}
                run = true;
                World world = (World) Bukkit.getServer().getWorld("world");
                Bukkit.broadcastMessage(ChatColor.RED + "Конфетные войны " + ChatColor.YELLOW + "через 30 секунд!");
                // Время
                taskId = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                    int time = 0;

                    @Override
                    public void run() {
                        if (run == false) {
                            cancelTask(taskId);
                        }
                        for (Player player: Bukkit.getOnlinePlayers()) {
                            player.setLevel(1000 - time);
                        }
                        switch (time) {
    //                    case 20: - Вставлю потом
    //                        Bukkit.broadcastMessage(ChatColor.RED + "Конфетные войны " + ChatColor.YELLOW + "через 10 секунд!");
    //                        break;
                            case 0: //30
                                Location location = new Location(world, 3000, 65, 0);
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    player.teleport(location);
                                    player.setGameMode(GameMode.ADVENTURE);
                                    player.setHealth(20);
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
                                    Location chestLocation = new Location(world, -100, 300, 0);
                                    Chest chest = (Chest) chestLocation.getBlock().getState();
                                    ItemStack brush = chest.getInventory().getItem(0);
                                    ItemStack feather = chest.getInventory().getItem(1);
                                    ItemStack candies = chest.getInventory().getItem(4);
                                    Inventory takerInventory = player.getInventory();
                                    takerInventory.setItem(0, brush);
                                    takerInventory.setItem(1, feather);
                                    takerInventory.setItem(4, candies);
                                    player.sendTitle(ChatColor.RED + "Старт", "");
                                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                                    player.playSound(player.getLocation(), Sound.ENTITY_WITCH_CELEBRATE, 1, 1);
                                    run = true;
                                    break;
                                }
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
    private void cancelTask(int taskId) {
        Bukkit.getScheduler().cancelTask(this.taskId);
    }
}
