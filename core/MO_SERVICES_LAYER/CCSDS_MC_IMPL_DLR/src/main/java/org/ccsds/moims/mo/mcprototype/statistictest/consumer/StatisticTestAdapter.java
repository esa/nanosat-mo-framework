package org.ccsds.moims.mo.mcprototype.statistictest.consumer;

/**
 * Consumer adapter for StatisticTest service.
 */
public abstract class StatisticTestAdapter extends org.ccsds.moims.mo.mal.consumer.MALInteractionAdapter
{
  /**
   * Called by the MAL when a SUBMIT acknowledgement is received from a provider for the operation resetTest.
   * @param msgHeader msgHeader The header of the received message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void resetTestAckReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a SUBMIT acknowledgement error is received from a provider for the operation resetTest.
   * @param msgHeader msgHeader The header of the received message.
   * @param error error The received error message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void resetTestErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a SUBMIT acknowledgement is received from a provider for the operation setParameterValue.
   * @param msgHeader msgHeader The header of the received message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void setParameterValueAckReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a SUBMIT acknowledgement error is received from a provider for the operation setParameterValue.
   * @param msgHeader msgHeader The header of the received message.
   * @param error error The received error message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void setParameterValueErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a REQUEST response is received from a provider for the operation addParameter.
   * @param msgHeader msgHeader The header of the received message.
   * @param _ObjectInstancePair0 _ObjectInstancePair0 Argument number 0 as defined by the service operation.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void addParameterResponseReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mc.structures.ObjectInstancePair _ObjectInstancePair0, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a REQUEST response error is received from a provider for the operation addParameter.
   * @param msgHeader msgHeader The header of the received message.
   * @param error error The received error message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void addParameterErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties)
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
      case org.ccsds.moims.mo.mcprototype.statistictest.StatisticTestHelper._RESETTEST_OP_NUMBER:
        resetTestAckReceived(msgHeader, qosProperties);
        break;
      case org.ccsds.moims.mo.mcprototype.statistictest.StatisticTestHelper._SETPARAMETERVALUE_OP_NUMBER:
        setParameterValueAckReceived(msgHeader, qosProperties);
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
      case org.ccsds.moims.mo.mcprototype.statistictest.StatisticTestHelper._RESETTEST_OP_NUMBER:
        resetTestErrorReceived(msgHeader, body.getError(), qosProperties);
        break;
      case org.ccsds.moims.mo.mcprototype.statistictest.StatisticTestHelper._SETPARAMETERVALUE_OP_NUMBER:
        setParameterValueErrorReceived(msgHeader, body.getError(), qosProperties);
        break;
      default:
        throw new org.ccsds.moims.mo.mal.MALException("Consumer adapter was not expecting operation number " + msgHeader.getOperation().getValue());
    }
  }

  /**
   * Called by the MAL when a REQUEST response is received from a provider.
   * @param msgHeader msgHeader The header of the received message.
   * @param body body The body of the received message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   * @throws org.ccsds.moims.mo.mal.MALException if an error is detected processing the message.
   */
  public final void requestResponseReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.transport.MALMessageBody body, java.util.Map qosProperties) throws org.ccsds.moims.mo.mal.MALException
  {
    switch (msgHeader.getOperation().getValue())
    {
      case org.ccsds.moims.mo.mcprototype.statistictest.StatisticTestHelper._ADDPARAMETER_OP_NUMBER:
        addParameterResponseReceived(msgHeader, (org.ccsds.moims.mo.mc.structures.ObjectInstancePair) body.getBodyElement(0, new org.ccsds.moims.mo.mc.structures.ObjectInstancePair()), qosProperties);
        break;
      default:
        throw new org.ccsds.moims.mo.mal.MALException("Consumer adapter was not expecting operation number " + msgHeader.getOperation().getValue());
    }
  }

  /**
   * Called by the MAL when a REQUEST response error is received from a provider.
   * @param msgHeader msgHeader The header of the received message.
   * @param body body The body of the received message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   * @throws org.ccsds.moims.mo.mal.MALException if an error is detected processing the message.
   */
  public final void requestErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.transport.MALErrorBody body, java.util.Map qosProperties) throws org.ccsds.moims.mo.mal.MALException
  {
    switch (msgHeader.getOperation().getValue())
    {
      case org.ccsds.moims.mo.mcprototype.statistictest.StatisticTestHelper._ADDPARAMETER_OP_NUMBER:
        addParameterErrorReceived(msgHeader, body.getError(), qosProperties);
        break;
      default:
        throw new org.ccsds.moims.mo.mal.MALException("Consumer adapter was not expecting operation number " + msgHeader.getOperation().getValue());
    }
  }

}
