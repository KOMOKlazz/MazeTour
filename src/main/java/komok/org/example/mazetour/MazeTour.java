package komok.org.example.mazetour;

import komok.org.example.mazetour.commands.*;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class MazeTour extends JavaPlugin implements Listener {
    private int taskId;
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

    @EventHandler(priority= EventPriority.HIGH)
    public void onPlayerUse(PlayerInteractEvent event){
        if (!candyWarStartCommand.isRun()) {return;}
        Player p = event.getPlayer();

        if(event.getAction().equals(Action.RIGHT_CLICK_AIR)){
            if(p.getItemInHand().getType() == Material.FEATHER){
//                p.setVelocity();
            }
            else if(p.getItemInHand().getType() == Material.BRUSH){
                Fireball fire = p.getWorld().spawn(event.getPlayer().getLocation().add(new Vector(0.0D, 1.0D, 0.0D)), Fireball.class);
                fire.setFireTicks(0);
                fire.setShooter(p);
                fire.setSilent(true);

                Entity pig = p.getWorld().spawnEntity(p.getLocation(), EntityType.PIG);
                for (Player player: Bukkit.getOnlinePlayers()) {
                    player.hideEntity(getInstance(), fire);
                    player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
                }
                taskId = Bukkit.getScheduler().runTaskTimer(MazeTour.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        pig.teleport(fire.getLocation());
                    }
                }, 0L,1L).getTaskId();
            }
        }
    }

    @EventHandler
    public void entityDeathEvent(EntityDeathEvent event) {
        event.setDroppedExp(0);
        for (ItemStack itemStack: event.getDrops()) {
            event.getDrops().remove(itemStack);
        }
        Entity entity = event.getEntity();
//        if (entity.getHeight() - e.getDamage() < 0) {
//            p.teleport(new Location(e.getEntity().getWorld(), 0.0, -400.0, 0.0));
//        }
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        Fireball fire = (Fireball) event.getEntity();
        Player taker = (Player) event.getHitEntity();
        Player shooter = (Player) fire.getShooter();
        if (taker == null) {return;}
        shooter.sendMessage( ChatColor.GRAY + "Вы убили " + ChatColor.RED + taker.getName());
        taker.setHealth(0);
        taker.sendTitle(ChatColor.RED + "Вас убили", "");
        taker.sendMessage( ChatColor.GRAY + "Вы были убиты " + ChatColor.RED + shooter.getName());
        taker.playSound(taker.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1, 1);
        Inventory takerInventory = taker.getInventory();
        for (ItemStack item: takerInventory.getContents()) {
            Bukkit.broadcastMessage(item.getTranslationKey());
        }
    }

    @EventHandler
    public void onPlayerMessage(PlayerChatEvent event) {
        Player player = (Player) event.getPlayer();
        World world = (World) Bukkit.getServer().getWorld("world");
        Location chestLocation = new Location(world, -100, 300, 0);
        Chest chest = (Chest) chestLocation.getBlock().getState();
        ItemStack brush = chest.getInventory().getItem(1);
        ItemStack candies = chest.getInventory().getItem(4);
        Inventory takerInventory = player.getInventory();
        takerInventory.setItem(1, brush);
        takerInventory.setItem(4, candies);
        event.setFormat("%s %s");
    }
}
