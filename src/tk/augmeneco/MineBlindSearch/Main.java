package tk.augmeneco.MineBlindSearch;

import jdk.jshell.execution.Util;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Main extends JavaPlugin {
    String help = "/ob start <player> - Start observation of player\n" +
                  "/ob stop - Stop previous observation\n"+
                  "/ob regen - Regenerate world\n"+
                  "/ob reload - Reload config";

    PlayerListener playerListener;
    WorldListener worldListener;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        File f = new File(this.getDataFolder() + "/");
        if(!f.exists())
            f.mkdir();

        playerListener = new PlayerListener();
        worldListener = new WorldListener();

        getServer().getPluginManager().registerEvents(playerListener, this);
        getServer().getPluginManager().registerEvents(worldListener, this);

        this.getCommand("ob").setExecutor(CommandObserver);
    }

    @Override
    public void onDisable(){

    }

    public CommandExecutor CommandObserver = new CommandExecutor() {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length < 1) {
                sender.sendMessage("/ob help");
                return true;
            }

            if (args[0].equals("help")) {
                sender.sendMessage(help);
                return true;
            } else if (args[0].equalsIgnoreCase("start")) {
                if (args.length < 2) {
                    sender.sendMessage("Not enough arguments");
                    return true;
                }

                if (!Observation.start(args[1])) {
                    sender.sendMessage("No such player on server");
                    return true;
                }

            } else if (args[0].equalsIgnoreCase("stop")) {
                if (!Observation.stop()) {
                    sender.sendMessage("No observation in progress");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("regen")) {
                String worldName = getConfig().getString("observation-world");

                // kick player for unload
                for (Player player : getServer().getOnlinePlayers()) {
                    player.kickPlayer("World regeneration...\nPlease wait a few seconds");
                }

                // unload all chunks
                for (Chunk chunk : getServer().getWorld(worldName).getLoadedChunks()) {
                    if (!chunk.unload(false)) {
                        Utils.broadcastToOps(String.format("Can't unload chunk: %d %d", chunk.getX(), chunk.getZ()));
                        System.out.println(String.format("Can't unload chunk: %d %d", chunk.getX(), chunk.getZ()));
                        throw new RuntimeException(String.format("Can't unload chunk: %d %d", chunk.getX(), chunk.getZ()));
                    }
                }

                // delete region files
                File regionFolder = new File(worldName+"/region");
                for (String filename : regionFolder.list()) {
                    File f = new File(worldName+"/region/"+filename);
                    if (!f.delete()) {
                        //sender.sendMessage("Can't delete region file: "+filename);
                        Utils.broadcastToOps("Can't delete region file: "+filename);
                        System.out.println("Can't delete region file: "+filename);
                        throw new RuntimeException("Can't delete region file: "+filename);
                    }
                }

                //Bukkit.spigot().restart();
            } else if (args[0].equalsIgnoreCase("reload")) {
                // reload config
                Main.getPlugin(Main.class).reloadConfig();
                Utils.broadcastToOps("Config reloaded");
            } else {
                sender.sendMessage("Unknown command. Use: /ob help");
                return true;
            }

            return true;
        }
    };
}