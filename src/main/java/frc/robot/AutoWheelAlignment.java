/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoWheelAlignment extends AutoBaseClass {

    public void start() {
        // super.start(robotPosition);
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
                    DriveTrain.setAllTurnOrientation(DriveTrain.angleToPosition(45), true);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 1:
                    break;
                case 2:
                    DriveTrain.setAllTurnOrientation(DriveTrain.angleToPosition(90), true);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 3:
                    break;
                case 4:
                    DriveTrain.setAllTurnOrientation(DriveTrain.angleToPosition(135), true);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 5:
                    break;
                case 6:
                    DriveTrain.setAllTurnOrientation(DriveTrain.angleToPosition(180), true);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 7:
                    break;

                case 8:
                    // setStep(0);
                    stop();
                    break;
            }
        }
    }
}
