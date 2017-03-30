package org.ccsds.moims.mo.mcprototype.aggregationtest.provider;

/**
 * Provider Inheritance skeleton for AggregationTestInheritanceSkeleton service.
 */
public abstract class AggregationTestInheritanceSkeleton implements org.ccsds.moims.mo.mal.provider.MALInteractionHandler, org.ccsds.moims.mo.mcprototype.aggregationtest.provider.AggregationTestSkeleton, org.ccsds.moims.mo.mcprototype.aggregationtest.provider.AggregationTestHandler
{
  private org.ccsds.moims.mo.mal.provider.MALProviderSet providerSet = new org.ccsds.moims.mo.mal.provider.MALProviderSet(org.ccsds.moims.mo.mcprototype.aggregationtest.AggregationTestHelper.AGGREGATIONTEST_SERVICE);
  /**
   * Implements the setSkeleton method of the handler interface but does nothing as this is the skeleton.
   * @param skeleton skeleton Not used in the inheritance pattern (the skeleton is 'this'.
   */
  public void setSkeleton(org.ccsds.moims.mo.mcprototype.aggregationtest.provider.AggregationTestSkeleton skeleton)
  {
    // Not used in the inheritance pattern (the skeleton is 'this');
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
      case org.ccsds.moims.mo.mcprototype.aggregationtest.AggregationTestHelper._TRIGGERAGGREGATIONUPDATE_OP_NUMBER:
        triggerAggregationUpdate((org.ccsds.moims.mo.mal.structures.Identifier) body.getBodyElement(0, new org.ccsds.moims.mo.mal.structures.Identifier()), interaction);
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
