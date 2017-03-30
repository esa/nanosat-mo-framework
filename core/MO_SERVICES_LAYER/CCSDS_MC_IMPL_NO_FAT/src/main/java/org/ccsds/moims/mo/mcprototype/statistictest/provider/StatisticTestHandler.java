package org.ccsds.moims.mo.mcprototype.statistictest.provider;

/**
 * Interface that providers of the StatisticTest service must implement to handle the operations of that service.
 */
public interface StatisticTestHandler
{
  /**
   * Implements the operation resetTest.
   * @param statisticDomain The domain to be used for the tests.
   * @param interaction interaction The MAL object representing the interaction in the provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void resetTest(String statisticDomain, org.ccsds.moims.mo.mal.provider.MALInteraction interaction) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Implements the operation setParameterValue.
   * @param id The parameter identifier.
   * @param value The parameter value.
   * @param interaction interaction The MAL object representing the interaction in the provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void setParameterValue(Integer id, Integer value, org.ccsds.moims.mo.mal.provider.MALInteraction interaction) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Implements the operation addParameter.
   * @param parName parName Argument number 0 as defined by the service operation.
   * @param interaction interaction The MAL object representing the interaction in the provider.
   * @return The return value of the operation.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  org.ccsds.moims.mo.mc.structures.ObjectInstancePair addParameter(String parName, org.ccsds.moims.mo.mal.provider.MALInteraction interaction) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Sets the skeleton to be used for creation of publishers.
   * @param skeleton skeleton The skeleton to be used.
   */
  void setSkeleton(org.ccsds.moims.mo.mcprototype.statistictest.provider.StatisticTestSkeleton skeleton);
}
