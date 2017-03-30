package org.ccsds.moims.mo.mcprototype.alerttest.provider;

/**
 * Interface that providers of the AlertTest service must implement to handle the operations of that service.
 */
public interface AlertTestHandler
{
  /**
   * Implements the operation resetTest.
   * @param alertDomain The alertDomain field specifies the domain in which alerts are to be created.
   * @param interaction interaction The MAL object representing the interaction in the provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void resetTest(String alertDomain, org.ccsds.moims.mo.mal.provider.MALInteraction interaction) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Implements the operation generateAlert.
   * @param alertIdentifier The alertIdentifier is the instance id of the alert definition.
   * @param alertArgumentValues The alertArguments is the list of argument values to set in the alert event.
   * @param convertArguments If true, convert argument values if a conversion is available and is possible.                                                                         If a raw value for an argument is provided in alertArgumentValues and convertArguments is true, the generated alert shall contain a converted value for that argument where a conversion exists.                                                                         If a converted value for an argument is provided in alertArgumentValues and convertArguments is true, the generated alert shall contain a raw value for that argument where a conversion exists and is of a type that can convert to raw values.
   * @param interaction interaction The MAL object representing the interaction in the provider.
   * @return The return value of the operation.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  Long generateAlert(Long alertIdentifier, org.ccsds.moims.mo.mc.structures.AttributeValueList alertArgumentValues, Boolean convertArguments, org.ccsds.moims.mo.mal.provider.MALInteraction interaction) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Implements the operation addAlertDefinition.
   * @param alertCreation alertCreation Argument number 0 as defined by the service operation.
   * @param interaction interaction The MAL object representing the interaction in the provider.
   * @return The return value of the operation.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  org.ccsds.moims.mo.mc.structures.ObjectInstancePair addAlertDefinition(org.ccsds.moims.mo.mc.alert.structures.AlertCreationRequest alertCreation, org.ccsds.moims.mo.mal.provider.MALInteraction interaction) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Sets the skeleton to be used for creation of publishers.
   * @param skeleton skeleton The skeleton to be used.
   */
  void setSkeleton(org.ccsds.moims.mo.mcprototype.alerttest.provider.AlertTestSkeleton skeleton);
}
