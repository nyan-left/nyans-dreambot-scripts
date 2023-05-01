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

    private long nextLoadAttemptTime;
    private boolean isCannonLoaded;
    private Cannon main;
    private InventoryManager inventoryManager;

    public CannonManager(Cannon main) {
        this.main = main;
        this.inventoryManager = inventoryManager;
    }

    private enum CannonState {
        PLACE_CANNON,
        LOAD_CANNON,
        FIRE_CANNON,
        PICK_UP_CANNON,
        EXIT
    }

    private CannonState getCannonState() {
        GameObject cannon = GameObjects.closest("Dwarf multicannon");

        if (cannon == null && !hasCannon() && !hasCannonballs()) {
            return CannonState.EXIT;
        }
        if (cannon == null) {
            return CannonState.PLACE_CANNON;
        }
        if (hasCannonballs()) {
            return isCannonLoaded ? CannonState.FIRE_CANNON : CannonState.LOAD_CANNON;
        }
        return CannonState.PICK_UP_CANNON;
    }


    public void manageCannon() {
        CannonState state = getCannonState();
        Logger.log(state.toString());

        switch (state) {
            case PLACE_CANNON:
                placeCannon();
                break;
            case LOAD_CANNON:
                loadCannon();
                break;
            case FIRE_CANNON:
                if (hasCannonballs()) {
                    loadCannon();
                } else {
                    main.exit("No more cannonballs in inventory");
                }
                break;
            case PICK_UP_CANNON:
                pickUpCannon();
                break;
            case EXIT:
            default:
                main.exit("You don't have a cannon or cannonballs");
                break;
        }
    }

    private void loadCannon() {
        if (System.currentTimeMillis() < nextLoadAttemptTime) {
            return;
        }

        GameObject cannon = GameObjects.closest("Dwarf multicannon");
        if (cannon != null) {
            cannon.interact("Fire");
            Sleep.sleep(1000);
            isCannonLoaded = true;
            nextLoadAttemptTime = System.currentTimeMillis() + getRandomLoadInterval();
        }
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
        }
    }

    private void pickUpCannon() {
        GameObject cannon = GameObjects.closest("Dwarf multicannon");
        if (cannon != null) {
            cannon.interact("Pick-up");
            Sleep.sleepUntil(() -> Inventory.contains(CANNON_ID), 5000);
        }
    }

    public void onGameMessage(String message) {
        switch (message) {
            case "There isn't enough space to set up here.":
                pickUpCannon();
                main.exit("There isn't enough space to set up here.");
                break;
            case "You add the furnace.":
                isCannonLoaded = false;
                break;
            case "You load the cannon with 30 cannonballs.":
            case "Your cannon is already firing.":
                isCannonLoaded = true;
                break;
            case "You pick up the cannon. It's really heavy.":
                isCannonLoaded = false;
                break;
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