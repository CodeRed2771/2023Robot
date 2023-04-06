package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Claw2 {
    private static DoubleSolenoid clawSolenoid;

    public static void init() {
        clawSolenoid = new DoubleSolenoid(PneumaticsModuleType.REVPH, Wiring.CLAW_SOLENOID_OPEN, Wiring.CLAW_SOLENOID_CLOSED);
    }

    public static void open() {
        clawSolenoid.set(Value.kForward);
    }
    
    public static void close() {
        clawSolenoid.set(Value.kReverse);
    }
}
