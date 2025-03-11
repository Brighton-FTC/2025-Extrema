package com.example.trajectories;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true"); // improve performance

        MeepMeep meepMeep = new MeepMeep(800);


        RoadRunnerBotEntity blueBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 16)
                .setDimensions(16, 18)
                .setColorScheme(new ColorSchemeBlue())
                .build();

        RoadRunnerBotEntity redBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 16)
                .setDimensions(16, 18)
                .setColorScheme(new ColorSchemeRed())
                .build();


        blueBot.runAction(blueBot.getDrive().actionBuilder(new Pose2d(34, 62, 0))
                .splineTo(new Vector2d(50, 60), 0) // basket
                .setTangent(-90)
                .splineToSplineHeading(new Pose2d(45, 30, -90), -90) // yellow sample
                .setTangent(90)
                .splineToSplineHeading(new Pose2d(50, 50, 90), 90) // basket
                .setTangent(-90)
                .splineToSplineHeading(new Pose2d(57, 30, -90), -90) // yellow sample
                .setTangent(90)
                .splineToSplineHeading(new Pose2d(50, 50, 90), 90) // basket
                .setTangent(-90)
                .splineToSplineHeading(new Pose2d(67, 30, -90), -90) // yellow sample
                .setTangent(90)
                .splineToSplineHeading(new Pose2d(50, 50, 90), 90) // basket
                .setTangent(-90)
                .splineToSplineHeading(new Pose2d(22, 0, -90), -90) // submersible
                .build());

        redBot.runAction(redBot.getDrive().actionBuilder(new Pose2d(-34, -62, 180))
                .splineTo(new Vector2d(-52, -52), 180) // basket
                .setTangent(90)
                .splineToSplineHeading(new Pose2d(-45, -30, 90), 90) // yellow sample
                .setTangent(-90)
                .splineToSplineHeading(new Pose2d(-52, -52, -90), -90) // basket
                .setTangent(90)
                .splineToSplineHeading(new Pose2d(-57, -30, 90), 90) // yellow sample
                .setTangent(-90)
                .splineToSplineHeading(new Pose2d(-52, -52, -90), -90) // basket
                .setTangent(90)
                .splineToSplineHeading(new Pose2d(-67, -30, 90), 90) // yellow sample
                .setTangent(-90)
                .splineToSplineHeading(new Pose2d(-52, -52, -90), -90) // basket
                .setTangent(90)
                .splineToSplineHeading(new Pose2d(-22, 0, -90), 90) // submersible
                .build());


        meepMeep.setBackground(MeepMeep.Background.FIELD_INTO_THE_DEEP_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(blueBot)
                .addEntity(redBot)
                .start();
    }
}
