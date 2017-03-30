package org.ccsds.moims.mo.mcprototype.actiontest.provider;

/**
 * Provider Delegation skeleton for ActionTestDelegationSkeleton service.
 */
public class ActionTestDelegationSkeleton implements org.ccsds.moims.mo.mal.provider.MALInteractionHandler, org.ccsds.moims.mo.mcprototype.actiontest.provider.ActionTestSkeleton
{
  private org.ccsds.moims.mo.mal.provider.MALProviderSet providerSet = new org.ccsds.moims.mo.mal.provider.MALProviderSet(org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper.ACTIONTEST_SERVICE);
  private org.ccsds.moims.mo.mcprototype.actiontest.provider.ActionTestHandler delegate;
  /**
   * Creates a delegation skeleton using the supplied delegate.
   * @param delegate delegate The interaction handler used for delegation.
   */
  public ActionTestDelegationSkeleton(org.ccsds.moims.mo.mcprototype.actiontest.provider.ActionTestHandler delegate)
  {
    this.delegate = delegate;
    delegate.setSkeleton(this);
  }

  /**
   * Adds the supplied MAL provider to the internal list of providers used for PubSub.
   * @param provider provider The provider to add.
   * @throws org.ccsds.moims.mo.mal.MALException If an error is detected.
   */
  public void malInitialize(org.ccsds.moims.mo.mal.provider.MALProvider provider) throws org.ccsds.moims.mo.mal.MALException
  {
    providerSet.addProvider(provider);
  }

  /**
   * Removes the supplied MAL provider from the internal list of providers used for PubSub.
   * @param provider provider The provider to add.
   * @throws org.ccsds.moims.mo.mal.MALException If an error is detected.
   */
  public void malFinalize(org.ccsds.moims.mo.mal.provider.MALProvider provider) throws org.ccsds.moims.mo.mal.MALException
  {
    providerSet.removeProvider(provider);
  }

