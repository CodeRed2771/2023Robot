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
    private static AutoBaseClass balance = new AutoClimbAndBalance();

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
                    Claw.flip();
                    Arm.presetLift(shoulderPresets.PLACING_HIGH);
                    Arm.presetExtend(extenderPresets.HIGH);
                    setTimerAndAdvanceStep(3500);
                    break;
                case 3:
                    break;
                case 4:
                    setStep(8);
                    // Arm.presetLift(shoulderPresets.PLACING_HIGH);
                    // setTimerAndAdvanceStep(100);
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
                    Claw.openClawTO();
                    setTimerAndAdvanceStep(1800);
                    break;
                case 9:
                    break;
                case 10:
                    Claw.stopClawTO();
                    Arm.presetExtend(extenderPresets.RETRACTED);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 11:
                    break;
                case 12:
                    driveInches(84, 0, 0.75);
                    setTimerAndAdvanceStep(2500);
                    break;
                case 13:
                    if(driveCompleted())
                        // advanceStep();
                        setStep(18);
                    break;
                case 14:
                    setStep(18);
                    //driveInches(20, 0, 0.6);
                    //setTimerAndAdvanceStep(3000);
                    break;
                case 15:
                    if(driveCompleted())
                        advanceStep(); 
                    break;
                case 16:
                    //driveInches(-20, 0, 0.6);
                    setStep(18);
                    // setTimerAndAdvanceStep(50);
                    break;
                case 17:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 18:
                    balance.start();
                    setTimerAndAdvanceStep(6000);
                    break;
                case 19:
                    balance.tick();
                    break;
                case 20:
                    stop();
                    break;
            }
        }
    }
}
