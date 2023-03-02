package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Arm.extenderPresets;
import frc.robot.Arm.shoulderPresets;
//probably just want to use Caleb's
public class AutoWings extends AutoBaseClass{
    int elementsPlaced = 0;

	public void start() {
		super.start();
	}
    
    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        if(isRunning()) {
            SmartDashboard.putNumber("Auto Step", getCurrentStep());
            switch (getCurrentStep()) {
                case 0:
                    Claw.closeClawA();
                    setTimerAndAdvanceStep(250);
                    break;
                case 1:
                    break;
                case 2:
                    Arm.presetExtend(extenderPresets.RETRACTED);
                    setTimerAndAdvanceStep(1250);
                    break;
                case 3:
                    break;
                case 4:
                    Claw.closeClawA();
                    Arm.presetLift(shoulderPresets.PLACING_HIGH);
                    setTimerAndAdvanceStep(750);
                    break;
                case 5:
                    break;
                case 6:
                    Arm.presetExtend(extenderPresets.HIGH);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 7:
                    break;
                case 8:
                    Claw.closeClawA();
                    setTimerAndAdvanceStep(750);
                    break;
                case 9:
                    break;
                case 10:
                    Claw.closeClawA();
                    Intake.deploy();
                    Arm.presetExtend(extenderPresets.RETRACTED);
                    Arm.presetLift(shoulderPresets.PICKUP_CUBE);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 11:
                    break;
                case 12:
                    Intake.run(1);
                    driveInches(240, 0, 0.8);
                    setTimerAndAdvanceStep(4000);
                    break;
                case 13:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 14:
                    driveInches(-48*(elementsPlaced-1)+39, 90, 1);//39 is probably wrong number
                    setTimerAndAdvanceStep(elementsPlaced*750);
                    break;
                case 15:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 16:
                    driveInches(10, 0, 1);
                    setTimerAndAdvanceStep(750);
                    break;
                case 17:
                    if(driveCompleted())
                        advanceStep();
                break;
                case 18:
                    elementsPlaced++;
                    if(placeNumInAuto >= elementsPlaced)
                        setStep(25);
                    else {
                        LiveBottom.backward();
                        driveInches(-250, 0, 1);
                        setTimerAndAdvanceStep(4000);
                        Claw.closeClawA();
                    }
                    break;
                case 19:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 20:
                    driveInches(-48*(elementsPlaced-1)+39, 90, 1);//39 is probably wrong number
                    setTimerAndAdvanceStep(elementsPlaced*750);
                    break;
                case 21:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 22:
                    driveInches(-250, 0, 1);
                    setTimerAndAdvanceStep(4000);
                case 23:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 24:
                    setStep(4);
                    break;
                case 25:
                    stop();
                    break;
            }
        }
    }

}
