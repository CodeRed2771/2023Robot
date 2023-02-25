package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoCPlace1 extends AutoBaseClass{

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
                    driveInches(210, 0, 1);
                    setTimerAndAdvanceStep(6000);
                    break;
                case 1:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 2: 
                    Intake.deploy();
                    Intake.run(1);
                    driveInches(26, 0, 1);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 3:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 4:
                    driveInches(-200, 0, 1);
                    setTimerAndAdvanceStep(5000);
                    break;
                case 5:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                break;
                case 6:
                    stop();
                    break;
            }
        }
    }

}
