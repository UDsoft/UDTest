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
package UDDrive.mode;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

/**
 *
 * @author darwin
 */
public class BackwardThread implements Runnable{

    private final Pin forwardPin = RaspiPin.GPIO_26;

    private final GpioController gpio = GpioFactory.getInstance();

    private final GpioPinPwmOutput forward = gpio.provisionPwmOutputPin(forwardPin);;
            
    String id;
    int speed;


    public BackwardThread(String id, int speed) {
        this.id = id;
        this.speed = speed;
    }

    private boolean ForwardAction(int speed) {
        boolean isDone = false;
        forward.setPwm(speed);
   
        return isDone;
    }
    

    @Override
    public void run() {
        ForwardAction(speed);
        Publish(id);
        gpio.shutdown();
    }

    private void Publish(String id) {
        System.out.println("Backward Thread is set by ID " + id);
    }
}
