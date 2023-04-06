package frc.robot;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Arm.extenderPresets;
import frc.robot.Arm.shoulderPresets;

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
                    //reset the arm
                    setTimerAndAdvanceStep(850);
                    break;
                case 1:
                    break;
                case 2:
                    Arm.presetShoulder(shoulderPresets.PLACING_HIGH);
                    Arm.presetExtend(extenderPresets.HIGH);
                    setTimerAndAdvanceStep(3500);
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
                    driveInches(200, 0,1);
                    setTimerAndAdvanceStep(4000);
                    break;
                case 7:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 8:
                    if(getAlliance() == Alliance.Blue)
                        driveInches(25, 90, 0.7);
                    else
                        driveInches(-25, 90, 0.7);
                    Intake.deploy();
                    Intake.run(1);
                    Arm.presetShoulder(shoulderPresets.PICKUP_CONE);
                    Arm.presetExtend(extenderPresets.PICKUP);
                    setTimerAndAdvanceStep(600);
                    break;
                case 9:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 10:
                    if(getAlliance() == Alliance.Blue)
                        driveInches(-19, 90, 0.7);
                    if(getAlliance() == Alliance.Blue)
                        driveInches(-19, 90, 0.7);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 11:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 12:
                    driveInches(24, 0, 0.7);
                    setTimerAndAdvanceStep(1750);
                    break;
                case 13:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 14:
                    Intake.retract();
                    LiveBottom2.forwardBasic();
                    driveInches(-212, 0, 1);
                    setTimerAndAdvanceStep(4000);
                    break;
                case 15:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 16:
                    Claw2.closeClaw;
                    driveInches(-18, 0, 0.65);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 17:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 18:
                    Arm.presetExtend(extenderPresets.HIGH);
                    Arm.presetShoulder(shoulderPresets.PLACING_HIGH);
                    setTimerAndAdvanceStep(2500);
                    break;
                case 19:
                    if(Arm.extensionCompleted())
                        advanceStep();
                    break;
                case 20:
                    Claw2.openClaw;
                    setTimerAndAdvanceStep(400);
                    break;
                case 21:
                    break;
                case 22:
                    Arm.presetExtend(extenderPresets.RETRACTED);
                    setTimerAndAdvanceStep(2500);
                    break;
                case 23:
                    break;
                case 24:
                    LiveBottom2.stopBasic();
                    stop();
                    break;
            }
        }
    }

}
