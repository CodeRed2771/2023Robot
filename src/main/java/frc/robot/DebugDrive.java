package frc.robot;

public class DebugDrive extends AutoBaseClass{
    int direction = 0;
    int cooldown = 0;

    public DebugDrive(int i) {
        direction = i;
    }

    public void start() {
        super.start();
    }

    public void stop() {
        super.stop();
    }



    @Override
    public void tick() {
       if(isRunning()){
        advanceStep();
        if(cooldown == 0){
        switch (direction) {
            case 0:
                DriveAuto.driveInches(12, 0, .5);
                cooldown = 50;
                break;
            case 1:
                DriveAuto.driveInches(12, -90, .5);
                cooldown = 50;
                break;
            case 2:
                DriveAuto.driveInches(12, 180, .5);
                cooldown = 50;
                break;
            case 3:
                DriveAuto.driveInches(12, 90, .5);
                cooldown = 50;
                break;
           
        }}
        else{
            cooldown -=1;
        }
       }
        
    }

}
