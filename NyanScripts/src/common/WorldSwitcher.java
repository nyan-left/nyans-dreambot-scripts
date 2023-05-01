import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class WorldSwitcher {

    public static void hopToRandomWorld() {
        // Retrieve a list of all available worlds
        List<World> allWorlds = Worlds.all();

        // Filter out any worlds that are not suitable for hopping (e.g. PvP worlds, high-risk worlds, etc.)
        List<World> filteredWorlds = allWorlds.stream()
                .filter(world -> world.isNormal() && !world.isF2P())
                .collect(Collectors.toList());

        // Choose a random world from the filtered list
        World randomWorld = filteredWorlds.get((int) (Math.random() * filteredWorlds.size()));

        // Hop to the chosen random world
        WorldHopper.hopWorld(randomWorld);

//        Sleep.sleepUntil(() -> Worlds.getCurrentWorld() == randomWorld., 5000);
        Logger.log("Hopped to world " + randomWorld.toString());
    }
}