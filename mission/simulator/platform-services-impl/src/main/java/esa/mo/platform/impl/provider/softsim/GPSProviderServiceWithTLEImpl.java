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
package esa.mo.platform.impl.provider.softsim;

import esa.mo.helpertools.misc.Const;
import esa.mo.nmf.sdk.OrekitResources;
import esa.mo.platform.impl.provider.gen.GPSProviderServiceImpl;
import esa.mo.platform.impl.util.HelperGPS;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.platform.PlatformHelper;
import org.ccsds.moims.mo.platform.gps.provider.GetTLEInteraction;
import org.ccsds.moims.mo.platform.gps.structures.Position;
import org.ccsds.moims.mo.platform.gps.structures.PositionExtraDetails;
import org.ccsds.moims.mo.platform.gps.structures.PositionSourceType;
import org.ccsds.moims.mo.platform.gps.structures.TwoLineElementSet;
import org.ccsds.moims.mo.platform.structures.VectorD3D;
import org.ccsds.moims.mo.platform.structures.VectorF3D;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.data.DataProvidersManager;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;

/**
 * GPS service Provider with TLE support.
 */
public class GPSProviderServiceWithTLEImpl extends GPSProviderServiceImpl {

  private final Boolean isTLEFallbackEnabled =
          Boolean.parseBoolean(System.getProperty(Const.PLATFORM_GNSS_FALLBACK_TO_TLE_PROPERTY,
          Const.PLATFORM_GNSS_FALLBACK_TO_TLE_DEFAULT));

  private static boolean isOrekitDataInitialized = false;
  
  private final GPSSoftSimAdapter adapterCast = (GPSSoftSimAdapter) adapter;
    
  /**
   * Updates the current position/velocity
   * @param useTLEpropagation
   */
  @Override
  public void updateCurrentPositionAndVelocity(boolean useTLEpropagation)
    throws IOException, NumberFormatException
  {
    try {
      final VectorD3D position;
      final VectorF3D positionDeviation;
      final VectorD3D velocity;
      final VectorF3D velocityDeviation;

      if(useTLEpropagation)
      {
        Calendar targetDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        SpacecraftState state = getSpacecraftState(targetDate);
        Vector3D pos = state.getPVCoordinates().getPosition();
        position = new VectorD3D(pos.getX(), pos.getY(), pos.getZ());

        positionDeviation = new VectorF3D(0f, 0f, 0f);

        Vector3D velocity3D = state.getPVCoordinates().getVelocity();
        velocity = new VectorD3D(velocity3D.getX(), velocity3D.getY(), velocity3D.getZ());

        velocityDeviation = new VectorF3D(0f, 0f, 0f);
      }
      else {
        String bestxyz = adapter.getBestXYZSentence();

        String[] fields = HelperGPS.getDataFieldsFromBestXYZ(bestxyz);

        position = new VectorD3D(
                Double.parseDouble(fields[HelperGPS.BESTXYZ_FIELD.PX]),
                Double.parseDouble(fields[HelperGPS.BESTXYZ_FIELD.PY]),
                Double.parseDouble(fields[HelperGPS.BESTXYZ_FIELD.PZ])
        );

        positionDeviation = new VectorF3D(
                Float.parseFloat(fields[HelperGPS.BESTXYZ_FIELD.PX_DEVIATION]),
                Float.parseFloat(fields[HelperGPS.BESTXYZ_FIELD.PY_DEVIATION]),
                Float.parseFloat(fields[HelperGPS.BESTXYZ_FIELD.PZ_DEVIATION])
        );

        velocity = new VectorD3D(
                Double.parseDouble(fields[HelperGPS.BESTXYZ_FIELD.VX]),
                Double.parseDouble(fields[HelperGPS.BESTXYZ_FIELD.VY]),
                Double.parseDouble(fields[HelperGPS.BESTXYZ_FIELD.VZ])
        );

        velocityDeviation = new VectorF3D(
                Float.parseFloat(fields[HelperGPS.BESTXYZ_FIELD.VX_DEVIATION]),
                Float.parseFloat(fields[HelperGPS.BESTXYZ_FIELD.VY_DEVIATION]),
                Float.parseFloat(fields[HelperGPS.BESTXYZ_FIELD.VZ_DEVIATION])
        );
      }
      synchronized (MUTEX) { // Store the latest Position
        currentCartesianPosition = position;
        currentCartesianPositionDeviation = positionDeviation;
        currentCartesianVelocity = velocity;
        currentCartesianVelocityDeviation = velocityDeviation;
        timeOfCurrentPositionAndVelocity = System.currentTimeMillis();
      }
    } catch (IOException | NumberFormatException e) {
      e.printStackTrace();
      throw e;
    }
  }

