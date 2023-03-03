package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Arm.extenderPresets;
import frc.robot.Arm.shoulderPresets;

public class AutoCP1CB extends AutoBaseClass{
    private int counter = 0;
    private int dacount = 0;
    private int stopCount = 0;
    private int timeToNextDrive = 0;
    boolean test= true;
    private final static double driveRatio = .5;

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
                    Claw.closeClawA();
                    setTimerAndAdvanceStep(2000);
                    break;
                case 1:
                    Claw.tickAuto();
                    break;
                case 2:
                    Claw.flip();
                    Arm.presetExtend(extenderPresets.RETRACTED);
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
                    Arm.presetExtend(extenderPresets.HIGH);
                    setTimerAndAdvanceStep(1500);
                case 7:
                    break;
                case 8:
                    Claw.openClawA();
                    setTimerAndAdvanceStep(1500);
                case 9:
                    break;
                case 10:
                    Claw.closeClawA();
                    Arm.presetExtend(extenderPresets.RETRACTED);
                    setTimerAndAdvanceStep(2000);
                case 11:
                    break;
                case 12:
                    driveInches(250, 0, 0.8);
                    setTimerAndAdvanceStep(6000);
                case 13:
                    if(driveCompleted())
                        advanceStep();
                case 14:
                    driveInches(150, 0, 0.8);
                    setTimerAndAdvanceStep(6000);
                case 15:
                    if(driveCompleted())
                        advanceStep();
                case 16:
                    driveInches(-50, 0, 0.5);
                    setTimerAndAdvanceStep(6000);
                case 17:
                        if(driveCompleted())
                            advanceStep();
                case 18:
                    stop();
                    break;
            }
        }
    }

}
