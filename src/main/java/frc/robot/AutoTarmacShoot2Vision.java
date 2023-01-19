package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Shooter.ManualShotPreset;
import frc.robot.Shooter.ShooterPosition;

public class AutoTarmacShoot2Vision extends AutoBaseClass {

	public void start() {
		super.start();
	}
    
    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        if (isRunning()) {
            SmartDashboard.putNumber("Auto Auto Step", getCurrentStep());

            switch (getCurrentStep()) {
                case 0://step 1
                    VisionShooter.setLED(true);
                    Intake.startIntake();
                    Shooter.StartShooter();
                    Shooter.setManualPresets(ManualShotPreset.TarmacLine);
                    driveInches(55, 0, .4); // drive slowly toward 2nd ball
                    setTimerAndAdvanceStep(5000);
                    break;
                case 1:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 2://step 2
                    driveInches(-15, 0, .4); // drive back up to the line
                    setTimerAndAdvanceStep(2000);
                    break;
                case 3:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 4:
                    Shooter.alignAndShoot(true);
                    setTimerAndAdvanceStep(3000);
                    break;
                case 5:
                    break;
                case 6:
                    driveInches(15, 0, 0.5); // drive out to jostle the 2nd ball in
                    Shooter.setManualPresets(ManualShotPreset.TarmacLine); // make sure this is still set
                    setTimerAndAdvanceStep(3000);
                    break;
                case 7:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 8:
                    driveInches(-15, 0, .8); // hopefully the ball slides into place
                    setTimerAndAdvanceStep(2000);
                    break;
                case 9:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 10:
                    Shooter.alignAndShoot(true);
                    Intake.stopIntake();
                    setTimerAndAdvanceStep(3000);
                    break;
                case 11:
                    break;
                case 12: 
                    Shooter.alignAndShoot(true);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 13: 
                    break;
                case 14: 
                    driveInches(12, 0, .8); // make sure we're out of the tarmac
                    setTimerAndAdvanceStep(2000);
                    break;
                case 15:
                    break;
                case 16:
                    Shooter.StopShooter();
                    stop();
                    break;

                //old
                // case 0://step 1
                //     VisionShooter.setLED(true);
                //     driveInches(24, 0, .5);
                //     setTimerAndAdvanceStep(6000);
                //     break;
                // case 1://step 2
                //     if (driveCompleted()) {
                //         advanceStep();
                //     }
                //     break;
                // case 2://step 3
                //     Shooter.alignAndShoot();
                //     setTimerAndAdvanceStep(1000);
                //     break;
                // case 3://step 4
                //     break;
                // case 4://step 5
                //     Intake.startIntake();
                //     advanceStep();
                //     break;
                // case 5://step 6
                //     setTimerAndAdvanceStep(1000);
                //     break;
                // case 6://step 7
                //     break;
                // case 7://step 8
                //     if (VisionBall.ballInView()) {
                //         setStep(20);
                //     } else {
                //         turnDegrees(25, .8);
                //         setTimerAndAdvanceStep(1500);
                //     }
                //     break;
                // case 8://step 9
                //     if (turnCompleted()) {
                //         advanceStep();
                //     }
                //     break;
                // case 9://step 10
                //     setTimerAndAdvanceStep(1000);
                //     break;
                // case 10://step 11
                //     break;
                // case 11://step 12
                //     if (VisionBall.ballInView()) {
                //         setStep(20);
                //     } else {
                //         turnDegrees(-50, .8);
                //         setTimerAndAdvanceStep(2500);
                //     }
                //     break;
                // case 12://step 13
                //     if (turnCompleted()) {
                //         advanceStep();
                //     }
                //     break;
                // case 13://step 14
                //     setTimerAndAdvanceStep(1000);
                //     break;
                // case 14://step 15
                //     break;
                // case 15://step 16
                //     if (VisionBall.ballInView()) {
                //         setStep(20);
                //     } else {
                //         turnDegrees(25, .8);
                //         setTimerAndAdvanceStep(3000);
                //     }
                //     break;
                // case 16://step 13
                //     if (turnCompleted()) {
                //         advanceStep();
                //     }
                //     break;
                // case 17:
                //     driveInches(12, 0, 0.5);
                //     setTimerAndAdvanceStep(2000);
                //     break;
                // case 18://step 13
                //     if (driveCompleted()) {
                //         advanceStep();
                //     }
                //     break;
                // case 19:
                //     setStep(24);
                // case 20://step 17
                //     double ballDistance = VisionBall.distanceToBall();
                //     if(ballDistance > 36)
                //         ballDistance = 36;
                //     driveInches(ballDistance, VisionBall.degreesToBall(), .8);
                //     setTimerAndAdvanceStep(7000);
                //     break;
                // case 21://step 18
                //     if (driveCompleted()){
                //         advanceStep();
                //     }
                //     break;
                // case 22://step 19
                //     driveInches(12,0,.8);
                //     setTimerAndAdvanceStep(1500);
                //     break;
                // case 23://step 20
                //     if(driveCompleted()) {
                //         advanceStep();
                //     }
                //     break;
                // case 24://step 21
                //     Shooter.alignAndShoot();
                //     setTimerAndAdvanceStep(1000);
                //     break;
                // case 25://step 22
                //     break;
                // case 26://end
                //     stop();
                //     break;
            } 
        }
    }
}