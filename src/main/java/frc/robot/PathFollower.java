/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

public class PathFollower {

    private float xValStop;
    private float differenceOfX;
    private Equation equation = new Equation();
    private float point = 0;
    public boolean isMovingForward = true;

    public void setIsMovingForward(boolean isMovingForward) {
        this.isMovingForward = isMovingForward;
    }

    public void setPath(Equation equation, float xValStop, float differenceOfX) {
        point = 0;
        this.equation = equation;
        this.xValStop = xValStop;
        this.differenceOfX = differenceOfX;
    }

    public DriveData getNextLine(DriveData data) {

        float currentXPoint = 0;
        float incrementationOfPoint = 0;
        float nextPoint = 0;
        float y2 = 0;
        float y1 = 0;
        float side1 = 0;
        float side2 = 0;
        float angleInDegrees = 0;
        float distanceToNextPoint = 0;

        incrementationOfPoint = point + 1;
        currentXPoint = point * differenceOfX;
        nextPoint = incrementationOfPoint * differenceOfX;

        if (nextPoint > xValStop) {
            nextPoint = xValStop;
        }

        if (currentXPoint >= xValStop) {
            currentXPoint = xValStop;
            data.setAngle(0);
            data.setDistance(0);
        } else {
            y1 = equation.evaluateExpression(currentXPoint);
            y2 = equation.evaluateExpression(nextPoint);

            side1 = y2 - y1;
            side2 = nextPoint - currentXPoint;

            angleInDegrees = equation.evaluateAngle(side1, side2);
            distanceToNextPoint = equation.evaluateDistance(side1, side2);

            if (isMovingForward == false) {
                angleInDegrees = angleInDegrees + 180;
                angleInDegrees = angleInDegrees % 360;
            }

            data.setAngle(angleInDegrees);
            data.setDistance(distanceToNextPoint);

            point++;

        }
        return data;
    }

    public float getPercentageComplete() {

        float percentage = 0;
        float totalPoints = 0;
        float realPercentage = 0;

        totalPoints = xValStop / differenceOfX;
        percentage = point / totalPoints;
        realPercentage = percentage * 100;

        if (realPercentage > 100) {
            realPercentage = 100;
        }
        return realPercentage;
    }
}
