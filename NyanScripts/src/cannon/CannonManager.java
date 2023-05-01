import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;

import java.util.Random;


interface InventoryManager {
    boolean contains(int itemId);
}

class SimpleInventoryManager implements InventoryManager {
    @Override
    public boolean contains(int itemId) {
        return Inventory.contains(itemId);
    }
}

interface GameMessageListener {
    void onGameMessage(String message);
}


public class CannonManager implements GameMessageListener {

    private static final int CANNON_ID = 6;
    private static final int CANNONBALL_ID = 2;

    private GameObject placedCannon;

    private long nextLoadAttemptTime;
    private final Cannon main;
    private final InventoryManager inventoryManager;

    public CannonManager(Cannon main) {
        this.main = main;
        this.inventoryManager = new SimpleInventoryManager();
    }

    private enum CannonState {
        PLACE_CANNON,
        FIRE_LOAD_CANNON,
        EXIT,
    }

    private CannonState getCannonState() {


        if ((placedCannon == null) && !hasCannon() && !hasCannonballs()) return CannonState.EXIT;
        if (placedCannon == null) return CannonState.PLACE_CANNON;
        if (hasCannonballs()) return CannonState.FIRE_LOAD_CANNON;


        Logger.log("Picking up cannon as we don't have any cannonballs");
        return CannonState.EXIT;
    }


    public void manageCannon() {
        CannonState state = getCannonState();
        Logger.log(state.toString());

        switch (state) {
            case PLACE_CANNON:
                placeCannon();
                break;

            case FIRE_LOAD_CANNON:
                loadCannon();
                break;

            case EXIT:
            default:
                this.exit();
                break;
        }
    }


    private void loadCannon() {
        if (System.currentTimeMillis() < nextLoadAttemptTime) return;
        if (placedCannon == null) return;


        placedCannon.interact("Fire");
        Sleep.sleep(100, 3000);
        nextLoadAttemptTime = System.currentTimeMillis() + getRandomLoadInterval();

    }


    private int getRandomLoadInterval() {
        Random random = new Random();
        return random.nextInt((15 - 10) * 1000) + 10 * 1000; // Convert to milliseconds
    }

    private void placeCannon() {
        Item cannon = Inventory.get(CANNON_ID);
        if (cannon != null) {
            cannon.interact("Set-up");
            Sleep.sleepUntil(() -> GameObjects.closest("Dwarf multicannon") != null, 5000);
            placedCannon = GameObjects.closest("Dwarf multicannon");
        }
    }

    public void exit() {
        if (placedCannon != null) {
            placedCannon.interact("Pick-up");
            Sleep.sleepUntil(() -> Inventory.contains(CANNON_ID), 5000);
        }
        main.exit("pick up cannon and exit called");
    }

    public void onGameMessage(String message) {
        switch (message) {

            case "There isn't enough space to set up here.":
            case "You pick up the cannon. It's really heavy.":
                this.exit();
                break;
            case "You add the furnace.":
            case "You load the cannon with 30 cannonballs.":
            case "Your cannon is already firing.":
            default:
                break;
        }
    }

    private boolean hasCannon() {
        return inventoryManager.contains(CANNON_ID);
    }

    private boolean hasCannonballs() {
        return inventoryManager.contains(CANNONBALL_ID);
    }

}