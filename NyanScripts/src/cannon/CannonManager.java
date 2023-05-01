import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Player;
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
        HOP_WORLD,
        BROKEN,
        EXIT,
    }

    private CannonState getCannonState() {
        Logger.log("Getting cannon state");
        Logger.log("Has cannonballs: " + hasCannonballs());

        if (placedCannon != null) {
            Logger.log("Placed cannon: " + placedCannon.getName());
        } else {
            Logger.log("Placed cannon: null");
        }

        if (!hasCannonballs()) return CannonState.EXIT;

        if (this.isBroken) return CannonState.BROKEN;

        // if cannon has been placed, load
        if (placedCannon != null) return CannonState.FIRE_LOAD_CANNON;
        // if cannon in inventory, place
        if (Inventory.contains(CANNON_ID)) return CannonState.PLACE_CANNON;

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
            case BROKEN:
                repairCannon();
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

    private int targetX = 2528;
    private int targetY = 3370;

    private Tile targetTile = new Tile(targetX, targetY);


    private void placeCannon() {
        this.walkToSpot();

        Item cannon = Inventory.get(CANNON_ID);
        if (cannon != null) {
            cannon.interact("Set-up");
            Sleep.sleepUntil(() -> GameObjects.closest("Dwarf multicannon") != null, 10000);
            GameObject placedCannonObject = GameObjects.closest("Dwarf multicannon");
            if (placedCannonObject != null) {
                placedCannon = placedCannonObject;
                Logger.log("Placed cannon");
            } else {
                Logger.log("Failed to place the cannon");
            }
        }

        Sleep.sleep(1000, 5000);

        this.walkToSpot();
    }

    private void walkToSpot() {
        //if not at spot, walk there
        Player localPlayer = Players.getLocal();
        // Check if the player is not already at the target tile
        if (localPlayer != null && !localPlayer.getTile().equals(targetTile)) {
            // Walk to the target tile
            Walking.walk(targetTile);
            Sleep.sleepUntil(() -> targetTile.distance() <= 1, 5000);
        }
    }

    public void exit() {
        this.pickUpCannon();
        main.exit("pick up cannon and exit called");
    }

    public void pickUpCannon() {
        if (placedCannon != null) {
            placedCannon.interact("Pick-up");
            Sleep.sleepUntil(() -> Inventory.contains(CANNON_ID), 5000);
        }
    }

    public void onGameMessage(String message) {
        switch (message) {
            case "That isn't your cannon!":
            case "There isn't enough space to set up here.":
                Logger.log("Hopping worlds");
                this.hopWorld();
                // HOP!
                break;
            case "You pick up the cannon. It's really heavy.":
                main.exit("Picked up cannon");
                break;
            case "Your cannon has broken!":
                Logger.log("Cannon has broken");
                this.isBroken = true;
                break;
            case "You repair your cannon, restoring it to working order.":
                Logger.log("Cannon has been repaired");
                this.isBroken = false;
                break;
            case "You add the furnace.":
            case "You load the cannon with 30 cannonballs.":
            case "Your cannon is already firing.":
            default:
                break;
        }
    }

    private boolean isBroken = false;

    public void repairCannon() {
        GameObject cannon = GameObjects.closest("Broken multicannon");
        if (cannon != null) {
            cannon.interact("Repair");
            Sleep.sleepUntil(() -> GameObjects.closest("Dwarf multicannon") != null, 10000);
            GameObject placedCannonObject = GameObjects.closest("Dwarf multicannon");
            if (placedCannonObject != null) {
                placedCannon = placedCannonObject;
                Logger.log("Placed cannon");
            } else {
                Logger.log("Failed to place the cannon");
            }
        }
    }

    private void hopWorld() {
        main.hopWorld();
    }


    private boolean hasCannonballs() {
        return inventoryManager.contains(CANNONBALL_ID);
    }

}