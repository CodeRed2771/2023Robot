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
    
    public static void init() {
        table = NetworkTableInstance.getDefault().getTable("limelight");
        setRetroreflectivePipeline();
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

    public static class Position {
        Translation3d translation;
        Rotation3d rotation;
        public Position(Translation3d translation, Rotation3d rotation) {
            this.translation = translation;
            this.rotation = rotation;
        }
    }

    public static Position botPose() {
        //return table.getEntry("botpose").getNumberArray(0);
        double[] test = table.getEntry("botpose").getDoubleArray(new double[]{});
        Translation3d translation = new Translation3d(test[1], test[2], test[3]);
        Rotation3d rotation = new Rotation3d(test[4], test[5], test[6]);
        return new Position(translation, rotation);
    }

    public static Position camTran() {
        double[] test = table.getEntry("camtran").getDoubleArray(new double[]{});
        Translation3d translation = new Translation3d(test[1], test[2], test[3]);
        Rotation3d rotation = new Rotation3d(test[4], test[5], test[6]);
        return new Position(translation, rotation);
    }

    public static double jsonDump() {
        return table.getEntry("json").getDouble(0);
    }

    public static double getDepth() {
        return 2;
    }
}
