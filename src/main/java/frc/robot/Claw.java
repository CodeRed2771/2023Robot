package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Servo;


public class Claw {

    private static CANSparkMax clawMotor;
    private static Servo wrist;
    private static double position;

    public static void init() {
       
        clawMotor = new CANSparkMax(Wiring.CLAW_MOTOR_ID, MotorType.kBrushed);

        wrist = new Servo(Wiring.CLAW_CHANNEL_ID);
        wrist.set(.0);
        position = .0;

    }
    public static void openClaw() {
        
    }
    
    public static void openClawTO() {
        clawMotor.set(.75);
    }
    
    public static void closeClawTO() {
        clawMotor.set(-.75);
    }

    public static void stopClawTO() {
        clawMotor.set(0);
    }
    
    public static void closeClaw() {
        
    }

    public static void openClawA() {

    }

    public static void closeClawA() {

    }

    public static void flip() {
        if (position == .0) {
            position = .58;
        } else {
            position = .0;
        }
        wrist.set(position);
    }



}

