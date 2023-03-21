package frc.robot;//should be fixed now 3/11

import com.revrobotics.CANSparkMax;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
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
/**
Made by ChatGPT with a few edits<p>

The Arm class controls the robot's arm, which consists of an extender and a shoulder.
The extender extends and retracts to change the reach of the arm, while the shoulder
rotates to change the orientation of the arm. This class uses PID controllers to move
the arm to specific positions and has preset positions for common arm configurations.
This class uses a color sensor to detect when the arm is in danger of going too far out or too far in.
 If the sensor detects a color above a certain threshold, it
prevents the arm from extending or retracting further to avoid collisions.
The Arm class also has a calibration mode to determine the maximum extension of the arm
in different configurations.
*/
public class Arm {
    
    /**

The extenderPresets enumeration lists the available preset values for the extender.
The available preset values are:<p>
        RETRACTED,<p>
        GROUND,<p>
        LOW,<p>
        HIGH,<p>
        GATE_MODE,<p>
        PICKUP,<p>
        FEEDER_STATION,<p>
        BACK_FEEDER_STATION
*/
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

/**   The shoulderPresets enumeration lists the available preset values for the shoulder.
The available preset values are:<p>
        PICKUP_CONE,<p>
        PICKUP_CUBE,<p>
        PICKUP_FEEDER_STATION,<p>
        PICKUP_BACK_FEEDER_STATION,<p>
        GATE_MODE,<p>
        PLACING_GROUND,<p>
        PLACING_LOW,<p>
        PLACING_HIGH
*/
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

    private static int RED_THRESHOLD = 250;
    private static int BLUE_THRESHOLD = 350;

    static SendableChooser<Boolean> manShoulder;

    private static CANSparkMax shoulderMotor;
    private static CANSparkMax extendMotor;
    private static SparkMaxPIDController shoulderPID;
    private static SparkMaxPIDController extendPID;
    
    private static DutyCycleEncoder shoulderAbsEncoder; 

    private static final int MAX_EXTEND_CURRENT = 50;
    private static final int MAX_SHOULDER_CURRENT = 50;

    //                                 ticks per inch = 2.132 (with new motor 3/13)
    //                                 now 2.045 ticks per inch (3/21 colson wheels)  
    //                                 Colson wheels - .489" of extension per revolution
    //                                 (that number is just to give these next numbers some context)
    //                                 (these numbers are still in ticks (motor revolutions)
    private static final double MAX_INSIDE_ROBOT_EXTENSION = 13; 
    private static final double MAX_GROUND_LEVEL_EXTENSION = 18;
    private static final double MAX_IN_AIR_EXTENSION = 110; 
    
    private static final double MIN_RETRACTION_INSIDE_ROBOT = 8;

    private static final double SHOULDER_ABS_MAX_UP = .58;
    private static final double SHOULDER_ABS_MAX_DOWN = .12;

    private static double SHOULDER_START_POSITION = 0;
    private static double SHOULDER_GROUND_POSITION = 6.2; 
    private static double SHOULDER_IN_ROBOT_POSITION = 8.5; 
    private final static double SHOULDER_MAX_POSITION = 16;

    private static double MAX_SHOULDER_SPEED = 0;

    private static double extendRequestedPos = 0;
    private static double shoulderRequestedPos = 0;
    private static double lastExtendRequestedPos = 999;
    private static double lastShoulderRequestedPos = 999;
    private static boolean extendOverrideMode = false;
    private static boolean shoulderOverrideMode = false;
    private static boolean extendAutoCalibrateMode = false;

    private static ColorSensorV3 armColorSensor;

    public static void init() {
        SmartDashboard.putNumber("shoulder test", .395);
        manShoulder = new SendableChooser<Boolean>();
        manShoulder.setDefaultOption("False", false);
        manShoulder.addOption("True", true);
        SmartDashboard.putData("Manual Shoulder", manShoulder);

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
        extendPID.setFF(Calibration.BISTABLE_MOTOR_F);
        extendPID.setIZone(Calibration.BISTABLE_MOTOR_IZONE);

        shoulderPID.setP(Calibration.SHOULDER_MOTOR_P);
        shoulderPID.setI(Calibration.SHOULDER_MOTOR_I);
        shoulderPID.setD(Calibration.SHOULDER_MOTOR_D);
        shoulderPID.setIZone(Calibration.SHOULDER_MOTOR_IZONE);

        //Other Setup
        extendPID.setOutputRange(-1, 1);
        shoulderPID.setOutputRange(-1,1);

        extendPID.setSmartMotionMaxVelocity(Calibration.BISTABLE_MAX_VELOCITY, 0);
        shoulderPID.setSmartMotionMaxVelocity(25, 0);

        extendPID.setSmartMotionMaxAccel(Calibration.BISTABLE_MAX_ACCEL, 0);
        shoulderPID.setSmartMotionMaxAccel(10,0);

        extendPID.setSmartMotionMinOutputVelocity(0, 0);
        shoulderPID.setSmartMotionMinOutputVelocity(0, 0);

        extendPID.setSmartMotionAllowedClosedLoopError(1.5,0);
        shoulderPID.setSmartMotionAllowedClosedLoopError(1.5,0);

        extendRequestedPos = 0;
        shoulderRequestedPos = SHOULDER_START_POSITION;

        MAX_SHOULDER_SPEED = 0;
    }

