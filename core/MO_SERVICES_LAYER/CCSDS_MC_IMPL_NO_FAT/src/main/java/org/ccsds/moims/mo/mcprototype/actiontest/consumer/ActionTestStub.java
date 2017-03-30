package org.ccsds.moims.mo.mcprototype.actiontest.consumer;

/**
 * Consumer stub for ActionTest service.
 */
public class ActionTestStub implements org.ccsds.moims.mo.mcprototype.actiontest.consumer.ActionTest
{
  private final org.ccsds.moims.mo.mal.consumer.MALConsumer consumer;
  /**
   * Wraps a MALconsumer connection with service specific methods that map from the high level service API to the generic MAL API.
   * @param consumer consumer The MALConsumer to use in this stub.
   */
  public ActionTestStub(org.ccsds.moims.mo.mal.consumer.MALConsumer consumer)
  {
    this.consumer = consumer;
  }

  /**
   * Returns the internal MAL consumer object used for sending of messages from this interface.
   * @return The MAL consumer object.
   */
  public org.ccsds.moims.mo.mal.consumer.MALConsumer getConsumer()
  {
    return consumer;
  }

  /**
   * Resets the ActionTest service provider and sets the source information that should be                                 used for ActivityTracking Events.
   * @param domain The domain field specifies the domain in which tracking events are to be created.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public void resetTest(String domain) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    consumer.submit(org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper.RESETTEST_OP, (domain == null) ? null : new org.ccsds.moims.mo.mal.structures.Union(domain));
  }

  /**
   * Asynchronous version of method resetTest.
   * @param domain The domain field specifies the domain in which tracking events are to be created.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public org.ccsds.moims.mo.mal.transport.MALMessage asyncResetTest(String domain, org.ccsds.moims.mo.mcprototype.actiontest.consumer.ActionTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    return consumer.asyncSubmit(org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper.RESETTEST_OP, adapter, (domain == null) ? null : new org.ccsds.moims.mo.mal.structures.Union(domain));
  }

  /**
   * Continues a previously started interaction.
   * @param lastInteractionStage lastInteractionStage The last stage of the interaction to continue.
   * @param initiationTimestamp initiationTimestamp Timestamp of the interaction initiation message.
   * @param transactionId transactionId Transaction identifier of the interaction to continue.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public void continueResetTest(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.actiontest.consumer.ActionTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    consumer.continueInteraction(org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper.RESETTEST_OP, lastInteractionStage, initiationTimestamp, transactionId, adapter);
  }

  /**
   * Add action definitions to the test scenario.
   * @param actionDefinition actionDefinition Argument number 0 as defined by the service operation.
   * @return The return value of the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public org.ccsds.moims.mo.mc.structures.ObjectInstancePair addActionDefinition(org.ccsds.moims.mo.mc.action.structures.ActionCreationRequest actionDefinition) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    org.ccsds.moims.mo.mal.transport.MALMessageBody body = consumer.request(org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper.ADDACTIONDEFINITION_OP, actionDefinition);
    Object body0 = (Object) body.getBodyElement(0, new org.ccsds.moims.mo.mc.structures.ObjectInstancePair());
    return (org.ccsds.moims.mo.mc.structures.ObjectInstancePair) body0;
  }

  /**
   * Asynchronous version of method addActionDefinition.
   * @param actionDefinition actionDefinition Argument number 0 as defined by the service operation.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public org.ccsds.moims.mo.mal.transport.MALMessage asyncAddActionDefinition(org.ccsds.moims.mo.mc.action.structures.ActionCreationRequest actionDefinition, org.ccsds.moims.mo.mcprototype.actiontest.consumer.ActionTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    return consumer.asyncRequest(org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper.ADDACTIONDEFINITION_OP, adapter, actionDefinition);
  }

  /**
   * Continues a previously started interaction.
   * @param lastInteractionStage lastInteractionStage The last stage of the interaction to continue.
   * @param initiationTimestamp initiationTimestamp Timestamp of the interaction initiation message.
   * @param transactionId transactionId Transaction identifier of the interaction to continue.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public void continueAddActionDefinition(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.actiontest.consumer.ActionTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    consumer.continueInteraction(org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper.ADDACTIONDEFINITION_OP, lastInteractionStage, initiationTimestamp, transactionId, adapter);
  }

  /**
   * Force the ActionHandler preCheck function to throw the invalid                                 exception. Index values to be set in the exception are provided.
   * @param force The force field whether the error should be forced or not.
   * @param extra The extra field is the list of index values to be supplied with the exception.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public void forcePreCheckInvalidException(Boolean force, org.ccsds.moims.mo.mal.structures.UIntegerList extra) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    consumer.submit(org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper.FORCEPRECHECKINVALIDEXCEPTION_OP, (force == null) ? null : new org.ccsds.moims.mo.mal.structures.Union(force), extra);
  }

  /**
   * Asynchronous version of method forcePreCheckInvalidException.
   * @param force The force field whether the error should be forced or not.
   * @param extra The extra field is the list of index values to be supplied with the exception.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public org.ccsds.moims.mo.mal.transport.MALMessage asyncForcePreCheckInvalidException(Boolean force, org.ccsds.moims.mo.mal.structures.UIntegerList extra, org.ccsds.moims.mo.mcprototype.actiontest.consumer.ActionTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    return consumer.asyncSubmit(org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper.FORCEPRECHECKINVALIDEXCEPTION_OP, adapter, (force == null) ? null : new org.ccsds.moims.mo.mal.structures.Union(force), extra);
  }

  /**
   * Continues a previously started interaction.
   * @param lastInteractionStage lastInteractionStage The last stage of the interaction to continue.
   * @param initiationTimestamp initiationTimestamp Timestamp of the interaction initiation message.
   * @param transactionId transactionId Transaction identifier of the interaction to continue.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public void continueForcePreCheckInvalidException(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.actiontest.consumer.ActionTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    consumer.continueInteraction(org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper.FORCEPRECHECKINVALIDEXCEPTION_OP, lastInteractionStage, initiationTimestamp, transactionId, adapter);
  }

  /**
   * Force the ActionHandler preCheck function to return failure.
   * @param force The force field whether the error should be forced or not.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public void forcePreCheckFailure(Boolean force) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    consumer.submit(org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper.FORCEPRECHECKFAILURE_OP, (force == null) ? null : new org.ccsds.moims.mo.mal.structures.Union(force));
  }

  /**
   * Asynchronous version of method forcePreCheckFailure.
   * @param force The force field whether the error should be forced or not.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public org.ccsds.moims.mo.mal.transport.MALMessage asyncForcePreCheckFailure(Boolean force, org.ccsds.moims.mo.mcprototype.actiontest.consumer.ActionTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    return consumer.asyncSubmit(org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper.FORCEPRECHECKFAILURE_OP, adapter, (force == null) ? null : new org.ccsds.moims.mo.mal.structures.Union(force));
  }

  /**
   * Continues a previously started interaction.
   * @param lastInteractionStage lastInteractionStage The last stage of the interaction to continue.
   * @param initiationTimestamp initiationTimestamp Timestamp of the interaction initiation message.
   * @param transactionId transactionId Transaction identifier of the interaction to continue.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public void continueForcePreCheckFailure(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.actiontest.consumer.ActionTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    consumer.continueInteraction(org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper.FORCEPRECHECKFAILURE_OP, lastInteractionStage, initiationTimestamp, transactionId, adapter);
  }

  /**
   * Set the stage number at which the next action instance should fail.                               0=No failure, 1=Start activity, 2+=Progress/Completion depending upon progressStepCount of related definition .
   * @param stage The step number at which to fail, where 0 is not failure.
   * @param failureCode If failure is set, the failure code to be returned in the ActionFailure event.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public void setFailureStage(org.ccsds.moims.mo.mal.structures.UInteger stage, org.ccsds.moims.mo.mal.structures.UInteger failureCode) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    consumer.submit(org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper.SETFAILURESTAGE_OP, stage, failureCode);
  }

  /**
   * Asynchronous version of method setFailureStage.
   * @param stage The step number at which to fail, where 0 is not failure.
   * @param failureCode If failure is set, the failure code to be returned in the ActionFailure event.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public org.ccsds.moims.mo.mal.transport.MALMessage asyncSetFailureStage(org.ccsds.moims.mo.mal.structures.UInteger stage, org.ccsds.moims.mo.mal.structures.UInteger failureCode, org.ccsds.moims.mo.mcprototype.actiontest.consumer.ActionTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    return consumer.asyncSubmit(org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper.SETFAILURESTAGE_OP, adapter, stage, failureCode);
  }

  /**
   * Continues a previously started interaction.
   * @param lastInteractionStage lastInteractionStage The last stage of the interaction to continue.
   * @param initiationTimestamp initiationTimestamp Timestamp of the interaction initiation message.
   * @param transactionId transactionId Transaction identifier of the interaction to continue.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public void continueSetFailureStage(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.actiontest.consumer.ActionTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    consumer.continueInteraction(org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper.SETFAILURESTAGE_OP, lastInteractionStage, initiationTimestamp, transactionId, adapter);
  }

  /**
   * Set the minumum execution time for actions.                               If the value is 0 or not set there is no minimum .
   * @param time The minimum execution time in seconds.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public void setMinimumActionExecutionTime(org.ccsds.moims.mo.mal.structures.UInteger time) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    consumer.submit(org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper.SETMINIMUMACTIONEXECUTIONTIME_OP, time);
  }

  /**
   * Asynchronous version of method setMinimumActionExecutionTime.
   * @param time The minimum execution time in seconds.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public org.ccsds.moims.mo.mal.transport.MALMessage asyncSetMinimumActionExecutionTime(org.ccsds.moims.mo.mal.structures.UInteger time, org.ccsds.moims.mo.mcprototype.actiontest.consumer.ActionTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    return consumer.asyncSubmit(org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper.SETMINIMUMACTIONEXECUTIONTIME_OP, adapter, time);
  }

  /**
   * Continues a previously started interaction.
   * @param lastInteractionStage lastInteractionStage The last stage of the interaction to continue.
   * @param initiationTimestamp initiationTimestamp Timestamp of the interaction initiation message.
   * @param transactionId transactionId Transaction identifier of the interaction to continue.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public void continueSetMinimumActionExecutionTime(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.actiontest.consumer.ActionTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    consumer.continueInteraction(org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper.SETMINIMUMACTIONEXECUTIONTIME_OP, lastInteractionStage, initiationTimestamp, transactionId, adapter);
  }

}
