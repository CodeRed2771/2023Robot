package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;

public class Claw {

    private static DoubleSolenoid claw;
    public static void init() {
       
        claw = new DoubleSolenoid(PneumaticsModuleType.REVPH, Wiring.CLAW_FORWARD, Wiring.CLAW_REVERSE);
    }
    public static void openClaw() {
        claw.set(DoubleSolenoid.Value.kForward);
        }
    
    public static void closeClaw() {
        claw.set(DoubleSolenoid.Value.kReverse);
    }



}

