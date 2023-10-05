package komok.org.example.mazetour;

import komok.org.example.mazetour.commands.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
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
        getCommand("candywars").setExecutor(new candyWarCommand());
        getCommand("candywars").setTabCompleter(new candyWarCompliter());
        getCommand("world").setExecutor(new worldCommand());
        getServer().getPluginManager().registerEvents(this, this);
        System.out.println(ChatColor.RED + "MazeTour launched!");
    }

    @Override
    public void onDisable() {
        System.out.println("MazeTour stopped!");
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
        if (candyWarCommand.isRun()) {event.setCancelled(true);}
    }

    @EventHandler
    public void entityDamageEvent(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity && !(entity instanceof Player)) {
            LivingEntity livingEntity = (LivingEntity) entity;
            if (event.getDamage() > livingEntity.getHealth()) {
                Firework firework = (Firework) entity.getWorld().spawn(entity.getLocation(), Firework.class);
                FireworkMeta fireworkMeta = firework.getFireworkMeta();
                fireworkMeta.addEffect(FireworkEffect.builder()
                        .flicker(false)
                        .trail(true)
                        .with(FireworkEffect.Type.BALL)
                        .with(FireworkEffect.Type.BALL_LARGE)
                        .with(FireworkEffect.Type.STAR)
                        .withColor(Color.YELLOW)
                        .withColor(Color.ORANGE)
                        .withFade(Color.RED)
                        .withFade(Color.PURPLE)
                        .build());
                fireworkMeta.setPower(0);
                firework.setFireworkMeta(fireworkMeta);
                firework.setLife(0);
                livingEntity.remove();
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Bukkit.broadcastMessage(ChatColor.RED + "Get entity");
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (event.getDamage() < player.getHealth()) {return;}
//            if (candyWarCommand.isRun()) {player.teleport(new Location(entity.getWorld(), 9000, 90, 0));}
            Player taker = (Player) event.getEntity();
            Player shooter = (Player) event.getDamager();
            onPlayerDied(taker, shooter);
//            int damageTaskId = Bukkit.getScheduler().runTaskTimer(MazeTour.getInstance(), new Runnable() {
//                int time = 10;
//                @Override
//                public void run() {
//                    if (time == 0) {
//                        player.setGameMode(GameMode.ADVENTURE);
//                        Bukkit.getScheduler().cancelTask(taskId);
//                    }
//                    time--;
//                }
//            }, 0,20L).getTaskId();
        }
    }

    @EventHandler
    public void onFireBallHit(ProjectileHitEvent event) {
        Fireball fire = (Fireball) event.getEntity();
        Entity entity = event.getEntity();
        LivingEntity livingEntity = null;
        if (entity instanceof LivingEntity) {
            livingEntity = (LivingEntity) entity;
    }
        Player taker = (Player) event.getHitEntity();
        Player shooter = (Player) fire.getShooter();
        if (livingEntity != null && !(livingEntity instanceof Player)) {livingEntity.setHealth(0);}
        onPlayerDied(taker, shooter);
    }

    public void onPlayerDied(Player taker, Player shooter) {
        Bukkit.broadcastMessage(ChatColor.RED + "OnPlayerDied");
        if (taker == null) {return;}
        if (candyWarCommand.isRun()) {
            Location location = new Location(getWorld("world"), 3000, 90, 0);
            taker.teleport(location);
            if (shooter == null) {
                taker.sendTitle(ChatColor.RED + "СМЕРТЬ", "");
                taker.sendMessage( ChatColor.RED + "Вы погибли");
                taker.playSound(taker.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 1);
                return;
            }
            else if (shooter == taker) {
                taker.sendTitle(ChatColor.RED + "СМЕРТЬ", "");
                taker.sendMessage( ChatColor.RED + "Вы убили самого себя!");
                taker.playSound(taker.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 1);
                return;
            }
            else {
                shooter.sendMessage(ChatColor.RED + "Вы убили " + ChatColor.RED + taker.getName());
                shooter.playSound(taker.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1, 1);
                taker.sendTitle(ChatColor.RED + "СМЕРТЬ", "");
                taker.sendMessage(ChatColor.RED + "Вы были убиты " + ChatColor.RED + shooter.getName());
                taker.playSound(taker.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 1);
                changeCandies(shooter.getInventory(), 1);
                changeCandies(taker.getInventory(), -1);
            }
        }
    }

    public void changeCandies(Inventory inventory, int amount) {
        ItemStack candies = inventory.getItem(4);
        candies.setAmount(candies.getAmount() + amount);
        inventory.setItem(4, candies);
    }

    @EventHandler
    public void onPlayerMessage(PlayerChatEvent event) {
        if (candyWarCommand.isRun()) {event.setCancelled(true); event.getPlayer().sendMessage(ChatColor.RED + "Во время испытания чат отключен!");}
        else {
            event.setFormat("%s: %s");
        }
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
    public void entityDeathEvent(EntityDeathEvent event) {
        event.setDroppedExp(0);
        for (Object itemStack: event.getDrops().toArray()) {
            event.getDrops().remove(itemStack);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e)
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

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    public static World getWorld(String name) {
        return ((World) Bukkit.getServer().getWorld(name));
    }
}
