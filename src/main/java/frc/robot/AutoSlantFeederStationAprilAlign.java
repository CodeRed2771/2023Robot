package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Arm.extenderPresets;
import frc.robot.Arm.shoulderPresets;
import frc.robot.VisionPlacer.LimelightOn;

public class AutoSlantFeederStationAprilAlign extends AutoBaseClass {
    double[] robotPose;
    double angle;
    double depthToEnd;
    double distanceToFeeder;
    double width;
    public void start() {
		super.start();
	}

    public void stop() {
        super.stop();
    }
    private static final double FIELD_LENGHT = 54 * 12+.75;
    private static final double FIELD_WIDTH = 26*12+3.5;
    private static final double metersToInches = 39.3701;
    
    final int YAW = 5;
    final int PITCH  = 4; 
    final int ROLL = 3;
    final int Z = 2; 
    final int WIDTH =  1;
    final int LENGTH = 0;

    @Override
    public void tick() {
        if (isRunning()) {
            SmartDashboard.putNumber("Auto April Tag Step", getCurrentStep());
            switch(getCurrentStep()) {
                case 0:
                    // Set-up
                    robotPose = VisionPlacer.botpose_wpiblue.averagedData();
                    VisionPlacer.setAprilTagPipeline();
                    VisionPlacer.setLED(LimelightOn.On);

                    // Moving Arm
                    Arm.presetLift(shoulderPresets.PICKUP_FEEDER_STATION);
                    Arm.presetExtend(extenderPresets.FEEDER_STATION);
                    
                    // Calculate Depth 
                    depthToEnd = FIELD_LENGHT - robotPose[LENGTH] * metersToInches;
                    distanceToFeeder = depthToEnd - 72; 
                    driveInches(distanceToFeeder, 0, .5);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 1: 
                    if(driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 2:
                    angle = RobotGyro.getClosestTurn(90);
                    if (Math.abs(angle) > 1) {
                        turnDegrees(angle, .5);
                        setTimerAndAdvanceStep(2000);
                    } else {
                        setStep(3);
                    }
                    break;
                case 3:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 4:
                    width = FIELD_WIDTH - robotPose[WIDTH];
                    driveInches(width, 0, .5);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 5: 
                    if(driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 6:
                    stop();
                    break;
            }
        }
    }
}
