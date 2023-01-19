package frc.robot;

import java.beans.Statement;
import java.lang.Math;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionShooter {

    private static double CameraHeight = 21.625;
    private static double TargetHeight = 104; 
    private static double cameraDistanceFromCenterOfRobot = 4.875; 
    private static double CameraAngle = 29.5;
    private static NetworkTable table = null;
    private static double ty = 0;
    private static double degreesTargetOffGround = 0;
    private static double distance = 0;
    private static double angleOffset;

    public static void init() {
        table = NetworkTableInstance.getDefault().getTable("limelight");
        SmartDashboard.putNumber("Adjust Val:", 1);
    }

    public static double getAngleOffset() {
        return table.getEntry("tx").getDouble(0);
    }

    public static double getDistanceAdjustedAngle () {

        // return getAngleOffset()*SmartDashboard.getNumber("Adjust Val:", 1);
                                                                                           
        double distance = getDistanceFromTarget();
        double originalDistance = distance;                                                                                                                        
        double upperVal = 0;
        double adjustFactorOne = 1;                                                    
        double adjustFactorTwo = 1;                                                    
        double averageAdjustFactorPerInch = 0;
        double finalAdjustedFactor = 1;
                                   
        distance = Math.floor(distance/12);                                 // THIS IS THE CODE WE ARE GOING TO USE TO GET
        upperVal = 48;                                            // THE DISTANCE ADJUSTED FACTOR - IS

        if (distance > upperVal) {
            distance = upperVal;
        }

        if (distance > 0 && distance <= upperVal) {
            adjustFactorOne = VisionCalibration.turnAdjustmentArray[(int) distance];
            adjustFactorTwo = VisionCalibration.turnAdjustmentArray[(int) upperVal];
            averageAdjustFactorPerInch = (adjustFactorTwo - adjustFactorOne) / 12;
            finalAdjustedFactor = (averageAdjustFactorPerInch * (originalDistance - (distance * 12))) + adjustFactorOne;
        } else {
            finalAdjustedFactor = 1;
            // finalAdjustedFactor = VisionCalibration.turnAdjustmentArray[(int)distance];
        }

        SmartDashboard.putNumber("FINAL ADJUSTED FACTOR", finalAdjustedFactor);
        SmartDashboard.putNumber("VISION Angle Offset", getAngleOffset());
        
        return finalAdjustedFactor * getAngleOffset();
    }

    public static boolean seesTarget() {
        return table.getEntry("tv").getDouble(0) > 0; 
    }

    public static boolean onTarget() {
        return (seesTarget() && (Math.abs(getAngleOffset()) <= 1));
    }

    public static double getDistanceFromTarget () {
        ty = table.getEntry("ty").getDouble(0);
        /*degreesTargetOffGround = CameraAngle + ty;
        distance = (TargetHeight - CameraHeight) / Math.tan(Math.toRadians(degreesTargetOffGround));*/
        double power = ty+Calibration.VISION_DISTANCE_B;
        SmartDashboard.putNumber("Power", power);
        distance = Math.pow(Calibration.VISION_DISTANCE_A, power);
        SmartDashboard.putNumber("Without C", distance);
        distance += Calibration.VISION_DISTANCE_C;
        SmartDashboard.putNumber("Distance:", distance);

        // Eshion's Way 
        ty = table.getEntry("ty").getDouble(0);
        degreesTargetOffGround = CameraAngle + ty;
        distance = (TargetHeight - CameraHeight) / Math.tan(degreesTargetOffGround);
        SmartDashboard.putNumber("Distance:", distance);
        return distance;
    }

    public static double getAdjustedDistanceFromTarget () {
        
        double originalDistance = getDistanceFromTarget(); 
        double angleFromTarget = getAngleOffset();

        return originalDistance - (cameraDistanceFromCenterOfRobot - ((Math.cos(Math.toRadians(angleFromTarget))) * cameraDistanceFromCenterOfRobot));
    }

    public static void setLED(boolean turnOn) {
    //    table.getEntry("ledMode").forceSetNumber(turnOn ? 3 : 1); // 3 - on, 1 = off, 2 - blink
    }
    public static void flashLED() {
        //table.getEntry("ledMode").forceSetNumber(2);

        new Thread(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setLED(false);
        }).start();
    }

    public static void setDriverMode() {
        setLED(false);
  //      table.getEntry("camMode").forceSetNumber(1);
    }

    public static void setVisionTrackingMode() {
        setLED(true);
  //      table.getEntry("camMode").forceSetNumber(0);
    }

    public static void setVisionToActiveTrackingMode() {
 //       table.getEntry("snapshot").forceSetNumber(1);
    }

    public static void stopActiveVisionMode() {
 //       table.getEntry("snapshot").forceSetNumber(0);
    }

    public static void setTargetForShooting() {
//table.getEntry("pipeline").forceSetNumber(0);
    }

    public static void setTargetForFuelCell() {
 //       table.getEntry("pipeline").forceSetNumber(1);
    }

    public static boolean seeTarget() {
        double t = table.getEntry("ty").getDouble(0);
        if (t != 0) {
            return true;
        } else {
            return false;
        }
    }
    
    public static void setAngleOffset() {
        angleOffset = VisionShooter.getDistanceAdjustedAngle();
    }

    public static double getStoredAngleOffset() {
        return angleOffset;
    }
}