import org.dreambot.api.methods.Calculations;
import org.dreambot.api.randoms.BreakSolver;
import org.dreambot.api.randoms.RandomSolver;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.widgets.message.Message;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


@ScriptManifest(name = "Nyan Cannoner", description = "Cannons ogres!", author = "Nyan Left", version = 0.0, category = Category.COMBAT)
public class Cannon extends AbstractScript implements ChatListener {

    private AntiBan antiBan;
    private CannonPaint paint;

    CannonManager cannonManager;

    @Override
    public void onGameMessage(Message message) {
        cannonManager.onGameMessage(message.getMessage());
    }

    @Override
    public void onStart() {
        Logger.log("Starting script");
        this.paint = new CannonPaint();
        this.setupAntiBan();
        this.cannonManager = new CannonManager(this);

        SwingUtilities.invokeLater(() -> {
            this.isRunning = true;
        });
    }

    private void setupAntiBan() {
        List<AntiBan.AntiBanAction> antiBanActions = new ArrayList<>();

        // antiBanActions.add(new AntiBan.AntiBanAction(AntiBan.AntiBanType.ROTATE_CAMERA, 30 * 1000, 10 * 120 * 1000, 1500, 5000));
        antiBanActions.add(new AntiBan.AntiBanAction(AntiBan.AntiBanType.IDLE_FOR_A_BIT, 60 * 1000, 10 * 60 * 1000, 60 * 1000, 2 * 60 * 1000));

        this.antiBan = new AntiBan(antiBanActions);

    }

    @Override
    public boolean onSolverStart(RandomSolver solver) {
        if (solver instanceof BreakSolver) {
            cannonManager.pickUpCannon();
            Sleep.sleep(10000);
        }
        super.onSolverStart(solver);
        return true;
    }

    @Override
    public int onLoop() {
        if (!isRunning) return Calculations.random(1000, 2000);

        antiBan.performAntiBan();

        if (!antiBan.isAntibanRunning()) {
            cannonManager.manageCannon();
        }


        return Calculations.random(50, 550);
    }


    public void exit(String message) {
        Logger.log(message);
        isRunning = false;
        this.stop();
    }

    public void hopWorld() {
        Logger.log("Hopping worlds");
        WorldSwitcher.hopToRandomWorld();
    }


    private boolean isRunning = false;

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    @Override
    public void onPaint(Graphics graphics) {
        paint.draw(graphics, antiBan, cannonManager);
    }
}