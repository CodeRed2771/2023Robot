package frc.robot;

import com.ctre.phoenix.led.ColorFlowAnimation.Direction;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.revrobotics.SparkMaxPIDController;

public class Intake {
    private static CANSparkMax intakeMotor;
    private static final int MAX_INTAKE_CURRENT = 50;
    private static SparkMaxPIDController intakeMotorPID;
    private static DoubleSolenoid deploySolenoid;

    public static void init() {
        intakeMotor = new CANSparkMax(Wiring.INTAKE_MOTOR_ID, MotorType.kBrushless);
        intakeMotor.restoreFactoryDefaults();
        intakeMotor.setSmartCurrentLimit(MAX_INTAKE_CURRENT);
        intakeMotor.setIdleMode(IdleMode.kBrake);
        deploySolenoid = new DoubleSolenoid(PneumaticsModuleType.REVPH, Wiring.INTAKE_SOLENOID_EXTENDED, Wiring.INTAKE_SOLENOID_STOW);
    }

    public static void deploy() {
        deploySolenoid.set(DoubleSolenoid.Value.kForward);
    }
    
    public static void retract() { 
        deploySolenoid.set(DoubleSolenoid.Value.kForward);
    }
    
    public static void run(double pwr) {
        intakeMotorPID.setReference(pwr, CANSparkMax.ControlType.kSmartVelocity);
    }

    public static void reverse(double pwr) {
        intakeMotorPID.setReference(-pwr, CANSparkMax.ControlType.kSmartVelocity);
    }
}

