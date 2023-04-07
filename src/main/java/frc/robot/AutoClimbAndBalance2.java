package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoClimbAndBalance2 extends AutoBaseClass {
    public void start() {
        super.start();
    }

    public void stop() {
        super.stop();
    }

    double currentPitch;
    double startPitch = 0;
    double endPitch = 0;
    double currentTime;
    final double DRIVE_DISTANCE = 2;
    double startTimeForPitchSamples;
    final double TIME_TO_NEXT_PITCH_CHECK = 500;
    double startTimeForLevelTesting;
    final double TIME_UNTIL_ASURED_LEVEL = 500;
    final double ALLOWABLE_PITCH_ERROR_FOR_BALANCE =  .2;
    final double PITCH_CHANGE_INDICATING_MOTION = .2;

        // final double DRIVE_DISTANCE = 2;
    // double lastPitchPosition;
    // double currentPitchPosition;
    // double startTime;
    // final double TIME_TO_NEXT_PITCH_CHECK = 500;

// if we are ! changing and not level --> Drive
// if changing --> wait
// if level --> wait for certain time
    @Override
    public void tick() {
        currentPitch = RobotGyro.pitch();
        currentTime = System.currentTimeMillis();
        SmartDashboard.putNumber("Difference In Pitch", endPitch-startPitch);
        if(isRunning()) {
            switch(getCurrentStep()) {
                case 0:
                    if(Math.abs(currentPitch) > ALLOWABLE_PITCH_ERROR_FOR_BALANCE) {
                        driveInches(Math.signum(currentPitch) * DRIVE_DISTANCE, 0, .6);
                    } 
                    startTimeForPitchSamples = currentTime;
                    startPitch = currentPitch;
                    advanceStep();
                    break;
                case 1:
                    if(currentTime > (startTimeForPitchSamples + TIME_TO_NEXT_PITCH_CHECK)) {
                        endPitch = currentPitch;
                        advanceStep();
                    }
                    break;
                case 2:
                    if(Math.abs(endPitch - startPitch) > PITCH_CHANGE_INDICATING_MOTION) {
                        // Moving                        
                        startTimeForPitchSamples = currentTime;
                        startPitch = currentPitch;
                        setStep(1);
                    } else if (Math.abs(currentPitch) < ALLOWABLE_PITCH_ERROR_FOR_BALANCE) {
                        // Level
                        startTimeForLevelTesting = currentTime;
                        advanceStep();
                    } else {
                        // Not Level
                        setStep(0);
                    }
                    break;
                case 3:
                    if(currentTime > (startTimeForLevelTesting + TIME_UNTIL_ASURED_LEVEL)) {
                        DriveAuto.parkingBrake();
                        stop();
                    }
                    break;
            }
        }

        
    }
    
}
