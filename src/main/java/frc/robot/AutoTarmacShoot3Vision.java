package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Shooter.ManualShotPreset;

public class AutoTarmacShoot3Vision extends AutoBaseClass {
    boolean fourBall;
	public void start(boolean ball) {
		super.start();
        fourBall = ball;
	}
    
    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        if (isRunning()) {
            SmartDashboard.putNumber("Auto Step", getCurrentStep());
            double angleOffset;
            VisionShooter.setAngleOffset();
            switch (getCurrentStep()) {
                case 0://step 1
                    VisionShooter.setLED(true);
                    Intake.startIntake();
                    Shooter.StartShooter();
                    Shooter.setManualPresets(ManualShotPreset.TarmacLine);
                    driveInches(55, 0, .8); // drive slowly toward 2nd ball
                    setTimerAndAdvanceStep(5000);
                    break;
                case 1:
                    if(driveCompleted())
                        advanceStep();
                    break;
                // case 2://step 2
                //     // driveInches(-15, 0, .5); // drive back up to the line
                //     // setTimerAndAdvanceStep(2000);
                //     advanceStep();
                //     break;
                // case 3:
                //     // if(driveCompleted())
                //     //     advanceStep();
                //     advanceStep();
                //     break;
                case 2:
                    // Shooter.alignAndShoot(true);-146+RobotGyro.getRelativeAngle()
                    // turnDegrees(VisionShooter.getStoredAngleOffset(), 1);
                    // setTimerAndAdvanceStep(1500);
                    advanceStep();
                    break;
                case 3:
                    // if (turnCompleted()) {
                    //     advanceStep();
                    // }
                    advanceStep();
                    break;
                case 4: 
                    Shooter.alignAndShoot(true);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 5: 
                    if (shooterCompleted()) {
                        advanceStep();
                    }
                    break;
                case 6: 
                    Shooter.oneShotAuto();
                    setTimerAndAdvanceStep(1000);
                    break;
                case 7:
                    if (shooterCompleted()) {
                        advanceStep();
                    }
                    break;
                case 8:
                    if (true) {
                       double angle =RobotGyro.getRelativeAngle() - 167;  // 162
                       turnDegrees(angle, 0.9);
                       setTimerAndAdvanceStep(1000);
                    } else {
                        setStep(10);
                    }
                    break;
                case 9:
                    if(turnCompleted()) {
                        advanceStep();
                    }
                    break;
                case 10:
                    driveInches(120, 0, 1);
                    setTimerAndAdvanceStep(5000);
                    break;
                case 11:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 12:
                    //angleOffset = RobotGyro.getRelativeAngle() - VisionBall.degreesToBall();
                    turnDegrees(VisionBall.degreesToBall(), 1);
                    setTimerAndAdvanceStep(750);
                    break;
                case 13:
                    if (turnCompleted()) {
                        advanceStep();
                    }
                    break;
                case 14:
                    driveInches(58, 0, 1);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 15:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                case 16:
                    Shooter.setManualPresets(ManualShotPreset.HumanPlayerStation);
                    if (fourBall) {
                        setTimerAndAdvanceStep(3000);
                    } else {
                        setStep(14);
                    }
                    break;
                case 17:
                    break;
                case 18:
                    driveInches(12, 0, 1);
                    setTimerAndAdvanceStep(750);
                    break;
                case 19:
                    if (driveCompleted()) {
                        advanceStep();
                    } // 149
                case 20:
                    turnDegrees(149 - RobotGyro.getRelativeAngle(), 1);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 21:
                    if (turnCompleted()) {
                        advanceStep();
                    }
                    break;
                case 22: 
                    Shooter.alignAndShoot(true);
                    setTimerAndAdvanceStep(1000);
                    break;
                case 23:
                    if (shooterCompleted()) {
                        advanceStep();
                    }
                    break;
                case 24:
                    Shooter.oneShotAuto();
                    setTimerAndAdvanceStep(1000);
                    break;
                case 25:
                    if (shooterCompleted()) {
                        advanceStep();
                    }
                    break;
                case 26:
                    Intake.stopIntake();
                    stop();
                    break;
                // case 18:
                //     driveInches(-168, 0, 1);
                //     setTimerAndAdvanceStep(3000);
                //     break;
                // case 19:
                //     if(driveCompleted()) {
                //         advanceStep();
                //     }
                //     break;
                // case 20:
                //     Shooter.alignAndShoot(true);
                //     setTimerAndAdvanceStep(2000);
                //     break;
                // case 21:
                //     break;
                // case 22:
                //     Shooter.oneShotAuto();
                //     setTimerAndAdvanceStep(1000);
                //     break;
                // case 23:
                //     break;
                // case 24:
                //     Intake.stopIntake();
                //     stop();
                //     break;
            } 
        }
    }
}