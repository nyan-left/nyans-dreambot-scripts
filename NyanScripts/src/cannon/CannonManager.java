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
    private static final int BROKEN_CANNON_ID = 14916;
    private static final Tile TARGET_TILE = new Tile(2528, 3370);

    private GameObject placedCannon;
    private long nextLoadAttemptTime;
    private final Cannon main;
    private final InventoryManager inventoryManager;

    public CannonManager(Cannon main) {
        this.main = main;
        this.inventoryManager = new SimpleInventoryManager();
        this.placedCannon = GameObjects.closest("Dwarf multicannon");
    }

    private enum CannonState {
        PLACE_CANNON, FIRE_LOAD_CANNON, BROKEN, EXIT
    }

    private CannonState getCannonState() {
        if (!hasCannonballs()) return CannonState.EXIT;
        if (isBrokenCannonNearby()) return CannonState.BROKEN;
        if (placedCannon != null) return CannonState.FIRE_LOAD_CANNON;
        if (Inventory.contains(CANNON_ID)) return CannonState.PLACE_CANNON;
        return CannonState.EXIT;
    }

    public String getCurrentStateName() {
        return getCannonState().name();
    }


    public void manageCannon() {
        switch (getCannonState()) {
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
                exit();
                break;
        }
    }


    private void loadCannon() {
        if (System.currentTimeMillis() < nextLoadAttemptTime || placedCannon == null) return;
        placedCannon.interact("Fire");
        Sleep.sleep(100, 3000);
        nextLoadAttemptTime = System.currentTimeMillis() + getRandomLoadInterval();
    }

    private int getRandomLoadInterval() {
        Random random = new Random();
        return random.nextInt((20 - 7) * 1000) + 7 * 1000; // 7-20 seconds
    }

    private boolean isBrokenCannonNearby() {
        return GameObjects.closest(BROKEN_CANNON_ID) != null;
    }

    private void placeCannon() {
        walkToSpot();
        Item cannon = Inventory.get(CANNON_ID);
        if (cannon != null) {
            cannon.interact("Set-up");
            Sleep.sleepUntil(() -> (placedCannon = GameObjects.closest("Dwarf multicannon")) != null, 10000);
        }
        Sleep.sleep(1000, 5000);
        walkToSpot();
    }

    private void walkToSpot() {
        Player localPlayer = Players.getLocal();
        if (localPlayer != null && !localPlayer.getTile().equals(TARGET_TILE)) {
            Walking.walk(TARGET_TILE);
            Sleep.sleepUntil(() -> TARGET_TILE.distance() <= 1, 5000);
        }
    }

    public void exit() {
        pickUpCannon();
        main.exit("pick up cannon and exit called");
    }

    public void pickUpCannon() {
        if (placedCannon != null) {
            placedCannon.interact("Pick-up");
            Sleep.sleepUntil(() -> Inventory.contains(CANNON_ID), 5000);
        }
    }

    @Override
    public void onGameMessage(String message) {
        Logger.log("Game message: " + message);
        switch (message) {
            case "That isn't your cannon!":
            case "There isn't enough space to set up here.":
                Logger.log("Hopping worlds");
                main.hopWorld();
                break;
            case "You pick up the cannon. It's really heavy.":
                main.exit("Picked up cannon");
                break;
            case "Your cannon has broken!":
                Logger.log("Cannon has broken");
                break;
            default:
                break;
        }
    }

    private void repairCannon() {
        GameObject cannon = GameObjects.closest("Broken multicannon");
        if (cannon != null) {
            cannon.interact("Repair");
            Sleep.sleepUntil(() -> (placedCannon = GameObjects.closest("Dwarf multicannon")) != null, 10000);
        }
    }

    private boolean hasCannonballs() {
        return inventoryManager.contains(CANNONBALL_ID);
    }
}