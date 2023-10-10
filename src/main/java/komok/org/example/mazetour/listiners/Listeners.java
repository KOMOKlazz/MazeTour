package komok.org.example.mazetour.listiners;

import komok.org.example.mazetour.MazeTour;
import komok.org.example.mazetour.commands.boatRaceCommand;
import komok.org.example.mazetour.commands.candyWarCommand;
import komok.org.example.mazetour.models.CandyWarsStats;
import komok.org.example.mazetour.utils.BoatRaceFunctions;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class Listeners implements Listener {
    private int taskId;
    private int damageTaskId;
    private Plugin plugin = MazeTour.getInstance();
    private static BoatRaceFunctions boatRaceFunctions = MazeTour.getBoatRaceFunctions();
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = (Player) event.getPlayer();

        if (candyWarCommand.isRun()) {
            player.setGameMode(GameMode.SPECTATOR);
            Location location = new Location(plugin.getServer().getWorld("world"), 3000, 80, 0);
            player.teleport(location);
        } else {
            player.setGameMode(GameMode.ADVENTURE);
            Location location = new Location(plugin.getServer().getWorld("world"), 8.5, -60, 8.5);
            location.setYaw(-180);
            player.teleport(location);
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
            Block block = player.getWorld().getBlockAt(player.getLocation().subtract(0, 2, 0));
            if (!block.getType().equals(Material.AIR)) {
                Vector vector = player.getLocation().getDirection().multiply(1.5).setY(1);
                player.setVelocity(vector);
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 1);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerUse(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.PHYSICAL)) {
            if (event.getClickedBlock().getType().equals(Material.FARMLAND)) {
                event.setCancelled(true);
            }
        }
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
                            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 1);
                        }
                    }
                } else if (player.getItemInHand().getType() == Material.BRUSH) {
                    Fireball fire = player.getWorld().spawn(event.getPlayer().getLocation().add(new Vector(0.0D, 1.0D, 0.0D)), Fireball.class);
                    fire.setFireTicks(0);
                    fire.setShooter(player);
                    fire.setSilent(true);

                    Entity pig = player.getWorld().spawnEntity(player.getLocation(), EntityType.PIG);
                    for (Player player_ : Bukkit.getOnlinePlayers()) {
                        player_.hideEntity(plugin, fire);
                        player_.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
                    }
                    taskId = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
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
                    boatRaceFunctions.teleportToStart(player);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (candyWarCommand.isRun()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void entityDamageEvent(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity && !(entity instanceof Player)) {
            LivingEntity livingEntity = (LivingEntity) entity;
            if (event.getDamage() > livingEntity.getHealth()) {
//                Firework firework = (Firework) entity.getWorld().spawn(entity.getLocation(), Firework.class);
//                FireworkMeta fireworkMeta = firework.getFireworkMeta();
//                fireworkMeta.addEffect(FireworkEffect.builder()
//                        .flicker(false)
//                        .trail(true)
//                        .with(FireworkEffect.Type.BALL)
//                        .with(FireworkEffect.Type.BALL_LARGE)
//                        .with(FireworkEffect.Type.STAR)
//                        .withColor(Color.YELLOW)
//                        .withColor(Color.ORANGE)
//                        .withFade(Color.RED)
//                        .withFade(Color.PURPLE)
//                        .build());
//                fireworkMeta.setPower(0);
//                firework.setFireworkMeta(fireworkMeta);
//                firework.setLife(0);
                livingEntity.remove();
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            Player taker = null;
            Player shooter = null;
            try {
                taker = (Player) event.getEntity();
                Fireball fireball = (Fireball) event.getDamager();
                shooter = (Player) fireball.getShooter();
            } catch (Exception exception) {System.out.println("damageByEntityProblem");}
            onPlayerDied(taker, shooter);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
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
        if (livingEntity != null && !(livingEntity instanceof Player)) {
            livingEntity.setHealth(0);
        }
        if (entity instanceof Player) {
            onPlayerDied(taker, shooter);
        }
    }

    public void onPlayerDied(Player taker, Player shooter) {
        if (taker == null) {
            return;
        }
        if (candyWarCommand.isRun()) {
            Location location = new Location(plugin.getServer().getWorld("world"), 3000, 90, 0);
            taker.teleport(location);
            taker.setGameMode(GameMode.SPECTATOR);
            if (shooter == null) {
                taker.sendTitle(ChatColor.RED + "СМЕРТЬ", "");
                taker.sendMessage(ChatColor.RED + "Вы погибли");
                taker.playSound(taker.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);
            } else if (shooter == taker) {
                taker.sendTitle(ChatColor.RED + "СМЕРТЬ", "");
                taker.sendMessage(ChatColor.RED + "Вы убили самого себя!");
                taker.playSound(taker.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);
            } else {
                shooter.sendMessage(ChatColor.RED + "Вы убили " + ChatColor.RED + taker.getName());
                shooter.playSound(taker.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1, 1);
                taker.sendTitle(ChatColor.RED + "СМЕРТЬ", "");
                taker.sendMessage(ChatColor.RED + "Вы были убиты " + ChatColor.RED + shooter.getName());
                taker.playSound(taker.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);
                changeCandies(shooter.getInventory(), 1);
                changeCandies(taker.getInventory(), -1);
            }
            damageTaskId = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                int time = 10;

                @Override
                public void run() {
                    if (time == 0) {
                        Location location = new Location(plugin.getServer().getWorld("world"), 3000, 65, 0);
                        taker.teleport(location);
                        taker.setGameMode(GameMode.ADVENTURE);
                        taker.setHealth(20);
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
        if (!boatRaceCommand.isRun() || player.isOp()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void playerBreakBoat(VehicleDamageEvent event) {
        Player player = (Player) event.getAttacker();
        if (!boatRaceCommand.isRun() || player.isOp()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void playerJoinBoat(VehicleEnterEvent event) {
        if (!boatRaceCommand.isRun()) {
            return;
        }
        Boat boat = (Boat) event.getVehicle();
        if (boat.getPassengers().size() == 1) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void boatEntityCollision(VehicleEntityCollisionEvent event) {
        if (boatRaceCommand.isRun()) {
            Boat boat;
            Player player;
            try {
                boat = (Boat) event.getVehicle();
                player = (Player) boat.getPassengers().get(0);
            } catch (Exception exception) {
                return;
            }
            if (!boatRaceCommand.isRun()) {
                return;
            }
            Entity entity = event.getEntity();
            if (entity.getType() == EntityType.MINECART_HOPPER) {
                boatRaceFunctions.teleportToLocation(player, -368, 160, 159);
            }
            if (entity.getType() == EntityType.MINECART) {
                boatRaceFunctions.teleportToLocation(player, -688, 145, 156);
            }
        }
    }

    //BOAT RACE ←
    @EventHandler
    public void PlayerDropItem(PlayerDropItemEvent event) {
        if (candyWarCommand.isRun() || event.getItemDrop().getItemStack().getType() == Material.BRUSH ||
                event.getItemDrop().getItemStack().getType() == Material.PLAYER_HEAD) {
            event.setCancelled(true);
        }
        if (boatRaceCommand.isRun() || event.getItemDrop().getItemStack().getType() == Material.RED_WOOL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void blockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!player.isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMessage(PlayerChatEvent event) {
        if (candyWarCommand.isRun()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Во время испытания чат отключен!");
        } else {
            event.setFormat("%s: %s");
        }
    }

    @EventHandler
    public void entityDeathEvent(EntityDeathEvent event) {
        event.setDroppedExp(0);
        for (Object itemStack : event.getDrops().toArray()) {
            event.getDrops().remove(itemStack);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        e.setDeathMessage(null);
    }

    @EventHandler
    public void onFall(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (event.getEntityType() == EntityType.PLAYER) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }
    @EventHandler
    public void onIceMelt(BlockFadeEvent event) {
        Block affectedBlock = event.getBlock();
        if (affectedBlock.getType() == Material.ICE) {
            event.setCancelled(true);
        }
    }

    private void cancelTask(int taskId) {
        Bukkit.getScheduler().cancelTask(this.taskId);
    }
}
