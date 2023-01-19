package frc.robot.libs.HID;

public abstract class DirectAccessKeyMap {

    public abstract HID getHID(int gamepad);

    public boolean getBumperLeft(int gamePadNumber) {
        return getHID(gamePadNumber).button(LogitechF310.BUMPER_LEFT);
    }

    public boolean getButtonA(int gamePadNumber) {
        return getHID(gamePadNumber).button(LogitechF310.A);
    }

    public boolean getButtonB(int gamePadNumber) {
        return getHID(gamePadNumber).button(LogitechF310.B);
    }

    public boolean getButtonX(int gamePadNumber) {
        return getHID(gamePadNumber).button(LogitechF310.X);
    }

    public boolean getButtonY(int gamePadNumber) {
        return getHID(gamePadNumber).button(LogitechF310.Y);
    }

    public boolean getButtonTriggerRight(int gamePadNumber) {
        return getHID(gamePadNumber).button(LogitechF310.TRIGGER_RIGHT);
    }

    public boolean getButtonTriggerLeft(int gamePadNumber) {
        return getHID(gamePadNumber).button(LogitechF310.TRIGGER_LEFT);
    }

    public boolean getButtonBumperLeft(int gamePadNumber) {
        return getHID(gamePadNumber).button(LogitechF310.BUMPER_LEFT);
    }

    public boolean getButtonDpadUp(int gamePadNumber) {
        return getHID(gamePadNumber).button(LogitechF310.DPAD_UP);
    }

    public boolean getButtonDpadDown(int gamePadNumber) {
        return getHID(gamePadNumber).button(LogitechF310.DPAD_DOWN);
    }

    public boolean getStartButton(int gamePadNumber) {
        return getHID(gamePadNumber).button(LogitechF310.START);
    }

    public boolean getDpadLeft(int gamePadNumber) {
        return getHID(gamePadNumber).button(LogitechF310.DPAD_LEFT);
    }

    public boolean getDpadRight(int gamePadNumber) {
        return getHID(gamePadNumber).button(LogitechF310.DPAD_RIGHT);
    }

    public boolean getDpadUp(int gamePadNumber) {
        return getHID(gamePadNumber).button(LogitechF310.DPAD_UP);
    }

    public boolean getDpadDown(int gamePadNumber) {
        return getHID(gamePadNumber).button(LogitechF310.DPAD_DOWN);
    }

    public double getLeftStickY(int gamePadNumber) {
        return getHID(gamePadNumber).axis(LogitechF310.STICK_LEFT_Y);
    }

    public double getRightStickY(int gamePadNumber) {
        return getHID(gamePadNumber).axis(LogitechF310.STICK_RIGHT_Y);
    }
}