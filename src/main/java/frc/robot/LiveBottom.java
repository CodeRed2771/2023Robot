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
/**
     * to be called once at the start to setup everything needed to make this work
     */
    public static void init() {


        //motor responsible of extension of bistable material
        LBmotor = new CANSparkMax(Wiring.LIVE_BOTTOM_MOTOR_ID, MotorType.kBrushed);
        LBmotor.restoreFactoryDefaults();

        LBmotor.setSmartCurrentLimit(MAX_LIVE_BOTTOM_CURRENT);
        LBmotor.setIdleMode(IdleMode.kBrake);

        LBGate = new Servo(Wiring.LIVE_BOTTOM_GATE_CHANNEL_ID);
        LBGate.set(0);

        mode = LBMode.NONE;

    }
/**
     * to be called every tick to make shuffle and eject methods work
     */
    public static void tick(){
        long currentTimeMillis = System.currentTimeMillis();
        switch (mode) {
            case EJECT:
                if(currentTimeMillis > eject){
                    off();
                    closeGate();
                    mode = LBMode.NONE;
                }
                break;
        
            case SHUFFLE:
                if(currentTimeMillis > shuffle){
                    switch (shuffleStep) {
                        case 1:
                            backward();
                            shuffle = System.currentTimeMillis() + 1500;
                            shuffleStep = 2;
                            break;
                        case 2:
                            forward();
                            shuffle = System.currentTimeMillis()+1500;
                            shuffleStep++;
                            break;
                        case 3:
                            backward();
                            shuffle = System.currentTimeMillis()+1500;
                            shuffleStep++;
                            break;
                        case 4:
                            shuffleStep = 0;
                            mode = LBMode.NONE;
                            off();
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

/**
     * open the gate
     */
    public static void openGate(){
        LBGate.set(1);
    }

    /**
     * close the gate
     */
    public static void closeGate(){
        LBGate.set(-1);
    }
    /**
     * get the pos of the gate server
     * 
     */
    public static double getGatePos() {
        return LBGate.get();
    }

    /**
     * turns motor(s) on and makes them go forward
     * forward is toward the front of the robot
     */
    public static void forward() {
        LBmotor.set(.5);
        dir = 1;
    }

    /**
     * turns motor(s) on and makes them go backward
     * backwarad is toward the back of the robot
     */
    public static void backward() {
        LBmotor.set(-.5);
        dir = -1;
    }

    /**
     * turns motor(s) off
     */
    public static void off() {
        if (mode == LBMode.NONE) // only stop it if it's not running a routine
            LBmotor.set(0);
    }

    /**
     * turns motor(s) on
     * motor(s) go the last direction they were going before they were off
     * default is forward
     */
    public static void on() {
        LBmotor.set(dir);
    }

    /**
     * turns motor(s) on and makes them go 
     * the opposite of the direction they were going
     */
    public static void reverse() {
        LBmotor.set(dir*-1);
        dir *= -1; 
    }

    /**
     * get the stored Direction for the motor
     */
    public static int getDirection() {
        return dir;
    }

    /**
     * get the current motor speed<p>
     * -1 is backward<p>
     * 1 is forward<p>
     * 0 is stopped<p>
     * if you want to know the stored direction for when the motor will when turned on
     * then use the getDirection() method
     */
    public static int getSpeed() {
        return (int)LBmotor.get();
    }

    /**
     * returns the mode the motors are in, it's best to not do any manual controls/methods when the robot is doing a preprogrammed thingy<p>
     * 
     * NONE = normal setting, the motor isn't doing any preprogrammed stuff<p>
     * EJECT = motor is doing the program for ejecting a game piece out of the robot<p>
     * SHUFFLE = motor is doing the program to shuffle a game piece around inside the robot so the arm can pick it up
     */
    public static LBMode getMode(){
        return mode;
    }

    /**
     * starts the eject program to eject a game piece from the robot<p>
     * moves the belt around a bit and will open and close the gate
     */
    
    public static void eject() {
        backward();
        openGate();
        eject = System.currentTimeMillis() + 1000;
        mode = LBMode.EJECT;
    }
    /**
     * starts the shuffle program to shuffle a game piece in the robot<p>
     * this moves the belt around a bit and closes the gate
     */
    public static void shuffle() {
        closeGate();
        shuffle = System.currentTimeMillis();
        mode = LBMode.SHUFFLE;
        shuffleStep = 1;
    }
}
