package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoLeaveTarmack extends AutoBaseClass{

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
                    driveInches(24, 0, 1);
                    setTimerAndAdvanceStep(6000);
                    break;
                case 1:
                    if (driveCompleted()) {
                        setTimerAndAdvanceStep(1000);
                    }
                    break;
                case 2:
                    setTimerAndAdvanceStep(2000);
                    break;
                case 3:
                    break;
                case 4: 
                    driveInches(-24, 0, 1);
                    setTimerAndAdvanceStep(6000);
                    break;
                case 5:
                    if (driveCompleted()) {
                        setTimerAndAdvanceStep(1000);
                    }
                    break;
                case 6:
                    stop();
                    break;
            }
        }
    }

}
