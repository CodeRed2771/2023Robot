package frc.robot;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Arm.extenderPresets;
import frc.robot.Arm.shoulderPresets;

public class AutoCPlace2 extends AutoBaseClass{//didn't change much

    double driveDistRedL = 0;
    double driveDistRedR = 0;
    double driveDistBlueL = 0;
    double driveDistBlueR = 0;
    int wheelAngleRedL = 0;
    int wheelAngleRedR = 0;
    int wheelAngleBlueL = 0;
    int wheelAngleBlueR = 0;
    int liftWait = 0;
    double forwardDrive = 0;

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
                    Arm.presetExtend(extenderPresets.HIGH);
                    setTimerAndAdvanceStep(liftWait);
                    break;
                case 1:
                    Claw.openClawTO();
                    setTimerAndAdvanceStep(1200);
                    break;
                case 2:
                    break;
                case 3:
                    Arm.presetExtend(extenderPresets.RETRACTED);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 5:
                    break;
                case 6:
                    if(getAlliance() == Alliance.Red && getAutoPosition() == Position.LEFT)
                        driveInches(driveDistRedL, wheelAngleRedL, 0.9);
                    if(getAlliance() == Alliance.Red && getAutoPosition() == Position.RIGHT)
                        driveInches(driveDistRedR, wheelAngleRedR, 0.9);
                    if(getAlliance() == Alliance.Blue && getAutoPosition() == Position.LEFT)
                        driveInches(driveDistBlueL, wheelAngleBlueL, 0.9);
                    if(getAlliance() == Alliance.Blue && getAutoPosition() == Position.RIGHT)
                        driveInches(driveDistBlueR, wheelAngleBlueR, 0.9);
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
                    driveInches(forwardDrive, 0, 0.5);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 9:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 10:
                    Intake.stop();
                    Intake.retract();
                    if(getAlliance() == Alliance.Red && getAutoPosition() == Position.LEFT)
                        driveInches(driveDistRedL, wheelAngleRedL, 0.9);
                    if(getAlliance() == Alliance.Red && getAutoPosition() == Position.RIGHT)
                        driveInches(driveDistRedR, wheelAngleRedR, 0.9);
                    if(getAlliance() == Alliance.Blue && getAutoPosition() == Position.LEFT)
                        driveInches(driveDistBlueL, wheelAngleBlueL, 0.9);
                    if(getAlliance() == Alliance.Blue && getAutoPosition() == Position.RIGHT)
                        driveInches(driveDistBlueR, wheelAngleBlueR, 0.9);
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
                    LiveBottom2.
                    stop();
                    break;
            }
        }
    }

}
