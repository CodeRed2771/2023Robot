package frc.robot;

import javax.lang.model.util.ElementScanner6;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.VisionPlacer.LimelightOn;
import frc.robot.VisionPlacer.Pole;

//The purpose of this class is to turn the robot until we are on target.

public class AutoRetroReflectiveAlign extends AutoBaseClass {

    private double strafeDistance = 0;
    private double distFromTarget = 41.75;
    private double angle;

    public void start() {
        super.start();
    }


    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        if (isRunning()) {
            DriveAuto.tick();
            SmartDashboard.putNumber("AutoAlign Auto Step", getCurrentStep());
            switch (getCurrentStep()) {
            case 0:
                VisionPlacer.setRetroreflectivePipeline();
                VisionPlacer.setLED(LimelightOn.On);
                advanceStep();
                break;
            case 1:
                angle = RobotGyro.getClosestTurn(0);
                if (angle > 1 || angle < -1) {
                    turnDegrees(angle, .4);
                    setTimerAndAdvanceStep(2000);
                } else {
                    setStep(3);
                }
                break;
            case 2:
                SmartDashboard.putNumber("Adj Angle Offset", strafeDistance);
                SmartDashboard.putNumber("Angle Offset", VisionPlacer.getXAngleOffset());
                SmartDashboard.putBoolean("Sees Target", VisionPlacer.seesTarget());
                if(VisionPlacer.getPole() == Pole.HighPole) {
                    distFromTarget = 41.75;
                } else if (VisionPlacer.getPole() == Pole.LowPole) {
                    distFromTarget = 32;
                }
                strafeDistance = distFromTarget*(Math.tan(Math.toRadians(VisionPlacer.getXAngleOffset())));
                VisionPlacer.setLED(LimelightOn.Off);
                if(driveCompleted()) {
                    advanceStep();
                }
                break;
            case 3:
                if (Math.abs(strafeDistance) > 1 || Math.abs(strafeDistance) < 48){
                    driveInches(strafeDistance, 90,0.5);
                    setTimerAndAdvanceStep(2000);
                } else {
                    setStep(5);
                }
                break;
            case 4:
                if (driveCompleted()) {
                    advanceStep();
                }
                break;
            case 5:
                stop();
                break;
            }
        }
    }

}