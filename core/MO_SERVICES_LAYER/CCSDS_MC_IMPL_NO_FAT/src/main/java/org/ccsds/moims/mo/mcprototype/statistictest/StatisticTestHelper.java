package org.ccsds.moims.mo.mcprototype.statistictest;

/**
 * Helper class for StatisticTest service.
 */
public class StatisticTestHelper
{
  /**
   * Service number literal.
   */
  public static final int _STATISTICTEST_SERVICE_NUMBER = 134;
  /**
   * Service number instance.
   */
  public static final org.ccsds.moims.mo.mal.structures.UShort STATISTICTEST_SERVICE_NUMBER = new org.ccsds.moims.mo.mal.structures.UShort(_STATISTICTEST_SERVICE_NUMBER);
  /**
   * Service name constant.
   */
  public static final org.ccsds.moims.mo.mal.structures.Identifier STATISTICTEST_SERVICE_NAME = new org.ccsds.moims.mo.mal.structures.Identifier("StatisticTest");
  /**
   * Service singleton instance.
   */
  public static org.ccsds.moims.mo.com.COMService STATISTICTEST_SERVICE = new org.ccsds.moims.mo.com.COMService(STATISTICTEST_SERVICE_NUMBER, STATISTICTEST_SERVICE_NAME);
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
   * Operation number literal for operation SETPARAMETERVALUE.
   */
  public static final int _SETPARAMETERVALUE_OP_NUMBER = 2;
  /**
   * Operation number instance for operation SETPARAMETERVALUE.
   */
  public static final org.ccsds.moims.mo.mal.structures.UShort SETPARAMETERVALUE_OP_NUMBER = new org.ccsds.moims.mo.mal.structures.UShort(_SETPARAMETERVALUE_OP_NUMBER);
  /**
   * Operation instance for operation SETPARAMETERVALUE.
   */
  public static final org.ccsds.moims.mo.mal.MALSubmitOperation SETPARAMETERVALUE_OP = new org.ccsds.moims.mo.mal.MALSubmitOperation(SETPARAMETERVALUE_OP_NUMBER, new org.ccsds.moims.mo.mal.structures.Identifier("setParameterValue"), false, new org.ccsds.moims.mo.mal.structures.UShort(1), new org.ccsds.moims.mo.mal.MALOperationStage(new org.ccsds.moims.mo.mal.structures.UOctet((short) 1), new Long[] {org.ccsds.moims.mo.mal.structures.Attribute.INTEGER_SHORT_FORM, org.ccsds.moims.mo.mal.structures.Attribute.INTEGER_SHORT_FORM}, new Long[] {}));
  /**
   * Operation number literal for operation ADDPARAMETER.
   */
  public static final int _ADDPARAMETER_OP_NUMBER = 3;
  /**
   * Operation number instance for operation ADDPARAMETER.
   */
  public static final org.ccsds.moims.mo.mal.structures.UShort ADDPARAMETER_OP_NUMBER = new org.ccsds.moims.mo.mal.structures.UShort(_ADDPARAMETER_OP_NUMBER);
  /**
   * Operation instance for operation ADDPARAMETER.
   */
  public static final org.ccsds.moims.mo.mal.MALRequestOperation ADDPARAMETER_OP = new org.ccsds.moims.mo.mal.MALRequestOperation(ADDPARAMETER_OP_NUMBER, new org.ccsds.moims.mo.mal.structures.Identifier("addParameter"), false, new org.ccsds.moims.mo.mal.structures.UShort(1), new org.ccsds.moims.mo.mal.MALOperationStage(new org.ccsds.moims.mo.mal.structures.UOctet((short) 1), new Long[] {org.ccsds.moims.mo.mal.structures.Attribute.STRING_SHORT_FORM}, new Long[] {}), new org.ccsds.moims.mo.mal.MALOperationStage(new org.ccsds.moims.mo.mal.structures.UOctet((short) 2), new Long[] {org.ccsds.moims.mo.mc.structures.ObjectInstancePair.SHORT_FORM}, new Long[] {}));
  /**
   * Registers all aspects of this service with the provided element factory.
   * @param bodyElementFactory bodyElementFactory The element factory registry to initialise with this helper.
   * @throws org.ccsds.moims.mo.mal.MALException If cannot initialise this helper.
   */
  public static void init(org.ccsds.moims.mo.mal.MALElementFactoryRegistry bodyElementFactory) throws org.ccsds.moims.mo.mal.MALException
  {
    STATISTICTEST_SERVICE.addOperation(RESETTEST_OP);
    STATISTICTEST_SERVICE.addOperation(SETPARAMETERVALUE_OP);
    STATISTICTEST_SERVICE.addOperation(ADDPARAMETER_OP);
    org.ccsds.moims.mo.mcprototype.MCPrototypeHelper.MCPROTOTYPE_AREA.addService(STATISTICTEST_SERVICE);
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
