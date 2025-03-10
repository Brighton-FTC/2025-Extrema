package org.firstinspires.ftc.teamcode;

import com.arcrobotics.ftclib.controller.PController;
import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.teamcode.util.inputs.PSButtons;

@TeleOp(name = "Teleop (use this one)", group = "competition")
public class Teleop extends OpMode {
    public static final double TURN_THRESHOLD = 0.1;

    private MecanumDrive drive;
    private GamepadEx gamepad1Ex, gamepad2Ex;
    private GamepadEx gamepad;
    private IMU imu;

    private GrabberComponent grabber;
    private LinearSlideComponent slide;

    private boolean isFieldCentric = true;

    private PController headingController = new PController(-0.025);

    @Override
    public void init() {
        gamepad1Ex = new GamepadEx(gamepad1);
        gamepad2Ex = new GamepadEx(gamepad2);

        gamepad = gamepad1Ex;

        Motor[] motors = {
                new Motor(hardwareMap, "front_left_drive"),
                new Motor(hardwareMap, "front_right_drive"),
                new Motor(hardwareMap, "back_left_drive"),
                new Motor(hardwareMap, "back_right_drive")
        };

        motors[1].setInverted(true);
        motors[2].setInverted(true);
        motors[3].setInverted(true);

        drive = new MecanumDrive(motors[0], motors[1], motors[2], motors[3]);

        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                        RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
                )
        ));

        imu.resetYaw();

        grabber = new GrabberComponent(hardwareMap, "left_claw_servo", "right_claw_servo", "grabber_rotator_motor");

        slide = new LinearSlideComponent(hardwareMap, "linear_slide_motor");
    }

    @Override
    public void loop() {
        gamepad1Ex.readButtons();
        gamepad2Ex.readButtons();

        // grabber
        if (gamepad.wasJustPressed(PSButtons.SQUARE)){
            slide.toggle();
            grabber.toggle();


        }
        // linear slide
        slide.run();

        // drivetrain
        double yaw = imu.getRobotYawPitchRollAngles().getYaw();

        if (gamepad.wasJustPressed(PSButtons.TRIANGLE)) {
            isFieldCentric = !isFieldCentric;
        }

        if (Math.abs(gamepad.getRightX()) > TURN_THRESHOLD) {
            if (isFieldCentric) {
                drive.driveFieldCentric(gamepad.getLeftX(), gamepad.getLeftY(), gamepad.getRightX(), yaw, true);
            } else {
                drive.driveRobotCentric(gamepad.getLeftX(), gamepad.getLeftY(), gamepad.getRightX(), true);
            }
            headingController.setSetPoint(yaw);
        } else {
            if (isFieldCentric) {
                drive.driveFieldCentric(gamepad.getLeftX(), gamepad.getLeftY(), headingController.calculate(yaw), yaw, true);
            } else {
                drive.driveRobotCentric(gamepad.getLeftX(), gamepad.getLeftY(), headingController.calculate(yaw), true);
            }
        }

        // gamepad override
        if (gamepad2Ex.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER) || gamepad2Ex.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) {
            gamepad = gamepad == gamepad1Ex ? gamepad2Ex : gamepad1Ex; // switch control
        }

        telemetry.addData("Control", gamepad == gamepad1Ex ? "Gamepad 1" : "Gamepad 2");
        telemetry.addLine();

        telemetry.addLine(isFieldCentric ? "Driving Field Centric" : "Driving Robot Centric");
        telemetry.addData("Heading", yaw);
        telemetry.addLine();

        telemetry.addData("Slide Position", slide.getMotor().getCurrentPosition());
        telemetry.addData("Slide Set point", slide.getSetPoint());
        telemetry.addData("Slide At Set-Point?", slide.atSetPoint());
        telemetry.addLine();

        telemetry.addData("Grabber Status", grabber.isClosed() ? "Closed" : "Opened");
        telemetry.addLine();
    }
}
