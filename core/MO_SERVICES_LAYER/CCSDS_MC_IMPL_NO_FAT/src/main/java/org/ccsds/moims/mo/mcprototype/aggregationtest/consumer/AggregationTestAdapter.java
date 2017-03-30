package org.ccsds.moims.mo.mcprototype.aggregationtest.consumer;

/**
 * Consumer adapter for AggregationTest service.
 */
public abstract class AggregationTestAdapter extends org.ccsds.moims.mo.mal.consumer.MALInteractionAdapter
{
  /**
   * Called by the MAL when a SUBMIT acknowledgement is received from a provider for the operation triggerAggregationUpdate.
   * @param msgHeader msgHeader The header of the received message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void triggerAggregationUpdateAckReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a SUBMIT acknowledgement error is received from a provider for the operation triggerAggregationUpdate.
   * @param msgHeader msgHeader The header of the received message.
   * @param error error The received error message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void triggerAggregationUpdateErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a SUBMIT acknowledgement is received from a provider.
   * @param msgHeader msgHeader The header of the received message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   * @throws org.ccsds.moims.mo.mal.MALException if an error is detected processing the message.
   */
  public final void submitAckReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, java.util.Map qosProperties) throws org.ccsds.moims.mo.mal.MALException
  {
    switch (msgHeader.getOperation().getValue())
    {
      case org.ccsds.moims.mo.mcprototype.aggregationtest.AggregationTestHelper._TRIGGERAGGREGATIONUPDATE_OP_NUMBER:
        triggerAggregationUpdateAckReceived(msgHeader, qosProperties);
        break;
      default:
        throw new org.ccsds.moims.mo.mal.MALException("Consumer adapter was not expecting operation number " + msgHeader.getOperation().getValue());
    }
  }

  /**
   * Called by the MAL when a SUBMIT acknowledgement error is received from a provider.
   * @param msgHeader msgHeader The header of the received message.
   * @param body body The body of the received message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   * @throws org.ccsds.moims.mo.mal.MALException if an error is detected processing the message.
   */
  public final void submitErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.transport.MALErrorBody body, java.util.Map qosProperties) throws org.ccsds.moims.mo.mal.MALException
  {
    switch (msgHeader.getOperation().getValue())
    {
      case org.ccsds.moims.mo.mcprototype.aggregationtest.AggregationTestHelper._TRIGGERAGGREGATIONUPDATE_OP_NUMBER:
        triggerAggregationUpdateErrorReceived(msgHeader, body.getError(), qosProperties);
        break;
      default:
        throw new org.ccsds.moims.mo.mal.MALException("Consumer adapter was not expecting operation number " + msgHeader.getOperation().getValue());
    }
  }

}