  /**
   * Called by the provider MAL layer on reception of a message to handle the interaction.
   * @param interaction interaction the interaction object.
   * @param body body the message body.
   * @throws org.ccsds.moims.mo.mal.MALException if there is a internal error.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a operation interaction error.
   */
  public void handleSend(org.ccsds.moims.mo.mal.provider.MALInteraction interaction, org.ccsds.moims.mo.mal.transport.MALMessageBody body) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    switch (interaction.getOperation().getNumber().getValue())
    {
      default:
        throw new org.ccsds.moims.mo.mal.MALInteractionException(new org.ccsds.moims.mo.mal.MALStandardError(org.ccsds.moims.mo.mal.MALHelper.UNSUPPORTED_OPERATION_ERROR_NUMBER, new org.ccsds.moims.mo.mal.structures.Union("Unknown operation")));
    }
  }

  /**
   * Called by the provider MAL layer on reception of a message to handle the interaction.
   * @param interaction interaction the interaction object.
   * @param body body the message body.
   * @throws org.ccsds.moims.mo.mal.MALException if there is a internal error.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a operation interaction error.
   */
  public void handleSubmit(org.ccsds.moims.mo.mal.provider.MALSubmit interaction, org.ccsds.moims.mo.mal.transport.MALMessageBody body) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    switch (interaction.getOperation().getNumber().getValue())
    {
      case org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper._RESETTEST_OP_NUMBER:
        delegate.resetTest((body.getBodyElement(0, new org.ccsds.moims.mo.mal.structures.Union("")) == null) ? null : ((org.ccsds.moims.mo.mal.structures.Union) body.getBodyElement(0, new org.ccsds.moims.mo.mal.structures.Union(""))).getStringValue(), interaction);
        interaction.sendAcknowledgement();
        break;
      case org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper._FORCEPRECHECKINVALIDEXCEPTION_OP_NUMBER:
        delegate.forcePreCheckInvalidException((body.getBodyElement(0, new org.ccsds.moims.mo.mal.structures.Union(Boolean.FALSE)) == null) ? null : ((org.ccsds.moims.mo.mal.structures.Union) body.getBodyElement(0, new org.ccsds.moims.mo.mal.structures.Union(Boolean.FALSE))).getBooleanValue(), (org.ccsds.moims.mo.mal.structures.UIntegerList) body.getBodyElement(1, new org.ccsds.moims.mo.mal.structures.UIntegerList()), interaction);
        interaction.sendAcknowledgement();
        break;
      case org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper._FORCEPRECHECKFAILURE_OP_NUMBER:
        delegate.forcePreCheckFailure((body.getBodyElement(0, new org.ccsds.moims.mo.mal.structures.Union(Boolean.FALSE)) == null) ? null : ((org.ccsds.moims.mo.mal.structures.Union) body.getBodyElement(0, new org.ccsds.moims.mo.mal.structures.Union(Boolean.FALSE))).getBooleanValue(), interaction);
        interaction.sendAcknowledgement();
        break;
      case org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper._SETFAILURESTAGE_OP_NUMBER:
        delegate.setFailureStage((org.ccsds.moims.mo.mal.structures.UInteger) body.getBodyElement(0, new org.ccsds.moims.mo.mal.structures.UInteger()), (org.ccsds.moims.mo.mal.structures.UInteger) body.getBodyElement(1, new org.ccsds.moims.mo.mal.structures.UInteger()), interaction);
        interaction.sendAcknowledgement();
        break;
      case org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper._SETMINIMUMACTIONEXECUTIONTIME_OP_NUMBER:
        delegate.setMinimumActionExecutionTime((org.ccsds.moims.mo.mal.structures.UInteger) body.getBodyElement(0, new org.ccsds.moims.mo.mal.structures.UInteger()), interaction);
        interaction.sendAcknowledgement();
        break;
      default:
        interaction.sendError(new org.ccsds.moims.mo.mal.MALStandardError(org.ccsds.moims.mo.mal.MALHelper.UNSUPPORTED_OPERATION_ERROR_NUMBER, new org.ccsds.moims.mo.mal.structures.Union("Unknown operation")));
    }
  }

  /**
   * Called by the provider MAL layer on reception of a message to handle the interaction.
   * @param interaction interaction the interaction object.
   * @param body body the message body.
   * @throws org.ccsds.moims.mo.mal.MALException if there is a internal error.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a operation interaction error.
   */
  public void handleRequest(org.ccsds.moims.mo.mal.provider.MALRequest interaction, org.ccsds.moims.mo.mal.transport.MALMessageBody body) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    switch (interaction.getOperation().getNumber().getValue())
    {
      case org.ccsds.moims.mo.mcprototype.actiontest.ActionTestHelper._ADDACTIONDEFINITION_OP_NUMBER:
        interaction.sendResponse(delegate.addActionDefinition((org.ccsds.moims.mo.mc.action.structures.ActionCreationRequest) body.getBodyElement(0, new org.ccsds.moims.mo.mc.action.structures.ActionCreationRequest()), interaction));
        break;
      default:
        interaction.sendError(new org.ccsds.moims.mo.mal.MALStandardError(org.ccsds.moims.mo.mal.MALHelper.UNSUPPORTED_OPERATION_ERROR_NUMBER, new org.ccsds.moims.mo.mal.structures.Union("Unknown operation")));
    }
  }

  /**
   * Called by the provider MAL layer on reception of a message to handle the interaction.
   * @param interaction interaction the interaction object.
   * @param body body the message body.
   * @throws org.ccsds.moims.mo.mal.MALException if there is a internal error.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a operation interaction error.
   */
  public void handleInvoke(org.ccsds.moims.mo.mal.provider.MALInvoke interaction, org.ccsds.moims.mo.mal.transport.MALMessageBody body) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    switch (interaction.getOperation().getNumber().getValue())
    {
      default:
        interaction.sendError(new org.ccsds.moims.mo.mal.MALStandardError(org.ccsds.moims.mo.mal.MALHelper.UNSUPPORTED_OPERATION_ERROR_NUMBER, new org.ccsds.moims.mo.mal.structures.Union("Unknown operation")));
    }
  }

  /**
   * Called by the provider MAL layer on reception of a message to handle the interaction.
   * @param interaction interaction the interaction object.
   * @param body body the message body.
   * @throws org.ccsds.moims.mo.mal.MALException if there is a internal error.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a operation interaction error.
   */
  public void handleProgress(org.ccsds.moims.mo.mal.provider.MALProgress interaction, org.ccsds.moims.mo.mal.transport.MALMessageBody body) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException
  {
    switch (interaction.getOperation().getNumber().getValue())
    {
      default:
        interaction.sendError(new org.ccsds.moims.mo.mal.MALStandardError(org.ccsds.moims.mo.mal.MALHelper.UNSUPPORTED_OPERATION_ERROR_NUMBER, new org.ccsds.moims.mo.mal.structures.Union("Unknown operation")));
    }
  }

}
