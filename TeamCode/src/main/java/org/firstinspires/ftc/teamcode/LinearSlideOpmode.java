package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.util.inputs.PSButtons;

@TeleOp

public class LinearSlideOpmode extends OpMode {

    private GamepadEx gamepad;
    private LinearSlideComponent linearSlide;

    private boolean isRunningPid = true;

    @Override
    public void init() {
        linearSlide = new LinearSlideComponent(hardwareMap, "linear_slide_motor");
        gamepad = new GamepadEx(gamepad1);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
    }

    @Override
    public void loop() {
        gamepad.readButtons();

        if (gamepad.wasJustPressed(GamepadKeys.Button.DPAD_UP)) {
            linearSlide.up();
            isRunningPid = true;
        } else if (gamepad.wasJustPressed(GamepadKeys.Button.DPAD_DOWN)) {
            linearSlide.down();
            isRunningPid = true;

        } else if (gamepad.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER)
                - gamepad.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) != 0) {
            isRunningPid = false;
        }

        if (isRunningPid) {
            linearSlide.run();
        } else {
            linearSlide.getMotor().set(gamepad.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER)
                    - gamepad.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER));
        }

        telemetry.addData("Position", linearSlide.getMotor().getCurrentPosition());
        telemetry.addData("Set point", linearSlide.getSetPoint());
        telemetry.addData("At Set-Point", linearSlide.atSetPoint());
    }

}
