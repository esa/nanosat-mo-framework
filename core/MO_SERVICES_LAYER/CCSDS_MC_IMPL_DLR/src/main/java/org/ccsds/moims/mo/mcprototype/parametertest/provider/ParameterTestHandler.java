package org.ccsds.moims.mo.mcprototype.parametertest.provider;

/**
 * Interface that providers of the ParameterTest service must implement to handle the operations of that service.
 */
public interface ParameterTestHandler
{
  /**
   * Implements the operation pushParameterValue.
   * @param name the name of the parameter the value should be updated from.
   * @param value the new parameter-value.
   * @param interaction interaction The MAL object representing the interaction in the provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void pushParameterValue(org.ccsds.moims.mo.mal.structures.Identifier name, org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValue value, org.ccsds.moims.mo.mal.provider.MALInteraction interaction) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Implements the operation setValidityStateOptions.
   * @param customMechanisms True, if implementation specific mechanisms should be used for evaluating the validity-state. Meaning the standard-ones defined in the functional requirements will not be used instead you own implemented ones.
   * @param customValue True, if deployment specific validity state values should be used. Meaning the standard-ones defined in the functional requirements will not be used instead you own specific ones. These values are greater than 127.
   * @param interaction interaction The MAL object representing the interaction in the provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void setValidityStateOptions(Boolean customMechanisms, Boolean customValue, org.ccsds.moims.mo.mal.provider.MALInteraction interaction) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Implements the operation setReadOnlyParameter.
   * @param name the name of the parameter.
   * @param value true if it should be set as a read-only parameter, false if it should be deleted from the read-only-list.
   * @param interaction interaction The MAL object representing the interaction in the provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void setReadOnlyParameter(org.ccsds.moims.mo.mal.structures.Identifier name, Boolean value, org.ccsds.moims.mo.mal.provider.MALInteraction interaction) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Implements the operation setProvidedIntervals.
   * @param providedIntervals list of update-intervals, that will be provided by the provider.
   * @param interaction interaction The MAL object representing the interaction in the provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void setProvidedIntervals(org.ccsds.moims.mo.mal.structures.DurationList providedIntervals, org.ccsds.moims.mo.mal.provider.MALInteraction interaction) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Implements the operation deleteParameterValues.
   * @param interaction interaction The MAL object representing the interaction in the provider.
   * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a problem during the interaction as defined by the MAL specification.
   * @throws org.ccsds.moims.mo.mal.MALException if there is an implementation exception.
   */
  void deleteParameterValues(org.ccsds.moims.mo.mal.provider.MALInteraction interaction) throws org.ccsds.moims.mo.mal.MALInteractionException, org.ccsds.moims.mo.mal.MALException;
  /**
   * Sets the skeleton to be used for creation of publishers.
   * @param skeleton skeleton The skeleton to be used.
   */
  void setSkeleton(org.ccsds.moims.mo.mcprototype.parametertest.provider.ParameterTestSkeleton skeleton);
}
