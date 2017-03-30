package org.ccsds.moims.mo.mcprototype.aggregationtest.provider;

/**
 * Interface that providers of the AggregationTest service must implement to handle the operations of that service.
 */
public interface AggregationTestHandler
{
  /**
   * Implements the operation triggerAggregationUpdate.
   * @param name .
   * @param interaction interaction The MAL object representing the interaction in the provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void triggerAggregationUpdate(org.ccsds.moims.mo.mal.structures.Identifier name, org.ccsds.moims.mo.mal.provider.MALInteraction interaction) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Sets the skeleton to be used for creation of publishers.
   * @param skeleton skeleton The skeleton to be used.
   */
  void setSkeleton(org.ccsds.moims.mo.mcprototype.aggregationtest.provider.AggregationTestSkeleton skeleton);
}
