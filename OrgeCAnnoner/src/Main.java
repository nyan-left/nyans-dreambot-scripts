import org.dreambot.api.methods.Calculations;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Logger;

import javax.swing.*;
import java.awt.*;


// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
@ScriptManifest(name = "Nyan Cannoner", description = "Cannons ogres!", author = "Nyan Left",
        version = 0.0, category = Category.COMBAT, image = "")
public class Main extends AbstractScript {

    private AntiBan antiBan;
    private Paint paint;

    @Override
    public void onStart() {
        Logger.log("Starting script");
        antiBan = new AntiBan(this);
        paint = new Paint(this, antiBan);
        
        SwingUtilities.invokeLater(() -> {
//            GUI.createGUI(this);
        });
//        antiBan = new AntiBan(this);
//        paint = new Paint(this, antiBan);
    }

    @Override
    public int onLoop() {
        if (!isRunning) return Calculations.random(1000, 2000);

//        if (!Alcher.hasRequirements()) {
//            exit("You don't have the requirements to run this script, make sure you can cast high alch and camelot teleport");
//        }
//
        antiBan.performAntiBan();
//
        if (!antiBan.isAntibanRunning()) {
//            Alcher.performAlchTeleportCycle(this);
        }
//

        return Calculations.random(50, 550);
    }


    public void exit(String message) {
        Logger.log(message);
        isRunning = false;
        this.stop();
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