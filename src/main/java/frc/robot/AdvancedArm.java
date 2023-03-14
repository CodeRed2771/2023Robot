package frc.robot;//the inch arm that needs to be updated

import javax.naming.ldap.ExtendedRequest;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.SerialPort.WriteBufferMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AdvancedArm {
    
    public static enum extenderPresets {
        RETRACTED,
        GROUND,
        LOW,
        HIGH,
        GATE_MODE,
        PICKUP,
        FEEDER_STATION
    }

    public static enum shoulderPresets {
        PICKUP_CONE,
        PICKUP_CUBE,
        PICKUP_FEEDER_STATION,
        GATE_MODE,
        PLACING_GROUND,
        PLACING_LOW,
        PLACING_HIGH
    }

    public static enum tickOrRefined {
        TICKS,
        INCHESorDEGREES,
    }

    private static CANSparkMax shoulderMotor;
    private static CANSparkMax extendMotor;
    private static SparkMaxPIDController shoulderPID;
    private static SparkMaxPIDController extendPID;

    private static final int MAX_EXTEND_CURRENT = 30;
    private static final int MAX_SHOULDER_CURRENT = 30;

    private static final double MIN_SHOULDER_POS = 45;

    private static final double MAX_INSIDE_ROBOT_EXTENSION = 10; //95 was too low 100
    private static final double MAX_GROUND_LEVEL_EXTENSION = 20;//220
    private static final double MAX_IN_AIR_EXTENSION = 55; //420 550 most recently

    private static final double MIN_RETRACTION_INSIDE_ROBOT = 3;

    private static double MAX_SHOULDER_SPEED = 0;
    private final static double MAX_SHOULDER_TRAVEL = 500;

    private static double extendRequestedPos = 0;//in inches
    private static double shoulderRequestedPos = 0;

    private static final double extenderTicksPerInch = 1/30;//ticks/inches
    private static final double shoulderTicksPerDegree = 1/30;//ticks/degrees

    public static void init() {
        zeroCancel();
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
        shoulderRequestedPos = 0;
 
        extendPID.setReference(extenderConvertInchesToTicks(extendRequestedPos), CANSparkMax.ControlType.kSmartMotion);
        updateShoulderPos();

        MAX_SHOULDER_SPEED = 0;

    }

    public static void reset() {
        extendMotor.getEncoder().setPosition(0);
        shoulderMotor.getEncoder().setPosition(0);
        extendRequestedPos = 0;
        // minExtension = 0;
        // minShoulderPosition = 0;
        shoulderRequestedPos = 0;
        extendPID.setReference(extenderConvertInchesToTicks(extendRequestedPos), CANSparkMax.ControlType.kSmartMotion);   
        updateShoulderPos();
        //zeroCancel();
        SmartDashboard.putBoolean("Arm reset being called", true);
    }
    public static void resetShoulderEncoder(double position) {
        shoulderMotor.getEncoder().setPosition(position);
    }
    public static void resetExtendEncoder(double position ) {
        extendMotor.getEncoder().setPosition(position);
    }

    public static void tick() {
        zeroTick();
        SmartDashboard.putBoolean("Arm reset being called", false);
        SmartDashboard.putNumber("Arm Extension Set Point", extendRequestedPos);
        SmartDashboard.putNumber("Arm Extension Actual", extendMotor.getEncoder().getPosition());
        SmartDashboard.putNumber("shoulder Position Actual", shoulderMotor.getEncoder().getPosition());
    }

    public static double extenderConvertInchesToTicks(double inches) {
        return inches/extenderTicksPerInch;
    }

    public static double shoulderConvertDegreesToTicks(double degrees) {
        return degrees/shoulderTicksPerDegree;
    }

    public static void presetExtend(extenderPresets position) {
        switch(position) {
            case FEEDER_STATION:
                extendRequestedPos = 3;
                break;
            case RETRACTED:
                extendRequestedPos = 0;
                break;
            case GROUND:
                extendRequestedPos = 8;
                break;
            case GATE_MODE:
                extendRequestedPos = MAX_INSIDE_ROBOT_EXTENSION;
                break;
            case LOW:
                extendRequestedPos = 15;//??
                break;
            case PICKUP:
                extendRequestedPos = 6;//??
                break;
            case HIGH:
                extendRequestedPos = MAX_IN_AIR_EXTENSION;
                break;
        }
        extendPID.setReference(extenderConvertInchesToTicks(extendRequestedPos), CANSparkMax.ControlType.kSmartMotion);
    }

    public static void extend(double pwr) {
        if (Math.abs(pwr)>.05) {
            extendRequestedPos = extendRequestedPos + (0.2 * pwr); // was 2.4/2.2 in prev. 
        
            // if (extendRequestedPos < minExtension) 
            //     extendRequestedPos = minExtension;
            // if ((shoulderRequestedPos+minShoulderPosition) > 290 && (extendRequestedPos+minExtension) > (minExtension + MAX_INSIDE_ROBOT_EXTENSION))  
            //     extendRequestedPos = (minExtension + MAX_INSIDE_ROBOT_EXTENSION);
            // else if ((shoulderRequestedPos + minShoulderPosition) > 162 && (extendRequestedPos+minExtension) > (minExtension + MAX_GROUND_LEVEL_EXTENSION))  
            //     extendRequestedPos = (minExtension + MAX_GROUND_LEVEL_EXTENSION);
            // else if ((extendRequestedPos+minExtension) > (minExtension + MAX_IN_AIR_EXTENSION))  
            //     extendRequestedPos = (minExtension + MAX_IN_AIR_EXTENSION);
            
            // if((extendRequestedPos+minExtension) <= (minExtension +MIN_RETRACTION_INSIDE_ROBOT) && (shoulderRequestedPos+minShoulderPosition) >= 290) {
            //     extendRequestedPos = minExtension + MIN_RETRACTION_INSIDE_ROBOT;
            // }

            if (extendRequestedPos < 0) 
                extendRequestedPos = 0;
            if (shoulderRequestedPos > 150 && extendRequestedPos > MAX_INSIDE_ROBOT_EXTENSION)//needs new shoulder pos
                extendRequestedPos = MAX_INSIDE_ROBOT_EXTENSION;
            else if (shoulderRequestedPos > 110 && extendRequestedPos > + MAX_GROUND_LEVEL_EXTENSION)//new shoulder pos needed
                extendRequestedPos = MAX_GROUND_LEVEL_EXTENSION;
            else if (extendRequestedPos > MAX_IN_AIR_EXTENSION)  
                extendRequestedPos = MAX_IN_AIR_EXTENSION;
            
            if(extendRequestedPos <= MIN_RETRACTION_INSIDE_ROBOT && shoulderRequestedPos >= 290) {//needs new shoulder pos
                extendRequestedPos = MIN_RETRACTION_INSIDE_ROBOT;
            }

            // if(ColorSensor.getRed() > 100) {
            //     extendRequestedPos = 0;
            // } else if(ColorSensor.getBlue() > 100) {
            //     extendRequestedPos = MAX_IN_AIR_EXTENSION;
            // }
            extendPID.setReference(extenderConvertInchesToTicks(extendRequestedPos), CANSparkMax.ControlType.kSmartMotion);        
        }        
     }

    public static void overrideExtend(double pwr) {
        if (Math.abs(pwr)>.05) {
            extendRequestedPos = extendRequestedPos + (0.15 * pwr);

            // if (extendRequestedPos < 0 && ColorSensor.getBlue() < 100) {
            //     extendRequestedPos = extendRequestedPos + (5 * pwr);
            //     resetExtendEncoder(Math.abs(extendRequestedPos));
            //     extendRequestedPos = 0;
            // }
            if (extendRequestedPos < 0) {
                extendRequestedPos = extendRequestedPos + (0.1 * pwr);
                resetExtendEncoder(extenderConvertInchesToTicks(Math.abs(extendRequestedPos)));
                extendRequestedPos = 0;
            }
          
            // limit extension
            // if (extendRequestedPos > (MAX_IN_AIR_EXTENSION) || ColorSensor.getRed() > 0) {
            //     extendRequestedPos = MAX_IN_AIR_EXTENSION;
            // }
            if (extendRequestedPos > (MAX_IN_AIR_EXTENSION)) {
                extendRequestedPos = MAX_IN_AIR_EXTENSION;
            }

            extendPID.setReference(extenderConvertInchesToTicks(extendRequestedPos), CANSparkMax.ControlType.kSmartMotion);
        }
    }

    public static double getExtendRequestedPosition(tickOrRefined ticksOrInches) {
        if(ticksOrInches == tickOrRefined.INCHESorDEGREES)
            return extenderConvertInchesToTicks(extendRequestedPos);
        else
            return extendRequestedPos;
    }

    public static boolean getIsExtenderExtended() {
        return extendRequestedPos > (MAX_IN_AIR_EXTENSION / 3); // extened 1/3 out or more
    }

    public static void presetLift(shoulderPresets position) {
        zeroCancel();
        switch(position) {
            case PICKUP_FEEDER_STATION:
                MAX_SHOULDER_SPEED = 1;
                shoulderRequestedPos = 90;  // 3-1-23 seems very finicky//116//110 before 3/13 changes
                break;
            case GATE_MODE:
                MAX_SHOULDER_SPEED = 1;
                shoulderRequestedPos = 125; //?? 390
                break;
            case PICKUP_CONE:
                MAX_SHOULDER_SPEED=1;
                shoulderRequestedPos = 160;//?? 50??
                break;
            case PICKUP_CUBE:
                shoulderRequestedPos = 160;//?? 100?
                MAX_SHOULDER_SPEED = 1;
                break;
            case PLACING_GROUND:
                shoulderRequestedPos = 110;//?? 150??
                MAX_SHOULDER_SPEED=0.7;
                break;
            case PLACING_LOW:
                shoulderRequestedPos = 75;//?? 150??
                MAX_SHOULDER_SPEED=0.55;
                break;
            case PLACING_HIGH:
                shoulderRequestedPos = MIN_SHOULDER_POS;//??
                MAX_SHOULDER_SPEED = 0.4;
                break;
                
        }

        updateShoulderPos();
    }

    public static void lift(double pwr) {
        
        if (Math.abs(pwr)>.05) {
            zeroCancel();
            if(extendRequestedPos > 15) {//UPDATE FOR NEW MOTOR
                pwr = pwr * .3;
                // if(pwr > (1/1500)*extendRequestedPos+0.75)
                //     pwr = (1/1500)*extendRequestedPos+0.75;
            }
            shoulderRequestedPos = shoulderRequestedPos + (2 * -pwr);
            
            if (shoulderRequestedPos < 0) 
                shoulderRequestedPos = 0;
            if (shoulderRequestedPos > MAX_SHOULDER_TRAVEL)  
                shoulderRequestedPos =  MAX_SHOULDER_TRAVEL;

            updateShoulderPos();
        }
     }

    public static void overrideLift(double pwr) {
        if (Math.abs(pwr)>.05) {
            zeroCancel();
            // pwr = pwr * .25;

            // shoulderRequestedPos = shoulderRequestedPos + (-pwr);

            // if (shoulderRequestedPos < 0) {
            //     resetShoulderEncoder(Math.abs(shoulderRequestedPos));
            //     shoulderRequestedPos = 0;
            // }

            shoulderRequestedPos = shoulderRequestedPos + (-pwr);

            if (shoulderRequestedPos < 0) {
                shoulderRequestedPos = shoulderRequestedPos + (3 * -pwr);
                resetShoulderEncoder(shoulderConvertDegreesToTicks(Math.abs(shoulderRequestedPos)));
                shoulderRequestedPos = 0;
            }
          
          
            // limit shoulder travel
            if (shoulderRequestedPos > (MAX_SHOULDER_TRAVEL)) {
                shoulderRequestedPos = MAX_SHOULDER_TRAVEL;
            }

            updateShoulderPos();
        }
    }

    private static boolean zeroActive = false;
    private static boolean zeroFast = false;
    private static long endTimeForZeroCalib = 0;
    private static LimitSwitch shoulderLimitSwitch = new LimitSwitch(Wiring.SHOULDER_LIMIT_SWITCH_CHANNEL,true);

    public static void zero(){
        if(!shoulderLimitSwitch.isPressed()){
            zeroFast = false;
            zeroActive = true;
            endTimeForZeroCalib = System.currentTimeMillis() + 2000 ;
        }
    }
    /**
     * @deprecated use {@code zero()} instead
     */
    public static void zeroFast(){
        if(!shoulderLimitSwitch.isPressed()){
            zeroFast = true;
            zeroActive = true;
            }
    }
    public static void zeroCancel(){
        zeroFast = false;
        zeroActive = false;
    }
    public static void zeroTick(){
        if(zeroActive){
            if (System.currentTimeMillis() >= endTimeForZeroCalib) {
                shoulderRequestedPos = 0;
                resetShoulderEncoder(shoulderConvertDegreesToTicks(shoulderRequestedPos));
                zeroActive = false;
                shoulderRequestedPos +=15;
                updateShoulderPos();
            } else {
                if(!shoulderLimitSwitch.isPressed()){
                    shoulderRequestedPos -= .8;
                    updateShoulderPos();
                }
                else {
                    shoulderRequestedPos = 0;
                    resetShoulderEncoder(shoulderRequestedPos);
                    shoulderRequestedPos +=15;
                    updateShoulderPos();
                    zeroActive = false;
                }
            }
        }
        SmartDashboard.putBoolean("Shoulder Limit Switch", shoulderLimitSwitch.isPressed());
    }

    public static boolean liftCompleted() {
        if(shoulderMotor.getEncoder().getPosition() > shoulderRequestedPos)
            return true;
        else
            return false;
    }

    public static boolean extensionCompleted() {
        if(extendMotor.getEncoder().getPosition() > extendRequestedPos)
            return true;
        else
            return false;
    }

    /**
     * updates the shoulder pos using {@code shoulderRequestedPos} var and the {@code shoulder.setReference} method
     */
    public static void updateShoulderPos() {
        shoulderPID.setReference(shoulderConvertDegreesToTicks(shoulderRequestedPos), CANSparkMax.ControlType.kPosition);
    }
}
