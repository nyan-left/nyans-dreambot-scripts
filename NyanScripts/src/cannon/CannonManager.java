import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;

public class CannonManager {

    // TODO: Maybe check for all cannon parts.
    private static final int CANNON_ID = 6;
    private static final int CANNONBALL_ID = 2;


    private enum CannonState {
        PLACE_CANNON,
        LOAD_FIRE_CANNON,
        PICK_UP_CANNON,
        EXIT
    }

    public static boolean hasCannon() {
        return Inventory.contains(CANNON_ID);
    }

    public static boolean hasCannonballs() {
        return Inventory.contains(CANNONBALL_ID);
    }

    private static CannonState getCannonState() {
        if (!hasCannon() || !hasCannonballs()) {
            return CannonState.EXIT;
        }
        GameObject cannon = GameObjects.closest("Dwarf Cannon");
        if (cannon == null) {
            return CannonState.PLACE_CANNON;
        }
        if (hasCannonballs()) {
            return CannonState.LOAD_FIRE_CANNON;
        }
        return CannonState.PICK_UP_CANNON;
    }

    public static void manageCannon(Cannon main) {
        CannonState state = getCannonState();

        switch (state) {
            case PLACE_CANNON:
                placeCannon();
                break;
            case LOAD_FIRE_CANNON:
                GameObject cannon = GameObjects.closest("Dwarf Cannon");
                loadAndFireCannon(cannon);
                break;
            case PICK_UP_CANNON:
                cannon = GameObjects.closest("Dwarf Cannon");
                pickUpCannon(cannon);
                break;
            case EXIT:
            default:
                main.exit("You don't have a cannon or cannonballs");
                break;
        }
    }

    private static void placeCannon() {
        Item cannon = Inventory.get(CANNON_ID);
        if (cannon != null) {
            cannon.interact("Set-up");
            Sleep.sleepUntil(() -> GameObjects.closest("Dwarf Cannon") != null, 5000);
        }
    }

    private static void loadAndFireCannon(GameObject cannon) {
        int cannonballsAmount = Inventory.count(CANNONBALL_ID);
        if (cannonballsAmount > 10) {
            cannon.interact("Fire");
            Sleep.sleepUntil(() -> !Players.getLocal().isAnimating(), 1000);
        } else {
            cannon.interact("Add");
            Sleep.sleepUntil(() -> Inventory.count(CANNONBALL_ID) < cannonballsAmount, 1000);
        }
    }

    private static void pickUpCannon(GameObject cannon) {
        if (cannon != null) {
            cannon.interact("Pick-up");
            Sleep.sleepUntil(() -> Inventory.contains(CANNON_ID), 5000);
        }
    }
}