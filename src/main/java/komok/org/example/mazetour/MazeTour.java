package komok.org.example.mazetour;

import komok.org.example.mazetour.commands.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

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
        getCommand("start").setExecutor(new boatRaceStartCommand());
        getCommand("world").setExecutor(new worldCommand());
        getServer().getPluginManager().registerEvents(this, this);
        System.out.println("MazeTour был запущен!");
    }

    @Override
    public void onDisable() {
        System.out.println("MazeTour был остановлен!");
    }
}
