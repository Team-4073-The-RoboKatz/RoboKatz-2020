package frc.robot.Hardware;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import frc.robot.Constants;

public class Drivetrain {

    //Instantiate Hardware Objects 
    public static TalonSRX m_leftFrontDrive;
    public static TalonSRX m_rightFrontDrive;
    public static TalonSRX m_leftBackDrive;
    public static TalonSRX m_rightBackDrive;
    public static ADXRS450_Gyro s_gyro;
    
    //Create other necessary data structures
    public double[] motorSpeeds;


    //Create all of the motor objects
    public Drivetrain() {
        m_leftFrontDrive = new TalonSRX(Constants.frontLeftPort);
        m_leftBackDrive = new TalonSRX(Constants.backLeftPort);
        m_rightBackDrive = new TalonSRX(Constants.backRightPort);
        m_rightFrontDrive = new TalonSRX(Constants.frontRightPort);
        s_gyro = new ADXRS450_Gyro();

        m_rightBackDrive.setInverted(true);
        m_rightFrontDrive.setInverted(true);

        motorSpeeds = new double[4];
    }


    //Intakes values for each of the three possible movements and sets motor power appropriatly
    public void mechanumDrive(double straight, double turn, double strafe, double vexcampspeeds) {
       //Calculate the speed to apply to each of the wheels (LF, LB, RB, RF)
       
       motorSpeeds[0] = straight + turn + strafe;
       motorSpeeds[1] = straight + turn - strafe;
       motorSpeeds[2] = straight - turn + strafe;
       motorSpeeds[3] = straight - turn - strafe;

        //Make the motors drive at a speed set in robot.java (max speed)
       normalizeArray(motorSpeeds, vexcampspeeds);

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

        //Sleep for the specified amount of time
        try {
            Thread.sleep(250);
        } catch (Exception e) {}
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
    

    //Turns the robot to a given angle using the built-in gyroscope using an intaken number of degrees
    public void turnToAngle(double speed, double degrees) {
        //Get the initial angle of the robot (just for initial calculations so we don't keep accessing it over and over again)
        double startAngle = s_gyro.getAngle();        

        //Normalize the angle to travel to be within -180 to 180 degrees so we don't rotate a bazillion times
        while ((degrees - startAngle) < -180 || (degrees - startAngle) > 180) {
            if  ((degrees - startAngle) < -180) {
                degrees += 360;
            } else if ((degrees - startAngle) > 180) {
                degrees -=360;
            }
        }

        //Change the speed based on what direction we are turning
        if ((startAngle - degrees) > 0) {
            speed *= -1;
        }

        //Set the speed of the motors to turn
        m_leftFrontDrive.set(ControlMode.PercentOutput, speed);
        m_leftBackDrive.set(ControlMode.PercentOutput, speed);
        m_rightBackDrive.set(ControlMode.PercentOutput, -speed);
        m_rightFrontDrive.set(ControlMode.PercentOutput, -speed);

        //Wait until we're within 25 degrees of the target angle
        while (Math.abs(s_gyro.getAngle() - degrees) > 25) {}

        //Linearly scale the speed down as we approach the target so we don't overshoot
        while (Math.abs(s_gyro.getAngle() - degrees) > 5) {
            //Calculate the speed based on the distance left to go (the minimum amount this will ever be is 25% of the original speed)
            double tempSpeed = (0.75*(Math.abs(s_gyro.getAngle() - degrees)/25) + 0.25) * speed; 

            //Actually apply the speed
            m_leftFrontDrive.set(ControlMode.PercentOutput, tempSpeed);
            m_leftBackDrive.set(ControlMode.PercentOutput, tempSpeed);
            m_rightBackDrive.set(ControlMode.PercentOutput, -tempSpeed);
            m_rightFrontDrive.set(ControlMode.PercentOutput, -tempSpeed);
        }

        //Set everything to zero power and wait a quarter second for braking purposes
        m_leftFrontDrive.set(ControlMode.PercentOutput, 0);
        m_leftBackDrive.set(ControlMode.PercentOutput, 0);
        m_rightBackDrive.set(ControlMode.PercentOutput, 0);
        m_rightFrontDrive.set(ControlMode.PercentOutput, 0);
        try {
            Thread.sleep(250);
        } catch (Exception e) {}
    }
}