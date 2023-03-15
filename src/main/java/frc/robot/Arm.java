package frc.robot;//should be fixed now 3/11

import com.revrobotics.CANSparkMax;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/*
 * TO DO
 * Programming to auto-set the extender limit by slowing retracting till
 * we see red - but only do it if we see SOME color, otherwise sensor may not
 * be working
 * 
 * More live checks for extension and shoulder positions that are incompatible
 * 
 * Calibrate Extender positions
 * Calibrate Shoulder positions
 * 
 */
public class Arm {
    
    public static enum extenderPresets {
        RETRACTED,
        GROUND,
        LOW,
        HIGH,
        GATE_MODE,
        PICKUP,
        FEEDER_STATION,
        BACK_FEEDER_STATION
    }

    public static enum shoulderPresets {
        PICKUP_CONE,
        PICKUP_CUBE,
        PICKUP_FEEDER_STATION,
        PICKUP_BACK_FEEDER_STATION,
        GATE_MODE,
        PLACING_GROUND,
        PLACING_LOW,
        PLACING_HIGH
    }

    private static int RED_THRESHOLD = 300;
    private static int BLUE_THRESHOLD = 300;

    private static CANSparkMax shoulderMotor;
    private static CANSparkMax extendMotor;
    private static SparkMaxPIDController shoulderPID;
    private static SparkMaxPIDController extendPID;
    
    private static DutyCycleEncoder shoulderAbsEncoder; 

    private static final int MAX_EXTEND_CURRENT = 50;
    private static final int MAX_SHOULDER_CURRENT = 50;

    private static final double SHOULDER_ABS_MAX_UP = .56;
    private static final double SHOULDER_ABS_MAX_DOWN = .12;

    //                                            ticks per inch = 2.132 (with new motor 3/13)
    private static final double MAX_INSIDE_ROBOT_EXTENSION = 18; 
    private static final double MAX_GROUND_LEVEL_EXTENSION = 60;
    private static final double MAX_IN_AIR_EXTENSION = 110; 
    
    private static final double MIN_RETRACTION_INSIDE_ROBOT = 8;

    private static double MAX_SHOULDER_SPEED = 0;
    private static double SHOULDER_START_POSITION = 0;
    private static double SHOULDER_IN_ROBOT_POSITION = 14; // TODO needs testing
    private static double SHOULDER_GROUND_POSITION = 10; // TODO needs testing
    private final static double MAX_SHOULDER_TRAVEL = 18;

    private static double extendRequestedPos = 0;
    private static double shoulderRequestedPos = 0;
    private static double lastExtendRequestedPos = 999;
    private static double lastShoulderRequestedPos = 999;
    private static boolean extendOverrideMode = false;
    private static boolean shoulderOverrideMode = false;

    private static ColorSensorV3 armColorSensor;

    public static void init() {
        armColorSensor = new ColorSensorV3(Port.kMXP);
        shoulderAbsEncoder = new DutyCycleEncoder(Wiring.SHOULDER_ABS_ENC);

        //motor responsible of extension of bistable material
        extendMotor = new CANSparkMax(Wiring.BISTABLE_MOTOR, MotorType.kBrushless);
        extendMotor.restoreFactoryDefaults();
        extendMotor.setClosedLoopRampRate(0.7);

        extendMotor.setSmartCurrentLimit(MAX_EXTEND_CURRENT);
        extendMotor.setIdleMode(IdleMode.kBrake);
        extendMotor.setInverted(true);

        //motor responsible of lifting motor that claw+shoulderMove is connected to
        shoulderMotor = new CANSparkMax(Wiring.SHOULDER_MOTOR, MotorType.kBrushless);
        shoulderMotor.restoreFactoryDefaults();
        shoulderMotor.setClosedLoopRampRate(0.5);

        shoulderMotor.setSmartCurrentLimit(MAX_SHOULDER_CURRENT);
        shoulderMotor.setIdleMode(IdleMode.kBrake);
        shoulderMotor.setInverted(false);

        extendPID = extendMotor.getPIDController();
        shoulderPID = shoulderMotor.getPIDController();

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

        extendRequestedPos = 0;
        shoulderRequestedPos = SHOULDER_START_POSITION;

        MAX_SHOULDER_SPEED = 0;
    }

