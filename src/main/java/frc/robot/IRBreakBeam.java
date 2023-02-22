package frc.robot;

import edu.wpi.first.wpilibj.DigitalInput;

public class IRBreakBeam {
    static DigitalInput IDBreakBeam1;
    public static enum DigitalData {
        On,
        Off
    }
    
    public static void init() {
        IDBreakBeam1 = new DigitalInput(0);
    }

    public static DigitalData IDBreakBeam1Data() {
        if (IDBreakBeam1.get()) {
            return DigitalData.On;
        } else {
            return DigitalData.Off;
        }
    }
}
