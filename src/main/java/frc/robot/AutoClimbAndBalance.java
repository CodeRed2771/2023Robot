/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoClimbAndBalance extends AutoBaseClass {
    private boolean isBalanced = false;
    private int counter = 0;
    private int dacount = 0;
    private int stopCount = 0;
    private int bob = 0;

    public void start() {
        super.start();
    }

    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        double currentPitch = RobotGyro.pitch();

        if (isRunning()) {
            // if (counter == 0)
            //     DriveAuto.driveInches(6, 0, .5);

            counter++;

            DriveAuto.tick();
            SmartDashboard.putNumber("Auto Balance Counter", counter);
            if(bob>0)
                bob--;
            if (Math.abs(currentPitch) < .5) {
                SmartDashboard.putNumber("Stop Count", stopCount++);
                // DriveTrain.stopDriveAndTurnMotors();
            } else
            {
                // move relative to pitch angle   signum returns -1 or 1 based on sign of currentPitch
                if (bob==0) {
                    // DriveAuto.driveInches(6 * Math.signum((currentPitch)), 0, .3);                
                    DriveAuto.driveInches(-6  * Math.signum((currentPitch)), 0, .5); 
                    bob =20;               
                    SmartDashboard.putNumber("Auto Drive Call", dacount++);
               }

            }
             
        }
    }
}
