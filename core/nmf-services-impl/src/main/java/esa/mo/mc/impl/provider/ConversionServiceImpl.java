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
package esa.mo.mc.impl.provider;

import esa.mo.com.impl.util.HelperCOM;
import org.ccsds.moims.mo.com.structures.ExpressionOperator;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.UnknownException;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.Pair;
import org.ccsds.moims.mo.mal.structures.PairList;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mc.conversion.ConversionHelper;
import org.ccsds.moims.mo.mc.conversion.provider.ConversionInheritanceSkeleton;
import org.ccsds.moims.mo.mc.structures.*;

/**
 * Conversion service.
 */
public class ConversionServiceImpl extends ConversionInheritanceSkeleton {

    private ParameterManager manager;
    private boolean initialiased = false;

    /**
     * Initializes the service.
     *
     * @throws MALException if the service could not be started.
     */
    protected synchronized void init() throws MALException {
        if (!initialiased) {
            MALContextFactory.getElementsRegistry().loadServiceAndAreaElements(ConversionHelper.CONVERSION_SERVICE);
        }

        initialiased = true;
    }

    protected Attribute generateConvertedValue(final Attribute rawValue, final ParameterConversion conversion) {
        if (conversion == null) { // No conversion?
            return null;
        }

        Attribute finalValue = null;

        final ConditionalConversionList conditionalConversions = conversion.getConditionalConversions();
        for (ConditionalConversion conditionalConversion : conditionalConversions) {
            // Cycle through all the conditions until it gets one that works...
            try {
                finalValue = applyConversion(rawValue, conditionalConversion);
            } catch (MALInteractionException ex) {
                continue;
            }
        }

        return finalValue;
    }

    /**
     * Evaluates the state of the Parameter expression.
     *
     * @param expression The Parameter Expression
     * @return The state of the expression
     * @throws org.ccsds.moims.mo.mal.MALInteractionException The parameter in
     * the expression does not exist and therefore the state of the expression
     * could not be evaluated.
     */
    protected Boolean evaluateParameterExpression(ParameterExpression expression) throws MALInteractionException {
        if (expression == null) {
            return true;  // No test is required
        }
        ParameterValue parameterValue;
        try {
            parameterValue = manager.getParameterValue(expression.getParameterId());
        } catch (UnknownException ex) {
            throw new MALInteractionException(ex);
        }
        Attribute param = expression.getUseConverted() ?
                parameterValue.getConvertedValue() : parameterValue.getRawValue();

        return HelperCOM.evaluateExpression(param, expression.getOperator(), expression.getValue());
    }

    private Attribute applyConversion(final Attribute value, final ConditionalConversion conditionalRef)
        throws MALInteractionException {
        Boolean eval = this.evaluateParameterExpression(conditionalRef.getCondition());

        if (!eval) {
            throw new MALInteractionException(new UnknownException(null));
        }

        Element conversionDetails = (Element) conditionalRef.getConversion();

        if (conversionDetails == null) {
            return null;
        }

        if (conversionDetails instanceof DiscreteConversion) {
            return this.applyDiscreteConversion((DiscreteConversion) conversionDetails, value);
        }
        if (conversionDetails instanceof LineConversion) {
            return this.applyLineConversion((LineConversion) conversionDetails, value);
        }
        if (conversionDetails instanceof PolyConversion) {
            return this.applyPolyConversion((PolyConversion) conversionDetails, value);
        }
        if (conversionDetails instanceof RangeConversion) {
            return this.applyRangeConversion((RangeConversion) conversionDetails, value);
        }

        return null;
    }

    private Attribute applyDiscreteConversion(final DiscreteConversion conversionDetails,
        final Attribute value) {
        //requirement: 3.8.3.c => no entry in the points-list returns null
        for (Pair mapping : conversionDetails.getMapping()) {
            if (mapping.getFirst().equals(value)) {
                return mapping.getSecond();
            }
        }

        return null;
    }

