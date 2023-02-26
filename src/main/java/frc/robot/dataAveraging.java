package frc.robot;

import java.util.Arrays;

import edu.wpi.first.wpilibj.SynchronousInterrupt;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class dataAveraging {
    String data;
    public dataAveraging(String data) {
        this.data = data;
    }

    // Data Instantiation 
    // Arrays 
    double instanceData[] = new double[6];
    double gatheredData[][] = new double[6][50];
    double dataTotal[] = new double[6];
    double averagedData[] = new double[6];
    final double sanityCheckRange[] = {20, 20, 5, 45, 45, 360};
    final double adjustCheckRange[] = {.05, .05, .05, 2, 2, 45};
    final double nullArray[] = {1, 0, 0, 0, 0,0};

    // Signle Data
    int numberOfNonZeros = 0; 
    int cycle = 0;
    boolean wild = false;

    // Init (Reset)
    public void init() {
        numberOfNonZeros = 0;
        wild = false;
        Arrays.fill(gatheredData[0], 0);
        Arrays.fill(gatheredData[5], 0);
        Arrays.fill(gatheredData[1], 0);
        Arrays.fill(gatheredData[2], 0);
        Arrays.fill(gatheredData[3], 0);
        Arrays.fill(gatheredData[4], 0);
    }
    // Perodic
    public void periodic() {
        instanceData = VisionPlacer.table.getEntry(data).getDoubleArray(new double[]{});
        if (instanceData.length != 0) { // !null
            // Basic check
            wild = false;
            for(int dataValue = 0; dataValue < 6; dataValue++) {
                if(instanceData[dataValue] > sanityCheckRange[dataValue] 
                    || instanceData[dataValue] < -sanityCheckRange[dataValue]){
                        wild = true;
                } 
            }
            // Ajust check
            if(cycle >= 2) {
                for(int dataValue = 0; dataValue < 6; dataValue++) {
                    if(Math.abs(instanceData[dataValue]-averagedData[dataValue]) > adjustCheckRange[dataValue]) {
                        instanceData[dataValue] = averagedData[dataValue] + Math.signum(instanceData[dataValue]-averagedData[dataValue])*adjustCheckRange[dataValue];
                    }
                }
            }
            // Stash Data 
            if (wild == false){
                if (cycle >= 50) {
                    cycle = 0;
                    for(int dataValue = 0; dataValue < 6; dataValue++) {
                        gatheredData[dataValue][cycle] = instanceData[dataValue];
                    }
                } else {
                    for(int dataValue = 0; dataValue < 6; dataValue++) {
                        gatheredData[dataValue][cycle] = instanceData[dataValue];
                    }
                    cycle++;
                }
                
                // Total number of NonZero Data
                numberOfNonZeros = 0;
                for(int i = 0; i < 50; i ++) {
                    if (gatheredData[0][i] != 0) {
                        numberOfNonZeros++;
                    }
                }

                // Total Data
                Arrays.fill(dataTotal, 0);
                for(int dataValue = 0; dataValue < 6; dataValue++) {
                    for(int runThrough = 0; runThrough < 50; runThrough++) {
                        dataTotal[dataValue] += gatheredData[dataValue][runThrough];
                    }   
                }

                // Find Average 
                for(int dataValue = 0; dataValue < 6; dataValue++) {
                    averagedData[dataValue] = dataTotal[dataValue]/numberOfNonZeros;
                }
            }

        } else {
            // System.out.println("No table found");
        }
    }
    // Getters 
    public double[] averagedData() {
        if (averagedData.length == 0) {
            return nullArray;
        } else {
            return averagedData;
        }
    }
    public double[] dataTotal() {
        if (averagedData.length == 0) {
            return nullArray;
        } else {
            return dataTotal;
        }
    }
}
