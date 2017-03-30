package org.ccsds.moims.mo.mcprototype.alerttest.consumer;

/**
 * Consumer stub for AlertTest service.
 */
public class AlertTestStub implements org.ccsds.moims.mo.mcprototype.alerttest.consumer.AlertTest
{
  private final org.ccsds.moims.mo.mal.consumer.MALConsumer consumer;
  /**
   * Wraps a MALconsumer connection with service specific methods that map from the high level service API to the generic MAL API.
   * @param consumer consumer The MALConsumer to use in this stub.
   */
  public AlertTestStub(org.ccsds.moims.mo.mal.consumer.MALConsumer consumer)
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
   * Resets the AlertTest service provider and sets the source information that should be                                 used for AlertEvents.
   * @param alertDomain The alertDomain field specifies the domain in which alerts are to be created.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public void resetTest(String alertDomain) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    consumer.submit(org.ccsds.moims.mo.mcprototype.alerttest.AlertTestHelper.RESETTEST_OP, (alertDomain == null) ? null : new org.ccsds.moims.mo.mal.structures.Union(alertDomain));
  }

  /**
   * Asynchronous version of method resetTest.
   * @param alertDomain The alertDomain field specifies the domain in which alerts are to be created.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public org.ccsds.moims.mo.mal.transport.MALMessage asyncResetTest(String alertDomain, org.ccsds.moims.mo.mcprototype.alerttest.consumer.AlertTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    return consumer.asyncSubmit(org.ccsds.moims.mo.mcprototype.alerttest.AlertTestHelper.RESETTEST_OP, adapter, (alertDomain == null) ? null : new org.ccsds.moims.mo.mal.structures.Union(alertDomain));
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
  public void continueResetTest(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.alerttest.consumer.AlertTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    consumer.continueInteraction(org.ccsds.moims.mo.mcprototype.alerttest.AlertTestHelper.RESETTEST_OP, lastInteractionStage, initiationTimestamp, transactionId, adapter);
  }

  /**
   * Generates an alert.                                  The alert domain shall be the values set via resetTest.                                  The source object shall be an OperationActivity object with instance number                                   Return is the instance ID of the generated alert.
   * @param alertIdentifier The alertIdentifier is the instance id of the alert definition.
   * @param alertArgumentValues The alertArguments is the list of argument values to set in the alert event.
   * @param convertArguments If true, convert argument values if a conversion is available and is possible.                                                                         If a raw value for an argument is provided in alertArgumentValues and convertArguments is true, the generated alert shall contain a converted value for that argument where a conversion exists.                                                                         If a converted value for an argument is provided in alertArgumentValues and convertArguments is true, the generated alert shall contain a raw value for that argument where a conversion exists and is of a type that can convert to raw values.
   * @return The return value of the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public Long generateAlert(Long alertIdentifier, org.ccsds.moims.mo.mc.structures.AttributeValueList alertArgumentValues, Boolean convertArguments) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    org.ccsds.moims.mo.mal.transport.MALMessageBody body = consumer.request(org.ccsds.moims.mo.mcprototype.alerttest.AlertTestHelper.GENERATEALERT_OP, (alertIdentifier == null) ? null : new org.ccsds.moims.mo.mal.structures.Union(alertIdentifier), alertArgumentValues, (convertArguments == null) ? null : new org.ccsds.moims.mo.mal.structures.Union(convertArguments));
    Object body0 = (Object) body.getBodyElement(0, new org.ccsds.moims.mo.mal.structures.Union(Long.MAX_VALUE));
    return (body0 == null) ? null : ((org.ccsds.moims.mo.mal.structures.Union) body0).getLongValue();
  }

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
  public org.ccsds.moims.mo.mal.transport.MALMessage asyncGenerateAlert(Long alertIdentifier, org.ccsds.moims.mo.mc.structures.AttributeValueList alertArgumentValues, Boolean convertArguments, org.ccsds.moims.mo.mcprototype.alerttest.consumer.AlertTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    return consumer.asyncRequest(org.ccsds.moims.mo.mcprototype.alerttest.AlertTestHelper.GENERATEALERT_OP, adapter, (alertIdentifier == null) ? null : new org.ccsds.moims.mo.mal.structures.Union(alertIdentifier), alertArgumentValues, (convertArguments == null) ? null : new org.ccsds.moims.mo.mal.structures.Union(convertArguments));
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
  public void continueGenerateAlert(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.alerttest.consumer.AlertTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    consumer.continueInteraction(org.ccsds.moims.mo.mcprototype.alerttest.AlertTestHelper.GENERATEALERT_OP, lastInteractionStage, initiationTimestamp, transactionId, adapter);
  }

  /**
   * Add alert definitions to the test scenario.
   * @param alertCreation alertCreation Argument number 0 as defined by the service operation.
   * @return The return value of the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public org.ccsds.moims.mo.mc.structures.ObjectInstancePair addAlertDefinition(org.ccsds.moims.mo.mc.alert.structures.AlertCreationRequest alertCreation) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    org.ccsds.moims.mo.mal.transport.MALMessageBody body = consumer.request(org.ccsds.moims.mo.mcprototype.alerttest.AlertTestHelper.ADDALERTDEFINITION_OP, alertCreation);
    Object body0 = (Object) body.getBodyElement(0, new org.ccsds.moims.mo.mc.structures.ObjectInstancePair());
    return (org.ccsds.moims.mo.mc.structures.ObjectInstancePair) body0;
  }

  /**
   * Asynchronous version of method addAlertDefinition.
   * @param alertCreation alertCreation Argument number 0 as defined by the service operation.
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public org.ccsds.moims.mo.mal.transport.MALMessage asyncAddAlertDefinition(org.ccsds.moims.mo.mc.alert.structures.AlertCreationRequest alertCreation, org.ccsds.moims.mo.mcprototype.alerttest.consumer.AlertTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    return consumer.asyncRequest(org.ccsds.moims.mo.mcprototype.alerttest.AlertTestHelper.ADDALERTDEFINITION_OP, adapter, alertCreation);
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
  public void continueAddAlertDefinition(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.alerttest.consumer.AlertTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    consumer.continueInteraction(org.ccsds.moims.mo.mcprototype.alerttest.AlertTestHelper.ADDALERTDEFINITION_OP, lastInteractionStage, initiationTimestamp, transactionId, adapter);
  }

}
