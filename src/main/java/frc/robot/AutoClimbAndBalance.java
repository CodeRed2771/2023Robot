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
    boolean test= true;

    public void start() {
        super.start();
        test = true;
    }

    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        double currentPitch = RobotGyro.pitch();
        SmartDashboard.putNumber("Climb Balance Step", getCurrentStep());
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
            // switch (getCurrentStep()) {
            //     case 0 :
            //         DriveAuto.driveInches(16, 0, .5);
            //         break;
            //     case 20 :  
            //         DriveAuto.driveInches(12, 0, .5);
            //         break; 
            //     case 40 :  
            //         DriveAuto.driveInches(12, 0, .5);
            //         break;   
            //     case 60 :  
            //         DriveAuto.driveInches(12, 0, .5);
            //         break;   
            //     case 80 :  
            //         DriveAuto.driveInches(12, 0, .5);
            //         break;   
            //     case 90 :  
            //         DriveAuto.driveInches(12, 0, .5);
            //         break;   
            //     case 140:  
            //         DriveAuto.driveInches(-48, 0, .5);
            //         break; 
               
            // }
            // advanceStep();

          

            // }
             
        }
    }
}
