/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

public class Shooter {

    private static TalonFX shooterMotor = new TalonFX(Wiring.SHOOTER_MOTOR_ID);
    private static TalonFX feederMotor = new TalonFX(Wiring.FEEDER_MOTOR_ID);

    private static boolean isInitialized = false;
    private static boolean isEnabled = false;
    private static boolean isGateOpen = false;
    private static boolean oneShot = false;
    private static boolean continuousShooting = false;
    private static double shooterMotorVelocityTarget = 0;
    private static double feederMotorVelocityTarget = 0;
    private static int timer = 0;
    private static final int kPIDLoopIdx = 0;
    private static double targetSpeed = Calibration.SHOOTER_DEFAULT_SPEED;
    private static double adjustmentFactor = 1;
    private static DoubleSolenoid ballLiftSolenoid;
    private static DoubleSolenoid shooterPositionSolenoid_Stage1;
    private static DoubleSolenoid shooterPositionSolenoid_Stage2;
    private static boolean reverse = false;
    private static boolean alignOnly = true;
    private static AutoBaseClass mAlignProgram;
    private static boolean shooterCompleted;
    public static enum ShooterPosition {
        Low,
        Medium,
        Backwards
    }
    private static ShooterPosition curShooterPosition;
    public static enum ManualShotPreset {
        SuperCloseHighShot,
        BackOfTarmac,
        TarmacLine,
        Backwards,
        LowGoal, SafeZone,
        HumanPlayerStation,
    }
    public static ManualShotPreset curManualShotPreset;
    private static boolean manualVisionOverride = false;
    private static boolean calibrationMode;

    private static class shooterArrayValue {
        ShooterPosition position;
        double speed;
        public shooterArrayValue(ShooterPosition position, double speed) {
            this.position = position;
            this.speed = speed;
        }
    }
    private static shooterArrayValue[] shooterArray =  {
        new shooterArrayValue(ShooterPosition.Low, 6200), // 0
        new shooterArrayValue(ShooterPosition.Low, 6200),  // 1
        new shooterArrayValue(ShooterPosition.Low, 6200), // 2
        new shooterArrayValue(ShooterPosition.Low, 6200), // 3
        new shooterArrayValue(ShooterPosition.Low, 4850), // 4
        new shooterArrayValue(ShooterPosition.Low, 5300), // 5.
        new shooterArrayValue(ShooterPosition.Low, 5750), // 6
        new shooterArrayValue(ShooterPosition.Low, 6200), // 7
        new shooterArrayValue(ShooterPosition.Low, 6800), // 8
        new shooterArrayValue(ShooterPosition.Low, 7400), // 9
        new shooterArrayValue(ShooterPosition.Low, 8000),// 10 8000
        new shooterArrayValue(ShooterPosition.Low, 8600), // 11
        new shooterArrayValue(ShooterPosition.Low, 8450), // 12
        new shooterArrayValue(ShooterPosition.Low, 8900), // 13
        new shooterArrayValue(ShooterPosition.Low, 9350), // 14
        new shooterArrayValue(ShooterPosition.Low, 9800), //15
        new shooterArrayValue(ShooterPosition.Low, 10250), // 16
    };


    public static void init() {
        shooterMotor.configFactoryDefault(10);
        shooterMotor.setInverted(true);
        shooterMotor.setSensorPhase(false);
       
		/* set the relevant frame periods to be at least as fast as periodic rate */
		// shooterMotor.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 10, 0);
        // shooterMotor.setStatusFramePeriod(StatusFrameEnhanced., 10, 0);

		/* set the peak and nominal outputs */
		shooterMotor.configNominalOutputForward(0, 0);
		shooterMotor.configNominalOutputReverse(0, 0);
		shooterMotor.configPeakOutputForward(1, 0);
		shooterMotor.configPeakOutputReverse(.2, 0); // put in .2 instead of 0 on 3/28 to keep from over spinning
		shooterMotor.setNeutralMode(NeutralMode.Coast);
        shooterMotor.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, kPIDLoopIdx, 0);

		shooterMotor.configClosedloopRamp(.45, 0);  // .45 seconds from 0 to full
		/* set closed loop gains in slot0 - see documentation */
        shooterMotor.selectProfileSlot(0, 0);


