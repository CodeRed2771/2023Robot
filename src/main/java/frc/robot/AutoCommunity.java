package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoCommunity extends AutoBaseClass{

	public void start() {
		super.start();
	}
    
    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        if (isRunning()) {
            SmartDashboard.putNumber("Auto Step", getCurrentStep());
            switch (getCurrentStep()) {
                case 0: 
                    driveInches(210, 0, 0.6);
                    setTimerAndAdvanceStep(6000);
                    break;
                case 1:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 2: 
                    driveInches(26, 0, 0.6);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 3:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 4:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                break;
                case 5:
                    stop();
                    break;
            }
        }
    }

}
