/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import javax.lang.model.util.ElementScanner6;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Arm.bistablePresets;
import frc.robot.Arm.shoulderPresets;
import frc.robot.AutoBaseClass.PlacePositions;
import frc.robot.VisionPlacer.LimelightOn;
import frc.robot.libs.HID.Gamepad;
import pabeles.concurrency.IntOperatorTask.Max;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

/* 
Buttons - LB , RB , X , Y , B , A , DPadR - right , DPadU - up , DPadL - left , DPadD - down , back , start 
Fluid controls - R Trigger , L Trigger , X and Y right stick , X and Y left stick
DPad = directional pad 



GAMEPAD 1:
 * Drive - right and left sticks
 * Deploy Intake and Run Intake - press right trigger to deploy and hold right trigger to run intake
 * Reverse Intake - hold left bumper and hold right trigger
 * Retract Intake - press right bumper
 * Auto Climb and Balance - press B button
 * Auto Align With Pole- press A button

GAMEPAD 2:
 * Extend Arm - right stick held downwards
 * Retract Arm - right stick held upwards
 * Move Arm Upwards - left stick held up
 * Move Arm Downwards -  left stick held down
 * 
 * Open Claw - press A button
 * Close Claw - press B button
 * Flip Claw - press Left Trigger
 * 
 * Deploy Intake and Run Intake - press right trigger to deploy and hold right trigger to run intake
 * Reverse Intake - hold down left bumper and right trigger at the same time
 * 
 * Strafe Robot Right - left stick held to the right
 * Strafe Robot Left - left stick held to the left
 * 
 * LiveBottom forwards - Dpad down
 * LiveBottom backwards - Dpad down plus Left Bumper
 * 
 * Presently Unused Presets - X and Y buttons
 * Move Live Floor Forwards - Dpad up button held down
 * Move Live Floor Backwards - Dpad down button held down
 * 
 */

public class Robot extends TimedRobot { 

    SendableChooser<String> autoChooser;
    SendableChooser<String> positionChooser;
    //SendableChooser<String> positionAllianceChooser;
    SendableChooser<String> driveChooser;
    String autoSelected;
    Gamepad gamepad1;
    Gamepad gamepad2;
    SwerveTurnTest swtest;
    Compressor compressor = new Compressor(1, PneumaticsModuleType.REVPH);
    boolean reverseShooter = false;
    boolean ballTrackingTurnedOn = false;  
    boolean clawFlippedPress = false;
    boolean rampCodeActive = true;

    AutoBaseClass mAutoProgram;

    final String autoCalibrator = "Auto Calibrator";
    final String autoWheelAlign = "Auto Wheel Align";
    final String autoAlign = "Auto Align";
    final String AutoLeaveTarmac = "Auto Leave Tarmac";

    private double lastFWDvalue = 0; 
    private double lastSTRvalue = 0;
    private double lastROTvalue = 0;
    private boolean stuffDelete = false;

    @Override
    public void robotInit() {
        gamepad1 = new Gamepad(0);
        gamepad2 = new Gamepad(1);
        SmartDashboard.putString("Alliance Decided", DriverStation.getAlliance().toString());
        compressor.enableAnalog(100, 120);
  
        Calibration.loadSwerveCalibration();
        if (Calibration.isPracticeBot()) 
            DriveTrain.init("FALCON");
        else
            DriveTrain.init("NEO");
        
        Claw.init();
        LiveBottom.init();
        DriveAuto.init();
        Arm.init();
        Intake.init();
        VisionPlacer.init();

        SmartDashboard.putNumber("Current Position", 0);
        SmartDashboard.putNumber("New Position", 0);
        SmartDashboard.putBoolean("Show Encoders", true);
        SmartDashboard.putBoolean("Tune Drive-Turn PIDs", false);

        setupAutoChoices();
        mAutoProgram = new AutoDoNothing();

        RobotGyro.init();
        VisionPlacer.init();

    }

