package komok.org.example.mazetour;

import komok.org.example.mazetour.commands.*;
import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class MazeTour extends JavaPlugin implements Listener {
    private static MazeTour instance;
    public MazeTour() {
        instance = this;
    }
    public static Plugin getInstance() {
        return instance;
    }
    @Override
    public void onEnable() {
        getCommand("boatrace").setExecutor(new boatRaceStartCommand());
        getCommand("candywar").setExecutor(new candyWarStartCommand());
        getCommand("world").setExecutor(new worldCommand());
        getServer().getPluginManager().registerEvents(this, this);
        System.out.println("MazeTour был запущен!");
    }

    @Override
    public void onDisable() {
        System.out.println("MazeTour был остановлен!");
    }
}
