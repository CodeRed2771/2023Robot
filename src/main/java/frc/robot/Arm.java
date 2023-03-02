package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.SerialPort.WriteBufferMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Arm {
    
    public static enum extenderPresets {
        RETRACTED,
        GROUND,
        LOW,
        HIGH,
        FEEDER_STATION
    }

    public static enum shoulderPresets {
        PICKUP_CONE,
        PICKUP_CUBE,
        PICKUP_FEEDER_STATION,
        PLACING_GROUND,
        PLACING_LOW,
        PLACING_HIGH
    }

    private static CANSparkMax shoulderMotor;
    private static CANSparkMax extendMotor;
    private static SparkMaxPIDController shoulderPID;
    private static SparkMaxPIDController extendPID;

    private static final int MAX_EXTEND_CURRENT = 30;
    private static final int MAX_SHOULDER_CURRENT = 30;

    private static final double MAX_INSIDE_ROBOT_EXTENSION = 95;//65 was too low
    private static final double MAX_GROUND_LEVEL_EXTENSION = 220;
    private static final double MAX_IN_AIR_EXTENSION = 500; //420

    private static double MAX_SHOULDER_SPEED = 0;
    private final static double MAX_SHOULDER_TRAVEL = 500;

    private static double minExtension = 0;
    private static double minShoulderPosition = 0;
    private static double extendRequestedPos = minExtension;
    private static double shoulderRequestedPos = minShoulderPosition;

    public static void init() {

        //motor responsible of extension of bistable material
        extendMotor = new CANSparkMax(Wiring.BISTABLE_MOTOR, MotorType.kBrushless);
        extendMotor.restoreFactoryDefaults();
        extendMotor.setClosedLoopRampRate(0.5);

        extendMotor.setSmartCurrentLimit(MAX_EXTEND_CURRENT);
        extendMotor.setIdleMode(IdleMode.kBrake);
        extendMotor.setInverted(true);

        //motor responsible of lifting motor that claw+lift is connected to
        shoulderMotor = new CANSparkMax(Wiring.SHOULDER_MOTOR, MotorType.kBrushless);
        shoulderMotor.restoreFactoryDefaults();
        shoulderMotor.setClosedLoopRampRate(0.5);

        shoulderMotor.setSmartCurrentLimit(MAX_SHOULDER_CURRENT);
        shoulderMotor.setIdleMode(IdleMode.kBrake);
        shoulderMotor.setInverted(false);

        extendPID = extendMotor.getPIDController();
        shoulderPID = shoulderMotor.getPIDController();

        minExtension = 0;
        extendRequestedPos = minExtension;

        //Setting PID
        extendPID.setP(Calibration.BISTABLE_MOTOR_P);
        extendPID.setI(Calibration.BISTABLE_MOTOR_I);
        extendPID.setD(Calibration.BISTABLE_MOTOR_D);
        extendPID.setIZone(Calibration.BISTABLE_MOTOR_IZONE);
        shoulderPID.setP(Calibration.SHOULDER_MOTOR_P);
        shoulderPID.setI(Calibration.SHOULDER_MOTOR_I);
        shoulderPID.setD(Calibration.SHOULDER_MOTOR_D);
        shoulderPID.setIZone(Calibration.SHOULDER_MOTOR_IZONE);

        //Other Setup
        extendPID.setOutputRange(-1, 1);
        shoulderPID.setOutputRange(-1,1);

        extendPID.setSmartMotionMaxVelocity(25, 0);
        shoulderPID.setSmartMotionMaxVelocity(25, 0);

        extendPID.setSmartMotionMinOutputVelocity(0, 0);
        shoulderPID.setSmartMotionMinOutputVelocity(0, 0);

        extendPID.setSmartMotionMaxAccel(10, 0);
        shoulderPID.setSmartMotionMaxAccel(10,0);

        extendPID.setSmartMotionAllowedClosedLoopError(0.5,0);
        shoulderPID.setSmartMotionAllowedClosedLoopError(0.5,0);

        extendPID.setReference(extendRequestedPos, CANSparkMax.ControlType.kPosition);
        shoulderPID.setReference(shoulderRequestedPos, CANSparkMax.ControlType.kPosition);

        MAX_SHOULDER_SPEED = 0;

    }

    public static void reset() {
        extendMotor.getEncoder().setPosition(0);
        shoulderMotor.getEncoder().setPosition(0);
        extendRequestedPos = 0;
        minExtension = 0;
        minShoulderPosition = 0;
        shoulderRequestedPos = 0;
        extendPID.setReference(extendRequestedPos, CANSparkMax.ControlType.kPosition);   
        shoulderPID.setReference(shoulderRequestedPos, CANSparkMax.ControlType.kPosition);
    }

    public static void tick() {
        SmartDashboard.putNumber("Arm Extension Set Point", extendRequestedPos);
		SmartDashboard.putNumber("Arm Extension Actual", extendMotor.getEncoder().getPosition());
		SmartDashboard.putNumber("shoulder Position Actual", shoulderMotor.getEncoder().getPosition());
    }

    public static void presetExtend(extenderPresets position) {
        switch(position) {
            case FEEDER_STATION:
                extendRequestedPos = 32;
                break;
            case RETRACTED:
                extendRequestedPos = 50;//??
                break;
            case GROUND:
                extendRequestedPos = 100;//??
                break;
            case LOW:
                extendRequestedPos = 150;//??
            case HIGH:
                extendRequestedPos = 200;//??
        }
        extendPID.setReference(extendRequestedPos, CANSparkMax.ControlType.kPosition);
    }

    public static void extend(double pwr) {
        if (Math.abs(pwr)>.05) {
            extendRequestedPos = extendRequestedPos + (2.2 * pwr);
        
            if (extendRequestedPos < minExtension) 
                extendRequestedPos = minExtension;
            if (shoulderRequestedPos > 290 && extendRequestedPos > (minExtension + MAX_INSIDE_ROBOT_EXTENSION))  
                extendRequestedPos = (minExtension + MAX_INSIDE_ROBOT_EXTENSION);
            else if (shoulderRequestedPos > 162 && extendRequestedPos > (minExtension + MAX_GROUND_LEVEL_EXTENSION))  
                extendRequestedPos = (minExtension + MAX_GROUND_LEVEL_EXTENSION);
            else if (extendRequestedPos > (minExtension + MAX_IN_AIR_EXTENSION))  
                extendRequestedPos = (minExtension + MAX_IN_AIR_EXTENSION);
            extendPID.setReference(extendRequestedPos, CANSparkMax.ControlType.kPosition);        
        }        
     }

    public static void overrideExtend(double pwr) {
        if (Math.abs(pwr)>.05) {
            pwr = pwr * .25;
            extendRequestedPos = extendRequestedPos + (3 * pwr);

            if (extendRequestedPos < minExtension) {
                minExtension = extendRequestedPos;
            }
            // by pushing past the max extension, it actually adjusts the min position out.
            if (extendRequestedPos > (minExtension + MAX_IN_AIR_EXTENSION)) {
                minExtension = minExtension + (extendRequestedPos - (minExtension + MAX_IN_AIR_EXTENSION));
            }

            extendPID.setReference(extendRequestedPos, CANSparkMax.ControlType.kPosition);
        }
    }

    public static double getExtendRequestedPosition() {
        return extendRequestedPos;
    }

    public static boolean getIsExtenderExtended() {
        return extendRequestedPos > ((minExtension + MAX_IN_AIR_EXTENSION) / 3); // extened 1/3 out or more
    }

    public static void presetLift(shoulderPresets position) {
        switch(position) {
            case PICKUP_FEEDER_STATION:
                MAX_SHOULDER_SPEED = 1;
                shoulderRequestedPos = 79;  // 3-1-23 seems very finicky
                break;
            case PICKUP_CONE:
                MAX_SHOULDER_SPEED=1;
                shoulderRequestedPos = 50;//??
                break;
            case PICKUP_CUBE:
                shoulderRequestedPos = 100;//??
                MAX_SHOULDER_SPEED = 1;
                break;
            case PLACING_GROUND:
                shoulderRequestedPos = 150;//??
                MAX_SHOULDER_SPEED=0.7;
            case PLACING_LOW:
                shoulderRequestedPos = 150;//??
                MAX_SHOULDER_SPEED=0.55;
            case PLACING_HIGH:
                shoulderRequestedPos = 150;//??
                MAX_SHOULDER_SPEED = 0.4;
        }

        shoulderPID.setReference(shoulderRequestedPos, CANSparkMax.ControlType.kPosition);
    }

    public static void lift(double pwr) {
        
        if (Math.abs(pwr)>.05) {
            if(extendRequestedPos > 150) {
                pwr = pwr * .3;
                // if(pwr > (1/1500)*extendRequestedPos+0.75)
                //     pwr = (1/1500)*extendRequestedPos+0.75;
            }
            shoulderRequestedPos = shoulderRequestedPos + (3.5 * -pwr);
            
            if (shoulderRequestedPos < minShoulderPosition) 
                shoulderRequestedPos = minShoulderPosition;
            if (shoulderRequestedPos > (minShoulderPosition + MAX_SHOULDER_TRAVEL))  
                shoulderRequestedPos = (minShoulderPosition + MAX_SHOULDER_TRAVEL);

            shoulderPID.setReference(shoulderRequestedPos, CANSparkMax.ControlType.kPosition);
        }
     }

    public static void overrideLift(double pwr) {
        if (Math.abs(pwr)>.05) {
            pwr = pwr * .25;
            shoulderRequestedPos = shoulderRequestedPos + (-pwr);

            if (shoulderRequestedPos < minShoulderPosition) {
                minShoulderPosition = shoulderRequestedPos;
            }
            // by pushing past the max extension, it actually adjusts the min position out.
            if (shoulderRequestedPos > (minShoulderPosition + MAX_SHOULDER_TRAVEL)) {
                minShoulderPosition = minShoulderPosition + (shoulderRequestedPos - (minShoulderPosition + MAX_SHOULDER_TRAVEL));
            }

            shoulderPID.setReference(shoulderRequestedPos, CANSparkMax.ControlType.kPosition);
        }
    }

    private static boolean zeroActive = false;
    private static boolean zeroFast = false;
    private static LimitSwitch shouldLimitSwitch = new LimitSwitch(Wiring.SHOULDER_LIMIT_SWITCH_CHANNEL);

    public static void zero(){
        zeroFast = false;
        zeroActive = true;
    }
    public static void zeroFast(){
        zeroFast = true;
        zeroActive = true;
    }
    public static void zeroCancel(){
        zeroFast = false;
        zeroActive = false;
    }
    public static void zeroTick(){
        if(zeroActive){
            if(!zeroFast){
                if(!shouldLimitSwitch.isPressed()){
                    shoulderRequestedPos -= .5;
                    shoulderPID.setReference(shoulderRequestedPos, CANSparkMax.ControlType.kPosition);
                } else {
                    zeroActive = false;
                }
            } else {
                if(!shouldLimitSwitch.isPressed()){
                    shoulderRequestedPos -= 1;
                    shoulderPID.setReference(shoulderRequestedPos, CANSparkMax.ControlType.kPosition);
                } else {
                    zeroActive = false;
                }
            }
        }
    }
}
