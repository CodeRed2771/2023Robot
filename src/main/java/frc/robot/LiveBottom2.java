package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class LiveBottom2 {
    private static CANSparkMax liveBottomMotor;
    private static SparkMaxPIDController liveBottomPID;
    
    private static final int MAX_LIVE_BOTTOM_CURRENT = 25;
    private static final double MIN_TO_INTAKE =  0;
    private static final double MAX_AWAY_INTAKE = -20;
    private static boolean autoZeroModeEnabled = false;
    private static double autoZeroEndTime = 0;
    private static double desiredPosition; 

    public static void init() {
        liveBottomMotor = new CANSparkMax(Wiring.LIVE_BOTTOM_MOTOR_ID, MotorType.kBrushless);
        liveBottomMotor.restoreFactoryDefaults();

        liveBottomMotor.setSmartCurrentLimit(MAX_LIVE_BOTTOM_CURRENT);
        liveBottomMotor.setIdleMode(IdleMode.kBrake);

        liveBottomMotor.setInverted(true);

        liveBottomPID = liveBottomMotor.getPIDController();

        liveBottomPID.setP(Calibration.LIVE_BOTTOM_MOTOR_P);
        liveBottomPID.setI(Calibration.LIVE_BOTTOM_MOTOR_I);
        liveBottomPID.setD(Calibration.LIVE_BOTTOM_MOTOR_D);
        liveBottomPID.setIZone(Calibration.LIVE_BOTTOM_MOTOR_IZONE);

        liveBottomPID.setOutputRange(-1, 1);
        liveBottomPID.setSmartMotionMaxVelocity(25, 0);
        liveBottomPID.setSmartMotionMinOutputVelocity(0, 0);
        liveBottomPID.setSmartMotionMaxAccel(10, 0);
        liveBottomPID.setSmartMotionAllowedClosedLoopError(0.5,0);
        
        desiredPosition =  0;
    }
    public static void tick() {
        if (autoZeroModeEnabled) {
            if (System.currentTimeMillis() > autoZeroEndTime) {
                autoZeroModeEnabled = false;
                liveBottomMotor.set(0);
                liveBottomMotor.getEncoder().setPosition(0);
            }
        }    
        SmartDashboard.putNumber("LiveBottom Encoder", liveBottomMotor.getEncoder().getPosition());
    }
    public static double getEncoder() {
        return liveBottomMotor.getEncoder().getPosition();
    }
    public static void forward() {
        liveBottomPID.setReference(MIN_TO_INTAKE, ControlType.kPosition);
        // liveBottomMotor.set(0.15);
    }
    public static void backward() {
        liveBottomPID.setReference(MAX_AWAY_INTAKE, ControlType.kPosition);
        // liveBottomMotor.set(-0.15);
    }
    public static void smartPosition(double power){
        if(Math.abs(power) > .07) {
            desiredPosition = desiredPosition + (2 * power);

            if(desiredPosition > MAX_AWAY_INTAKE) {
                desiredPosition = MAX_AWAY_INTAKE;
            } else if (desiredPosition < MIN_TO_INTAKE) {
                desiredPosition = MIN_TO_INTAKE;
            }

            liveBottomPID.setReference(desiredPosition, ControlType.kPosition);
        }
    }
    public static void override(double power) {
        if(Math.abs(power)>.07) {
            desiredPosition = desiredPosition + (1.5 * power);
            
            if (desiredPosition < 0) {
                desiredPosition = desiredPosition + (5 * power);
                liveBottomMotor.getEncoder().setPosition(desiredPosition);
                desiredPosition = 0;   
            }
            liveBottomPID.setReference(desiredPosition, ControlType.kPosition);
        }
        
    }
    public static void autoZero() {
        autoZeroModeEnabled = true;
        autoZeroEndTime = System.currentTimeMillis() + 2000;
        liveBottomMotor.set(.1);
        
    }
}