    private Attribute applyLineConversion(final LineConversion conversionDetails, final Attribute value) {

        PairList points = conversionDetails.getPoints();

        if (points == null) // Should never happen because the object is not nullable
        {
            return null;
        }

        //requirement: 3.8.3.d: Do we have at least 2 points?
        if (points.size() < 2) {  // It is only possible to do a line conversion if there are at least 2 points
            return null;
        }

        Pair top = null;
        Pair bottom = null;

        // Do we have a direct hit?
        for (Pair point : points) {
            if (HelperCOM.evaluateExpression(point.getFirst(), ExpressionOperator.EQUAL, value)) { // If we get a hit, then return it right away
                return point.getSecond();
            }
        }

        top = findTop(value, points);
        bottom = findBottom(value, points);

        // Let's interpolate if we have the top and the bottom points
        if (top != null && bottom != null) {
            return linearInterpolation(value, top, bottom);
        }

        // It was not possible to interpolate. Are we allowed to extrapolate?
        if (!conversionDetails.getExtrapolate()) { // If not, then leave...
            return null;
        }

        // So, we must extrapolate...
        // The value is less than the minimum point
        if (bottom == null && top != null) {
            bottom = top;
            top = findTop(bottom.getFirst(), points);

            return linearInterpolation(value, top, bottom);
        }

        // The value is greater than the maximum point
        if (top == null && bottom != null) {
            top = bottom;
            bottom = findBottom(top.getFirst(), points);

            return linearInterpolation(value, top, bottom);
        }

        return null;
    }

    private Attribute applyPolyConversion(final PolyConversion conversionDetails, final Attribute value) {
        //requirement: 3.8.3.e => no entry in the points-list returns null
        final PairList points = conversionDetails.getPoints();
        if (points.size() == 0) {
            return null;
        }

        double convertedValue = 0;
        for (Pair point : points) {
            double midStep = Math.pow(value.attribute2double(),
                    ((Union) point.getFirst()).getIntegerValue());
            convertedValue += point.getSecond().attribute2double() * midStep;
        }

        return new Union(convertedValue);
    }

    private Attribute applyRangeConversion(final RangeConversion conversionDetails, final Attribute value) {
        //requirement: 3.8.3.f => no entry in the points-list returns null
        // Do we have a direct hit?
        final PairList points = conversionDetails.getPoints();
        for (Pair point : points) {
            if (HelperCOM.evaluateExpression(point.getFirst(), ExpressionOperator.EQUAL, value)) { // If we get a hit, then return it right away
                return point.getSecond();
            }
        }

        Pair bottom = findBottom(value, points);
        if (bottom == null) {
            return null;
        }
        return bottom.getSecond();
    }

    private double linearInterpolation(double x, double x_0, double y_0, double x_1, double y_1) {
        // From wikipedia: http://en.wikipedia.org/wiki/Linear_interpolation
        return (y_0 + (y_1 - y_0) * (x - x_0) / (x_1 - x_0));
    }

    private Union linearInterpolation(final Attribute value, final Pair top, final Pair bottom) {

        double x = value.attribute2double();
        double x_0 = bottom.getFirst().attribute2double();
        double y_0 = bottom.getSecond().attribute2double();
        double x_1 = top.getFirst().attribute2double();
        double y_1 = top.getSecond().attribute2double();

        return new Union(this.linearInterpolation(x, x_0, y_0, x_1, y_1));
    }

    private Pair findTop(final Attribute value, final PairList points) {

        Pair top = null;

        for (Pair point : points) {
            // Check if the point is after the value
            if (HelperCOM.evaluateExpression(point.getFirst(), ExpressionOperator.GREATER, value)) {
                if (top == null) {
                    top = point;
                    continue;
                }

                if (HelperCOM.evaluateExpression(point.getFirst(), ExpressionOperator.LESS, top.getFirst())) {
                    top = point;
                }
            }
        }

        return top;
    }

    private Pair findBottom(final Attribute value, final PairList points) {

        Pair bottom = null;

        for (Pair point : points) {

            // Check if the point is NOT after the value
            if (HelperCOM.evaluateExpression(point.getFirst(), ExpressionOperator.LESS, value)) {
                if (bottom == null) {
                    bottom = point;
                    continue;
                }

                if (HelperCOM.evaluateExpression(point.getFirst(), ExpressionOperator.GREATER, bottom.getFirst())) {
                    bottom = point;
                }

            }
        }

        return bottom;
    }

}
