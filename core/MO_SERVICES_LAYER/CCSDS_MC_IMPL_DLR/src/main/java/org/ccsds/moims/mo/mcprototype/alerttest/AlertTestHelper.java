package org.ccsds.moims.mo.mcprototype.alerttest;

/**
 * Helper class for AlertTest service.
 */
public class AlertTestHelper
{
  /**
   * Service number literal.
   */
  public static final int _ALERTTEST_SERVICE_NUMBER = 2;
  /**
   * Service number instance.
   */
  public static final org.ccsds.moims.mo.mal.structures.UShort ALERTTEST_SERVICE_NUMBER = new org.ccsds.moims.mo.mal.structures.UShort(_ALERTTEST_SERVICE_NUMBER);
  /**
   * Service name constant.
   */
  public static final org.ccsds.moims.mo.mal.structures.Identifier ALERTTEST_SERVICE_NAME = new org.ccsds.moims.mo.mal.structures.Identifier("AlertTest");
  /**
   * Service singleton instance.
   */
  public static org.ccsds.moims.mo.com.COMService ALERTTEST_SERVICE = new org.ccsds.moims.mo.com.COMService(ALERTTEST_SERVICE_NUMBER, ALERTTEST_SERVICE_NAME);
  /**
   * Operation number literal for operation RESETTEST.
   */
  public static final int _RESETTEST_OP_NUMBER = 1;
  /**
   * Operation number instance for operation RESETTEST.
   */
  public static final org.ccsds.moims.mo.mal.structures.UShort RESETTEST_OP_NUMBER = new org.ccsds.moims.mo.mal.structures.UShort(_RESETTEST_OP_NUMBER);
  /**
   * Operation instance for operation RESETTEST.
   */
  public static final org.ccsds.moims.mo.mal.MALSubmitOperation RESETTEST_OP = new org.ccsds.moims.mo.mal.MALSubmitOperation(RESETTEST_OP_NUMBER, new org.ccsds.moims.mo.mal.structures.Identifier("resetTest"), false, new org.ccsds.moims.mo.mal.structures.UShort(1), new org.ccsds.moims.mo.mal.MALOperationStage(new org.ccsds.moims.mo.mal.structures.UOctet((short) 1), new Long[] {org.ccsds.moims.mo.mal.structures.Attribute.STRING_SHORT_FORM}, new Long[] {}));
  /**
   * Operation number literal for operation GENERATEALERT.
   */
  public static final int _GENERATEALERT_OP_NUMBER = 2;
  /**
   * Operation number instance for operation GENERATEALERT.
   */
  public static final org.ccsds.moims.mo.mal.structures.UShort GENERATEALERT_OP_NUMBER = new org.ccsds.moims.mo.mal.structures.UShort(_GENERATEALERT_OP_NUMBER);
  /**
   * Operation instance for operation GENERATEALERT.
   */
  public static final org.ccsds.moims.mo.mal.MALRequestOperation GENERATEALERT_OP = new org.ccsds.moims.mo.mal.MALRequestOperation(GENERATEALERT_OP_NUMBER, new org.ccsds.moims.mo.mal.structures.Identifier("generateAlert"), false, new org.ccsds.moims.mo.mal.structures.UShort(1), new org.ccsds.moims.mo.mal.MALOperationStage(new org.ccsds.moims.mo.mal.structures.UOctet((short) 1), new Long[] {org.ccsds.moims.mo.mal.structures.Attribute.LONG_SHORT_FORM, org.ccsds.moims.mo.mc.structures.AttributeValueList.SHORT_FORM, org.ccsds.moims.mo.mal.structures.Attribute.BOOLEAN_SHORT_FORM}, new Long[] {}), new org.ccsds.moims.mo.mal.MALOperationStage(new org.ccsds.moims.mo.mal.structures.UOctet((short) 2), new Long[] {org.ccsds.moims.mo.mal.structures.Attribute.LONG_SHORT_FORM}, new Long[] {}));
  /**
   * Operation number literal for operation ADDALERTDEFINITION.
   */
  public static final int _ADDALERTDEFINITION_OP_NUMBER = 3;
  /**
   * Operation number instance for operation ADDALERTDEFINITION.
   */
  public static final org.ccsds.moims.mo.mal.structures.UShort ADDALERTDEFINITION_OP_NUMBER = new org.ccsds.moims.mo.mal.structures.UShort(_ADDALERTDEFINITION_OP_NUMBER);
  /**
   * Operation instance for operation ADDALERTDEFINITION.
   */
  public static final org.ccsds.moims.mo.mal.MALRequestOperation ADDALERTDEFINITION_OP = new org.ccsds.moims.mo.mal.MALRequestOperation(ADDALERTDEFINITION_OP_NUMBER, new org.ccsds.moims.mo.mal.structures.Identifier("addAlertDefinition"), false, new org.ccsds.moims.mo.mal.structures.UShort(1), new org.ccsds.moims.mo.mal.MALOperationStage(new org.ccsds.moims.mo.mal.structures.UOctet((short) 1), new Long[] {org.ccsds.moims.mo.mc.alert.structures.AlertCreationRequest.SHORT_FORM}, new Long[] {}), new org.ccsds.moims.mo.mal.MALOperationStage(new org.ccsds.moims.mo.mal.structures.UOctet((short) 2), new Long[] {org.ccsds.moims.mo.mc.structures.ObjectInstancePair.SHORT_FORM}, new Long[] {}));
  /**
   * Registers all aspects of this service with the provided element factory.
   * @param bodyElementFactory bodyElementFactory The element factory registry to initialise with this helper.
   * @throws org.ccsds.moims.mo.mal.MALException If cannot initialise this helper.
   */
  public static void init(org.ccsds.moims.mo.mal.MALElementFactoryRegistry bodyElementFactory) throws org.ccsds.moims.mo.mal.MALException
  {
    ALERTTEST_SERVICE.addOperation(RESETTEST_OP);
    ALERTTEST_SERVICE.addOperation(GENERATEALERT_OP);
    ALERTTEST_SERVICE.addOperation(ADDALERTDEFINITION_OP);
    org.ccsds.moims.mo.mcprototype.MCPrototypeHelper.MCPROTOTYPE_AREA.addService(ALERTTEST_SERVICE);
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
