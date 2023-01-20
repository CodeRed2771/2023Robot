package frc.robot; 

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

}