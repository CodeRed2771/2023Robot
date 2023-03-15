package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Arm.extenderPresets;
import frc.robot.Arm.shoulderPresets;

public class AutoAprilTag2Elements extends AutoBaseClass {
    public void start() {
        super.start();
    }
    public void stop() {
        super.stop();
    }
    AutoBaseClass align = new AutoAprilTagAlign();
    @Override
    public void tick() {
        if(isRunning()) {
            SmartDashboard.putNumber("Auto Step", getCurrentStep());
            switch (getCurrentStep()) {
                case 0: 
                    Arm.presetShoulder(shoulderPresets.PLACING_HIGH);
                    setTimerAndAdvanceStep(1000);
                    break;
                case 2:
                    break;
                case 3:
                    Arm.presetExtend(extenderPresets.HIGH);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 4:
                    break;
                case 5:
                    Claw.openClawA();
                    setTimerAndAdvanceStep(1000);
                    break;
                case 6: 
                    break;
                case 7: 
                    Claw.closeClawA();
                    Intake.deploy();
                    Arm.presetExtend(extenderPresets.RETRACTED);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 8:
                    break;
                case 9:
                    driveInches(120, 0, 1);
                    setTimerAndAdvanceStep(3000);
                    break;
                case 10: 
                    if(driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 11:
                    Intake.run(1);
                    driveInches(120, 20, 1);
                    setTimerAndAdvanceStep(3000);
                    break;
                case 12: 
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 13: 
                    Intake.stop();
                    align.start();
                    align.tick();
                    setTimerAndAdvanceStep(5100);
                    break;
                case 14:
                    break;
                case 15:
                    driveInches(240, 0, 1);
                    setTimerAndAdvanceStep(4000);
                    break;
                case 16:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 17:
                    LiveBottom.backward();
                    setTimerAndAdvanceStep(1500);
                    break;
                case 18:
                    break;
                case 19:
                    stop();
                    break;
                    
            }
        }
    }
    
}
