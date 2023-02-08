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

        bistablePID.setP(Calibration.BISTABLE_MOTOR_P);
    }
}
