package org.firstinspires.ftc.teamcode;

import com.arcrobotics.ftclib.hardware.ServoEx;
import com.arcrobotics.ftclib.hardware.SimpleServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;


public class GrabberComponent {
    public static final double TURN_AMOUNT = 0.5;

    private final ServoEx leftClaw;
    private final ServoEx rightClaw;

    private final ServoEx grabberRotateMotor;

    private boolean closed = true;

    public GrabberComponent(HardwareMap hardwareMap, String leftId, String rightId, String genId) {
        leftClaw = new SimpleServo(hardwareMap, leftId, 0, 360);
        rightClaw = new SimpleServo(hardwareMap, rightId, 0, 360);
        grabberRotateMotor = new SimpleServo(hardwareMap, genId, 0, 360);

    }



    public void grab() {
        grabberRotateMotor.rotateBy(TURN_AMOUNT);
        leftClaw.rotateBy(-TURN_AMOUNT);
        rightClaw.rotateBy(TURN_AMOUNT);
        closed = true;

    }

    public void reset() {
        grabberRotateMotor.rotateBy(-TURN_AMOUNT);
        leftClaw.rotateBy(TURN_AMOUNT);
        rightClaw.rotateBy(-TURN_AMOUNT);
        closed = false;

    }

    public void toggle() {
        if (closed) {
            reset();
        } else {
            grab();
        }
    }

    public boolean isClosed() {
        return closed;
    }
}