    @Override
    public void teleopInit() {
        mAutoProgram.stop();        
        RobotGyro.reset();
        
        DriveTrain.stopDriveAndTurnMotors();
        DriveTrain.allowTurnEncoderReset();
        DriveTrain.resetTurnEncoders();
        DriveTrain.setAllTurnOrientation(0, false); // sets them back to calibrated zero position
        Arm.reset();
        // VisionElements.init();
    }

    @Override
    public void teleopPeriodic() {
        if(gamepad2.getXButton()) {
            VisionPlacer.setAprilTagPipeline();
        } else if (gamepad2.getYButton()) {
            VisionPlacer.setRetroreflectivePipeline();
        }
        // SmartDashboard.putNumber("Camera Tran Length", VisionPlacer.camPoseY());
        // SmartDashboard.putNumber("Bot Pose Lenght", VisionPlacer.botPoseLength());
        // SmartDashboard.putNumber("Test Z", VisionPlacer.botPoseZ());
        // SmartDashboard.putNumber("Depth Offset Calculated", AutoAprilTagAlign.depthOffset);
        // SmartDashboard.putNumber("Center Offset Based on angle", AutoAprilTagAlign.centerOffset);
        // SmartDashboard.putNumber("Bot Pose Width", VisionPlacer.botPoseWidth());
        // SmartDashboard.putNumber("Camera Tran Width", VisionPlacer.camPoseX());
        // SmartDashboard.putNumber("Angle for X", VisionPlacer.getXAngleOffset());
        SmartDashboard.putNumber("Target Space Stability Test", VisionPlacer.botPoseLength());
        
        if (gamepad2.getAButton()){
            Claw.openClawTO();
        } else if (gamepad2.getBButton()) 
            Claw.closeClawTO();
        else
            Claw.stopClawTO();

        if (gamepad1.getBButton()){
           // Claw.closeClaw();
            mAutoProgram.stop();
            mAutoProgram = new AutoClimbAndBalance(); 
            mAutoProgram.start();

        }
        if(gamepad1.getAButton()) {
            mAutoProgram.stop();
            mAutoProgram = new AutoRetroReflectiveAlign();
            mAutoProgram.start();
        }
        if (gamepad1.getXButton()) {
            // VisionPlacer.setLED(LimelightOn.Off);
            mAutoProgram.stop();
            mAutoProgram = new AutoAprilTagAlign();
            mAutoProgram.start();
        }
        if (gamepad1.getYButton()) {
            VisionPlacer.setLED(LimelightOn.On);
        }
 
        if(gamepad2.getLeftTriggerAxis() > .5){
            if (!clawFlippedPress){
                Claw.flip();
                clawFlippedPress = true;
            } 
        } else{
            clawFlippedPress = false;
        }
        if(gamepad2.getDPadUp()){
            //Arm.presetExtend(bistablePresets.RETRACTED);
            LiveBottom.shuffle();
        }
            
        if(gamepad2.getDPadDown()) {
            if(gamepad2.getLeftBumper())
                LiveBottom.backward();
            else   
                LiveBottom.forward();
        }
        else {
            LiveBottom.off();
        }

        if (gamepad2.getLeftBumper()) {
            Arm.overrideExtend(gamepad2.getRightY());
            Arm.overrideLift(gamepad2.getLeftY());
        } else {
            Arm.extend(gamepad2.getRightY());
            Arm.lift(gamepad2.getLeftY());
        }

        if (gamepad2.getRightTriggerAxis()>.2){
            if (gamepad2.getLeftBumper()){
                Intake.deploy();
                Intake.reverse(1);
            } 
            else{
                Intake.deploy();
                Intake.run(1);
            }
        } else
            Intake.stop();

        if (gamepad2.getRightBumper()){
            Intake.retract();
        }

        SmartDashboard.putNumber("Vision X", VisionPlacer.getXAngleOffset());
        // --------------------------------------------------
        // RESET - allow manual reset of systems by pressing Start
        // --------------------------------------------------
        if (gamepad1.getStartButton()) {
            RobotGyro.reset();
            
            DriveTrain.allowTurnEncoderReset();
            DriveTrain.resetTurnEncoders(); // sets encoders based on absolute encoder positions

            DriveTrain.setAllTurnOrientation(0, false);
        }
        // DRIVE
        if (mAutoProgram.isRunning()) {
            mAutoProgram.tick();
        }

        //if (mAutoProgram.isRunning() && Math.abs(gamepad2.getRightY()) > 0.2) {
        //    mAutoProgram.stop();
        //}

        // DRIVER CONTROL MODE
        // Issue the drive command using the parameters from
        // above that have been tweaked as needed
        double driveRotAmount = -gamepad1.getRightX();
        double driveFWDAmount = -gamepad1.getLeftY();
        double driveStrafeAmount = -gamepad1.getLeftX();

        // SmartDashboard.putNumber("SWERVE ROT AXIS", driveRotAmount);
        // if (gamepad1.getAButton()) {
        //     // Shooter.StartShooter();
        //     rampCodeActive = true;
        // } else if (gamepad1.getBButton()) {
        //     rampCodeActive = false;
        // }
        
        if (rampCodeActive) {
            driveRotAmount = rotationalAdjust(driveRotAmount);
            // SmartDashboard.putNumber("ADJUSTED SWERVE ROT AMOUNT", driveRotAmount);
            driveFWDAmount = forwardAdjustV2(driveFWDAmount, true);
            driveStrafeAmount = strafeAdjustV2(driveStrafeAmount, true);
        }

        if (gamepad1.getRightBumper() || Arm.getIsExtenderExtended()) {  // slow mode if arm is extended
            driveFWDAmount = driveFWDAmount * .3;
            driveStrafeAmount = driveStrafeAmount * .3;
            driveRotAmount = driveRotAmount * .2;
        }

        SmartDashboard.putBoolean("DPadUp", gamepad1.getDPadUp());
        SmartDashboard.putBoolean("DPadLeft", gamepad1.getDPadLeft());
        SmartDashboard.putBoolean("DPadRight", gamepad1.getDPadRight());
        SmartDashboard.putBoolean("DPadDown", gamepad1.getDPadDown());

        SmartDashboard.putNumber("Best Position", TurnPosition.getBestPosition());
        SmartDashboard.putNumber("Outputs FWD", driveFWDAmount);
        SmartDashboard.putNumber("Outputs Strafe", driveStrafeAmount);

        if (Math.abs(driveFWDAmount) > .5 || Math.abs(driveRotAmount) > .5) {
            if (mAutoProgram.isRunning())
                mAutoProgram.stop();
        }
        if (!mAutoProgram.isRunning()) {
            if (gamepad1.getBackButton()) {
                DriveTrain.humanDrive(driveFWDAmount, driveStrafeAmount, driveRotAmount);
            } else {
                DriveTrain.fieldCentricDrive(driveFWDAmount, driveStrafeAmount, driveRotAmount);
            }
        }
        
        showDashboardInfo();
    }

