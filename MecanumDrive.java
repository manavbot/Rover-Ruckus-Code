package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
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

public class MecanumDrive extends OpMode
{
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftFront = null;
    private DcMotor rightFront = null;
    private DcMotor leftBack = null;
    private DcMotor rightBack = null;

    public MecanumDrive(DcMotor lf, DcMotor rf, DcMotor lb, DcMotor rb){

        leftFront = lf;
        rightFront = rf;
        leftBack = lb;
        rightBack = rb;
    }
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.REVERSE);


        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
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
        int threshold = 20;

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
            FRPower = (-gamepad1.left_stick_y - gamepad1.left_stick_x);
            FLPower = (gamepad1.left_stick_y - gamepad1.left_stick_x);
            BRPower = (gamepad1.left_stick_y - gamepad1.left_stick_x);
            BLPower = (-gamepad1.left_stick_y - gamepad1.left_stick_x);
        }

        if(Math.abs(gamepad1.right_stick_x) != 0){
            FRPower = (gamepad1.right_stick_x);
            FLPower = (-gamepad1.right_stick_x);
            BRPower = (gamepad1.right_stick_x);
            BLPower = (-gamepad1.right_stick_x);
        }


        //Clip Range, 100% power
        FRPower = Range.clip(FRPower,-1.0,1.0);
        FLPower = Range.clip(FLPower,-1.0,1.0);
        BLPower = Range.clip(BLPower,-1.0,1.0);
        BRPower = Range.clip(BRPower,-1.0,1.0);

        // Send calculated power to wheels
        leftFront.setPower(FLPower);
        rightFront.setPower(FRPower);
        leftBack.setPower(BLPower);
        rightBack.setPower(BRPower);

        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "left (%.2f), right (%.2f)", FLPower, FRPower);
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {

    }

}
