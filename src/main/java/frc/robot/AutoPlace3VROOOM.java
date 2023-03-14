package frc.robot;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
//robot is 31 in wide and each side is 15.5 (With Bumpers)
//bars are 3 in wide
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Arm.extenderPresets;
import frc.robot.Arm.shoulderPresets;

public class AutoPlace3VROOOM extends AutoBaseClass{
    int positionMultiplier = 0;
    int posRLOffset = 0;
    int posRROffset = 0;
    int posBLOffset = 0;
    int posBROffset = 0;

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
                case 0://eject preload and set position multiplier
                    if(DriverStation.getAlliance() == Alliance.Blue && robotPosition() == Position.LEFT)
                        positionMultiplier = -1;
                    if(DriverStation.getAlliance() == Alliance.Blue && robotPosition() == Position.RIGHT)
                        positionMultiplier = 1;
                    if(DriverStation.getAlliance() == Alliance.Red && robotPosition() == Position.LEFT)
                        positionMultiplier = 1;
                    if(DriverStation.getAlliance() == Alliance.Red && robotPosition() == Position.RIGHT)
                        positionMultiplier = -1;
                    LiveBottom.backward();
                    setTimerAndAdvanceStep(1250);
                    break;
                case 1:
                    break;
                case 2://slide to pickup position
                    driveInches(43.75*positionMultiplier, 90, 0.8);//TUNE LINE??
                    setTimerAndAdvanceStep(1000);
                    break;
                case 3:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 4://go to pickup
                    LiveBottom.off();
                    driveInches(216, 0, 1);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 5:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 6://run intake
                    Intake.run(1);
                    break;
                case 7://pickup
                    driveInches(12, 0, 0.2);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 8:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 9://make element ready to eject
                    //unneeded
                    break;
                case 10://stop intake and go to place
                    Intake.stop();
                    driveInches(-224, 0, 1);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 11:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 12://turn live bottom off and align for placing
                    driveInches(-8*positionMultiplier, 90, 0.8);//TUNE LINE
                    setTimerAndAdvanceStep(1000);
                    break;
                case 13:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 14://get ready for ejecting the elemnt
                    driveInches(-4, 0, 0.5);
                    setTimerAndAdvanceStep(1000);
                case 15:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 16://eject the element
                    LiveBottom.on();
                    setTimerAndAdvanceStep(750);
                    break;
                case 17:
                    break;
                case 18:
                    driveInches(8*positionMultiplier, 90, 0.8);//TUNE LINE
                    setTimerAndAdvanceStep(1000);
                    break;
                case 19:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 20://drive to 3rd elemnet
                    LiveBottom.off();
                    driveInches(216, 0, 1);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 21:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 22://start intake and align to pick up element
                    Intake.run(1);
                    driveInches(-48*positionMultiplier, 90, 1);//TUNE LINE??
                    setTimerAndAdvanceStep(1500);
                    break;
                case 23:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 24://pick up element
                    driveInches(12, 0, 0.2);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 25:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 26://stop intake, start live bottom, and drive to go back
                    Intake.stop();
                    driveInches(48*positionMultiplier, 90, 1);//TUNE LINE
                    setTimerAndAdvanceStep(1500);
                    break;
                case 27:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 28://drive back
                    driveInches(-224, 0, 1);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 29:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 30://turn live bottom off, align horizontally
                    driveInches(25.5*positionMultiplier, 90, 0.5);//TUNE LINE
                    setTimerAndAdvanceStep(1250);
                    break;
                case 31:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 32://drive back a lill' more
                    driveInches(-4, 0, 0.5);//TUNE LINE
                    setTimerAndAdvanceStep(1250);
                    break;
                case 33:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 34://eject elemnt
                    LiveBottom.on();
                    setTimerAndAdvanceStep(1500);
                    break;
                case 35:
                    break;
                case 36:
                    LiveBottom.off();
                    stop();
                    break;
            }
        }
    }

}
