/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoElementAlignment extends AutoBaseClass {
    
    // public VisionElements visionElements = new VisionElements();

    public void start(char robotPosition) {
        super.start(robotPosition);
    }

    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        if (isRunning()) {
            DriveAuto.tick();
            SmartDashboard.putNumber("Auto Step", getCurrentStep());
            SmartDashboard.putBoolean("Cone Seen? ", VisionElements.coneInView());
            SmartDashboard.putBoolean("Cube Seen? ", VisionElements.cubeInView());
        }
    }
}
