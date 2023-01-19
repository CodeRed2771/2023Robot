package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
                    // VisionShooter.setLED(true);
                    // Intake.startIntake();
                    // Shooter.StartShooter();
                    // Shooter.setManualPresets(ManualShotPreset.TarmacLine);
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
                    // Shooter.alignAndShoot(true);
                    setTimerAndAdvanceStep(3000);
                    break;
                case 5:
                    break;
                case 6:
                    driveInches(15, 0, 0.5); // drive out to jostle the 2nd ball in
                    // Shooter.setManualPresets(ManualShotPreset.TarmacLine); // make sure this is still set
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
                    // Shooter.alignAndShoot(true);
                    // Intake.stopIntake();
                    setTimerAndAdvanceStep(3000);
                    break;
                case 11:
                    break;
                case 12: 
                    // Shooter.alignAndShoot(true);
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
                    // Shooter.StopShooter();
                    stop();
                    break;
            } 
        }
    }
}