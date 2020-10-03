package tk.augmeneco.MineBlindSearch;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.material.Wool;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public final class WorldListener implements Listener {

    WorldListener() {
    }

    private boolean getRandomBoolean(Random random, double p){
        return random.nextDouble() <= p;
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        event.getWorld().getPopulators().add(blockPopulator);
        System.out.println(Arrays.toString(event.getWorld().getPopulators().toArray()));
        System.out.println("Block Populator added");
    }

    BlockPopulator blockPopulator = new BlockPopulator() {
        @Override
        public void populate(World world, Random random, Chunk chunk) {
            boolean have = getRandomBoolean(random,
                    Main.getPlugin(Main.class).getConfig().getDouble("block-spawn-probability"));
            if (have) {
                int X = random.nextInt(15);
                int Z = random.nextInt(15);
                int Y;
                for (Y = world.getMaxHeight() - 1; chunk.getBlock(X, Y, Z).getType() == Material.AIR; Y--);

                Wool wool = new Wool(DyeColor.RED);
                Block block = chunk.getBlock(X, Y + 1, Z);
                block.setType(Material.WOOL);
                block.setData(wool.getData());

                if (Observation.observationInProgress) {
                    try {
                        Observation.out.write(String.format("NewBlock: %d %d %d\n", X, Y, Z));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    };

    //    @EventHandler
//    public void onPopulate(ChunkPopulateEvent event) {
//        //System.out.println("222222222222AAAAAA\nADFDFSEDF\nERGHDFFAS\nFDGZDFFGZ\nDSFZSDFSZD");
//        //event.getChunk().getBlock(5,70,5).setType(Material.REDSTONE_BLOCK);
//
//        Wool wool = new Wool(DyeColor.RED);
//        //int amount = random.nextInt(4);
//        boolean have = getRandomBoolean((float) 0.01);
//        if (have) {
//            //for (int i = 1; i < amount; i++) {
//            int X = random.nextInt(15);
//            int Z = random.nextInt(15);
//            int Y;
//            for (Y = event.getWorld().getMaxHeight() - 1; event.getChunk().getBlock(X, Y, Z).getType() == Material.AIR; Y--);
//
//            Block block = event.getChunk().getBlock(X, Y + 1, Z);
//            block.setType(Material.WOOL);
//            block.setData(wool.getData());
//            //}
//        }
//    }
}
