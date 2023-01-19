/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Shooter.ShooterPosition;

public class Climber {
	private static CANSparkMax climberMotorLeft;
	private static CANSparkMax climberMotorRight;
	private static SparkMaxPIDController climber1PID;
	private static SparkMaxPIDController climber2PID;
	private static DoubleSolenoid climberSolenoid;
	private final static double MAX_EXTENSION_VERTICAL = 76.5;
	private final static double MAX_EXTENSION_BACK = 97; 
	private static final double MAX_EXTENSION_BACK_BEFORE_BAR = 78;
	private final static double MAX_RETRACTED = -2;
	private final static double RETRACTED = 0;
	private static final double EXTENSION_OFFSET = 1; // 1.037; 
	private static final int MAX_CLIMBER_CURRENT = 50;
	private static double endMaxRetractTime;
	private static ClimberPosition currentClimberPosition;
	public static enum ClimberPosition {
		Straight, 
		Back
	}
	public static enum Rung {
		LowRung, 
		MediumRung, 
		ExtendToNextRung,
		Retract,
		UpLittle, 
	}
	private static Rung rungToClimb;
	private static boolean climbRung;
	private static double timer;
	private static double timeToClimb;
	private static double lastPositionRequested = 0;
	private static boolean climbStarted = false;
	public static enum Motor {
		LeftMotor,
		RightMotor,
	}
	private static double startExtentionTime;
	private static double extensionTimerDifference;

	public static void init() {
        climberMotorLeft = new CANSparkMax(Wiring.CLIMBER_MOTOR_1, MotorType.kBrushless);
        climberMotorLeft.restoreFactoryDefaults(); 
        climberMotorLeft.setClosedLoopRampRate(0.5);
        climberMotorLeft.setSmartCurrentLimit(MAX_CLIMBER_CURRENT);
        climberMotorLeft.setIdleMode(IdleMode.kBrake);
		climberMotorLeft.setInverted(true);

		climberMotorRight = new CANSparkMax(Wiring.CLIMBER_MOTOR_2, MotorType.kBrushless);
		climberMotorRight.restoreFactoryDefaults();
		climberMotorRight.setClosedLoopRampRate(0.5);
        climberMotorRight.setSmartCurrentLimit(MAX_CLIMBER_CURRENT);
		climberMotorRight.setIdleMode(IdleMode.kBrake);
		climberMotorRight.setInverted(false);

		
		climber1PID = climberMotorLeft.getPIDController();
		climber2PID = climberMotorRight.getPIDController();
		
		climber1PID.setP(Calibration.CLIMBER_MOTOR_P);
		climber1PID.setI(Calibration.CLIMBER_MOTOR_I);
		climber1PID.setD(Calibration.CLIMBER_MOTOR_D);
		climber1PID.setIZone(Calibration.CLIMBER_MOTOR_IZONE);
		
		climber2PID.setP(Calibration.CLIMBER_MOTOR_P);
		climber2PID.setI(Calibration.CLIMBER_MOTOR_I);
		climber2PID.setD(Calibration.CLIMBER_MOTOR_D);
		climber2PID.setIZone(Calibration.CLIMBER_MOTOR_IZONE);

		climber1PID.setOutputRange(-1, 1);
		climber2PID.setOutputRange(-1, 1);

		climber1PID.setSmartMotionMaxVelocity(25, 0);
		climber2PID.setSmartMotionMaxVelocity(25, 0);

		climber1PID.setSmartMotionMinOutputVelocity(0, 0);
		climber2PID.setSmartMotionMinOutputVelocity(0, 0);

		climber1PID.setSmartMotionMaxAccel(10, 0);
		climber2PID.setSmartMotionMaxAccel(10, 0);

		climber1PID.setSmartMotionAllowedClosedLoopError(0.5, 0);
		climber2PID.setSmartMotionAllowedClosedLoopError(0.5, 0);

		climberSolenoid = new DoubleSolenoid(PneumaticsModuleType.REVPH, Wiring.CLIMBER_SOLENOID_FORWARD, Wiring.CLIMBER_SOLENOID_REVERSE);

		currentClimberPosition = ClimberPosition.Straight;

		endMaxRetractTime = System.currentTimeMillis() + 999999;
	}

	public static void tick() {
		if (System.currentTimeMillis() > endMaxRetractTime && lastPositionRequested < RETRACTED) {
			lastPositionRequested = RETRACTED;

			climberMotorLeft.getPIDController().setReference(lastPositionRequested * EXTENSION_OFFSET, ControlType.kPosition);
			climberMotorRight.getPIDController().setReference(lastPositionRequested , ControlType.kPosition);

			endMaxRetractTime  = System.currentTimeMillis() + 99999;
		} 

		SmartDashboard.putNumber("Climber1 Position Actual", climberMotorLeft.getEncoder().getPosition());
		SmartDashboard.putNumber("Climber2 Position Actual", climberMotorRight.getEncoder().getPosition());
	}

