package org.ccsds.moims.mo.mcprototype.actiontest.consumer;

/**
 * Consumer adapter for ActionTest service.
 */
public abstract class ActionTestAdapter extends org.ccsds.moims.mo.mal.consumer.MALInteractionAdapter
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
   * Called by the MAL when a REQUEST response is received from a provider for the operation addActionDefinition.
   * @param msgHeader msgHeader The header of the received message.
   * @param _ObjectInstancePair0 _ObjectInstancePair0 Argument number 0 as defined by the service operation.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void addActionDefinitionResponseReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mc.structures.ObjectInstancePair _ObjectInstancePair0, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a REQUEST response error is received from a provider for the operation addActionDefinition.
   * @param msgHeader msgHeader The header of the received message.
   * @param error error The received error message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void addActionDefinitionErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a SUBMIT acknowledgement is received from a provider for the operation forcePreCheckInvalidException.
   * @param msgHeader msgHeader The header of the received message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void forcePreCheckInvalidExceptionAckReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a SUBMIT acknowledgement error is received from a provider for the operation forcePreCheckInvalidException.
   * @param msgHeader msgHeader The header of the received message.
   * @param error error The received error message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void forcePreCheckInvalidExceptionErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a SUBMIT acknowledgement is received from a provider for the operation forcePreCheckFailure.
   * @param msgHeader msgHeader The header of the received message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void forcePreCheckFailureAckReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a SUBMIT acknowledgement error is received from a provider for the operation forcePreCheckFailure.
   * @param msgHeader msgHeader The header of the received message.
   * @param error error The received error message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void forcePreCheckFailureErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a SUBMIT acknowledgement is received from a provider for the operation setFailureStage.
   * @param msgHeader msgHeader The header of the received message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void setFailureStageAckReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a SUBMIT acknowledgement error is received from a provider for the operation setFailureStage.
   * @param msgHeader msgHeader The header of the received message.
   * @param error error The received error message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void setFailureStageErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a SUBMIT acknowledgement is received from a provider for the operation setMinimumActionExecutionTime.
   * @param msgHeader msgHeader The header of the received message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void setMinimumActionExecutionTimeAckReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, java.util.Map qosProperties)
  {
  }

  /**
   * Called by the MAL when a SUBMIT acknowledgement error is received from a provider for the operation setMinimumActionExecutionTime.
   * @param msgHeader msgHeader The header of the received message.
   * @param error error The received error message.
   * @param qosProperties qosProperties The QoS properties associated with the message.
   */
  public void setMinimumActionExecutionTimeErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties)
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
      case org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper._RESETTEST_OP_NUMBER:
        resetTestAckReceived(msgHeader, qosProperties);
        break;
      case org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper._FORCEPRECHECKINVALIDEXCEPTION_OP_NUMBER:
        forcePreCheckInvalidExceptionAckReceived(msgHeader, qosProperties);
        break;
      case org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper._FORCEPRECHECKFAILURE_OP_NUMBER:
        forcePreCheckFailureAckReceived(msgHeader, qosProperties);
        break;
      case org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper._SETFAILURESTAGE_OP_NUMBER:
        setFailureStageAckReceived(msgHeader, qosProperties);
        break;
      case org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper._SETMINIMUMACTIONEXECUTIONTIME_OP_NUMBER:
        setMinimumActionExecutionTimeAckReceived(msgHeader, qosProperties);
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
      case org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper._RESETTEST_OP_NUMBER:
        resetTestErrorReceived(msgHeader, body.getError(), qosProperties);
        break;
      case org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper._FORCEPRECHECKINVALIDEXCEPTION_OP_NUMBER:
        forcePreCheckInvalidExceptionErrorReceived(msgHeader, body.getError(), qosProperties);
        break;
      case org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper._FORCEPRECHECKFAILURE_OP_NUMBER:
        forcePreCheckFailureErrorReceived(msgHeader, body.getError(), qosProperties);
        break;
      case org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper._SETFAILURESTAGE_OP_NUMBER:
        setFailureStageErrorReceived(msgHeader, body.getError(), qosProperties);
        break;
      case org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper._SETMINIMUMACTIONEXECUTIONTIME_OP_NUMBER:
        setMinimumActionExecutionTimeErrorReceived(msgHeader, body.getError(), qosProperties);
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
      case org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper._ADDACTIONDEFINITION_OP_NUMBER:
        addActionDefinitionResponseReceived(msgHeader, (org.ccsds.moims.mo.mc.structures.ObjectInstancePair) body.getBodyElement(0, new org.ccsds.moims.mo.mc.structures.ObjectInstancePair()), qosProperties);
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
      case org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper._ADDACTIONDEFINITION_OP_NUMBER:
        addActionDefinitionErrorReceived(msgHeader, body.getError(), qosProperties);
        break;
      default:
        throw new org.ccsds.moims.mo.mal.MALException("Consumer adapter was not expecting operation number " + msgHeader.getOperation().getValue());
    }
  }

}
