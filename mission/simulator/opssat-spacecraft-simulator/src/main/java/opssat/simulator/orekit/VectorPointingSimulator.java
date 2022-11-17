/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2021      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 *  You may not use this file except in compliance with the License.
 *
 *  Except as expressly set forth in this License, the Software is provided to
 *  You on an "as is" basis and without warranties of any kind, including without
 *  limitation merchantability, fitness for a particular purpose, absence of
 *  defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ----------------------------------------------------------------------------
 */
package opssat.simulator.orekit;

import org.hipparchus.RealFieldElement;
import org.hipparchus.geometry.euclidean.threed.Rotation;
import org.hipparchus.geometry.euclidean.threed.RotationConvention;
import org.hipparchus.geometry.euclidean.threed.RotationOrder;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.orekit.attitudes.Attitude;
import org.orekit.attitudes.AttitudeProvider;
import org.orekit.attitudes.FieldAttitude;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.propagation.SpacecraftState;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.FieldAbsoluteDate;
import org.orekit.utils.FieldPVCoordinatesProvider;
import org.orekit.utils.PVCoordinatesProvider;

/**
 * for more information see AutonomousADCSOPSSATAdapter VectorPointing Mode in NMF-OPSSAT
 *
 * @author Kevin Otto
 */
public class VectorPointingSimulator implements AttitudeProvider {

    Attitude lastState;

    boolean isHoldingPosition = false;
    private float margin;

    Vector3D targetVec;

    boolean isX = false;
    boolean isY = false;
    boolean isZ = false;

    private enum AXIS {
        X, Y, Z
    }

    public VectorPointingSimulator() {
    }

    /**
     * starts vector pointing mode requires a target vector in ICRF
     *
     * @param x      x component of vector
     * @param y      y component of vector
     * @param z      z component of vector
     * @param margin angle that the real vector is allowed to differ from target (WARNING small values
     *               might lead to jitter)
     */
    public void start(float x, float y, float z, float margin) {
        this.targetVec = new Vector3D(x, y, z);

        // init holder
        this.margin = margin;
        this.isHoldingPosition = true;
        this.isX = true;
        this.isY = false;
        this.isZ = false;
    }

    public void stop() {
        isHoldingPosition = false;
    }

    @Override
    public Attitude getAttitude(PVCoordinatesProvider pvProv, AbsoluteDate date, Frame frame) {
        if (isHoldingPosition) {
            return angleStep(date, frame);
        }
        return new Attitude(date, frame, this.lastState.withReferenceFrame(frame).getOrientation().getRotation(),
            Vector3D.MINUS_I, Vector3D.ZERO);
    }

    @Override
    public <T extends RealFieldElement<T>> FieldAttitude<T> getAttitude(FieldPVCoordinatesProvider<T> pvProv,
        FieldAbsoluteDate<T> date, Frame frame) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void update(SpacecraftState newState) {
        lastState = newState.getAttitude();
    }

    private Attitude angleStep(AbsoluteDate date, Frame frame) {
        Rotation currentRotation = this.lastState.withReferenceFrame(FramesFactory.getICRF()).getRotation();

        Attitude result;

        Vector3D diff = new Vector3D(new Rotation(new Vector3D(0, 0, -1), currentRotation.applyInverseTo(targetVec))
            .getAngles(RotationOrder.XYZ, RotationConvention.VECTOR_OPERATOR));

        if (isX) {
            result = new Attitude(date, frame, angleStepAxis(currentRotation, diff.getX() * 0.001, AXIS.X),
                Vector3D.ZERO, Vector3D.ZERO);
        } else if (isY) {
            result = new Attitude(date, frame, angleStepAxis(currentRotation, diff.getY() * 0.001, AXIS.Y),
                Vector3D.ZERO, Vector3D.ZERO);
        } else if (isZ) {
            result = new Attitude(date, frame, angleStepAxis(currentRotation, diff.getZ() * 0.001, AXIS.Z),
                Vector3D.ZERO, Vector3D.ZERO);
        } else {
            result = new Attitude(date, frame, currentRotation, Vector3D.ZERO, Vector3D.ZERO);
        }

        // 0.1 to make debugging easier, because default margin is 0
        if (isX && Math.abs(FastMath.toDegrees(diff.getX())) <= this.margin + 0.01) {
            isX = false;
            isY = true;
        }

        if (isY && Math.abs(FastMath.toDegrees(diff.getY())) <= this.margin + 0.01) {
            isY = false;
            isZ = true;
        }

        if (isZ && Math.abs(FastMath.toDegrees(diff.getZ())) <= this.margin + 0.01) {
            isZ = false;
        }

        this.lastState = result;
        return result.withReferenceFrame(frame);
    }

    /**
     * rotates the given Rotation Object around the given axis (in spacecraft frame) by the given
     * angle.
     *
     * @param currentRot the Rotation Object to rotate
     * @param angle      the angle of the rotation
     * @param axis       the axis of the rotation in Spacecraft Frame.
     * @return
     */
    Rotation angleStepAxis(Rotation currentRot, double angle, AXIS axis) {
        // transforms the axis from spacecraft frame into the frame of the given rotation, than uses it as rotaion axis
        switch (axis) {
            case X:
                return new Rotation(currentRot.applyTo(Vector3D.PLUS_I), angle, RotationConvention.VECTOR_OPERATOR)
                    .applyTo(currentRot);
            case Y:
                return new Rotation(currentRot.applyTo(Vector3D.PLUS_J), angle, RotationConvention.VECTOR_OPERATOR)
                    .applyTo(currentRot);
            case Z:
                return new Rotation(currentRot.applyTo(Vector3D.PLUS_K), angle, RotationConvention.VECTOR_OPERATOR)
                    .applyTo(currentRot);
        }
        return null;
    }

}
