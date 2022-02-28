package frc.robot.Hardware;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import frc.robot.Constants;

public class Shooter {
    //Create hardware objects
    public TalonSRX m_intake;
    public TalonSRX m_shooter;

    public Shooter() {
        //Initialize hardware objects
        m_shooter = new TalonSRX(Constants.shooterPort);
        m_intake = new TalonSRX(Constants.intakePort);
    }
    
}
