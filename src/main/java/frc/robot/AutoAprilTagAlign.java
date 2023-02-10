package frc.robot;

import java.lang.ProcessBuilder.Redirect;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.VisionPlacer.LimelightOn;

public class AutoAprilTagAlign extends AutoBaseClass{
	
    double angle;
    int IDSwitch; 
    double xOffsest;
    static double centerOffset;
    static double depthOffset;
    static double positionOffsets;
    double adjustAngle;
    double adjustDistance;
    PlacePositions positions;
    final static double PoleWideWidth = 29;
    final static double PoleNarrowWidth = 29;
    final static double CubeWidth = 21.5;
    final static double HalfLengthField = 30*12;
    final static double PlacingDepth =  54;
    final static double LoadingZoneWidt = 10;
    
    

    public void start(int IDSwitch, PlacePositions positions) {
		super.start();
        this.IDSwitch = IDSwitch;
        this.positions = positions;
	}
    
    public void stop() {
        super.stop();
    }

    public static double relativePosition(double ID) {
        double relativePosition;
        if (ID == 1 || ID == 6) {
            relativePosition = PoleWideWidth + CubeWidth*.5;
        } else if (ID == 2 || ID == 7){
            relativePosition = 0;
        } else if (ID == 3 || ID == 8) {
            relativePosition = -LoadingZoneWidt -PoleWideWidth - CubeWidth*.5;
        } else {
            relativePosition = 0;
        }
        return relativePosition;
    }

    @Override
    public void tick() {
        if (isRunning()) {
            SmartDashboard.putNumber("Auto April Tag Step", getCurrentStep());
            SmartDashboard.putNumber("Angle for April Tag", VisionPlacer.botPoseYaw());
            SmartDashboard.putNumber("Depth Offset Raw", VisionPlacer.botPoseLength());
            SmartDashboard.putNumber("Depth Offset for the limelight", depthOffset);
            SmartDashboard.putNumber("Center Offset April Tag", centerOffset);
            switch(getCurrentStep()) {
                case 0:
                    VisionPlacer.setAprilTagPipeline();
                    VisionPlacer.setLED(LimelightOn.On);
                    advanceStep();
                    break;
                case 1:
                    if (VisionPlacer.botPoseYaw() > 1 || VisionPlacer.botPoseYaw() < -1) {
                        turnDegrees(VisionPlacer.botPoseYaw(), .6);
                        setTimerAndAdvanceStep(2000);
                    } else {
                        setStep(3);
                    }
                    break;
                case 2:
                    if (turnCompleted()) {
                        advanceStep();
                    }
                    break;
                case 3:
                    if (depthOffset > 0) {
                        depthOffset = VisionPlacer.botPoseLength() - PlacingDepth;
                    } else {
                        depthOffset = VisionPlacer.botPoseLength() + PlacingDepth;
                    }
                    centerOffset = depthOffset/Math.tan(VisionPlacer.getXAngleOffset());
                    stop();
                    break;
                case 4:
                    if(driveCompleted()) {
                        advanceStep();
                    }
                    break;

            }
        }    
    }
}