package frc.robot;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

import frc.robot.commands.Swerve.*;
import frc.robot.subsystems.*;

public class RobotContainer {

    /* Controllers */
    private final CommandXboxController baseDriver = new CommandXboxController(0);
    private final CommandXboxController armDriver = new CommandXboxController(1);

    /* Drive Controls */
    private final int translationAxis = XboxController.Axis.kLeftY.value;
    private final int strafeAxis = XboxController.Axis.kLeftX.value;
    private final int rotationAxis = XboxController.Axis.kRightX.value;

    /* Subsystems */
    private final Swerve s_Swerve = new Swerve();

    /* Commands */
    private final SlowMode slowMode;
    private final FastMode fastMode;

    private final SendableChooser<Command> autoChooser;

    public RobotContainer() {
        CameraServer.startAutomaticCapture();
        s_Swerve.setDefaultCommand(
                new TeleopSwerve(
                        s_Swerve,
                        () -> -baseDriver.getRawAxis(translationAxis),
                        () -> -baseDriver.getRawAxis(strafeAxis),
                        () -> -baseDriver.getRawAxis(rotationAxis),
                        () -> baseDriver.leftBumper().getAsBoolean()
                )
        );

        slowMode = new SlowMode(s_Swerve);
        slowMode.addRequirements(s_Swerve);
        fastMode = new FastMode(s_Swerve);
        fastMode.addRequirements(s_Swerve);

        NamedCommands.registerCommand("zero gyro", new InstantCommand(() -> s_Swerve.zeroHeading()));

        // Configure the button bindings
        configureButtonBindings();

        autoChooser = AutoBuilder.buildAutoChooser();
        SmartDashboard.putData("Auto Chooser", autoChooser);
    }

    private void configureButtonBindings() {
        baseDriver.y().onTrue(new InstantCommand(() -> s_Swerve.zeroHeading()));
        baseDriver.b().onTrue(slowMode);
        baseDriver.b().onTrue(fastMode);
    }

    public Command getAutonomousCommand() {
        return autoChooser.getSelected().andThen(new InstantCommand(() -> s_Swerve.autoHeadingFix()));
    }

}