package frc.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;

public class RobotGyro {
    private static AHRS mGyro;

    public static void init() {
        mGyro = new AHRS(SPI.Port.kMXP);
    }

    public static AHRS getGyro() {
        return mGyro;
    }

    /***
     * 
     * @return gyro angle This angle can include multiple revolutions so if the
     *         robot has rotated 3 1/2 times, it will return a value of 1260. This
     *         is useful for turning but not as useful for figuring out which
     *         direction you're facing. Use getRelativeAngle for that.
     */
    public static double getAngle() {
        return mGyro.getAngle();
    }


    static class Position {
        double x;
        double y;
        double z;
        public Position(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
    public static Position position = new Position(0, 0, 0);
    private static final double CYCLE_TIME = 0.02;
    /***
     * ƒ
     * 
     * @return gyro angle 0 to 360
     */
    public static double getRelativeAngle() {
        if (getAngle() < 0) {
            return 360 + (getAngle() % 360);
        } else {
            return getAngle() % 360;
        }
    }

    public static double velocityX() {
        return mGyro.getVelocityX();
    }

    public static double velocityY() {
        return mGyro.getVelocityY();
    }

    public static double velocityZ() {
        return mGyro.getVelocityZ();
    }

    public static double pitch() {
        return mGyro.getPitch();
    }

    public static double roll() {
        return mGyro.getRoll();
    }

    public static double yaw() {
        return mGyro.getYaw();
    }
    /***
     * 
     * @param desiredPosition - desired 0 to 360 position
     * @return amount to turn to get there the quickest way
     */
    public static double getClosestTurn(double desiredPosition) {
        double distance = 0;
        double currentPosition = getRelativeAngle();

        if (currentPosition - desiredPosition >= 0)
            if (currentPosition - desiredPosition > 180)
                distance = (360 - currentPosition) + desiredPosition;
            else
                distance = desiredPosition - currentPosition;
        else if (desiredPosition - currentPosition > 180)
            distance = -((360 - desiredPosition) + currentPosition);
        else
            distance = desiredPosition - currentPosition;

        return distance;
    }

    public static void reset() {
        mGyro.reset();
    }

    public static double getGyroAngleInRad() {
        double adjustedAngle = -Math.floorMod((long) mGyro.getAngle(), 360);
        if (adjustedAngle > 180)
            adjustedAngle = -(360 - adjustedAngle);

        return adjustedAngle * (Math.PI / 180d);
    }
    

    public double pidGet() {
        return mGyro.getAngle();
    }
    public static void position() {
        position.x += mGyro.getVelocityX() * CYCLE_TIME;
        position.y += mGyro.getVelocityY() * CYCLE_TIME;
        position.x = mGyro.getVelocityZ() * CYCLE_TIME;
    }
    public static Position getPosition() {
        return position;
    }
}
