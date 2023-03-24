package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Arm.extenderPresets;
import frc.robot.Arm.shoulderPresets;

public class AutoCPlace2 extends AutoBaseClass{//didn't change much

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
                    Arm.presetShoulder(shoulderPresets.PLACING_LOW);
                    setTimerAndAdvanceStep(300);
                    break;
                case 1:
                    Arm.presetExtend(extenderPresets.LOW);
                    setTimerAndAdvanceStep(1250);
                    break;
                case 2:
                    Claw.flip();
                    setTimerAndAdvanceStep(1250);
                    break;
                case 3:
                    break;
                case 4:
                    Arm.presetExtend(extenderPresets.RETRACTED);
                    setTimerAndAdvanceStep(1250);
                    break;
                case 5:
                    break;
                case 6:
                    //driveInches(Math.sqrt(64+46656), Math.atan((8/216)), 1);
                    driveInches((18*12)+8,0,1);
                    setTimerAndAdvanceStep(4000);
                    break;
                case 7:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 8:
                    setStep(14);
                    Intake.deploy();
                    Intake.run(1);
                    driveInches(12, 0, 0.5);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 9:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 10:
                    driveInches(Math.sqrt((230*230)+(24*24)), Math.atan(230/24), 1);
                    Intake.stop();
                    Intake.retract();
                    setTimerAndAdvanceStep(4000);
                    break;
                case 11:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 12:
                    //LiveBottom.backward();
                    setTimerAndAdvanceStep(3000);
                    break;
                case 13:
                    break;
                case 14:
                    // LiveBottom.off();
                    stop();
                    break;
            }
        }
    }

}
