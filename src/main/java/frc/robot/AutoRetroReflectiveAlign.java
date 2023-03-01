package frc.robot;

import javax.lang.model.util.ElementScanner6;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.VisionPlacer.LimelightOn;

//The purpose of this class is to turn the robot until we are on target.

public class AutoRetroReflectiveAlign extends AutoBaseClass {

    private double strafeDistance = 0;
    private double distFromTarget = 41.75;

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
                SmartDashboard.putNumber("Adj Angle Offset", strafeDistance);
                SmartDashboard.putNumber("Angle Offset", VisionPlacer.getXAngleOffset());
                SmartDashboard.putBoolean("Sees Target", VisionPlacer.seesTarget());
                strafeDistance = distFromTarget*(Math.toDegrees(Math.tan(Math.toRadians(VisionPlacer.getXAngleOffset()))));
                advanceStep();
                break;
            case 2:
                if (Math.abs(strafeDistance) > 1){
                    driveInches(strafeDistance, 90,0.4);
                    setTimerAndAdvanceStep(2000);
                } else {
                    setStep(5);
                }
                break;
            case 3:
                if (driveCompleted()) {
                    advanceStep();
                }
                break;
            case 4:
                stop();
                break;
            }
        }
    }
}