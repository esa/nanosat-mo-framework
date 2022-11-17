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

import esa.mo.com.impl.provider.ArchiveProviderServiceImpl;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.helpers.HelperAttributes;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.archive.structures.ExpressionOperator;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
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
import org.ccsds.moims.mo.mc.structures.ConditionalConversionList;
import org.ccsds.moims.mo.mc.structures.ConditionalConversion;
import org.ccsds.moims.mo.mc.structures.ParameterExpression;

/**
 * Conversion service.
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

            if (MALContextFactory.lookupArea(MCHelper.MC_AREA_NAME, MCHelper.MC_AREA_VERSION).getServiceByName(
                ConversionHelper.CONVERSION_SERVICE_NAME) == null) {
                ConversionHelper.init(MALContextFactory.getElementFactoryRegistry());
            }
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
        ParameterValue parameterValue = manager.getParameterValue(expression.getParameterId().getInstId());
        Attribute param = expression.getUseConverted() ? parameterValue.getConvertedValue() : parameterValue
            .getRawValue();

        return HelperCOM.evaluateExpression(param, expression.getOperator(), expression.getValue());
    }

    /* Not used...
    private Element retrieveConversionDetailsFromArchive(ObjectId referenceId) {
        if (archiveService == null) { // If there's no archive...
            return null;
        }
        return HelperArchive.getObjectBodyFromArchive(archiveService, referenceId.getType(), 
                referenceId.getKey().getDomain(), referenceId.getKey().getInstId());
    }
    */

    private Attribute applyConversion(final Attribute value, final ConditionalConversion conditionalRef)
        throws MALInteractionException {
        Boolean eval = this.evaluateParameterExpression(conditionalRef.getCondition());

        if (!eval) {  // Is the Parameter Expression Invalid?
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, null));
        }

        //requirement: 3.8.4.g id references a ConversionDetails-object (not an identity)
        //        Element conversionDetails = this.retrieveConversionDetailsFromArchive(conditionalRef.getConversionId());
        /**
         * get the conversionDetails by the identityId *
         */
        //TODO: use a query method here
        final IdentifierList domain = conditionalRef.getConversionId().getDomain();

        Element conversionDetails = this.getConversionDefinition(domain, conditionalRef.getConversionId().getInstId());

        if (conversionDetails == null) {
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

    /**
     * Looks for the Conversion-Definition-Details-Object that has the identity
     * as its source link. The method gets the object by the archive, by
     * checking all conversion-types.
     *
     * @param domain
     * @param identityId 
     * @return The latest conversion-definition-details-object if one is found
     * with the given identityId, or null otherwise.
     */
    private Element getConversionDefinition(final IdentifierList domain, final Long identityId) {
        Element conversionDetails;
        //Search in PolyConversions:
        ObjectType convType = ConversionHelper.POLYCONVERSION_OBJECT_TYPE;
        conversionDetails = getDefinitionDetailsFromIdentityIdFromArchive(convType, domain, identityId);

        if (conversionDetails == null) {
            //Search in DiscreteConversions
            convType = ConversionHelper.DISCRETECONVERSION_OBJECT_TYPE;
            conversionDetails = getDefinitionDetailsFromIdentityIdFromArchive(convType, domain, identityId);
        } else {
            return conversionDetails;
        }
        if (conversionDetails == null) {
            //Search in LineConversions
            convType = ConversionHelper.LINECONVERSION_OBJECT_TYPE;
            conversionDetails = getDefinitionDetailsFromIdentityIdFromArchive(convType, domain, identityId);
        } else {
            return conversionDetails;
        }
        if (conversionDetails == null) {
            //Search in RangeConversions
            convType = ConversionHelper.RANGECONVERSION_OBJECT_TYPE;
            conversionDetails = getDefinitionDetailsFromIdentityIdFromArchive(convType, domain, identityId);
        }
        return conversionDetails;
    }

    /**
     * TODO: move to a ArchiveHelper or remove it because a query-method is more
     * efficient
     *
     * Iterates through all Objects in archive with the given objectType and
     * looks for the given identity as the source object. The Object with the
     * newest timestamp is returned.
     *
     * @param objType the objecttype to look for
     * @param domain the domain to look at in the archive
     * @param identityId the identity id the searched object has a source-link
     * to
     * @return The DefinitionDetails-Object with the newest timestamp or null if
     * no object of the given objectType references the given identity.
     */
    private Element getDefinitionDetailsFromIdentityIdFromArchive(ObjectType objType, final IdentifierList domain,
        Long identityId) {
        //retrieve all existing conversion-objects
        LongList defIds = new LongList();
        defIds.add(0L);
        final ArchiveDetailsList defarchiveDetailsListFromArchive = HelperArchive.getArchiveDetailsListFromArchive(
            archiveService, objType, domain, defIds);
        //look if there are conversionDetails, which reference the identity
        Long defId = null;
        long maxTimeStamp = 0L;
        if (defarchiveDetailsListFromArchive == null)
            return null;
        //iterate through all entries to check for the given identity as the source object
        for (ArchiveDetails defArchiveDetails : defarchiveDetailsListFromArchive) {
            if (defArchiveDetails.getDetails().getRelated() == null)
                continue;
            if (defArchiveDetails.getDetails().getRelated().equals(identityId)) {
                //and filter for the latest one
                final long itemTimestamp = defArchiveDetails.getTimestamp().getValue();
                if (itemTimestamp > maxTimeStamp) {
                    defId = defArchiveDetails.getInstId();
                    maxTimeStamp = itemTimestamp;
                }
            }
        }
        if (defId != null) {
            return HelperArchive.getObjectBodyFromArchive(archiveService, objType, domain, defId);
        }
        return null;
    }

    private Attribute applyDiscreteConversion(final DiscreteConversionDetails conversionDetails,
        final Attribute value) {
        //requirement: 3.8.3.c => no entry in the points-list returns null
        for (Pair mapping : conversionDetails.getMapping()) {
            if (mapping.getFirst().equals(value)) {
                return mapping.getSecond();
            }
        }

        return null;
    }

    private Attribute applyLineConversion(final LineConversionDetails conversionDetails, final Attribute value) {

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

    private Attribute applyPolyConversion(final PolyConversionDetails conversionDetails, final Attribute value) {
        //requirement: 3.8.3.e => no entry in the points-list returns null
        final PairList points = conversionDetails.getPoints();
        if (points.size() == 0) {
            return null;
        }

        double convertedValue = 0;
        for (Pair point : points) {
            double midStep = Math.pow(HelperAttributes.attribute2double(value), ((Union) point.getFirst())
                .getIntegerValue());
            convertedValue += HelperAttributes.attribute2double(point.getSecond()) * midStep;
        }

        return new Union(convertedValue);
    }

    private Attribute applyRangeConversion(final RangeConversionDetails conversionDetails, final Attribute value) {
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
