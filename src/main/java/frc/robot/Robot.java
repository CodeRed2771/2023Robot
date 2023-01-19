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
import frc.robot.Climber.ClimberPosition;
import frc.robot.Shooter.ManualShotPreset;
import frc.robot.Shooter.ShooterPosition;
import frc.robot.libs.HID.Gamepad;
import pabeles.concurrency.IntOperatorTask.Max;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

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
    boolean rampCodeActive = true;

    AutoBaseClass mAutoProgram;

    final String autoCalibrator = "Auto Calibrator";
    final String autoWheelAlign = "Auto Wheel Align";
    final String autoAlign = "Auto Align";
    final String ballPickUp = "Auto Ball Pick Up";
    final String AutoLeaveTarmac = "Auto Leave Tarmac";
    final String AutoTarmacShoot1 = "Auto Tarmac Shoot 1";
    final String AutoTarmacShoot2 = "Auto Tarmac Shoot 2";
    final String AutoTarmacShoot2Vision = "Auto Tarmac Shoot 2 Vision";
    final String AutoTarmacShoot3Vision = "Auto Tarmac Shoot 3 Vision";

    private boolean isIntakeUpPosition = true;
    private boolean intakeKeyAlreadyPressed = false;

    private double lastFWDvalue = 0; 
    private double lastSTRvalue = 0;
    private double lastROTvalue = 0;

    @Override
    public void robotInit() {
        gamepad1 = new Gamepad(0);
        gamepad2 = new Gamepad(1);
        SmartDashboard.putString("Alliance Decided", DriverStation.getAlliance().toString());
        compressor.enableAnalog(100, 120);

        Intake.init();
        Shooter.init();
        RobotGyro.init();
        Climber.init();

        // swtest = new SwerveTurnTest();

        Calibration.loadSwerveCalibration();
        if (Calibration.isPracticeBot()) 
            DriveTrain.init("NEO");
        else
            DriveTrain.init("FALCON");
        
        DriveAuto.init();

        SmartDashboard.putNumber("Current Position", 0);
        SmartDashboard.putNumber("New Position", 0);
        SmartDashboard.putBoolean("Show Encoders", true);
        SmartDashboard.putBoolean("Tune Drive-Turn PIDs", false);

        VisionShooter.init(); // Limelight shooter vision tracking
        setupAutoChoices();
        mAutoProgram = new AutoDoNothing();

        // VisionBall.init(); // Ball vision tracking setup
        // VisionBall.start();
    }

    @Override
    public void teleopInit() {
        mAutoProgram.stop();
      
        DriveTrain.stopDriveAndTurnMotors();
        DriveTrain.allowTurnEncoderReset();
        DriveTrain.resetTurnEncoders();
        DriveTrain.setAllTurnOrientation(0, false); // sets them back to calibrated zero position
        // VisionBall.start(); // (3/26/22 - this crashes the program when run a second time)
        Shooter.StartShooter();
        Shooter.setSpeed(6000);
        Climber.reset();
    }

    @Override
    public void teleopPeriodic() {

        // SmartDashboard.putNumber("Ball # Selected" , VisionBall.getClosestBallIndex());
        // SmartDashboard.putNumber("Y Offset", VisionBall.getBallYOffset());
        // SmartDashboard.putNumber("Number of Balls", VisionBall.getBallNumber());
        // SmartDashboard.putNumber("Score", VisionBall.getBallScore());
        // SmartDashboard.putNumber("Center X", VisionBall.centerX());
        // SmartDashboard.putNumber("Center Y", VisionBall.centerY());
        // SmartDashboard.putNumber("Distance to Ball", VisionBall.distanceToBall());

        if (gamepad1.getRightTriggerAxis() > 0 || gamepad2.getRightTriggerAxis() > 0) {
            Intake.startIntake();
        } else {
            Intake.stopIntake();
        }
        if (gamepad2.getLeftBumper()) {
            Intake.reverseIntake();
        }

        if (gamepad2.getDPadUp() && !Intake.getIntakePosition()) {
            Intake.deployIntake();
         } else if (gamepad2.getDPadDown() && Intake.getIntakePosition()) {
             Intake.retractIntake();
         }
        SmartDashboard.putNumber("D pad", gamepad1.getPOV(1));

        if (gamepad1.getLeftBumper() && !mAutoProgram.isRunning() && !AutoAlign.getAllignment()) {
            mAutoProgram = new AutoAlign();
            mAutoProgram.start(true);
        } else if (gamepad1.getLeftBumper() && AutoAlign.getAllignment()) {
            Shooter.alignAndShoot(true);
        }
        
         if (gamepad2.getAButton()) {
             Shooter.setAutoDistancingMode();
            // Shooter.setManualPresets(ManualShotPreset.LowGoal);
        } else if (gamepad2.getBButton()) {
            Shooter.setManualPresets(ManualShotPreset.TarmacLine);
        } else if (gamepad2.getYButton()) {
            //Shooter.setManualPresets(ManualShotPreset.Backwards);
            Shooter.setManualPresets(ManualShotPreset.SafeZone);
        } else if (gamepad2.getXButton()) {
            Shooter.setManualPresets(ManualShotPreset.HumanPlayerStation);
        }

        if (gamepad2.getLeftTriggerAxis() > 0 ||gamepad1.getLeftTriggerAxis() > 0) {
            Shooter.oneShotAuto();
        }

        if (gamepad2.getStartButton()) {
            Shooter.reverseShooter();
        } else {
            Shooter.endReverseShooter();
        }

        if (gamepad2.getRightBumper() && gamepad2.getBackButton() && !mAutoProgram.isRunning() ) {
            mAutoProgram = new AutoTraverse();
            mAutoProgram.start();
        }
            

        if (gamepad2.getRightBumper() || gamepad1.getRightBumper()) {
            Shooter.StopShooter();
        }

        if (gamepad2.getLeftStickButtonPressed()) {
            Shooter.setBallLiftDown();
        }
        
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

        if (gamepad1.getXButton()) {
            VisionShooter.setLED(true);
        } else if (gamepad1.getYButton()) {
            VisionShooter.setLED(false);
        }

        if (!mAutoProgram.isRunning()) {
            if (gamepad2.getRightBumper()) {  // override mode
                if (Math.abs(gamepad2.getRightY()) > 0.05) {
                    Climber.move(gamepad2.getRightY());
                } else
                    Climber.move(0);
            } else {
                if (Math.abs(gamepad2.getRightY()) > 0.05) {
                    Climber.moveV2(gamepad2.getRightY());
                } else 
                    Climber.moveV2(0);
            }
        }
        if (mAutoProgram.isRunning() && Math.abs(gamepad2.getRightY()) > 0.2) {
            mAutoProgram.stop();
        }

        if (gamepad2.getLeftY() > 0.5) {
            Climber.climberPosition(ClimberPosition.Back);
        } else if (gamepad2.getLeftY() < -0.5) {
            Climber.climberPosition(ClimberPosition.Straight);
        }

        if (gamepad1.getRightBumper() && gamepad1.getYButton()) {
            mAutoProgram = new AutoBallAlign();
            mAutoProgram.start(true);
        }

        // DRIVER CONTROL MODE
        // Issue the drive command using the parameters from
        // above that have been tweaked as needed
        double driveRotAmount = -gamepad1.getRightX();
        double driveFWDAmount = -gamepad1.getLeftY();
        double driveStrafeAmount = -gamepad1.getLeftX();

       // double ballLaneAssist = VisionBall.getBallXOffset();

        // if (Math.abs(driveRotAmount)>.1) {
        //     swtest.move(driveRotAmount);
        // }

        // if (gamepad1.getAButton()) {
        //     swtest.setPosA();
        // }
        // if (gamepad1.getBButton()) {
        //     swtest.setPosB();
        // }
        // if (gamepad1.getXButton()) {
        //     swtest.setPosOrig();
        // }
        // swtest.tick();

        // SmartDashboard.putNumber("SWERVE ROT AXIS", driveRotAmount);
        if (gamepad1.getAButton()) {
            // Shooter.StartShooter();
            rampCodeActive = true;
        } else if (gamepad1.getBButton()) {
            rampCodeActive = false;
        }
        if (rampCodeActive) {
            driveRotAmount = rotationalAdjust(driveRotAmount);
            // SmartDashboard.putNumber("ADJUSTED SWERVE ROT AMOUNT", driveRotAmount);
            driveFWDAmount = forwardAdjustV2(driveFWDAmount, true);
            driveStrafeAmount = strafeAdjustV2(driveStrafeAmount, true);
        }

        if (gamepad1.getRightBumper()) {  // slow mode
            driveFWDAmount = driveFWDAmount * .3;
            driveStrafeAmount = driveStrafeAmount * .3;
            driveRotAmount = driveRotAmount * .3;
        }

        SmartDashboard.putNumber("Best Position", TurnPosition.getBestPosition());

        // if (ballTrackingTurnedOn && VisionBall.ballInView()) {
            // if (Intake.isRunning()) {
            //     if (ballLaneAssist > 0.05) 
            //         driveStrafeAmount += .2;
            //     else if (ballLaneAssist < -0.05) 
            //         driveStrafeAmount -= .2;
            // }


        //     if (Intake.isRunning()) {
        //         if (RobotGyro.getAngle() == 0 || RobotGyro.getAngle() == 180) {
        //             if(ballLaneAssist > 0.05) {
        //                 driveStrafeAmount += .2;
        //             } else if (ballLaneAssist < -0.05) {
        //                 driveStrafeAmount += -.2;
        //             }
        //         } else if (RobotGyro.getAngle() == 90 || RobotGyro.getAngle() == 270) {
        //             if(ballLaneAssist > 0.05) {
        //                 driveFWDAmount += .2;
        //             } else if (ballLaneAssist < -0.05) {
        //                 driveFWDAmount += -.2;
        //             }
        //         }
        //         else {
        //             if(ballLaneAssist > 0.05) {
        //                 driveFWDAmount += Math.cos(VisionBall.degreesToBall()) * 0.2;
        //                 driveStrafeAmount += Math.sin(VisionBall.degreesToBall()) * 0.2;
        //             }else if (ballLaneAssist < -0.05) {
        //                 driveFWDAmount += Math.cos(VisionBall.degreesToBall()) * -0.2;
        //                 driveStrafeAmount += Math.sin(VisionBall.degreesToBall()) * -0.2;
        //             }
        //         }
        //     }
        // }

        SmartDashboard.putNumber("Outputs FWD", driveFWDAmount);
        SmartDashboard.putNumber("Outputs Strafe", driveStrafeAmount);

        if (Math.abs(driveFWDAmount) > .5 || Math.abs(driveRotAmount) > .5) {
            if (mAutoProgram.isRunning())
                mAutoProgram.stop();
        }

        if (!mAutoProgram.isRunning()) {
            if (gamepad1.getBackButton()) {
                DriveTrain.humanDrive(driveFWDAmount, driveStrafeAmount, driveRotAmount);
                AutoAlign.setAllignment(false);
            } else {
                DriveTrain.fieldCentricDrive(driveFWDAmount, driveStrafeAmount, driveRotAmount);
                AutoAlign.setAllignment(false);
            }
        }

        showDashboardInfo();
    }

    @Override
    public void robotPeriodic() {
        SmartDashboard.updateValues();
        // SmartDashboard.putNumber("Number of Balls", VisionBall.getBallNumber());
        // SmartDashboard.putBoolean("Working", VisionBall.working());
        Shooter.tick();
        Intake.tick();
        DriveAuto.tick();
        Climber.tick();

        // SmartDashboard.putNumber("Ball X Offset", VisionBall.getBallXOffset());
        // SmartDashboard.putNumber("Ball Y Offset", VisionBall.getBallYOffset());
        // SmartDashboard.putNumber("Degrees Output", VisionBall.degreesToBall());
        // SmartDashboard.putNumber("Distance to Target", VisionShooter.getDistanceFromTarget());

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

            DriveTrain.setDriveMMAccel((int) SmartDashboard.getNumber("DRIVE MM ACCEL", Calibration.DT_MM_ACCEL));
            DriveTrain.setDriveMMVelocity(
                    (int) SmartDashboard.getNumber("DRIVE MM VELOCITY", Calibration.DT_MM_VELOCITY));
        }
        RobotGyro.position();
        // SmartDashboard.putNumber("Position X", RobotGyro.getPosition().x);
        // SmartDashboard.putNumber("Position Y", RobotGyro.getPosition().y);
        // SmartDashboard.putNumber("Position Z", RobotGyro.getPosition().z);
        SmartDashboard.putNumber("Velocity X", RobotGyro.velocityX());
        SmartDashboard.putNumber("Velocity Y", RobotGyro.velocityY());
        SmartDashboard.putNumber("Velocity Z", RobotGyro.velocityZ());
        SmartDashboard.putNumber("Pitch", RobotGyro.pitch());
        SmartDashboard.putNumber("Roll", RobotGyro.roll());
        SmartDashboard.putNumber("Yaw", RobotGyro.yaw());
    }

    @Override
    public void autonomousInit() {

        // RobotGyro.reset();

        DriveTrain.stopDriveAndTurnMotors();
        DriveTrain.allowTurnEncoderReset();
        DriveTrain.resetTurnEncoders();
        DriveTrain.setAllTurnOrientation(0, false); // sets them back to calibrated zero position

        mAutoProgram.stop();
        String selectedPos = positionChooser.getSelected();
        SmartDashboard.putString("Position Chooser Selected", selectedPos);
        char robotPosition = selectedPos.toCharArray()[0];
        System.out.println("Robot position: " + robotPosition);

        // Shooter.setShooterPosition(ShooterPosition.Medium); // releases the starting position block
        Intake.startIntake(); // releases the intake strap

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
        case autoAlign:
            mAutoProgram = new AutoAlign();
            mAutoProgram.start(robotPosition);
            break;
        case ballPickUp:
            mAutoProgram = new AutoBallPickUp();
            mAutoProgram.start();
            break;
        case AutoLeaveTarmac:
            mAutoProgram = new AutoLeaveTarmack();
            mAutoProgram.start();
            break;
        case AutoTarmacShoot1:
            mAutoProgram = new AutoTarmacShoot1();
            mAutoProgram.start();
            break;
        case AutoTarmacShoot2:
            mAutoProgram = new AutoTarmacShoot2();
            mAutoProgram.start();
            break;
        case AutoTarmacShoot2Vision:
            mAutoProgram = new AutoTarmacShoot2Vision();
            mAutoProgram.start();
            break;
        case AutoTarmacShoot3Vision:
            mAutoProgram = new AutoTarmacShoot3Vision();
            mAutoProgram.start(true);
            break;
        }
        // double degreesOffset;
        // switch (postionAllianceSelected) {
        //     case "Red 1":
        //         degreesOffset = 15;
        //         break;
        //     case "Red 2":
        //         degreesOffset = 0;
        //         break;
        //     case "Red 3":
        //         degreesOffset = -15;
        //         break;
        //     case "Blue 1":
        //         degreesOffset = 15;
        //         break;
        //     case "Blue 2":
        //         degreesOffset = 0;
        //         break;
        //     case "Blue 3":
        //         degreesOffset = -15; 
        //         break;
        // }

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
        autoChooser.addOption(AutoLeaveTarmac, AutoLeaveTarmac);
        autoChooser.addOption(AutoTarmacShoot1, AutoTarmacShoot1);
        autoChooser.addOption(AutoTarmacShoot2, AutoTarmacShoot2);
        autoChooser.setDefaultOption(AutoTarmacShoot2Vision, AutoTarmacShoot2Vision);
        autoChooser.addOption(AutoTarmacShoot3Vision, AutoTarmacShoot3Vision);

        SmartDashboard.putData("Auto Chose:", autoChooser);

        // Position Chooser 2
        // positionAllianceChooser = new SendableChooser<String>();
        // positionAllianceChooser.addOption("Red 1", "Red 1");
        // positionAllianceChooser.addOption("Red 2", "Red 2");
        // positionAllianceChooser.addOption("Red 3", "Red 3");
        // positionAllianceChooser.addOption("Blue 1", "Blue 1");
        // positionAllianceChooser.addOption("Blue 2", "Blue 2");
        // positionAllianceChooser.addOption("Blue 3", "Blue 3");
    }

    /**
     * This function is called periodically during autonomous.
     */
    @Override
    public void autonomousPeriodic() {
        if (mAutoProgram.isRunning()) {
            mAutoProgram.tick();
        }
        showDashboardInfo();
    }

    public void disabledInit() {
        // allows the turn encoders to be reset once during disabled periodic
        // DriveTrain.allowTurnEncoderReset();
        // DriveTrain.resetDriveEncoders();
        // DriveTrain.resetTurnEncoders();

        Shooter.StopShooter();
        
        Calibration.initializeSmartDashboard(); 

        VisionShooter.setLED(false);
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

        if (Math.abs(rotateAmt) < .05) {
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


