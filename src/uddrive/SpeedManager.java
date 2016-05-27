/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uddrive;


import iot.DateTime;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author darwin
 */
public class SpeedManager implements Runnable {

    private final BlockingQueue queue;
    //Condition 
    private boolean inStatic;      //not Moving 
    private boolean isMovingForward;// is moving Forward direction.

    //store value
    private int previousSpeed;

    DateTime dateTime = new DateTime();

    ThreadDataAnalyser dataAnalyser = new ThreadDataAnalyser(":");
    
    SoftwarePWM forward = new SoftwarePWM(1, 0, 100);
    SoftwarePWM reverse = new SoftwarePWM(23, 0, 100);

    //The current ID in process
    String ID;

    //Contructor for this class.
    public SpeedManager(BlockingQueue q) {
        this.queue = q;
    }

    /**
     * initialize the Logic function.
     */
    private void init() {
        inStatic = true;
        isMovingForward = false;
        previousSpeed = 0;
    }

    private void inAction() {
        int speed = queueDataOut();
        Logic(speed);
    }

    /**
     *
     * @return
     */
    private int queueDataOut() {
        int speed = 0;
        String data;

        try {
            data = (String) queue.take();
            dataAnalyser.setData(data);
            speed = dataAnalyser.getdata();
            ID = dataAnalyser.getID();

        } catch (InterruptedException ex) {
            Logger.getLogger(SpeedManager.class.getName()).
                    log(Level.SEVERE, null, ex);
            System.out.println("Error in getDataFromBlockingQueue function");
        }

        return speed;
    }

    private void Brake() {

        System.out.println("Brake active by ID " + ID + " at " + dateTime.getTimeFormated());

        inStatic = true;
        /**
         * forward and backward is set to zero so that the the previous PWM
         * value is not stored in the respective pin. IF this is not taken care
         * , there will be in accuracy in the action given to the car.
         */
        forward.setValue(0);
        reverse.setValue(0);

        /**
         * Thread is set to be inactive for 2 seconds for the complete braking
         * to take place the time is due to how the hardware works and giving
         * the time to be execute b4 adding more data to be execute while the
         * hardware haven't fully done the job given.
         */
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(SpeedManager.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        previousSpeed = 0;
    }

    private void Forward(int speed) {
        System.out.println("Forward active by ID " + ID + " at " + dateTime.getTimeFormated());
        forward.setValue(speed);
        isMovingForward = true;
        inStatic = false;
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(SpeedManager.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        previousSpeed = speed;

    }

    private void Reverse(int speed) {
        System.out.println("Reverse active by ID " + ID + " at " + dateTime.getTimeFormated());
        reverse.setValue(speed);
        isMovingForward = false;
        inStatic = false;
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(SpeedManager.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        previousSpeed = speed;
    }

    /**
     * This MainLogic is the main Brain for all the logic decision. The rules of
     * the condition is 1. Braking takes 2 second interval to fully executed 2.
     * Only after the fully braked the another condition of forward or Reverse
     * can be executed.. 3. While braking the steering is still free to function
     * 4. For Steering the only one condition need to be checked isTurningRight.
     * If the condition to be changed the previous active GPIO should be set to
     * 0 and activate the New GPIO.
     *
     * @param speed positive if forward , negative if reverse.
     * @param steering positive if Right, negative if Left
     */
    private void Logic(int speed) {

        /**
         * Condition of Speed 1.Check if the car in static or non Static
         * condition. 2.If in static , check if the speed is positive or
         * Negative - Positive value will trigger Forward Motion; - Negative
         * value will trigger Reverse Motion; 3.If car in a motion , check if
         * the previous Value of speed is equal to current speed. --> If it is
         * same do not change anything.
         *
         * 3.1 If the value is not equal then check for direction of the Motion
         * --->>If car is in Forward Motion. --> check if the current speed is
         * positive or negative. --> If it is Positive then Trigger Forward
         * Motion with the speed; --> If it is negative then Trigger Brake ;
         *
         * -->>If car is in Backward Motion. --> Check if the current speed is
         * positive or negative. --> If it it Negative then Trigger Backward
         * Motion with the speed; --> If it is Positive then Trigger Brake;
         *
         * 4. Save the current speed as a new value for previous speed.
         *
         */
        if (inStatic) {
            if (speed > 0) {
                Forward(speed);
            } else if (speed < 0) {
                Reverse(-speed);
            } else {
                System.out.println("Auto Noch in Ruheby ID " + ID + " at " + dateTime.getTimeFormated());
            }

        } else /**
         * Code to execute if the Car already in non Static Condition
         */
        {
            if (previousSpeed != speed) {
                if (isMovingForward) {
                    /**
                     * Code to execute if the car is already in Forward motion
                     */
                    if (speed > 0) {
                        Forward(speed);
                    } else {
                        Brake();
                    }
                } else /**
                 * Code to execute if the car is already in Reverse Motion
                 */
                {
                    if (speed < 0) {
                        Reverse(-speed);
                    } else {
                        Brake();
                    }
                }
            }
        }

    }

    @Override
    public void run() {
        System.out.println("Started Speed Thread");
        init();
        while (true) {
            inAction();
        }
    }
}
