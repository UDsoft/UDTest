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


/**
 *
 * @author darwin
 */
public class MainDataAnalyser{
    
    
     //alle Notwediger Parameter zu initializierung
    private final String dataSeparator; 
    private final String idSeparator; // wo wird die daten enden
    private final int idPosition ;
    private final int commandPosition;
    private final int speedPosition;
    private final int steeringPosition;
    private final String finalDataConnector;
    private final int finalDataArrayBlock;  // in a case of error the data CurrentdataArray will hv less or more array
                                      // than expected .. this will be a checking if the data recieved is a
                                      // clean data or a corrupted on. arrays are counted from 0
    private final boolean isSplitEqualConnector;

    private String id = " ";
    private String data;
    private String[] currentArrayData;

    //DataAnalyse's Constructot

    //einfach Konstruktor
    public MainDataAnalyser(String dataSeparator,String idSeparator,int idPosition,
            int commandPosition,int speedPosition,int steeringPosition,
            boolean isSplitEqualConnector, int finalDataArrayBlock){
        this.dataSeparator = dataSeparator;
        this.idSeparator = idSeparator;
        this.idPosition = idPosition;
        this.commandPosition = commandPosition;
        this.speedPosition = speedPosition;
        this.steeringPosition = steeringPosition;
        this.finalDataConnector = dataSeparator;
        this.isSplitEqualConnector = isSplitEqualConnector;
        this.finalDataArrayBlock = finalDataArrayBlock;

    }

    public void setData(String data){
        this.data = data;
        runAnalyseData();
    }

    private void runAnalyseData(){
        currentArrayData = dataSplit();
        idSplit();
        

    }

    //Trennen die Daten in Array und Array erste position ist 0
    private String[] dataSplit(){
        String[] arraydata = data.split(dataSeparator);
        return arraydata;
    }

    // Trennen die IDposition um die ID nummer zu kriegen
    private void idSplit(){
        String IDLocation = currentArrayData[idPosition];
        String[] temp_array = IDLocation.split(idSeparator);
        id = temp_array[1];
    }

    public String getID(){
        return id;
    }
    
    public String getCommand(){
        String command = currentArrayData[commandPosition];
        return command;
    }
    
    
    /**
     * checks if the data received and the expected data length received is same
     * if data is more or less then then expected length of array data is corrupted
     * @return 
     */
    public boolean isDataValid(){
        boolean valid = false;
        if(currentArrayData.length == finalDataArrayBlock){
            valid = true;
        }
        return valid;
    }
    
    /**
     * 
     * @return String of speed
     */
    
    private String getSpeed(){
        String temp = currentArrayData[speedPosition];
        return  temp;
    }
    
        private String getSteering(){
        String temp = currentArrayData[steeringPosition];
        return  temp;
    }
    
    public int getSpeedInt(){
         int speed = Integer.parseInt(getSpeed());
         return speed;
    }
    
    public int getSteeringInt(){
        int steering = Integer.parseInt(getSteering());
        return steering;
    }
    
    public double getSpeedDouble(){
         Double speed = Double.parseDouble(getSpeed());
         return speed;
    }
    
    public double getSteeringDouble(){
        double steering = Double.parseDouble(getSteering());
        return steering;
    }

    
    
}
