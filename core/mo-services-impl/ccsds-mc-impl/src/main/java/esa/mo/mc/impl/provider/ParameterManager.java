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

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.mc.impl.interfaces.ParameterStatusListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.FineTimeList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mc.parameter.ParameterHelper;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterConversion;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValue;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValueList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValueList;
import org.ccsds.moims.mo.mc.parameter.structures.ValidityState;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePair;
import org.ccsds.moims.mo.mc.structures.ParameterExpression;

/**
 * Parameter service Manager.
 */
public class ParameterManager extends MCManager
{

  private final ConversionServiceImpl conversionService = new ConversionServiceImpl();

  private Long uniqueObjIdIdentity;
  private Long uniqueObjIdDef; // Counter (different for every Definition)
  private Long uniqueObjIdPVal;
  private final ParameterStatusListener parametersMonitoring;

  public ParameterManager(COMServicesProvider comServices,
      ParameterStatusListener parametersMonitoring)
  {
    super(comServices);

    try {
      ParameterHelper.init(MALContextFactory.getElementFactoryRegistry());
    } catch (MALException ex) { // nothing to be done..
    }

    this.parametersMonitoring = parametersMonitoring;

    if (super.getArchiveService() == null) {  // No Archive?
      this.uniqueObjIdIdentity = 0L; // The zeroth value will not be used (reserved for the wildcard)
      this.uniqueObjIdDef = 0L; // The zeroth value will not be used (reserved for the wildcard)
      this.uniqueObjIdPVal = 0L; // The zeroth value will not be used (reserved for the wildcard)
    } else {
      // With Archive...

      // Initialize the Conversion service
      try {
        this.conversionService.init(super.getArchiveService());
      } catch (MALException ex) {
        Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  /**
   * checks in the application if a parameter is read-only
   *
   * @param identityId the id of the parameter to be checked
   * @return true, if it is readonly. false, if you can set it.
   */
  public boolean isReadOnly(Long identityId)
  {
    Class cla;
    try {
      cla = parametersMonitoring.getClass().getMethod("onGetValue",
          Identifier.class, Byte.class).getDeclaringClass();
      if (cla == ParameterStatusListener.class) {
        return parametersMonitoring.isReadOnly(identityId);
      }
    } catch (NoSuchMethodException | SecurityException ex) {
      Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
    }

    return parametersMonitoring.isReadOnly(getName(identityId));
  }

  /**
   * gets the intervals supported by the provider
   *
   * @return
   */
//    protected DurationList getProvidedIntervals() {
//        return parametersMonitoring.getProvidedIntervals();
//    }
  /**
   * Gets the details of the definition with the given identity-id.
   *
   * @param identityId the id of the identity you want the details from
   * @return the definition-details. Or Null if not found.
   */
  public ParameterDefinitionDetails getParameterDefinition(Long identityId)
  {
    return (ParameterDefinitionDetails) this.getDefinition(identityId);
  }

  /**
   * stores the new parameter-value in the provider and if existent in the COM-Archive
   *
   * @param identityId        the id of the parameters identity
   * @param pVal              the parametervalue to be stored
   * @param source            the Id of the object that caused the update to be generated
   * @param connectionDetails the details of the connection
   * @param timestamp         the timestamp that will be set as the creation-timestamp of the, in
   *                          this method created, ParameterValue-Object
   * @return The unique identifier or null if the implementation is using the Archive service for
   * objects storage. In this case, the unique identifier must be retrieved from the Archive during
   * storage
   */
  protected Long storeAndGeneratePValobjId(Long identityId, ParameterValue pVal,
      ObjectId source, SingleConnectionDetails connectionDetails, FineTime timestamp)
  {
    if (super.getArchiveService() == null) {
      uniqueObjIdPVal++;
      return this.uniqueObjIdPVal;
    } else {
      ParameterValueList pValList = new ParameterValueList();
      pValList.add(pVal);
      final Long related = getDefinitionId(identityId);

      //create the archive details(related/source link, ...). The timestamp must 
      //be same as the one that will be used later for publishing the ParameterValue
      final ArchiveDetailsList archiveDetailsList;
      if (timestamp == null) //ParameterValue-Object will not be published, generate a new timestamp then 
      {
        archiveDetailsList = HelperArchive.generateArchiveDetailsList(related, source,
            connectionDetails);
      } else { //use the timestamp given
        archiveDetailsList = new ArchiveDetailsList();
        archiveDetailsList.add(new ArchiveDetails(0L, new ObjectDetails(related, source),
            ConfigurationProviderSingleton.getNetwork(), timestamp,
            connectionDetails.getProviderURI()));
      }

      try {
        // requirement: 3.3.4.d
        //save the published value in the COM-Archive
        LongList objIds = super.getArchiveService().store(true,
            ParameterHelper.PARAMETERVALUEINSTANCE_OBJECT_TYPE,
            ConfigurationProviderSingleton.getDomain(),
            archiveDetailsList,
            pValList,
            null);
        if (objIds.size() == 1) {
          return objIds.get(0);
        }
      } catch (MALException | MALInteractionException ex) {
        Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
      }
      return null;
    }
  }

  /**
   *
   * @param pVals
   * @param relatedList
   * @param sourcesList
   * @param connectionDetails
   * @param timestamps        the current timestamps. it will be used as the creation time for all
   *                          ParameterValues
   * @return The unique identifier or null if the implementation is using the Archive service for
   * objects storage. In this case, the unique identifier must be retrieved from the Archive during
   * storage
   */
  protected LongList storeAndGenerateMultiplePValobjId(final ParameterValueList pVals,
      final LongList relatedList, final ObjectIdList sourcesList,
      final SingleConnectionDetails connectionDetails, final FineTimeList timestamps)
  {
    if (super.getArchiveService() != null) {

      ArchiveDetailsList archiveDetailsList = new ArchiveDetailsList();

      for (int i = 0; i < relatedList.size(); i++) {
        ArchiveDetails archiveDetails = new ArchiveDetails();
        archiveDetails.setInstId(0L);
        archiveDetails.setDetails(new ObjectDetails(relatedList.get(i), sourcesList.get(i)));
        archiveDetails.setNetwork(ConfigurationProviderSingleton.getNetwork());
        archiveDetails.setTimestamp(timestamps.get(i));
        archiveDetails.setProvider(connectionDetails.getProviderURI());

        archiveDetailsList.add(archiveDetails);
      }

      try {// requirement: 3.3.4.d 
        LongList objIds = super.getArchiveService().store(
            true,
            ParameterHelper.PARAMETERVALUEINSTANCE_OBJECT_TYPE,
            ConfigurationProviderSingleton.getDomain(),
            archiveDetailsList,
            pVals,
            null);

        if (objIds.size() == pVals.size()) {
          return objIds;
        }
      } catch (MALException | MALInteractionException ex) {
        Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    return null;
  }

  /**
   * Gets the current values of the parameters with the given identity-ids
   *
   * @param identityIds the ids of the identities
   * @return a list with the requested parameter-values
   */
  protected ParameterValueList getParameterValues(LongList identityIds)
  {
    return getParameterValues(identityIds, false);

  }

  /**
   * Gets the current values of the parameters with the given identity-ids
   *
   * @param identityIds the ids of the identities
   * @param aggrExpired should be set to true, if the aggregation that is sampling the parameter, is
   *                    periodic and the update hasnt been received in the aggregation-period. if
   *                    true, the validity-state of the new parameter will be expired.
   * @return a list with the requested parameter-values
   */
  protected ParameterValueList getParameterValues(LongList identityIds, boolean aggrExpired)
  {
    ParameterValueList pValList = new ParameterValueList();
    for (Long identityId : identityIds) {
      try {
        pValList.add(getParameterValue(identityId, aggrExpired));
      } catch (MALInteractionException ex) {
        pValList.add(null);
      }
    }

    return pValList;
  }

  /**
   * Gets the current value of the parameter with the given identity-id
   *
   * @param identityId the id of the identity
   * @return the requested parameter-value
   * @throws MALInteractionException
   */
  public ParameterValue getParameterValue(Long identityId) throws MALInteractionException
  {
    return getParameterValue(identityId, false);
  }

  /**
   * Creates a new ParameterValue object with the given value for the parameter with the given id.
   *
   * @param name     of the parameter the value should be set to
   * @param rawValue the value to be set at the new ParameterValue
   * @return a new ParameterValue object
   */
  /*
    public ParameterValue createParameterValue(Identifier name, Attribute rawValue) {
        final ParameterDefinitionDetails pDef = this.getParameterDefinition(getIdentity(name));
        // Generate final Parameter Value
        return generateNewParameterValue(rawValue, pDef, false);
    }
   */
  /**
   * Gets the current value of the parameter with the given identity-id
   *
   * @param identityId  the id of the identity
   * @param aggrExpired should be set to true, if the aggregation that is sampling the parameter, is
   *                    periodic and the update hasnt been received in the aggregation-period. if
   *                    true, the validity-state of the new parameter will be expired.
   * @return the requested parameter-value
   * @throws MALInteractionException
   */
  public ParameterValue getParameterValue(Long identityId, boolean aggrExpired) throws
      MALInteractionException
  {
    if (!this.existsIdentity(identityId)) {  // The Parameter does not exist
      throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER,
          identityId));
    }

    ParameterDefinitionDetails pDef = this.getParameterDefinition(identityId);

    try {
      Attribute rawValue = getRawValue(identityId, pDef);
      // Generate final Parameter Value
      return generateNewParameterValue(rawValue, pDef, aggrExpired);
    } catch (IOException ex) {
      return new ParameterValue(getAsUOctet(ValidityState.INVALID_RAW), null, null);
    }
  }

  /**
   * Wrapper function for calling onGetValue without breaking backwards compatibility
   *
   * @return
   */
  public Attribute getValue(Long paramIdentityId) throws IOException
  {
    // check if new interface method is implemented, if yes, call it
    try {
      Class cla = parametersMonitoring.getClass().getMethod("onGetValue",
          Identifier.class, Byte.class).getDeclaringClass();
      if (cla == ParameterStatusListener.class) {
        return parametersMonitoring.onGetValue(paramIdentityId);
      }
    } catch (NoSuchMethodException | SecurityException ex) {
    }

    // else use old procedure:
    ParameterDefinitionDetails pDef = this.getParameterDefinition(paramIdentityId);
    Attribute value;

    return parametersMonitoring.onGetValue(super.getName(paramIdentityId), pDef.getRawType());

  }

  /**
   * evaluates the given parameterExpression
   *
   * @param expression the parameterExpression to be evaluated
   * @return The boolean value of the evaluation. Null if not evaluated.
   */
  public Boolean evaluateParameterExpression(ParameterExpression expression)
  {

    if (expression == null) {
      return true;  // No test is required
    }

    //TODO: contains the expression defintion or identity-id? -> issue #132, #179
    final Long paramIdentityId = expression.getParameterId().getInstId();
    ParameterDefinitionDetails pDef = this.getParameterDefinition(paramIdentityId);
    Attribute value;
    try {

      Class cla = parametersMonitoring.getClass().getMethod("onGetValue",
          Identifier.class, Byte.class).getDeclaringClass();
      if (cla == ParameterStatusListener.class) {
        value = parametersMonitoring.onGetValue(paramIdentityId);
      } else {
        value = parametersMonitoring.onGetValue(super.getName(paramIdentityId), pDef.getRawType());
      }
    } catch (IOException | NoSuchMethodException | SecurityException ex) {
      Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
      return false;
    }

    if (expression.getUseConverted()) { // Is the validity checking for the converted or for the raw value?
      value = conversionService.generateConvertedValue(value, pDef.getConversion());
    }

    return HelperCOM.evaluateExpression(value, expression.getOperator(), expression.getValue());
  }

  /**
   * generates the validity-state for the new parameterValue
   *
   * @param pDef           the values definition that holds the parameterExpression and conversion
   *                       needed for calculating the validityState
   * @param rawValue       the new raw value
   * @param convertedValue the converted Value of the new Raw-Value
   * @param aggrExpired    should be set to true, if the aggregation that is sampling the parameter,
   *                       is periodic and the update hasnt been received in the aggregation-period.
   *                       if true, the validity-state of the new parameter will be expired.
   * @return the validityState
   */
  protected UOctet generateValidityState(final ParameterDefinitionDetails pDef,
      final Attribute rawValue, final Attribute convertedValue, final boolean aggrExpired)
  {

    //parameter-aggregation has a timeout that is expired
    if (pDef.getGenerationEnabled() && pDef.getReportInterval().getValue() != 0 && aggrExpired) //requirement 3.3.3.i
    {
      return getAsUOctet(ValidityState.EXPIRED);
    }

    //parameter raw value cannot be obtained, or calculated for synthetic parameters
    if (rawValue == null /*|| rawValueOfSyntheticParameterCannotBeCalculated*/) { //requirement: 3.3.3.j
      return getAsUOctet(ValidityState.INVALID_RAW);
    }
    final ParameterExpression validityExpression = pDef.getValidityExpression();
    final Boolean evalExpression = this.evaluateParameterExpression(validityExpression);
    final ParameterConversion conversion = pDef.getConversion();

    //expression didnt fail 
    if (validityExpression == null || evalExpression) {
      //conversions didnt fail
      if (conversion == null || convertedValue != null) { // requirement: 3.3.3.k
        return getAsUOctet(ValidityState.VALID);
      } //conversion failed -> convertedValue == null
      else {// requirement: 3.3.3.l
        return getAsUOctet(ValidityState.INVALID_CONVERSION);
      }
    } //expression failed -> evalExpression == false
    else {
      //get validityState of the parameters that are needed for the expression
      UOctet expPValState = getValidityState(validityExpression, aggrExpired);
      //expression failed with not valid parameters
      if (!expPValState.equals(getAsUOctet(ValidityState.VALID))) { // requirement: 3.3.3.m
        return getAsUOctet(ValidityState.UNVERIFIED);
      } //expression failed with valid parameters
      else { // requirement: 3.3.3.n
        return getAsUOctet(ValidityState.INVALID);
      }
    }
  }

  /**
   * converts a validityState to its UOctet-Value
   *
   * @param valState the validityState to be converted
   * @return the converted validityState as an UOctet
   */
  private UOctet getAsUOctet(ValidityState valState)
  {
    return new UOctet((short) valState.getNumericValue().getValue());
  }

  /**
   * gets the validity-state of the parameter used in the expression
   *
   * @param validityExpression the expression where parameters are used
   * @return the validity-state of the parameter used in the expression
   */
  private UOctet getValidityState(final ParameterExpression validityExpression, boolean aggrExpired)
  {
    final Long expPIdentityId = validityExpression.getParameterId().getInstId();
    final Attribute expParamValue;
    try {
      Class cla = parametersMonitoring.getClass().getMethod("onGetValue",
          Identifier.class, Byte.class).getDeclaringClass();
      if (cla == ParameterStatusListener.class) {
        expParamValue = parametersMonitoring.onGetValue(expPIdentityId);
      } else {
        expParamValue = parametersMonitoring.onGetValue(super.getName(expPIdentityId), null);
      }

    } catch (IOException | NoSuchMethodException | SecurityException ex) {
      Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
      return getAsUOctet(ValidityState.INVALID_RAW);
    }
    final ParameterDefinitionDetails expPDef = getParameterDefinition(expPIdentityId);
    return generateValidityState(this.getParameterDefinition(expPIdentityId),
          expParamValue, getConvertedValue(expParamValue, expPDef), aggrExpired);
  }

  protected ObjectInstancePair add(Identifier name, ParameterDefinitionDetails definition,
      ObjectId source, SingleConnectionDetails connectionDetails)
  { // requirement: 3.3.2.5
    ObjectInstancePair newIdPair;

    if (super.getArchiveService() == null) {
      //add to providers local list
      uniqueObjIdIdentity++; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
      uniqueObjIdDef++; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
      newIdPair = new ObjectInstancePair(uniqueObjIdIdentity, uniqueObjIdDef);
    } else {
      try {
        //requirement: 3.3.12.2.f: if a ParameterName ever existed before, use the old ParameterIdentity-Object by retrieving it from the archive
        //check if the name existed before and retrieve id if found
        Long identityId = retrieveIdentityIdByNameFromArchive(
            ConfigurationProviderSingleton.getDomain(),
            name, ParameterHelper.PARAMETERIDENTITY_OBJECT_TYPE);

        //in case the ParameterName never existed before, create a new identity
        if (identityId == null) {
          IdentifierList names = new IdentifierList();
          names.add(name);
          //add to the archive
          LongList identityIds = super.getArchiveService().store(true,
              ParameterHelper.PARAMETERIDENTITY_OBJECT_TYPE,
              ConfigurationProviderSingleton.getDomain(),
              HelperArchive.generateArchiveDetailsList(null, source, connectionDetails),
              names,
              null);
          identityId = identityIds.get(0);
        }

        //not matter if the parameter was created or loaded, a new definition will be created
        ParameterDefinitionDetailsList defs = new ParameterDefinitionDetailsList();
        defs.add(definition);
        LongList defIds = super.getArchiveService().store(true,
            ParameterHelper.PARAMETERDEFINITION_OBJECT_TYPE,
            ConfigurationProviderSingleton.getDomain(),
            HelperArchive.generateArchiveDetailsList(identityId, source, connectionDetails),
            defs,
            null);

        //add to providers local list
        newIdPair = new ObjectInstancePair(identityId, defIds.get(0));

      } catch (MALException | MALInteractionException ex) {
        Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
        return null;
      }
    }

//        this.addIdentityDefinition(newIdPair.getObjIdentityInstanceId(), name, newIdPair.getObjDefInstanceId(), definition);
    this.addIdentityDefinition(name, newIdPair, definition);
    return newIdPair;
  }

  /**
   * updates the parameter-definition of the parameter-identity by adding a new parameter-definition
   *
   * @param identityId        the id of the identity
   * @param definition        the new definition details
   * @param source            the source object id that caused the new parameter-definition to be
   *                          created
   * @param connectionDetails the given connectionDetails
   * @return the object instance identifier of the new parameter-definition
   */
  protected Long update(Long identityId, ParameterDefinitionDetails definition,
      ObjectId source, SingleConnectionDetails connectionDetails)
  { // requirement: 3.3.2.d
    Long newDefId = null;

    if (super.getArchiveService() == null) { //only update locally
      //add to providers local list
      uniqueObjIdDef++; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
      newDefId = uniqueObjIdDef;

    } else {  // update in the COM Archive
      try {
        ParameterDefinitionDetailsList defs = new ParameterDefinitionDetailsList();
        defs.add(definition);

        //create a new ParameterDefinition 
        LongList defIds = super.getArchiveService().store(true,
            ParameterHelper.PARAMETERDEFINITION_OBJECT_TYPE,
            ConfigurationProviderSingleton.getDomain(),
            HelperArchive.generateArchiveDetailsList(identityId, source, connectionDetails),
            defs,
            null);

        newDefId = defIds.get(0);

      } catch (MALException | MALInteractionException ex) {
        Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    //update internal list
    this.updateDef(identityId, newDefId, definition);

    return newDefId;
  }

  /**
   * deletes the identity with the given id and its definition from the internal list
   *
   * @param identityId the id of the identity to be removed
   * @return true if it was successful.
   */
  protected boolean delete(Long identityId)
  { // requirement: 3.3.2.d
    return this.deleteIdentity(identityId);
  }

  /**
   * sets the generationEnabled field for the given parameter as the given bool-value
   *
   * @param identityId        identityId of the parameter
   * @param bool              the new generationEnabled value
   * @param source            the source link for the new definition
   * @param connectionDetails the details of the connection
   * @return true if it was successfully updated. false if def is null or the new bool value was the
   * same as the current value.
   */
  protected Long setGenerationEnabled(Long identityId, Boolean bool, ObjectId source,
      SingleConnectionDetails connectionDetails)
  { // requirement: 3.3.2.a.c
    ParameterDefinitionDetails def = (ParameterDefinitionDetails) this.getParameterDefinition(
        identityId);

    if (def == null) {
      return null;
    }

    //requirement: 3.3.10.2.f
    if (def.getGenerationEnabled().booleanValue() == bool) { // Is it set with the requested value already?
      return identityId; // the value was not changed
    }
    def.setGenerationEnabled(bool);

    //requirement: 3.3.10.2.k
    return this.update(identityId, def, source, connectionDetails);
  }

  /**
   * sets the enableGeneration field of all existing parameters to the given bool-value
   *
   * @param bool              the value the enableGeneration field of all parameters should be set
   *                          to
   * @param source            the source that created this update. will be set as the new defintions
   *                          source
   * @param connectionDetails the details of the connection
   */
  protected void setGenerationEnabledAll(Boolean bool, ObjectId source,
      SingleConnectionDetails connectionDetails)
  {
    LongList identitiyIds = new LongList();
    identitiyIds.addAll(this.listAllIdentities());

    for (Long identityId : identitiyIds) {
      ParameterDefinitionDetails def = this.getParameterDefinition(identityId);
      def.setGenerationEnabled(bool);
      this.update(identityId, def, source, connectionDetails);
    }
  }

  /**
   * sets a new value for a parameter by creating a new parameterValueObject and storing it in the
   * provider or COM-Archive
   *
   * @param newRawValues the new value to be set
   * @return a list with boolean values, that say which values were successfully (true) or
   * unsuccessfully(false) set
   */
  protected ParameterValueList setValues(ParameterRawValueList newRawValues)
  {

    // check if new interface method is implemented, if yes, call it
    try {
      Class cla = parametersMonitoring.getClass().getMethod("onSetValue",
          IdentifierList.class, ParameterRawValueList.class).getDeclaringClass();
      if (cla == ParameterStatusListener.class) {
        Boolean setSuccessful = parametersMonitoring.onSetValue(newRawValues);
      }
    } catch (NoSuchMethodException | SecurityException ex) {
    }
    // else use old procedure:

    ParameterValueList paramValList = new ParameterValueList();
    IdentifierList names = new IdentifierList(newRawValues.size());

    //each Raw Value shall be set
    for (ParameterRawValue newRawValue : newRawValues) {
      Long identityId = newRawValue.getParamInstId();
      //requirement 3.3.9.2.h: create a new ParameterValue
      //TODO: what happens with the newly crated value? only raw value will be saved in the parameterApplication -> issue #140 
//            ParameterValue newValue = generateNewParameterValue(newRawValue.getRawValue(), getParameterDefinition(identityId), false);
      paramValList.add(generateNewParameterValue(newRawValue.getRawValue(), getParameterDefinition(
          identityId), false));
      names.add(getName(identityId));
//            parametersMonitoring.onSetValue(getName(identityId), newRawValue.getRawValue(), timestamp);
//            parametersMonitoring.onSetValue(getName(identityId), newRawValue.getRawValue());
//            Boolean success;
//            if (parametersMonitoring != null) {
//                success = 
//            } else {
//                success = false;
//            }
//            successFlags.add(success);
    }

    // setSuccessful is not being used anywhere... weird
    try {
      Boolean setSuccessful = parametersMonitoring.onSetValue(names, newRawValues);
    } catch (UnsupportedOperationException ex) {
    }

    return paramValList;
  }

  /**
   * This method fills a new parameter value with its rawValue, convertedValue and validityState
   *
   * @param rawValue    the rawValue to be set and converted
   * @param pDef        the parameter-defintion the value belongs to
   * @param aggrExpired should be set to true, if the aggregation that is sampling the parameter, is
   *                    periodic and the update hasnt been received in the aggregation-period. if
   *                    true, the validity-state of the new parameter will be expired.
   * @return a filled ParameterValue
   */
  public ParameterValue generateNewParameterValue(Attribute rawValue,
      final ParameterDefinitionDetails pDef, final boolean aggrExpired)
  {
    ParameterValue newPValue;

    //requirement: 3.3.3.q, 3.3.3.r         
    // if implementation specific machanisms or deployment specific values should be used 
    // for evaluating the validity-state, use these, otherwise use the standard ones.
    newPValue = parametersMonitoring.getValueWithCustomValidityState(rawValue, pDef);
    if (newPValue != null) {
      return newPValue;
    }
    newPValue = new ParameterValue();
    //convert the raw-value
    //requirement 3.3.3.p is implicitly met here. 
    Attribute convertedValue = this.getConvertedValue(rawValue, pDef);

    //check the validity and set the state
    UOctet validityState = generateValidityState(pDef, rawValue, convertedValue, aggrExpired);
    newPValue.setValidityState(validityState);

    if (validityState.equals(getAsUOctet(ValidityState.INVALID_CONVERSION))) {
      convertedValue = null;  // requirement: 3.3.3.o
    }
    if (validityState.equals(getAsUOctet(ValidityState.INVALID_RAW))) {
      rawValue = null; //requirement: 3.3.3.j
    }

    newPValue.setRawValue(rawValue);
    newPValue.setConvertedValue(convertedValue);
    //Attribute unionConvertedValue = (convertedValue == null) ? null : convertedValue;  // Union doesn't directly accept null values
    return newPValue;
  }

  /**
   * gets the current raw value of the parameter with the given identity-Id from the application
   *
   * @param identityId the identity-id of the parameter
   * @param pDef       the definition of the parameter
   * @return the raw value. null if there is no parametersMonitoring
   */
  private Attribute getRawValue(Long identityId, ParameterDefinitionDetails pDef) throws IOException
  {
    if (parametersMonitoring == null) {
      return null;
    }
    try {
      Class cla = parametersMonitoring.getClass().getMethod("onGetValue",
          Identifier.class, Byte.class).getDeclaringClass();
      if (cla == ParameterStatusListener.class) {
        return parametersMonitoring.onGetValue(identityId);
      }
    } catch (NoSuchMethodException | SecurityException ex) {
      Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
    }
    return parametersMonitoring.onGetValue(this.getName(identityId), pDef.getRawType());

  }

  /**
   * gets the timestamp of the current raw value of the parameter with the given identity-Id from
   * the application
   *
   * @param identityId the identity-id of the parameter
   * @return the timestamp of the current parameters raw value. null if there is no
   * parametersMonitoring
   */
  /*
    protected Long getParameterValuesTimestamp(Long identityId) {
        Long timestamp;
        if (parametersMonitoring != null) {
            timestamp = parametersMonitoring.onGetValueTimestamp(super.getName(identityId));
        } else {
            timestamp = null;
        }
        return timestamp;
    }
   */
  /**
   * gets the converted value of a raw value
   *
   * @param rawValue
   * @param pDef     the definition it should get the conversion from
   * @return the converted value. null if no conversionservice is available
   */
  private Attribute getConvertedValue(final Attribute rawValue,
      final ParameterDefinitionDetails pDef)
  {
    // Is the Conversion service available for use?
    return (conversionService != null)
        ? conversionService.generateConvertedValue(rawValue, pDef.getConversion())
        : null;
  }

}