	public static void move(double speed){
		if (climberMotorLeft.getEncoder().getPosition() <= 0  && speed > 0.2) {
			climberMotorLeft.set(-.2); // allow them to very slowly retract to allow for manual pull in 
			                       // after powering off in the extended position
			climberMotorRight.set(-.2);
		} else if (climberMotorLeft.getEncoder().getPosition() >= getMaxExtension() && speed < .2) {
			climberMotorLeft.set(.20);
			climberMotorRight.set(.20);
		}
		else if ((climberMotorLeft.getEncoder().getPosition() <= 0  && speed > 0)) {
			climberMotorLeft.set(0);
			climberMotorRight.set(0);
		}
		else if (climberMotorLeft.getEncoder().getPosition() >= getMaxExtension() && speed < 0) {
			climberMotorLeft.set(0);
			climberMotorRight.set(0);
		} else {
			climberMotorLeft.set(-speed);
			climberMotorRight.set(-speed);
		}
	}

	public static void moveV2(double direction) {
		double movementFactor = 1; // was 1.3
		if (Math.abs(direction) < 0.1) {
			if (lastPositionRequested < 0) {
				lastPositionRequested = RETRACTED;
			}
		} else {
			// direction is between -1 and 1 indicating the direction to manually move
			if (lastPositionRequested < 20 && direction < 0) { // slowing speed until static hooks engage
				movementFactor = .6;
			} else {
				movementFactor = 1.5;
			}
			double newPosition = lastPositionRequested + (movementFactor * -direction);
			
			if (newPosition < MAX_RETRACTED) {
				newPosition = MAX_RETRACTED;
			} else if (newPosition > getMaxExtension()) {
				newPosition = getMaxExtension();
			}
			if (newPosition < RETRACTED) {
				if (endMaxRetractTime > System.currentTimeMillis() + 1000) {
					endMaxRetractTime = System.currentTimeMillis()  + 1000;
				}
			}

			lastPositionRequested = newPosition;
		}
		SmartDashboard.putNumber("Climber Position Requested", lastPositionRequested);

		climberMotorLeft.getPIDController().setReference(lastPositionRequested * EXTENSION_OFFSET, ControlType.kPosition);
		// climberMotor2.getPIDController().setReference(lastPositionRequested , ControlType.kPosition);
		climberMotorRight.getPIDController().setReference(lastPositionRequested , ControlType.kPosition);
	}

	public static void moveV3(double direction, Motor motor) {
		// direction is between -1 and 1 indicating the direction to manually move
		double movementFactor = 1.3;

		double newPosition = lastPositionRequested + (movementFactor * -direction);
		
		if (newPosition < 0) {
			newPosition = 0;
		} else if (newPosition > getMaxExtension()) {
			newPosition = getMaxExtension();
		}

		lastPositionRequested = newPosition;

		SmartDashboard.putNumber("Climber Position Requested", lastPositionRequested);
		switch (motor) {
			case LeftMotor:
				climberMotorLeft.getPIDController().setReference(lastPositionRequested, ControlType.kPosition);
				break;
			case RightMotor:
				climberMotorRight.getPIDController().setReference(lastPositionRequested, ControlType.kPosition);
				break;
		}
	}

	public static void climberStop() {
		climberMotorLeft.set(0);
		climberMotorRight.set(0);
	}

	public static void reset() {
		climberMotorLeft.getEncoder().setPosition(0);
		climberMotorRight.getEncoder().setPosition(0);
		lastPositionRequested = 0;
		climbStarted = false;
	}

	public static void climberPosition(ClimberPosition position) {
		switch(position) {
			case Straight:
				climberSolenoid.set(DoubleSolenoid.Value.kReverse);
				currentClimberPosition = position;
				break; 
			case Back:
				climberSolenoid.set(DoubleSolenoid.Value.kForward);
				currentClimberPosition = position;
				climbStarted = true;
				Shooter.setShooterPosition(ShooterPosition.Medium);
				startExtentionTime = System.currentTimeMillis();
				break;
		}
	}

	private static double getMaxExtension() {
		extensionTimerDifference = System.currentTimeMillis() - startExtentionTime;
		if (currentClimberPosition == ClimberPosition.Back || climbStarted) {
			if (extensionTimerDifference > 1750) {
				return MAX_EXTENSION_BACK;
			} else {
				return MAX_EXTENSION_BACK_BEFORE_BAR;
			}
		} else
			return MAX_EXTENSION_VERTICAL;
	}

	public static void climbTo(Rung rung) { 
		// rungToClimb = rung;
		// climbRung = true;
		switch(rung) {
			case LowRung:
				lastPositionRequested = 50;
				break;
			case MediumRung:
				lastPositionRequested = 60; // not right
				break;
			case ExtendToNextRung:
				lastPositionRequested = MAX_EXTENSION_BACK;
				break;
			case Retract:
				lastPositionRequested = MAX_RETRACTED;
				break;
			case UpLittle:
				lastPositionRequested = 20;
			}

		climber1PID.setReference(lastPositionRequested, CANSparkMax.ControlType.kPosition);
		climber2PID.setReference(lastPositionRequested, CANSparkMax.ControlType.kPosition);
	}
}