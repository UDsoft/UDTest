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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import iot.TcpServer;
import java.util.logging.Logger;


/**
 *
 * @author darwin
 */
public class UDDrive {
    

    public static void main(String[] args) {

        /**
         * USEFUL Initialize
         */
        int port = 8000;
        
        int factor = 100;

        /**
         * This BlockingQueue will be the storage point for all the data coming
         * from the server and to be taken by the appropriate Threads.
         */
        BlockingQueue speedQueue = new ArrayBlockingQueue(1);
        BlockingQueue steeringQueue = new ArrayBlockingQueue(1);
      
        DateTime time = new DateTime();

     
        SteeringManager steerManager = new SteeringManager(steeringQueue);
        SpeedManager speedManager = new SpeedManager(speedQueue);

        MainDataAnalyser analyse = new MainDataAnalyser(":", "=", 0, 1, 2, 3, true, 4);

    
        Thread l = new Thread(steerManager);
        Thread b = new Thread(speedManager);

        TcpServer server = new TcpServer(port);

        l.start();
        b.start();

        boolean noClient = false;

        while (server.isServerInitalized()) {
            if (server.isClientConnected()) {
                String data = server.readClient();
                if (!data.equals("DISCONNECTED")) {
                    System.out.println(data);
                    analyse.setData(data);
                    if (analyse.getCommand().equals("MC")) {
                        int steering = analyse.getSteeringInt()*factor;
                        int speed = analyse.getSpeedInt();
                        String ID = analyse.getID();
                        String speedData = ID + ":" + speed;
                        String steeringData = ID + ":" + steering;
//                        System.out.println("This is the current speed :" + speed);
//                        System.out.println("This is the current steering :" + steering);
                        
                            boolean isSpeedAccepted =  speedQueue.offer(speedData);
                            if(!isSpeedAccepted){
                                speedQueue.clear();
                                System.out.println("Clearing Queue Speed at "+ time.getTimeFormated());
                            }
                            boolean isSteeringAccepted = steeringQueue.offer(steeringData);
                            if(!isSteeringAccepted){
                                steeringQueue.clear();;
                                System.out.println("Clearing Queue Steeering at"+ time.getTimeFormated());
                            }
                  

                    } else {
                        double steering = analyse.getSteeringDouble();
                        double speed = analyse.getSpeedDouble();
                        System.out.println("This is the current speed :" + speed);
                        System.out.println("This is the current steering :" + steering);
                    }
                } else {
                    noClient = true;
                }
            }

            if (noClient) {
                server.waitClient();
                noClient = false;
            }
        }

        System.out.println("The Server is Shutting Down");
        try {
            l.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(UDDrive.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            b.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(UDDrive.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    

}
