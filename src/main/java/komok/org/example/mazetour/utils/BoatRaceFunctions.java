package komok.org.example.mazetour.utils;

import komok.org.example.mazetour.MazeTour;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class BoatRaceFunctions {
    private Plugin plugin = MazeTour.getInstance();
    public void teleportToLocation(Player player, double x, double y, double z) {
        //Телепорт
        Location boatRaceStartLocation = new Location(plugin.getServer().getWorld("mountains"), x, y, z);
        try {
            player.getVehicle().remove();
        } catch (Exception exception) {}
        player.teleport(boatRaceStartLocation);
        Boat boat = (Boat) player.getWorld().spawnEntity(player.getLocation(), EntityType.BOAT);
        boat.addPassenger(player);

        //Визуал
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
    }

    public void teleportToStart(Player player) {
        //Телепорт
        Location boatRaceStartLocation = new Location(plugin.getServer().getWorld("mountains"), -37, 150, 185);
        boatRaceStartLocation.setPitch(-180);
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
}
