package org.ccsds.moims.mo.mcprototype.aggregationtest;

/**
 * Helper class for AggregationTest service.
 */
public class AggregationTestHelper
{
  /**
   * Service number literal.
   */
  public static final int _AGGREGATIONTEST_SERVICE_NUMBER = 133;
  /**
   * Service number instance.
   */
  public static final org.ccsds.moims.mo.mal.structures.UShort AGGREGATIONTEST_SERVICE_NUMBER = new org.ccsds.moims.mo.mal.structures.UShort(_AGGREGATIONTEST_SERVICE_NUMBER);
  /**
   * Service name constant.
   */
  public static final org.ccsds.moims.mo.mal.structures.Identifier AGGREGATIONTEST_SERVICE_NAME = new org.ccsds.moims.mo.mal.structures.Identifier("AggregationTest");
  /**
   * Service singleton instance.
   */
  public static org.ccsds.moims.mo.com.COMService AGGREGATIONTEST_SERVICE = new org.ccsds.moims.mo.com.COMService(AGGREGATIONTEST_SERVICE_NUMBER, AGGREGATIONTEST_SERVICE_NAME);
  /**
   * Operation number literal for operation TRIGGERAGGREGATIONUPDATE.
   */
  public static final int _TRIGGERAGGREGATIONUPDATE_OP_NUMBER = 1;
  /**
   * Operation number instance for operation TRIGGERAGGREGATIONUPDATE.
   */
  public static final org.ccsds.moims.mo.mal.structures.UShort TRIGGERAGGREGATIONUPDATE_OP_NUMBER = new org.ccsds.moims.mo.mal.structures.UShort(_TRIGGERAGGREGATIONUPDATE_OP_NUMBER);
  /**
   * Operation instance for operation TRIGGERAGGREGATIONUPDATE.
   */
  public static final org.ccsds.moims.mo.mal.MALSubmitOperation TRIGGERAGGREGATIONUPDATE_OP = new org.ccsds.moims.mo.mal.MALSubmitOperation(TRIGGERAGGREGATIONUPDATE_OP_NUMBER, new org.ccsds.moims.mo.mal.structures.Identifier("triggerAggregationUpdate"), false, new org.ccsds.moims.mo.mal.structures.UShort(1), new org.ccsds.moims.mo.mal.MALOperationStage(new org.ccsds.moims.mo.mal.structures.UOctet((short) 1), new Long[] {org.ccsds.moims.mo.mal.structures.Attribute.IDENTIFIER_SHORT_FORM}, new Long[] {}));
  /**
   * Registers all aspects of this service with the provided element factory.
   * @param bodyElementFactory bodyElementFactory The element factory registry to initialise with this helper.
   * @throws org.ccsds.moims.mo.mal.MALException If cannot initialise this helper.
   */
  public static void init(org.ccsds.moims.mo.mal.MALElementFactoryRegistry bodyElementFactory) throws org.ccsds.moims.mo.mal.MALException
  {
    AGGREGATIONTEST_SERVICE.addOperation(TRIGGERAGGREGATIONUPDATE_OP);
    org.ccsds.moims.mo.mcprototype.MCPrototypeHelper.MCPROTOTYPE_AREA.addService(AGGREGATIONTEST_SERVICE);
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
