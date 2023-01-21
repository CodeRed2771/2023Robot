package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.VisionPlacer.LimelightOn;

public class AutoAprilTagAlign extends AutoBaseClass{
	
    double angle;
    boolean IDSwitch; 
    double xOffsest;
    double depthOffset;
    double adjustAngle;
    double adjustDistance;

    public void start(boolean IDSwitch) {
		super.start();
        this.IDSwitch = IDSwitch;
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
                    angle = 180 - RobotGyro.getRelativeAngle();
                    break;
                case 1:
                    turnDegrees(angle, .8);
                    setTimerAndAdvanceStep(1000);
                    break;
                case 2:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 3:
                    depthOffset = VisionPlacer.getDepth();
                    xOffsest = VisionPlacer.getXAngleOffset();
                    adjustDistance = Math.sqrt(Math.pow(xOffsest, 2) + Math.pow(depthOffset, 2));
                    adjustAngle = Math.atan(xOffsest/depthOffset);

                    driveInches(adjustDistance, adjustAngle, .8);
                    setTimerAndAdvanceStep(1000);
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