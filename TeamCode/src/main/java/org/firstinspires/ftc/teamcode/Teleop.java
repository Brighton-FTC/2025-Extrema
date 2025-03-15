package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.teamcode.util.inputs.PSButtons;

@Config
@TeleOp(name = "Teleop (use this one)", group = "competition")
public class Teleop extends OpMode {
    public static final double TURN_THRESHOLD = 0.1;
    public static double SLOW_MODE_SPEED = 0.6;

    private MecanumDrive drive;
    private GamepadEx gamepad1Ex, gamepad2Ex;
    private GamepadEx gamepad;
    private IMU imu;

    private GrabberComponent grabber;
    private LinearSlideComponent slide;

    private boolean isFieldCentric = true;

    private boolean isRunningPid = true;
    private double inputMultiplier = 1;

    public static double kP = -0.035, kI = 0, kD = 0;
    private final PIDController headingController = new PIDController(kP, kI, kD);

    @Override
    public void init() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

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

//        motors[0].setRunMode(Motor.RunMode.VelocityControl);
//        motors[1].setRunMode(Motor.RunMode.VelocityControl);

        drive = new MecanumDrive(motors[0], motors[1], motors[2], motors[3]);

        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                        RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
                )
        ));

        imu.resetYaw();

        grabber = new GrabberComponent(hardwareMap, "left_claw_servo", "right_claw_servo", "rotator_servo");

        slide = new LinearSlideComponent(hardwareMap, "linear_slide_motor");
    }

    @Override
    public void loop() {
        headingController.setP(kP);

        gamepad1Ex.readButtons();
        gamepad2Ex.readButtons();

        // grabber
        if (gamepad.wasJustPressed(PSButtons.SQUARE)){
            grabber.toggleClaw();
        }
        if (gamepad.wasJustPressed(PSButtons.CIRCLE)){
            grabber.toggleRotator();
        }

        // linear slide
        if (gamepad.wasJustPressed(GamepadKeys.Button.DPAD_UP)) {
            slide.up();
            isRunningPid = true;
        } else if (gamepad.wasJustPressed(GamepadKeys.Button.DPAD_DOWN)) {
            slide.down();
            isRunningPid = true;

        } else if (gamepad.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER)
                - gamepad.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) != 0) {
            isRunningPid = false;
        }

        if (isRunningPid) {
            slide.run();
        } else {
            slide.getMotor().set(gamepad.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER)
                    - gamepad.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER));
        }

        // drivetrain
        double yaw = imu.getRobotYawPitchRollAngles().getYaw();

        if (gamepad.wasJustPressed(PSButtons.TRIANGLE)) {
            isFieldCentric = !isFieldCentric;
        }

        if (gamepad.wasJustPressed(PSButtons.CROSS)) {
            inputMultiplier = inputMultiplier == 1 ? SLOW_MODE_SPEED : 1;
        }

        if (Math.abs(gamepad.getRightX()) > TURN_THRESHOLD && !gamepad.isDown(GamepadKeys.Button.RIGHT_STICK_BUTTON)) {
            if (isFieldCentric) {
                drive.driveFieldCentric(gamepad.getLeftX() * inputMultiplier,
                        gamepad.getLeftY() * inputMultiplier,
                        gamepad.getRightX() * inputMultiplier,
                        yaw, true);
            } else {
                drive.driveRobotCentric(gamepad.getLeftX() * inputMultiplier,
                        gamepad.getLeftY() * inputMultiplier,
                        gamepad.getRightX() * inputMultiplier,
                        true);
            }

            headingController.setSetPoint(yaw);

        } else {
            if (isFieldCentric) {
                drive.driveFieldCentric(gamepad.getLeftX() * inputMultiplier,
                        gamepad.getLeftY() * inputMultiplier,
                        headingController.calculate(correctYaw(yaw, headingController.getSetPoint())),
                        yaw, true);
            } else {
                drive.driveRobotCentric(gamepad.getLeftX() * inputMultiplier,
                        gamepad.getLeftY() * inputMultiplier,
                        headingController.calculate(correctYaw(yaw, headingController.getSetPoint())),
                        true);
            }
        }

        // gamepad override
        if (gamepad2Ex.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER) || gamepad2Ex.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) {
            gamepad = gamepad == gamepad1Ex ? gamepad2Ex : gamepad1Ex; // switch control
        }

        telemetry.addData("Control", gamepad == gamepad1Ex ? "Gamepad 1" : "Gamepad 2");
        telemetry.addLine();

        telemetry.addLine(isFieldCentric ? "Driving Field Centric" : "Driving Robot Centric");
        if (inputMultiplier == SLOW_MODE_SPEED) {
            telemetry.addLine("Slow Mode Activated");
        }
        telemetry.addData("Heading", yaw);
        telemetry.addData("Target Heading", headingController.getSetPoint());
        telemetry.addLine();

        telemetry.addData("Slide Position", slide.getMotor().getCurrentPosition());
        telemetry.addData("Slide Set point", slide.getSetPoint());
        telemetry.addData("Slide At Set-Point?", slide.atSetPoint());
        telemetry.addLine();

        telemetry.addData("Grabber Status", grabber.isClosed() ? "Closed" : "Opened");
        telemetry.addData("Grabber Rotator Status", grabber.isDown() ? "Down" : "Forwards");
        telemetry.addLine();
    }

    // wraps yaw, in degrees
    private double correctYaw(double yaw, double expectedYaw) {
        while (expectedYaw - yaw > 180) {
            yaw += 360;
        }

        while (expectedYaw - yaw < -180) {
            yaw -= 360;
        }

        return yaw;
    }
}