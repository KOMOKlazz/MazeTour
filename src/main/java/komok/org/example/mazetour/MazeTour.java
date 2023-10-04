package komok.org.example.mazetour;

import komok.org.example.mazetour.commands.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
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
        getCommand("boatrace").setExecutor(new boatRaceCommand());
        getCommand("candywar").setExecutor(new candyWarCommand());
        getCommand("canfywar").setTabCompleter(new candyWarCompliter());
        getCommand("world").setExecutor(new worldCommand());
        getServer().getPluginManager().registerEvents(this, this);
        System.out.println("MazeTour был запущен!");
    }

    @Override
    public void onDisable() {
        System.out.println("MazeTour был остановлен!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = (Player) event.getPlayer();
        if (candyWarCommand.isRun()) {
            player.setGameMode(GameMode.SPECTATOR);
            Location location = new Location(getWorld("world"), 3000, 80, 0);
            player.teleport(location);
        }
        else {
            player.setGameMode(GameMode.ADVENTURE);
            player.sendMessage(ChatColor.GOLD + "Добро пожаловать на хеллоуинский турнир 2023!");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        }
        event.setJoinMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    //CANDY WAR →

    @EventHandler
    public void onPlayerDoubleJump(PlayerToggleFlightEvent event) {
        Player player = (Player) event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            Block block = player.getWorld().getBlockAt(player.getLocation().subtract(0, 2, 0));
            if (!block.getType().equals(Material.AIR)) {
                Vector vector = player.getLocation().getDirection().multiply(1.5).setY(1);
                player.setVelocity(vector);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerUse(PlayerInteractEvent event){
        if (!candyWarCommand.isRun()) {return;}
        Player player = event.getPlayer();

        if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            if(player.getItemInHand().getType() == Material.FEATHER){
                if (player.getGameMode() != GameMode.CREATIVE) {
                    event.setCancelled(true);
                    Block block = player.getWorld().getBlockAt(player.getLocation().subtract(0, 2, 0));
                    if (!block.getType().equals(Material.AIR)) {
                        Vector vector = player.getLocation().getDirection().multiply(1.5).setY(1);
                        player.setVelocity(vector);
                    }
                }
            }
            else if(player.getItemInHand().getType() == Material.BRUSH){
                Fireball fire = player.getWorld().spawn(event.getPlayer().getLocation().add(new Vector(0.0D, 1.0D, 0.0D)), Fireball.class);
                fire.setFireTicks(0);
                fire.setShooter(player);
                fire.setSilent(true);

                Entity pig = player.getWorld().spawnEntity(player.getLocation(), EntityType.PIG);
                for (Player player_: Bukkit.getOnlinePlayers()) {
                    player_.hideEntity(getInstance(), fire);
                    player_.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
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

    @EventHandler (priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
//        if (event.getCurrentItem().getType() == )
        event.setCancelled(true);
    }

    @EventHandler
    public void entityDeathEvent(EntityDeathEvent event) {
        Bukkit.broadcastMessage(ChatColor.YELLOW + "ENTITY DEATH");
        event.setDroppedExp(0);
        for (Object itemStack: event.getDrops().toArray()) {
            event.getDrops().remove(itemStack);
        }
        Entity entity = event.getEntity();
        if(entity.getLastDamageCause().getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
            if (entity instanceof Player) {
                Player taker = event.getEntity().getKiller();
                Player shooter = event.getEntity().getKiller();
                onPlayerExploded(taker, shooter);
                return;
            }
        }
//        if (entity.getHeight() - e.getDamage() < 0) {
//            p.teleport(new Location(e.getEntity().getWorld(), 0.0, -400.0, 0.0));
//        }
        return;
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        Bukkit.broadcastMessage(ChatColor.YELLOW + "FIREBALL HIT");
        Fireball fire = (Fireball) event.getEntity();
        LivingEntity entity = (LivingEntity) event.getHitEntity();
        Player taker = (Player) event.getHitEntity();
        Player shooter = (Player) fire.getShooter();
        if (entity != null) {entity.setHealth(0);}
        if (taker == null) {return;}
        onPlayerExploded(taker, shooter);
    }

    public void onPlayerExploded(Player taker, Player shooter) {
        if (shooter == taker) {
            taker.sendTitle(ChatColor.RED + "СМЕРТЬ", "");
            taker.sendMessage( ChatColor.RED + "Вы убили самого себя!");
            taker.playSound(taker.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 1);
            return;
        }
        shooter.sendMessage( ChatColor.RED + "Вы убили " + ChatColor.RED + taker.getName());
        shooter.playSound(taker.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1, 1);
        taker.sendTitle(ChatColor.RED + "СМЕРТЬ", "");
        taker.sendMessage( ChatColor.RED + "Вы были убиты " + ChatColor.RED + shooter.getName());
        taker.playSound(taker.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 1);
        changeCandies(shooter.getInventory(), 1);
        changeCandies(taker.getInventory(), -1);
    }

    public void changeCandies(Inventory inventory, int amount) {
        ItemStack candies = inventory.getItem(4);
        candies.setAmount(candies.getAmount() + amount);
        inventory.setItem(4, candies);
    }

    @EventHandler
    public void onPlayerMessage(PlayerChatEvent event) {
        event.setFormat("%s: %s");
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event){
        if(event.getItemDrop().getItemStack().getType() == Material.BRUSH ||
                event.getItemDrop().getItemStack().getType() == Material.PLAYER_HEAD) {
            event.setCancelled(true);
        }
    }

    //CANDY WAR ←

    @EventHandler
    public void onDeath(PlayerDeathEvent e)
    {
        e.setDeathMessage(null);
    }

    @EventHandler
    public void onFall(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if(event.getEntityType()==EntityType.PLAYER) {
                event.setCancelled(true);
            }
        }
    }

    public World getWorld(String name) {
        return ((World) Bukkit.getServer().getWorld(name));
    }
}
