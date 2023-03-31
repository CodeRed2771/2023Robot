package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Arm.extenderPresets;
import frc.robot.Arm.shoulderPresets;
import frc.robot.Claw.ClawPresets;

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
                    Claw.closeClawTO();
                    setTimerAndAdvanceStep(1000);
                    break;
                case 1:
                    break;
                case 2:
                    Claw.stopClawTO();
                    Arm.presetShoulder(shoulderPresets.PLACING_HIGH);
                    Arm.presetExtend(extenderPresets.HIGH);
                    //Arm.presetExtend(extenderPresets.RETRACTED);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 3:
                    Claw.stopClawTO();
                    break;
                case 4:
                    setStep(8);
                    // Arm.presetLift(shoulderPresets.PLACING_HIGH);
                    // setTimerAndAdvanceStep(750);
                    break;
                case 5:
                    break;
                case 6:
                    // Arm.presetExtend(extenderPresets.HIGH);
                    // setTimerAndAdvanceStep(3500);
                    break;
                case 7:
                    break;
                case 8:
                    Claw.setClawPosition(ClawPresets.CLOSE);
                    setTimerAndAdvanceStep(1800);
                    break;
                case 9:
                    break;
                case 10:
                    Claw.stopClawTO();
                    Arm.presetExtend(extenderPresets.RETRACTED);
                    setTimerAndAdvanceStep(1000);
                    break;
                case 11:
                    break;
                case 12:
                    driveInches(156, 0, 0.8);
                    setTimerAndAdvanceStep(6000);
                    break;
                case 13:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 14:
                    stop();
                    break;
            }
        }
    }

}
