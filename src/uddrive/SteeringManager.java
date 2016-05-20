/*
 * Copyright 2016 darwin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uddrive;


import iot.DateTime;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author darwin
 */
public class SteeringManager implements Runnable {

    //Data collection 
//    private final BlockingQueue<String> dataqueue
//            = new LinkedBlockingQueue<String>();
    
    private final BlockingQueue queue;

    /**
     * Private Variable to make logical decision with the condition of the car.
     */
    private boolean isTurningRight;
    private boolean isSteeringStraight;
    private int previousSteering;
    
    ThreadDataAnalyser dataAnalyser = new ThreadDataAnalyser(":");
    DateTime time = new DateTime();
    
    //The current ID in process
    String ID;
//    long startTime;
//    long endTime;


    private final GpioController gpio = GpioFactory.getInstance();

    private final Pin leftSteeringPin = RaspiPin.GPIO_23;
    private final Pin rightSteeringPin = RaspiPin.GPIO_24;

    private final GpioPinPwmOutput leftSteering
            = gpio.provisionPwmOutputPin(leftSteeringPin);
    private final GpioPinPwmOutput rightSteering
            = gpio.provisionPwmOutputPin(rightSteeringPin);
    
    public SteeringManager(BlockingQueue q){
        this.queue = q;
        init();
    }

    private void init() {
        this.isTurningRight = false;
        this.isSteeringStraight = true;
        previousSteering = 0;

    }

    private void inAction() {
        int steering = queueDataOut();
        Logic(steering);
    }



    /**
     *
     * @return
     */
    private int queueDataOut() {
        int steering = 0;
        String data;

        try {
            data =  (String) queue.take();
            dataAnalyser.setData(data);
            steering = dataAnalyser.getdata();
            ID = dataAnalyser.getID();
            
        } catch (InterruptedException ex) {
            Logger.getLogger(SteeringManager.class.getName()).
                    log(Level.SEVERE, null, ex);
            System.out.println("Error in getDataFromBlockingQueue function");
        }

        return steering;
    }

    /**
     *
     * @param steering
     */
    private void TurnLeft(int steering) {
        
        System.out.println("Turning Left active by ID " + ID + " at " + time.getTimeFormated());
        
        leftSteering.setPwm(steering);
        isTurningRight = false;
        isSteeringStraight = false;
        try {
            Thread.sleep(300);
        } catch (InterruptedException ex) {
            Logger.getLogger(SteeringManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        previousSteering = steering;
    }

    /**
     *
     * @param steering
     */
    private void TurnRight(int steering) {
        
        System.out.println("Turning Right active by ID " + ID +" at "+ time.getTimeFormated());
        
        rightSteering.setPwm(steering);
        isTurningRight = true;
        isSteeringStraight = false;
        try {
            Thread.sleep(300);
        } catch (InterruptedException ex) {
            Logger.getLogger(SteeringManager.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        
        previousSteering = steering;

    }

    /**
     *
     */
    private void SteeringStraight() {
        
        System.out.println("Steering is straight by ID " + ID + " at " + time.getTimeFormated());
        
        leftSteering.setPwm(0);
        rightSteering.setPwm(0);
        isSteeringStraight = true;
        isTurningRight = false;
        try {
            Thread.sleep(300);
        } catch (InterruptedException ex) {
            Logger.getLogger(SteeringManager.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        previousSteering = 0;
    }

    /**
     *
     * @param lengkung
     */
    private void Logic(int steering) {
        /**
         * Condition of Steering 1. Check if the previous steering Value is not
         * equal to steering. 2. Check if the steering value is positive or
         * negative. 3. Check if the Steering condition was in left or right
         * Turning. 4. -->
         */
        
        if (previousSteering != steering) {
            
            //Checking is the steeering is straight
            if (isSteeringStraight) {
                
                //if the steering data is more than 0 then turn right
                if (steering > 0) {
                    TurnRight(steering);
                    
                } else if (steering < 0) {
                    //if the steeering data is less than 0 then turn left
                    TurnLeft(-steering);
                } else {
                    System.out.println("Steering straight by ID " + ID);
                }
                
            } // if the steering was not straight so check if it is turning right or ledt 
            else if (isTurningRight) {
                //if it is turning right then check if the steering data is more than 0 to make more or less turning to right.
                if (steering > 0) {
                    TurnRight(steering);
                } else 
//                    if (steering <= 0)
                {
                    //if the steering data is less or equal to zero then just make it straight. 
                    SteeringStraight();
                }
            }//if the steering is not straight and not turning right then check if the steering data is more or less than zero
            else if (steering < 0) {
                //if it is more than zero then make it straight.
                TurnLeft(-steering);
            }else {
                // if the steering is not equal or more than  zero then it turn left 
                SteeringStraight();
            }
        }
    }

    @Override
    public void run() {
        System.out.println("Started SteeringManager Thread");
        init();
        while(true){
        inAction();
        }
    }
    
   
    
}
