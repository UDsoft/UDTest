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
public class ThreadDataAnalyser {
    
    private final String dataSeparator;
    private final int DATASIZE = 2;
    
    private String[] dataArray;
    
    public ThreadDataAnalyser(String dataSeparator){
        this.dataSeparator = dataSeparator;
        
    }
    
    public void setData(String data){
        String[] temp = dataSplit(data);
        if(temp.length == DATASIZE){
            dataArray = temp;
        }else{
            dataArray[0] = "Error";
            dataArray[1] = "Error";
        }
        
    }
    
    private String[] dataSplit(String data){
        return data.split(dataSeparator);
        
    }
    
    public String getID(){
        return dataArray[0];
    }
    
    public int getdata(){
        return Integer.parseInt(dataArray[1]);
    }
    
}
