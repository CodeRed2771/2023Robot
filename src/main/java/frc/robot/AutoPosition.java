package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoPosition extends AutoBaseClass {


    public AutoPosition() {
    }

    public void start() {
        super.start();
    }

    public void stop() {
        super.stop();
    }

    public void tick(double x, double y, double orientation) {

        if (isRunning()) {
            double x1 = DriveAuto.currrentPosition.x;
            double y1 = DriveAuto.currrentPosition.y;
            double orientation1 = DriveAuto.currrentPosition.orientation;
            double newOrient = orientation - orientation1;
            double slope = (y1+y)/(x1+x);
            double degrees;
            if (slope > 0) {
                degrees = Math.atan(slope);
            } else {
                degrees = Math.abs(Math.atan(slope)) + 180;
            }
            double distance;
            if (degrees == 0 || degrees == 180) {
                distance = Math.abs(y-y1);
            } else if (degrees == 90 || degrees == 270){
                distance = Math.abs(x-x1);
            } else {
                distance =  Math.sqrt(Math.pow(x+x1, 2)+Math.pow(y+y1, 2));
            }
            DriveAuto.tick();
            SmartDashboard.putNumber("Auto Step", getCurrentStep());
            switch (getCurrentStep()) {
            case 0:
                turnDegrees(newOrient, .8);
                setTimerAndAdvanceStep(5000);
                break;
            case 1:
                if (turnCompleted()) {
                    advanceStep();
                }
                break;
            case 2:
                driveInches(distance, degrees, .8);
                setTimerAndAdvanceStep(5000);
                break;
            case 3:
                if (driveCompleted()) {
                    advanceStep();
                }
            case 4:
                stop();
                break;
            }
        }
    }

    @Override
    public void tick() {
        // TODO Auto-generated method stub
        
    }
}
