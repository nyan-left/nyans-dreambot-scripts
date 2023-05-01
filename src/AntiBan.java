import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;

import java.util.concurrent.ThreadLocalRandom;

public class AntiBan {
    private AbstractScript script;
    private Timer timer;

    private static final int MIN_TIME = 12000;
    private static final int MAX_TIME = 90000;

    public AntiBan(AbstractScript script) {
        this.script = script;
        this.timer = new Timer();
        timer.setRunTime(ThreadLocalRandom.current().nextInt(MIN_TIME, MAX_TIME + 1)); // Next antiban action within the range of MIN_TIME and MAX_TIME

    }

    public void performAntiBan() {
        if (timer.finished()) {
            switch (ThreadLocalRandom.current().nextInt(2)) {
                case 0:
                    checkMagicXP();
                    break;
                case 1:
                    idleForABit();
                    break;
            }
            timer.reset();
            timer.setRunTime(ThreadLocalRandom.current().nextInt(MIN_TIME, MAX_TIME)); // Next antiban action in 1-5 minutes

        }

//        Logger.log("Next antiban action in " + timer.remaining() / 1000 + " seconds");
    }

    private void checkMagicXP() {
        script.log("Performing antiban: Checking Magic XP");
//
//        if (Tabs.isOpen(Tab.SKILLS)) {
//            Skills.hoverSkill(Skill.MAGIC);
//            Sleep.sleepUntil(() -> Skills.hoverSkill(Skill.MAGIC), 2000);
//            Sleep.sleep(1000, 3000);
//        } else {
//            Tabs.open(Tab.SKILLS);
//            Sleep.sleepUntil(() -> Tabs.isOpen(Tab.SKILLS), 2000);
//            Sleep.sleep(1000, 5000);
//        }
    }

    private void idleForABit() {
        Logger.log("Performing antiban: Idling for a bit");
        Sleep.sleep(1000, 3412);
    }

    public boolean isAntibanRunning() {
        return timer.finished();
    }

    public long getTimeToNextAction() {
        return timer.remaining() / 1000;
    }

}