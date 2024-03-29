/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

// import java.lang.reflect.Array;
// import java.util.EnumMap;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Arm.extenderPresets;
import frc.robot.Arm.shoulderPresets;

public class AutoAlignToDoubleSS extends AutoBaseClass {

    public enum AlignD {
        WALL, BARRIER
    }
    private double angle;
    private double[] data;
    private double depthDistToWall;
    final int YAW = 5;
    final int PITCH  = 4; 
    final int ROLL = 3;
    final int Z = 2; 
    final int WIDTH =  1;
    final int LENGTH = 0;
    final double metersToInches = 39.3701;
    private double slideDistToWall;
    private AlignD alignDirection;
    private final double otherSideInchesToWall = 15;

    public void start(AlignD direction) {
        super.start();
        alignDirection = direction;
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
                    data = VisionPlacer.botpose_wpiblue.averagedData();
                    advanceStep();
                    break;
                case 1:
                    angle = RobotGyro.getClosestTurn(180);
                    if (angle > 1 || angle < -1) {
                        turnDegrees(angle, .4);
                        setTimerAndAdvanceStep(2000);
                    } else {
                        setStep(3);
                    }
                    break;
                case 2:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 3:
                    if(DriverStation.getAlliance() == Alliance.Blue)
                        depthDistToWall = 649-data[LENGTH]*metersToInches;
                    if(DriverStation.getAlliance() == Alliance.Red)
                        depthDistToWall = data[LENGTH]*metersToInches;
                    driveInches(depthDistToWall-38, 0, 0.6);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 4:
                    if (driveCompleted()) {
                        advanceStep();
                    }
                    break;
                case 5:
                    Arm.presetExtend(extenderPresets.BACK_FEEDER_STATION);
                    Arm.presetShoulder(shoulderPresets.PICKUP_BACK_FEEDER_STATION);
                    slideDistToWall = 319-data[WIDTH]*metersToInches;
                    if(alignDirection == AlignD.WALL && getAlliance() == Alliance.Blue){
                        driveInches(-slideDistToWall, 90, 0.7);
                    } else if(alignDirection == AlignD.BARRIER && getAlliance() == Alliance.Blue){
                        driveInches(slideDistToWall+otherSideInchesToWall, 90, 0.7);
                    } else if(alignDirection == AlignD.WALL && getAlliance() == Alliance.Red){
                        driveInches(slideDistToWall, 90, 0.7);
                    } else if(alignDirection == AlignD.BARRIER && getAlliance() == Alliance.Red){
                        driveInches(-slideDistToWall-otherSideInchesToWall, 90, 0.7);
                    }
                    setTimerAndAdvanceStep(3500);
                    break;
                case 6:
                    if(driveCompleted())
                        advanceStep();
                    break;
                case 7:
                    stop();
                    break;
            }
        }
    }
}
