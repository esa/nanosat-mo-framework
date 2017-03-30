package org.ccsds.moims.mo.mcprototype;

/**
 * Helper class for MCPrototype area.
 */
public class MCPrototypeHelper
{
  /**
   * Area number literal.
   */
  public static final int _MCPROTOTYPE_AREA_NUMBER = 210;
  /**
   * Area number instance.
   */
  public static final org.ccsds.moims.mo.mal.structures.UShort MCPROTOTYPE_AREA_NUMBER = new org.ccsds.moims.mo.mal.structures.UShort(_MCPROTOTYPE_AREA_NUMBER);
  /**
   * Area name constant.
   */
  public static final org.ccsds.moims.mo.mal.structures.Identifier MCPROTOTYPE_AREA_NAME = new org.ccsds.moims.mo.mal.structures.Identifier("MCPrototype");
  /**
   * Area version literal.
   */
  public static final short _MCPROTOTYPE_AREA_VERSION = 1;
  /**
   * Area version instance.
   */
  public static final org.ccsds.moims.mo.mal.structures.UOctet MCPROTOTYPE_AREA_VERSION = new org.ccsds.moims.mo.mal.structures.UOctet(_MCPROTOTYPE_AREA_VERSION);
  /**
   * Area singleton instance.
   */
  public static org.ccsds.moims.mo.mal.MALArea MCPROTOTYPE_AREA = new org.ccsds.moims.mo.mal.MALArea(MCPROTOTYPE_AREA_NUMBER, MCPROTOTYPE_AREA_NAME, new org.ccsds.moims.mo.mal.structures.UOctet((short) 1));
  /**
   * Registers all aspects of this area with the provided element factory.
   * @param bodyElementFactory bodyElementFactory The element factory registry to initialise with this helper.
   * @throws org.ccsds.moims.mo.mal.MALException If cannot initialise this helper.
   */
  public static void init(org.ccsds.moims.mo.mal.MALElementFactoryRegistry bodyElementFactory) throws org.ccsds.moims.mo.mal.MALException
  {
    org.ccsds.moims.mo.mal.MALContextFactory.registerArea(MCPROTOTYPE_AREA);
  }

  /**
   * Registers all aspects of this area with the provided element factory and any referenced areas and contained services.
   * @param bodyElementFactory bodyElementFactory The element factory registry to initialise with this helper.
   * @throws org.ccsds.moims.mo.mal.MALException If cannot initialise this helper.
   */
  public static void deepInit(org.ccsds.moims.mo.mal.MALElementFactoryRegistry bodyElementFactory) throws org.ccsds.moims.mo.mal.MALException
  {
    init(bodyElementFactory);
    org.ccsds.moims.mo.mcprototype.alerttest.AlertTestHelper.deepInit(bodyElementFactory);
    org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper.deepInit(bodyElementFactory);
    org.ccsds.moims.mo.mcprototype.parametertest.ParameterTestHelper.deepInit(bodyElementFactory);
    org.ccsds.moims.mo.mcprototype.aggregationtest.AggregationTestHelper.deepInit(bodyElementFactory);
    org.ccsds.moims.mo.mcprototype.statistictest.StatisticTestHelper.deepInit(bodyElementFactory);
  }

}
