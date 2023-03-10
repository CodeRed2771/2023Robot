/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Arm.extenderPresets;
import frc.robot.Arm.shoulderPresets;

public class AutoInRobotPickup extends AutoBaseClass {


    public AutoInRobotPickup () {

    }

    public void start() {
        super.start();
    }

    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        if (isRunning()) {
            DriveAuto.tick();
            SmartDashboard.putNumber("Auto Step", getCurrentStep());
            switch (getCurrentStep()) {
                case 0:
                    Arm.presetExtend(extenderPresets.RETRACTED);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 1:
                    // if(extensionCompleted())
                        // advanceStep();
                    break;
                case 5:
                    stop();
                    break;
            }
        }
    }
}
