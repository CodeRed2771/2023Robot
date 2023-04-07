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
                    // Claw.resetToCloseClaw();
                    // Claw.stopClawTO();
                    setStep(2);
                    break;
                case 1:
                    break;
                case 2:
                    // Claw.setClawPosition(ClawPresets.OPEN);
                    // Claw2.open();
                    Arm.presetShoulder(shoulderPresets.PLACING_HIGH);
                    // Arm.presetExtend(extenderPresets.HIGH);
                    setTimerAndAdvanceStep(1000);
                    break;
                case 3:
                    // Claw.stopClawTO();
                    break;
                case 4:
                    Arm.presetExtend(extenderPresets.HIGH);
                    setTimerAndAdvanceStep(1700);
                    break;
                case 5:
                    break;
                case 6:
                    Claw2.open();
                    setTimerAndAdvanceStep(100);
                    break;
                case 7:
                    break;
                case 8:
                    setStep(10);
                    // Claw.setClawPosition(ClawPresets.CLOSE);
                    // setTimerAndAdvanceStep(1000);
                    break;
                case 9:
                    break;
                case 10:
                    // Claw.stopClawTO();
                    Arm.presetExtend(extenderPresets.RETRACTED);
                    setTimerAndAdvanceStep(1000);
                    break;
                case 11:
                    break;
                case 12:
                    driveInches(156, 0, 1);
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
