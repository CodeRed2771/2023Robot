package frc.robot; 



import java.util.Arrays;

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

    // Find Mean
    // Find MAD
    // Elimate all MAD > ____
    // Find New Mean
    // Trash data for longer than 10 s without seeing target
    
    // Input Data to track by Stirng 
    static class dataAveraging {
        static String data;
        static double[] instanceData;
        static double[][] trackedData = new double[6][20];
        static double[] dataTotal = new double[6];
        static double[] averageData = new double[6];
        static double[] nullArray = {0,0,0,0,0,0};
        static double[][] MAD_Data = new double[6][20];
        static double[] gatheredDataLenght = new double[6];
        static int round;
        static boolean gettingSomewhere = false;
        // latency 
        public dataAveraging(String data) {
            this.data = data;
            round = 0;
        }
        public static void init() {
            Arrays.fill(trackedData[0],0);
            Arrays.fill(trackedData[1],0);
            Arrays.fill(trackedData[2],0);
            Arrays.fill(trackedData[3],0);
            Arrays.fill(trackedData[4],0);
            Arrays.fill(trackedData[5],0);
            Arrays.fill(dataTotal,0);
            Arrays.fill(averageData,0);
            Arrays.fill(gatheredDataLenght,0);
        }
        public static void periodic() {
            instanceData = table.getEntry(data).getDoubleArray(new double[]{});
            if (instanceData.length != 0){
                if (round >= 20) {
                    round = 0;
                    for(int dataValue = 0; dataValue < 6; dataValue++) {
                        if (dataValue > 3) {
                              trackedData[dataValue][round] = instanceData[dataValue]* 39.3701;
                        } else {
                            trackedData[dataValue][round] = instanceData[dataValue]* 39.3701;
                        }
                    }
                } else {
                    for(int dataValue = 0; dataValue < 6; dataValue++) {
                        trackedData[dataValue][round] = instanceData[dataValue];
                    }
                    round++;
                }
                Arrays.fill(dataTotal, 0);
                Arrays.fill(gatheredDataLenght, 0);
                for(int dataValue = 0; dataValue < 6; dataValue++) {
                    for(int runThrough = 0; runThrough < trackedData.length; runThrough++) {
                        if (trackedData[dataValue][runThrough] != 0) {
                            dataTotal[dataValue] += Math.abs(trackedData[dataValue][runThrough]);
                            gatheredDataLenght[dataValue] ++;
                            
                        } else {
                            gettingSomewhere = true;
                        }
                    }
                }
                for(int dataValue = 0; dataValue < 6; dataValue++) {
                    averageData[dataValue] = dataTotal[dataValue]/gatheredDataLenght[dataValue];
                }
            }

        }

        public double roundsOfData() {
            return gatheredDataLenght[0];
        }
        
        public static double[] averageData() {
            if (averageData.length == 0) {
                return nullArray;
            } else {
                return averageData;
            }
        }
        public static double[] dataTota() {
            if (dataTotal.length == 0) {
                return nullArray;
            } else {
                return dataTotal;
            }
        }
        public static boolean gotSomewhere() {
            return gettingSomewhere;
        }
    }

    
    static double[] botPose;

    public static dataAveraging botpose_targetspace = new dataAveraging("botpose_targetspace");
    public static void init() {
        table = NetworkTableInstance.getDefault().getTable("limelight");
        setAprilTagPipeline();
        botpose_targetspace.init();
    }
    
    public static void periodic() {
        dataAveraging.periodic();
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
        botPose = table.getEntry("botpose_targetspace").getDoubleArray(new double[]{});
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
    public static double botPoseWidth() {
        getBotPose();
        if (botPose.length == 0) {
            return 0;
        } else {
            return botPose[1]  * 39.3701;
        }
    }
    public static double botPoseLength() {
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
