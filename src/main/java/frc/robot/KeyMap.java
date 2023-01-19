package frc.robot;

import frc.robot.libs.HID.*;

public class KeyMap extends DirectAccessKeyMap {

    // GAMEPADS
    private final HID gp1 = new HID(0);
    private final HID gp2 = new HID(1);
    private final int gamepad1 = 0;
    private final int gamepad2 = 1;

    // MANAGEMENT BOOLEANS
    private boolean singleControllerMode = false;

    // CONTROLLER 1
        // private final HID.Axis swerveXAxis = LogitechExtreme3D.STICK_X;
        // private final HID.Axis swerveYAxis = LogitechExtreme3D.STICK_Y;
        // private final HID.Axis shooterAdjuster = LogitechExtreme3D.SLIDER;
        // private final HID.Axis swerveRotAxis = LogitechExtreme3D.STICK_ROT;
        // private final HID.Button switchToRobotCentric = LogitechExtreme3D.Thumb;
        // private final HID.Button climberLowPosition = LogitechExtreme3D.Button1;
        // private final HID.Button automaticallyAdjustRobotToLevelScale = LogitechExtreme3D.Button3;
        // private final HID.Button turboTurning = LogitechExtreme3D.Button4;
        // private final HID.Button dropBellyPanForBuddyClimb = LogitechExtreme3D.Button5;
        // private final HID.Button pickUpBellyPanAndContinueClimbing = LogitechExtreme3D.Button6;
        // private final HID.Button startVision = LogitechExtreme3D.Button7;
        // private final HID.Button zeroGyro = LogitechExtreme3D.Button8;
        // private final HID.Button oneShotShooter = LogitechExtreme3D.Button9;
        // private final HID.Button continualShooter = LogitechExtreme3D.Button10;
        // private final HID.Button stopShooter = LogitechExtreme3D.Button11;
        // private final HID.Button climber = LogitechExtreme3D.Button12;
        // private final HID.Button turnToZeroDegrees = LogitechExtreme3D.HAT_BUTTON_UP;
        // private final HID.Button turnTo180Degrees = LogitechExtreme3D.HAT_BUTTON_DOWN;
    private final HID.Axis swerveXAxis = LogitechF310.STICK_LEFT_X;
    private final HID.Axis swerveYAxis = LogitechF310.STICK_LEFT_Y;
    // private final HID.Axis shooterAdjuster = LogitechF310.STICK_RIGHT_Y;
    private final HID.Axis swerveRotAxis = LogitechF310.STICK_RIGHT_X;
    private final HID.Button switchToRobotCentric = LogitechF310.BACK;
    private final HID.Button climberModifier = LogitechF310.A;
    // private final HID.Button automaticallyAdjustRobotToLevelScale = LogitechF310.X;
    // private final HID.Button climbHighPosiButton = LogitechF310.DPAD_DOWN;
    private final HID.Button pickUpBellyPanAndContinueClimbing = LogitechF310.DPAD_UP;
    private final HID.Button startVision = LogitechF310.BUMPER_RIGHT;
    private final HID.Button zeroGyro = LogitechF310.START;
    private final HID.Axis oneShotShooter = LogitechF310.TRIGGER_RIGHT_AXIS;
    private final HID.Axis continualShooter = LogitechF310.TRIGGER_LEFT_AXIS;
    private final HID.Button stopShooter = LogitechF310.DPAD_LEFT;
    private final HID.Button climber = LogitechF310.TRIGGER_RIGHT;
    private final HID.Button turn180Degrees = LogitechF310.DPAD_RIGHT;
    private final HID.Button BallVision = LogitechF310.BUMPER_LEFT;
    // CONTROLLER 2
    private final HID.Button closeShooterPosition = LogitechF310.A;
    private final HID.Button midTrenchPosition = LogitechF310.B;
    private final HID.Button backTrenchPosition = LogitechF310.Y;
    private final HID.Button startShooter = LogitechF310.TRIGGER_RIGHT;
    private final HID.Button stopShooting = LogitechF310.BUMPER_RIGHT;
    private final HID.Button runIntakeBackWards = LogitechF310.BACK;
    private final HID.Button intakeUpPosition = LogitechF310.DPAD_UP;
    private final HID.Button intakeDownPosition = LogitechF310.DPAD_DOWN;
    private final HID.Button climbHighPosiButton = LogitechF310.START;
    // private final HID.Button climberModifier = LogitechF310.;
    // private final HID.Button colorWheelClimberHeight = LogitechF310.;
    private final HID.Button startIntake = LogitechF310.TRIGGER_LEFT;
    private final HID.Button stopIntake = LogitechF310.BUMPER_LEFT;
    // private final HID.Button colorWheelStartRotationControl = LogitechF310.;
    // private final HID.Button colorWheelStartPositionControl = LogitechF310.;
    private final HID.Axis manualShooterPivoterAdjuster = LogitechF310.STICK_RIGHT_Y;
    private final HID.Axis manualClimberAdjuster = LogitechF310.STICK_LEFT_Y;



