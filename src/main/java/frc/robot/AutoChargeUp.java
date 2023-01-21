package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoChargeUp extends AutoBaseClass {
    double pitch; 
    double angle;


    public void start() {
		super.start();
	}
    
    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        if (isRunning()) {
            SmartDashboard.putNumber("Auto Chart Step", getCurrentStep());
            pitch = RobotGyro.pitch();
            
            switch (getCurrentStep()) {
                case 0: 
                    angle = 180 - RobotGyro.getRelativeAngle();
                    turnDegrees(angle, .5);
                    setTimerAndAdvanceStep(1000);
                    break;
                case 1:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 2:
                    driveInches(10, 0, .5);
                    setTimerAndAdvanceStep(1000);
                    break;
                case 3:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 4: 
                    if (pitch >= 2) {
                        driveInches(4, 0, .5);
                    } else if (pitch <= -2) {
                        driveInches(-4, 0, .5);
                    } else {
                        lockWheels();
                    }
                    break;
                case 5: 
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 6:
                    if (pitch >= 2 || pitch <= -2) {
                        setStep(4);
                    } else {
                        stop();
                    }
            }
        }
    }

    private void lockWheels() {
    }
}
