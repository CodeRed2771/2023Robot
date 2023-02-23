package frc.robot;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.lang.model.util.ElementScanner6;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwerveModuleFalcon implements SwerveModule {
    public TalonFX drive;
    public CANSparkMax turn;
	private final SparkMaxPIDController turnPID;
	private final RelativeEncoder turnEncoder;
	private final DutyCycleEncoder turnAbsEncoder;
	private char mModuleID;
	private final int FULL_ROTATION = 1;
	private double TURN_P, TURN_I, TURN_D, TURN_F, DRIVE_P, DRIVE_I, DRIVE_D;
	private final double TURN_IZONE, DRIVE_IZONE;
	private double turnZeroPos = 0;
	private double currentDriveSetpoint = 0;
	private boolean isReversed = false;

	/**
	 * Lets make a new module :)
	 * 
	 * @param driveMotorID First I gotta know what talon we are using for driving
	 * @param turnMotorID  Next I gotta know what talon we are using to turn

	 */
	public SwerveModuleFalcon(int driveMotorID, int turnMotorID, int turnAbsEncID, double tZeroPos, char moduleID) {

		mModuleID = moduleID;

		DRIVE_P = Calibration.getDriveP();
		DRIVE_I = Calibration.getDriveI();
		DRIVE_D = Calibration.getDriveD();
		DRIVE_IZONE = Calibration.getDriveIZone();

		drive = new TalonFX(driveMotorID);
		drive.configFactoryDefault(10);
		drive.config_kP(0, DRIVE_P, 0);
		drive.config_kI(0, DRIVE_I, 0);
		drive.config_kD(0, DRIVE_D, 0);
		drive.config_kF(0, 0, 0);
		drive.config_IntegralZone(0, DRIVE_IZONE, 0);
		drive.selectProfileSlot(0, 0);
		/* set the peak and nominal outputs */
		drive.configNominalOutputForward(0, 0);
		drive.configNominalOutputReverse(0, 0);
		drive.configPeakOutputForward(1, 0);
		drive.configPeakOutputReverse(-1, 0);
		drive.setNeutralMode(NeutralMode.Brake);
		drive.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, 0);
		
		drive.configClosedloopRamp(.45, 0);
		/* set closed loop gains in slot0 - see documentation */
		drive.selectProfileSlot(0, 0);

     //   drivePID.setSmartMotionMaxVelocity(Calibration.DT_MM_VELOCITY, 0);
     //   drivePID.setSmartMotionMaxAccel(Calibration.DT_MM_ACCEL, 0);

        // TURN
		turnAbsEncoder = new DutyCycleEncoder(turnAbsEncID);

		turn = new CANSparkMax(turnMotorID, MotorType.kBrushless);
		turn.restoreFactoryDefaults();
       // turn.setOpenLoopRampRate(.5);
        turn.setSmartCurrentLimit(30);

        turn.setIdleMode(IdleMode.kBrake);
		turn.setInverted(true);

		turnEncoder = turn.getAlternateEncoder(8192);
		turnEncoder.setInverted(true);
		turnPID = turn.getPIDController();
		turnPID.setFeedbackDevice(turnEncoder);

		TURN_P = Calibration.getTurnP();
		TURN_I = Calibration.getTurnI();
		TURN_D = Calibration.getTurnD();
		TURN_IZONE = Calibration.getTurnIZone();
		TURN_F = Calibration.getTurnF();

		setTurnPIDValues(TURN_P, TURN_I, TURN_D, TURN_IZONE, TURN_F);
		
        turnPID.setOutputRange(-1, 1);
		
		//turnPID.setReference(0, ControlType.kVelocity);

		turn.burnFlash(); // save settings for power off

		turnZeroPos = tZeroPos;
		// turnZeroPos = turnEncoder.getPosition();

	}

	// public void setFollower(int talonToFollow) {
	// 	if (talonToFollow != 0) {
	// 		drive.set(ControlMode.Follower, talonToFollow);
	// 	} else
	// 		drive.set(ControlMode.Velocity, 0);
	// }

    public void setDriveMMAccel(final int accel) {
		drive.configMotionAcceleration(accel);
    }

    public void setDriveMMVelocity(final int velocity) {
		drive.configMotionCruiseVelocity(velocity);
    }

	// public double getDriveVelocity() {
	// 	return drive.getSelectedSensorVelocity(0);
	// }

	/**
	 * getModuleLetters
	 * @return a single character, A,B,C,D indicating which module this is
	 */
	public char getModuleLetter() {
		return mModuleID;
	}

	/**
	 * Setting turn motor power
	 * 
	 * @param p value from -1 to 1
	 */
	public void setTurnPower(double p) {
		this.turn.set(p);
	}

	/**
	 * Setting drive motor power
	 * 
	 * @param p value from -1 to 1
	 */
	public void setDrivePower(double p) {
		this.drive.set(ControlMode.PercentOutput, (isReversed ? -1 : 1) * p);
	}

	/**
	 * Getting the turn encoder position (not absolute)
	 * 
	 * @return turn encoder position
	 */
	public double getTurnRelativePosition() {
		return turnEncoder.getPosition();
	}

	/**
	 * Gets the absolute encoder position for the turn encoder It will be a value
	 * between 0 and 1
	 * 
	 * @return turn encoder absolute position
	 */
	public double getTurnAbsolutePosition() {
		double encPos = 0;

		if (turnAbsEncoder.get() >= 0)
			encPos = (turnAbsEncoder.get() - (int) turnAbsEncoder.get()); // e.g. 3.2345 - 3.000 = .2345
		else
			encPos = ((int)Math.abs( turnAbsEncoder.get()) + 1 - Math.abs(turnAbsEncoder.get())); // e.g. -3.7655  = .2345
		// now invert it because the turn motors are inverted
		if (encPos <= .5) 
			encPos = 1 - encPos; // e.g .2 becomes .8
		else
			encPos = .5 - (encPos - .5); // e.g. .5 - (.8 - .5) = .2
		
		return encPos;
	}

	public double getTurnPosition() {
		// returns the 0 to 1 value of the turn position
		// uses the calibration value and the actual position
		// to determine the relative turn position
		return getTurnPositionWithInRotation();
		// double currentPos = getTurnAbsolutePosition();
		// if (currentPos - turnZeroPos > 0) {
		// 	return currentPos - turnZeroPos;
		// } else {
		// 	return (1 - turnZeroPos) + currentPos;
		// }
	}

	public double getTurnAngle() {
		// returns the angle in -180 to 180 range
		double turnPos = getTurnPosition();
		if (turnPos > .5) {
			return (360 - (turnPos * 360));
		} else
			return turnPos * 360;
	}

	public boolean modulesReversed() {
		return isReversed;
	}

	public void unReverseModule() {
		isReversed = false;
	}

	public void resetZeroPosToCurrentPos() {
		// sets the known "zero position" to be whatever we're at now.
		// should only be called when the modules are KNOWN to be straight.
		// turnZeroPos = turnEncoder.getPosition() - (int)turnEncoder.getPosition();
	}

	/*
		resets the Quad Encoder based on absolute encoder
	*/
	public void resetTurnEncoder() {
		double currentPos = 0;
		double positionToSet = 0;
		setTurnPower(0);
		Timer.delay(.1); // give module time to settle down
		currentPos = getTurnAbsolutePosition();
		
		positionToSet = calculatePositionDifference(currentPos, turnZeroPos);
		setEncPos(100 + positionToSet);
		// setEncPos(0.95);
	}

	private static double calculatePositionDifference(double currentPosition, double calibrationZeroPosition) {
        if (currentPosition - calibrationZeroPosition >= 0) {
            return currentPosition - calibrationZeroPosition;
        } else {
            return (1 - calibrationZeroPosition) + currentPosition;
        }
    }
	public double getDriveEnc() {
		return drive.getSelectedSensorPosition();
	}

	public void resetDriveEnc() {
        this.drive.setSelectedSensorPosition(0);
	}

	public void setEncPos(double d) {
		turnEncoder.setPosition(d);
	}

	/**
	 * Is electrical good? Probably not.... Is the turn encoder connected?
	 * 
	 * @return true if the encoder is connected
	 */
	public boolean isTurnEncConnected() {
		// return turn.isSensorPresent(FeedbackDevice.CtreMagEncoder_Relative) ==
		// FeedbackDeviceStatus.FeedbackStatusPresent;
		return true; // didn't immediately see a compatible replacement
	}

	public int getTurnRotations() {
		// i have verified that the (int) typecast 
		// does properly truncate off the decimal portion
		// without any rounding.
		return (int) turnEncoder.getPosition();
	}

	public double getTurnOrientation() {
		if (turnEncoder.getPosition() >= 0) {
			return turnEncoder.getPosition() - (int) turnEncoder.getPosition();	
		} else
			return turnEncoder.getPosition() + (int) turnEncoder.getPosition();
	}
	
	public double getTurnPositionWithInRotation() {
		double rawPos = 0;
		if (turnEncoder.getPosition() >= 0) {
			return turnEncoder.getPosition() - (int) turnEncoder.getPosition();	
		} else
		rawPos =  turnEncoder.getPosition() + (int) turnEncoder.getPosition();
		return round(rawPos, 3);
	}

    public double getCurrentDriveSetpoint() {
        return currentDriveSetpoint;
    }

    // These are used for driving and turning in auto.
    public void setDrivePIDToSetPoint(final double setpoint) {
        currentDriveSetpoint = setpoint;
        drive.set(TalonFXControlMode.MotionMagic, setpoint); 
    }

    public boolean hasDriveCompleted(final double inchesError) {
        return Math.abs(currentDriveSetpoint - getDriveEnc()) <= Calibration.getDriveTicksPerInch() * inchesError;
    }
    
	public boolean hasDriveCompleted() {
		return hasDriveCompleted(0.25);
	}

	public void setTurnPIDToSetPoint(double setpoint) {
		turn.set(setpoint);
	}

	/**
	 * Set turn to pos from 0 to 1 using PID
	 * 
	 * @param reqPosition orientation to set to
	 */
	public void setTurnOrientation(double reqPosition, boolean optimize) {
		// reqPosition - a value between 0 and 1 that indicates the rotational position
		//               that we want the module to be facing.
		// Output
		//      The result of this method is to instruct the turn motor to go to a
		//      position that will equal the desired position.  But that position might
		//      be the opposite side of the circle if we can get there quicker and
		//      just invert the drive direction.
		//      
		// Note
		//		Since "zero" on the modules doesn't equal zero as the requested position
		//      we need to take into consideration the specific zero position of this 
		//		module.  In other words, if the straight ahead "zero" position of this 
		//		module is a reading of ".250" and our requested position is "0", then we
		//		actually have to go to ".250" to satisfy the requested position.
		// 		The encoders actual position will be a number like 5.2332 or -5.3842
		//		indicating that it's 5 revolutions in plus a partial revolution.
		//		so the final turn instruction needs to be relative to that original 
		//      reading otherwise the module will have to unwind a bunch of revolutions.

        boolean invertDrive = false;
        double nearestPosInRotation = 0;
        double newTargetPosition = 0;
		double currentPosition = turnEncoder.getPosition();

		// I think it would be best to adjust our requested position first so that it  
		// is compatible with our modules zero offset.  Then all calculations after that
		// will be in actual encoder positions.

		// reqPosition = round(reqPosition,3); round was crashing occasionally

		//reqPosition += turnZeroPos;  
		if (reqPosition > 0.99999) // we went past the rotation point
			reqPosition -= 1;  // remove the extra rotation. change 1.2344 to .2344
        double reqPositionReverse = (reqPosition >= .5 ? reqPosition - .5 : reqPosition + .5) ; // e.g. .8 becomes .3

		// now we can check where we currently are and figure out the optimal new position 
		// based on which of the two potential positions is closest.
		double currentPosInRotation = getTurnPositionWithInRotation();  // this is where it is .
		int currentRevolutions = getTurnRotations();  // remember this for later

        // e.g. curpos = .125;  req = .800  rot=15

		if (optimize) {
			// if the difference between the current position and the requested position is less than 
			// a half rotation, then use that position, otherwise use the reverse position
			if (Math.abs(currentPosInRotation - reqPosition) <= .25 || Math.abs(currentPosInRotation - (1 + reqPosition)) <= .25|| Math.abs((currentPosInRotation+1)-reqPosition) <=0.25) {
				nearestPosInRotation = reqPosition;
				invertDrive = false;
			} else {
				nearestPosInRotation = reqPositionReverse;
				invertDrive = true;
			}
		} else {
			nearestPosInRotation = reqPosition;
			invertDrive = false;
		}
        
        
        // now we need to determine if we need to change our rotation counter to get to 
        // this new position
        // if we're at .9 and need to get to .1 for instance, then we need to increment 
        // our rotation count or the module will unwind backwards a revolution.

        // e.g. curpos = .125;  nearest = .300  rot=15
        // have to handle cur = .125 nearest = .99  rot=15 
        // and have to handle negative rotations
        double newRevolutions = currentRevolutions;
        if (nearestPosInRotation < currentPosInRotation)   {
            // our nearest is a smaller number so we may need to change revolutions
            // if the difference is larger than .5 then we are rotating across
            // the zero position, so we need to adjust revolutions
            if (Math.abs(currentPosInRotation - nearestPosInRotation) > .5) {
                // e.g cur = .9  near = .1 rot = 15, then new = 16.1
                // e.g cur = .9  near = .1 rot = -15, then new = -16.1
                newRevolutions = (currentRevolutions >= 0 ? currentRevolutions + 1 : currentRevolutions - 1);
            } 
        } else { 
            // see if we're backing up across the zero and then reduce the revs
            if (Math.abs(currentPosInRotation - nearestPosInRotation) > .5) {
                // e.g cur = .9  near = .1 rot = 15, then new = 16.1
                // e.g cur = .9  near = .1 rot = -15, then new = -16.1
                newRevolutions = (currentRevolutions >= 0 ? currentRevolutions - 1 : currentRevolutions + 1);
            } 
        }

        newTargetPosition = (newRevolutions >= 0 ? newRevolutions + nearestPosInRotation : newRevolutions - nearestPosInRotation);
        
		// newTargetPosition = round(newTargetPosition,3);  round was crashing occasionally

		// Set drive inversion if needed
		isReversed = invertDrive; // invert

        // TURN
        turnPID.setReference(newTargetPosition, ControlType.kPosition );

		if (mModuleID=='C') {
			SmartDashboard.putString("MC Cur Pos", String.format("%.3f", turnEncoder.getPosition()));
			SmartDashboard.putString("MC Req Pos", String.format("%.3f", reqPosition));
			SmartDashboard.putString("MC Nearest", String.format("%.3f", nearestPosInRotation));
			SmartDashboard.putString("MC NewTarget", String.format("%.3f", newTargetPosition));
			SmartDashboard.putBoolean("MC Reversed", isReversed);
		}

		// look for error
		if (Math.abs(currentPosition - newTargetPosition) > .250) {
			SmartDashboard.putString("MOD " + mModuleID + " CALC ERROR", "Cur: " + String.format("%.3f", currentPosition) + " New: " + String.format("%.3f", newTargetPosition) + "Called:  " + String.format("%.3f", reqPosition));
		}

        // System.out.println("");
        // System.out.println("Current Rotations:" + currentRevolutions);
        // System.out.println("Current Position: " + currentPosInRotation);
        // System.out.println("Requested Pos:    " + reqPosition);
        // System.out.println("Requested Pos Rev:" + reqPositionReverse);
        // System.out.println("Nearest Pos:      " + nearestPosInRotation);
		// System.out.println("NEW TARGET POS:   " + newTargetPosition);
        // System.out.println("INVERT DRIVE:     " + invertDrive);
	}

	public void resetTurnReversedFlag() {
		isReversed = false;
	}

	public void setTurnOrientationOLD(double reqPosition, boolean optimize) {
		int base = getTurnRotations();
		double currentTurnPosition = getTurnPosition();
		double reverseTurnPosition = (reqPosition + 0.5) % 1.0;
		double distanceToNormalPosition;
		double distanceToReversePosition;
		double closestTurnPosition = 0; // closest to currentTurnPosition
		double turnRelativePosition = getTurnRelativePosition();
		double turnPos = 0;
		// double distanceToNormalPosition = Math.abs(currentTurnPosition - position);
		// double distanceToReversePosition = Math.abs(currentTurnPosition -
		// reverseTurnPosition);
		
		if (currentTurnPosition - reqPosition >= 0)
			if (currentTurnPosition - reqPosition > .5)
				distanceToNormalPosition = (1 - currentTurnPosition) + reqPosition;
			else
				distanceToNormalPosition = currentTurnPosition - reqPosition;
		else if (reqPosition - currentTurnPosition > .5)
			distanceToNormalPosition = (1 - reqPosition) + currentTurnPosition;
		else
			distanceToNormalPosition = reqPosition - currentTurnPosition;

		// note - this part could be eliminated because the distance to reverse
		// is a simple calculation based on the distance of the normal way.
		// I believe it would be just 1 - distance to Normal
		// if normal is .7, reverse would be .3 (1 - .7)
		// if normal is .3, reverse would be .7 (1 - .3)
		// this next line didn't work for some reason....
		// distanceToReversePosition = 1 - distanceToNormalPosition;
		if (currentTurnPosition - reverseTurnPosition >= 0)
			if (currentTurnPosition - reverseTurnPosition > .5)
				distanceToReversePosition = (1 - currentTurnPosition) + reverseTurnPosition;
			else
				distanceToReversePosition = currentTurnPosition - reverseTurnPosition;
		else if (reverseTurnPosition - currentTurnPosition > .5)
			distanceToReversePosition = (1 - reverseTurnPosition) + currentTurnPosition;
		else
			distanceToReversePosition = reverseTurnPosition - currentTurnPosition;

		if (optimize) {
			closestTurnPosition = distanceToReversePosition < distanceToNormalPosition ? reverseTurnPosition
					: reqPosition;
		} else
			closestTurnPosition = reqPosition;

		isReversed = (closestTurnPosition != reqPosition);

		if (turnRelativePosition >= 0) {
			// if ((base + (closestTurnPosition * FULL_ROTATION)) - turnRelativePosition < -FULL_ROTATION / 2) {
			// 	base += FULL_ROTATION;
			// } else if ((base + (closestTurnPosition * FULL_ROTATION)) - turnRelativePosition > FULL_ROTATION / 2) {
			// 	base -= FULL_ROTATION;
			// }
			
			turnPos = ((((closestTurnPosition * FULL_ROTATION) + (base)))) + turnZeroPos;
		} else {
			// if ((base - ((1 - closestTurnPosition) * FULL_ROTATION)) - turnRelativePosition < -FULL_ROTATION / 2) {
			// 	base += FULL_ROTATION;
			// } else if ((base - ((1 - closestTurnPosition) * FULL_ROTATION)) - turnRelativePosition > FULL_ROTATION
			// 		/ 2) {
			// 	base -= FULL_ROTATION;
			// }
			
			turnPos = ((base - (((1 - closestTurnPosition) * FULL_ROTATION)))) - turnZeroPos;
		
		}
		SmartDashboard.putNumber("TURN CALL " + mModuleID, turnPos);
		turnPID.setReference(turnPos, ControlType.kPosition );
		
	}

	// private void showDetailsOnDash(int base, int turnRelative, double
	// currentTurnPosition, double requestedPosition,
	// double reverseTurnPos, double distNormal, double distReverse, double
	// closestTurn, boolean optimize,
	// int newSetpoint) {
	// if (mModuleID == 'B') {
	// System.out.println("CurrentTurn: " + currentTurnPosition + " Req Pos: " +
	// requestedPosition + " dist Norm: "
	// + distNormal + " dist Rev: " + distReverse);
	// SmartDashboard.putNumber("AAA Base", base);
	// SmartDashboard.putNumber("AAA turnRel", turnRelative);
	// SmartDashboard.putNumber("AAA req pos", requestedPosition);
	// SmartDashboard.putNumber("AAA cur pos", currentTurnPosition);
	// SmartDashboard.putNumber("AAA revPos", reverseTurnPos);
	// SmartDashboard.putNumber("AAA distnorm", distNormal);
	// SmartDashboard.putNumber("AAA distrev", distReverse);
	// SmartDashboard.putBoolean("AAA reversed", isReversed);
	// SmartDashboard.putNumber("AAA closest", closestTurn);
	// SmartDashboard.putNumber("AAA new set", newSetpoint);
	// SmartDashboard.putBoolean("AAA optimize", optimize);
	// }
	// }

	public double getTurnError() {
		return 9999; // fix later
	}

	public double getTurnSetpoint() {
		return 9999; // fix later
	}

	// public double getDriveError() {
		// note that when using Motion Magic, the error is not what you'd expect
		// MM sets intermediate set points, so the error is just the error to
		// that set point, not to the final setpoint.
	// 	return drive.getClosedLoopError(0);
	// }

	public void stopDriveAndTurnMotors() {
		setDrivePower(0);
		setTurnPower(0);
	}

	public void stopTurn() {
		setTurnPower(0);
	}

	public void stopDrive() {
		setDrivePower(0);
	}

    public void setBrakeMode(final boolean b) {
        drive.setNeutralMode(b ? NeutralMode.Brake : NeutralMode.Coast);
    }

    public void setDrivePIDValues(final double p, final double i, final double d, final double f) {
		drive.config_kP(0, p, 0);
        drive.config_kI(0, i, 0);
        drive.config_kD(0, d, 0);
        drive.config_kF(0, f, 0);
    }

	public void setTurnPIDValues(double p, double i, double d, double izone, double f) {
		turnPID.setP(p);
        turnPID.setI(i);
        turnPID.setIZone(izone);
        turnPID.setD(d);
        turnPID.setFF(f);
	}

	public double getTurnZero() {
		return turnZeroPos;
	}

	public static Double round(Double val, int scale) {
		try {
			return new BigDecimal(val.toString()).setScale(scale, RoundingMode.HALF_UP).doubleValue();
		} catch (Exception ex) {
			return val;
		}

    }

}