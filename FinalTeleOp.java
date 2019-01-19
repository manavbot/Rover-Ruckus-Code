package org.firstinspires.ftc.teamcode;

import android.media.MediaPlayer;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import java.lang.Math;

/**
 * This file contains an example of an iterative (Non-Linear) "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all iterative OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="FinalTeleOp", group="Drive Opmodes")
public class FinalTeleOp extends OpMode
{
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftFront = null;
    private DcMotor rightFront = null;
    private DcMotor leftBack = null;
    private DcMotor rightBack = null;
    private DcMotor lift = null;
    private DcMotor left = null;
    private DcMotor right = null;
    private DcMotor flip = null;
    private Servo cont = null;
    private Servo swing = null;
    private Servo claw = null;
    private Servo jewel = null;

    //slow/fast mode
    boolean control = false;
    boolean change = false;

    //intake toggle
    boolean intake = false;
    boolean changed = false;

    //flip toggle
    boolean tflip = false;
    boolean changed1 = false;


    //relic stuff
    double swingpos = 0.0;

    double contpos = 0;
    double speed = 0.15;



    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftFront  = hardwareMap.get(DcMotor.class, "lf");
        rightFront = hardwareMap.get(DcMotor.class, "rf");
        leftBack  = hardwareMap.get(DcMotor.class, "lb");
        rightBack = hardwareMap.get(DcMotor.class, "rb");
        lift = hardwareMap.get(DcMotor.class, "lift");
        left = hardwareMap.get(DcMotor.class, "left");
        right = hardwareMap.get(DcMotor.class, "right");
        flip = hardwareMap.get(DcMotor.class, "flip");
        cont = hardwareMap.get(Servo.class, "cont");
        swing = hardwareMap.get(Servo.class, "swing");
        claw = hardwareMap.get(Servo.class, "claw");
        jewel = hardwareMap.get(Servo.class, "jewel");

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        leftFront.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.FORWARD);
        lift.setDirection(DcMotor.Direction.REVERSE);
        flip.setDirection(DcMotor.Direction.REVERSE);

        left.setDirection(DcMotor.Direction.REVERSE);
        right.setDirection(DcMotor.Direction.FORWARD);
        //cont.setDirection(DcMotorSimple.Direction.REVERSE);

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        flip.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        flip.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        lift.setTargetPosition(15);
        jewel.setPosition(1);
        claw.setPosition(1);


        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");
        telemetry.addData("Flip position", flip.getCurrentPosition());
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
//        flip.setTargetPosition(flip.getCurrentPosition());
//        flip.setPower(0.1);
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        // Setup a variable for each drive wheel to save power level for telemetry
        double FLPower = 0;
        double FRPower = 0;
        double BLPower = 0;
        double BRPower = 0;
        double liftpower = 0.5;
        double intakepower = 0;
        int flipposition = 17;
        int levelposition = 15;
        int liftposition = 12096;
        int downposition = 10;
        double contspeed = 0.005;
        int threshold = 20; //joystick dead zones

        // Choose to drive using either Tank Mode, or POV Mode
        // Comment out the method that's not used.  The default below is POV.

        // POV Mode uses left stick to go forward, and right stick to turn.
        // - This uses basic math to combine motions and is easier to drive straight.
        //double drive = -gamepad1.left_stick_y;
        //double turn  =  gamepad1.right_stick_x;
        //leftPower    = Range.clip(drive + turn, -1.0, 1.0) ;
        //rightPower   = Range.clip(drive - turn, -1.0, 1.0) ;

        // Tank Mode uses one stick to control each wheel.
        // - This requires no math, but it is hard to drive forward slowly and keep straight.
        // leftPower  = -gamepad1.left_stick_y ;
        // rightPower = -gamepad1.right_stick_y ;

        //Mecanum Drive Mode uses left stick to strafe and go forwards and backwards; right stick to rotate
        if(Math.abs(gamepad1.left_stick_y) != 0 || Math.abs(gamepad1.left_stick_x) != 0){
            FRPower = FRPower + (-gamepad1.left_stick_y - gamepad1.left_stick_x);
            FLPower = FLPower + (-gamepad1.left_stick_y + gamepad1.left_stick_x);
            BRPower = BRPower + (-gamepad1.left_stick_y + gamepad1.left_stick_x);
            BLPower = BLPower + (-gamepad1.left_stick_y - gamepad1.left_stick_x);
        }
        if(Math.abs(gamepad1.right_stick_x) != 0){
            FRPower = FRPower + (gamepad1.right_stick_x);
            FLPower = FLPower + (-gamepad1.right_stick_x);
            BRPower = BRPower + (gamepad1.right_stick_x);
            BLPower = BLPower + (-gamepad1.right_stick_x);
        }
        /*
        if(gamepad2.left_stick_y > 0){
            lift.setPower(liftpower);

        }else if(gamepad2.left_stick_y < 0){
            lift.setPower(-liftpower);
            lift.setTargetPosition(2000);

        }else{
            lift.setPower(0);
        }
        */
        if(gamepad2.left_stick_y >= 0.1){
            lift.setPower(liftpower);
        }
        else if(gamepad2.left_stick_y <= -0.1){
            lift.setPower(-liftpower);

        }else{
            lift.setPower(0);
        }

        //fine control mode - doesn't work

        if (gamepad1.a)  {
            speed = 0.25;
        }else if(gamepad1.b){
            speed = 0.15;
        }

        // check for button state transitions.
        //Toggle between intake
        if (gamepad2.dpad_up)  {
            intakepower = 0.65;
//            flip.setTargetPosition(downposition);
//            flip.setPower(0.1);
        }else if(gamepad2.dpad_down){
            intakepower = -0.65;
        }else{
            intakepower = 0;
        }

