/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoClimbAndBalance extends AutoBaseClass {

    private final static double driveRatio = .3; // was .5
    private boolean isBalanced = false;
    private int counter = 0;
    private int dacount = 0;
    private int stopCount = 0;
    private int timeToNextDrive = 0;
    boolean test = true;
    int timesLevel = 0;
    AutoBaseClass brake;

    public void start() {
        super.start();
        test = true;
    }

    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        double driveDistance = 0;
        double currentPitch = RobotGyro.pitch();

        SmartDashboard.putNumber("Climb Balance Step", getCurrentStep());
        
        if (isRunning()) {
            
            counter++;

            DriveAuto.tick();
            SmartDashboard.putNumber("Auto Balance Counter", counter);
            if(timeToNextDrive>0)
                timeToNextDrive--;
            if (Math.abs(currentPitch) < 1.3) {
                SmartDashboard.putNumber("Stop Count", stopCount++);
                DriveAuto.parkingBrake();
                timesLevel ++;
                if (timesLevel >  50) {
                    stop();
                }
            } else
            {
                // move relative to pitch angle   signum returns -1 or 1 based on sign of currentPitch
                if (timeToNextDrive==0) {
                    driveDistance = driveRatio  * currentPitch;
                    DriveAuto.driveInches(driveDistance, 0, .6); 
                    timeToNextDrive = (int)(Math.abs(driveDistance) * 12);       
                    SmartDashboard.putNumber("Auto Drive Call", dacount++);
               }
               timesLevel = 0;
            }
        }
    }
}
