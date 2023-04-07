package frc.robot;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Arm.extenderPresets;
import frc.robot.Arm.shoulderPresets;
import frc.robot.Claw.ClawPresets;;

public class AutoCPlace2 extends AutoBaseClass{//didn't change much

    public enum p2Positions {
        BUMP, NON_BUMP
    }

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
                    Arm.presetShoulder(shoulderPresets.PLACING_HIGH);
                    setTimerAndAdvanceStep(1000);
                    break;
                case 1:
                    if(Arm.shoulderMoveCompleted())
                        advanceStep();
                    break;
                case 2:
                    Arm.presetExtend(extenderPresets.HIGH);
                    setTimerAndAdvanceStep(1700);
                    break;
                case 3:
                    if(Arm.extensionCompleted())
                        advanceStep();
                    break;
                case 4:
                    Claw2.open();
                    setTimerAndAdvanceStep(150);
                    break;
                case 5:
                    break;
                case 6:
                    Arm.presetExtend(extenderPresets.RETRACTED);
                    setTimerAndAdvanceStep(750);
                    break;
                case 7:
                    break;
                case 8:
                    driveInches(228, 0,1);
                    setTimerAndAdvanceStep(2500);
                    break;
                case 9:
                    break;
                case 10:
                    Intake.deploy();
                    Arm.presetExtend(extenderPresets.GATE_MODE);
                    Arm.presetShoulder(shoulderPresets.GATE_MODE);
                    Intake.run(1);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 11:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 12:
                    Intake.retract();
                    Intake.stop();
                    Arm.presetShoulder(shoulderPresets.PLACING_HIGH);
                    Arm.presetExtend(extenderPresets.RETRACTED);
                    driveInches(-230, 0, 1);
                    setTimerAndAdvanceStep(4200);
                    break;
                case 13:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 14:
                    LiveBottom2.forwardBasic();
                    setTimerAndAdvanceStep(3000);
                    break;
                case 15:
                    break;
                case 16:
                    Intake.stop();
                    LiveBottom2.stopBasic();
                    stop();
                    break;
            }
        }
    }

}
