package frc.robot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveTrain {

    public static SwerveModule moduleA, moduleB, moduleC, moduleD;

    public static void init(String driveMotorType) {
        
        if (driveMotorType.equals("NEO")) {
            moduleA = new SwerveModuleNEO(Calibration.DT_A_DRIVE_ID, Calibration.DT_A_TURN_ID, Wiring.TURN_ABS_ENC_A, Calibration.getTurnZeroPos('A'), 'A'); // Front right
            moduleB = new SwerveModuleNEO(Calibration.DT_B_DRIVE_ID, Calibration.DT_B_TURN_ID, Wiring.TURN_ABS_ENC_B, Calibration.getTurnZeroPos('B'), 'B'); // Back left
            moduleC = new SwerveModuleNEO(Calibration.DT_C_DRIVE_ID, Calibration.DT_C_TURN_ID, Wiring.TURN_ABS_ENC_C, Calibration.getTurnZeroPos('C'), 'C'); // Back right
            moduleD = new SwerveModuleNEO(Calibration.DT_D_DRIVE_ID, Calibration.DT_D_TURN_ID, Wiring.TURN_ABS_ENC_D, Calibration.getTurnZeroPos('D'), 'D'); // Front left
        } else {
            moduleA = new SwerveModuleFalcon(Calibration.DT_A_DRIVE_ID, Calibration.DT_A_TURN_ID, Wiring.TURN_ABS_ENC_A, Calibration.getTurnZeroPos('A'), 'A'); // Front right
            moduleB = new SwerveModuleFalcon(Calibration.DT_B_DRIVE_ID, Calibration.DT_B_TURN_ID, Wiring.TURN_ABS_ENC_B, Calibration.getTurnZeroPos('B'), 'B'); // Back left
            moduleC = new SwerveModuleFalcon(Calibration.DT_C_DRIVE_ID, Calibration.DT_C_TURN_ID, Wiring.TURN_ABS_ENC_C, Calibration.getTurnZeroPos('C'), 'C'); // Back right
            moduleD = new SwerveModuleFalcon(Calibration.DT_D_DRIVE_ID, Calibration.DT_D_TURN_ID, Wiring.TURN_ABS_ENC_D, Calibration.getTurnZeroPos('D'), 'D'); // Front left
        } 

        SmartDashboard.putNumber("TURN P", Calibration.getTurnP());
        SmartDashboard.putNumber("TURN I", Calibration.getTurnI());
        SmartDashboard.putNumber("TURN D", Calibration.getTurnD());

        allowTurnEncoderReset();
        resetTurnEncoders();
    }

    // define robot dimensions. L=wheel base W=track width
    private static final double l = 19, w = 19, r = Math.sqrt((l * l) + (w * w));

    public static void resetTurnZeroToCurrentPos() {
   		// sets the known "zero position" to be whatever we're at now.
		// should only be called when the modules are KNOWN to be straight.

        moduleA.resetZeroPosToCurrentPos();
        moduleB.resetZeroPosToCurrentPos();
        moduleC.resetZeroPosToCurrentPos();
        moduleD.resetZeroPosToCurrentPos();
    }

    public static void resetTurnReversedFlag() {
        moduleA.resetTurnReversedFlag();
        moduleB.resetTurnReversedFlag();
        moduleC.resetTurnReversedFlag();
        moduleD.resetTurnReversedFlag();
    }

    public static void setDrivePower(double modAPower, double modBPower, double modCPower, double modDPower) {
        moduleA.setDrivePower(modAPower);
        moduleB.setDrivePower(modBPower);
        moduleC.setDrivePower(modCPower);
        moduleD.setDrivePower(modDPower);
    }

    public static void setDriveMMAccel(int accel) {
        moduleA.setDriveMMAccel(accel);
        moduleB.setDriveMMAccel(accel);
        moduleC.setDriveMMAccel(accel);
        moduleD.setDriveMMAccel(accel);
    }

    public static void setDriveMMVelocity(int velocity) {
        moduleA.setDriveMMVelocity(velocity);
        moduleB.setDriveMMVelocity(velocity);
        moduleC.setDriveMMVelocity(velocity);
        moduleD.setDriveMMVelocity(velocity);
    }

    public static boolean hasDriveCompleted(double inchesError) {
        // just checking two of the modules to see if they are within the desired accuracy
        return moduleB.hasDriveCompleted(inchesError) && moduleA.hasDriveCompleted(inchesError);
    }

    public static boolean hasDriveCompleted() {
        return hasDriveCompleted(0.25);
    }

    public static void setTurnPower(double modAPower, double modBPower, double modCPower, double modDPower) {
        moduleA.setTurnPower(modAPower);
        moduleB.setTurnPower(modBPower);
        moduleC.setTurnPower(modCPower);
        moduleD.setTurnPower(modDPower);
    }

    public static void setTurnOrientation(double modAPosition, double modBPosition, double modCPosition,
            double modDPosition) {
        setTurnOrientation(modAPosition, modBPosition, modCPosition, modDPosition, false);
    }

    public static void setTurnOrientation(double modAPosition, double modBPosition, double modCPosition,
            double modDPosition, boolean optimizeTurn) {

        // position is a value from 0 to 1 that indicates
        // where in the rotation of the module the wheel should be set.
        // e.g. a value of .5 indicates a half turn from the zero position

        moduleA.setTurnOrientation(modAPosition, optimizeTurn);
        moduleB.setTurnOrientation(modBPosition, optimizeTurn);
        moduleC.setTurnOrientation(modCPosition, optimizeTurn);
        moduleD.setTurnOrientation(modDPosition, optimizeTurn);

        SmartDashboard.putNumber("A pos call", round(modAPosition,3));
        SmartDashboard.putNumber("B pos call", round(modBPosition,3));
        SmartDashboard.putNumber("C pos call", round(modCPosition,3));
        SmartDashboard.putNumber("D pos call", round(modDPosition,3));
    }

    public static void setAllTurnOrientation(double position) {
        setTurnOrientation(position, position, position, position, true);
    }

    /**
     * @param position     - position, 0 to 1, to turn to.
     * @param optimizeTurn - allow turn optimization
     */
    public static void setAllTurnOrientation(double position, boolean optimizeTurn) {
        setTurnOrientation(position, position, position, position, optimizeTurn);
    }

    public static void setAllDrivePosition(int position) {
        setDrivePosition(position, position, position, position);
    }

    public static void setDrivePosition(double modAPosition, double modBPosition, double modCPosition,
            double modDPosition) {
        moduleA.setDrivePIDToSetPoint(modAPosition);
        moduleB.setDrivePIDToSetPoint(modBPosition);
        moduleC.setDrivePIDToSetPoint(modCPosition);
        moduleD.setDrivePIDToSetPoint(modDPosition);
    }

    public static void addToAllDrivePositions(double ticks) {
        setDrivePosition(moduleA.getDriveEnc() + ((moduleA.modulesReversed() ? -1 : 1) * ticks),
                moduleB.getDriveEnc() + ((moduleB.modulesReversed() ? -1 : 1) * ticks),
                moduleC.getDriveEnc() + ((moduleC.modulesReversed() ? -1 : 1) * ticks),
                moduleD.getDriveEnc() + ((moduleD.modulesReversed() ? -1 : 1) * ticks));
    }

    public static double getDriveEnc() {
        return (moduleA.getDriveEnc() + moduleB.getDriveEnc() + moduleC.getDriveEnc() + moduleD.getDriveEnc()) / 4;
    }

    public static void autoSetRot(double rot) {
        swerveDrive(0, 0, rot);
    }

    public static void setAllTurnPower(double power) {
        setTurnPower(power, power, power, power);
    }

    public static void setAllDrivePower(double power) {
        setDrivePower(power, power, power, power);
    }

    public static boolean isModuleATurnEncConnected() {
        return moduleA.isTurnEncConnected();
    }

    public static boolean isModuleBTurnEncConnected() {
        return moduleB.isTurnEncConnected();
    }

    public static boolean isModuleCTurnEncConnected() {
        return moduleC.isTurnEncConnected();
    }

    public static boolean isModuleDTurnEncConnected() {
        return moduleD.isTurnEncConnected();
    }

    public static void resetDriveEncoders() {
        moduleA.resetDriveEnc();
        moduleB.resetDriveEnc();
        moduleC.resetDriveEnc();
        moduleD.resetDriveEnc();
    }

    public static void stopDriveAndTurnMotors() {
        moduleA.stopDriveAndTurnMotors();
        moduleB.stopDriveAndTurnMotors();
        moduleC.stopDriveAndTurnMotors();
        moduleD.stopDriveAndTurnMotors();
    }

    public static void stopDrive() {
        moduleA.stopDrive();
        moduleB.stopDrive();
        moduleC.stopDrive();
        moduleD.stopDrive();
    }

    public static void stopTurn() {
        moduleA.stopTurn();
        moduleB.stopTurn();
        moduleC.stopTurn();
        moduleD.stopTurn();
    }

    public static double angleToPosition(double angle) {
        if (angle < 0) {
            return .5d + ((180d - Math.abs(angle)) / 360d);
        } else {
            return angle / 360d;
        }
    }

    private static boolean allowTurnEncoderReset = false;

    public static void allowTurnEncoderReset() {
        allowTurnEncoderReset = true;
    }

    public static void resetTurnEncoders() {
        if (allowTurnEncoderReset) {
            moduleA.resetTurnEncoder();
            moduleB.resetTurnEncoder();
            moduleC.resetTurnEncoder();
            moduleD.resetTurnEncoder();

            // new robot
			// moduleA.setEncPos((calculatePositionDifference(modAOff, Calibration.GET_DT_A_ABS_ZERO())));
			// moduleB.setEncPos( (calculatePositionDifference(modBOff, Calibration.GET_DT_B_ABS_ZERO())));
			// moduleC.setEncPos( (calculatePositionDifference(modCOff, Calibration.GET_DT_C_ABS_ZERO())));
			// moduleD.setEncPos( (calculatePositionDifference(modDOff, Calibration.GET_DT_D_ABS_ZERO())));

           
            allowTurnEncoderReset = false;
        }
    }

    public static void setDriveBrakeMode(boolean b) {
        moduleA.setBrakeMode(b);
        moduleB.setBrakeMode(b);
        moduleC.setBrakeMode(b);
        moduleD.setBrakeMode(b);
    }

    public static double getAverageTurnError() {
        return (Math.abs(moduleA.getTurnError()) + Math.abs(moduleB.getTurnError()) + Math.abs(moduleC.getTurnError())
                + Math.abs(moduleD.getTurnError())) / 4d;
    }

    /*
     * 
     * Drive methods
     */
    public static void swerveDrive(double fwd, double strafe, double rot) {
        double a = strafe - (rot * (l / r));
        double b = strafe + (rot * (l / r));
        double c = fwd - (rot * (w / r));
        double d = fwd + (rot * (w / r));

        double ws1 = Math.sqrt((b * b) + (c * c)); // front_right (CHECK THESE
                                                   // AGAINST OUR BOT)
        double ws2 = Math.sqrt((b * b) + (d * d)); // front_left
        double ws3 = Math.sqrt((a * a) + (d * d)); // rear_left
        double ws4 = Math.sqrt((a * a) + (c * c)); // rear_right

        double wa1 = Math.atan2(b, c) * 180 / Math.PI;
        double wa2 = Math.atan2(b, d) * 180 / Math.PI;
        double wa3 = Math.atan2(a, d) * 180 / Math.PI;
        double wa4 = Math.atan2(a, c) * 180 / Math.PI;

        double max = ws1;
        max = Math.max(max, ws2);
        max = Math.max(max, ws3);
        max = Math.max(max, ws4);
        if (max > 1) {
            ws1 /= max;
            ws2 /= max;
            ws3 /= max;
            ws4 /= max;
        }

        DriveTrain.setTurnOrientation(angleToPosition(wa4), angleToPosition(wa2), angleToPosition(wa1),
                angleToPosition(wa3), true);
        DriveTrain.setDrivePower(ws4, ws2, ws1, ws3);
    }

    public static void showDriveEncodersOnDash() {
        SmartDashboard.putNumber("Mod A Drive Enc", (int)moduleA.getDriveEnc());
        SmartDashboard.putNumber("Mod B Drive Enc", (int)moduleB.getDriveEnc());
        SmartDashboard.putNumber("Mod C Drive Enc", (int)moduleC.getDriveEnc());
        SmartDashboard.putNumber("Mod D Drive Enc", (int)moduleD.getDriveEnc());

        SmartDashboard.putNumber("Mod A Drive Setpt", (int)moduleA.getCurrentDriveSetpoint());
        SmartDashboard.putNumber("Mod B Drive Setpt", (int)moduleB.getCurrentDriveSetpoint());
        SmartDashboard.putNumber("Mod C Drive Setpt", (int)moduleC.getCurrentDriveSetpoint());
        SmartDashboard.putNumber("Mod D Drive Setpt", (int)moduleD.getCurrentDriveSetpoint());
    }

    public static void showTurnEncodersOnDash() {
        SmartDashboard.putNumber("TURN A RAW", round(moduleA.getTurnAbsolutePosition(), 3));
        SmartDashboard.putNumber("TURN B RAW", round(moduleB.getTurnAbsolutePosition(), 3));
        SmartDashboard.putNumber("TURN C RAW", round(moduleC.getTurnAbsolutePosition(), 3));
        SmartDashboard.putNumber("TURN D RAW", round(moduleD.getTurnAbsolutePosition(), 3));

        SmartDashboard.putNumber("TURN A ENC", round(moduleA.getTurnRelativePosition(),3));
        SmartDashboard.putNumber("TURN B ENC", round(moduleB.getTurnRelativePosition(),3));
        SmartDashboard.putNumber("TURN C ENC", round(moduleC.getTurnRelativePosition(),3));
        SmartDashboard.putNumber("TURN D ENC", round(moduleD.getTurnRelativePosition(),3));

        SmartDashboard.putNumber("TURN A POS", round(moduleA.getTurnPosition(), 2));
        SmartDashboard.putNumber("TURN B POS", round(moduleB.getTurnPosition(), 2));
        SmartDashboard.putNumber("TURN C POS", round(moduleC.getTurnPosition(), 2));
        SmartDashboard.putNumber("TURN D POS", round(moduleD.getTurnPosition(), 2));

        SmartDashboard.putNumber("TURN A ANGLE", round(moduleA.getTurnAngle(), 0));
        SmartDashboard.putNumber("TURN B ANGLE", round(moduleB.getTurnAngle(), 0));
        SmartDashboard.putNumber("TURN C ANGLE", round(moduleC.getTurnAngle(), 0));
        SmartDashboard.putNumber("TURN D ANGLE", round(moduleD.getTurnAngle(), 0));

        SmartDashboard.putNumber("TURN A ERR", round(moduleA.getTurnError(),2));
        SmartDashboard.putNumber("TURN B ERR", round(moduleB.getTurnError(),2));
        SmartDashboard.putNumber("TURN C ERR", round(moduleC.getTurnError(),2));
		SmartDashboard.putNumber("TURN D ERR", round(moduleD.getTurnError(),2));

        SmartDashboard.putNumber("TURN A ZERO", moduleA.getTurnZero());
        SmartDashboard.putNumber("TURN B ZERO", moduleB.getTurnZero());
        SmartDashboard.putNumber("TURN C ZERO", moduleC.getTurnZero());
        SmartDashboard.putNumber("TURN D ZERO", moduleD.getTurnZero());
		
		//SmartDashboard.putNumber("TURN A SETPOINT", moduleA.getTurnSetpoint());
    }

    public static void humanDrive(double fwd, double str, double rot) {
        if (Math.abs(rot) < 0.01)
            rot = 0;

        if (Math.abs(fwd) < .15 && Math.abs(str) < .15 && Math.abs(rot) < 0.01) {
            setDriveBrakeMode(true);
            stopDrive();
        } else {
            setDriveBrakeMode(false);
            swerveDrive(fwd, str, rot);
        }
    }

    public static void fieldCentricDrive(double fwd, double strafe, double rot) {
        double temp = (fwd * Math.cos(RobotGyro.getGyroAngleInRad()))
                + (strafe * Math.sin(RobotGyro.getGyroAngleInRad()));
        strafe = (-fwd * Math.sin(RobotGyro.getGyroAngleInRad()))
                + (strafe * Math.cos(
                    RobotGyro.getGyroAngleInRad()));
        fwd = temp;
        humanDrive(fwd, strafe, rot);
    }

    public static void tankDrive(double left, double right) {
        setAllTurnOrientation(0);
        setDrivePower(right, left, right, left);
    }

    public static double[] getAllAbsoluteTurnOrientations() {
        return new double[] { moduleA.getTurnAbsolutePosition(), moduleB.getTurnAbsolutePosition(),
                moduleC.getTurnAbsolutePosition(), moduleD.getTurnAbsolutePosition() };
    }

    public static void setDrivePIDValues(double p, double i, double d, double f) {
        moduleA.setDrivePIDValues(p, i, d, f);
        moduleB.setDrivePIDValues(p, i, d, f);
        moduleC.setDrivePIDValues(p, i, d, f);
        moduleD.setDrivePIDValues(p, i, d, f);
    }

    public static void setTurnPIDValues(double p, double i, double d, double iZone, double f) {
        moduleA.setTurnPIDValues(p, i, d, iZone, f);
        moduleB.setTurnPIDValues(p, i, d, iZone, f);
        moduleC.setTurnPIDValues(p, i, d, iZone, f);
        moduleD.setTurnPIDValues(p, i, d, iZone, f);
    }

    public static Double round(Double val, int scale) {
        return new BigDecimal(val.toString()).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }
}
