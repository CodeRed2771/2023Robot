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
import edu.wpi.first.cscore.CvSink;


public class VisionElements implements VisionRunner.Listener<ElementPipeline>
{
    // Varriables 
    private static UsbCamera  camera;
    private static CvSource mProcessedStream;
    private static Mat mProcessedFrame;
    private static CvSink mCameraFrameGrabber;
    private static Mat mUnprocessedFrame;
    private static int IMG_WIDTH = 320;
    private static int IMG_HEIGHT = 240;
    private static VisionThread visionThread;
    private static final Object imgLock = new Object();
    private static double centerX = 0.0;
    private static AtomicBoolean running = new AtomicBoolean(false);
    private static double centerY = 0.0;
    private static AtomicBoolean foundCone = new AtomicBoolean(false);
    private static AtomicBoolean foundCube = new AtomicBoolean(false);
    private static int closestConeIndex = -1;
    private static int closestCubeIndex = -1;
    private static int coneAmount = 0;
    private static int cubeAmount = 0;
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

    private static void GrabFrameFromServer()
    {
        // Grabs Frame From Camera
        int attemptCount = 0;
        while (mCameraFrameGrabber.grabFrame(mUnprocessedFrame) == 0 && attemptCount < 10)
            attemptCount++;
        //Display raw image
        // mRawImageStream.putFrame(mUnprocessedFrame);
        //Save copy of raw image so that we can bound balls on it later
        Imgproc.cvtColor(mUnprocessedFrame, mProcessedFrame, Imgproc.COLOR_BGR2RGB);
    }


    public static Mat findClosestCone(ArrayList<MatOfPoint> conesFound) {
        double topScore = 0;
        int closestIndex = 0;

        if (conesFound.size() <= 1) {
            closestIndex = 0;
            currentScore = algorithim(Imgproc.boundingRect(conesFound.get(0)));
            topScore = currentScore;
        } else {
            for (int i = 0; i < conesFound.size(); i ++) {
                currentScore = algorithim(Imgproc.boundingRect(conesFound.get(i))); // Determining Closest Element; Change as needed/
                SmartDashboard.putNumber("Current Score", currentScore);
                if (topScore < currentScore) {
                    topScore = currentScore;
                    closestIndex = i;
                }
            }
        }
        synchronized (imgLock) {
            closestConeIndex = closestIndex;
            bestScore = topScore;
            coneAmount = conesFound.size();
        }

        return conesFound.get(closestConeIndex);
    }
    public static Mat findClosestCube(ArrayList<MatOfPoint> cubesFound) {
        double topScore = 0;
        int closestIndex = 0;

        if (cubesFound.size() <= 1) {
            closestIndex = 0;
            currentScore = algorithim(Imgproc.boundingRect(cubesFound.get(0)));
            topScore = currentScore;
        } else {
            for (int i = 0; i < cubesFound.size(); i ++) {
                currentScore = algorithim(Imgproc.boundingRect(cubesFound.get(i))); // Determining Closest Element; Change as needed/
                SmartDashboard.putNumber("Current Score", currentScore);
                if (topScore < currentScore) {
                    topScore = currentScore;
                    closestIndex = i;
                }
            }
        }
        synchronized (imgLock) {
            closestCubeIndex = closestIndex;
            bestScore = topScore;
            cubeAmount = cubesFound.size();
        }

        return cubesFound.get(closestCubeIndex);
    }


    // Vision Set-Up and Run
    public static void init() {
        camera = CameraServer.startAutomaticCapture();
        camera.setResolution(IMG_WIDTH, IMG_HEIGHT);
        mProcessedFrame = new Mat();
        mProcessedStream.putFrame(mProcessedFrame);
        GrabFrameFromServer();
        mProcessedStream = CameraServer.putVideo("Processed Image", IMG_WIDTH, IMG_HEIGHT);
        visionThread = new VisionThread(camera, new ElementPipeline(), pipeline -> {
            if (pipeline.getElement() == ElementPipeline.Element.CONE) {
                Rect r1 = Imgproc.boundingRect(VisionElements.findClosestCone(pipeline.coneFilterContoursOutput()));                    //Imgproc.rectangle(mProcessedFrame, new Point(r.x, r.y), 
                //new Point(r.x + r.width, r.y + r.height),
                //new Scalar(0,255,0), 10);
                //mProcessedStream.putFrame(mProcessedFrame);
                synchronized (imgLock) {
                    centerX = r1.x + (r1.width / 2);
                    centerY = r1.y + (r1.height/2);
                    SmartDashboard.putString("Center X Components: X and Width (Cone)", r1.x + " " + r1.width);
                    foundCone.set(true);
                    working.set(true);
                }
            }
            else if(pipeline.getElement() == ElementPipeline.Element.CUBE) {
                Rect r2 = Imgproc.boundingRect(VisionElements.findClosestCube(pipeline.cubeFilterContoursOutput()));                    //Imgproc.rectangle(mProcessedFrame, new Point(r.x, r.y), 
                synchronized (imgLock) {
                    centerX = r2.x + (r2.width / 2);
                    centerY = r2.y + (r2.height/2);
                    SmartDashboard.putString("Center X Components: X and Width (Cube)", r2.x + " " + r2.width);
                    foundCube.set(true);
                    working.set(true);
                }
            }
            else {//may need to not clear unless not seen for a second
                synchronized (imgLock) {
                    centerX = IMG_WIDTH / 2; // default to being centered
                    centerY = IMG_HEIGHT / 2;
                    coneAmount = 0;
                    cubeAmount = 0;
                    foundCube.set(false);
                    foundCone.set(false);
                    bestScore = 0;
                    working.set(true);                
                }
            }
        }
            );
        }

    public static void start() {
        visionThread.start();
    }

    public static void stop() {
        visionThread.interrupt();  // probably NOT the right call
    }

    // Return Values for Finding elements
    public static double degreesToElement() {
        double degrees;
        double slope;
        double distance =  distanceToElement();
        slope = getElementYOffset()/getElementXOffset();
        slope = getElementXOffset()/getElementYOffset();
        slope = getElementXOffset()/distance;
        //slope = centerX/centerY;
        //degrees = Math.toDegrees(Math.atan(slope));
        degrees = Math.toDegrees(Math.asin(slope));
        return degrees;
    }

    public static double distanceToElement() {
        return distance(centerX, centerY);
    }

    // Return Values for Finding Elements
    public static int getConeNumber() {
        return coneAmount;
    }
    public static int getCubeNumber() {
        return cubeAmount;
    }

    public static double getElementXOffset() {
        if (centerX == 0) {
            return 0;
        } else {
            return centerX - (IMG_WIDTH / 2);
        }
    }
    public static double getElementYOffset() {
        return centerY - (IMG_HEIGHT / 2);
    }

    public static boolean coneInView() {
        return foundCone.compareAndSet(true, true);
    }
    public static boolean cubeInView() {
        return foundCube.compareAndSet(true, true);
    }

    public static int getClosestConeIndex() {
        return closestConeIndex;

    }
    public static int getClosestCubeIndex() {
        return closestCubeIndex;
    }

    public static double getElementScore() {
        return bestScore;
    }

    @Override
    public void copyPipelineOutputs(ElementPipeline pipeline) {
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