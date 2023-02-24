package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Arm {
    private static CANSparkMax shoulderMotor;
    private static CANSparkMax bistableMotor;
    private static SparkMaxPIDController shoulderPID;
    private static SparkMaxPIDController bistablePID;
    private static final int MAX_BISTABE_CURRENT = 50;
    private static final int MAX_SHOULDER_CURRENT = 50;
    private static double MAX_BISTABLE_EXTENSION = 320;
    private static double minExtension = 0;
    private final static double MAX_SHOULDER_EXTENSION = 80;
    private final static double MAX_BISTABE_RETRACTION = 0;
    private final static double MAX_SHOULDER_RETRACTION = 0;//Unknown Values end
    public static enum bistablePresets {
        RETRACTED,
        GROUND,
        LOW,
        HIGH
    }
    public static enum shoulderPresets {
        PICKUP_CONE,
        PICKUP_CUBE,
        PLACING_GROUND,
        PLACING_LOW,
        PLACING_HIGH
    }

    public static double bistableRequestedPos = minExtension;
    public static double shoulderRequestedPos = 0;

    public static void init() {


        //motor responsible of extension of bistable material
        bistableMotor = new CANSparkMax(Wiring.BISTABLE_MOTOR, MotorType.kBrushless);
        bistableMotor.restoreFactoryDefaults();
        bistableMotor.setClosedLoopRampRate(0.5);

        bistableMotor.setSmartCurrentLimit(MAX_BISTABE_CURRENT);
        bistableMotor.setIdleMode(IdleMode.kBrake);
        bistableMotor.setInverted(true);

        //motor responsible of lifting motor that claw+lift is connected to
        shoulderMotor = new CANSparkMax(Wiring.SHOULDER_MOTOR, MotorType.kBrushless);
        shoulderMotor.restoreFactoryDefaults();
        shoulderMotor.setClosedLoopRampRate(0.5);

        shoulderMotor.setSmartCurrentLimit(MAX_SHOULDER_CURRENT);
        shoulderMotor.setIdleMode(IdleMode.kBrake);
        shoulderMotor.setInverted(false);

        bistablePID = bistableMotor.getPIDController();
        shoulderPID = shoulderMotor.getPIDController();

        bistableRequestedPos = minExtension;

        //Setting PID
        bistablePID.setP(Calibration.BISTABLE_MOTOR_P);
        bistablePID.setI(Calibration.BISTABLE_MOTOR_I);
        bistablePID.setD(Calibration.BISTABLE_MOTOR_D);
        bistablePID.setIZone(Calibration.BISTABLE_MOTOR_IZONE);
        shoulderPID.setP(Calibration.SHOULDER_MOTOR_P);
        shoulderPID.setI(Calibration.SHOULDER_MOTOR_I);
        shoulderPID.setD(Calibration.SHOULDER_MOTOR_D);
        shoulderPID.setIZone(Calibration.SHOULDER_MOTOR_IZONE);

        //Other Setup
        bistablePID.setOutputRange(-1, 1);
        shoulderPID.setOutputRange(-1,1);

        bistablePID.setSmartMotionMaxVelocity(25, 0);
        shoulderPID.setSmartMotionMaxVelocity(25, 0);

        bistablePID.setSmartMotionMinOutputVelocity(0, 0);
        shoulderPID.setSmartMotionMinOutputVelocity(0, 0);

        bistablePID.setSmartMotionMaxAccel(10, 0);
        shoulderPID.setSmartMotionMaxAccel(10,0);

        bistablePID.setSmartMotionAllowedClosedLoopError(0.5,0);
        shoulderPID.setSmartMotionAllowedClosedLoopError(0.5,0);

    }

    public static void reset() {
        bistableMotor.getEncoder().setPosition(0);
        bistableRequestedPos = 0;
        minExtension = 0;
    }

    public static void tick() {
        SmartDashboard.putNumber("Arm Extension Set Point", bistableRequestedPos);
		SmartDashboard.putNumber("Arm Extension Actual", bistableMotor.getEncoder().getPosition());
		SmartDashboard.putNumber("shoulder Position Actual", shoulderMotor.getEncoder().getPosition());
    }

    public static void presetExtend(bistablePresets position) {
        switch(position) {
            case RETRACTED:
                bistableRequestedPos = 50;//??
                break;
            case GROUND:
                bistableRequestedPos = 100;//??
                break;
            case LOW:
                bistableRequestedPos = 150;//??
            case HIGH:
                bistableRequestedPos = 200;//??
        }
        bistablePID.setReference(bistableRequestedPos, CANSparkMax.ControlType.kPosition);
    }

    public static void extend(double pwr) {
        bistableRequestedPos = bistableRequestedPos + (4.5 * pwr);
        
        if (bistableRequestedPos < minExtension) 
            bistableRequestedPos = minExtension;
        if (bistableRequestedPos > (minExtension + MAX_BISTABLE_EXTENSION))  
            bistableRequestedPos = (minExtension + MAX_BISTABLE_EXTENSION);

        bistablePID.setReference(bistableRequestedPos, CANSparkMax.ControlType.kPosition);
        
     }

    public static void overrideExtend(double pwr) {
        
        bistableRequestedPos = bistableRequestedPos + (3 * pwr);

        if (bistableRequestedPos < minExtension) {
            minExtension = bistableRequestedPos;
        }
        // by pushing past the max extension, it actually adjusts the min position out.
        if (bistableRequestedPos > (minExtension + MAX_BISTABLE_EXTENSION)) {
            minExtension = minExtension + (bistableRequestedPos - (minExtension + MAX_BISTABLE_EXTENSION));
        }

        bistablePID.setReference(bistableRequestedPos, CANSparkMax.ControlType.kPosition);

    }

    public static void presetLift(shoulderPresets position) {
        switch(position) {
            case PICKUP_CONE:
                shoulderRequestedPos = 50;//??
                break;
            case PICKUP_CUBE:
                shoulderRequestedPos = 100;//??
                break;
            case PLACING_GROUND:
                shoulderRequestedPos = 150;//??
            case PLACING_LOW:
                shoulderRequestedPos = 150;//??
            case PLACING_HIGH:
                shoulderRequestedPos = 150;//??
        }
    }

    public static void lift(double pwr) {
        shoulderRequestedPos = shoulderRequestedPos + (4.5 * pwr);
        
        if (shoulderRequestedPos < minExtension) 
            shoulderRequestedPos = minExtension;
        if (shoulderRequestedPos > (minExtension + MAX_SHOULDER_EXTENSION))  
            shoulderRequestedPos = (minExtension + MAX_SHOULDER_EXTENSION);

        shoulderPID.setReference(shoulderRequestedPos, CANSparkMax.ControlType.kPosition);
        
     }

    public static void overrideLift(double pwr) {
        
        shoulderRequestedPos = shoulderRequestedPos + (pwr);

        if (shoulderRequestedPos < minExtension) {
            minExtension = shoulderRequestedPos;
        }
        // by pushing past the max extension, it actually adjusts the min position out.
        if (shoulderRequestedPos > (minExtension + MAX_SHOULDER_EXTENSION)) {
            minExtension = minExtension + (shoulderRequestedPos - (minExtension + MAX_SHOULDER_EXTENSION));
        }

        shoulderPID.setReference(shoulderRequestedPos, CANSparkMax.ControlType.kPosition);

    }

}