		shooterMotor.config_kP(kPIDLoopIdx, Calibration.SHOOTER_P, 0);
		shooterMotor.config_kI(kPIDLoopIdx, Calibration.SHOOTER_I, 0);
		shooterMotor.config_kD(kPIDLoopIdx, Calibration.SHOOTER_D, 0);
        shooterMotor.config_kF(kPIDLoopIdx, Calibration.SHOOTER_F, 0);
		/* zero the sensor */
        shooterMotor.setSelectedSensorPosition(0, kPIDLoopIdx, 0);
 	

        feederMotor.configFactoryDefault(10);
        feederMotor.setInverted(false);
        feederMotor.setSensorPhase(true);
       
		/* set the relevant frame periods to be at least as fast as periodic rate */
		// shooterMotor.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 10, 0);
        // shooterMotor.setStatusFramePeriod(StatusFrameEnhanced., 10, 0);

		/* set the peak and nominal outputs */
		feederMotor.configNominalOutputForward(0, 0);
		feederMotor.configNominalOutputReverse(0, 0);
		feederMotor.configPeakOutputForward(1, 0);
		feederMotor.configPeakOutputReverse(.2, 0);
		feederMotor.setNeutralMode(NeutralMode.Coast);
        feederMotor.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, kPIDLoopIdx, 0);

		feederMotor.configClosedloopRamp(.45, 0);
		/* set closed loop gains in slot0 - see documentation */
        feederMotor.selectProfileSlot(0, 0);


		feederMotor.config_kP(kPIDLoopIdx, Calibration.SHOOTER_P, 0);
		feederMotor.config_kI(kPIDLoopIdx, Calibration.SHOOTER_I, 0);
		feederMotor.config_kD(kPIDLoopIdx, Calibration.SHOOTER_D, 0);
        feederMotor.config_kF(kPIDLoopIdx, Calibration.SHOOTER_F, 0);
		/* zero the sensor */
        feederMotor.setSelectedSensorPosition(0, kPIDLoopIdx, 0);
        	
		SmartDashboard.putNumber("Shoot P", Calibration.SHOOTER_P);
		SmartDashboard.putNumber("Shoot D", Calibration.SHOOTER_D);
        SmartDashboard.putNumber("Shoot F", Calibration.SHOOTER_F);
        SmartDashboard.putNumber("Shoot Setpoint", Calibration.SHOOTER_DEFAULT_SPEED);
        SmartDashboard.putNumber("Feeder Setpoint", Calibration.FEEDER_DEFAULT_SPEED);
        SmartDashboard.putBoolean("Shooter TUNE", false);

        isInitialized = true;

        ballLiftSolenoid = new DoubleSolenoid(PneumaticsModuleType.REVPH, Wiring.BALLLIFT_FOWARD, Wiring.BALLLIFT_REVERSE);
        shooterPositionSolenoid_Stage1 = new DoubleSolenoid(PneumaticsModuleType.REVPH, Wiring.SHOOTERPOSITION_STAGE1_FOWARD, Wiring.SHOOTERPOSITION_STAGE1_REVERSE);
        shooterPositionSolenoid_Stage2 = new DoubleSolenoid(PneumaticsModuleType.REVPH, Wiring.SHOOTERPOSITION_STAGE2_FOWARD, Wiring.SHOOTERPOSITION_STAGE2_REVERSE);
        
        setBallLiftDown();

        mAlignProgram = new AutoDoNothing();
        
    }

    public static void tick() {
        if (mAlignProgram.isRunning()) {
            mAlignProgram.tick();
        }

        if (!isInitialized) 
            return;

            if (isEnabled) {
                if (calibrationMode) {
                  	shooterMotor.config_kF(kPIDLoopIdx, SmartDashboard.getNumber("Shoot F", 0), 0);
                    shooterMotor.config_kP(kPIDLoopIdx, SmartDashboard.getNumber("Shoot P", 0), 0);
                    shooterMotor.config_kI(kPIDLoopIdx, SmartDashboard.getNumber("Shoot I", 0), 0);
                    shooterMotor.config_kD(kPIDLoopIdx, SmartDashboard.getNumber("Shoot D", 0), 0);

                    feederMotor.config_kF(kPIDLoopIdx, SmartDashboard.getNumber("Shoot F", 0), 0);
                    feederMotor.config_kP(kPIDLoopIdx, SmartDashboard.getNumber("Shoot P", 0), 0);
                    feederMotor.config_kI(kPIDLoopIdx, SmartDashboard.getNumber("Shoot I", 0), 0);
                    feederMotor.config_kD(kPIDLoopIdx, SmartDashboard.getNumber("Shoot D", 0), 0);

                    int dist = (int)Math.round(VisionShooter.getDistanceFromTarget());
                    dist = dist/12;
                    SmartDashboard.putNumber("Distance to Target", dist);
                    // shooterVelocityTarget = SmartDashboard.getNumber("Shoot Setpoint", Calibration.SHOOTER_DEFAULT_SPEED);
                    
               }
               if (reverse) {
                    shooterMotor.configPeakOutputReverse(-1, 0);
                    feederMotor.configPeakOutputReverse(-1, 0);
                    shooterMotor.set(ControlMode.PercentOutput, -0.2);
                    feederMotor.set(ControlMode.PercentOutput, -0.2);
               } else {
                    shooterMotor.configPeakOutputReverse(0, 0);
                    feederMotor.configPeakOutputReverse(0, 0);
                    feederMotorVelocityTarget = shooterMotorVelocityTarget;
                    shooterMotor.set(ControlMode.Velocity, shooterMotorVelocityTarget);
                    feederMotor.set(ControlMode.Velocity, shooterMotorVelocityTarget);
               }

               calibrationMode = SmartDashboard.getBoolean("Shooter TUNE", false);
              

                SmartDashboard.putNumber("SHOOTER VELOCITY", shooterMotor.getSelectedSensorVelocity());
                SmartDashboard.putNumber("SHOOTER ERROR", shooterMotorVelocityTarget - shooterMotor.getSelectedSensorVelocity());
                
                // System.out.println(timer);

                if (oneShot) {
                        timer += 1; // ONE TIMER UNIT EQUALS ABOUT 20 MILLISECONDS
                        if (timer < 5) {
                            setBallLiftUp();
                        }
                        if (timer == 25) {
                            setBallLiftDown();
                            setShooterPosition(ShooterPosition.Low);
                            oneShot = false;
                            resetTimer();
                            // manualVisionOverride = false;
                            AutoAlign.setAllignment(false);
                            shooterCompleted = true;
                        }
               }
 
                if (continuousShooting) {
                    timer += 1; // ONE TIMER UNIT EQUALS ABOUT 20 MILLISECONDS
                    setBallLiftUp();
                    if (timer == 25) {
                        setBallLiftDown();
                    }
                    if (timer == 50) {
                        Intake.retractIntake();
                    }
                    if (timer == 100) {
                        setBallLiftUp();
                    }
                    if (timer == 125) {
                        setBallLiftDown();
                        StopShooter();
                        resetTimer();
                    }
                }
                
            }
        
        SmartDashboard.putBoolean("Is At Speed", isAtSpeed());
    }

    public static void StartShooter() {
        isEnabled = true;
        oneShot = false;
        continuousShooting = false;
    }

    public static boolean isShooterEnabled () {
        return isEnabled;
    }

    public static void StopShooter() {
        isEnabled = false;
        
        if (!isInitialized) return;

        oneShot = false;
        continuousShooting = false;
        resetTimer();
        shooterMotor.set(ControlMode.Velocity,0);
        feederMotor.set(ControlMode.Velocity, 0);
    }

    public static void setAdjustmentFactor (double adjustmentFactor) {
        adjustmentFactor = (((adjustmentFactor + 1) / 2) + 2) * 0.4;
        Shooter.adjustmentFactor = adjustmentFactor;
        System.out.println(Shooter.adjustmentFactor);
    } 

    public static double getShooterSpeed() {
        if (!isInitialized) 
            return 0;
        else
            return shooterMotor.getSelectedSensorVelocity();
    }

    public static boolean isAtSpeed() {
        if (!isInitialized) 
            return true;
        else
            return (getShooterSpeed() > 0 && Math.abs(getShooterSpeed() - targetSpeed) < 300);
    }
    
    public static void oneShotAuto(){
        isEnabled = true;
        oneShot = true;
        continuousShooting = false;
        shooterCompleted = false;
    }

    public static void alignAndShoot (boolean pAlignOnly) {
        alignOnly = pAlignOnly;
        // isEnabled = true;
        // oneShot = true;
        shooterCompleted = false;
        continuousShooting = false;
        if (calibrationMode) {
            manualVisionOverride = true;
        }
        if (!manualVisionOverride || alignOnly) {
            mAlignProgram = new AutoAlign();
            mAlignProgram.start(true);
        }
    }

    public static void continuousShooting () {
        isEnabled = true;
        continuousShooting = true;
        oneShot = false;
    }

    public static boolean isGateOpen () {
        return isGateOpen;
    }

    public static void resetTimer () {
        timer = 0;
    }

    public static void stopTimer () {
        
    }
    public static boolean getManualOveride() {
        return manualVisionOverride;
    }

    public static void setShooterPosition(ShooterPosition position) {
        switch (position) {
            case Low:
                shooterPositionSolenoid_Stage1.set(DoubleSolenoid.Value.kReverse);
                shooterPositionSolenoid_Stage2.set(DoubleSolenoid.Value.kForward);
                // shooterPositionSolenoid_Stage1.set(DoubleSolenoid.Value.kReverse);
                // shooterPositionSolenoid_Stage2.set(DoubleSolenoid.Value.kReverse);
                break;
            case Medium:
                shooterPositionSolenoid_Stage1.set(DoubleSolenoid.Value.kReverse);
                shooterPositionSolenoid_Stage2.set(DoubleSolenoid.Value.kReverse);
                // shooterPositionSolenoid_Stage1.set(DoubleSolenoid.Value.kReverse);
                // shooterPositionSolenoid_Stage2.set(DoubleSolenoid.Value.kForward);
                break;
            case  Backwards:
                shooterPositionSolenoid_Stage1.set(DoubleSolenoid.Value.kForward);
                shooterPositionSolenoid_Stage2.set(DoubleSolenoid.Value.kForward);
                break;
        }
        curShooterPosition = position;
    }
    
    public static void setBallLiftUp() {
        ballLiftSolenoid.set(DoubleSolenoid.Value.kForward);
    }

    public static void setBallLiftDown() {
        ballLiftSolenoid.set(DoubleSolenoid.Value.kReverse);
    }

    public static ShooterPosition getShooterPosition() {
        return curShooterPosition;
    }

    public static double getShooterVelocity() {
        double power = 0;
        switch (getShooterPosition()) {
            case Low:
                power = 20;
                break;
            case Medium:
                power = 30;
                break;
            case Backwards:
                power = 40;
                break;
        }
        return power; 
    }

    public static void setAutoDistancingMode() {
        manualVisionOverride = false;
    }

    public static void setAlignOnly(boolean pAlignOnly) {
        alignOnly = pAlignOnly;
    }

    public static boolean isAlignOnly() {
        return alignOnly;
    }

    public static void setManualPresets(ManualShotPreset position) {
        manualVisionOverride = true;
        switch(position) {
            case SuperCloseHighShot:
                setShooterPosition(ShooterPosition.Medium);
                shooterMotorVelocityTarget = 6200;
                break;
            case BackOfTarmac:
                setShooterPosition(ShooterPosition.Low);
                shooterMotorVelocityTarget = 6200;
                break;
            case TarmacLine:
                setShooterPosition(ShooterPosition.Low);
                shooterMotorVelocityTarget = 6200;
                break;
            case Backwards:
                setShooterPosition(ShooterPosition.Backwards);
                shooterMotorVelocityTarget = 4800;
                break;
            case LowGoal:
                setShooterPosition(ShooterPosition.Low);
                shooterMotorVelocityTarget = 3800;
                break;
            case SafeZone:
                setShooterPosition(ShooterPosition.Low);
                shooterMotorVelocityTarget = 8000;
                break;
            case HumanPlayerStation:
                setShooterPosition(ShooterPosition.Low); // changed 4/25/22 for super high shot - was Low
                shooterMotorVelocityTarget = 20000;  // was 9000
                break;  
        }
        
        SmartDashboard.putNumber("Shoot Setpoint",shooterMotorVelocityTarget);
        
        isEnabled = true;
        oneShot = false;
        continuousShooting = false;
        
        manualVisionOverride = true;
    }

    public static void reverseShooter(){
        reverse = true;
    }
    public static void endReverseShooter() {
        reverse = false;
    }
    public static void setSpeed(double speed) {
        shooterMotorVelocityTarget= speed;
    }
    public static void setupShooterAuto() {
        int dis = (int)Math.round(VisionShooter.getDistanceFromTarget());
        dis = dis/12;
        if (dis < shooterArray.length) {
            setShooterPosition(shooterArray[dis].position);
            shooterMotorVelocityTarget = shooterArray[dis].speed;
        }
        SmartDashboard.putNumber("Distance Feet ", dis);
    } 

    public static boolean shooterCompleted() {
        return shooterCompleted;
    }
}