package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Config
public class LinearSlideComponent {
    private Motor slideMotor;

    private PIDController controller = new PIDController(0, 0, 0);
    public static double kP = 0.003, kI = 0, kD = 0;

    public static double UP_POSITION = 9000;
    public static double DOWN_POSITION = 0;

    public LinearSlideComponent(HardwareMap hardwareMap, String motorId){
        slideMotor = new Motor(hardwareMap, motorId);
        slideMotor.resetEncoder();
        // slideMotor.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        slideMotor.setRunMode(Motor.RunMode.VelocityControl);
        controller.setSetPoint(DOWN_POSITION);
    }

    /**
     * Make sure to call every tick.
     */
    public void run() {
        controller.setPID(kP, kI, kD);
        slideMotor.set(controller.calculate(slideMotor.getCurrentPosition()));
    }

    public void up() {
        controller.setSetPoint(UP_POSITION);
    }

    public void down() {
        controller.setSetPoint(DOWN_POSITION);
    }

    public boolean atSetPoint() {
            return controller.atSetPoint();
    }

    public Motor getMotor() {
        return slideMotor;
    }

    public double getSetPoint() {
        return controller.getSetPoint();
    }
}
