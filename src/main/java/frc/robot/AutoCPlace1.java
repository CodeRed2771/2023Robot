package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Arm.bistablePresets;
import frc.robot.Arm.shoulderPresets;

public class AutoCommunity extends AutoBaseClass{

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
                    Arm.presetLift(shoulderPresets.PLACING);
                    setTimerAndAdvanceStep(750);
                    break;
                case 5:
                    break;
                case 6:
                    Arm.presetExtend(bistablePresets.HIGH);
                    
                case 9999:
                    stop();
                    break;
            }
        }
    }

}
