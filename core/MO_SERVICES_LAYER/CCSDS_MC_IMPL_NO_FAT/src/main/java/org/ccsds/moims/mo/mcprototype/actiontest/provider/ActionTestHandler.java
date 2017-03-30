package org.ccsds.moims.mo.mcprototype.actiontest.provider;

/**
 * Interface that providers of the ActionTest service must implement to handle the operations of that service.
 */
public interface ActionTestHandler
{
  /**
   * Implements the operation resetTest.
   * @param domain The domain field specifies the domain in which tracking events are to be created.
   * @param interaction interaction The MAL object representing the interaction in the provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void resetTest(String domain, org.ccsds.moims.mo.mal.provider.MALInteraction interaction) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Implements the operation addActionDefinition.
   * @param actionDefinition actionDefinition Argument number 0 as defined by the service operation.
   * @param interaction interaction The MAL object representing the interaction in the provider.
   * @return The return value of the operation.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  org.ccsds.moims.mo.mc.structures.ObjectInstancePair addActionDefinition(org.ccsds.moims.mo.mc.action.structures.ActionCreationRequest actionDefinition, org.ccsds.moims.mo.mal.provider.MALInteraction interaction) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Implements the operation forcePreCheckInvalidException.
   * @param force The force field whether the error should be forced or not.
   * @param extra The extra field is the list of index values to be supplied with the exception.
   * @param interaction interaction The MAL object representing the interaction in the provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void forcePreCheckInvalidException(Boolean force, org.ccsds.moims.mo.mal.structures.UIntegerList extra, org.ccsds.moims.mo.mal.provider.MALInteraction interaction) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Implements the operation forcePreCheckFailure.
   * @param force The force field whether the error should be forced or not.
   * @param interaction interaction The MAL object representing the interaction in the provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void forcePreCheckFailure(Boolean force, org.ccsds.moims.mo.mal.provider.MALInteraction interaction) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Implements the operation setFailureStage.
   * @param stage The step number at which to fail, where 0 is not failure.
   * @param failureCode If failure is set, the failure code to be returned in the ActionFailure event.
   * @param interaction interaction The MAL object representing the interaction in the provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void setFailureStage(org.ccsds.moims.mo.mal.structures.UInteger stage, org.ccsds.moims.mo.mal.structures.UInteger failureCode, org.ccsds.moims.mo.mal.provider.MALInteraction interaction) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Implements the operation setMinimumActionExecutionTime.
   * @param time The minimum execution time in seconds.
   * @param interaction interaction The MAL object representing the interaction in the provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void setMinimumActionExecutionTime(org.ccsds.moims.mo.mal.structures.UInteger time, org.ccsds.moims.mo.mal.provider.MALInteraction interaction) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Sets the skeleton to be used for creation of publishers.
   * @param skeleton skeleton The skeleton to be used.
   */
  void setSkeleton(org.ccsds.moims.mo.mcprototype.actiontest.provider.ActionTestSkeleton skeleton);
}
