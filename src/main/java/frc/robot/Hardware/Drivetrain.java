package frc.robot.Hardware;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import frc.robot.Constants;

public class Drivetrain {

    //Instantiate Hardware Objects 
    public static TalonSRX m_leftFrontDrive;
    public static TalonSRX m_rightFrontDrive;
    public static TalonSRX m_leftBackDrive;
    public static TalonSRX m_rightBackDrive;
    
    //Create other necessary data structures
    public double[] motorSpeeds;


    //Create all of the motor objects
    public Drivetrain() {
        m_leftFrontDrive = new TalonSRX(Constants.frontLeftPort);
        m_leftBackDrive = new TalonSRX(Constants.backLeftPort);
        m_rightBackDrive = new TalonSRX(Constants.backRightPort);
        m_rightFrontDrive = new TalonSRX(Constants.frontRightPort);

        m_rightBackDrive.setInverted(true);
        m_rightFrontDrive.setInverted(true);

        motorSpeeds = new double[4];
    }


    //Intakes values for each of the three possible movements and sets motor power appropriatly
    public void mechanumDrive(double straight, double turn, double strafe) {
       //Calculate the speed to apply to each of the wheels (LF, LB, RB, RF)
       motorSpeeds[0] = straight + turn + strafe;
       motorSpeeds[1] = straight + turn - strafe;
       motorSpeeds[2] = straight - turn + strafe;
       motorSpeeds[3] = straight - turn - strafe;

       normalizeArray(motorSpeeds, 1);

       m_leftFrontDrive.set(ControlMode.PercentOutput, motorSpeeds[0]);
       m_leftBackDrive.set(ControlMode.PercentOutput, motorSpeeds[1]);
       m_rightBackDrive.set(ControlMode.PercentOutput, motorSpeeds[2]);
       m_rightFrontDrive.set(ControlMode.PercentOutput, motorSpeeds[3]);

       return;
    }

    //Intakes values for each of the three possible movements and sets motor power appropriatly for a certain period of time
    public void mechanumDriveTime(double straight, double turn, double strafe, long time) {
        //Calculate the speed to apply to each of the wheels (LF, LB, RB, RF)
        motorSpeeds[0] = straight + turn + strafe;
        motorSpeeds[1] = straight + turn - strafe;
        motorSpeeds[2] = straight - turn + strafe;
        motorSpeeds[3] = straight - turn - strafe;
 
        //Normalize these speeds
        normalizeArray(motorSpeeds, 1);
 
        //Apply the speed values appropriatly
        m_leftFrontDrive.set(ControlMode.PercentOutput, motorSpeeds[0]);
        m_leftBackDrive.set(ControlMode.PercentOutput, motorSpeeds[1]);
        m_rightBackDrive.set(ControlMode.PercentOutput, motorSpeeds[2]);
        m_rightFrontDrive.set(ControlMode.PercentOutput, motorSpeeds[3]);

        //Sleep for the specified amount of time
        try {
            Thread.sleep(time);
        } catch (Exception e) {}
 

        //Set the motor speeds back to zero
        m_leftFrontDrive.set(ControlMode.PercentOutput, 0);
        m_leftBackDrive.set(ControlMode.PercentOutput, 0);
        m_rightBackDrive.set(ControlMode.PercentOutput, 0);
        m_rightFrontDrive.set(ControlMode.PercentOutput, 0);

        return;
     }


    //Normalizes an array to a maximum value
    public void normalizeArray(double[] input, double valueToNormalizeTo) {

        double currentMax = input[0];

        //Find the maximum value
        for (int i = 1; i < input.length; i++) {
            if (input[i] > currentMax) {
                currentMax = input[i];
            }
        }

        //Don't bother changing it if the current maximum is smaller than the value to normalize to
        if (currentMax < valueToNormalizeTo) {
            return;
        }

        //Normalize the remaining values in the array
        for (int i = 0; i < input.length; i++) {
            input[i] = input[i] * valueToNormalizeTo / currentMax;
        }
    }  
}