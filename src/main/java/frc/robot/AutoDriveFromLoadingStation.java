package frc.robot;

import javax.lang.model.util.ElementScanner6;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class AutoDriveFromLoadingStation extends AutoBaseClass {
    public AutoDriveFromLoadingStation() {
		super();

	}

	public void start() {
		super.start();
	}

	public void tick() {
        // if (isRunning()) {
        //     DriveAuto.tick();
        //     SmartDashboard.putNumber("Auto Step", getCurrentStep());
        //     switch (getCurrentStep()) {
        //         case 0:
        //             driveInches(12, 0, 1, false, true); 
        //             setTimerAndAdvanceStep(2000);
        //             break;
        //         case 1:
        //             if (driveCompleted()){
        //                 advanceStep();
        //             }
        //             break;
        //         case 2:
        //             turnDegrees(180, 1);
        //             setTimerAndAdvanceStep(3000);
        //             break;
        //         case 3:
        //             if (driveCompleted()) {
        //                 advanceStep();
        //             }
        //             break;
        //         case 4:
        //             driveInches(160, 35, 1); 
        //             setTimerAndAdvanceStep(3000);
        //             break;
        //         case 5:
        //             if (driveCompleted()){
        //                 advanceStep();
        //             }
        //             break;
        //         case 6:
        //             advanceStep();
        //             break;
        //         case 7:
        //             if (DistanceSensor.getRange() > 12){
        //                 driveInches(DistanceSensor.getRange() - 6, 90, 1, false, true);
        //                 setTimerAndAdvanceStep(2000);  
        //             } else {
        //                 setStep(getCurrentStep() + 2);
        //                  } 
        //             break;
        //         case 8: 
        //             if (driveCompleted()){
        //                 advanceStep();
        //             }
        //             break;
        //         case 9:
        //             driveInches(120, 0, 1, false, true);
        //             setTimerAndAdvanceStep(5000);
        //             break;
        //         case 10:
        //             if (driveCompleted()){
        //                 advanceStep();
        //             }
        //         case 11:
        //             stop();
        //             break;
                
            // }
        // }
	} //is there ever going to be a time when we use this or can we delete it? ~AR
}