    @Override
    public void robotPeriodic() {
        SmartDashboard.updateValues();
        SmartDashboard.putNumber("Limelight Yaw", VisionPlacer.botPoseYaw());
        SmartDashboard.putNumber("Limelight Roll", VisionPlacer.botPoseRoll());
        SmartDashboard.putNumber("Limelight Pitch", VisionPlacer.botPosePitch());
        SmartDashboard.putNumber("Limelight Width", VisionPlacer.botPoseWidth());
        SmartDashboard.putNumber("Limelight Lenght", VisionPlacer.botPoseLength());
        SmartDashboard.putNumber("Limelight Z", VisionPlacer.botPoseZ());
        VisionPlacer.periodic();
        SmartDashboard.putNumber("Limelight Average Testing Normal", VisionPlacer.botPoseLength());
        SmartDashboard.putNumber("Limelight Average Testing", VisionPlacer.botpose_targetspace.averagedData()[0]*39.3701);
        SmartDashboard.putNumber("Limelight Average Testing Total", VisionPlacer.botpose_targetspace.dataTotal()[0]*39.3701);

        VisionPlacer.periodic();
        DriveAuto.tick();
        Arm.tick();
        LiveBottom.tick();


         // Sets the PID values based on input from the SmartDashboard
        // This is only needed during tuning
        if (SmartDashboard.getBoolean("Tune Drive-Turn PIDs", false)) {
       
            DriveTrain.setDrivePIDValues(SmartDashboard.getNumber("AUTO DRIVE P", Calibration.getDriveP()),
                    SmartDashboard.getNumber("AUTO DRIVE I", Calibration.getDriveI()),
                    SmartDashboard.getNumber("AUTO DRIVE D", Calibration.getDriveD()),
                    SmartDashboard.getNumber("AUTO DRIVE F", Calibration.getDriveF()));

            DriveTrain.setTurnPIDValues(SmartDashboard.getNumber("TURN P", Calibration.getTurnP()),
                    SmartDashboard.getNumber("TURN I", Calibration.getTurnI()),
                    SmartDashboard.getNumber("TURN D", Calibration.getTurnD()),
                    SmartDashboard.getNumber("TURN I ZONE", Calibration.getTurnIZone()),
                    SmartDashboard.getNumber("TURN F", Calibration.getTurnF()));

            DriveTrain.setDriveMMAccel((int) SmartDashboard.getNumber("DRIVE MM ACCEL", Calibration.getDT_MM_ACCEL()));
            DriveTrain.setDriveMMVelocity(
                    (int) SmartDashboard.getNumber("DRIVE MM VELOCITY", Calibration.getDT_MM_VELOCITY()));
        }
        RobotGyro.position();
        // SmartDashboard.putNumber("Position X", RobotGyro.getPosition().x);
        // SmartDashboard.putNumber("Position Y", RobotGyro.getPosition().y);
        // SmartDashboard.putNumber("Position Z", RobotGyro.getPosition().z);
        SmartDashboard.putNumber("Velocity X", RobotGyro.velocityX());
        SmartDashboard.putNumber("Velocity Y", RobotGyro.velocityY());
        SmartDashboard.putNumber("Velocity Z", RobotGyro.velocityZ());
        SmartDashboard.putNumber("Pitch", RobotGyro.pitch());
        SmartDashboard.putNumber("Pitch Raw", RobotGyro.pitch_raw());
        SmartDashboard.putNumber("Roll", RobotGyro.roll());
        SmartDashboard.putNumber("Yaw", RobotGyro.yaw());
    }

