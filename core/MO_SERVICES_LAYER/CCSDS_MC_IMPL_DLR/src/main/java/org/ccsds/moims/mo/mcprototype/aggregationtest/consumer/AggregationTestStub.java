package org.ccsds.moims.mo.mcprototype.aggregationtest.consumer;

/**
 * Consumer stub for AggregationTest service.
 */
public class AggregationTestStub implements org.ccsds.moims.mo.mcprototype.aggregationtest.consumer.AggregationTest
{
  private final org.ccsds.moims.mo.mal.consumer.MALConsumer consumer;
  /**
   * Wraps a MALconsumer connection with service specific methods that map from the high level service API to the generic MAL API.
   * @param consumer consumer The MALConsumer to use in this stub.
   */
  public AggregationTestStub(org.ccsds.moims.mo.mal.consumer.MALConsumer consumer)
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
   * @param name .
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public void triggerAggregationUpdate(org.ccsds.moims.mo.mal.structures.Identifier name) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    consumer.submit(org.ccsds.moims.mo.mcprototype.aggregationtest.AggregationTestHelper.TRIGGERAGGREGATIONUPDATE_OP, name);
  }

  /**
   * Asynchronous version of method triggerAggregationUpdate.
   * @param name .
   * @param adapter adapter Listener in charge of receiving the messages from the service provider.
   * @return the MAL message sent to initiate the interaction.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  public org.ccsds.moims.mo.mal.transport.MALMessage asyncTriggerAggregationUpdate(org.ccsds.moims.mo.mal.structures.Identifier name, org.ccsds.moims.mo.mcprototype.aggregationtest.consumer.AggregationTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    return consumer.asyncSubmit(org.ccsds.moims.mo.mcprototype.aggregationtest.AggregationTestHelper.TRIGGERAGGREGATIONUPDATE_OP, adapter, name);
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
  public void continueTriggerAggregationUpdate(org.ccsds.moims.mo.mal.structures.UOctet lastInteractionStage, org.ccsds.moims.mo.mal.structures.Time initiationTimestamp, Long transactionId, org.ccsds.moims.mo.mcprototype.aggregationtest.consumer.AggregationTestAdapter adapter) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    consumer.continueInteraction(org.ccsds.moims.mo.mcprototype.aggregationtest.AggregationTestHelper.TRIGGERAGGREGATIONUPDATE_OP, lastInteractionStage, initiationTimestamp, transactionId, adapter);
  }

}
