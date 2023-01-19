/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Shooter.ManualShotPreset;
import frc.robot.Shooter.ShooterPosition;

public class AutoTarmacShoot1 extends AutoBaseClass {
    public void start() {
        super.start();
    }

    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        if (isRunning()) {
            VisionShooter.setTargetForShooting();
            DriveAuto.tick();
            SmartDashboard.putNumber("Auto Step", getCurrentStep());
            
            switch (getCurrentStep()) {
                case 0://segment 1 
                    driveInches(24, 0, 0.75);
                    Shooter.setManualPresets(ManualShotPreset.BackOfTarmac);
                    setTimerAndAdvanceStep(3000);
                    break;
                case 1:
                    break;
                case 2://segment 2
                    Shooter.oneShotAuto();
                    setTimerAndAdvanceStep(1000);
                    break;
                case 3:
                    break;
                case 4: 
                    driveInches(26, 0, .75);
                    setTimerAndAdvanceStep(3000);
                    break;
                case 5:
                    break;
                case 6://stop
                    stop();
                    advanceStep();
                    break;
                case 7:
                    break;
            } 
        }
    }
}