    public HID getHID(int gamepad) {
        if (!singleControllerMode) {
            switch (gamepad) {
            case gamepad1:
                return gp1;
            case gamepad2:
                return gp2;
            default:
                return null;
            }
        } else {
            return gp1;
        }
    }

    public boolean getZeroGyro() {
        return getHID(gamepad1).button(zeroGyro);
    }

    public boolean turn180Degrees () {
        return getHID(gamepad1).button(turn180Degrees);
    }

    public double getSwerveRotAxis() {
        return getHID(gamepad1).axis(swerveRotAxis);
    }

    public double getSwerveXAxis() {
        return getHID(gamepad1).axis(swerveXAxis);
    }

    public double getSwerveYAxis() {
        return getHID(gamepad1).axis(swerveYAxis);
    }

    public boolean climber () {
        return getHID(gamepad1).button(climber);
    }

    // public boolean turnToZeroDegrees () {
    //     return getHID(gamepad1).button(turnToZeroDegrees);
    // }

    // public boolean turnTo180Degrees () {
    //     return getHID(gamepad1).button(turnTo180Degrees);
    // }

    // public boolean levelScale () {
    //     return getHID(gamepad1).button(automaticallyAdjustRobotToLevelScale);
    // }

    public boolean oneShotShooter () {
        return Math.abs(getHID(gamepad1).axis(oneShotShooter)) > 0.2;
    }

    public boolean continualShooter () {
        return Math.abs(getHID(gamepad1).axis(continualShooter))  > 0.2;
    }

    public double liftSpeed () {
        return Math.abs(getHID(gamepad1).axis(continualShooter));
    }

    public boolean stopShooting () {
        return getHID(gamepad1).button(stopShooter);
    }
    // public boolean turboTurning () {
    //     return getHID(gamepad1).button(turboTurning);
    // }

    // public boolean dropBellyPan () {
    //     return getHID(gamepad1).button(dropBellyPanForBuddyClimb);
    // }

    public boolean pickUpbellyPanContinueClimb () {
        return getHID(gamepad1).button(pickUpBellyPanAndContinueClimbing);
    }

    public boolean getRobotCentricModifier() {
        return getHID(gamepad1).button(switchToRobotCentric);
    }

    public boolean startVision () {
        return getHID(gamepad1).button(startVision);
    }

    public boolean lowClimberHeight () {
        return getHID(gamepad1).button(climbHighPosiButton);
    }

    // public double getShooterAdjustment () {
    //     return getHID(gamepad1).axis(shooterAdjuster);
    // }

    public boolean startIntake() {
        return getHID(gamepad2).button(startIntake);
    }

    public boolean reverseIntake() {
        return getHID(gamepad2).button(stopIntake);
    }

    public boolean stopIntake() {
        return getHID(gamepad2).button(stopIntake);
    }

    // public boolean spinWheel() {
    //     return getHID(gamepad2).button(colorWheelStartRotationControl);
    // }

    // public boolean matchColor() {
    //     return getHID(gamepad2).button(colorWheelStartRotationControl);
    // }

    public boolean intakeUpPosition () {
        return getHID(gamepad2).button(intakeUpPosition);
    }

    public boolean intakeDownPosition() {
        return getHID(gamepad2).button(intakeDownPosition);
    }

    // public boolean climberLowPosition () {
    //     return getHID(gamepad2).button(lowClimberHeight);
    // }

    public boolean startShooter() {
        return getHID(gamepad2).button(startShooter);
    }

    public boolean stopShooter() {
        return getHID(gamepad2).button(stopShooting);
    }

    public boolean closeShooterPosition () {
        return getHID(gamepad2).button(closeShooterPosition);
    }

    public boolean midTrenchPosition () {
        return getHID(gamepad2).button(midTrenchPosition);
    }

    public boolean backTrenchPosition () {
        return getHID(gamepad2).button(backTrenchPosition);
    }

    // public boolean colorWheelClimberHeight () {
    //     return getHID(gamepad2).button(colorWheelClimberHeight);
    // }

    // public boolean colorWheenPositionColor () {
    //     return getHID(gamepad2).button(colorWheelStartPositionControl);
    // }

    public double shooterPivoterAdjuster () {
        return getHID(gamepad2).axis(manualShooterPivoterAdjuster);
    }

    public double manualClimberAdjuster () {
        return getHID(gamepad2).axis(manualClimberAdjuster);
    }
    public boolean runIntakeBackWards () {
        return getHID(gamepad2).button(runIntakeBackWards);
    }
}
