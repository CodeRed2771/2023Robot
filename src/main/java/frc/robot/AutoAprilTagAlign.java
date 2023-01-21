package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.VisionPlacer.LimelightOn;

public class AutoAprilTagAlign extends AutoBaseClass{
	
    double angle;

    public void start() {
		super.start();
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
            }
        }    
        
    }
    
}