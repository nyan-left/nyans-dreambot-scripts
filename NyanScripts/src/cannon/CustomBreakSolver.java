import org.dreambot.api.randoms.BreakSolver;

public class CustomBreakSolver extends BreakSolver {

    private final CannonManager cannonManager;
    private boolean isCleaningUp;

    public CustomBreakSolver(CannonManager cannonManager) {
        this.cannonManager = cannonManager;
        this.isCleaningUp = false;
    }

    @Override
    public boolean shouldExecute() {
        if (super.shouldExecute()) {
            if (!isCleaningUp) {
                isCleaningUp = true;
                // Perform your cleanup tasks here before the break starts
                // For example: picking up the cannon
                cannonManager.pickUpCannon();
                // After the cleanup, return true for the break to start
                return true;
            }
        }
        return false;
    }
}