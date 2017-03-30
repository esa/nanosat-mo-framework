package org.ccsds.moims.mo.mcprototype.alerttest.consumer;

/**
 * Consumer interface for AlertTest service.
 */
public interface AlertTest
{
  /**
   * Returns the internal MAL consumer object used for sending of messages from this interface.
   * @return The MAL consumer object.
   */
  org.ccsds.moims.mo.mal.consumer.MALConsumer getConsumer();
  /**
   * Resets the AlertTest service provider and sets the source information that should be                                 used for AlertEvents.
   * @param alertDomain The alertDomain field specifies the domain in which alerts are to be created.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void resetTest(String alertDomain) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Asynchronous version of method resetTest.
   * @param alertDomain The alertDomain field specifies the domain in which alerts are to be created.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  org.ccsds.moims.mo.mal.transport.MALMessage asyncResetTest(String alertDomain, org.ccsds.moims.mo.mcprototype.alerttest.consumer.AlertTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Continues a previously started interaction.
   * @param lastInteractionStage lastInteractionStage The last stage of the interaction to continue.
   * @param initiationTimestamp initiationTimestamp Timestamp of the interaction initiation message.
   * @param transactionId transactionId Transaction identifier of the interaction to continue.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void continueResetTest(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.alerttest.consumer.AlertTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Generates an alert.                                  The alert domain shall be the values set via resetTest.                                  The source object shall be an OperationActivity object with instance number                                   Return is the instance ID of the generated alert.
   * @param alertIdentifier The alertIdentifier is the instance id of the alert definition.
   * @param alertArgumentValues The alertArguments is the list of argument values to set in the alert event.
   * @param convertArguments If true, convert argument values if a conversion is available and is possible.                                                                         If a raw value for an argument is provided in alertArgumentValues and convertArguments is true, the generated alert shall contain a converted value for that argument where a conversion exists.                                                                         If a converted value for an argument is provided in alertArgumentValues and convertArguments is true, the generated alert shall contain a raw value for that argument where a conversion exists and is of a type that can convert to raw values.
   * @return The return value of the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  Long generateAlert(Long alertIdentifier, org.ccsds.moims.mo.mc.structures.AttributeValueList alertArgumentValues, Boolean convertArguments) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Asynchronous version of method generateAlert.
   * @param alertIdentifier The alertIdentifier is the instance id of the alert definition.
   * @param alertArgumentValues The alertArguments is the list of argument values to set in the alert event.
   * @param convertArguments If true, convert argument values if a conversion is available and is possible.                                                                         If a raw value for an argument is provided in alertArgumentValues and convertArguments is true, the generated alert shall contain a converted value for that argument where a conversion exists.                                                                         If a converted value for an argument is provided in alertArgumentValues and convertArguments is true, the generated alert shall contain a raw value for that argument where a conversion exists and is of a type that can convert to raw values.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  org.ccsds.moims.mo.mal.transport.MALMessage asyncGenerateAlert(Long alertIdentifier, org.ccsds.moims.mo.mc.structures.AttributeValueList alertArgumentValues, Boolean convertArguments, org.ccsds.moims.mo.mcprototype.alerttest.consumer.AlertTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Continues a previously started interaction.
   * @param lastInteractionStage lastInteractionStage The last stage of the interaction to continue.
   * @param initiationTimestamp initiationTimestamp Timestamp of the interaction initiation message.
   * @param transactionId transactionId Transaction identifier of the interaction to continue.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void continueGenerateAlert(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.alerttest.consumer.AlertTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Add alert definitions to the test scenario.
   * @param alertCreation alertCreation Argument number 0 as defined by the service operation.
   * @return The return value of the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  org.ccsds.moims.mo.mc.structures.ObjectInstancePair addAlertDefinition(org.ccsds.moims.mo.mc.alert.structures.AlertCreationRequest alertCreation) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Asynchronous version of method addAlertDefinition.
   * @param alertCreation alertCreation Argument number 0 as defined by the service operation.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  org.ccsds.moims.mo.mal.transport.MALMessage asyncAddAlertDefinition(org.ccsds.moims.mo.mc.alert.structures.AlertCreationRequest alertCreation, org.ccsds.moims.mo.mcprototype.alerttest.consumer.AlertTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Continues a previously started interaction.
   * @param lastInteractionStage lastInteractionStage The last stage of the interaction to continue.
   * @param initiationTimestamp initiationTimestamp Timestamp of the interaction initiation message.
   * @param transactionId transactionId Transaction identifier of the interaction to continue.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void continueAddAlertDefinition(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.alerttest.consumer.AlertTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
}
