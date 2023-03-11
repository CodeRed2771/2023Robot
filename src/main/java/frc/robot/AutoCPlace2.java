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
                    Arm.presetLift(shoulderPresets.PLACING_LOW_CUBE);
                    setTimerAndAdvanceStep(300);
                    break;
                case 1:
                    Arm.presetExtend(extenderPresets.LOW_CUBE);
                    setTimerAndAdvanceStep(1250);
                    break;
                case 2:
                    if(getStepTimeRemainingInMilliSeconds() < 400)
                        Claw.openClawTO();
                case 3:
                    Claw.openClawTO();
                    setTimerAndAdvanceStep(1250);
                    break;
                case 4:
                    break;
                case 5:
                    Arm.presetExtend(extenderPresets.RETRACTED);
                    setTimerAndAdvanceStep(1250);
                    break;
                case 6:
                    break;
                case 7:
                    double cPlace2DriveIn = Math.sqrt(64+46656)-30;
                    double cPlace2DriveDeg = Math.atan((8/216));
                    driveInches(cPlace2DriveIn, cPlace2DriveDeg, 1);//diagonal slide, pythagorean theorem-robot length, tangent
                    setTimerAndAdvanceStep(4000);
                    break;
                case 8:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 9:
                    Intake.deploy();
                    Intake.run(1);
                    driveInches(12, 0, 0.5);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 10:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 11:
                    //driveInches(Math.sqrt((230*230)+(24*24)), Math.atan(230/24), 1);
                    driveInches(-20, 0, 1);
                    Intake.stop();
                    Intake.retract();
                    setTimerAndAdvanceStep(4000);
                    break;
                case 12:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 13:
                    LiveBottom.backward();
                    setTimerAndAdvanceStep(3000);
                    break;
                case 14:
                    break;
                case 15:
                    LiveBottom.off();
                    stop();
                    break;
            }
        }
    }

}