  @Override
  public void getTLE(GetTLEInteraction interaction) throws MALInteractionException, MALException
  {
    if (!adapter.isUnitAvailable() && isTLEFallbackEnabled == false) { // Is the unit available?
      throw new MALInteractionException(
          new MALStandardError(PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER, null));
    }
    interaction.sendAcknowledgement();
    TLE tle = adapterCast.getTLE();

    interaction.sendResponse(new TwoLineElementSet(tle.getSatelliteNumber(), "" + tle.getClassification(),
            tle.getLaunchYear(), tle.getLaunchNumber(), tle.getLaunchPiece(),
            tle.getDate().getComponents(0).getDate().getYear(),
            tle.getDate().getComponents(0).getDate().getDayOfYear(),
            tle.getDate().getComponents(0).getTime().getSecondsInUTCDay(),
            tle.getMeanMotionFirstDerivative(), tle.getMeanMotionSecondDerivative(),
            tle.getBStar(), tle.getElementNumber(), tle.getI(), tle.getRaan(), tle.getE(),
            tle.getPerigeeArgument(), tle.getMeanAnomaly(), tle.getMeanMotion(),
            tle.getRevolutionNumberAtEpoch()));
  }

  private Position getTLEPropagatedPosition()
  {
    Calendar targetDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    SpacecraftState state = getSpacecraftState(targetDate);

    // Converting to geodetic lat/lon/alt
    Frame ecf = FramesFactory.getITRF(IERSConventions.IERS_2010,true);
    OneAxisEllipsoid earth = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS, Constants.WGS84_EARTH_FLATTENING, ecf);
    GeodeticPoint satLatLonAlt = earth.transform(state.getPVCoordinates().getPosition(), FramesFactory.getEME2000(),
            state.getDate());

    PositionExtraDetails extraDetails = new PositionExtraDetails(new Time(targetDate.getTimeInMillis()),
            0, 0, 0.0f,0.0f, PositionSourceType.TLE);

    return new Position((float) FastMath.toDegrees(satLatLonAlt.getLatitude()), (float) FastMath.toDegrees(satLatLonAlt.getLongitude()),
            (float) satLatLonAlt.getAltitude(), extraDetails);
  }

  private SpacecraftState getSpacecraftState(Calendar targetDate) {

    if(!isOrekitDataInitialized) {
      //setup orekit if not yet initialized
      DataProvidersManager manager = DataProvidersManager.getInstance();
      if(manager.getProviders().isEmpty())
      {
        manager.addProvider(OrekitResources.getOrekitData());
      }
      isOrekitDataInitialized = true;
    }

    TLE tle = adapterCast.getTLE();
    TLEPropagator propagator = TLEPropagator.selectExtrapolator(tle);

    return propagator.propagate(new AbsoluteDate(targetDate.get(Calendar.YEAR),
            targetDate.get(Calendar.MONTH) + 1,
            targetDate.get(Calendar.DAY_OF_MONTH), targetDate.get(Calendar.HOUR_OF_DAY), targetDate.get(Calendar.MINUTE),
            targetDate.get(Calendar.SECOND), TimeScalesFactory.getUTC()));
  }    

  /**
   * Checks if TLE propagation should be used
   * @return true if TLE propagation should be used, false otherwise
   */
  @Override
  public boolean useTLEPropagation() throws MALInteractionException, MALException {
    boolean useTLEpropagation = false;
    if (!adapter.isUnitAvailable()) {
        if(isTLEFallbackEnabled) {
            useTLEpropagation = true;
        } else {
            throw new MALInteractionException(
                new MALStandardError(PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER, null)); 
        }
    }
    else if(!isPositionFixed()){
      useTLEpropagation = true;
    }
    return useTLEpropagation;
  }

  /**
   * Updates the current position using TLE if useTLEpropagation is true,
   * or the GPS adapter if useTLEpropagation is false
   * @param useTLEpropagation
   * @return the updated position, or null if the methods that get latest position fail
   */
  @Override
  public Position updateCurrentPosition(boolean useTLEpropagation)
  {
    Position position = useTLEpropagation ? getTLEPropagatedPosition() : adapter.getCurrentPosition();
    if (position == null) {
      return null;
    }

    synchronized (MUTEX) { // Store the latest Position
      currentPosition = position;
      timeOfCurrentPosition = System.currentTimeMillis();
    }
    return position;
  }

  /**
   * Checks the fixQquality on the GNSS position.
   * @return true if a fixed position has been established, false otherwise
   */
  private boolean isPositionFixed() {
    boolean isFixed = false;
    try {
      if(adapter.getCurrentPosition().getExtraDetails().getFixQuality() > 0){
        isFixed = true;
      }
    } catch (NullPointerException e)
    {
      LOGGER.warning("Could not receive a fixed position: " + e.getMessage());
    }
    return isFixed;
  }
  
}
