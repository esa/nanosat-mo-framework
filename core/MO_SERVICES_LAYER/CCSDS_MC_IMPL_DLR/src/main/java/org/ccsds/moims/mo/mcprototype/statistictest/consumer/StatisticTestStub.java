package org.ccsds.moims.mo.mcprototype.statistictest.consumer;

/**
 * Consumer stub for StatisticTest service.
 */
public class StatisticTestStub implements org.ccsds.moims.mo.mcprototype.statistictest.consumer.StatisticTest
{
  private final org.ccsds.moims.mo.mal.consumer.MALConsumer consumer;
  /**
   * Wraps a MALconsumer connection with service specific methods that map from the high level service API to the generic MAL API.
   * @param consumer consumer The MALConsumer to use in this stub.
   */
  public StatisticTestStub(org.ccsds.moims.mo.mal.consumer.MALConsumer consumer)
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
   * Resets the StatisticTest service provider and sets the domain that should be                                 used.
   * @param statisticDomain The domain to be used for the tests.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public void resetTest(String statisticDomain) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    consumer.submit(org.ccsds.moims.mo.mcprototype.statistictest.StatisticTestHelper.RESETTEST_OP, (statisticDomain == null) ? null : new org.ccsds.moims.mo.mal.structures.Union(statisticDomain));
  }

  /**
   * Asynchronous version of method resetTest.
   * @param statisticDomain The domain to be used for the tests.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public org.ccsds.moims.mo.mal.transport.MALMessage asyncResetTest(String statisticDomain, org.ccsds.moims.mo.mcprototype.statistictest.consumer.StatisticTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    return consumer.asyncSubmit(org.ccsds.moims.mo.mcprototype.statistictest.StatisticTestHelper.RESETTEST_OP, adapter, (statisticDomain == null) ? null : new org.ccsds.moims.mo.mal.structures.Union(statisticDomain));
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
  public void continueResetTest(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.statistictest.consumer.StatisticTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    consumer.continueInteraction(org.ccsds.moims.mo.mcprototype.statistictest.StatisticTestHelper.RESETTEST_OP, lastInteractionStage, initiationTimestamp, transactionId, adapter);
  }

  /**
   * Sets the current value of a parameter used in the test.
   * @param id The parameter identifier.
   * @param value The parameter value.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public void setParameterValue(Integer id, Integer value) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    consumer.submit(org.ccsds.moims.mo.mcprototype.statistictest.StatisticTestHelper.SETPARAMETERVALUE_OP, (id == null) ? null : new org.ccsds.moims.mo.mal.structures.Union(id), (value == null) ? null : new org.ccsds.moims.mo.mal.structures.Union(value));
  }

  /**
   * Asynchronous version of method setParameterValue.
   * @param id The parameter identifier.
   * @param value The parameter value.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public org.ccsds.moims.mo.mal.transport.MALMessage asyncSetParameterValue(Integer id, Integer value, org.ccsds.moims.mo.mcprototype.statistictest.consumer.StatisticTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    return consumer.asyncSubmit(org.ccsds.moims.mo.mcprototype.statistictest.StatisticTestHelper.SETPARAMETERVALUE_OP, adapter, (id == null) ? null : new org.ccsds.moims.mo.mal.structures.Union(id), (value == null) ? null : new org.ccsds.moims.mo.mal.structures.Union(value));
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
  public void continueSetParameterValue(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.statistictest.consumer.StatisticTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    consumer.continueInteraction(org.ccsds.moims.mo.mcprototype.statistictest.StatisticTestHelper.SETPARAMETERVALUE_OP, lastInteractionStage, initiationTimestamp, transactionId, adapter);
  }

  /**
   * Add a parameter to the test scenario. Parameter must be one of                                  the pre-defined set defined in service description.
   * @param parName parName Argument number 0 as defined by the service operation.
   * @return The return value of the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public org.ccsds.moims.mo.mc.structures.ObjectInstancePair addParameter(String parName) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    org.ccsds.moims.mo.mal.transport.MALMessageBody body = consumer.request(org.ccsds.moims.mo.mcprototype.statistictest.StatisticTestHelper.ADDPARAMETER_OP, (parName == null) ? null : new org.ccsds.moims.mo.mal.structures.Union(parName));
    Object body0 = (Object) body.getBodyElement(0, new org.ccsds.moims.mo.mc.structures.ObjectInstancePair());
    return (org.ccsds.moims.mo.mc.structures.ObjectInstancePair) body0;
  }

  /**
   * Asynchronous version of method addParameter.
   * @param parName parName Argument number 0 as defined by the service operation.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public org.ccsds.moims.mo.mal.transport.MALMessage asyncAddParameter(String parName, org.ccsds.moims.mo.mcprototype.statistictest.consumer.StatisticTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    return consumer.asyncRequest(org.ccsds.moims.mo.mcprototype.statistictest.StatisticTestHelper.ADDPARAMETER_OP, adapter, (parName == null) ? null : new org.ccsds.moims.mo.mal.structures.Union(parName));
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
  public void continueAddParameter(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.statistictest.consumer.StatisticTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    consumer.continueInteraction(org.ccsds.moims.mo.mcprototype.statistictest.StatisticTestHelper.ADDPARAMETER_OP, lastInteractionStage, initiationTimestamp, transactionId, adapter);
  }

}
