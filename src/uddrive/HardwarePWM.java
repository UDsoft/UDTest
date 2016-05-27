/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uddrive;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;

/**
 *
 * @author darwin
 */
public class HardwarePWM {

    
    private final GpioPinPwmOutput pwm;
            
    public HardwarePWM(GpioController gpio, Pin pin) {
        this.pwm = gpio.provisionPwmOutputPin(pin); 
    }
    
    private void valueSetting(int value){
        pwm.setPwm(value);
    }
    
    public void setPwmValue(int value){
        valueSetting(value);
    }

}
