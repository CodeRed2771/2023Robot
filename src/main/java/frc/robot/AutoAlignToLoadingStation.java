/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoAlignToLoadingStation extends AutoBaseClass {

    private double angleOffset = 0;
    private double robotCurrentAngle = 0;
    private double distance = 0;

    // NEW PIPELINE NEEDED FOR THIS CLASS - IS

    public AutoAlignToLoadingStation () {

    }

    public void start() {
        super.start();
        // VisionShooter.setVisionTrackingMode();
        // VisionShooter.setTargetForShooting();
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
                    // VisionShooter.setTargetForShooting();
                    // distance = VisionShooter.getDistanceFromTarget();
                    // angleOffset = VisionShooter.getDistanceAdjustedAngle();
                    // robotCurrentAngle = RobotGyro.getRelativeAngle();
                    advanceStep();
                    break;
                case 1:
                    turnDegrees(-90 - (-(robotCurrentAngle - 180)), 1);
                    setTimerAndAdvanceStep(1000);
                    break;
                case 2:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 3:
                    driveInches(distance, angleOffset + 180, 0.8, false, true);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 4:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 5:
                    stop();
                    break;
            }
        }
    }
}
