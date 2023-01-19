package frc.robot;

import javax.lang.model.util.ElementScanner6;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//The purpose of this class is to turn the robot until we are on target.

public class AutoAlign extends AutoBaseClass {

    private double angleOffset = 0;
    private boolean wasAligned = false;
    private static boolean alingnedComplete = false;
    private static double timer = 0;

    public AutoAlign () {
        
    }

    public void start() {
        super.start();
        wasAligned = false;
    }

    public void start(boolean autoShoot){
        super.start(autoShoot);
        wasAligned = false;
        angleOffset = 0;
    }

    public void stop() {
        super.stop();
    }

    public boolean wasAligned() {
        return wasAligned;
    }
    public static boolean getAllignment() {
        return alingnedComplete;
    }
    public static void setAllignment(boolean allignment) {
        alingnedComplete = allignment;
    }
    @Override
    public void tick() {
        if (isRunning()) {
            DriveAuto.tick();
            SmartDashboard.putNumber("AutoAlighn Auto Step", getCurrentStep());
            switch (getCurrentStep()) {
            case 0:
                VisionShooter.setVisionTrackingMode();
                VisionShooter.setTargetForShooting();
                // Intake.moveIntakeDown();
                advanceStep();
                break;
            case 1:
                setTimerAndAdvanceStep(1000);
                break;
            case 2:
                if (VisionShooter.seesTarget()) {
                    angleOffset = VisionShooter.getDistanceAdjustedAngle();
                    advanceStep();
                }
                SmartDashboard.putNumber("Adj Angle Offset", angleOffset);
                SmartDashboard.putNumber("Angle Offset", VisionShooter.getAngleOffset());
                SmartDashboard.putBoolean("Sees Target", VisionShooter.seesTarget());
                break;
            case 3:
                timer ++;
                if (Math.abs(angleOffset) > 1) {
                    DriveAuto.turnDegrees(angleOffset, 1);
                    setTimerAndAdvanceStep(8500);
                } else {
                    setStep(5);
                }
                if (autoShoot()){
                    if (!Shooter.isShooterEnabled()) {
                        Shooter.StartShooter();
                    }
                }
                if (timer > 750) {
                    setStep(5);
                }
                break;
            case 4:
                if (driveCompleted()) {
                    advanceStep();
                }
                break;
            case 5:
                wasAligned = true;
                // Vision.flashLED();
                System.out.println("On Target!");
                advanceStep();
                break;
            case 6: 
                if (autoShoot()){
                    if (!Shooter.getManualOveride() && !Shooter.isAlignOnly()) {
                        Shooter.setupShooterAuto();
                    }
                }
                setTimerAndAdvanceStep(700);
                break;
            case 7:
                break;
            case 8:
                if (autoShoot()){
                    Shooter.oneShotAuto();
                }
                alingnedComplete = true;
                stop();
                break;
            }
        }
    }
}