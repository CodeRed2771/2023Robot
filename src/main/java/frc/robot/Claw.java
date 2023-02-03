public class Claw {
    private static DoubleSolenoid claw;

    public static void openClaw() {
        claw.set(DoubleSolenoid.Value.kForward);
        }
    
    public static void closeClaw() {
        claw.set(DoubleSolenoid.Value.kReverse);
    }



}

