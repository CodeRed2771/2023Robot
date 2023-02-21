package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Servo;

public class LiveBottom {
    private static CANSparkMax LBmotor;
    private static final int MAX_LIVE_BOTTOM_CURRENT = 25;

    private static Servo LBGate;


    private static int dir = 1;

    private static long eject = 0;
    private static long shuffle = 0;
    private static int shuffleStep = 0;
    private static LBMode mode;


    public static enum LBMode {
        NONE,
        EJECT,
        SHUFFLE
    }

    public static void init() {


        //motor responsible of extension of bistable material
        LBmotor = new CANSparkMax(Wiring.LIVE_BOTTOM_MOTOR_ID, MotorType.kBrushed);
        LBmotor.restoreFactoryDefaults();

        LBmotor.setSmartCurrentLimit(MAX_LIVE_BOTTOM_CURRENT);
        LBmotor.setIdleMode(IdleMode.kBrake);

        LBGate = new Servo(Wiring.LIVE_BOTTOM_GATE_CHANNEL_ID);
        LBGate.set(0);

    }

    public static void tick(){
        long currentTimeMillis = System.currentTimeMillis();
        switch (mode) {
            case EJECT:
                if(currentTimeMillis > eject){
                    off();
                    mode = LBMode.NONE;
                }
                break;
        
            case SHUFFLE:
                if(currentTimeMillis > shuffle){
                    switch (shuffleStep) {
                        case 1:
                            backward();
                            shuffle = System.currentTimeMillis() + 1000;
                            shuffleStep = 2;
                        case 2:
                            forward();
                            shuffle = System.currentTimeMillis()+1000;
                            shuffleStep++;
                            break;
                        case 3:
                            backward();
                            shuffle = System.currentTimeMillis()+1000;
                            shuffleStep++;
                            break;
                        case 4:
                            off();
                            shuffleStep = 0;
                            mode = LBMode.NONE;
                            break;
                    }
                }
                if(shuffleStep>4){
                    shuffleStep = 0;
                    mode = LBMode.NONE;
                }

                break;
            case NONE:
                break;
        }
        
    }


    public static void openGate(){
        LBGate.set(1);
    }

    public static void closeGate(){
        LBGate.set(-1);
    }

    public static double getGatePos() {
        return LBGate.get();
    }


    public static void forward() {
        LBmotor.set(1);
        dir = 1;
    }

    public static void backward() {
        LBmotor.set(-1);
        dir = -1;
    }

    public static void off() {
        LBmotor.set(0);
    }

    public static void on() {
        LBmotor.set(dir);
    }

    public static void reverse() {
        LBmotor.set(dir*-1);
        dir *= -1; 
    }

    public static int getDirection() {
        return dir;
    }

    public static int getSpeed() {
        return (int)LBmotor.get();
    }

    public static LBMode getMode(){
        return mode;
    }

    public static void eject() {
        backward();
        eject = System.currentTimeMillis() + 1000;
        mode = LBMode.EJECT;
    }

    public static void shuffle() {
        shuffle = System.currentTimeMillis();
        mode = LBMode.SHUFFLE;
    }
}