    public static void tick() {

        extendRequestedPos = validateExtenderRequest(extendRequestedPos);
        shoulderRequestedPos = validateShoulderRequest(shoulderRequestedPos);

        if (extendRequestedPos != lastExtendRequestedPos) {
            extendPID.setReference(extendRequestedPos, CANSparkMax.ControlType.kPosition);
            lastExtendRequestedPos = extendRequestedPos;
        }
        if (shoulderRequestedPos != lastShoulderRequestedPos) {
            shoulderPID.setReference(shoulderRequestedPos, CANSparkMax.ControlType.kPosition);
            lastShoulderRequestedPos = shoulderRequestedPos;
        }
 
        SmartDashboard.putNumber("Arm Extension Set Point", extendRequestedPos);
		SmartDashboard.putNumber("Arm Extension Actual", extendMotor.getEncoder().getPosition());
		SmartDashboard.putNumber("shoulder Position Actual", shoulderMotor.getEncoder().getPosition());
        SmartDashboard.putNumber("Shoulder Thru Bore", shoulderAbsEncoder.get());
        SmartDashboard.putNumber("Shoulder Requested", shoulderRequestedPos);
        SmartDashboard.putNumber("Arm Color Sensor - Red", armColorSensor.getRed());
        SmartDashboard.putNumber("Arm Color Sensor - Blue", armColorSensor.getBlue());
    }

    public static void reset() {

        //NOT RIGHT - SHOULD RUN CODE TO RESET BY LOOKING FOR COLOR
        extendMotor.getEncoder().setPosition(0);
        extendRequestedPos = 0;
        
        resetShoulderEncoder();
        shoulderRequestedPos = SHOULDER_START_POSITION;
    }

    public static void presetExtend(extenderPresets position) {
        
        extendOverrideMode = false;

        switch(position) {
            case FEEDER_STATION:
                extendRequestedPos = 8;
                break;
            case BACK_FEEDER_STATION:
                extendRequestedPos = 75;
                break;
            case RETRACTED:
                extendRequestedPos = 0;
                break;
            case GROUND:
                extendRequestedPos = MAX_GROUND_LEVEL_EXTENSION;//??
                break;
            case GATE_MODE:
                extendRequestedPos = MAX_INSIDE_ROBOT_EXTENSION;
                break;
            case LOW:
                extendRequestedPos = 27;//??
                break;
            case PICKUP:
                extendRequestedPos = 5;//??
                break;
            case HIGH:
                extendRequestedPos = MAX_IN_AIR_EXTENSION;//??
                break;
        }
    }

   private static double validateShoulderRequest(double shReq) {
        
        if (!shoulderOverrideMode) {
            if (shReq < 0)
                shReq = 0;

            if (shReq > MAX_SHOULDER_TRAVEL)  
                shReq =  MAX_SHOULDER_TRAVEL;
        }
        
        return shReq;
     }
     private static double validateExtenderRequest(double extReq) {
        
        if (!extendOverrideMode) {
            if (extReq < 0) 
                extReq = 0;
        
                // Check for limits based on shoulder position
            if (shoulderRequestedPos > SHOULDER_IN_ROBOT_POSITION && extReq > MAX_INSIDE_ROBOT_EXTENSION)  
                extReq = MAX_INSIDE_ROBOT_EXTENSION;
            else if (shoulderRequestedPos > SHOULDER_GROUND_POSITION && extReq > + MAX_GROUND_LEVEL_EXTENSION)  
                extReq = MAX_GROUND_LEVEL_EXTENSION;
            else if (extReq > MAX_IN_AIR_EXTENSION)  
                extReq = MAX_IN_AIR_EXTENSION;
            
            // check for RETRACTION limits based on being inside the robot
            if (extReq <= MIN_RETRACTION_INSIDE_ROBOT && shoulderRequestedPos >= SHOULDER_IN_ROBOT_POSITION) {
                extReq = MIN_RETRACTION_INSIDE_ROBOT;
            }
    
        }

        // CHECK FOR HARD LIMITS ON EXTENSION AND RETRACTION

        if (extReq < extendMotor.getEncoder().getPosition() && armColorSensor.getRed() > RED_THRESHOLD) {
            // hit hard retract limit while trying to retract
            // stop it and reset the zero to automatically  recalibrate
            extReq = 0;
            resetExtendEncoder(0);
        }

        if (extReq > extendMotor.getEncoder().getPosition() && armColorSensor.getBlue() > BLUE_THRESHOLD) {
            // hit hard extend limit while trying to extend further
            // don't allow going beyond where we're at
            extReq = extendMotor.getEncoder().getPosition();
        }

        return extReq;
    }

