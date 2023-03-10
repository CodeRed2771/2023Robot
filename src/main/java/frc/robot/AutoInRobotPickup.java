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
                    if(Arm.extensionCompleted())
                        advanceStep();
                    break;
                case 2:
                    Arm.presetLift(shoulderPresets.PICKUP_CONE);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 3:
                    if(Arm.liftCompleted())
                        advanceStep();
                    break;
                case 4:
                    Arm.presetExtend(extenderPresets.PICKUP);
                    setTimerAndAdvanceStep(1200);
                    break;
                case 5:
                    if(Arm.extensionCompleted())
                        advanceStep();
                    break;
                case 6:
                    Claw.closeClawTO();//closing claw to the closed position
                    setTimerAndAdvanceStep(2200);
                    break;
                case 7:
                    // if(clawMovementCompleted)
                        // advanceStep();
                    // break;
                    break;
                case 8:
                    Arm.presetLift(shoulderPresets.PLACING_LOW);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 9:
                    if(Arm.liftCompleted())
                        advanceStep();
                    break;
                case 10:
                    stop();
                    break;
            }
        }
    }
}