    public static void tick() {
        if (extendAutoCalibrateMode) {
            extendOverrideMode = true;
            extendRequestedPos = extendRequestedPos - 3;
        }
        extendRequestedPos = validateExtenderRequest(extendRequestedPos);

        if (extendRequestedPos != lastExtendRequestedPos) {
            extendPID.setReference(extendRequestedPos, CANSparkMax.ControlType.kSmartMotion);
            lastExtendRequestedPos = extendRequestedPos;
        }

        if(manShoulder.getSelected()){
            shoulderRequestedPos = convertAbsEncoderToTicks(SmartDashboard.getNumber("shoulder test",.395));
        }
        

        shoulderRequestedPos = validateShoulderRequest(shoulderRequestedPos);
        if(manShoulder.getSelected()){
            manShoulder = new SendableChooser<Boolean>();
            manShoulder.setDefaultOption("False", false);
            SmartDashboard.putData("Manual Shoulder", manShoulder);
            manShoulder.addOption("True", true);
            SmartDashboard.putData("Manual Shoulder", manShoulder);
            
        }

        if (shoulderRequestedPos != lastShoulderRequestedPos) {
            shoulderPID.setReference(shoulderRequestedPos, CANSparkMax.ControlType.kPosition);
            
            lastShoulderRequestedPos = shoulderRequestedPos;
        }
 
        if(armColorSensor.getBlue() == 0 || armColorSensor.getRed() == 0){
            extendAutoCalibrateMode = false;
        }
        SmartDashboard.putNumber("Arm Extension Set Point", extendRequestedPos);
		SmartDashboard.putNumber("Arm Extension Actual", extendMotor.getEncoder().getPosition());
		SmartDashboard.putNumber("shoulder Position Actual", shoulderMotor.getEncoder().getPosition());
        SmartDashboard.putNumber("Shoulder Absoulte Encoder", shoulderAbsEncoder.get());
        SmartDashboard.putNumber("Shoulder Requested", shoulderRequestedPos);
        SmartDashboard.putNumber("Arm Color Sensor - Red", armColorSensor.getRed());
        SmartDashboard.putNumber("Arm Color Sensor - Blue", armColorSensor.getBlue());

    }

    public static void reset() {

        extendAutoCalibrateMode = true;
        extendRequestedPos = 5;  // don't start quite at zero in case we're already at zero
        
        resetShoulderEncoder();
        shoulderRequestedPos = SHOULDER_START_POSITION;
    }
    
    public static void resetShoulder() {
        shoulderRequestedPos = getNewEncoderPos();
        resetShoulderEncoder();
    }


    public static void presetExtend(extenderPresets position) {
        
        extendOverrideMode = false;

        switch(position) {
            case FEEDER_STATION:
                extendRequestedPos = 8.6;
                break;
            case BACK_FEEDER_STATION:
                extendRequestedPos = 36.1;
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
                extendRequestedPos = 24.3;//??
                break;
            case PICKUP:
                extendRequestedPos = 4.5;//??
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

            if (shReq > SHOULDER_MAX_POSITION)  
                shReq =  SHOULDER_MAX_POSITION;
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
    
        } else {
            // we're overriding
            if (extReq < 0) {
                extReq = 0;
                resetExtendEncoder(2);
            }
        }

        // CHECK FOR HARD LIMITS ON EXTENSION AND RETRACTION
        SmartDashboard.putBoolean("Red Check 1", extReq < extendMotor.getEncoder().getPosition());
        SmartDashboard.putBoolean("Red Check 2", armColorSensor.getRed() > RED_THRESHOLD);
        SmartDashboard.putBoolean("Red Check both", extReq < extendMotor.getEncoder().getPosition() && armColorSensor.getRed() > RED_THRESHOLD);
        if (extReq < extendMotor.getEncoder().getPosition() && armColorSensor.getRed() > RED_THRESHOLD) {
            // hit hard retract limit while trying to retract
            // stop it and reset the zero to automatically  recalibrate
            extReq = 0;
            resetExtendEncoder(0);
            extendAutoCalibrateMode = false;
        }

        SmartDashboard.putBoolean("Blue Check 1", extReq > extendMotor.getEncoder().getPosition());
        SmartDashboard.putBoolean("Blue Check 2", armColorSensor.getBlue() > BLUE_THRESHOLD);
        SmartDashboard.putBoolean("Blue Check both", extReq > extendMotor.getEncoder().getPosition() && armColorSensor.getBlue() > BLUE_THRESHOLD);
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
                shoulderRequestedPos = 5;  
                break;
            case PICKUP_BACK_FEEDER_STATION:
                MAX_SHOULDER_SPEED = 1;
                shoulderRequestedPos = 1.2;
                break;
            case GATE_MODE:
                MAX_SHOULDER_SPEED = 1;
                shoulderRequestedPos = 12.3;
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
                shoulderRequestedPos = 0;//??
                MAX_SHOULDER_SPEED = 0.4;
                break;
        }
    }

    public static void shoulderMove(double pwr) {
        
        if (Math.abs(pwr)>.07) {
            // if(extendRequestedPos > MAX_GROUND_LEVEL_EXTENSION) {
            //     // reduced speed when arm is extended
            //     pwr = pwr * .1;
            // }
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
        double OUT_MAX = SHOULDER_MAX_POSITION; // relative encoder - full forward/down
        double curPos = shoulderAbsEncoder.get();

        return ((curPos-IN_MIN)*(OUT_MAX-OUT_MIN)/(IN_MAX-IN_MIN)+OUT_MIN);
    }


    public static double convertTicksToAbsEncoder(double ticks){
        return SHOULDER_ABS_MAX_UP+(ticks)*getTicksToAbsEncoderRatio();
    }

    public static double convertAbsEncoderToTicks(double absEncoder){
        return (absEncoder-SHOULDER_ABS_MAX_UP)/getTicksToAbsEncoderRatio();
    }

    public static double getTicksToAbsEncoderRatio(){
        return ((SHOULDER_ABS_MAX_UP-SHOULDER_ABS_MAX_DOWN)/(SHOULDER_START_POSITION-SHOULDER_MAX_POSITION));
    }
}
