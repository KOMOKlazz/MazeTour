package komok.org.example.mazetour.events;

import komok.org.example.mazetour.MazeTour;
import komok.org.example.mazetour.commands.candyWarStartCommand;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class candyWarEvents implements Listener {
    private int taskId;
//    @EventHandler(priority= EventPriority.HIGH)
//    public void onPlayerUse(PlayerInteractEvent event){
//        if (!candyWarStartCommand.isRun()) {return;}
//        Player p = event.getPlayer();
//
//        if(event.getAction().equals(Action.RIGHT_CLICK_AIR)){
//            if(p.getItemInHand().getType() == Material.BRUSH){
//                Fireball fire = p.getWorld().spawn(event.getPlayer().getLocation().add(new Vector(0.0D, 1.0D, 0.0D)), Fireball.class);
//                fire.setFireTicks(0);
//                fire.setShooter(p);
//                fire.setSilent(true);
//                fire.getLastDamageCause().setDamage(100);
//
//                for (Player player: Bukkit.getOnlinePlayers()) {
//                    player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
//                }
//            }
//            else if(p.getItemInHand().getType() == Material.BLAZE_ROD){
//                Fireball fire = p.getWorld().spawn(event.getPlayer().getLocation().add(new Vector(0.0D, 1.0D, 0.0D)), Fireball.class);
//                fire.setFireTicks(0);
//                fire.setShooter(p);
//                fire.setSilent(true);
//                fire.getLastDamageCause().setDamage(100);
//
//                Entity pig = p.getWorld().spawnEntity(p.getLocation(), EntityType.PIG);
//                taskId = Bukkit.getScheduler().runTaskTimer(MazeTour.getInstance(), new Runnable() {
//                    @Override
//                    public void run() {
//                        pig.teleport(fire.getLocation());
//                    }
//                }, 0L,1L).getTaskId();
//            }
//        }
//    }
}
