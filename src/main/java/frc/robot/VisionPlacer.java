package frc.robot; 

import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance; 

public class VisionPlacer {
    private static NetworkTable table;

    public static enum LimelightOn {
        On,
        Off,
        Blink
    }
    public static enum Pipeline {
        Retroreflective, 
        AprilTag,
    }
    static double[] botPose;

    public static void init() {
        table = NetworkTableInstance.getDefault().getTable("limelight");
        setAprilTagPipeline();
    }

    private static void setPipeline(int pipeline) {
		NetworkTableEntry pipelineEntry = table.getEntry("pipeline");
    	pipelineEntry.setNumber(pipeline);
    }

    public static void setAprilTagPipeline() {
        setPipeline(0);
    }

    public static void setRetroreflectivePipeline() {
        setPipeline(1);
    }

    public static Pipeline getPipeline() {
        double pipeline = table.getEntry("getpip").getDouble(0);
        if (pipeline == 1) {
            return Pipeline.Retroreflective;
        } else {
            return Pipeline.AprilTag;
        }
    }

    public static double getXAngleOffset() {
        return table.getEntry("tx").getDouble(0);
    }

    public static double getYAngleOffset() {
        return table.getEntry("ty").getDouble(0);
    }

    public static double getArea() {
        return table.getEntry("ta").getDouble(0);
    }

    public static boolean seesTarget() {
        boolean seesTarget = false; 
        if (table.getEntry("tv").getDouble(0) == 1) {
            seesTarget = true;
        }
        return seesTarget;
    }

    public static void setLED(LimelightOn value) {
        if (value == LimelightOn.Off) {
            table.getEntry("ledMode").setNumber(1);
        } else if (value == LimelightOn.Blink) {
            table.getEntry("ledMode").setNumber(2);
        } else {
            table.getEntry("ledMode").setNumber(3);
        }
    }
    
    public static void switchLED() {
        double onOff = table.getEntry("ledMode").getDouble(0); 
        if (onOff == 3) {
            setLED(LimelightOn.Off);
        } else {
            setLED(LimelightOn.On);
        }
    }

    public static double imageRotation() {
        return table.getEntry("ts").getDouble(0);
    }

    public static double aprilTagID() {
        return table.getEntry("tid").getDouble(0);
    }

    public static void getBotPose() {
        botPose = table.getEntry("botpose").getDoubleArray(new double[]{});
    }

    public static double botPoseYaw() {
        getBotPose();
        if (botPose.length == 0) {
            return 0;
        } else {
            return botPose[5];
        }
    }
    public static double botPosePitch() {
        getBotPose();
        if (botPose.length == 0) {
            return 0;
        } else {
            return botPose[4];
        }
    }
    public static double botPoseRoll() {
        getBotPose();
        if (botPose.length == 0) {
            return 0;
        } else {
            return botPose[3];
        }
    }
    public static double botPoseZ() {
        getBotPose();
        if (botPose.length == 0) {
            return 0;
        } else {
            return botPose[2] *39.3701;
        }
    }
    public static double botPoseY() {
        getBotPose();
        if (botPose.length == 0) {
            return 0;
        } else {
            return botPose[1]  * 39.3701;
        }
    }
    public static double botPoseX() {
        getBotPose();
        if (botPose.length == 0) {
            return 0;
        } else {
            return botPose[0] *39.3701;
        }
    }

    public static void getCamPose() {
        botPose = table.getEntry("camtran").getDoubleArray(new double[]{});
    }

    public static double camPoseYaw() {
        getCamPose();
        if (botPose.length == 0) {
            return 0;
        } else {
            return botPose[5];
        }
    }
    public static double camPosePitch() {
        getCamPose();
        if (botPose.length == 0) {
            return 0;
        } else {
            return botPose[4];
        }
    }
    public static double camPoseRoll() {
        getCamPose();
        if (botPose.length == 0) {
            return 0;
        } else {
            return botPose[3];
        }
    }
    public static double camPoseZ() {
        getCamPose();
        if (botPose.length == 0) {
            return 0;
        } else {
            return botPose[2] *39.3701;
        }
    }
    public static double camPoseY() {
        getCamPose();
        if (botPose.length == 0) {
            return 0;
        } else {
            return botPose[1]  * 39.3701;
        }
    }
    public static double camPoseX() {
        getCamPose();
        if (botPose.length == 0) {
            return 0;
        } else {
            return botPose[0] *39.3701;
        }
    }
    
    // public static Position camTran() {
    //     double[] test = table.getEntry("camtran").getDoubleArray(new double[]{});
    //     if(test.length >0) {
    //         Translation3d translation = new Translation3d(test[0], test[1], test[2]);
    //         Rotation3d rotation = new Rotation3d(test[3], test[4], test[5]);
    //         return new Position(translation, rotation);
    //     } else {
    //         Translation3d translation = new Translation3d(0, 0, 0);
    //         Rotation3d rotation = new Rotation3d(0, 0, 0);
    //         return new Position(translation, rotation);
    //     }
    // }

    public static double getDepth() {
        return 2;
    }
}
