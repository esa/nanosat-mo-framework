package org.ccsds.moims.mo.mcprototype.actiontest.consumer;

/**
 * Consumer interface for ActionTest service.
 */
public interface ActionTest
{
  /**
   * Returns the internal MAL consumer object used for sending of messages from this interface.
   * @return The MAL consumer object.
   */
  org.ccsds.moims.mo.mal.consumer.MALConsumer getConsumer();
  /**
   * Resets the ActionTest service provider and sets the source information that should be                                 used for ActivityTracking Events.
   * @param domain The domain field specifies the domain in which tracking events are to be created.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void resetTest(String domain) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Asynchronous version of method resetTest.
   * @param domain The domain field specifies the domain in which tracking events are to be created.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  org.ccsds.moims.mo.mal.transport.MALMessage asyncResetTest(String domain, org.ccsds.moims.mo.mcprototype.actiontest.consumer.ActionTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Continues a previously started interaction.
   * @param lastInteractionStage lastInteractionStage The last stage of the interaction to continue.
   * @param initiationTimestamp initiationTimestamp Timestamp of the interaction initiation message.
   * @param transactionId transactionId Transaction identifier of the interaction to continue.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void continueResetTest(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.actiontest.consumer.ActionTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Add action definitions to the test scenario.
   * @param actionDefinition actionDefinition Argument number 0 as defined by the service operation.
   * @return The return value of the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  org.ccsds.moims.mo.mc.structures.ObjectInstancePair addActionDefinition(org.ccsds.moims.mo.mc.action.structures.ActionCreationRequest actionDefinition) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Asynchronous version of method addActionDefinition.
   * @param actionDefinition actionDefinition Argument number 0 as defined by the service operation.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  org.ccsds.moims.mo.mal.transport.MALMessage asyncAddActionDefinition(org.ccsds.moims.mo.mc.action.structures.ActionCreationRequest actionDefinition, org.ccsds.moims.mo.mcprototype.actiontest.consumer.ActionTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Continues a previously started interaction.
   * @param lastInteractionStage lastInteractionStage The last stage of the interaction to continue.
   * @param initiationTimestamp initiationTimestamp Timestamp of the interaction initiation message.
   * @param transactionId transactionId Transaction identifier of the interaction to continue.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void continueAddActionDefinition(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.actiontest.consumer.ActionTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Force the ActionHandler preCheck function to throw the invalid                                 exception. Index values to be set in the exception are provided.
   * @param force The force field whether the error should be forced or not.
   * @param extra The extra field is the list of index values to be supplied with the exception.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void forcePreCheckInvalidException(Boolean force, org.ccsds.moims.mo.mal.structures.UIntegerList extra) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Asynchronous version of method forcePreCheckInvalidException.
   * @param force The force field whether the error should be forced or not.
   * @param extra The extra field is the list of index values to be supplied with the exception.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  org.ccsds.moims.mo.mal.transport.MALMessage asyncForcePreCheckInvalidException(Boolean force, org.ccsds.moims.mo.mal.structures.UIntegerList extra, org.ccsds.moims.mo.mcprototype.actiontest.consumer.ActionTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Continues a previously started interaction.
   * @param lastInteractionStage lastInteractionStage The last stage of the interaction to continue.
   * @param initiationTimestamp initiationTimestamp Timestamp of the interaction initiation message.
   * @param transactionId transactionId Transaction identifier of the interaction to continue.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void continueForcePreCheckInvalidException(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.actiontest.consumer.ActionTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Force the ActionHandler preCheck function to return failure.
   * @param force The force field whether the error should be forced or not.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void forcePreCheckFailure(Boolean force) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Asynchronous version of method forcePreCheckFailure.
   * @param force The force field whether the error should be forced or not.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  org.ccsds.moims.mo.mal.transport.MALMessage asyncForcePreCheckFailure(Boolean force, org.ccsds.moims.mo.mcprototype.actiontest.consumer.ActionTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Continues a previously started interaction.
   * @param lastInteractionStage lastInteractionStage The last stage of the interaction to continue.
   * @param initiationTimestamp initiationTimestamp Timestamp of the interaction initiation message.
   * @param transactionId transactionId Transaction identifier of the interaction to continue.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void continueForcePreCheckFailure(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.actiontest.consumer.ActionTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Set the stage number at which the next action instance should fail.                               0=No failure, 1=Start activity, 2+=Progress/Completion depending upon progressStepCount of related definition .
   * @param stage The step number at which to fail, where 0 is not failure.
   * @param failureCode If failure is set, the failure code to be returned in the ActionFailure event.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void setFailureStage(org.ccsds.moims.mo.mal.structures.UInteger stage, org.ccsds.moims.mo.mal.structures.UInteger failureCode) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Asynchronous version of method setFailureStage.
   * @param stage The step number at which to fail, where 0 is not failure.
   * @param failureCode If failure is set, the failure code to be returned in the ActionFailure event.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  org.ccsds.moims.mo.mal.transport.MALMessage asyncSetFailureStage(org.ccsds.moims.mo.mal.structures.UInteger stage, org.ccsds.moims.mo.mal.structures.UInteger failureCode, org.ccsds.moims.mo.mcprototype.actiontest.consumer.ActionTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Continues a previously started interaction.
   * @param lastInteractionStage lastInteractionStage The last stage of the interaction to continue.
   * @param initiationTimestamp initiationTimestamp Timestamp of the interaction initiation message.
   * @param transactionId transactionId Transaction identifier of the interaction to continue.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void continueSetFailureStage(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.actiontest.consumer.ActionTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Set the minumum execution time for actions.                               If the value is 0 or not set there is no minimum .
   * @param time The minimum execution time in seconds.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void setMinimumActionExecutionTime(org.ccsds.moims.mo.mal.structures.UInteger time) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Asynchronous version of method setMinimumActionExecutionTime.
   * @param time The minimum execution time in seconds.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  org.ccsds.moims.mo.mal.transport.MALMessage asyncSetMinimumActionExecutionTime(org.ccsds.moims.mo.mal.structures.UInteger time, org.ccsds.moims.mo.mcprototype.actiontest.consumer.ActionTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Continues a previously started interaction.
   * @param lastInteractionStage lastInteractionStage The last stage of the interaction to continue.
   * @param initiationTimestamp initiationTimestamp Timestamp of the interaction initiation message.
   * @param transactionId transactionId Transaction identifier of the interaction to continue.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void continueSetMinimumActionExecutionTime(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.actiontest.consumer.ActionTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
}
