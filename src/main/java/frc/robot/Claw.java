package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Servo;
import frc.robot.libs.CurrentBreaker;


public class Claw {

    private static CANSparkMax clawMotor;
    private static Servo wrist;
    private static double position;
    private static int clawRecordedTicks = 0;
    private static CurrentBreaker clawCurrentBreaker;
    private static enum clawCalls {
        POWERED_OPEN,
        POWERED_CLOSED,
        UNPOWERED
    }
    private static clawCalls clawCurrentCall = clawCalls.UNPOWERED;



    public static void init() {
       
        clawMotor = new CANSparkMax(Wiring.CLAW_MOTOR_ID, MotorType.kBrushed);

        wrist = new Servo(Wiring.CLAW_CHANNEL_ID);
        wrist.set(.0);
        position = .0;

    }

    
    public static void openClawTOPos() {
        clawMotor.set(.75);
    }
    
    public static void closeClawTONeg() {
        clawMotor.set(-.75);
    }

    public static void stopClawTO() {
        clawMotor.set(0);
    }
    

    public static void tickAuto() {
        if(clawRecordedTicks < 1000 && clawCurrentCall == clawCalls.POWERED_OPEN) {
            clawMotor.set(0.7);
            clawRecordedTicks++;
        }
        if(clawRecordedTicks > 0 && clawCurrentCall == clawCalls.POWERED_CLOSED) {
            clawMotor.set(-0.7);
            clawRecordedTicks--;
        }
        if(clawRecordedTicks == 0 || clawRecordedTicks == 1000 || clawCurrentBreaker.tripped()) {
            clawCurrentCall = clawCalls.UNPOWERED;
        }
    }
    
    public static void closeClawA() {
        clawCurrentCall = clawCalls.POWERED_CLOSED;
    }
    public static void openClawA() {
        clawCurrentCall = clawCalls.POWERED_OPEN;
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

