package org.firstinspires.ftc.teamcode;

import com.arcrobotics.ftclib.gamepad.GamepadEx;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.util.inputs.PSButtons;

@TeleOp
public class GrabberTester extends OpMode {
    private GrabberComponent grabber;

    private GamepadEx gamepad;

    @Override
    public void init() {
        grabber = new GrabberComponent(hardwareMap, "left_claw_servo", "right_claw_servo", "rotator_servo");
        gamepad = new GamepadEx(gamepad1);
    }

    @Override
    public void loop() {
        gamepad.readButtons();

        if (gamepad.wasJustPressed(PSButtons.SQUARE)) {
            grabber.toggleClaw();
        }

        if (gamepad.wasJustPressed(PSButtons.CIRCLE)) {
            grabber.toggleRotator();
        }

        telemetry.addData("Grabber Status", grabber.isClosed() ? "Closed" : "Opened");
        telemetry.addData("Rotator Status", grabber.isDown() ? "Down" : "Forwards");
    }
}
