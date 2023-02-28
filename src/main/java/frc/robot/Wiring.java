package frc.robot;

public class Wiring {
	/*
	 * CAN Addresses
	 * 
	 * 1 thru 8 are the drive train
	 */

	public static final int LIVE_BOTTOM_MOTOR_ID = 9;
	public static final int CLAW_MOTOR_ID = 10;
	public static final int INTAKE_MOTOR_ID = 11;
	public static final int SHOULDER_MOTOR = 13;
	public static final int BISTABLE_MOTOR = 12;

	// PCM Ports
	public static final int INTAKE_SOLENOID_STOW = 13;
	public static final int INTAKE_SOLENOID_EXTENDED = 15;

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


	// PWM/SERVO ports.
    public static final int CLAW_CHANNEL_ID = 0;
	public static final int LIVE_BOTTOM_GATE_CHANNEL_ID = 1;

}