    @Override
    public void autonomousInit() {

        RobotGyro.reset();

        DriveTrain.stopDriveAndTurnMotors();
        DriveTrain.allowTurnEncoderReset();
        DriveTrain.resetTurnEncoders();
        DriveTrain.setAllTurnOrientation(0, false); // sets them back to calibrated zero position

        mAutoProgram.stop();
        String selectedPos = positionChooser.getSelected();
        SmartDashboard.putString("Position Chooser Selected", selectedPos);
        char robotPosition = selectedPos.toCharArray()[0];
        System.out.println("Robot position: " + robotPosition);

        autoSelected = (String) autoChooser.getSelected();
        SmartDashboard.putString("Auto Selected: ", autoSelected);

        mAutoProgram = new AutoDoNothing();
        mAutoProgram.start();

        switch (autoSelected) {
        case autoCalibrator:
            mAutoProgram = new AutoCalibrator();
            mAutoProgram.start(robotPosition);
            break;
        case autoWheelAlign:
            mAutoProgram = new AutoWheelAlignment();
            mAutoProgram.start();
            break;
        case AutoLeaveTarmac:
            mAutoProgram = new AutoLeaveTarmack();
            mAutoProgram.start();
            break;
        }
      
    }

    private void setupAutoChoices() {
        // Position Chooser
        positionChooser = new SendableChooser<String>();
        positionChooser.addOption("Left", "L");
        positionChooser.setDefaultOption("Center", "C");
        positionChooser.addOption("Right", "R");
        SmartDashboard.putData("Position", positionChooser);

        autoChooser = new SendableChooser<String>();
        autoChooser.addOption(autoCalibrator, autoCalibrator);
        //autoChooser.addOption(autoWheelAlign, autoWheelAlign);
        autoChooser.addOption(autoAlign, autoAlign);
        //autoChooser.addOption(ballPickUp, ballPickUp);
        autoChooser.setDefaultOption(AutoLeaveTarmac, AutoLeaveTarmac);
     
        SmartDashboard.putData("Auto Chose:", autoChooser);

    }

