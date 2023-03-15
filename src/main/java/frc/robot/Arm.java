package frc.robot;//should be fixed now 3/11

import javax.naming.ldap.ExtendedRequest;

import com.revrobotics.AlternateEncoderType;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.MotorFeedbackSensor;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.SerialPort.WriteBufferMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


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
    private final static double MAX_SHOULDER_TRAVEL = 18;

    private static double extendRequestedPos = 0;
    private static double shoulderRequestedPos = 0;

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
 
        extendPID.setReference(extendRequestedPos, CANSparkMax.ControlType.kPosition);
        setShoulderPositon();

        MAX_SHOULDER_SPEED = 0;
    }

    public static void reset() {
        extendMotor.getEncoder().setPosition(0);

        extendRequestedPos = 0;
        extendPID.setReference(extendRequestedPos, CANSparkMax.ControlType.kPosition);   
        
        resetShoulderEncoder();
        shoulderMotor.getEncoder().setPosition(getNewEncoderPos()); // reset using absolute encoder
        shoulderRequestedPos = SHOULDER_START_POSITION;
        setShoulderPositon();
    }

    public static void resetShoulderEncoder() {
        shoulderMotor.getEncoder().setPosition(getNewEncoderPos()); // reset using absolute encoder
    }

    public static void resetExtendEncoder(double position ) {
        extendMotor.getEncoder().setPosition(position);
    }

    public static void tick() {
        SmartDashboard.putNumber("Arm Extension Set Point", extendRequestedPos);
		SmartDashboard.putNumber("Arm Extension Actual", extendMotor.getEncoder().getPosition());
		SmartDashboard.putNumber("shoulder Position Actual", shoulderMotor.getEncoder().getPosition());
        SmartDashboard.putNumber("Shoulder Thru Bore", shoulderAbsEncoder.get());
        SmartDashboard.putNumber("Shoulder Requested", shoulderRequestedPos);
        SmartDashboard.putNumber("Arm Color Sensor - Red", armColorSensor.getRed());
        SmartDashboard.putNumber("Arm Color Sensor - Blue", armColorSensor.getBlue());
    }

    public static void presetExtend(extenderPresets position) {
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
        extendPID.setReference(extendRequestedPos, CANSparkMax.ControlType.kPosition);
    }

    public static void extenderMove(double requestedVelocity) {
        if (Math.abs(requestedVelocity)>.07) {
            extendRequestedPos = extendRequestedPos + (1.4 * requestedVelocity); // was 2.2 
        
            if (extendRequestedPos < 0) 
                extendRequestedPos = 0;
            if (shoulderRequestedPos > 290 && extendRequestedPos > MAX_INSIDE_ROBOT_EXTENSION)  
                extendRequestedPos = MAX_INSIDE_ROBOT_EXTENSION;
            else if (shoulderRequestedPos > 162 && extendRequestedPos > + MAX_GROUND_LEVEL_EXTENSION)  
                extendRequestedPos = MAX_GROUND_LEVEL_EXTENSION;
            else if (extendRequestedPos > MAX_IN_AIR_EXTENSION)  
                extendRequestedPos = MAX_IN_AIR_EXTENSION;
            
            if(extendRequestedPos <= MIN_RETRACTION_INSIDE_ROBOT && shoulderRequestedPos >= 290) {
                extendRequestedPos = MIN_RETRACTION_INSIDE_ROBOT;
            }

            if (extendRequestedPos < extendMotor.getEncoder().getPosition() && armColorSensor.getRed() > RED_THRESHOLD) {
                // hit hard retract limit while trying to retract
                extendRequestedPos = 0;
                extendMotor.getEncoder().setPosition(0);
            }

            if(armColorSensor.getRed() > RED_THRESHOLD) {
                extendRequestedPos = 0;
            } else if(armColorSensor.getBlue() > BLUE_THRESHOLD) {
                extendRequestedPos = extendMotor.getEncoder().getPosition();
            }
            extendPID.setReference(extendRequestedPos, CANSparkMax.ControlType.kPosition);        
        }        
     }

    public static void overrideExtenderMove(double requestedVelocity) {
        if (Math.abs(requestedVelocity)>.07) {
            extendRequestedPos = extendRequestedPos + (2.9 * requestedVelocity);

            // if (extendRequestedPos < 0 && ColorSensor.getBlue() < 100) {
            //     extendRequestedPos = extendRequestedPos + (5 * pwr);
            //     resetExtendEncoder(Math.abs(extendRequestedPos));
            //     extendRequestedPos = 0;
            // }
            if (extendRequestedPos < 0) {
                resetExtendEncoder(Math.abs(extendRequestedPos));
                extendRequestedPos = 0;
            }
          
            // limit extension
            // if (extendRequestedPos > (MAX_IN_AIR_EXTENSION) || ColorSensor.getRed() > 0) {
            //     extendRequestedPos = MAX_IN_AIR_EXTENSION;
            // }
            if (extendRequestedPos > (MAX_IN_AIR_EXTENSION)) {
                extendRequestedPos = MAX_IN_AIR_EXTENSION;
            }

            if (extendRequestedPos < extendMotor.getEncoder().getPosition() && armColorSensor.getRed() > RED_THRESHOLD) {
                // hit hard retract limit while trying to retract
                extendRequestedPos = 0;
                extendMotor.getEncoder().setPosition(0);
            }

            extendPID.setReference(extendRequestedPos, CANSparkMax.ControlType.kPosition);
        }
    }

    public static double getExtendRequestedPosition() {
        return extendRequestedPos;
    }

    public static boolean getIsExtenderExtended() {
        return extendRequestedPos > (MAX_IN_AIR_EXTENSION / 3); // extened 1/3 out or more
    }

    public static void presetShoulder(shoulderPresets position) {

        switch(position) {
            case PICKUP_FEEDER_STATION:
                MAX_SHOULDER_SPEED = 1;
                shoulderRequestedPos = 110;  // 3-1-23 seems very finicky//116
                break;
            case PICKUP_BACK_FEEDER_STATION:
                MAX_SHOULDER_SPEED = 1;
                shoulderRequestedPos = 0;
                break;
            case GATE_MODE:
                MAX_SHOULDER_SPEED = 1;
                shoulderRequestedPos = 390;
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
                break;
            case PLACING_LOW:
                shoulderRequestedPos = 150;//??
                MAX_SHOULDER_SPEED=0.55;
                break;
            case PLACING_HIGH:
                shoulderRequestedPos = 0;//??
                MAX_SHOULDER_SPEED = 0.4;
                break;
                
        }

        setShoulderPositon();
    }

    public static void shoulderMove(double pwr) {
        
        if (Math.abs(pwr)>.07) {
            // zeroCancel();
            if(extendRequestedPos > MAX_GROUND_LEVEL_EXTENSION) {
                // reduced speed when arm is extended
                pwr = pwr * .1;
                // if(pwr > (1/1500)*extendRequestedPos+0.75)
                //     pwr = (1/1500)*extendRequestedPos+0.75;
            }
            shoulderRequestedPos = shoulderRequestedPos + (.2 * -pwr);
            
            // if (shoulderRequestedPos < 0) 
            //     shoulderRequestedPos = 0;
            if (shoulderRequestedPos > MAX_SHOULDER_TRAVEL)  
                shoulderRequestedPos =  MAX_SHOULDER_TRAVEL;

            setShoulderPositon();
        }
     }

    public static void overrideShoulderMove(double pwr) {
        if (Math.abs(pwr)>.07) {
            // zeroCancel();
            pwr = pwr * .25;

            // shoulderRequestedPos = shoulderRequestedPos + (-pwr);

            // if (shoulderRequestedPos < 0) {
            //     resetShoulderEncoder(Math.abs(shoulderRequestedPos));
            //     shoulderRequestedPos = 0;
            // }

            shoulderRequestedPos = shoulderRequestedPos + (-pwr);
          
            setShoulderPositon();
        }
    }

    public static boolean shoulderMoveCompleted() {
        if(Math.abs(shoulderMotor.getEncoder().getPosition() - shoulderRequestedPos) < .2)
            return true;
        else
            return false;
    }

    public static boolean extensionCompleted() {
        if(Math.abs(extendMotor.getEncoder().getPosition() - extendRequestedPos) < 2)
            return true;
        else
            return false;
    }

    /**
     * updates the shoulder pos using {@code shoulderRequestedPos} var and the {@code shoulder.setReference} method
     */
    public static void setShoulderPositon() {
        shoulderPID.setReference(shoulderRequestedPos, CANSparkMax.ControlType.kPosition);
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
