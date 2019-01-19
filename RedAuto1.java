/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;


/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

/** Shoutout to Rishi Parikh for being a true hiu */

@Autonomous(name="Auto Red 1", group="Linear Opmode")
public class RedAuto1 extends LinearOpMode {

    public static final String TAG = "Vuforia VuMark Sample";

    OpenGLMatrix lastLocation = null;
    VuforiaLocalizer vuforia;

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftFront = null;
    private DcMotor rightFront = null;
    private DcMotor leftBack = null;
    private DcMotor rightBack = null;
    private DcMotor lift1 = null;
    private ColorSensor color = null;
    private DcMotor flip = null;
    //    private Servo cont = null;
    private DcMotor left = null;
    private DcMotor right = null;

    double FLPower = 0;
    double FRPower = 0;
    double BLPower = 0;
    double BRPower = 0;
    double liftpower = 0.9;

    double speed = 0.1;

    final double countsPerRev = 537.6;
    final int countsPerInch = 21;

    int flipposition = 90;
    int downposition = 0;
    RelicRecoveryVuMark finalview = null;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = "AXDt5KP/////AAAAGWeVKR4ppkmsoKP4RcK6sthe5mVGiCtzcIzjeNoMcU9DeY+UJpjzOy7Imjj4NGFNf5tL78lK8cOIbNxiSZgfRpILVKwHXIvpsB3FoEb1Bsi2eg2uc2bgkwi4Ms+aCDExZbH/ltzjQJab44d07kPYMCqkfnjDnWzMugWnbXZFbvARgBbn+T3zZUMsUIpspNKSM6h9zIQrQ4kzkZVzRX/mvB8dBp4VBeIKNfjjCIIgPatNI8erwY563jPC2CgIU45TWXFeFw2Crkt2e12JUE5LGTzL0JT7jktTTpTAuSuRuDjFqdNOW2eYdl37hL8JbR2fxUND1fDP3aAKedC9tepHw7pdo5ruqdeEVsYJFUGvvEXj";

        /*
         * We also indicate which camera on the RC that we wish to use.
         * Here we chose the back (HiRes) camera (for greater range), but
         * for a competition robot, the front camera might be more convenient.
         */
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).

        leftFront  = hardwareMap.get(DcMotor.class, "lf");
        rightFront = hardwareMap.get(DcMotor.class, "rf");
        leftBack  = hardwareMap.get(DcMotor.class, "lb");
        rightBack = hardwareMap.get(DcMotor.class, "rb");
        lift1 = hardwareMap.get(DcMotor.class, "lift");
        color = hardwareMap.get(ColorSensor.class, "color");
        flip = hardwareMap.get(DcMotor.class, "flip");
//        cont = hardwareMap.get(Servo.class, "cont");
        left = hardwareMap.get(DcMotor.class, "left");
        right = hardwareMap.get(DcMotor.class, "right");

        leftFront.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        left.setDirection(DcMotor.Direction.REVERSE);
        right.setDirection(DcMotor.Direction.FORWARD);

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lift1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        resetEncoders();

        leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        flip.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        boolean LEDOn = true;

        color.enableLed(LEDOn);

        boolean scan = true;

        String vuMarkString = "";

        // Wait for the game to start (driver presses PLAY)
        telemetry.addData(">", "Press Play to start");
        telemetry.update();
        waitForStart();

        Servo jewel = null;
        Servo swing = null;
        Servo claw = null;

        jewel = hardwareMap.get(Servo.class, "jewel");
        swing = hardwareMap.get(Servo.class, "swing");
        claw = hardwareMap.get(Servo.class, "claw");

        jewel.setPosition(1);
        swing.setPosition(0);
        claw.setPosition(0.4);


        relicTrackables.activate();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while(scan){
            RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);
            telemetry.addData("VuMark", "%s visible", vuMark);
            telemetry.update();
            if (vuMark != RelicRecoveryVuMark.UNKNOWN) {

                /* Found an instance of the template. In the actual game, you will probably
                  loop until this condition occurs, then move on to act accordingly depending
                  on which VuMark was visible.*/
                finalview= vuMark;
                scan = false;
            }
            if(runtime.seconds() > 4){
                scan = false;
                jewel.setPosition(1);
            }
        }

        //Align with jewel
        jewel.setPosition(0.35);
        sleep(500);
        //Hit Jewel
        runtime.reset();
        scan = true;
        while(scan){
            if(isBlue()){
                backward(550);
                sleep(1750);
                jewel.setPosition(1);
                sleep(750);
                scan = false;
            }else if(isRed()){
                forward(100);
                sleep(1500);
                jewel.setPosition(1);
                sleep(250);
                backward(625);
                sleep(2000);
                scan = false;
            }else if(runtime.seconds()>2){
                backward(550);
                sleep(3000);
                jewel.setPosition(1);
                scan=false;
            }
        }


        //Align with center of cryptobox, we are backward so everything is flipped
        backward(600);
        sleep(3500);
        right(675);
        sleep(2750);

        speed = 0.05;

        if(finalview == RelicRecoveryVuMark.LEFT){
            rotate(20);
            sleep(2000);
            backward(275);
            sleep(1000);
        }else if(finalview == RelicRecoveryVuMark.RIGHT){
            left(800);
            sleep(4000);
            rotate(20);
            sleep(2000);
            backward(250);
            sleep(1000);
        }else{
            left(290);
            sleep(2000);
            rotate(20);
            sleep(2000);
            backward(250);
            sleep(1000);
        }
        speed = 0.2;

        outtake(true);
        sleep(1000);
        forward(150);
        sleep(1000);
        outtake(false);
        claw.setPosition(0.4);
        sleep(1000);
