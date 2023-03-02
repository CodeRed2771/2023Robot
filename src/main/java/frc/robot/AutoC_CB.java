package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Arm.extenderPresets;
import frc.robot.Arm.shoulderPresets;

public class AutoC_CB extends AutoBaseClass{
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
                    setTimerAndAdvanceStep(250);
                    break;
                case 1:
                    break;
                case 2:
                    driveInches(250, 0, 0.8);
                    setTimerAndAdvanceStep(6000);
                case 3:
                    if(driveCompleted())
                        advanceStep();
                case 4:
                    driveInches(150, 0, 0.8);
                    setTimerAndAdvanceStep(6000);
                case 5:
                    if(driveCompleted())
                        advanceStep();
                case 6:
                    driveInches(-50, 0, 0.5);
                    setTimerAndAdvanceStep(6000);
                case 7:
                        if(driveCompleted())
                            advanceStep();
                case 8:
                    stop();
                    break;
            }
        }
    }

}
