package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.libs.CurrentBreaker;

/*
 * 
 * 
 * 
 *    THIS IS THE OLD CLAW  
 * 
 * 
 *    SEE Claw2.java for pnuematic claw
 * 
 * 
 * 
 * 
 * 
 */
public class Claw {

    private static CANSparkMax clawMotor;
    private static Servo wrist;
    private static AnalogPotentiometer potentiometer;
    private static AnalogInput analogInput;
    private static double position;
    private static int clawRecordedTicks = 0;
    private static CurrentBreaker clawCurrentBreaker;

    private static final double MIN_WRIST_POSITION = 0;
    private static final double MAX_WRIST_POSITION = .56;

    private static final double CLAW_FULLY_OPEN = 90;
    private static final double CLAW_FULLY_CLOSED = 99;
    private static final double CONE_POSITION = 94;

    static boolean overrideMode = false;

    // private static final double CONE_PICKUP = .035;
    // private static final double CUBE_PICKUP = .02;
    private static int direction;
    private static double clawDesiredPosition;

    private static enum clawCalls {
        POWERED_OPEN,
        POWERED_CLOSED,
        UNPOWERED
    }

    public static enum ClawPresets{
        OPEN,
        CLOSE,
        STOP,
        CONE
    }

    private static clawCalls clawCurrentCall = clawCalls.UNPOWERED;

    public static void init() {
        overrideMode = false;
        analogInput = new AnalogInput(Wiring.POTENTIOMETER_CHANNEL);
        analogInput.setAverageBits(2);
        potentiometer = new AnalogPotentiometer(analogInput, 180, 0);
        
        clawMotor = new CANSparkMax(Wiring.CLAW_MOTOR_ID, MotorType.kBrushed);

        clawMotor.setInverted(true);

        wrist = new Servo(Wiring.CLAW_CHANNEL_ID);
        wrist.set(MIN_WRIST_POSITION);
        position = MIN_WRIST_POSITION;
        direction = 0;
        // clawDesiredPosition = CLAW_FULLY_CLOSED;
    }

    public static void tick() {
        if (!overrideMode){
            checkClawLimits();
            switch (direction) {
                case 1:
                    clawClose();
                    break;
                case 0:
                    clawStop();
                    break;
                case -1:
                    clawOpen();
                    break;
            }
        }
        SmartDashboard.putNumber("Claw Direction", direction);
        SmartDashboard.putNumber("Claw Position", getCurrentClawPos());
        SmartDashboard.putNumber("Claw Desired Position", clawDesiredPosition);
        SmartDashboard.putNumber("Potentiometer", getCurrentClawPos());
    }

    private static void checkClawLimits() {
        if(getCurrentClawPos()<CLAW_FULLY_OPEN && direction == -1){
            direction = 0;
        }
        if(getCurrentClawPos()>CLAW_FULLY_CLOSED && direction == 1){
            direction = 0;
        }
        if(clawDesiredPosition>CLAW_FULLY_CLOSED){
            clawDesiredPosition = CLAW_FULLY_CLOSED;
        }

        if(clawDesiredPosition<CLAW_FULLY_OPEN){
            clawDesiredPosition = CLAW_FULLY_OPEN;
        }
        if(getCurrentClawPos()>clawDesiredPosition){
            direction = 1;
        }

        if(getCurrentClawPos()<clawDesiredPosition){
            direction = -1;
        }
        if(Math.abs(getCurrentClawPos()-clawDesiredPosition)<1){
            direction = 0;
        }

        if(getCurrentClawPos() < 40){
            direction = 0;// this if statement must be the last one to prevent claw from going when potentiometer is disconnected
        }
    }

    private static void clawOpen() {
        clawMotor.set(1);
    }

    private static void clawStop() {
        clawMotor.set(0);
    }

    private static void clawClose() {
        clawMotor.set(-1);
    }

    public static double getCurrentClawPos() {
        return potentiometer.get();
    }

    // public static void openClaw() {
        
    // }
    
    public static void openClawTO() {
        overrideMode = true;
        if(getCurrentClawPos() < CLAW_FULLY_CLOSED) {
            clawMotor.set(1);
            clawDesiredPosition = getCurrentClawPos();
        }
    }
    
    public static void closeClawTO() {
        overrideMode = true;
        if(getCurrentClawPos() > CLAW_FULLY_OPEN) {
            clawMotor.set(-1);
            clawDesiredPosition = getCurrentClawPos();
        }
    }

    public static void stopClawTO() {
        clawMotor.set(0);
        overrideMode = false;
    }

    public static void  resetToCloseClaw() {
        clawDesiredPosition = CLAW_FULLY_CLOSED;
    }
    

    public static void tickAuto() {
        if(clawRecordedTicks < 10000 && clawCurrentCall == clawCalls.POWERED_OPEN) {
            clawMotor.set(1);
            clawRecordedTicks++;
        }
        if(clawRecordedTicks > -5000 && clawCurrentCall == clawCalls.POWERED_CLOSED) {
            clawMotor.set(-1);
            clawRecordedTicks--;
        }
        if(clawRecordedTicks == 0 || clawRecordedTicks == 1000 || clawCurrentBreaker.tripped()) {
            clawCurrentCall = clawCalls.UNPOWERED;
        }
    }
    
    public static void closeClawA() {
        clawRecordedTicks = 1000*50;
        clawCurrentCall = clawCalls.POWERED_CLOSED;
    }
    public static void openClawA() {
        clawRecordedTicks = 0;
        clawCurrentCall = clawCalls.POWERED_OPEN;
    }


    public static void flip() {
        if (position == MIN_WRIST_POSITION) {
            position = MAX_WRIST_POSITION;
        } else {
            position = MIN_WRIST_POSITION;
        }
        wrist.set(position);
    }

    public static void setStartingPosition() {
        wrist.set(MIN_WRIST_POSITION);
        position = MIN_WRIST_POSITION;
    }

    public static void setClawPosition(ClawPresets preset) {
        switch(preset) {
            case OPEN:
                // clawDesiredPosition = CLAW_FULLY_OPEN;
                Claw2.close();
                break;
            case CLOSE:
                // clawDesiredPosition = CLAW_FULLY_CLOSED;
                Claw2.open();
                break;
            case STOP:
                clawDesiredPosition = getCurrentClawPos();
                break;
            case CONE:
                clawDesiredPosition =  CONE_POSITION;
                break;
            
        }
    }

}

