package frc.robot;

import javax.lang.model.util.ElementScanner6;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Shooter.ManualShotPreset;

//The purpose of this class is to turn the robot until we are on target.

public class AutoTarmacShoot3Troll extends AutoBaseClass {

    private double angleOffset = 0;
    private static AutoBaseClass mAlignProgram;
    double distance = 0;

    public void start() {
        super.start();
    }


    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        if (isRunning()) {
            DriveAuto.tick();
            SmartDashboard.putNumber("AutoAlighn Auto Step", getCurrentStep());
            switch (getCurrentStep()) {
                case 0://step 1
                VisionShooter.setLED(true);
                Intake.startIntake();
                Shooter.StartShooter();
                Shooter.setManualPresets(ManualShotPreset.TarmacLine);
                driveInches(40, 0, .8); // drive slowly toward 2nd ball
                setTimerAndAdvanceStep(5000);
                break;
            case 1:
                if(driveCompleted())
                    advanceStep();
                break;
            case 2: 
                Shooter.alignAndShoot(true); 
                setTimerAndAdvanceStep(5000);
                break;
            case 3: 
                break;
            case 4:
                driveInches(15, 0, .4);
                setTimerAndAdvanceStep(3000);
                break;
            case 5:
                if (driveCompleted()) {
                    advanceStep();
                } 
                break;
            case 6:
                turnDegrees(45, .8);
                setTimerAndAdvanceStep(3000);
                break;
            case 7:
                if (turnCompleted()) {
                    advanceStep();
                }
                break;
            case 8: 
                driveInches(24, 0, .8);
                setTimerAndAdvanceStep(3000);
                break;
            case 9:
                if (driveCompleted()) {
                    advanceStep();
                }
            case 10:
                driveInches(6, 0, .4);
                setTimerAndAdvanceStep(3000);
                break;
            case 11:
                if (driveCompleted()) {
                    advanceStep();
                }
                break;
            case 12:
                turnDegrees(45, .8);
                setTimerAndAdvanceStep(3000);
                break;
            case 13:
                if (turnCompleted()) {
                    advanceStep();
                }
                break;
            case 14:
                driveInches(6, 0, .8);
                setTimerAndAdvanceStep(1000);
                break;
            case 15:
                if (driveCompleted()) {
                    advanceStep();
                }
                break;
            case 16:
                mAlignProgram = new AutoBallAlign();
                mAlignProgram.start();
                setTimerAndAdvanceStep(2000);
                break;
            case 17:
                break;
            case 18:
                turnDegrees(10, .4);
                setTimerAndAdvanceStep(1000);
                break;
            case 19:
                if (turnCompleted()) {
                    advanceStep();
                }
                break;
            case 20:
                Intake.reverseIntake();
                setTimerAndAdvanceStep(1500);
                break;
            case 21:
                break;
            case 22:
                mAlignProgram = new AutoBallAlign();
                mAlignProgram.start();
                setTimerAndAdvanceStep(2000);
                break;
            case 23:
                break; 
            case 24:
                distance = VisionBall.distanceToBall();
                if (distance < 36) {
                    driveInches(distance, 0, .6);
                    setTimerAndAdvanceStep(3000);
                } else {

                }
                break;
            case 25:
                if(driveCompleted()) {
                    advanceStep();
                }
                break;
            case 26:
                driveInches(-distance + 10, 10, 0.8);
                setTimerAndAdvanceStep(3000);
                break;
            case 27:
                if (driveCompleted()) {
                    advanceStep();
                }
                break;
            case 28:
                Shooter.alignAndShoot(true);
                setTimerAndAdvanceStep(5000);
                break;
            case 29:
                break;
            case 30:
                Shooter.oneShotAuto();
                setTimerAndAdvanceStep(1000);
                break;
            case 31:
                break;
            case 32:
                stop();
                break;
            }
        }
    }
}