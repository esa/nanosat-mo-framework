package org.ccsds.moims.mo.mcprototype.parametertest.consumer;

/**
 * Consumer adapter for ParameterTest service.
 */
public abstract class ParameterTestAdapter extends org.ccsds.moims.mo.mal.consumer.MALInteractionAdapter
{
  /**
   * Called by the MAL when a SUBMIT acknowledgement is received from a provider for the operation pushParameterValue.
   * @param msgHeader msgHeader The header of the received message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void pushParameterValueAckReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a SUBMIT acknowledgement error is received from a provider for the operation pushParameterValue.
   * @param msgHeader msgHeader The header of the received message.
   * @param error error The received error message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void pushParameterValueErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a SUBMIT acknowledgement is received from a provider for the operation setValidityStateOptions.
   * @param msgHeader msgHeader The header of the received message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void setValidityStateOptionsAckReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a SUBMIT acknowledgement error is received from a provider for the operation setValidityStateOptions.
   * @param msgHeader msgHeader The header of the received message.
   * @param error error The received error message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void setValidityStateOptionsErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a SUBMIT acknowledgement is received from a provider for the operation setReadOnlyParameter.
   * @param msgHeader msgHeader The header of the received message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void setReadOnlyParameterAckReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a SUBMIT acknowledgement error is received from a provider for the operation setReadOnlyParameter.
   * @param msgHeader msgHeader The header of the received message.
   * @param error error The received error message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void setReadOnlyParameterErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a SUBMIT acknowledgement is received from a provider for the operation setProvidedIntervals.
   * @param msgHeader msgHeader The header of the received message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void setProvidedIntervalsAckReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a SUBMIT acknowledgement error is received from a provider for the operation setProvidedIntervals.
   * @param msgHeader msgHeader The header of the received message.
   * @param error error The received error message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void setProvidedIntervalsErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a SUBMIT acknowledgement is received from a provider for the operation deleteParameterValues.
   * @param msgHeader msgHeader The header of the received message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void deleteParameterValuesAckReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a SUBMIT acknowledgement error is received from a provider for the operation deleteParameterValues.
   * @param msgHeader msgHeader The header of the received message.
   * @param error error The received error message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void deleteParameterValuesErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties)
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
      case org.ccsds.moims.mo.mcprototype.parametertest.ParameterTestHelper._PUSHPARAMETERVALUE_OP_NUMBER:
        pushParameterValueAckReceived(msgHeader, qosProperties);
        break;
      case org.ccsds.moims.mo.mcprototype.parametertest.ParameterTestHelper._SETVALIDITYSTATEOPTIONS_OP_NUMBER:
        setValidityStateOptionsAckReceived(msgHeader, qosProperties);
        break;
      case org.ccsds.moims.mo.mcprototype.parametertest.ParameterTestHelper._SETREADONLYPARAMETER_OP_NUMBER:
        setReadOnlyParameterAckReceived(msgHeader, qosProperties);
        break;
      case org.ccsds.moims.mo.mcprototype.parametertest.ParameterTestHelper._SETPROVIDEDINTERVALS_OP_NUMBER:
        setProvidedIntervalsAckReceived(msgHeader, qosProperties);
        break;
      case org.ccsds.moims.mo.mcprototype.parametertest.ParameterTestHelper._DELETEPARAMETERVALUES_OP_NUMBER:
        deleteParameterValuesAckReceived(msgHeader, qosProperties);
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
      case org.ccsds.moims.mo.mcprototype.parametertest.ParameterTestHelper._PUSHPARAMETERVALUE_OP_NUMBER:
        pushParameterValueErrorReceived(msgHeader, body.getError(), qosProperties);
        break;
      case org.ccsds.moims.mo.mcprototype.parametertest.ParameterTestHelper._SETVALIDITYSTATEOPTIONS_OP_NUMBER:
        setValidityStateOptionsErrorReceived(msgHeader, body.getError(), qosProperties);
        break;
      case org.ccsds.moims.mo.mcprototype.parametertest.ParameterTestHelper._SETREADONLYPARAMETER_OP_NUMBER:
        setReadOnlyParameterErrorReceived(msgHeader, body.getError(), qosProperties);
        break;
      case org.ccsds.moims.mo.mcprototype.parametertest.ParameterTestHelper._SETPROVIDEDINTERVALS_OP_NUMBER:
        setProvidedIntervalsErrorReceived(msgHeader, body.getError(), qosProperties);
        break;
      case org.ccsds.moims.mo.mcprototype.parametertest.ParameterTestHelper._DELETEPARAMETERVALUES_OP_NUMBER:
        deleteParameterValuesErrorReceived(msgHeader, body.getError(), qosProperties);
        break;
      default:
        throw new org.ccsds.moims.mo.mal.MALException("Consumer adapter was not expecting operation number " + msgHeader.getOperation().getValue());
    }
  }

}
