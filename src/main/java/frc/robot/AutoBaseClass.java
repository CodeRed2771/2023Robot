package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.libs.Timer;

public abstract class AutoBaseClass {
    private Timer mAutoTimer; // note that the timer is ticked in isRunning() and hasCompleted()
    private Position mRobotPosition;
    private boolean mIsRunning = false;
    private Direction mDirection;
    private boolean mAutoPlace = false;
    public int placeNumInAuto = 2;
    private AutoType autoMode;
    // private double positionDesired;

    public static enum AutoType {
        NonDriveAuto,
        DriveAuto,
    }

    public static enum Direction {
        LEFT, RIGHT
    };

    public static enum Position {
        LEFT, CENTER, RIGHT
    };

    public AutoBaseClass(AutoType autoMode) {
        this.autoMode = autoMode;
        mAutoTimer = new Timer();
    }
    public AutoBaseClass() {
        autoMode = AutoType.DriveAuto;
        mAutoTimer = new Timer();
    }

    public abstract void tick();

    public void start() {
        mAutoTimer.setStep(0);
        mIsRunning = true;

        if(autoMode == AutoType.DriveAuto) {
            DriveAuto.reset();

            // set current position equal to Gyro
            // otherwise turns will be relative to 0
            DriveAuto.setTurnDegreesToCurrentAngle();
        }
    }

    public void start(Position robotPosition) {
        mRobotPosition = robotPosition;
        start();
    }

    public void start(char robotPosition) {
        if (robotPosition == 'L') {
            mRobotPosition = Position.LEFT;
        } else if (robotPosition == 'R') {
            mRobotPosition = Position.RIGHT;
        } else {
            mRobotPosition = Position.CENTER;
        }
        start();
    }

    public void start(Direction direction) {
        mDirection = direction;
        start();
    }

    public void start(boolean autoPlace){
       mAutoPlace = autoPlace;
       start(); 
    }
    public void start(double desiredPosition){
        start(); 
     }

    public void stop() {
        mIsRunning = false;
        DriveAuto.stop();
    }

    public boolean isRunning() {
        mAutoTimer.tick(); // we need to tick the timer and this is a good place to do it.
        if(autoMode == AutoType.DriveAuto) {
            DriveAuto.tick();
        }
        return mIsRunning;
    }

    public boolean hasCompleted() {
        mAutoTimer.tick(); // we need to tick the timer and this is a good place to do it.
        return !mIsRunning;
    }

    public int getCurrentStep() {
        return mAutoTimer.getStep();
    }

    public void setStep(int step) {
        mAutoTimer.setStep(step);
    }

    public double getStepTimeRemainingInSeconds() {
        return mAutoTimer.getTimeRemainingSeconds();
    }

    public double getStepTimeRemainingInMilliSeconds() {
        return mAutoTimer.getTimeRemainingMilliseconds();
    }

    public void driveInches(double distance, double angle, double maxPower) {
        if(autoMode == AutoType.DriveAuto) {
            DriveAuto.driveInches(distance, angle, maxPower, false);
        }
    }

    public void driveInches(double distance, double angle, double maxPower, boolean followTarget, boolean fieldCentric) {
        if(autoMode == AutoType.DriveAuto) {
            DriveAuto.driveInches(distance, angle, maxPower, followTarget, fieldCentric);
        }
    }

    public void turnToHeading(double desiredHeading, double turnSpeedFactor) {
        if(autoMode == AutoType.DriveAuto) {
            DriveAuto.turnToHeading(desiredHeading, turnSpeedFactor);
        }
    }

    public boolean driveCompleted() {
        return DriveAuto.hasArrived();
    }

    public boolean turnCompleted() {
        return DriveAuto.turnCompleted();
    }

    public boolean turnCompleted(double allowedErrorDegrees) {
        return DriveAuto.turnCompleted(allowedErrorDegrees);
    }

    public void turnDegrees(double degrees, double maxPower) {
        if(autoMode == AutoType.DriveAuto) {
            DriveAuto.turnDegrees(degrees, maxPower);
        }
    }

    public void continuousDrive(double inches, double maxPower) {
        if(autoMode == AutoType.DriveAuto) {
            DriveAuto.continuousDrive(inches, maxPower);
        }
    }

    public Position robotPosition() {
        return mRobotPosition;
    }

    public void setRobotPosition(Position position) {
        mRobotPosition = position;
    }

    public Direction slideDirection() {
        return mDirection;
    }

    public boolean autoPlace(){
        return mAutoPlace;
    }

    public void advanceStep() {
        mAutoTimer.stopTimerAndAdvanceStep();
    }

    // starts a timer for the time indicated and then immediately advances the
    // stage counter
    // this is typically used when starting a driving maneuver because the next
    // stage would
    // be watching to see when the maneuver was completed.
    public void setTimerAndAdvanceStep(long milliseconds) {
        mAutoTimer.setTimerAndAdvanceStep(milliseconds);
    }

    public void setTimer(long milliseconds) {
        mAutoTimer.setTimer(milliseconds);
    }

    public boolean timeExpired() {
        return mAutoTimer.timeExpired();
    }

    public Alliance getAlliance() {
        return DriverStation.getAlliance();
    }

}
