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
package esa.mo.mc.impl.provider;

import esa.mo.com.impl.provider.ArchiveProviderServiceImpl;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.helpers.HelperAttributes;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.archive.structures.ExpressionOperator;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.Pair;
import org.ccsds.moims.mo.mal.structures.PairList;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.conversion.ConversionHelper;
import org.ccsds.moims.mo.mc.conversion.provider.ConversionInheritanceSkeleton;
import org.ccsds.moims.mo.mc.conversion.structures.DiscreteConversionDetails;
import org.ccsds.moims.mo.mc.conversion.structures.LineConversionDetails;
import org.ccsds.moims.mo.mc.conversion.structures.PolyConversionDetails;
import org.ccsds.moims.mo.mc.conversion.structures.RangeConversionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterConversion;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;
import org.ccsds.moims.mo.mc.structures.ConditionalReference;
import org.ccsds.moims.mo.mc.structures.ParameterExpression;

/**
 *
 */
public class ConversionServiceImpl extends ConversionInheritanceSkeleton {

    private ArchiveProviderServiceImpl archiveService;
    private ParameterManager manager;
    private boolean initialiased = false;

    /**
     *
     * @param archiveService
     * @throws org.ccsds.moims.mo.mal.MALException
     */
    protected synchronized void init(ArchiveProviderServiceImpl archiveService) throws MALException {
        this.archiveService = archiveService;

        if (!initialiased) {
            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(MCHelper.MC_AREA_NAME, MCHelper.MC_AREA_VERSION) == null) {
                MCHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            try {
                ConversionHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
                // nothing to be done..
            }
        }

        initialiased = true;
    }

    protected Attribute generateConvertedValue(Attribute rawValue, ParameterConversion conversion) {
        if (conversion == null){ // No conversion?
            return null;
        }

        Attribute finalValue = null;

        for (int i = 0; i < conversion.getConversionConditions().size(); i++) {
            // Cycle through all the conditions until it gets one that works...
            try {
                finalValue = applyConversion(rawValue, conversion.getConversionConditions().get(i));
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

        parameterValue = manager.getParameterValue(expression.getParameterId().getInstId());
        Attribute param = expression.getUseConverted() ? parameterValue.getConvertedValue() : parameterValue.getRawValue();

        return HelperCOM.evaluateExpression(param, expression.getOperator(), expression.getValue());

    }

    private Element retrieveConversionDetailsFromArchive(ObjectId referenceId) {

        if (archiveService == null){ // If there's no archive...
            return null;
        }
        
        return  HelperArchive.getObjectBodyFromArchive(archiveService, referenceId.getType(), referenceId.getKey().getDomain(), referenceId.getKey().getInstId());
    }

    private Attribute applyConversion(final Attribute value, final ConditionalReference conditionalRef) throws MALInteractionException {

        ParameterExpression condition = conditionalRef.getCondition();
        Boolean eval = this.evaluateParameterExpression(condition);

        if (!eval) {  // Is the Parameter Expression Invalid?
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, null));
        }

        Element conversionDetails = this.retrieveConversionDetailsFromArchive(conditionalRef.getReferenceId());

        if (conversionDetails == null){
            return null; // The Conversion object was not found in the Archive or Archive not available
        }

        // Execute conversion...
        
        // Discrete Conversion:
        if (conversionDetails instanceof DiscreteConversionDetails) {
            return this.applyDiscreteConversion((DiscreteConversionDetails) conversionDetails, value);
        }

        // Line Conversion:
        if (conversionDetails instanceof LineConversionDetails) {
            return this.applyLineConversion((LineConversionDetails) conversionDetails, value);
        }

        // Polynomial Conversion:
        if (conversionDetails instanceof PolyConversionDetails) {
            return this.applyPolyConversion((PolyConversionDetails) conversionDetails, value);
        }

        // Range Conversion:
        if (conversionDetails instanceof RangeConversionDetails) {
            return this.applyRangeConversion((RangeConversionDetails) conversionDetails, value);
        }

        // The object returned didn't match any type of Conversion
        return null;

    }

    private Attribute applyDiscreteConversion(final DiscreteConversionDetails conversionDetails, final Attribute value){
            for (Pair mapping : conversionDetails.getMapping()) {
                if (mapping.getFirst().equals(value)) {
                    return mapping.getSecond();
                }
            }

            return null;
    }
    
    private Attribute applyLineConversion(final LineConversionDetails conversionDetails, final Attribute value){

            PairList points = conversionDetails.getPoints();

            if (points == null) // Should never happen because the object is not nullable
            {
                return null;
            }

            // Do we have at least 2 points?
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
            if (!conversionDetails.getExtrapolate()){ // If not, then leave...
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

    private Attribute applyPolyConversion(final PolyConversionDetails conversionDetails, final Attribute value){
            double convertedValue = 0;

            for (Pair point : conversionDetails.getPoints()) {
                double midStep = Math.pow(HelperAttributes.attribute2double(value), ((Union) point.getFirst()).getIntegerValue());
                convertedValue += HelperAttributes.attribute2double(point.getSecond()) * midStep;
            }

            return new Union(convertedValue);
    }
    
    private Attribute applyRangeConversion(final RangeConversionDetails conversionDetails, final Attribute value){
            // Do we have a direct hit?
            for (Pair point : conversionDetails.getPoints()) {
                if (HelperCOM.evaluateExpression(point.getFirst(), ExpressionOperator.EQUAL, value)) { // If we get a hit, then return it right away
                    return point.getSecond();
                }
            }
            
            Pair bottom = findBottom(value, conversionDetails.getPoints());
            if (bottom == null) return null;
            return bottom.getSecond();
    }
    
    private double linearInterpolation(double x, double x_0, double y_0, double x_1, double y_1) {
        // From wikipedia: http://en.wikipedia.org/wiki/Linear_interpolation
        return (y_0 + (y_1 - y_0) * (x - x_0) / (x_1 - x_0));
    }

    private Union linearInterpolation(final Attribute value, final Pair top, final Pair bottom) {

        double x = HelperAttributes.attribute2double(value);
        double x_0 = HelperAttributes.attribute2double(bottom.getFirst());
        double y_0 = HelperAttributes.attribute2double(bottom.getSecond());
        double x_1 = HelperAttributes.attribute2double(top.getFirst());
        double y_1 = HelperAttributes.attribute2double(top.getSecond());

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