//        if(gamepad1.left_bumper){
//            flip.setPower(0.1);
//            flip.setTargetPosition(downposition);
//        }
//
//        if (gamepad1.right_bumper && !changed1)  {
//            // button is transitioning to a pressed state.
//            tflip = !tflip; //true equals flip
//            changed1 = true;
//            if(tflip){
//                flip.setPower(0.2);
//                flip.setTargetPosition(flipposition);
//            }else{
//                flip.setPower(0.2);
//                flip.setTargetPosition(levelposition);
//            }
//        }else if(!gamepad1.right_bumper){
//            changed1 = false;
//        }

        if(gamepad1.right_bumper){
            flip.setPower(0.1);
        }else if(gamepad1.left_bumper){
            flip.setPower(-0.1);
        }else{
            flip.setPower(0);
        }
        //Position control vs fine control for relic
//        if(gamepad2.right_stick_y >= 0.1){
//            cont.setPosition(1);
//        }else if(gamepad2.right_stick_y <= -0.1){
//            cont.setPosition(0);
//        }

        if(contpos > 0.59){
            contpos = 0.59;
        }
        if(contpos < 0){
            contpos = 0;
        }

        if(gamepad2.right_stick_y >= 0.1){
            contpos += contspeed;
            cont.setPosition(contpos);
        }else if(gamepad2.right_stick_y <= -0.1){
            contpos -= contspeed;
            cont.setPosition(contpos);
        }
        /*
        if(gamepad2.y){
            swingpos += swingspeed;
            swing.setPosition(0.5);
        }
        if(gamepad2.x){
            swingpos += swingspeed;
            swing.setPosition(swingpos);
        }
        if(gamepad2.right_stick_x != 0){
            claw.setPosition(0.5 * gamepad2.right_stick_x);
        }
        */
        if(gamepad2.y){
            swing.setPosition(0);
        }
        if(gamepad2.x){
            swing.setPosition(1);
        }
        if(gamepad2.b){
            claw.setPosition(1);
        }
        if(gamepad2.a){
            claw.setPosition(0.4);
        }
        //Clip Range
        double FRPower2 = Range.clip(Range.scale(FRPower, -1, 1, -speed, speed), -1, 1);
        double FLPower2 = Range.clip(Range.scale(FLPower, -1, 1, -speed, speed), -1, 1);
        double BLPower2 = Range.clip(Range.scale(BLPower, -1, 1, -speed, speed), -1, 1);
        double BRPower2 = Range.clip(Range.scale(BRPower, -1, 1, -speed, speed), -1, 1);

        // Send calculated power to wheels
        leftFront.setPower(FLPower2);
        rightFront.setPower(FRPower2);
        leftBack.setPower(BLPower2);
        rightBack.setPower(BRPower2);

        if(gamepad2.dpad_left){
            left.setPower(-0.65);
            right.setPower(0.75);
        }else if(gamepad2.dpad_right){
            right.setPower(-0.65);
            left.setPower(0.75);
        }else{
            left.setPower(intakepower);
            right.setPower(intakepower*1.15384); //convert to 0.75
        }

        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Servo", "left (%.2f), right (%.2f)", FLPower, FRPower);
        telemetry.addData("Flip position", flip.getCurrentPosition());
        telemetry.addData("Lift position", lift.getCurrentPosition());
        telemetry.addData("Cont position", contpos);

    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {

    }

}
