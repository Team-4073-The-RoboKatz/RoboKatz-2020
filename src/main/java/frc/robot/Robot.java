// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Hardware.Drivetrain;
import frc.robot.Hardware.Shooter;

import java.lang.Math.*;


/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private double xDistance;
  private double totalAngle;
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  //Create input objects
  private XboxController c_xbox;

  //Create hardware Objects
  private Drivetrain drivetrain = new Drivetrain();
  private Shooter shooter = new Shooter();

  //Deal with limelight data
  NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
  NetworkTableEntry hasTargetEntry;
  NetworkTableEntry offsetXEntry;
  NetworkTableEntry offsetYEntry;

  //Create variables
  public double straight;
  public double strafe;
  public double turn;
  public double shooterPower;
  public double intakePower;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    
    drivetrain.m_leftFrontDrive.set(ControlMode.PercentOutput, 1);

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    drivetrain.m_leftFrontDrive.set(ControlMode.PercentOutput, 0);
    drivetrain.m_leftBackDrive.set(ControlMode.PercentOutput, 1);

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    drivetrain.m_leftBackDrive.set(ControlMode.PercentOutput, 0);
    drivetrain.m_rightFrontDrive.set(ControlMode.PercentOutput, 1);

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    drivetrain.m_rightFrontDrive.set(ControlMode.PercentOutput, 0);
    drivetrain.m_rightBackDrive.set(ControlMode.PercentOutput, 1);

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    drivetrain.m_rightBackDrive.set(ControlMode.PercentOutput, 0);

  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    /* switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break; */
    }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
    //Create XBox Controller Object
    c_xbox = new XboxController(0);

    //Get initial Limelight values
    table = NetworkTableInstance.getDefault().getTable("limelight");
    hasTargetEntry = table.getEntry("tv");
    offsetXEntry = table.getEntry("tx");
    offsetYEntry = table.getEntry("ty");


  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    
    //Calcculate Limelight data
    totalAngle = Math.toRadians(8) + Math.toRadians(offsetYEntry.getDouble(0.0));
    xDistance = 54/Math.tan(totalAngle);

    //Put data to the dashboard
    SmartDashboard.putNumber("Target?", hasTargetEntry.getDouble(0.0));
    SmartDashboard.putNumber("offsetX", offsetXEntry.getDouble(0.0));
    SmartDashboard.putNumber("offsetY", offsetYEntry.getDouble(0.0));
    SmartDashboard.putNumber("xDistance", xDistance);

    //Store the values to use to control the robot
    straight = -c_xbox.getRawAxis(5);
    strafe = c_xbox.getRawAxis(4);
    turn = c_xbox.getRawAxis(3) - c_xbox.getRawAxis(2);
    shooterPower = c_xbox.getRawAxis(1);

    //Double-check to ensure that we;re getting actual readings
    if (Math.abs(straight) < 0.1) {
      straight = 0;
    }

    if (Math.abs(strafe) < 0.1) {
      strafe = 0;
    }

    if (Math.abs(turn) < 0.1) {
      turn = 0;
    }

    if (Math.abs(shooterPower) < 0.1) {
      shooterPower = 0;
    }

    //Apply joystick values
    drivetrain.mechanumDrive(straight, turn, strafe);
    shooter.m_shooter.set(ControlMode.PercentOutput, 0.7*shooterPower);

    //Use the buttons to control the intake
    if (c_xbox.getYButton()) {
      shooter.m_intake.set(ControlMode.PercentOutput, 0.5);
    } else if (c_xbox.getBButton()) {
      shooter.m_intake.set(ControlMode.PercentOutput, -0.5);
    } else {
      shooter.m_intake.set(ControlMode.PercentOutput, 0);
    }

  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
