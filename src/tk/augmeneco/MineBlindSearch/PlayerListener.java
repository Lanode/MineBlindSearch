package tk.augmeneco.MineBlindSearch;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Score;

import java.io.IOException;

public final class PlayerListener implements Listener {

    public PlayerListener() {
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (Observation.observationInProgress && (event.getPlayer().getUniqueId() == Observation.playerUniqueId)) {
            Location to = event.getTo();
            try {
                Observation.out.write(String.format("Move: %f %f %f\n", to.getX(), to.getY(), to.getZ()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (Observation.observationInProgress && (event.getPlayer().getUniqueId() == Observation.playerUniqueId)) {
            Block b = event.getBlock();
            if (b.getType() == Material.WOOL) {
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);

                try {
                    Observation.out.write(String.format("BlockCollected: %d %d %d\n", b.getX(), b.getY(), b.getZ()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                event.getPlayer().getScoreboard().getObjective("collecting").getScore(ChatColor.GREEN + "Blocks:");
                Score score = event.getPlayer().getScoreboard().getObjective("collecting").getScore(ChatColor.GREEN + "Blocks:");
                score.setScore(score.getScore()+1);

                event.getPlayer().sendMessage("You collected a point");
            } else {
                //event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onFoodLevelChange (FoodLevelChangeEvent event) {
        if (event.getEntityType () != EntityType.PLAYER) return;
        event.setCancelled (true);
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onFoodLevelChange (EntityDamageEvent event) {
        if (event.getEntityType () != EntityType.PLAYER) return;
        event.setCancelled (true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (event.getPlayer().getUniqueId() == Observation.playerUniqueId) {
            Observation.stop();
        }
    }
}
