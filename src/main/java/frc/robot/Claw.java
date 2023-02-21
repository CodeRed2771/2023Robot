package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Servo;


public class Claw {

    private static DoubleSolenoid claw;
    private static Servo wrist;
    private static double position;

    public static void init() {
       
        claw = new DoubleSolenoid(PneumaticsModuleType.REVPH, Wiring.CLAW_FORWARD, Wiring.CLAW_REVERSE);
        wrist = new Servo(Wiring.CLAW_CHANNEL_ID);
        wrist.set(0);
        position = 0;

    }
    public static void openClaw() {
        claw.set(DoubleSolenoid.Value.kForward);
        }
    
    public static void closeClaw() {
        claw.set(DoubleSolenoid.Value.kReverse);
    }

    public static void flip() {
        if (position == 0) {
            position = 1;
        } else {
            position = 0;
        }
        wrist.set(position);
    }



}

