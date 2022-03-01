package frc.robot.Hardware;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import frc.robot.Constants;

public class Shooter {
    //Create hardware objects
    public TalonSRX m_intake;
    public CANSparkMax m_shooter;

    public Shooter() {
        //Initialize hardware objects
        m_shooter = new CANSparkMax(Constants.shooterPort, MotorType.kBrushless);
        m_intake = new TalonSRX(Constants.intakePort);

        //Set the shooter motor to break when zero power is applied
        m_shooter.setIdleMode(CANSparkMax.IdleMode.kBrake);
    }
    
}
