package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Arm.bistablePresets;
import frc.robot.Arm.shoulderPresets;

public class AutoCPlace1 extends AutoBaseClass{

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
                    Claw.closeClaw();
                    setTimerAndAdvanceStep(250);
                    break;
                case 1:
                    break;
                case 2:
                    Arm.presetExtend(bistablePresets.RETRACTED);
                    setTimerAndAdvanceStep(1250);
                    break;
                case 3:
                    break;
                case 4:
                    Arm.presetLift(shoulderPresets.PLACING_HIGH);
                    setTimerAndAdvanceStep(750);
                    break;
                case 5:
                    break;
                case 6:
                    Arm.presetExtend(bistablePresets.HIGH);
                    setTimerAndAdvanceStep(1500);
                case 7:
                    break;
                case 8:
                    Claw.openClaw();
                    setTimerAndAdvanceStep(750);
                case 9:
                    break;
                case 10:
                    Claw.closeClaw();
                    Arm.presetExtend(bistablePresets.RETRACTED);
                    setTimerAndAdvanceStep(2000);
                case 11:
                    break;
                case 12:
                    driveInches(250, 0, 0.8);
                case 13:
                    stop();
                    break;
            }
        }
    }

}