//        rotate(180); WEIRD CODE FOR GOING BACK FOR ANOTHER GLYPH
//        sleep(500);
//        intake(true);
//        backward(250);
//        sleep(1000);
//        rotate(180);
//        backward(1100);
        /*
        //go back for another glyph
        intake(true);
        backward(200);
        sleep(250); //have glyph
//            if(finalview == RelicRecoveryVuMark.LEFT){
//                right(25);
//                sleep(100);
//            }else if(finalview == RelicRecoveryVuMark.RIGHT){
//                left(25);
//                sleep(100);
//            }
        forward(200);
        sleep(500);
        flip(true);
        */

        // Show the elapsed game time.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.update();
        return;
    }
    public void rotate(double degrees){
        final int countsPerDegree = 9; //Calculate for 10 degrees with protractor, then divide and hope it works
        //degrees to the left, make negative to turn right
        int countsInDegrees = (int)Math.round(degrees * countsPerDegree);
        rightFront.setTargetPosition(rightFront.getCurrentPosition() + countsInDegrees);
        rightBack.setTargetPosition(rightBack.getCurrentPosition() + countsInDegrees);
        leftFront.setTargetPosition(leftFront.getCurrentPosition() - countsInDegrees);
        leftBack.setTargetPosition(leftBack.getCurrentPosition() - countsInDegrees);
        leftFront.setPower(speed);
        leftBack.setPower(speed);
        rightFront.setPower(speed);
        rightBack.setPower(speed);

    }
    public void flip(boolean up){
        if(up){
            flip.setPower(0.1);
        }else if(!up){
            flip.setPower(-0.1);
        }
        sleep(250);
        flip.setPower(0);
    }
    public void forward(double distance){
        int distanceInCounts = (int)Math.round(distance);
        leftFront.setTargetPosition(leftFront.getCurrentPosition() + distanceInCounts);
        rightBack.setTargetPosition(rightBack.getCurrentPosition() + distanceInCounts);
        rightFront.setTargetPosition(rightFront.getCurrentPosition() + distanceInCounts);
        leftBack.setTargetPosition(leftBack.getCurrentPosition() + distanceInCounts);
        leftFront.setPower(speed);
        leftBack.setPower(speed);
        rightFront.setPower(speed);
        rightBack.setPower(speed);
    }
    public void backward(double distance){
        int distanceInCounts = (int)Math.round(distance);
        leftFront.setTargetPosition(leftFront.getCurrentPosition() - distanceInCounts);
        rightBack.setTargetPosition(rightBack.getCurrentPosition() - distanceInCounts);
        rightFront.setTargetPosition(rightFront.getCurrentPosition() - distanceInCounts);
        leftBack.setTargetPosition(leftBack.getCurrentPosition() - distanceInCounts);
        leftFront.setPower(speed);
        leftBack.setPower(speed);
        rightFront.setPower(speed);
        rightBack.setPower(speed);
    }
    public void right(double distance){
        int distanceInCounts = (int)Math.round(distance);
        leftFront.setTargetPosition(leftFront.getCurrentPosition() + distanceInCounts);
        rightBack.setTargetPosition(rightBack.getCurrentPosition() + distanceInCounts);
        rightFront.setTargetPosition(rightFront.getCurrentPosition() - distanceInCounts);
        leftBack.setTargetPosition(leftBack.getCurrentPosition() - distanceInCounts);
        leftFront.setPower(speed);
        leftBack.setPower(speed);
        rightFront.setPower(speed);
        rightBack.setPower(speed);
    }
    public void left(double distance){
        int distanceInCounts = (int)Math.round(distance);
        leftFront.setTargetPosition(leftFront.getCurrentPosition() - distanceInCounts);
        rightBack.setTargetPosition(rightBack.getCurrentPosition() - distanceInCounts);
        rightFront.setTargetPosition(rightFront.getCurrentPosition() + distanceInCounts);
        leftBack.setTargetPosition(leftBack.getCurrentPosition() + distanceInCounts);
        leftFront.setPower(speed);
        leftBack.setPower(speed);
        rightFront.setPower(speed);
        rightBack.setPower(speed);
    }
    public boolean isRed(){
        double threshold = 2;
        boolean b = false;
        if(color.blue() < threshold && color.red() > threshold){
            telemetry.addData("Colors", "red , blue ", color.blue(), color.red());
            return true;
        }else{
            telemetry.addData("Colors", "red , blue ", color.blue(), color.red());
            return false;
        }
    }
    public boolean isBlue(){
        double threshold = 2;
        boolean b = false;
        if(color.blue() > threshold && color.red() < threshold){
            telemetry.addData("Colors", "red , blue ", color.blue(), color.red());
            return true;
        }else{
            telemetry.addData("Colors", "red , blue ", color.blue(), color.red());
            return false;
        }
    }
    public void resetEncoders(){
        while(rightBack.getCurrentPosition() != 0 || leftFront.getCurrentPosition() != 0 || leftBack.getCurrentPosition() != 0){
            leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
    }
    public void outtake(boolean on){
        if(on){
            left.setPower(-0.65);
            right.setPower(-0.65);
        }else{
            left.setPower(0);
            right.setPower(0);
        }
    }
    public void intake(boolean on){
        if(on){
            left.setPower(0.55);
            right.setPower(0.55);
        }else{
            left.setPower(0);
            right.setPower(0);
        }
    }
}