    /**
     * This function is called periodically during autonomous.
     */
    @Override
    public void autonomousPeriodic() {
        if (mAutoProgram.isRunning()) {
            mAutoProgram.tick();
            Arm.tick();
            
        }
        showDashboardInfo();
    }

    public void disabledInit() {
        // allows the turn encoders to be reset once during disabled periodic
        // DriveTrain.allowTurnEncoderReset();
        // DriveTrain.resetDriveEncoders();
        // DriveTrain.resetTurnEncoders();
        
        Calibration.initializeSmartDashboard(); 

    }

    public void disabledPeriodic() {
        showDashboardInfo();
        SmartDashboard.putBoolean("COMP BOT", !Calibration.isPracticeBot());

        // Check if we are calling for a 'save' of the current wheel positions
        // as the new calibration values.
        // This is triggered by setting "Calibrate Swerve" to true on the Dashboard
        if (Calibration.shouldCalibrateSwerve()) {
            double[] pos = DriveTrain.getAllAbsoluteTurnOrientations();
            Calibration.saveSwerveCalibration(pos[0], pos[1], pos[2], pos[3]);
        }

        // see if we want to reset the calibration to whatever is in the program
        // basically setting "Delete Swerve Calibration" to true will trigger
        // this, which deletes the calibration file.
        Calibration.checkIfShouldDeleteCalibration();
    }

    /**
     * This function is called periodically during test mode.
     */
    @Override
    public void testPeriodic() {
    }

    private double rotationalAdjust(double rotateAmt) {
        // put some rotational power restrictions in place to make it
        // more controlled movement
        double adjustedAmt = 0;

        if (Math.abs(rotateAmt) < .08) {
            adjustedAmt = 0;
        } else {
            if (Math.abs(rotateAmt) < .5) {
                adjustedAmt = .1 * Math.signum(rotateAmt); // take 10% of the input
            } else {
                if (Math.abs(rotateAmt) < .99) {
                    adjustedAmt = .25 * rotateAmt;
                } else {
                    adjustedAmt = rotateAmt * .4;
                }
            }
        }
        return adjustedAmt;
    }

