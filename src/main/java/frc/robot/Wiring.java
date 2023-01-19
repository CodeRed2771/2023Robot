package frc.robot;

public class Wiring {
	/*
	 * CAN Addresses
	 * 
	 * 1 thru 8 are the drive train
	 */


	public static final int SHOOTER_MOTOR_ID = 10;
	public static final int FEEDER_MOTOR_ID = 9;
	public static final int INTAKE_MOTOR_ID = 11;
	public static final int CLIMBER_MOTOR_1 = 12;
	public static final int CLIMBER_MOTOR_2 = 13;

	// PCM Ports
	public static final int SHOOTERPOSITION_STAGE1_REVERSE = 1;
	public static final int SHOOTERPOSITION_STAGE1_FOWARD = 0;
	public static final int SHOOTERPOSITION_STAGE2_REVERSE = 2;
	public static final int SHOOTERPOSITION_STAGE2_FOWARD = 3;
	public static final int BALLLIFT_REVERSE = 4;
	public static final int BALLLIFT_FOWARD = 5;
	public static final int CLIMBER_SOLENOID_FORWARD = 6 ;
	public static final int CLIMBER_SOLENOID_REVERSE = 7;


	/**
	 * DIO ID PORTS
	 */

	public static final int TURN_ABS_ENC_A = 5;
	public static final int TURN_ABS_ENC_B = 6;
	public static final int TURN_ABS_ENC_C = 7;
	public static final int TURN_ABS_ENC_D = 8;
	public static final int PRACTICE_BOT_INDICATOR = 9;

	// public static final int CLIMB_MOTOR_ID = 11;
	// public static final int CLIMB_DRIVE_ID = 14;
	// public static final int MANIPULATOR_MOTOR_ID = 10;

	/*
	 * PDP PORTS
	 */
	// public static final int LINKAGE_PDP_PORT = 8;
	// public static final int LIFT_PDP_PORT = 6;
	// public static final int INTAKE_PDP_PORT = 7;
	public static final int DRIVE_PDP_PORT = 1;


}
