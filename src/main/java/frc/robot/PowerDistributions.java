package frc.robot;

import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;

public class PowerDistributions {
    PowerDistribution distributionTest = new PowerDistribution(0, ModuleType.kRev);
    
    double voltage = distributionTest.getVoltage();
    
}