    private double forwardAdjustV2(double fwd, boolean normalDrive) {
        final double maxACCELchange = .03;
        final double maxSTOPPINGchange = .05;
        double lastSetSpeed; 
        double adjustedSpeed = 0;

        if (normalDrive) {
            adjustedSpeed = fwd * 1;
        } else {
            adjustedSpeed = fwd * .45;
        }

        lastSetSpeed = lastFWDvalue;

        if (Math.abs(adjustedSpeed) < .05) { // then we're stopping so handle that as
                                             // a special case
            if (lastSetSpeed > 0) {
                if (lastSetSpeed > maxSTOPPINGchange) {
                    adjustedSpeed = lastSetSpeed - maxSTOPPINGchange;
                } 
            } else {
                if (lastSetSpeed < maxSTOPPINGchange) {
                    adjustedSpeed = lastSetSpeed + maxSTOPPINGchange;
                }
            }

        } else {
            
            // This next section is identical in forwardAdjust and strafeAdjust
            if (adjustedSpeed >= 0) {
                if (adjustedSpeed > lastSetSpeed && adjustedSpeed > .25) { // speeding up so control it
                    if (adjustedSpeed >  lastSetSpeed + maxACCELchange) {
                        adjustedSpeed = lastSetSpeed + maxACCELchange;
                    } 
                } else if (adjustedSpeed <= lastSetSpeed) { 
                    // see if we're slowing down too fast
                    if (adjustedSpeed < lastSetSpeed - maxSTOPPINGchange) {
                        adjustedSpeed = lastSetSpeed - maxSTOPPINGchange;
                    }
                }
            } else {
                if (adjustedSpeed < lastSetSpeed && adjustedSpeed < -.2) { // speeding up in reverse
                    if (adjustedSpeed < lastSetSpeed - maxACCELchange) {
                        adjustedSpeed = lastSetSpeed - maxACCELchange;       
                    }
                } else if (adjustedSpeed >= lastSetSpeed) {
                    // see if we're slowing down too fast
                    if (adjustedSpeed > lastSetSpeed + maxSTOPPINGchange) {
                        adjustedSpeed = lastSetSpeed + maxSTOPPINGchange;
                    }
                }
            }

        }

        lastFWDvalue = adjustedSpeed;
        return lastFWDvalue;
    }

