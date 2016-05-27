/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uddrive;

import com.pi4j.wiringpi.SoftPwm;

/**
 *
 * @author darwin
 */
public class SoftwarePWM {
    
    private final int pin;
    private final int min;
    private final int max;

    public SoftwarePWM(int pinNummer,int minValue,int maxValue) {
        this.pin = pinNummer;
        this.min = minValue;
        this.max = maxValue;
        SoftPwm.softPwmCreate(pin, min, max);
    }
    
    private void valueSetting(int value){
        SoftPwm.softPwmWrite(pin,value );
    }
    
    public void setValue(int value){
        valueSetting(value);
    }
    
    
    
}
