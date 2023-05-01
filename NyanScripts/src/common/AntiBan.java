import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AntiBan {
    private Timer timer;
    private List<AntiBanAction> antiBanActions;


    public enum AntiBanType {
        CHECK_MAGIC_XP,
        IDLE_FOR_A_BIT,

        ROTATE_CAMERA,
    }

    public static class AntiBanAction {
        private AntiBanType type;
        private int minFrequency;
        private int maxFrequency;
        private int minDuration;
        private int maxDuration;

        public AntiBanAction(AntiBanType type, int minFrequency, int maxFrequency, int minDuration, int maxDuration) {
            this.type = type;
            this.minFrequency = minFrequency;
            this.maxFrequency = maxFrequency;
            this.minDuration = minDuration;
            this.maxDuration = maxDuration;
        }

        public AntiBanType getType() {
            return type;
        }

        public int getRandomFrequency() {
            return ThreadLocalRandom.current().nextInt(minFrequency, maxFrequency + 1);
        }

        public int getRandomDuration() {
            return ThreadLocalRandom.current().nextInt(minDuration, maxDuration + 1);
        }
    }

    public AntiBan(List<AntiBanAction> antiBanActions) {
        this.timer = new Timer();
        this.antiBanActions = antiBanActions;
        timer.setRunTime(getRandomActionFrequency());
    }

    public void performAntiBan() {
        if (timer.finished()) {
            AntiBanAction randomAction = antiBanActions.get(ThreadLocalRandom.current().nextInt(antiBanActions.size()));
            switch (randomAction.getType()) {
                case CHECK_MAGIC_XP:
                    checkMagicXP(randomAction.getRandomDuration());
                    break;
                case IDLE_FOR_A_BIT:
                    idleForABit(randomAction.getRandomDuration());
                    break;
                case ROTATE_CAMERA:
                    rotateCamera(randomAction.getRandomDuration());
                    break;
            }

            
            synchronized (timer) {
                timer.reset();
                timer.setRunTime(getRandomActionFrequency());
            }
        }
    }

    private void rotateCamera(int duration) {
        Camera.mouseRotateTo(Calculations.random(0, 360), Calculations.random(0, 360));
        Sleep.sleep(duration);
    }

    private int getRandomActionFrequency() {
        return antiBanActions.get(ThreadLocalRandom.current().nextInt(antiBanActions.size())).getRandomFrequency();
    }

    private void checkMagicXP(int duration) {
        Logger.log("Performing antiban: Checking Magic XP");

    }

    private void idleForABit(int duration) {
        Logger.log("Performing antiban: Idling for a bit");
        Sleep.sleep(duration);
    }

    public boolean isAntibanRunning() {
        return timer.finished();
    }

    public long getTimeToNextAction() {
        return timer.remaining() / 1000;
    }
}
