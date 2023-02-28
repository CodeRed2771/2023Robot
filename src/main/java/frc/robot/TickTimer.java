package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TickTimer {
    
    
    static long[] ticks = new long[100];


    //this is a debug class, it tests how many ticks per second the robot is doing and the ms between ticks

    //average ticks per second
    //average ms between ticks


    public static void init() {
        for (int i = 0; i < ticks.length; i++) {
            ticks[i] = System.currentTimeMillis();
        }
    }

    public static void tick(){
        long currenttime = System.currentTimeMillis();
        for (int i = 0; i < ticks.length-1; i++) {
            ticks[i] = ticks[i+1];
        }
        ticks[99] = currenttime;
        int timediff = Math.abs((int)(ticks[99]-ticks[0]));
        int delay = 0;
        for (int i = 0; i < ticks.length-1; i++) {
            delay += Math.abs((int)(ticks[i]-ticks[i+1]));
        }
        delay /= ticks.length-1;
        int avgTickPerSecond = (int) (ticks.length/(timediff)/1000.0);
        int avgMsBetweenTicks = delay;
        SmartDashboard.putNumber("Average Ticks Per Second", avgTickPerSecond);
        SmartDashboard.putNumber("Average Ms Between Ticks", avgMsBetweenTicks);
    }
}
