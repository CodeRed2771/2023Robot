package frc.robot;

import java.lang.reflect.Array;
import java.util.ArrayList;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TickTimer {
    
    
    static ArrayList<Long> tickLengthTime; //holds the System.currentTimeMillis() of the last 2 seconds of ticks
    static final int MAX_TICKS_HELD = 100; // 2 seconds worth of ticks
    static ArrayList<Double> avgTickLength; //holds the avg ms per tick for the last minute,it is updated every 2 seconds
    static ArrayList<Double> avgAmtTicks; // holds the avg amount of ticks for the last "minute"(this is unsure if the ticks are higher or lower than the norm of 50 ticks per second), should be updated every 2 seconds 
    static Long updateAvg;

    //this is a debug class, it tests how many ticks per second the robot is doing and the ms between ticks

    //average ticks per second
    //average ms between ticks


    public static void init() {
        tickLengthTime = new ArrayList<Long>();
        avgTickLength = new ArrayList<Double>();
        avgAmtTicks = new ArrayList<Double>();
        for (int i = 0; i < 100; i++) {
            tickLengthTime.add(0,System.currentTimeMillis());
        }
        for (int i = 0; i < 29; i++) {
            avgTickLength.add(1.0);
            avgAmtTicks.add(1.0);
        }
        updateAvg = tickLengthTime.get(0);
        
    }

    public static void tick(){
        tickLengthTime.add(0,System.currentTimeMillis());
        tickLengthTime.remove(tickLengthTime.size()-1);

        double avgTicksPerSecond = getAvgTicksPerSecond();
        double avgMsPerTick = getAvgMsPerTick();
        
        double avgTPS = avgTicksPerSecond;
        double avgMPT = avgMsPerTick;
        for (int i = 0; i < avgTickLength.size(); i++) {
            avgTPS+=avgTickLength.get(i);
            avgMPT+=avgAmtTicks.get(i);
        }
        avgTPS/=avgTickLength.size()+1;
        avgMPT/=avgTickLength.size()+1;
        if(! tickLengthTime.contains(updateAvg)){
            updateAvg = tickLengthTime.get(0);
            avgTickLength.add(0,avgMsPerTick);
            avgTickLength.remove(avgTickLength.size()-1);
            avgAmtTicks.add(0,avgTicksPerSecond);
            avgAmtTicks.remove(avgAmtTicks.size()-1);
        }

        SmartDashboard.putNumber("Average Ticks Per Second", avgTPS);
        SmartDashboard.putNumber("Average Ms Per Tick", avgMPT);
    }

    private static double getAvgMsPerTick() {
        double count = 0.0;
        long diff = 0;
        Long last = tickLengthTime.get(0);
        for (int i = 0; i < tickLengthTime.size()-1; i++) {
            diff+=Math.abs(last-tickLengthTime.get(i));
            last = tickLengthTime.get(i);
            count+=1;
        }
    
        return (diff/count)/1000.0;
    }

    private static double getAvgTicksPerSecond() {
        Long start = tickLengthTime.get(0);
        Long stop = tickLengthTime.get(tickLengthTime.size()-1);
        double diff = (Math.abs(start-stop)/1000);
        diff/=100;
        diff = 1/diff;
        return diff;
    }
}
