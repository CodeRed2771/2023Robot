package frc.robot;

import edu.wpi.first.wpilibj.DigitalInput;

public class LimitSwitch {
    private DigitalInput switchInput;

    /**
     * 
     * @param channel specifies the digital input channel to use for reading the switch state.
     */
    public LimitSwitch(int channel) {
        switchInput = new DigitalInput(channel);
    }

    /**
     * 
     * @return returns true if switch is pressed
     */
    public boolean isPressed() {
        return !switchInput.get(); // Returns true if the switch is pressed (input is low)
    }
}
