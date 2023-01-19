package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Shooter.ManualShotPreset;

public class AutoTarmacShoot2 extends AutoBaseClass{

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
                    setTimerAndAdvanceStep(500);
                    break;
                case 1:
                    break;
                case 2://segment 1 
                    driveInches(24, 0, 0.75);
                    Shooter.setManualPresets(ManualShotPreset.BackOfTarmac);
                    Intake.deployIntake();
                    setTimerAndAdvanceStep(3000);
                    break;
                case 3:
                    break;
                case 4://segment 2
                    Shooter.oneShotAuto();
                    setTimerAndAdvanceStep(750);
                    break;
                case 5:
                    break;
                case 6:
                    Intake.startIntake();
                    advanceStep();
                    break;
                case 7:
                    driveInches(20, 0, 0.75);
                    setTimerAndAdvanceStep(3000);
                    break;
                case 8:
                    if(driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 9:
                    advanceStep();
                    break;
                case 10: 
                    driveInches(-30,0,.75);
                    Shooter.setManualPresets(ManualShotPreset.BackOfTarmac);
                    setTimerAndAdvanceStep(3000);
                    break;
                case 11: 
                    if(driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 12: 
                    driveInches(10,0,.75);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 13: 
                    if(driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 14:
                    Intake.stopIntake();
                    setTimerAndAdvanceStep(1000); // give ball time to settle
                    break;
                case 15:
                    break;
                case 16:
                    Shooter.oneShotAuto();
                    setTimerAndAdvanceStep(750);
                    break;
                case 17:
                    break;
                case 18: 
                    driveInches(30, 0, .75);
                    setTimerAndAdvanceStep(3000);
                    break;
                case 19:
                    if(driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 20://stop
                    stop();
                    break;
            } 
        }
    }
}