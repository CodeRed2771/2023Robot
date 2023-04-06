package frc.robot;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
//robot is 31 in wide and each side is 15.5 (With Bumpers)
//bars are 3 in wide
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Arm.extenderPresets;
import frc.robot.Arm.shoulderPresets;
import frc.robot.AutoBaseClass.Position;

public class AutoPlace3VROOOM extends AutoBaseClass{

    byte positionMultiplier = 1;

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
                    LiveBottom2.backwardBasic();
                    setTimerAndAdvanceStep(1250);
                    break;
                case 1:
                if(DriverStation.getAlliance() == Alliance.Blue)
                    positionMultiplier = -1;
                else 
                    positionMultiplier = 1;
                if(robotPosition() == Position.CENTER)
                    setStep(72);
                else
                    advanceStep();
                break;
                case 2:
                    Arm.presetExtend(extenderPresets.GATE_MODE);
                    Arm.presetShoulder(shoulderPresets.GATE_MODE);
                    driveInches(200, 0, 1);
                    setTimerAndAdvanceStep(1000);
                    break;
                case 3:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 6:
                    Intake.deploy();
                    Intake.run(1);
                    driveInches(28, 0, 0.65);
                    setTimerAndAdvanceStep(2250);
                    break;
                case 8:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 10:
                    Intake.retract();
                    driveInches(-220, 0, 1);
                    setTimerAndAdvanceStep(3000);
                    break;
                case 11:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 12:
                    Intake.stop();
                    Arm.presetShoulder(shoulderPresets.PICKUP_BACK_FEEDER_STATION);
                    Arm.presetExtend(extenderPresets.RETRACTED);
                    driveInches(-18*positionMultiplier, 90, 0.8);
                    setTimerAndAdvanceStep(1000);
                    break;
                case 13:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 14:
                    driveInches(-6, 0, 0.5);
                    setTimerAndAdvanceStep(1000);
                    break;
                case 15:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 16:
                    LiveBottom2.forwardBasic();
                    setTimerAndAdvanceStep(1250);
                    break;
                case 17:
                    break;
                case 18:
                    driveInches(18*positionMultiplier, 90, 0.8);
                    setTimerAndAdvanceStep(1000);
                    break;
                case 19:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 20:
                    LiveBottom2.stopBasic();
                    Arm.presetExtend(extenderPresets.GATE_MODE);
                    Arm.presetShoulder(shoulderPresets.GATE_MODE);
                    driveInches(216, 0, 1);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 21:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 22:
                    Intake.deploy();
                    Intake.run(1);
                    driveInches(-48*positionMultiplier, 90, 1);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 23:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 24:
                    driveInches(12, 0, 0.2);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 25:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 26:
                    Intake.retract();
                    driveInches(48*positionMultiplier, 90, 1);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 27:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 28:
                    driveInches(-224, 0, 1);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 29:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 30:
                    driveInches(10*positionMultiplier, 90, 0.5);
                    setTimerAndAdvanceStep(1250);
                    break;
                case 31:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 32:
                    driveInches(-4, 0, 0.5);
                    setTimerAndAdvanceStep(1250);
                    break;
                case 33:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 34:
                    LiveBottom2.forwardBasic();
                    setTimerAndAdvanceStep(1500);
                    break;
                case 35:
                    break;
                case 36:
                    setStep(37);
                    break;
                case 37:
                    LiveBottom2.stopBasic();
                    stop();
                    break;
            }
        }
    }

}
