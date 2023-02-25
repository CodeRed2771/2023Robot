package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoPark extends AutoBaseClass{

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
                    turnDegrees(5, 0.1);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 1:
                    if (turnCompleted()) {
                        advanceStep();
                    }
                    break;
                case 2:
                    stop();
                    break;
            }
        }
    }

}
