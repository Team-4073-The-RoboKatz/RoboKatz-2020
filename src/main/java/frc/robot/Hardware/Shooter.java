package frc.robot.Hardware;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import frc.robot.Constants;

public class Shooter {
    public TalonSRX m_intake;
    public TalonSRX m_shooter;

    public Shooter() {
        m_shooter = new TalonSRX(Constants.shooterPort);
        m_intake = new TalonSRX(Constants.intakePort);
    }
    
}
