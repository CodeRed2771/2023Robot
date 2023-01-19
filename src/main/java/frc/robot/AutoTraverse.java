package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Climber.ClimberPosition;
import frc.robot.Climber.Rung;
import frc.robot.Shooter.ShooterPosition;

public class AutoTraverse extends AutoBaseClass {
	public AutoTraverse() {
		super();

	}
    private boolean completedTwice = false;
	public void start() {
		super.start();
	}

	public void tick() {
        if (isRunning()) {
            // DriveAuto.tick(); this is also in RobotPeriodic
            SmartDashboard.putNumber("Auto Step", getCurrentStep());
            switch (getCurrentStep()) {
                case 0:
                    Climber.climbTo(Rung.UpLittle);
                    setTimerAndAdvanceStep(4000);
                    break;
                case 1:
                    break;
                case 2:
                    Climber.climbTo(Rung.Retract);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 3:
                    break;
                case 4:
                    Climber.climbTo(Rung.UpLittle);
                    Shooter.setShooterPosition(ShooterPosition.Medium);
                    setTimerAndAdvanceStep(1500);
                    break;
                case 5:
                    break;
                case 6:
                    Climber.climberPosition(ClimberPosition.Back);
                    setTimerAndAdvanceStep(750);
                    break;
                case 7:
                    break;
                case 8:
                    Climber.climbTo(Rung.ExtendToNextRung);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 9:
                    break;
                case 10:
                    Climber.climberPosition(ClimberPosition.Straight);
                    Shooter.setShooterPosition(ShooterPosition.Backwards);
                    setTimerAndAdvanceStep(1000);
                    break;
                case 11:
                    break;
                case 12: 
                    if (completedTwice) {
                        advanceStep();
                    } else {
                        setStep(0);
                        completedTwice = true;
                    }
                    break;
                case 13:
                    Climber.climbTo(Rung.UpLittle);
                    setTimerAndAdvanceStep(4000);
                    break;
                case 14: 
                    break;
                case 15:
                    Climber.climbTo(Rung.Retract);
                    setTimerAndAdvanceStep(2000);
                    break;
                case 16:
                    break;
                case 17:
                    //limber.climbTo(Rung.UpLittle);
                    Shooter.setShooterPosition(ShooterPosition.Medium);
                    stop();
                    break;
            }
        } 
	}
}