    public static void extenderMove(double requestedVelocity) {
        if (Math.abs(requestedVelocity)>.07) {
            extendRequestedPos = extendRequestedPos + (1.4 * requestedVelocity); // was 2.2 
        }       
        extendOverrideMode = false; 
     }

    public static void overrideExtenderMove(double requestedVelocity) {
        if (Math.abs(requestedVelocity) >.07) {
            extendRequestedPos = extendRequestedPos + (2.9 * requestedVelocity);
            extendOverrideMode = true;
        } else {
            extendOverrideMode = false;
        }
    }

    public static void presetShoulder(shoulderPresets position) {
        shoulderOverrideMode = false;

        switch(position) {
            case PICKUP_FEEDER_STATION:
                MAX_SHOULDER_SPEED = 1;
                shoulderRequestedPos = 3;  
                break;
            case PICKUP_BACK_FEEDER_STATION:
                MAX_SHOULDER_SPEED = 1;
                shoulderRequestedPos = 1;
                break;
            case GATE_MODE:
                MAX_SHOULDER_SPEED = 1;
                shoulderRequestedPos = 14;
                break;
            case PICKUP_CONE:
                MAX_SHOULDER_SPEED=1;
                shoulderRequestedPos = 14;//??
                break;
            case PICKUP_CUBE:
                shoulderRequestedPos = 14;//??
                MAX_SHOULDER_SPEED = 1;
                break;
            case PLACING_GROUND:
                shoulderRequestedPos = 12;//??
                MAX_SHOULDER_SPEED=0.7;
                break;
            case PLACING_LOW:
                shoulderRequestedPos = 9;//??
                MAX_SHOULDER_SPEED=0.55;
                break;
            case PLACING_HIGH:
                shoulderRequestedPos = 1;//??
                MAX_SHOULDER_SPEED = 0.4;
                break;
        }
    }

    public static void shoulderMove(double pwr) {
        
        if (Math.abs(pwr)>.07) {
            if(extendRequestedPos > MAX_GROUND_LEVEL_EXTENSION) {
                // reduced speed when arm is extended
                pwr = pwr * .1;
            }
            shoulderRequestedPos = shoulderRequestedPos + (.2 * -pwr);
        }
        shoulderOverrideMode = false;
     }

    public static void overrideShoulderMove(double pwr) {
        if (Math.abs(pwr)>.07) {
            pwr = pwr * .25;

            shoulderRequestedPos = shoulderRequestedPos + (-pwr);
            shoulderOverrideMode = true;
        } else
            shoulderOverrideMode = false;
    }

    public static boolean shoulderMoveCompleted() {
        if(Math.abs(shoulderMotor.getEncoder().getPosition() - shoulderRequestedPos) < .2)
            return true;
        else
            return false;
    }

    public static double getExtendRequestedPosition() {
        return extendRequestedPos;
    }

    public static boolean getIsExtenderPartiallyExtended() {
        return extendRequestedPos > (MAX_IN_AIR_EXTENSION / 3); // extened 1/3 out or more
    }

    public static boolean extensionCompleted() {
        if(Math.abs(extendMotor.getEncoder().getPosition() - extendRequestedPos) < 2)
            return true;
        else
            return false;
    }
    
    public static void resetShoulderEncoder() {
        shoulderMotor.getEncoder().setPosition(getNewEncoderPos()); // reset using absolute encoder
    }

    public static void resetExtendEncoder(double position ) {
        extendMotor.getEncoder().setPosition(position);
    }

    /*
     * getNewEncoderPos
     * 
     * uses the absolute encoder to determine what the relative encoder 
     * value should be.  Used to reset the relative encoder.
     */
    public static double getNewEncoderPos() {
        double IN_MIN = SHOULDER_ABS_MAX_UP;
        double IN_MAX = SHOULDER_ABS_MAX_DOWN;
        double OUT_MIN = 0;  // relative encoder - full back
        double OUT_MAX = 20.11; // relative encoder - full forward/down
        double curPos = shoulderAbsEncoder.get();

        return ((curPos-IN_MIN)*(OUT_MAX-OUT_MIN)/(IN_MAX-IN_MIN)+OUT_MIN);
    }
}
