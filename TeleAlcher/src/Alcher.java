import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.utilities.Sleep;


public class Alcher {

    private static boolean switchOrderOfActions = false;

    public static boolean hasRequirements() {
        return Magic.canCast(Normal.HIGH_LEVEL_ALCHEMY) && Magic.canCast(Normal.CAMELOT_TELEPORT);
    }

    public static void performAlchTeleportCycle(Main main) {
        if (!Magic.isSpellSelected()) {
            castHighAlch();
        } else {
            if (Inventory.contains(main.getItemToAlch())) {
                alchItem(main);
                Sleep.sleep(500, 1000);
                castCamelotTeleport();
            } else {
                main.exit("You don't have any more items to alch");
            }
        }
    }

    private static void castHighAlch() {
        Magic.castSpell(Normal.HIGH_LEVEL_ALCHEMY);
        Sleep.sleepUntil(() -> Tabs.isOpen(Tab.INVENTORY), 2000);
    }

    private static void alchItem(Main main) {
        Inventory.get(main.getItemToAlch()).interact();
        Sleep.sleepUntil(() -> !Players.getLocal().isAnimating(), 1000);
    }

    private static void castCamelotTeleport() {
        Magic.castSpell(Normal.CAMELOT_TELEPORT);
        Sleep.sleepUntil(() -> !Players.getLocal().isAnimating(), 5000);
    }
}