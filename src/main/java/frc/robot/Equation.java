/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.lang.Math;

public class Equation {

    /*
     * WRITE COEFFICIENT ARRAY IN REVERSE ORDER ONLY Example: Array - (1,-2,3)
     * Modeled Equation Based Off Array -- 3x^3 - 2x^2 + x
     */

    public float[] coefficients;

    public void setCoefficients(float[] coefficients) {
        this.coefficients = coefficients;
    }

    public float evaluateExpression(float x) {

        float sum = 0;
        float result = 0;

        for (float i = 0; i < coefficients.length; i++) {
            float n = i + 1;
            result = (float) Math.pow(x, n);
            sum = (coefficients[(int) i] * result + sum);
        }
        return sum;
    }

    public float evaluateDistance(float side1, float side2) { // SIDE1 REPRESENTS Y-AXIS AND SIDE2 REPRESENTS X-AXIS

        float distance = 0;

        distance = (float) Math.sqrt(side1 * side1 + side2 * side2);
        return distance;
    }

    public float evaluateAngle(float side1, float side2) { // SIDE1 REPRESENTS Y-AXIS AND SIDE2 REPRESENTS X-AXIS

        float radianAngle = 0;
        float degreeAngle = 0;
        float realDegreeAngle = 0;

        radianAngle = (float) Math.atan(side1 / side2);
        degreeAngle = (float) Math.toDegrees(radianAngle);

        if (degreeAngle < 0) {
            realDegreeAngle = 360 + degreeAngle;
        } else {
            realDegreeAngle = degreeAngle;
        }
        return realDegreeAngle;
    }
}