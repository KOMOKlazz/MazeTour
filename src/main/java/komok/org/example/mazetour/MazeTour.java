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
import org.bukkit.event.vehicle.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class MazeTour extends JavaPlugin implements Listener {
    private int taskId;
    private int damageTaskId;
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
        if (candyWarCommand.isRun()) {
            Player player = event.getPlayer();

            if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if (player.getItemInHand().getType() == Material.FEATHER) {
                    if (player.getGameMode() != GameMode.CREATIVE) {
                        event.setCancelled(true);
                        Block block = player.getWorld().getBlockAt(player.getLocation().subtract(0, 2, 0));
                        if (!block.getType().equals(Material.AIR)) {
                            Vector vector = player.getLocation().getDirection().multiply(1.5).setY(1);
                            player.setVelocity(vector);
                        }
                    }
                } else if (player.getItemInHand().getType() == Material.BRUSH) {
                    Fireball fire = player.getWorld().spawn(event.getPlayer().getLocation().add(new Vector(0.0D, 1.0D, 0.0D)), Fireball.class);
                    fire.setFireTicks(0);
                    fire.setShooter(player);
                    fire.setSilent(true);

                    Entity pig = player.getWorld().spawnEntity(player.getLocation(), EntityType.PIG);
                    for (Player player_ : Bukkit.getOnlinePlayers()) {
                        player_.hideEntity(getInstance(), fire);
                        player_.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
                    }
                    taskId = Bukkit.getScheduler().runTaskTimer(MazeTour.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            pig.teleport(fire.getLocation());
                        }
                    }, 0L, 1L).getTaskId();
                }
            }
        }

        if (boatRaceCommand.isRun()) {
            Player player = event.getPlayer();

            if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if (player.getItemInHand().getType() == Material.RED_WOOL) {
                    event.setCancelled(true);
                    boatRaceCommand.teleportToStart(player);
                }
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
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (event.getDamage() < player.getHealth()) {return;}
            Player taker = (Player) event.getEntity();
            Player shooter = (Player) event.getDamager();
            onPlayerDied(taker, shooter);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onFireBallHit(ProjectileHitEvent event) {
        Entity entity = event.getEntity();
        LivingEntity livingEntity = null;
        if (entity instanceof LivingEntity) {
            livingEntity = (LivingEntity) entity;
    }
        Player taker = null;
        Player shooter = null;
        try {
            taker = (Player) event.getHitEntity();
        } catch (Exception exception) {
            taker = null;
        }
        try {
            Fireball fire = (Fireball) event.getEntity();
            shooter = (Player) fire.getShooter();
        } catch (Exception exception) {
            shooter = null;
        }
        if (livingEntity != null && !(livingEntity instanceof Player)) {livingEntity.setHealth(0);}
        onPlayerDied(taker, shooter);
    }

    public void onPlayerDied(Player taker, Player shooter) {
        if (taker == null) {return;}
        if (candyWarCommand.isRun()) {
            Location location = new Location(getWorld("world"), 3000, 90, 0);
            taker.teleport(location);
            taker.setGameMode(GameMode.SPECTATOR);
            if (shooter == null) {
                taker.sendTitle(ChatColor.RED + "СМЕРТЬ", "");
                taker.sendMessage( ChatColor.RED + "Вы погибли");
                taker.playSound(taker.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);
            }
            else if (shooter == taker) {
                taker.sendTitle(ChatColor.RED + "СМЕРТЬ", "");
                taker.sendMessage( ChatColor.RED + "Вы убили самого себя!");
                taker.playSound(taker.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);
            }
            else {
                shooter.sendMessage(ChatColor.RED + "Вы убили " + ChatColor.RED + taker.getName());
                shooter.playSound(taker.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1, 1);
                taker.sendTitle(ChatColor.RED + "СМЕРТЬ", "");
                taker.sendMessage(ChatColor.RED + "Вы были убиты " + ChatColor.RED + shooter.getName());
                taker.playSound(taker.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);
                changeCandies(shooter.getInventory(), 1);
                changeCandies(taker.getInventory(), -1);
            }
            damageTaskId = Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
                int time = 10;
                @Override
                public void run() {
                    if (time == 0) {
                        Location location = new Location(getWorld("world"), 3000, 65, 0);
                        taker.teleport(location);
                        taker.setGameMode(GameMode.ADVENTURE);
                        taker.playSound(taker.getLocation(), Sound.ENTITY_WITCH_CELEBRATE, 1, 1);
                        cancelTask(damageTaskId);
                    }
                    switch (time) {
                        case 9:
                            taker.sendMessage(ChatColor.YELLOW + "Возрождение через 10 секунд!");
                            break;
                        case 3:
                            taker.sendMessage(ChatColor.YELLOW + "Возрождение через 3 секунды!");
                            taker.playSound(taker.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 1);
                            break;
                        case 2:
                            taker.sendMessage(ChatColor.YELLOW + "Возрождение через 2 секунды!");
                            taker.playSound(taker.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 1);
                            break;
                        case 1:
                            taker.sendMessage(ChatColor.YELLOW + "Возрождение через 1 секунду!");
                            taker.playSound(taker.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 1);
                            taker.playSound(taker.getLocation(), Sound.ENTITY_ALLAY_AMBIENT_WITH_ITEM, 1, 1);
                            break;
                    }
                    time--;
                }
            }, 0, 20L).getTaskId();
        }
    }

    public void changeCandies(Inventory inventory, int amount) {
        ItemStack candies = inventory.getItem(4);
        candies.setAmount(candies.getAmount() + amount);
        inventory.setItem(4, candies);
    }
    //CANDY WAR ←

    //BOAT RACE →
    @EventHandler
    public void playerLeaveBoat(VehicleExitEvent event) {
        Player player = (Player) event.getExited();
        if (!boatRaceCommand.isRun() || player.isOp()) {return;}
        event.setCancelled(true);
    }
    @EventHandler
    public void playerBreakBoat(VehicleDamageEvent event) {
        Player player = (Player) event.getAttacker();
        if (!boatRaceCommand.isRun() || player.isOp()) {return;}
        event.setCancelled(true);
    }
    @EventHandler
    public void playerJoinBoat(VehicleEnterEvent event) {
        if (!boatRaceCommand.isRun()) {return;}
        Boat boat = (Boat) event.getVehicle();
        if (boat.getPassengers().size() == 1) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void playerCollisionBoat(VehicleEntityCollisionEvent event) {
        Bukkit.broadcastMessage("рандомVVVVV");
        Player player = (Player) event.getVehicle().getPassengers().get(0);
        player.sendMessage("рандом");
        if (!boatRaceCommand.isRun()) {return;}
//        Block block = event.getBlock();
        Entity entity = event.getEntity();
//        if (block.getType() == Material.BLACK_CONCRETE) {
        if (entity.getType() == EntityType.MINECART) {
            player.sendMessage("конкрете");
            Location location = new Location(MazeTour.getWorld("mountains"), -368, 160, 159);
            player.teleport(location);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
        }
    }

    @EventHandler
    public void playerColissionBlock(VehicleBlockCollisionEvent event) {
        Bukkit.broadcastMessage("оооооо");
        Block block = event.getBlock();
        if (block.getType() == Material.BLACK_CONCRETE) {
            Bukkit.broadcastMessage("fghjkl");

        }
    }
    //BOAT RACE ←
    @EventHandler
    public void PlayerDropItem(PlayerDropItemEvent event) {
        if(candyWarCommand.isRun() || event.getItemDrop().getItemStack().getType() == Material.BRUSH ||
                event.getItemDrop().getItemStack().getType() == Material.PLAYER_HEAD) {
            event.setCancelled(true);
        }
        if(boatRaceCommand.isRun() || event.getItemDrop().getItemStack().getType() == Material.RED_WOOL) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerMessage(PlayerChatEvent event) {
        if (candyWarCommand.isRun()) {event.setCancelled(true); onPlayerDied(event.getPlayer(), event.getPlayer()); event.getPlayer().sendMessage(ChatColor.RED + "Во время испытания чат отключен!");}
        else {
            event.setFormat("%s: %s");
        }
    }
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

    private void cancelTask(int taskId) {
        Bukkit.getScheduler().cancelTask(this.taskId);
    }
}
