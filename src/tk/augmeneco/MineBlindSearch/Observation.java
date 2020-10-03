package tk.augmeneco.MineBlindSearch;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class Observation {
    public static boolean observationInProgress = false;
    public static UUID playerUniqueId;
    public static FileWriter out;

    public static boolean start(String playerName) {
        stop();

        Player player = Bukkit.getServer().getPlayer(playerName);
        if (player == null) {
            return false;
        }
        playerUniqueId = player.getUniqueId();

        Date now = new Date();
        try {
            out = new FileWriter(String.format("%s/observation-%s-%s.log",
                    Main.getPlugin(Main.class).getDataFolder(), player.getName(), now.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // set new scoreboard
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        final Scoreboard board = manager.getNewScoreboard();
        final Objective objective = board.registerNewObjective("collecting", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.RED + "Block collecting");
        Score score = objective.getScore(ChatColor.GREEN + "Blocks:");
        score.setScore(0);
        player.setScoreboard(board);

        Random random = new Random();

        int X = random.nextInt(4000000);
        int Z = random.nextInt(4000000);
        int Y;
        Location loc = new Location(player.getWorld(),
                random.nextInt(4000000),
                player.getWorld().getMaxHeight() - 1,
                random.nextInt(4000000));
        while (loc.getBlock().getType() == Material.AIR)
            loc.setY(loc.getY()-1);
        loc.setY(loc.getY()+1);

        player.teleport(loc);
        try {
            Observation.out.write(String.format("InitialPos: %f %f %f\n", loc.getX(), loc.getY(), loc.getZ()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        observationInProgress = true;

        Utils.broadcast(String.format("Observation of %s started", player.getName()));

        return true;
    }

    public static boolean stop() {
        if (Observation.observationInProgress) {
            Observation.observationInProgress = false;

            // Remove scoreboard
            Bukkit.getServer().getPlayer(Observation.playerUniqueId).setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

            Observation.playerUniqueId = null;

            try {
                Observation.out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Utils.broadcast("Observation stopped");

            return true;
        } else {
            return false;
        }
    }

}
