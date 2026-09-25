/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
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
package esa.mo.platform.impl.provider.lite;

import esa.mo.platform.impl.provider.gen.GPSAdapterInterface;
import java.io.IOException;
import opssat.simulator.InstrumentsSimulator;
import opssat.simulator.OrbitParameters;
import opssat.simulator.Vector;
import org.ccsds.moims.mo.platform.structures.Position;
import org.ccsds.moims.mo.platform.structures.PositionExtraDetails;
import org.ccsds.moims.mo.platform.structures.SatelliteInfoList;

/**
 * The GPS of the lite simulator, which works the orbit out analytically and
 * reports where the spacecraft is.
 * <p>
 * The other adapters of the framework extend {@link
 * esa.mo.platform.impl.provider.gen.GPSNMEAonlyAdapter} and are given their
 * position by a receiver that talks NMEA, which the base class then parses.
 * This simulator has no receiver and formats no sentences, so the position is
 * taken from it directly and the operations that only a sentence can answer
 * report that they are not supported.
 */
public class GPSLiteAdapter implements GPSAdapterInterface {

    /**
     * The simulator reports the altitude in kilometres, while the Position of
     * the Platform services is in metres.
     */
    private static final float METRES_PER_KILOMETRE = 1000;

    private final InstrumentsSimulator simulator;

    /**
     * Constructor.
     *
     * @param simulator The simulator that works out the position.
     */
    public GPSLiteAdapter(InstrumentsSimulator simulator) {
        this.simulator = simulator;
    }

    @Override
    public boolean isUnitAvailable() {
        return true;
    }

    @Override
    public Position getCurrentPosition() {
        OrbitParameters parameters = simulator.getGPS().getPositionNow();
        return new Position((float) parameters.getLatitude(),
                (float) parameters.getLongitude(),
                (float) parameters.getAltitude() * METRES_PER_KILOMETRE,
                new PositionExtraDetails());
    }

    @Override
    public String getNMEASentence(final String identifier) throws IOException {
        throw new IOException(unsupported("NMEA sentences"));
    }

    @Override
    public String getBestXYZSentence() throws IOException {
        OrbitParameters parameters = simulator.getGPS().getPositionNow();
        Vector position = parameters.getPositionEarthFixed();
        Vector velocity = parameters.getVelocityEarthFixed();

        if (position == null || velocity == null) {
            throw new IOException("The simulator did not work the position out "
                    + "in the frame that turns with the Earth.");
        }

        // The receiver reports metres and metres per second; the simulator
        // works in kilometres.
        return bestXYZ(position.x() * METRES_PER_KILOMETRE,
                position.y() * METRES_PER_KILOMETRE,
                position.z() * METRES_PER_KILOMETRE,
                velocity.x() * METRES_PER_KILOMETRE,
                velocity.y() * METRES_PER_KILOMETRE,
                velocity.z() * METRES_PER_KILOMETRE);
    }

    /**
     * Writes a BESTXYZ sentence, which is how the Platform services are given a
     * position and a velocity together.
     * <p>
     * The deviations are zero: the simulator works the orbit out rather than
     * measuring it, so there is no error to report. The counts of satellites
     * are zero for the same reason.
     */
    private static String bestXYZ(double px, double py, double pz,
            double vx, double vy, double vz) {
        StringBuilder fields = new StringBuilder();
        fields.append("SOL_COMPUTED,NARROW_INT,");                       // position solution
        fields.append(px).append(',').append(py).append(',').append(pz).append(',');
        fields.append("0.0,0.0,0.0,");                                   // position deviation
        fields.append("SOL_COMPUTED,NARROW_INT,");                       // velocity solution
        fields.append(vx).append(',').append(vy).append(',').append(vz).append(',');
        fields.append("0.0,0.0,0.0,");                                   // velocity deviation
        fields.append(",0.000,0.000,0.000,0,0,0,0,0,0,0,0");             // station, ages, counts
        return "#BESTXYZA,COM1,0,0.0,FINESTEERING,0,0.000,00000000,0000,0;"
                + fields + "*00000000";
    }

    @Override
    public String getTIMEASentence() throws IOException {
        throw new IOException(unsupported("NMEA sentences"));
    }

    @Override
    public SatelliteInfoList getSatelliteInfoList() {
        // The satellites in view are read out of a sentence, and there is none.
        return new SatelliteInfoList();
    }

    private static String unsupported(String what) {
        return "The lite simulator does not produce " + what + ". It works the "
                + "orbit out analytically and reports the position of the "
                + "spacecraft; use nmf-mission-simulator-orekit for the rest.";
    }
}
