import org.dreambot.api.methods.Calculations;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


@ScriptManifest(name = "Nyan Alch Teler", description = "A simple script that alches and teles", author = "Nyan Left",
        version = 0.0, category = Category.MAGIC, image = "")
public class Main extends AbstractScript {
    private int itemToAlch = 1406;

    private AntiBan antiBan;

    private Paint paint;


    @Override
    public void onStart() {
        SwingUtilities.invokeLater(() -> {
            GUI.createGUI(this);
        });

        this.setupAntiBan();
        paint = new Paint(this, antiBan);
    }


    private void setupAntiBan() {
        List<AntiBan.AntiBanAction> antiBanActions = new ArrayList<>();

        antiBanActions.add(new AntiBan.AntiBanAction(AntiBan.AntiBanType.CHECK_MAGIC_XP, 1 * 60 * 1000, 10 * 60 * 1000, 1 * 60 * 1000, 2 * 60 * 1000));
        antiBanActions.add(new AntiBan.AntiBanAction(AntiBan.AntiBanType.IDLE_FOR_A_BIT, 1 * 60 * 1000, 10 * 60 * 1000, 1 * 60 * 1000, 2 * 60 * 1000));
        AntiBan antiBan = new AntiBan(this, antiBanActions);
        this.antiBan = antiBan;
    }

    @Override
    public int onLoop() {
        if (!isRunning) return Calculations.random(1000, 2000);

        if (!Alcher.hasRequirements()) {
            exit("You don't have the requirements to run this script, make sure you can cast high alch and camelot teleport");
        }

        antiBan.performAntiBan();

        if (!antiBan.isAntibanRunning()) {
            Alcher.performAlchTeleportCycle(this);
        }


        return Calculations.random(50, 550);
    }


    public void exit(String message) {
        Logger.log(message);
        isRunning = false;
        this.stop();
    }

    public void setItemToAlch(int itemToAlch) {
        this.itemToAlch = itemToAlch;
    }

    public int getItemToAlch() {
        return itemToAlch;
    }

    private boolean isRunning = false;

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    @Override
    public void onPaint(Graphics graphics) {
        paint.draw(graphics);
    }
}