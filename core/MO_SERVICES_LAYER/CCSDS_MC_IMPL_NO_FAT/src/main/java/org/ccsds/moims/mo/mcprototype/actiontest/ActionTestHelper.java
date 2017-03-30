package org.ccsds.moims.mo.mcprototype.actiontest;

/**
 * Helper class for ActionTest service.
 */
public class ActionTestHelper
{
  /**
   * Service number literal.
   */
  public static final int _ACTIONTEST_SERVICE_NUMBER = 3;
  /**
   * Service number instance.
   */
  public static final org.ccsds.moims.mo.mal.structures.UShort ACTIONTEST_SERVICE_NUMBER = new org.ccsds.moims.mo.mal.structures.UShort(_ACTIONTEST_SERVICE_NUMBER);
  /**
   * Service name constant.
   */
  public static final org.ccsds.moims.mo.mal.structures.Identifier ACTIONTEST_SERVICE_NAME = new org.ccsds.moims.mo.mal.structures.Identifier("ActionTest");
  /**
   * Service singleton instance.
   */
  public static org.ccsds.moims.mo.com.COMService ACTIONTEST_SERVICE = new org.ccsds.moims.mo.com.COMService(ACTIONTEST_SERVICE_NUMBER, ACTIONTEST_SERVICE_NAME);
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
   * Operation number literal for operation ADDACTIONDEFINITION.
   */
  public static final int _ADDACTIONDEFINITION_OP_NUMBER = 3;
  /**
   * Operation number instance for operation ADDACTIONDEFINITION.
   */
  public static final org.ccsds.moims.mo.mal.structures.UShort ADDACTIONDEFINITION_OP_NUMBER = new org.ccsds.moims.mo.mal.structures.UShort(_ADDACTIONDEFINITION_OP_NUMBER);
  /**
   * Operation instance for operation ADDACTIONDEFINITION.
   */
  public static final org.ccsds.moims.mo.mal.MALRequestOperation ADDACTIONDEFINITION_OP = new org.ccsds.moims.mo.mal.MALRequestOperation(ADDACTIONDEFINITION_OP_NUMBER, new org.ccsds.moims.mo.mal.structures.Identifier("addActionDefinition"), false, new org.ccsds.moims.mo.mal.structures.UShort(1), new org.ccsds.moims.mo.mal.MALOperationStage(new org.ccsds.moims.mo.mal.structures.UOctet((short) 1), new Long[] {org.ccsds.moims.mo.mc.action.structures.ActionCreationRequest.SHORT_FORM}, new Long[] {}), new org.ccsds.moims.mo.mal.MALOperationStage(new org.ccsds.moims.mo.mal.structures.UOctet((short) 2), new Long[] {org.ccsds.moims.mo.mc.structures.ObjectInstancePair.SHORT_FORM}, new Long[] {}));
  /**
   * Operation number literal for operation FORCEPRECHECKINVALIDEXCEPTION.
   */
  public static final int _FORCEPRECHECKINVALIDEXCEPTION_OP_NUMBER = 4;
  /**
   * Operation number instance for operation FORCEPRECHECKINVALIDEXCEPTION.
   */
  public static final org.ccsds.moims.mo.mal.structures.UShort FORCEPRECHECKINVALIDEXCEPTION_OP_NUMBER = new org.ccsds.moims.mo.mal.structures.UShort(_FORCEPRECHECKINVALIDEXCEPTION_OP_NUMBER);
  /**
   * Operation instance for operation FORCEPRECHECKINVALIDEXCEPTION.
   */
  public static final org.ccsds.moims.mo.mal.MALSubmitOperation FORCEPRECHECKINVALIDEXCEPTION_OP = new org.ccsds.moims.mo.mal.MALSubmitOperation(FORCEPRECHECKINVALIDEXCEPTION_OP_NUMBER, new org.ccsds.moims.mo.mal.structures.Identifier("forcePreCheckInvalidException"), false, new org.ccsds.moims.mo.mal.structures.UShort(1), new org.ccsds.moims.mo.mal.MALOperationStage(new org.ccsds.moims.mo.mal.structures.UOctet((short) 1), new Long[] {org.ccsds.moims.mo.mal.structures.Attribute.BOOLEAN_SHORT_FORM, org.ccsds.moims.mo.mal.structures.UIntegerList.SHORT_FORM}, new Long[] {}));
  /**
   * Operation number literal for operation FORCEPRECHECKFAILURE.
   */
  public static final int _FORCEPRECHECKFAILURE_OP_NUMBER = 5;
  /**
   * Operation number instance for operation FORCEPRECHECKFAILURE.
   */
  public static final org.ccsds.moims.mo.mal.structures.UShort FORCEPRECHECKFAILURE_OP_NUMBER = new org.ccsds.moims.mo.mal.structures.UShort(_FORCEPRECHECKFAILURE_OP_NUMBER);
  /**
   * Operation instance for operation FORCEPRECHECKFAILURE.
   */
  public static final org.ccsds.moims.mo.mal.MALSubmitOperation FORCEPRECHECKFAILURE_OP = new org.ccsds.moims.mo.mal.MALSubmitOperation(FORCEPRECHECKFAILURE_OP_NUMBER, new org.ccsds.moims.mo.mal.structures.Identifier("forcePreCheckFailure"), false, new org.ccsds.moims.mo.mal.structures.UShort(1), new org.ccsds.moims.mo.mal.MALOperationStage(new org.ccsds.moims.mo.mal.structures.UOctet((short) 1), new Long[] {org.ccsds.moims.mo.mal.structures.Attribute.BOOLEAN_SHORT_FORM}, new Long[] {}));
  /**
   * Operation number literal for operation SETFAILURESTAGE.
   */
  public static final int _SETFAILURESTAGE_OP_NUMBER = 6;
  /**
   * Operation number instance for operation SETFAILURESTAGE.
   */
  public static final org.ccsds.moims.mo.mal.structures.UShort SETFAILURESTAGE_OP_NUMBER = new org.ccsds.moims.mo.mal.structures.UShort(_SETFAILURESTAGE_OP_NUMBER);
  /**
   * Operation instance for operation SETFAILURESTAGE.
   */
  public static final org.ccsds.moims.mo.mal.MALSubmitOperation SETFAILURESTAGE_OP = new org.ccsds.moims.mo.mal.MALSubmitOperation(SETFAILURESTAGE_OP_NUMBER, new org.ccsds.moims.mo.mal.structures.Identifier("setFailureStage"), false, new org.ccsds.moims.mo.mal.structures.UShort(1), new org.ccsds.moims.mo.mal.MALOperationStage(new org.ccsds.moims.mo.mal.structures.UOctet((short) 1), new Long[] {org.ccsds.moims.mo.mal.structures.Attribute.UINTEGER_SHORT_FORM, org.ccsds.moims.mo.mal.structures.Attribute.UINTEGER_SHORT_FORM}, new Long[] {}));
  /**
   * Operation number literal for operation SETMINIMUMACTIONEXECUTIONTIME.
   */
  public static final int _SETMINIMUMACTIONEXECUTIONTIME_OP_NUMBER = 7;
  /**
   * Operation number instance for operation SETMINIMUMACTIONEXECUTIONTIME.
   */
  public static final org.ccsds.moims.mo.mal.structures.UShort SETMINIMUMACTIONEXECUTIONTIME_OP_NUMBER = new org.ccsds.moims.mo.mal.structures.UShort(_SETMINIMUMACTIONEXECUTIONTIME_OP_NUMBER);
  /**
   * Operation instance for operation SETMINIMUMACTIONEXECUTIONTIME.
   */
  public static final org.ccsds.moims.mo.mal.MALSubmitOperation SETMINIMUMACTIONEXECUTIONTIME_OP = new org.ccsds.moims.mo.mal.MALSubmitOperation(SETMINIMUMACTIONEXECUTIONTIME_OP_NUMBER, new org.ccsds.moims.mo.mal.structures.Identifier("setMinimumActionExecutionTime"), false, new org.ccsds.moims.mo.mal.structures.UShort(1), new org.ccsds.moims.mo.mal.MALOperationStage(new org.ccsds.moims.mo.mal.structures.UOctet((short) 1), new Long[] {org.ccsds.moims.mo.mal.structures.Attribute.UINTEGER_SHORT_FORM}, new Long[] {}));
  /**
   * Registers all aspects of this service with the provided element factory.
   * @param bodyElementFactory bodyElementFactory The element factory registry to initialise with this helper.
   * @throws org.ccsds.moims.mo.mal.MALException If cannot initialise this helper.
   */
  public static void init(org.ccsds.moims.mo.mal.MALElementFactoryRegistry bodyElementFactory) throws org.ccsds.moims.mo.mal.MALException
  {
    ACTIONTEST_SERVICE.addOperation(RESETTEST_OP);
    ACTIONTEST_SERVICE.addOperation(ADDACTIONDEFINITION_OP);
    ACTIONTEST_SERVICE.addOperation(FORCEPRECHECKINVALIDEXCEPTION_OP);
    ACTIONTEST_SERVICE.addOperation(FORCEPRECHECKFAILURE_OP);
    ACTIONTEST_SERVICE.addOperation(SETFAILURESTAGE_OP);
    ACTIONTEST_SERVICE.addOperation(SETMINIMUMACTIONEXECUTIONTIME_OP);
    org.ccsds.moims.mo.mcprototype.MCPrototypeHelper.MCPROTOTYPE_AREA.addService(ACTIONTEST_SERVICE);
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
