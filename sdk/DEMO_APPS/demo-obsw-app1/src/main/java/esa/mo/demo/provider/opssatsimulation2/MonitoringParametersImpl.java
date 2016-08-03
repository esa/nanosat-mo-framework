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
package esa.mo.demo.provider.opssatsimulation2;

import esa.mo.mc.impl.interfaces.ParameterStatusListener;
import esa.mo.helpertools.helpers.HelperAttributes;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import opssat.simulator.InstrumentsSimulator;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.Union;

/**
 *
 * @author Cesar Coelho
 */
public class MonitoringParametersImpl implements ParameterStatusListener {

    private final InstrumentsSimulator app;
    
    public enum Parameter_enum {
        GPS_LATITUDE, GPS_LONGITUDE, GPS_ALTITUDE, GPS_TIME,
        CLOCK, FINEADCS_MAG_B_R, FINEADCS_MAG_B_THETA,
        ZERO, ONE, TWO, THREE, FOUR,
        STRING, MODE, TEMPERATURE, PICTURE
    }
    
    public MonitoringParametersImpl (InstrumentsSimulator application){
        app = application;
    }

    public Parameter_enum getParameterEnum(Identifier identifier){
        
        final String name = identifier.toString();

        if (name == null)
            return null;
        
        switch (name) {
            case "GPS.Latitude":                    return Parameter_enum.GPS_LATITUDE;
            case "GPS.Longitude":                   return Parameter_enum.GPS_LONGITUDE;
            case "GPS.Altitude":                    return Parameter_enum.GPS_ALTITUDE;
            case "GPS.Time":                        return Parameter_enum.GPS_TIME;
            case "Clock":                           return Parameter_enum.CLOCK;
            case "FineADCS.Magnetometer.B_r":       return Parameter_enum.FINEADCS_MAG_B_R;
            case "FineADCS.Magnetometer.B_theta":   return Parameter_enum.FINEADCS_MAG_B_THETA;
            case "Zero":                            return Parameter_enum.ZERO;
            case "One":                             return Parameter_enum.ONE;
            case "Two":                             return Parameter_enum.TWO;
            case "Three":                           return Parameter_enum.THREE;
            case "Four":                            return Parameter_enum.FOUR;
            case "String":                          return Parameter_enum.STRING;
            case "Mode":                            return Parameter_enum.MODE;
            case "Temperature":                     return Parameter_enum.TEMPERATURE;
            case "Picture":                         return Parameter_enum.PICTURE;

            default:                                return null;  // Parameter not found
        }
        
    }
    
    public Boolean exists(Identifier indentifier){
        return (this.getParameterEnum(indentifier) != null);
    }
    
    @Override
    public Attribute onGetValue(Identifier identifier, Byte rawType) {
        
        final Parameter_enum parameter_enum = this.getParameterEnum(identifier);
        
        if (parameter_enum == null)
            return null;
        
        switch (parameter_enum) {
            case GPS_LATITUDE:                      return new Union(app.getGPSlatitude());
            case GPS_LONGITUDE:                     return new Union(app.getGPSlongitude());
            case GPS_ALTITUDE:                      return new Union(app.getGPSaltitude());
            case GPS_TIME:                          return new Union(app.getGPStime().toEpochSecond(ZoneOffset.UTC));
            case CLOCK:                             return new Union(LocalDateTime.now().toLocalTime().toString());
            case FINEADCS_MAG_B_R:                  return new Union(app.getFineADCSmagnetometerBr());
            case FINEADCS_MAG_B_THETA:              return new Union(app.getFineADCSmagnetometerBtheta());
            case ZERO:                              return new Union(0);
            case ONE:                               return new Union(1);
            case TWO:                               return new Union(2);
            case THREE:                             return new Union(3);
            case FOUR:                              return new Union(4);
            case STRING:                            return new Union("This is a String!");
            case MODE:                              return new Union(app.getMode());
            case TEMPERATURE:                       return new Union(app.getTemperature());
            case PICTURE:                           return new Blob(app.getPicture());

            default:                                return null;  // Parameter not found
        }
    }

    @Override
    public Boolean onSetValue(Identifier identifier, Attribute value) {

        final Parameter_enum parameter_enum = this.getParameterEnum(identifier);

        if (parameter_enum == null) // The supplied name does not exist
            return null;
        
        switch (parameter_enum) {
            case MODE:                              return app.setMode(((Double) Double.parseDouble(value.toString())).intValue());
            case TEMPERATURE:                       return app.setTemperature( HelperAttributes.attribute2double(value) );

            default:                                return false;  // Parameter not found
        }
    }
            
}
