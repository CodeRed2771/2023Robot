package frc.robot;

import java.lang.ProcessBuilder.Redirect;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.VisionPlacer.LimelightOn;

public class AutoAprilTagAlign extends AutoBaseClass{
	
    double angle;
    int IDSwitch; 
    double xOffsest;
    double depthOffset;
    double adjustAngle;
    double adjustDistance;
    PlacePositions positions;
    final double PoleWidth = 29;
    final double CubeWidth = 21.5;
    final double HalfField = 30*12;
    final double PlacingDepth =  54;
    

    public void start(int IDSwitch, PlacePositions positions) {
		super.start();
        this.IDSwitch = IDSwitch;
        this.positions = positions;
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
                    advanceStep();
                    break;
                case 1:
                    turnDegrees(VisionPlacer.botPoseYaw(), .6);
                    SmartDashboard.putNumber("Angle for April Tag", VisionPlacer.botPoseYaw());
                    setTimerAndAdvanceStep(2000);
                    break;
                case 2:
                    if (turnCompleted()) {
                        advanceStep();
                    }
                    break;
                case 3:
                    
                    if(VisionPlacer.botPoseY() > 0) {
                        // depthOffset = VisionPlacer.botPoseY() - 10;
                        depthOffset = HalfField - VisionPlacer.botPoseY() - PlacingDepth;
                    } else if (VisionPlacer.botPoseY() < 0) {
                        depthOffset = HalfField + VisionPlacer.botPoseY() + PlacingDepth;
                    }
                    // SmartDashboard.putNumber("Depth Offset Raw", VisionPlacer.botPoseY());
                    SmartDashboard.putNumber("Depth Offset for the limelight", depthOffset);
                    

                //     if (positions == PlacePositions.LeftConeHigh || positions == PlacePositions.LeftConeLow || positions == PlacePositions.LeftConeNuetral) {
                //         if (VisionPlacer.camTran().translation.getX() > 0) {
                //             xOffsest =  InchesToTheLeft - VisionPlacer.camTran().translation.getX();
                //         } else if (VisionPlacer.botPose().translation.getX() < 0) {
                //             xOffsest = InchesToTheLeft + Math.abs(VisionPlacer.botPose().translation.getX());
                //         }
                //     } else if (positions == PlacePositions.RightConeHigh || positions == PlacePositions.RightConeLow || positions == PlacePositions.RightConeNuetral) {
                //         if (VisionPlacer.camTran().translation.getX() > 0) {
                //             xOffsest =  InchesToTheRight - VisionPlacer.camTran().translation.getX();
                //         } else if (VisionPlacer.botPose().translation.getX() < 0) {
                //             xOffsest = InchesToTheRight + VisionPlacer.botPose().translation.getX();
                //         }
                //     } else {
                //         if (VisionPlacer.camTran().translation.getX() > 0) {
                //             xOffsest =  -VisionPlacer.camTran().translation.getX();
                //         } else if (VisionPlacer.botPose().translation.getX() < 0) {
                //             xOffsest = Math.abs(VisionPlacer.botPose().translation.getX());
                //         }
                //     }
                //     adjustDistance = Math.sqrt(Math.pow(xOffsest, 2) + Math.pow(depthOffset, 2));
                //     adjustAngle = Math.atan(xOffsest/depthOffset);

                //     setTimerAndAdvanceStep(1000);
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