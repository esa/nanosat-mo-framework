package org.ccsds.moims.mo.mcprototype.alerttest.consumer;

/**
 * Consumer adapter for AlertTest service.
 */
public abstract class AlertTestAdapter extends org.ccsds.moims.mo.mal.consumer.MALInteractionAdapter
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
   * Called by the MAL when a REQUEST response is received from a provider for the operation generateAlert.
   * @param msgHeader msgHeader The header of the received message.
   * @param _Long0 _Long0 Argument number 0 as defined by the service operation.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void generateAlertResponseReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, Long _Long0, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a REQUEST response error is received from a provider for the operation generateAlert.
   * @param msgHeader msgHeader The header of the received message.
   * @param error error The received error message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void generateAlertErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a REQUEST response is received from a provider for the operation addAlertDefinition.
   * @param msgHeader msgHeader The header of the received message.
   * @param _ObjectInstancePair0 _ObjectInstancePair0 Argument number 0 as defined by the service operation.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void addAlertDefinitionResponseReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mc.structures.ObjectInstancePair _ObjectInstancePair0, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a REQUEST response error is received from a provider for the operation addAlertDefinition.
   * @param msgHeader msgHeader The header of the received message.
   * @param error error The received error message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void addAlertDefinitionErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties)
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
      case org.ccsds.moims.mo.mcprototype.alerttest.AlertTestHelper._RESETTEST_OP_NUMBER:
        resetTestAckReceived(msgHeader, qosProperties);
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
      case org.ccsds.moims.mo.mcprototype.alerttest.AlertTestHelper._RESETTEST_OP_NUMBER:
        resetTestErrorReceived(msgHeader, body.getError(), qosProperties);
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
      case org.ccsds.moims.mo.mcprototype.alerttest.AlertTestHelper._GENERATEALERT_OP_NUMBER:
        generateAlertResponseReceived(msgHeader, (body.getBodyElement(0, new org.ccsds.moims.mo.mal.structures.Union(Long.MAX_VALUE)) == null) ? null : ((org.ccsds.moims.mo.mal.structures.Union) body.getBodyElement(0, new org.ccsds.moims.mo.mal.structures.Union(Long.MAX_VALUE))).getLongValue(), qosProperties);
        break;
      case org.ccsds.moims.mo.mcprototype.alerttest.AlertTestHelper._ADDALERTDEFINITION_OP_NUMBER:
        addAlertDefinitionResponseReceived(msgHeader, (org.ccsds.moims.mo.mc.structures.ObjectInstancePair) body.getBodyElement(0, new org.ccsds.moims.mo.mc.structures.ObjectInstancePair()), qosProperties);
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
      case org.ccsds.moims.mo.mcprototype.alerttest.AlertTestHelper._GENERATEALERT_OP_NUMBER:
        generateAlertErrorReceived(msgHeader, body.getError(), qosProperties);
        break;
      case org.ccsds.moims.mo.mcprototype.alerttest.AlertTestHelper._ADDALERTDEFINITION_OP_NUMBER:
        addAlertDefinitionErrorReceived(msgHeader, body.getError(), qosProperties);
        break;
      default:
        throw new org.ccsds.moims.mo.mal.MALException("Consumer adapter was not expecting operation number " + msgHeader.getOperation().getValue());
    }
  }

}
