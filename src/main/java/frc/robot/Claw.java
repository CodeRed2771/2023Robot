package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Servo;
import frc.robot.libs.CurrentBreaker;


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

    private static final double MAX_CLAW_OPEN = .05;
    private static final double MIN_CLAW_CLOSE = .001;

    private static final double CONE_PICKUP = .035;
    private static final double CUBE_PICKUP = .02;
    private static boolean closing;
    private static clawPositions clawDesiredPosition;

    private static enum clawCalls {
        POWERED_OPEN,
        POWERED_CLOSED,
        UNPOWERED
    }

    public static enum clawPositions{
        ConePickUp,
        CubePickUp,
    }

    private static clawCalls clawCurrentCall = clawCalls.UNPOWERED;



    public static void init() {
        analogInput = new AnalogInput(Wiring.POTENTIOMETER_CHANNEL);
        analogInput.setAverageBits(2);
        potentiometer = new AnalogPotentiometer(analogInput, 180, 0);
        
        clawMotor = new CANSparkMax(Wiring.CLAW_MOTOR_ID, MotorType.kBrushed);

        wrist = new Servo(Wiring.CLAW_CHANNEL_ID);
        wrist.set(MIN_WRIST_POSITION);
        position = MIN_WRIST_POSITION;

    }

    public static void tick() {
        if(!closing){
            if(clawDesiredPosition == clawPositions.ConePickUp) {
                if(Math.abs(getPotentionmeterDegree() - CONE_PICKUP)>.01) {
                    openClawTO();
                }
            } else {
                if(Math.abs(getPotentionmeterDegree() - CUBE_PICKUP)>.01) {
                    openClawTO();
                }
            }
        } else {
            if(clawDesiredPosition == clawPositions.ConePickUp) {
                if(Math.abs(getPotentionmeterDegree() - CONE_PICKUP)>.01) {
                    closeClawTO();
                }
            } else {
                if(Math.abs(getPotentionmeterDegree() - CUBE_PICKUP)>.01) {
                    closeClawTO();
                }
            }
        }
    }

    public static double getPotentionmeterDegree() {
        return potentiometer.get();
    }

    // public static void openClaw() {
        
    // }
    
    public static void openClawTO() {
        if(getPotentionmeterDegree() < MAX_CLAW_OPEN) {
            clawMotor.set(1);
        }
    }
    
    public static void closeClawTO() {
        if(getPotentionmeterDegree() > MIN_CLAW_CLOSE) {
            clawMotor.set(-1);
        }
    }

    public static void stopClawTO() {
        clawMotor.set(0);
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

    public static void setClawPosition(clawPositions position) {
        switch(position) {
            case ConePickUp:
                clawDesiredPosition = clawPositions.ConePickUp;
                if(getPotentionmeterDegree() > CONE_PICKUP) {
                    closing = true;
                } else {
                    closing = false;
                }
                break;
            case CubePickUp:
                clawDesiredPosition = clawPositions.CubePickUp;
                if(getPotentionmeterDegree() > CUBE_PICKUP) {
                    closing = true;
                } else {
                    closing = false;
                }
                break;
        }
    }

}

