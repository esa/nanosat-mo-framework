package org.ccsds.moims.mo.mcprototype.parametertest.consumer;

/**
 * Consumer interface for ParameterTest service.
 */
public interface ParameterTest
{
  /**
   * Returns the internal MAL consumer object used for sending of messages from this interface.
   * @return The MAL consumer object.
   */
  org.ccsds.moims.mo.mal.consumer.MALConsumer getConsumer();
  /**
   * Pushes a new value of a parameter. This creates a parameterValue-Update that will be published and can be monitored via the monitorValue-Operation.
   * @param name the name of the parameter the value should be updated from.
   * @param value the new parameter-value.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void pushParameterValue(org.ccsds.moims.mo.mal.structures.Identifier name, org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValue value) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Asynchronous version of method pushParameterValue.
   * @param name the name of the parameter the value should be updated from.
   * @param value the new parameter-value.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  org.ccsds.moims.mo.mal.transport.MALMessage asyncPushParameterValue(org.ccsds.moims.mo.mal.structures.Identifier name, org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValue value, org.ccsds.moims.mo.mcprototype.parametertest.consumer.ParameterTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Continues a previously started interaction.
   * @param lastInteractionStage lastInteractionStage The last stage of the interaction to continue.
   * @param initiationTimestamp initiationTimestamp Timestamp of the interaction initiation message.
   * @param transactionId transactionId Transaction identifier of the interaction to continue.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void continuePushParameterValue(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.parametertest.consumer.ParameterTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Sets the evaluation options for creating a custom validity state of a parameters-value. Custom means, on the one hand, using custom mechanisms for evaluating the validity state and on the other hand using custom validity-state values. Via the getCustomValidityState-Operation these options will be considered.
   * @param customMechanisms True, if implementation specific mechanisms should be used for evaluating the validity-state. Meaning the standard-ones defined in the functional requirements will not be used instead you own implemented ones.
   * @param customValue True, if deployment specific validity state values should be used. Meaning the standard-ones defined in the functional requirements will not be used instead you own specific ones. These values are greater than 127.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void setValidityStateOptions(Boolean customMechanisms, Boolean customValue) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Asynchronous version of method setValidityStateOptions.
   * @param customMechanisms True, if implementation specific mechanisms should be used for evaluating the validity-state. Meaning the standard-ones defined in the functional requirements will not be used instead you own implemented ones.
   * @param customValue True, if deployment specific validity state values should be used. Meaning the standard-ones defined in the functional requirements will not be used instead you own specific ones. These values are greater than 127.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  org.ccsds.moims.mo.mal.transport.MALMessage asyncSetValidityStateOptions(Boolean customMechanisms, Boolean customValue, org.ccsds.moims.mo.mcprototype.parametertest.consumer.ParameterTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Continues a previously started interaction.
   * @param lastInteractionStage lastInteractionStage The last stage of the interaction to continue.
   * @param initiationTimestamp initiationTimestamp Timestamp of the interaction initiation message.
   * @param transactionId transactionId Transaction identifier of the interaction to continue.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void continueSetValidityStateOptions(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.parametertest.consumer.ParameterTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * sets a parameter with the given name to a readonly-parameter.
   * @param name the name of the parameter.
   * @param value true if it should be set as a read-only parameter, false if it should be deleted from the read-only-list.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void setReadOnlyParameter(org.ccsds.moims.mo.mal.structures.Identifier name, Boolean value) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Asynchronous version of method setReadOnlyParameter.
   * @param name the name of the parameter.
   * @param value true if it should be set as a read-only parameter, false if it should be deleted from the read-only-list.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  org.ccsds.moims.mo.mal.transport.MALMessage asyncSetReadOnlyParameter(org.ccsds.moims.mo.mal.structures.Identifier name, Boolean value, org.ccsds.moims.mo.mcprototype.parametertest.consumer.ParameterTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Continues a previously started interaction.
   * @param lastInteractionStage lastInteractionStage The last stage of the interaction to continue.
   * @param initiationTimestamp initiationTimestamp Timestamp of the interaction initiation message.
   * @param transactionId transactionId Transaction identifier of the interaction to continue.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void continueSetReadOnlyParameter(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.parametertest.consumer.ParameterTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Sets a list of update-intervals, that will be provided by the provider. A parameters updateInterval, must be equal to one of these provided interval.
   * @param providedIntervals list of update-intervals, that will be provided by the provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void setProvidedIntervals(org.ccsds.moims.mo.mal.structures.DurationList providedIntervals) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Asynchronous version of method setProvidedIntervals.
   * @param providedIntervals list of update-intervals, that will be provided by the provider.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  org.ccsds.moims.mo.mal.transport.MALMessage asyncSetProvidedIntervals(org.ccsds.moims.mo.mal.structures.DurationList providedIntervals, org.ccsds.moims.mo.mcprototype.parametertest.consumer.ParameterTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Continues a previously started interaction.
   * @param lastInteractionStage lastInteractionStage The last stage of the interaction to continue.
   * @param initiationTimestamp initiationTimestamp Timestamp of the interaction initiation message.
   * @param transactionId transactionId Transaction identifier of the interaction to continue.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void continueSetProvidedIntervals(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.parametertest.consumer.ParameterTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Deletes all Parameters Raw Values set in the internal provider list.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void deleteParameterValues() throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Asynchronous version of method deleteParameterValues.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  org.ccsds.moims.mo.mal.transport.MALMessage asyncDeleteParameterValues(org.ccsds.moims.mo.mcprototype.parametertest.consumer.ParameterTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Continues a previously started interaction.
   * @param lastInteractionStage lastInteractionStage The last stage of the interaction to continue.
   * @param initiationTimestamp initiationTimestamp Timestamp of the interaction initiation message.
   * @param transactionId transactionId Transaction identifier of the interaction to continue.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void continueDeleteParameterValues(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.parametertest.consumer.ParameterTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
}
