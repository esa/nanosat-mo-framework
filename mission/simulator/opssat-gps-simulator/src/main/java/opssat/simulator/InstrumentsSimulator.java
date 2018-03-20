/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 * ----------------------------------------------------------------------------
 */
package opssat.simulator;

import java.util.Date;

/**
 *
 * @author Cesar Coelho
 */
public class InstrumentsSimulator {

    private final Orbit darkDusk;
    private final GPS gps;
    private final FineADCS fineADCS;
    private final Camera camera;
    private int mode = 0;
    private double temperature = 0;
    private byte[] picture = null;
    
    /**
     * Constructor for the Instruments Simulator Block
     */
    public InstrumentsSimulator() {
        // TODO code application logic here
        
        // Values from the OPS-SAT document: a = 6371+650= 7021 km ; i = 98.05 deg  (orbital period: 1.63 hours)
        // (double a, double i, double RAAN, double arg_per, double true_anomaly)
        this.darkDusk = new Orbit ( 7021, 98.05*(Math.PI/180), (340)*(Math.PI/180), (0)*(Math.PI/180), 0);
        
        this.gps = new GPS(darkDusk);
        this.camera = new Camera();
        this.fineADCS = new FineADCS(darkDusk);
           
    }

    public void printRealPosition() {
        Orbit.OrbitParameters param = this.darkDusk.getParameters();
        System.out.printf("\n\nLatitude, Longitude: %f, %f\nAltitude: %f\nTime: %s\n\n\n", 
                param.getlatitude(), param.getlongitude(), param.geta(), param.gettime().toString());
    }

    public double getGPSlatitude(){
        return this.gps.getPosition().getlatitude();
    }

    public double getGPSaltitude(){
        return GPS.truncateDecimal(this.gps.getPosition().getGPSaltitude(), 1).doubleValue();
    }
    
    public double getGPSlongitude(){
        return this.gps.getPosition().getlongitude();
    }

    public Date getGPStime(){
        return this.gps.getPosition().gettime();
    }
    
    public double getFineADCSmagnetometerBr(){
        return fineADCS.getMagnetometer().getB_r();
    }

    public double getFineADCSmagnetometerBtheta(){
        return fineADCS.getMagnetometer().getB_theta();
    }
    
    public boolean setMode(int modeNumber){
        this.mode = modeNumber;
        return true;
    }

    public int getMode(){
        return this.mode;
    }
    
    public boolean setTemperature(double temperature){
        this.temperature = temperature;
        return true;
    }

    public double getTemperature(){
        return this.temperature;
    }

    public Boolean takePicture(int seconds){
        
        this.picture = this.camera.takePicture(seconds);

        return (this.picture != null);
    }
    
    public byte[] getPicture (){
        this.picture = this.camera.takePicture(0);
        return this.picture;
    }

}
