import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

import java.awt.*;


public class CannonPaint {

    public static void draw(Graphics graphics, AntiBan antiBan, CannonManager cannonManager) {
        graphics.setFont(new Font("Century Gothic", Font.BOLD, 12));
        graphics.setColor(new Color(135, 135, 125));

        graphics.drawString("Xp to next level: " + Skills.getExperienceToLevel(Skill.RANGED), 20, 50);
        graphics.drawString("Next antiban action in: " + antiBan.getTimeToNextAction() + " seconds", 20, 70);

        String currentStateName = cannonManager.getCurrentStateName();
        graphics.drawString("Current state: " + currentStateName, 20, 90);
    }

}
