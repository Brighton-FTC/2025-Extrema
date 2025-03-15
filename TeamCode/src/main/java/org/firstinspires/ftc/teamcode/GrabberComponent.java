package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.hardware.ServoEx;
import com.arcrobotics.ftclib.hardware.SimpleServo;
import com.qualcomm.robotcore.hardware.HardwareMap;


@Config
public class GrabberComponent {
    public static double LEFT_CLAW_TURN_AMOUNT = 1;
    public static double RIGHT_CLAW_TURN_AMOUNT = 0.4;

    public static double ROTATOR_TURN_AMOUNT = 1;

    private final ServoEx leftClaw, rightClaw, rotator;


    public GrabberComponent(HardwareMap hardwareMap, String leftId, String rightId, String rotatorId) {
        leftClaw = new SimpleServo(hardwareMap, leftId, 0, 360);
        rightClaw = new SimpleServo(hardwareMap, rightId, 0, 360);

        rotator = new SimpleServo(hardwareMap, rotatorId, 0, 360);
        rotator.setInverted(true);

        leftClaw.setInverted(true);
    }

    public void grab() {
        leftClaw.setPosition(0);
        rightClaw.setPosition(0);

    }

    public void reset() {
        leftClaw.setPosition(LEFT_CLAW_TURN_AMOUNT);
        rightClaw.setPosition(RIGHT_CLAW_TURN_AMOUNT);

    }

    public void toggleClaw() {
        if (leftClaw.getPosition() == 0) {
            reset();
        } else {
            grab();
        }
    }

    public void down() {
        rotator.setPosition(0);
    }

    public void forward() {
        rotator.setPosition(ROTATOR_TURN_AMOUNT);
    }

    public void toggleRotator() {
        if (rotator.getPosition() == 0) {
            forward();
        } else {
            down();
        }
    }

    public boolean isClosed() {
        return leftClaw.getPosition() == 0;
    }

    public boolean isDown() {
        return rotator.getPosition() == 0;
    }
}
