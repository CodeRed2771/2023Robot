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
                    setTimerAndAdvanceStep(500);
                    break;
                case 11:
                    break;
                case 12:
                    driveInches(170, 0, 0.65);
                    setTimerAndAdvanceStep(4000);
                    break;
                case 13:
                    if(driveCompleted())
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
