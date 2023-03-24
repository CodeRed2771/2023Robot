package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Test extends AutoBaseClass {
    public Test(AutoType type) {
        super(type);
    }
    public void start() {
        super.start();
    }
    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        SmartDashboard.putNumber("Test NonDrive Auto step", getCurrentStep());
        if(isRunning()) {
            switch(getCurrentStep()) {
                case 0:
                    // LiveBottom.forward();
                    setTimerAndAdvanceStep(2000);
                    break;
                case 1:
                    break;
                case 2:
                    // LiveBottom.reverse();
                    setTimerAndAdvanceStep(2000);
                    break;
                case 3:
                    break;
                case 4:
                    stop();
                    break;
            }
        }
    }
    
}
