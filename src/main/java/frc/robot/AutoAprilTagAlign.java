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
    final double InchesToTheLeft = 10;
    final double InchesToTheRight = -10;

    public void start(int IDSwitch, PlacePositions positions) {
		super.start();
        this.IDSwitch = IDSwitch;
        this.positions = positions;
	}
    
    public void stop() {
        super.stop();
    }

    private static double turnTo180(double angle) {
        double adjustedAngle = 0;
        if (angle > 45) {
            adjustedAngle = angle - 45;
        } else if (angle < 45) {
            adjustedAngle = 45 - angle;
        }
        return adjustedAngle;
    }

    @Override
    public void tick() {
        if (isRunning()) {
            SmartDashboard.putNumber("Auto April Tag Step", getCurrentStep());
            // DriveAuto.tick();
            switch(getCurrentStep()) {
                case 0:
                    VisionPlacer.setAprilTagPipeline();
                    VisionPlacer.setLED(LimelightOn.On);
                    // angle = turnTo180(VisionPlacer.botPose().rotation.getZ());
                    advanceStep();
                    break;
                case 1:
                    turnDegrees(VisionPlacer.botPoseYaw(), .4);
                    SmartDashboard.putNumber("Angle for April Tag", VisionPlacer.botPosePitch());
                    setTimerAndAdvanceStep(2000);
                    break;
                case 2:
                    if (driveCompleted()) {
                        // advanceStep();
                        stop();
                    }
                    break;
                case 3:
                    
                    if(VisionPlacer.botPoseY() > 0) {
                        depthOffset = VisionPlacer.botPoseY() - 2;
                    } else if (VisionPlacer.botPoseY() < 0) {
                        depthOffset = VisionPlacer.botPoseY() + 2;
                    }
                    
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

                //     driveInches(adjustDistance, adjustAngle, .4);
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