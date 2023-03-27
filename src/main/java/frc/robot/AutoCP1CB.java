package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Arm.extenderPresets;
import frc.robot.Arm.shoulderPresets;
import frc.robot.Claw.ClawPresets;

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
                    Claw.resetToCloseClaw();
                    setStep(2);
                    break;
                case 1:
                    break;
                case 2:
                    Arm.presetShoulder(shoulderPresets.PLACING_HIGH);
                    Arm.presetExtend(extenderPresets.HIGH);
                    setTimerAndAdvanceStep(1000);
                    break;
                case 3:
                    break;
                case 4:
                    // Claw.stopClawTO();
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
                    Claw.setClawPosition(ClawPresets.OPEN);
                    setTimerAndAdvanceStep(1000);
                    break;
                case 9:
                    break;
                case 10:
                    // Claw.stopClawTO();
                    Arm.presetExtend(extenderPresets.RETRACTED);
                    setTimerAndAdvanceStep(500);
                    break;
                case 11:
                    break;
                case 12:
                    driveInches(170, 0, 0.65);
                    setTimerAndAdvanceStep(4500);
                    break;
                case 13:
                    if(driveCompleted())
                        // advanceStep();
                        setStep(16);
                    break;
                case 14:
                    setStep(16);
                    //driveInches(20, 0, 0.6);
                    //setTimerAndAdvanceStep(3000);
                    break;
                case 15:
                    if(driveCompleted())
                        advanceStep(); 
                    break;
                case 16:
                    driveInches(-75, 0, 0.7);
                    setTimerAndAdvanceStep(3000);
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
