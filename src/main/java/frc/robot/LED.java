package frc.robot;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;

public class LED {
    static AddressableLED led;
    static AddressableLEDBuffer buffer;
    public static void init() {
        led = new AddressableLED(1);
        buffer =  new AddressableLEDBuffer(60);
        led.setLength(buffer.getLength());
    }

    public static void LEDstart() {
        led.start();
    }
    public static void LEDstop() {
        led.stop();
    }
}