package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import frc.robot.libs.CurrentBreaker;

public class Intake {

    private static CANSparkMax intakeMotor;
    public static CurrentBreaker currentBreaker;
    private static boolean running = false;
    private static DoubleSolenoid intakeDeploy;
    private static double position;
    private static boolean curPosition;

    private static boolean Up(boolean pos) {
        curPosition = pos;
        return pos;
    }
    public static void init() {
       
        // intakeDeploy = new DoubleSolenoid(PneumaticsModuleType.REVPH, Wiring.INTAKE_DEPLOY_FORWARD, Wiring.INTAKE_DEPLOY_REVERSE);

        intakeMotor = new CANSparkMax(Wiring.INTAKE_MOTOR_ID, MotorType.kBrushless);
        intakeMotor.restoreFactoryDefaults(); 
        intakeMotor.setClosedLoopRampRate(0.5);
        intakeMotor.setSmartCurrentLimit(20);
        intakeMotor.setIdleMode(IdleMode.kBrake);
    }

    public static void tick() {
        // SmartDashboard.putNumber("Intake Deploy Encoder", intakeDeployMotor.getSelectedSensorPosition(0));
        // SmartDashboard.putNumber("Intake ABS", intakeDeployMotor.getSensorCollection().getPulseWidthPosition());
        
    }

    public static void deployIntake() {
    //  intakeDeploy.set(DoubleSolenoid.Value.kForward);
    Up(false);
    }

    public static void retractIntake() {
        // intakeDeploy.set(DoubleSolenoid.Value.kReverse);
        Up(true);
    }

    public static void startIntake() {
        deployIntake();
        intakeMotor.set(1);
        running = true; 
    }
    
    public static void reverseIntake() {
        deployIntake();
        intakeMotor.set(-1);
        running = true; 
    }

    public static void stopIntake() {
        intakeMotor.set(0);
        running = false; 
    }

    public static boolean isRunning() {
        return running;
    }
    public static boolean getIntakePosition() {
        return curPosition;
    }
}