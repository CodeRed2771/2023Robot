package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoParkingBrake extends AutoBaseClass{
    public void start() {
		super.start();
	}

    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        if (isRunning()) {
            SmartDashboard.putNumber("Auto Parking Brake Step", getCurrentStep());
            switch(getCurrentStep()) {
                case 0:
                    // turnDegrees(1, 0);
                    DriveAuto.parkingBrake();
                    setTimerAndAdvanceStep(100);
                    break;
                case 1:
                    break;
                case 2:
                    break;
            }
        }
    }
}
