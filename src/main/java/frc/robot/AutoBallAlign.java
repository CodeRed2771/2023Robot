package frc.robot;

import javax.lang.model.util.ElementScanner6;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//The purpose of this class is to turn the robot until we are on target.

public class AutoBallAlign extends AutoBaseClass {

    private double angleOffset = 0;

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
                angleOffset = RobotGyro.getRelativeAngle() - VisionBall.degreesToBall();
                turnDegrees(VisionBall.degreesToBall(), 0.8);
                setTimerAndAdvanceStep(2000);
                break;
            case 1:
                if (turnCompleted()) {
                    advanceStep();
                }
                break;
            case 2:
                //stop();
                break;
            }
        }
    }
}