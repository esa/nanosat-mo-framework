package org.ccsds.moims.mo.mcprototype.parametertest;

/**
 * Helper class for ParameterTest service.
 */
public class ParameterTestHelper
{
  /**
   * Service number literal.
   */
  public static final int _PARAMETERTEST_SERVICE_NUMBER = 130;
  /**
   * Service number instance.
   */
  public static final org.ccsds.moims.mo.mal.structures.UShort PARAMETERTEST_SERVICE_NUMBER = new org.ccsds.moims.mo.mal.structures.UShort(_PARAMETERTEST_SERVICE_NUMBER);
  /**
   * Service name constant.
   */
  public static final org.ccsds.moims.mo.mal.structures.Identifier PARAMETERTEST_SERVICE_NAME = new org.ccsds.moims.mo.mal.structures.Identifier("ParameterTest");
  /**
   * Service singleton instance.
   */
  public static org.ccsds.moims.mo.com.COMService PARAMETERTEST_SERVICE = new org.ccsds.moims.mo.com.COMService(PARAMETERTEST_SERVICE_NUMBER, PARAMETERTEST_SERVICE_NAME);
  /**
   * Operation number literal for operation PUSHPARAMETERVALUE.
   */
  public static final int _PUSHPARAMETERVALUE_OP_NUMBER = 1;
  /**
   * Operation number instance for operation PUSHPARAMETERVALUE.
   */
  public static final org.ccsds.moims.mo.mal.structures.UShort PUSHPARAMETERVALUE_OP_NUMBER = new org.ccsds.moims.mo.mal.structures.UShort(_PUSHPARAMETERVALUE_OP_NUMBER);
  /**
   * Operation instance for operation PUSHPARAMETERVALUE.
   */
  public static final org.ccsds.moims.mo.mal.MALSubmitOperation PUSHPARAMETERVALUE_OP = new org.ccsds.moims.mo.mal.MALSubmitOperation(PUSHPARAMETERVALUE_OP_NUMBER, new org.ccsds.moims.mo.mal.structures.Identifier("pushParameterValue"), false, new org.ccsds.moims.mo.mal.structures.UShort(1), new org.ccsds.moims.mo.mal.MALOperationStage(new org.ccsds.moims.mo.mal.structures.UOctet((short) 1), new Long[] {org.ccsds.moims.mo.mal.structures.Attribute.IDENTIFIER_SHORT_FORM, org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValue.SHORT_FORM}, new Long[] {}));
  /**
   * Operation number literal for operation SETVALIDITYSTATEOPTIONS.
   */
  public static final int _SETVALIDITYSTATEOPTIONS_OP_NUMBER = 2;
  /**
   * Operation number instance for operation SETVALIDITYSTATEOPTIONS.
   */
  public static final org.ccsds.moims.mo.mal.structures.UShort SETVALIDITYSTATEOPTIONS_OP_NUMBER = new org.ccsds.moims.mo.mal.structures.UShort(_SETVALIDITYSTATEOPTIONS_OP_NUMBER);
  /**
   * Operation instance for operation SETVALIDITYSTATEOPTIONS.
   */
  public static final org.ccsds.moims.mo.mal.MALSubmitOperation SETVALIDITYSTATEOPTIONS_OP = new org.ccsds.moims.mo.mal.MALSubmitOperation(SETVALIDITYSTATEOPTIONS_OP_NUMBER, new org.ccsds.moims.mo.mal.structures.Identifier("setValidityStateOptions"), false, new org.ccsds.moims.mo.mal.structures.UShort(1), new org.ccsds.moims.mo.mal.MALOperationStage(new org.ccsds.moims.mo.mal.structures.UOctet((short) 1), new Long[] {org.ccsds.moims.mo.mal.structures.Attribute.BOOLEAN_SHORT_FORM, org.ccsds.moims.mo.mal.structures.Attribute.BOOLEAN_SHORT_FORM}, new Long[] {}));
  /**
   * Operation number literal for operation SETREADONLYPARAMETER.
   */
  public static final int _SETREADONLYPARAMETER_OP_NUMBER = 3;
  /**
   * Operation number instance for operation SETREADONLYPARAMETER.
   */
  public static final org.ccsds.moims.mo.mal.structures.UShort SETREADONLYPARAMETER_OP_NUMBER = new org.ccsds.moims.mo.mal.structures.UShort(_SETREADONLYPARAMETER_OP_NUMBER);
  /**
   * Operation instance for operation SETREADONLYPARAMETER.
   */
  public static final org.ccsds.moims.mo.mal.MALSubmitOperation SETREADONLYPARAMETER_OP = new org.ccsds.moims.mo.mal.MALSubmitOperation(SETREADONLYPARAMETER_OP_NUMBER, new org.ccsds.moims.mo.mal.structures.Identifier("setReadOnlyParameter"), false, new org.ccsds.moims.mo.mal.structures.UShort(1), new org.ccsds.moims.mo.mal.MALOperationStage(new org.ccsds.moims.mo.mal.structures.UOctet((short) 1), new Long[] {org.ccsds.moims.mo.mal.structures.Attribute.IDENTIFIER_SHORT_FORM, org.ccsds.moims.mo.mal.structures.Attribute.BOOLEAN_SHORT_FORM}, new Long[] {}));
  /**
   * Operation number literal for operation SETPROVIDEDINTERVALS.
   */
  public static final int _SETPROVIDEDINTERVALS_OP_NUMBER = 4;
  /**
   * Operation number instance for operation SETPROVIDEDINTERVALS.
   */
  public static final org.ccsds.moims.mo.mal.structures.UShort SETPROVIDEDINTERVALS_OP_NUMBER = new org.ccsds.moims.mo.mal.structures.UShort(_SETPROVIDEDINTERVALS_OP_NUMBER);
  /**
   * Operation instance for operation SETPROVIDEDINTERVALS.
   */
  public static final org.ccsds.moims.mo.mal.MALSubmitOperation SETPROVIDEDINTERVALS_OP = new org.ccsds.moims.mo.mal.MALSubmitOperation(SETPROVIDEDINTERVALS_OP_NUMBER, new org.ccsds.moims.mo.mal.structures.Identifier("setProvidedIntervals"), false, new org.ccsds.moims.mo.mal.structures.UShort(1), new org.ccsds.moims.mo.mal.MALOperationStage(new org.ccsds.moims.mo.mal.structures.UOctet((short) 1), new Long[] {org.ccsds.moims.mo.mal.structures.DurationList.SHORT_FORM}, new Long[] {}));
  /**
   * Operation number literal for operation DELETEPARAMETERVALUES.
   */
  public static final int _DELETEPARAMETERVALUES_OP_NUMBER = 5;
  /**
   * Operation number instance for operation DELETEPARAMETERVALUES.
   */
  public static final org.ccsds.moims.mo.mal.structures.UShort DELETEPARAMETERVALUES_OP_NUMBER = new org.ccsds.moims.mo.mal.structures.UShort(_DELETEPARAMETERVALUES_OP_NUMBER);
  /**
   * Operation instance for operation DELETEPARAMETERVALUES.
   */
  public static final org.ccsds.moims.mo.mal.MALSubmitOperation DELETEPARAMETERVALUES_OP = new org.ccsds.moims.mo.mal.MALSubmitOperation(DELETEPARAMETERVALUES_OP_NUMBER, new org.ccsds.moims.mo.mal.structures.Identifier("deleteParameterValues"), false, new org.ccsds.moims.mo.mal.structures.UShort(1), new org.ccsds.moims.mo.mal.MALOperationStage(new org.ccsds.moims.mo.mal.structures.UOctet((short) 1), new Long[] {}, new Long[] {}));
  /**
   * Registers all aspects of this service with the provided element factory.
   * @param bodyElementFactory bodyElementFactory The element factory registry to initialise with this helper.
   * @throws org.ccsds.moims.mo.mal.MALException If cannot initialise this helper.
   */
  public static void init(org.ccsds.moims.mo.mal.MALElementFactoryRegistry bodyElementFactory) throws org.ccsds.moims.mo.mal.MALException
  {
    PARAMETERTEST_SERVICE.addOperation(PUSHPARAMETERVALUE_OP);
    PARAMETERTEST_SERVICE.addOperation(SETVALIDITYSTATEOPTIONS_OP);
    PARAMETERTEST_SERVICE.addOperation(SETREADONLYPARAMETER_OP);
    PARAMETERTEST_SERVICE.addOperation(SETPROVIDEDINTERVALS_OP);
    PARAMETERTEST_SERVICE.addOperation(DELETEPARAMETERVALUES_OP);
    org.ccsds.moims.mo.mcprototype.MCPrototypeHelper.MCPROTOTYPE_AREA.addService(PARAMETERTEST_SERVICE);
  }

  /**
   * Registers all aspects of this service with the provided element factory and any referenced areas/services.
   * @param bodyElementFactory bodyElementFactory The element factory registry to initialise with this helper.
   * @throws org.ccsds.moims.mo.mal.MALException If cannot initialise this helper.
   */
  public static void deepInit(org.ccsds.moims.mo.mal.MALElementFactoryRegistry bodyElementFactory) throws org.ccsds.moims.mo.mal.MALException
  {
    init(bodyElementFactory);
  }

}
