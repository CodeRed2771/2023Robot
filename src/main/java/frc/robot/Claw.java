package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Servo;
import frc.robot.libs.CurrentBreaker;


public class Claw {

    private static CANSparkMax clawMotor;
    private static CurrentBreaker clawCurrentBreaker;
    private static final int MAX_CLAW_CURRENT = 50;
    private static int endMillisOpen = 0;
    private static int endMillisClose = 0;
    private static int clawRecordedTicks = 0;
    private static enum clawCalls {
        POWERED_OPEN,
        POWERED_CLOSED,
        UNPOWERED
    }
    private static clawCalls clawCurrentCall = clawCalls.UNPOWERED;
    private static Servo wrist;
    private static double position;

    public static void init() {
       
        clawMotor = new CANSparkMax(Wiring.CLAW_MOTOR_ID,MotorType.kBrushed);
        clawCurrentBreaker = new CurrentBreaker(Wiring.CLAW_MOTOR_ID, 10, 250);
        clawMotor.restoreFactoryDefaults();
        clawMotor.setSmartCurrentLimit(MAX_CLAW_CURRENT);
        clawMotor.setIdleMode(IdleMode.kBrake);
        wrist = new Servo(Wiring.CLAW_CHANNEL_ID);
        wrist.set(0);
        position = 0;

    }
    public static void openClawTO() {
        clawMotor.set(0.7);
    }
    public static void closeClawTO() {
        clawMotor.set(-0.7);
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
        if (position == 0) {
            position = 1;
        } else {
            position = 0;
        }
        wrist.set(position);
    }
}