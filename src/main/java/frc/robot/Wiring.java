package frc.robot;

public class Wiring {
	/*
	 * CAN Addresses
	 * 
	 * 1 thru 8 are the drive train
	 */

	public static final int LIVE_BOTTOM_MOTOR_ID = 9;
	public static final int AVAILABLE_MOTOR_ID = 10;
	public static final int INTAKE_MOTOR_ID = 11;
	public static final int PANCAKE_MOTOR = 12;
	public static final int BISTABLE_MOTOR = 13;


	// PCM Ports
	public static final int CLAW_FORWARD = 2;
	public static final int CLAW_REVERSE = 3;


	/**
	 * DIO ID PORTS
	 */

	public static final int TURN_ABS_ENC_A = 5;
	public static final int TURN_ABS_ENC_B = 6;
	public static final int TURN_ABS_ENC_C = 7;
	public static final int TURN_ABS_ENC_D = 8;
	public static final int PRACTICE_BOT_INDICATOR = 9;

	/*
	 * PDP PORTS
	 */
	public static final int DRIVE_PDP_PORT = 1;


}
