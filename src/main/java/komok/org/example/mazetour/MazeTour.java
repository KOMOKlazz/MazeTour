package komok.org.example.mazetour;

import komok.org.example.mazetour.commands.*;
import komok.org.example.mazetour.listiners.Listeners;
import komok.org.example.mazetour.utils.BoatRaceFunctions;
import org.bukkit.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class MazeTour extends JavaPlugin implements Listener {
    private static MazeTour instance;
    private static BoatRaceFunctions boatRaceFunctions;

    public MazeTour() {
        instance = this;
    }

    public static Plugin getInstance() {
        return instance;
    }
    private Database database;
    @Override
    public void onEnable() {
        try {
            this.database = new Database();
            database.initializeDatabase();
            getServer().getConsoleSender().sendMessage("[MazeTour] " + ChatColor.GREEN + "Successfully connected to the database");
        } catch (SQLException e) {
            getServer().getConsoleSender().sendMessage("[MazeTour] " + ChatColor.RED + "Unable to connect to the database or create the tables!");
            throw new RuntimeException(e);
        }

        getCommand("boatrace").setExecutor(new boatRaceCommand());
        getCommand("candywars").setExecutor(new candyWarCommand());
        getCommand("candywars").setTabCompleter(new candyWarCompliter());
        getCommand("world").setExecutor(new worldCommand());
        getServer().getPluginManager().registerEvents(new Listeners(), this);

        getServer().getConsoleSender().sendMessage("[MazeTour] " + ChatColor.GREEN + "Launched");
    }
    @Override
    public void onDisable() {
        System.out.println("MazeTour stopped!");
    }

    public Database getDatabase() {
        return database;
    }
    public static BoatRaceFunctions getBoatRaceFunctions() {return boatRaceFunctions;}
}