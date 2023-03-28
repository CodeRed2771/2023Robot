package frc.robot;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
//robot is 31 in wide and each side is 15.5 (With Bumpers)
//bars are 3 in wide
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Arm.extenderPresets;
import frc.robot.Arm.shoulderPresets;
import frc.robot.AutoBaseClass.Position;
import frc.robot.Claw.ClawPresets;

public class AutoPlace2Wings extends AutoBaseClass{

    byte positionMultiplier = 1;
    static boolean isNegAngle = false;
    static boolean isAngled = false;

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
            SmartDashboard.putBoolean("Robot is Angled in Auto", isAngled);
            SmartDashboard.putBoolean("Robot is needs a negative angle in Auto", isNegAngle);
            switch (getCurrentStep()) {
                case 0:
                    isAngled = false;
                    isNegAngle = false;
                    Claw.stopClawTO();
                    Claw.setClawPosition(ClawPresets.CLOSE);
                    advanceStep();
                    break;
                case 1:
                    Arm.presetShoulder(shoulderPresets.PLACING_HIGH);
                    Arm.presetExtend(extenderPresets.HIGH);
                    setTimerAndAdvanceStep(1200);
                    break;
                case 2:
                    Claw.stopClawTO();
                    break;
                case 3:
                    Claw.setClawPosition(ClawPresets.OPEN);
                    setTimerAndAdvanceStep(1000);
                    break;
                case 4:
                    break;
                case 5:
                    Arm.presetExtend(extenderPresets.RETRACTED);
                    setTimerAndAdvanceStep(500);
                    break;
                case 6:
                    Intake.deploy();
                    Intake.run(1);
                    if(VisionPlacer.aprilTagID() == 1) {
                        driveInches(120, 13, .8);
                        isAngled = true;
                        isNegAngle = false;
                    }
                    else if(VisionPlacer.aprilTagID() == 8) {
                        driveInches(120, -13, .8);
                        isAngled =true;
                        isNegAngle = true;
                    } else {
                        driveInches(124, 0, .8);
                        isAngled =false;
                    }
                    SmartDashboard.putBoolean("The Robot Believes an Angled Drive is required", isAngled);
                    SmartDashboard.putBoolean("The Robot Beleives an Negative Angle is Required", isNegAngle);
                    setTimerAndAdvanceStep(4000);
                    break;
                case 7:
                    if(driveCompleted()){
                        advanceStep();
                    }
                    break;
                case 8:
                    Intake.retract();
                    Intake.stop();
                    if(isAngled) {
                        if(isNegAngle)
                            driveInches(-120, 13, .8);
                        else
                            driveInches(-120, -13, .8);
                    } else {
                        driveInches(-104, 0, .8);
                    }
                    setTimerAndAdvanceStep(4000);
                    break;
                case 9:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 10:
                    LiveBottom2.forwardBasic();
                    setTimerAndAdvanceStep(1500);
                    break;
                case 11:
                    break;
                case 12:
                    LiveBottom2.stopBasic();
                    stop();
                    break;
            }
        }
    }

}
