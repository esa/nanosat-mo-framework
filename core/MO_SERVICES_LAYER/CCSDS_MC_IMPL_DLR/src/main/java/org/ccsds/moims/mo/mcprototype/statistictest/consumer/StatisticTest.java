package org.ccsds.moims.mo.mcprototype.statistictest.consumer;

/**
 * Consumer interface for StatisticTest service.
 */
public interface StatisticTest
{
  /**
   * Returns the internal MAL consumer object used for sending of messages from this interface.
   * @return The MAL consumer object.
   */
  org.ccsds.moims.mo.mal.consumer.MALConsumer getConsumer();
  /**
   * Resets the StatisticTest service provider and sets the domain that should be                                 used.
   * @param statisticDomain The domain to be used for the tests.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void resetTest(String statisticDomain) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Asynchronous version of method resetTest.
   * @param statisticDomain The domain to be used for the tests.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  org.ccsds.moims.mo.mal.transport.MALMessage asyncResetTest(String statisticDomain, org.ccsds.moims.mo.mcprototype.statistictest.consumer.StatisticTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Continues a previously started interaction.
   * @param lastInteractionStage lastInteractionStage The last stage of the interaction to continue.
   * @param initiationTimestamp initiationTimestamp Timestamp of the interaction initiation message.
   * @param transactionId transactionId Transaction identifier of the interaction to continue.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void continueResetTest(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.statistictest.consumer.StatisticTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Sets the current value of a parameter used in the test.
   * @param id The parameter identifier.
   * @param value The parameter value.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void setParameterValue(Integer id, Integer value) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Asynchronous version of method setParameterValue.
   * @param id The parameter identifier.
   * @param value The parameter value.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  org.ccsds.moims.mo.mal.transport.MALMessage asyncSetParameterValue(Integer id, Integer value, org.ccsds.moims.mo.mcprototype.statistictest.consumer.StatisticTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Continues a previously started interaction.
   * @param lastInteractionStage lastInteractionStage The last stage of the interaction to continue.
   * @param initiationTimestamp initiationTimestamp Timestamp of the interaction initiation message.
   * @param transactionId transactionId Transaction identifier of the interaction to continue.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void continueSetParameterValue(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.statistictest.consumer.StatisticTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Add a parameter to the test scenario. Parameter must be one of                                  the pre-defined set defined in service description.
   * @param parName parName Argument number 0 as defined by the service operation.
   * @return The return value of the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  org.ccsds.moims.mo.mc.structures.ObjectInstancePair addParameter(String parName) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Asynchronous version of method addParameter.
   * @param parName parName Argument number 0 as defined by the service operation.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  org.ccsds.moims.mo.mal.transport.MALMessage asyncAddParameter(String parName, org.ccsds.moims.mo.mcprototype.statistictest.consumer.StatisticTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Continues a previously started interaction.
   * @param lastInteractionStage lastInteractionStage The last stage of the interaction to continue.
   * @param initiationTimestamp initiationTimestamp Timestamp of the interaction initiation message.
   * @param transactionId transactionId Transaction identifier of the interaction to continue.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void continueAddParameter(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.statistictest.consumer.StatisticTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
}
