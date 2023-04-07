package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Arm.extenderPresets;
import frc.robot.Arm.shoulderPresets;
import frc.robot.Claw.ClawPresets;

public class AutoRailRider2 extends AutoBaseClass{

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
                // Claw.setClawPosition(ClawPresets.OPEN);
                    Arm.presetShoulder(shoulderPresets.PLACING_HIGH);
                    //Arm.presetExtend(extenderPresets.RETRACTED);
                    setTimerAndAdvanceStep(1000);
                    break;
                case 1:
                    break;
                case 2:
                    Arm.presetExtend(extenderPresets.HIGH);
                    setTimerAndAdvanceStep(1700);
                    break;
                case 3: 
                    break;
                case 4:
                    // Claw.setClawPosition(ClawPresets.CLOSE);
                    Claw2.open();
                    setTimerAndAdvanceStep(800);
                    break;
                case 5:
                    break;
                case 6:
                    // Claw.stopClawTO();
                    Arm.presetExtend(extenderPresets.RETRACTED);
                    setTimerAndAdvanceStep(100);
                    break;
                case 7:
                    break;
                case 8:
                    driveInches(72, 0, 1);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 9: 
                    if(driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 10:
                    if(DriverStation.getAlliance() == Alliance.Red) {
                        driveInches(132, 15, 1);
                    } else {
                        driveInches(132, -15, 1);
                    }
                    setTimerAndAdvanceStep(3500);
                    break;
                case 11:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 12:
                    if(DriverStation.getAlliance() == Alliance.Red) {
                        driveInches(-25, 90, 1);
                    } else {
                        driveInches(25, 90, 1);
                    }
                    Intake.deploy();
                    Intake.run(1);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 13:
                    if(driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 14:
                    driveInches(32, 0, 0.9);
                    setTimerAndAdvanceStep(1250);
                    break;
                case 15:
                    if(driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 16:
                    if(DriverStation.getAlliance() == Alliance.Red) {
                        driveInches(-252, -12, 1);
                    } else {
                        driveInches(-252, 12, 1);
                    }
                    setTimerAndAdvanceStep(3500);
                break;
                case 17:
                    if(driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 18:
                    Intake.retract();
                    Intake.stop();
                    Intake.liveBottomIntake();
                    LiveBottom2.forwardBasic();
                    setTimerAndAdvanceStep(4000);
                    break;
                case 19:
                    break;
                case 20:
                    Intake.liveBottomIntakeStop();
                    LiveBottom2.stopBasic();
                    stop();
                    break;
            }
        }
    }

}

