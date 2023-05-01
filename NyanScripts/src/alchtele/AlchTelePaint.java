import common.AntiBan;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.AbstractScript;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;


public class AlchTelePaint {

    private AntiBan antiBan;
    private AbstractScript script;

    public AlchTelePaint(AbstractScript script, AntiBan antiBan) {
        this.script = script;
        this.antiBan = antiBan;

    }

    public Image getImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void draw(Graphics graphics) {

        graphics.setFont(new Font("Century Gothic", Font.BOLD, 12));
        graphics.setColor(new Color(135, 135, 125));

        graphics.drawString("Xp to next level: " + Skills.getExperienceToLevel(Skill.MAGIC), 20, 50);
        graphics.drawString("Next antiban action in: " + antiBan.getTimeToNextAction() + " seconds", 20, 70);
    }

}
