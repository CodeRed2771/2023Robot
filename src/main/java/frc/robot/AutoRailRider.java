package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Arm.extenderPresets;
import frc.robot.Arm.shoulderPresets;
import frc.robot.Claw.ClawPresets;

public class AutoRailRider extends AutoBaseClass{

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
                    Claw.stopClawTO();
                    Arm.presetShoulder(shoulderPresets.PLACING_HIGH);
                    Arm.presetExtend(extenderPresets.HIGH);
                    //Arm.presetExtend(extenderPresets.RETRACTED);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 1:
                    Claw.stopClawTO();
                    break;
                case 2:
                    Claw.setClawPosition(ClawPresets.CLOSE);
                    setTimerAndAdvanceStep(800);
                    break;
                case 3:
                    break;
                case 4:
                    Claw.stopClawTO();
                    Arm.presetExtend(extenderPresets.RETRACTED);
                    setTimerAndAdvanceStep(100);
                    break;
                case 5:
                    break;
                case 6:
                    driveInches(205, 0, 1);
                    setTimerAndAdvanceStep(3500);
                    break;
                case 7:
                    if(driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 8:
                    if(DriverStation.getAlliance() == Alliance.Red) {
                        driveInches(20, 90, 1);
                    } else {
                        driveInches(-20, 90, 1);
                    }
                    setTimerAndAdvanceStep(3500);
                    break;
                case 9:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 10:
                    if(DriverStation.getAlliance() == Alliance.Red) {
                        driveInches(25, 90, 1);
                    } else {
                        driveInches(-25, 90, 1);
                    }
                    Intake.deploy();
                    Intake.run(1);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 11:
                    if(driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 12:
                    driveInches(20, 0, 0.9);
                    setTimerAndAdvanceStep(1250);
                    break;
                case 13:
                    if(driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 14:
                    if(DriverStation.getAlliance() == Alliance.Red) {
                        driveInches(-240, -12, 1);
                    } else {
                        driveInches(-240, 12, 1);
                    }
                    setTimerAndAdvanceStep(3500);
                break;
                case 15:
                    if(driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 16:
                    Intake.retract();
                    Intake.stop();
                    Intake.liveBottomIntake();
                    LiveBottom2.forwardBasic();
                    setTimerAndAdvanceStep(2500);
                    break;
                case 17:
                    break;
                case 18:
                    Intake.liveBottomIntakeStop();
                    LiveBottom2.stopBasic();
                    stop();
                    break;
            }
        }
    }

}

