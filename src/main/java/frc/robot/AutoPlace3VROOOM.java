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
                case 0://eject preload
                    LiveBottom2.backward();
                    setTimerAndAdvanceStep(1250);
                    break;
                case 1://set position stuff
                if(DriverStation.getAlliance() == Alliance.Blue)
                    positionMultiplier = -1;
                else 
                    positionMultiplier = 1;
                
                if(robotPosition() == Position.RIGHT && DriverStation.getAlliance() == Alliance.Blue)
                    setStep(37);
                if(robotPosition() == Position.RIGHT && DriverStation.getAlliance() == Alliance.Blue)
                    setStep(37);
                else if(robotPosition() == Position.CENTER)
                    setStep(72);
                else
                    advanceStep();
                break;
                    
                case 2://slide to pickup position
                    driveInches(6*positionMultiplier, 90, 0.8);//TUNE LINE
                    setTimerAndAdvanceStep(1000);
                    break;
                case 3:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 4://go to pickup
                    LiveBottom2.forward();
                    driveInches(216, 0, 1);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 5:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 6://run intake
                    Intake.deploy();
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
                    Intake.retract();
                    driveInches(-224, 0, 1);
                    setTimerAndAdvanceStep(3000);
                    break;
                case 11:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 12://align for placing
                    driveInches(-18*positionMultiplier, 90, 0.8);//TUNE LINE
                    setTimerAndAdvanceStep(1000);
                    break;
                case 13:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 14://get ready for ejecting the element
                    driveInches(-6, 0, 0.5);
                    setTimerAndAdvanceStep(1000);
                    break;
                case 15:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 16://eject the element
                    LiveBottom2.backward();
                    setTimerAndAdvanceStep(750);
                    break;
                case 17:
                    break;
                case 18://drive back to drive to new element
                    driveInches(18*positionMultiplier, 90, 0.8);//TUNE LINE
                    setTimerAndAdvanceStep(1000);
                    break;
                case 19:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 20://drive to 3rd elemnet
                    LiveBottom2.forward();
                    driveInches(216, 0, 1);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 21:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 22://start intake and align to pick up element
                    Intake.deploy();
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
                    driveInches(-224*positionMultiplier, 0, 1);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 29:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 30://turn live bottom off, align horizontally
                    driveInches(40*positionMultiplier, 90, 0.5);//TUNE LINE
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
                    LiveBottom2.backward();
                    setTimerAndAdvanceStep(1500);
                    break;
                case 35:
                    break;
                case 36:
                    setStep(72);
                    break;
                case 37://slide to pickup position
                    driveInches(6*positionMultiplier, 90, 0.8);//TUNE LINE
                    setTimerAndAdvanceStep(1000);
                    break;
                case 38:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 39://go to pickup
                    LiveBottom2.forward();
                    driveInches(216, 0, 1);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 40:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 41://run intake
                    Intake.deploy();
                    Intake.run(1);
                    break;
                case 42://pickup
                    driveInches(12, 0, 0.2);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 43:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 44://make element ready to eject
                    //unneeded
                    break;
                case 45://stop intake and go to place
                    Intake.stop();
                    Intake.retract();
                    driveInches(-224, 0, 1);
                    setTimerAndAdvanceStep(3000);
                    break;
                case 46:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 47://align for placing
                    driveInches(-18*positionMultiplier, 90, 0.8);//TUNE LINE
                    setTimerAndAdvanceStep(1000);
                    break;
                case 48:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 49://get ready for ejecting the element
                    driveInches(-6, 0, 0.5);
                    setTimerAndAdvanceStep(1000);
                    break;
                case 50:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 51://eject the element
                    LiveBottom2.backward();
                    setTimerAndAdvanceStep(750);
                    break;
                case 52:
                    break;
                case 53://drive back to drive to new element
                    driveInches(18*positionMultiplier, 90, 0.8);//TUNE LINE
                    setTimerAndAdvanceStep(1000);
                    break;
                case 54:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 55://drive to 3rd elemnet
                    LiveBottom2.forward();
                    driveInches(216, 0, 1);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 56:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 57://start intake and align to pick up element
                    Intake.deploy();
                    Intake.run(1);
                    driveInches(-48*positionMultiplier, 90, 1);//TUNE LINE??
                    setTimerAndAdvanceStep(1500);
                    break;
                case 58:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 59://pick up element
                    driveInches(12, 0, 0.2);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 60:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 61://stop intake, start live bottom, and drive to go back
                    Intake.stop();
                    driveInches(48*positionMultiplier, 90, 1);//TUNE LINE
                    setTimerAndAdvanceStep(1500);
                    break;
                case 62:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 63://drive back
                    driveInches(-224*positionMultiplier, 0, 1);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 64:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 65://turn live bottom off, align horizontally
                    driveInches(40*positionMultiplier, 90, 0.5);//TUNE LINE
                    setTimerAndAdvanceStep(1250);
                    break;
                case 66:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 67://drive back a lill' more
                    driveInches(-4, 0, 0.5);//TUNE LINE
                    setTimerAndAdvanceStep(1250);
                    break;
                case 68:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 69://eject elemnt
                    LiveBottom2.backward();
                    setTimerAndAdvanceStep(1500);
                    break;
                case 70:
                    break;
                case 71:
                    advanceStep();
                    break;
                case 72:
                    LiveBottom2.forward();
                    stop();
                    break;
            }
        }
    }

}
