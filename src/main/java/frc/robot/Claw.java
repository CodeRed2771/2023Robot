package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Servo;


public class Claw {

    private static DoubleSolenoid claw;
    private static Servo wrist;
    private static double position;

    public static void init() {
       
        wrist = new Servo(Wiring.CLAW_CHANNEL_ID);
        wrist.set(0);
        position = 0;

    }
    public static void openClaw() {
        
        }
        public static void openClawTO() {
        
        }
        public static void closeClawTO() {
        
        }
    public static void closeClaw() {
        
    }

    public static void openClawA() {

    }

    public static void closeClawA() {

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

