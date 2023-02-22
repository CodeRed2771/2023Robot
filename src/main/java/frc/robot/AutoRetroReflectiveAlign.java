package frc.robot;

import javax.lang.model.util.ElementScanner6;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//The purpose of this class is to turn the robot until we are on target.

public class AutoRetroReflectiveAlign extends AutoBaseClass {

    private double strafeDistance = 0;
    private boolean wasAligned = false;
    private static boolean alingnedComplete = false;
    private static double timer = 0;

    public void start() {
        super.start();
        wasAligned = false;
    }

    public void start(boolean autoPlace){
        super.start(autoPlace);
        wasAligned = false;
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
            SmartDashboard.putNumber("AutoAlign Auto Step", getCurrentStep());
            switch (getCurrentStep()) {
            case 0:
                VisionPlacer.setRetroreflectivePipeline();
                // Intake.moveIntakeDown();
                advanceStep();
                break;
            case 1:
                if (VisionPlacer.seesTarget()) {
                    strafeDistance = VisionPlacer.getXAngleOffset();
                    advanceStep();
                }
                SmartDashboard.putNumber("Adj Angle Offset", strafeDistance);
                SmartDashboard.putNumber("Angle Offset", VisionPlacer.getXAngleOffset());
                SmartDashboard.putBoolean("Sees Target", VisionPlacer.seesTarget());
                break;
            case 2:
                timer++;
                if (Math.abs(strafeDistance) > 1) {
                    DriveAuto.driveInches(strafeDistance, 90,0.4);
                    setTimerAndAdvanceStep(2000);
                } else {
                    setStep(5);
                }
                if (timer > 750) {
                    setStep(5);
                }
                break;
            case 3:
                if (driveCompleted()) {
                    advanceStep();
                }
                break;
            case 4:
                wasAligned = true;
                // Vision.flashLED();
                System.out.println("On Target!");
                advanceStep();
                break;
            case 5:
                break;
            case 6:
                // if (autoPlace()){
                //     Claw.openClaw();
                // }
                alingnedComplete = true;
                stop();
                break;
            }
        }
    }
}