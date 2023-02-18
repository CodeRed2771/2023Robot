package frc.robot;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwerveTurnTest {
    public CANSparkMax turn;
	private final SparkMaxPIDController turnPID;
	private final RelativeEncoder turnEncoder;
	private char mModuleID;
	private final int FULL_ROTATION = 1;
	private double TURN_P, TURN_I, TURN_D;
	private final int TURN_IZONE;
	private double turnZeroPos = 0;
	private boolean isReversed = false;
	private double targetPos = 0;

	
	public SwerveTurnTest() {

	    // TURN

		turn = new CANSparkMax(5, MotorType.kBrushless);
		turn.restoreFactoryDefaults();
        turn.setOpenLoopRampRate(.5);
        turn.setSmartCurrentLimit(30);

        turn.setIdleMode(IdleMode.kBrake);
		turn.setInverted(true);

		turnEncoder = turn.getAlternateEncoder(8192);
		turnEncoder.setInverted(true);
		targetPos = turnEncoder.getPosition();
		turnPID = turn.getPIDController();
		turnPID.setFeedbackDevice(turnEncoder);

		turnZeroPos = turnEncoder.getPosition();

		TURN_P = Calibration.getTurnP();
		TURN_I = Calibration.getTurnI();
		TURN_D = Calibration.getTurnD();
		TURN_IZONE = (int) Calibration.getTurnIZone();

		turnPID.setP(TURN_P);
		turnPID.setI(TURN_I);
		turnPID.setD(TURN_D);
		turnPID.setIZone(TURN_IZONE);
		turnPID.setFF(0);
        turnPID.setOutputRange(-1, 1);
		
		turn.burnFlash(); // save settings for power off
	}
	
	public void tick() {
		SmartDashboard.putNumber("Position", turnEncoder.getPosition());
		SmartDashboard.putNumber("Target", targetPos);
		SmartDashboard.putNumber("Start Pos", turnZeroPos);
	}

	public void move(double amt) {
		targetPos += (amt / 10);
		//2/18 commented out to remove errors   turnPID.setReference(targetPos, ControlType.kPosition );
	}

	public void setPosA() {
		setTurnOrientation(.400);
	}

	public void setPosB() {
		setTurnOrientation(.700);
	}

	public void setPosOrig() {
		//2/18 commented out to remove errors   turnPID.setReference(turnZeroPos, ControlType.kPosition );
	}

	public double getTurnRelativePosition() {
		return turnEncoder.getPosition();
	}

	public double getTurnAbsolutePosition() {
		return(turnEncoder.getPosition() - (int)turnEncoder.getPosition()); // change 23.3434 to .3434
		//return (turn.getSensorCollection().getPulseWidthPosition() & 0xFFF) / 4096d;
	}

	public double getTurnPosition() {
		// returns the 0 to 1 value of the turn position
		// uses the calibration value and the actual position
		// to determine the relative turn position

		double currentPos = getTurnAbsolutePosition();
		if (currentPos - turnZeroPos > 0) {
			return currentPos - turnZeroPos;
		} else {
			return (1 - turnZeroPos) + currentPos;
		}
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

	public void resetTurnEnc() {
		turnEncoder.setPosition(0);
		//this.turn.getSensorCollection().setQuadraturePosition(0, 10);
	}

	public void setEncPos(double d) {
		turnEncoder.setPosition(d);
	}

	public int getTurnRotations() {
		return (int) turnEncoder.getPosition();
	}

	public double getTurnOrientation() {
		return turnEncoder.getPosition() - (int) turnEncoder.getPosition();
	}

 	public void setTurnPIDToSetPoint(double setpoint) {
		turn.set(setpoint);
	}

	public void setTurnOrientation(double position) {
		setTurnOrientation(position, true);
	}

	/**
	 * Set turn to pos from 0 to 1 using PID
	 * 
	 * @param reqPosition orientation to set to
	 */
	public void setTurnOrientation(double reqPosition, boolean optimize) {
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
			
			turnPos = ((((closestTurnPosition * FULL_ROTATION) + (base))));
		} else {
			// if ((base - ((1 - closestTurnPosition) * FULL_ROTATION)) - turnRelativePosition < -FULL_ROTATION / 2) {
			// 	base += FULL_ROTATION;
			// } else if ((base - ((1 - closestTurnPosition) * FULL_ROTATION)) - turnRelativePosition > FULL_ROTATION
			// 		/ 2) {
			// 	base -= FULL_ROTATION;
			// }
			
			turnPos = ((base - (((1 - closestTurnPosition) * FULL_ROTATION))));
		
		}
		SmartDashboard.putNumber("TURN CALL " + mModuleID, turnPos);
		//2/18 commented out to remove errors    turnPID.setReference(turnPos, ControlType.kPosition );
		
	}

	public double getTurnError() {
		return 9999; // fix later
	}

	public double getTurnSetpoint() {
		return 9999; // fix later
	}

}