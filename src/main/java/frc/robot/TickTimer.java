package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TickTimer {
    
    
    static long[] ticks;
    final static int length_avg_long_data = 4;
    static double[] lastDelays;
    static double[] lastTicksPerSecond;
    static int longdelay = 0;

    //this is a debug class, it tests how many ticks per second the robot is doing and the ms between ticks

    //average ticks per second
    //average ms between ticks


    public static void init() {
        ticks = new long[100];
        lastDelays = new double[length_avg_long_data];
        lastTicksPerSecond = new double[length_avg_long_data];
        for (int i = 0; i < ticks.length; i++) {
            ticks[i] = System.currentTimeMillis();
        }
        for (int i = 0; i < lastDelays.length; i++) {
            lastDelays[i] = 0;
            lastTicksPerSecond[i] = 0;
        }
        longdelay = 0;
    }

    public static void tick(){
        longdelay++;
        long currenttime = System.currentTimeMillis();
        for (int i = 0; i < ticks.length-1; i++) {
            ticks[i] = ticks[i+1];
        }
        ticks[99] = currenttime;
        double timediff = Math.abs((int)(ticks[99]-ticks[0]));
        double delay = 0;
        for (int i = 0; i < ticks.length-1; i++) {
            delay += Math.abs((int)(ticks[i]-ticks[i+1]));
        }
        delay /= ticks.length-1;
        double avgTickPerSecond = (int) (ticks.length/(timediff)/1000.0);
        double avgMsBetweenTicks = delay;
        if(longdelay>ticks.length){
            for (int i = 0; i < lastDelays.length-1; i++) {
                lastDelays[i] = lastDelays[i+1];
                lastTicksPerSecond[i] = lastTicksPerSecond[i+1];
            }
            lastDelays[lastDelays.length-1] = avgMsBetweenTicks;
            lastTicksPerSecond[lastTicksPerSecond.length-1] = avgTickPerSecond;
            longdelay = 0;
        }

        for (int i = 0; i < lastDelays.length; i++) {
            avgTickPerSecond += lastTicksPerSecond[i];
            avgMsBetweenTicks += lastDelays[i];
        }
        avgTickPerSecond /= length_avg_long_data+1;
        avgMsBetweenTicks /= length_avg_long_data+1;
        
        avgTickPerSecond = ((int)(avgTickPerSecond*100))/100.0;
        avgMsBetweenTicks = ((int)(avgMsBetweenTicks*100))/100.0;
        SmartDashboard.putNumber("Average Ticks Per Second", avgTickPerSecond);
        SmartDashboard.putNumber("Average Ms Between Ticks", avgMsBetweenTicks);
    }
}
