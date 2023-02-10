package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Arm {
    private static CANSparkMax pancakeMotor;
    private static CANSparkMax bistableMotor;
    private static SparkMaxPIDController pancakePID;
    private static SparkMaxPIDController bistablePID;
    private static final int MAX_BISTABE_CURRENT = 50;
    private static final int MAX_PANCAKE_CURRENT = 50;
    private final static double MAX_BISTABLE_EXTENSION = 50;//Unknown Values below as of 2-7
    private final static double MAX_PANCAKE_EXTENSION = 80;
    private final static double MAX_BISTABE_RETRACTION = 0;
    private final static double MAX_PANKAKE_RETRACTION = 0;//Unknown Values end
    public static enum bistablePresets {
        RETRACTED,
        GROUND,
        LOW,
        HIGH
    }
    public static enum pancakePresets {
        PICKUP_CONE,
        PICKUP_CUBE,
        PLACING
    }

    public static double bistableRequestedPos = 0;
    public static double pancakeRequestedPos = 0;

    public static void init() {
        
        //motor responsible of extension of bistable material
        bistableMotor = new CANSparkMax(Wiring.BISTABLE_MOTOR, MotorType.kBrushless);
        bistableMotor.restoreFactoryDefaults();
        bistableMotor.setClosedLoopRampRate(0.5);

        bistableMotor.setSmartCurrentLimit(MAX_BISTABE_CURRENT);
        bistableMotor.setIdleMode(IdleMode.kBrake);
        bistableMotor.setInverted(false);//don't know what this is, if needed, and what it needs to be set to.

        //motor responsible of lifting motor that claw+lift is connected to
        pancakeMotor = new CANSparkMax(Wiring.PANCAKE_MOTOR, MotorType.kBrushless);
        pancakeMotor.restoreFactoryDefaults();
        pancakeMotor.setClosedLoopRampRate(0.5);

        pancakeMotor.setSmartCurrentLimit(MAX_PANCAKE_CURRENT);
        pancakeMotor.setIdleMode(IdleMode.kBrake);
        pancakeMotor.setInverted(false);//don't know what this is, if needed, and what it needs to be set to.

        bistablePID = bistableMotor.getPIDController();
        pancakePID = pancakeMotor.getPIDController();

        //Setting PID
        bistablePID.setP(Calibration.BISTABLE_MOTOR_P);
        bistablePID.setI(Calibration.BISTABLE_MOTOR_I);
        bistablePID.setD(Calibration.BISTABLE_MOTOR_D);
        bistablePID.setIZone(Calibration.BISTABLE_MOTOR_IZONE);
        pancakePID.setP(Calibration.PANCAKE_MOTOR_P);
        pancakePID.setI(Calibration.PANCAKE_MOTOR_I);
        pancakePID.setD(Calibration.PANCAKE_MOTOR_D);
        pancakePID.setIZone(Calibration.PANCAKE_MOTOR_IZONE);

        //Other Setup
        bistablePID.setOutputRange(-1, 1);
        pancakePID.setOutputRange(-1,1);

        bistablePID.setSmartMotionMaxVelocity(25, 0);
        pancakePID.setSmartMotionMaxVelocity(25, 0);

        bistablePID.setSmartMotionMinOutputVelocity(0, 0);
        pancakePID.setSmartMotionMinOutputVelocity(0, 0);

        bistablePID.setSmartMotionMaxAccel(10, 0);
        pancakePID.setSmartMotionMaxAccel(10,0);

        bistablePID.setSmartMotionAllowedClosedLoopError(0.5,0);
        pancakePID.setSmartMotionAllowedClosedLoopError(0.5,0);

    }
    public static void tick() {
		SmartDashboard.putNumber("Climber1 Position Actual", bistableMotor.getEncoder().getPosition());
		SmartDashboard.putNumber("Climber2 Position Actual", pancakeMotor.getEncoder().getPosition());
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
        bistableRequestedPos = bistableRequestedPos + (2*pwr);
        bistablePID.setReference(bistableRequestedPos, CANSparkMax.ControlType.kPosition);
    }

    public static void presetLift(pancakePresets position) {
        switch(position) {
            case PICKUP_CONE:
                pancakeRequestedPos = 50;//??
                break;
            case PICKUP_CUBE:
                pancakeRequestedPos = 100;//??
                break;
            case PLACING:
                pancakeRequestedPos = 150;//??
        }
    }

    public static void lift(double pwr) {
        pancakeRequestedPos = pancakeRequestedPos + (0.25*pwr);
        pancakePID.setReference(pancakeRequestedPos, CANSparkMax.ControlType.kPosition);
    }
}
