package frc.robot;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.CvSource;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.vision.*;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.opencv.core.Scalar;
import org.opencv.core.Point;

public class VisionBall implements VisionRunner.Listener<VisionBallPipelineRed>
{
    // Varriables 
    private static UsbCamera  camera;
    private static int IMG_WIDTH = 320;
    private static int IMG_HEIGHT = 240;
    private static VisionThread visionThread;
    private static final Object imgLock = new Object();
    private static double centerX = 0.0;
    private static AtomicBoolean running = new AtomicBoolean(false);
    private static double centerY = 0.0;
    private static AtomicBoolean foundBall = new AtomicBoolean(false);
    private static int closestBallIndex = -1;
    private static int ballAmount = 0;
    private static double bestScore = 0;
    private static double currentScore = 0;
    private static AtomicBoolean working = new AtomicBoolean(false);
    //private static CvSource mProcessedStream;
    //private static Mat mProcessedFrame;

    
    // Distance Formula 
    public static double distance(double xPosition, double yPosition) {
        return Math.sqrt(Math.pow(xPosition, 2)+Math.pow(yPosition,2));
    }
    
    // Vision Processing 
    public static double algorithim(Rect re) {
        double cenX, cenY, dis, width, score, yOffset, xOffset;
        cenX = re.x + (re.width / 2);
        cenY = re.y + (re.height/2);
        width = re.width;
        xOffset = Math.abs(cenX - (IMG_WIDTH / 2));
        yOffset = Math.abs(cenY - (IMG_HEIGHT / 2));
        dis = distance(cenX, cenY);
        score = dis+ xOffset + yOffset + dis;
        return score;
    }

    public static Mat findClosestBall(ArrayList<MatOfPoint> ballsFound) {
        double topScore = 0;
        int closestIndex = 0;

        if (ballsFound.size() <= 1) {
            closestIndex = 0;
            currentScore = algorithim(Imgproc.boundingRect(ballsFound.get(0)));
            topScore = currentScore;
        } else {
            for (int i = 0; i < ballsFound.size(); i ++) {
                currentScore = algorithim(Imgproc.boundingRect(ballsFound.get(i))); // Determining Closest Ball; Change as needed/
                SmartDashboard.putNumber("Current Score", currentScore);
                if (topScore < currentScore) {
                    topScore = currentScore;
                    closestIndex = i;
                }
            }
        }
        synchronized (imgLock) {
            closestBallIndex = closestIndex;
            bestScore = topScore;
            ballAmount = ballsFound.size();
        }

        return ballsFound.get(closestBallIndex);
    }

    // Vision Set-Up and Run
    public static void init() {
        camera = CameraServer.startAutomaticCapture();
        camera.setResolution(IMG_WIDTH, IMG_HEIGHT);
        //mProcessedFrame = new Mat();
        //mProcessedStream.putFrame(mProcessedFrame);
        //mProcessedStream = CameraServer.putVideo("Processed Image", IMG_WIDTH, IMG_HEIGHT);
        if (DriverStation.getAlliance() == Alliance.Red) {
            visionThread = new VisionThread(camera, new VisionBallPipelineRed(), pipeline -> {
                if (!pipeline.filterContoursOutput().isEmpty()) {

                    Rect r = Imgproc.boundingRect(VisionBall.findClosestBall(pipeline.filterContoursOutput()));
                    //Imgproc.rectangle(mProcessedFrame, new Point(r.x, r.y), 
                    //new Point(r.x + r.width, r.y + r.height),
                    //new Scalar(0,255,0), 10);
                    //mProcessedStream.putFrame(mProcessedFrame);
                    synchronized (imgLock) {
                        centerX = r.x + (r.width / 2);
                        centerY = r.y + (r.height/2);
                        SmartDashboard.putString("Center X Components: X and Width", r.x + " " + r.width);
                        foundBall.set(true);
                        working.set(true);
                    }
                } else {
                    synchronized (imgLock) {
                        centerX = IMG_WIDTH / 2; // default to being centered
                        centerY = IMG_HEIGHT / 2;
                        ballAmount = 0;
                        foundBall.set(false);
                        bestScore = 0;
                        working.set(true);                
                    }
            }
        }
            );
        } else {
            visionThread = new VisionThread(camera, new VisionBallPipelineBlue(), pipeline -> {
                if (!pipeline.filterContoursOutput().isEmpty()) {
                    Rect r = Imgproc.boundingRect(VisionBall.findClosestBall(pipeline.filterContoursOutput()));
                    synchronized (imgLock) {
                        centerX = r.x + (r.width / 2);
                        centerY = r.y + (r.height/2);
                        working.set(true);
                    }
                    foundBall.set(true);
                } else {
                    synchronized (imgLock) {
                        centerX = IMG_WIDTH / 2; // default to being centered
                        centerY = IMG_HEIGHT / 2;
                        ballAmount = 0;
                        foundBall.set(false);
                        bestScore = 0;    
                        working.set(true);                
                    }
                }
            });
        }
    }

    public static void start() {
        visionThread.start();
    }

    public static void stop() {
        visionThread.interrupt();  // probably NOT the right call
    }

    // Return Values for Finding Balls
    public static double degreesToBall() {
        double degrees;
        double slope;
        double distance =  distanceToBall();
        slope = getBallYOffset()/getBallXOffset();
        slope = getBallXOffset()/getBallYOffset();
        slope = getBallXOffset()/distance;
        //slope = centerX/centerY;
        //degrees = Math.toDegrees(Math.atan(slope));
        degrees = Math.toDegrees(Math.asin(slope));
        return degrees;
    }

    public static double distanceToBall() {
        return distance(centerX, centerY);
    }

    // Return Values for Finding Balls
    public static int getBallNumber() {
        return ballAmount;
    }

    public static double getBallXOffset() {
        if (centerX == 0) {
            return 0;
        } else {
            return centerX - (IMG_WIDTH / 2);
        }
    }
    public static double getBallYOffset() {
        return centerY - (IMG_HEIGHT / 2);
    }

    public static boolean ballInView() {
        return foundBall.compareAndSet(true, true);
    }

    public static int getClosestBallIndex() {
        return closestBallIndex;
    }

    public static double getBallScore() {
        return bestScore;
    }

    @Override
    public void copyPipelineOutputs(VisionBallPipelineRed pipeline) {
        // TODO Auto-generated method stub
        
    }
    public static boolean working() {
        return working.compareAndSet(true, true);
    }
    public static double centerX() {
        return centerX;
    } 
    public static double centerY() {
        return centerY;
    } 
}