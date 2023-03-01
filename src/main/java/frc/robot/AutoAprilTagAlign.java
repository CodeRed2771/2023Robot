package frc.robot;

import java.lang.ProcessBuilder.Redirect;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.VisionPlacer.LimelightOn;

public class AutoAprilTagAlign extends AutoBaseClass{
	
    int IDSwitch; 
    PlacePositions positions;
    double[] data = new double[6];
    double lenghtDisplacement = 0;
    double angle = 0;
    // final static double PoleWideWidth = 29;
    // final static double PoleNarrowWidth = 24;
    // final static double CubeWidth = 21.5;
    // final static double PlacingDepth =  54;
    // final static double LoadingZoneWidth = 10;
    
    final double targetPole = 20.19;
    double ID;

    final double metersToInches = 39.3701;
    
    final int YAW = 5;
    final int PITCH  = 4; 
    final int ROLL = 3;
    final int Z = 2; 
    final int WIDTH =  1;
    final int LENGTH = 0;
    
    

    public void start() {
		super.start();
        angle = 0;
	}

    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        if (isRunning()) {
            SmartDashboard.putNumber("Auto April Tag Step", getCurrentStep());
            switch(getCurrentStep()) {
                case 0:
                    VisionPlacer.setAprilTagPipeline();
                    VisionPlacer.setLED(LimelightOn.On);
                    data =  VisionPlacer.botpose_wpiblue.averagedData();
                    advanceStep();
                    break;
                case 1:
                    angle = RobotGyro.getClosestTurn(0);
                    if (angle > 1 || angle < -1) {
                        turnDegrees(angle, .3);
                        setTimerAndAdvanceStep(4000);
                    } else {
                       setStep(3);
                    }
                    break;
                case 2:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 3:
                    if (VisionPlacer.seesTarget()) {
                        data[WIDTH] = data[WIDTH] * metersToInches;
                        ID = VisionPlacer.aprilTagID();
                        if (ID == 1|| ID == 2 || ID == 3 || ID == 4) {
                            lenghtDisplacement =  data[WIDTH] - targetPole;
                        } else {
                            lenghtDisplacement = (targetPole - data[WIDTH]);
                        }
                        
                        driveInches(lenghtDisplacement, 90, .3);
                        setTimerAndAdvanceStep(3000);
                    } else {
                        setStep(5);
                    }
                    break;
                case 4:
                    if(driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 5:
                    VisionPlacer.setLED(LimelightOn.Off);
                    stop();
                    break;
            }
        }    
    }
}