    private double forwardAdjust(double fwd, boolean normalDrive) {
        final double maxACCELchange = .02;
        final double maxSTOPPINGchange = .03;
        double lastSetSpeed; 
        double adjustedSpeed = 0;

        if (normalDrive) {
            adjustedSpeed = fwd * 1;
        } else {
            adjustedSpeed = fwd * .45;
        }

        lastSetSpeed = lastFWDvalue;

        // This next section is identical in forwardAdjust and strafeAdjust
        if (adjustedSpeed >= 0) {
            if (adjustedSpeed > lastSetSpeed && adjustedSpeed > .2) { // speeding up so control it
                if (adjustedSpeed >  lastSetSpeed + maxACCELchange) {
                    adjustedSpeed = lastSetSpeed + maxACCELchange;
                } 
            } else if (adjustedSpeed < lastSetSpeed) { 
                // see if we're slowing down too fast
                if (adjustedSpeed < lastSetSpeed - maxSTOPPINGchange) {
                    adjustedSpeed = lastSetSpeed - maxSTOPPINGchange;
                }
            }
        } else {
            if (adjustedSpeed < lastSetSpeed && adjustedSpeed < -.2) { // speeding up in reverse
                if (adjustedSpeed < lastSetSpeed - maxACCELchange) {
                    adjustedSpeed = lastSetSpeed - maxACCELchange;       
                }
            } else if (adjustedSpeed > lastSetSpeed) {
                // see if we're slowing down too fast
                if (adjustedSpeed > lastSetSpeed + maxSTOPPINGchange) {
                    adjustedSpeed = lastSetSpeed + maxSTOPPINGchange;
                }
            }
        }

        lastFWDvalue = adjustedSpeed;
        return lastFWDvalue;
    }
    private double strafeAdjustV2(double strafeAmt, boolean normalDrive) {
        final double maxACCELchange = .03;
        final double maxSTOPPINGchange = .05;
        double lastSetSpeed; 
        double adjustedSpeed = 0;
 
        if (normalDrive) {
            adjustedSpeed = strafeAmt * 1;
        } else {
            adjustedSpeed = strafeAmt * .45;
        }

        lastSetSpeed = lastSTRvalue;

        if (Math.abs(adjustedSpeed) < .05) { // then we're stopping so handle that as
                    // a special case
            if (lastSetSpeed > 0) {
                if (lastSetSpeed > maxSTOPPINGchange) {
                adjustedSpeed = lastSetSpeed - maxSTOPPINGchange;
                } 
            } else {
            if (lastSetSpeed < maxSTOPPINGchange) {
            adjustedSpeed = lastSetSpeed + maxSTOPPINGchange;
            }
            }

            } else {
    // This next section is identical in forwardAdjust and strafeAdjust
        if (adjustedSpeed >= 0) {
                if (adjustedSpeed > lastSetSpeed && adjustedSpeed > .25) { // speeding up so control it
                    if (adjustedSpeed >  lastSetSpeed + maxACCELchange) {
                        adjustedSpeed = lastSetSpeed + maxACCELchange;
                    } 
                } else if (adjustedSpeed < lastSetSpeed) { 
                    // see if we're slowing down too fast
                    if (adjustedSpeed < lastSetSpeed - maxSTOPPINGchange) {
                        adjustedSpeed = lastSetSpeed - maxSTOPPINGchange;
                    }
                }
            } else {
                if (adjustedSpeed < lastSetSpeed && adjustedSpeed < -.2) { // speeding up in reverse
                    if (adjustedSpeed < lastSetSpeed - maxACCELchange) {
                        adjustedSpeed = lastSetSpeed - maxACCELchange;       
                    }
                } else if (adjustedSpeed > lastSetSpeed) {
                    // see if we're slowing down too fast
                    if (adjustedSpeed > lastSetSpeed + maxSTOPPINGchange) {
                        adjustedSpeed = lastSetSpeed + maxSTOPPINGchange;
                    }
                }
            }
        }

        lastSTRvalue = adjustedSpeed;
        
        return lastSTRvalue;
    }
    private double strafeAdjust(double strafeAmt, boolean normalDrive) {
        final double maxACCELchange = .02;
        final double maxSTOPPINGchange = .04;
        double lastSetSpeed; 
        double adjustedSpeed = 0;
 
        if (normalDrive) {
            adjustedSpeed = strafeAmt * 1;
        } else {
            adjustedSpeed = strafeAmt * .45;
        }

        lastSetSpeed = lastSTRvalue;

       // This next section is identical in forwardAdjust and strafeAdjust
       if (adjustedSpeed >= 0) {
            if (adjustedSpeed > lastSetSpeed && adjustedSpeed > .2) { // speeding up so control it
                if (adjustedSpeed >  lastSetSpeed + maxACCELchange) {
                    adjustedSpeed = lastSetSpeed + maxACCELchange;
                } 
            } else if (adjustedSpeed < lastSetSpeed) { 
                // see if we're slowing down too fast
                if (adjustedSpeed < lastSetSpeed - maxSTOPPINGchange) {
                    adjustedSpeed = lastSetSpeed - maxSTOPPINGchange;
                }
            }
        } else {
            if (adjustedSpeed < lastSetSpeed && adjustedSpeed < -.2) { // speeding up in reverse
                if (adjustedSpeed < lastSetSpeed - maxACCELchange) {
                    adjustedSpeed = lastSetSpeed - maxACCELchange;       
                }
            } else if (adjustedSpeed > lastSetSpeed) {
                // see if we're slowing down too fast
                if (adjustedSpeed > lastSetSpeed + maxSTOPPINGchange) {
                    adjustedSpeed = lastSetSpeed + maxSTOPPINGchange;
                }
            }
        }

        lastSTRvalue = adjustedSpeed;
        
        return lastSTRvalue;
    }

    private void showDashboardInfo() {
        SmartDashboard.putNumber("Gyro Relative", round2(RobotGyro.getRelativeAngle()));
        SmartDashboard.putNumber("Gyro Raw", round2(RobotGyro.getAngle()));

        if (SmartDashboard.getBoolean("Show Encoders", true)) {
            DriveTrain.showTurnEncodersOnDash();
            DriveTrain.showDriveEncodersOnDash();
        }
        SmartDashboard.updateValues();
    }

    private static Double round2(Double val) {
        // added this back in on 1/15/18
        return new BigDecimal(val.toString()).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}


