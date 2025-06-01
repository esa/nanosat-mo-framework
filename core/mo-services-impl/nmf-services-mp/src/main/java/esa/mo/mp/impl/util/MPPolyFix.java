/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 */
package esa.mo.mp.impl.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mp.structures.ActivityDetails;
import org.ccsds.moims.mo.mp.structures.ActivityNode;
import org.ccsds.moims.mo.mp.structures.Trigger;
import org.ccsds.moims.mo.mp.structures.VelocityPointingWithOrbitalPoleYawSteeringConstraint;
import org.ccsds.moims.mo.mp.structures.AngleConstraint;
import org.ccsds.moims.mo.mp.structures.ArgDef;
import org.ccsds.moims.mo.mp.structures.ArgumentConstraint;
import org.ccsds.moims.mo.mp.structures.b_ArgDef;
import org.ccsds.moims.mo.mp.structures.b_Expression;
import org.ccsds.moims.mo.mp.structures.b_PointingConstraint;
import org.ccsds.moims.mo.mp.structures.b_ResourceConstraint;
import org.ccsds.moims.mo.mp.structures.b_ResourceDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.b_ResourceUpdateDetails;
import org.ccsds.moims.mo.mp.structures.BooleanExpression;
import org.ccsds.moims.mo.mp.structures.ComplexConstraint;
import org.ccsds.moims.mo.mp.structures.ComplexEffect;
import org.ccsds.moims.mo.mp.structures.ComplexResourceConstraint;
import org.ccsds.moims.mo.mp.structures.ConditionalConstraint;
import org.ccsds.moims.mo.mp.structures.Constraint;
import org.ccsds.moims.mo.mp.structures.ConstraintNode;
import org.ccsds.moims.mo.mp.structures.DirectionExpression;
import org.ccsds.moims.mo.mp.structures.DistanceConstraint;
import org.ccsds.moims.mo.mp.structures.DurationConstraint;
import org.ccsds.moims.mo.mp.structures.DurationExpression;
import org.ccsds.moims.mo.mp.structures.Effect;
import org.ccsds.moims.mo.mp.structures.EventRepetition;
import org.ccsds.moims.mo.mp.structures.EventTrigger;
import org.ccsds.moims.mo.mp.structures.ExclusionConstraint;
import org.ccsds.moims.mo.mp.structures.Expression;
import org.ccsds.moims.mo.mp.structures.FunctionConstraint;
import org.ccsds.moims.mo.mp.structures.GeometricConstraint;
import org.ccsds.moims.mo.mp.structures.GeometricRepetition;
import org.ccsds.moims.mo.mp.structures.InertialPointingConstraint;
import org.ccsds.moims.mo.mp.structures.IntegerExpression;
import org.ccsds.moims.mo.mp.structures.LimbPointingWithInertialDirectionYawSteeringConstraint;
import org.ccsds.moims.mo.mp.structures.LimbPointingWithPowerOptimizedYawSteeringConstraint;
import org.ccsds.moims.mo.mp.structures.NadirWithGroundTrackAlignedYawSteeringConstraint;
import org.ccsds.moims.mo.mp.structures.NadirWithOrbitalPoleAlignedYawSteeringConstraint;
import org.ccsds.moims.mo.mp.structures.NadirWithPowerOptimizedYawSteeringConstraint;
import org.ccsds.moims.mo.mp.structures.NumericArgDef;
import org.ccsds.moims.mo.mp.structures.NumericResourceDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.NumericResourceUpdateDetails;
import org.ccsds.moims.mo.mp.structures.ObjectExpression;
import org.ccsds.moims.mo.mp.structures.OrbitTrigger;
import org.ccsds.moims.mo.mp.structures.OrbitalConstraint;
import org.ccsds.moims.mo.mp.structures.OrbitalRepetition;
import org.ccsds.moims.mo.mp.structures.PointingConstraint;
import org.ccsds.moims.mo.mp.structures.PositionExpression;
import org.ccsds.moims.mo.mp.structures.PositionTrigger;
import org.ccsds.moims.mo.mp.structures.RealExpression;
import org.ccsds.moims.mo.mp.structures.Repetition;
import org.ccsds.moims.mo.mp.structures.ResourceConstraint;
import org.ccsds.moims.mo.mp.structures.ResourceDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.ResourceUpdateDetails;
import org.ccsds.moims.mo.mp.structures.RevolutionConstraint;
import org.ccsds.moims.mo.mp.structures.RevolutionRepetition;
import org.ccsds.moims.mo.mp.structures.RevolutionTrigger;
import org.ccsds.moims.mo.mp.structures.SequentialConstraint;
import org.ccsds.moims.mo.mp.structures.SimpleActivityDetails;
import org.ccsds.moims.mo.mp.structures.SimpleEffect;
import org.ccsds.moims.mo.mp.structures.StatusArgDef;
import org.ccsds.moims.mo.mp.structures.StatusResourceDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.StatusResourceUpdateDetails;
import org.ccsds.moims.mo.mp.structures.StringArgDef;
import org.ccsds.moims.mo.mp.structures.StringResourceDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.StringResourceUpdateDetails;
import org.ccsds.moims.mo.mp.structures.SunPointingConstraint;
import org.ccsds.moims.mo.mp.structures.TemporalConstraint;
import org.ccsds.moims.mo.mp.structures.TemporalRepetition;
import org.ccsds.moims.mo.mp.structures.TimeConstraint;
import org.ccsds.moims.mo.mp.structures.TimeExpression;
import org.ccsds.moims.mo.mp.structures.TimeTrigger;
import org.ccsds.moims.mo.mp.structures.TimeWindowConstraint;
import org.ccsds.moims.mo.mp.structures.TrackWithInertialDirectionYawSteeringConstraint;
import org.ccsds.moims.mo.mp.structures.TrackWithPowerOptimizedYawSteeringConstraint;
import org.ccsds.moims.mo.mp.structures.c_ActivityDetails;
import org.ccsds.moims.mo.mp.structures.c_ActivityDetailsList;
import org.ccsds.moims.mo.mp.structures.c_ArgDef;
import org.ccsds.moims.mo.mp.structures.c_ArgDefList;
import org.ccsds.moims.mo.mp.structures.c_ConditionalConstraint;
import org.ccsds.moims.mo.mp.structures.c_Constraint;
import org.ccsds.moims.mo.mp.structures.c_ConstraintList;
import org.ccsds.moims.mo.mp.structures.c_Effect;
import org.ccsds.moims.mo.mp.structures.c_EffectList;
import org.ccsds.moims.mo.mp.structures.c_Expression;
import org.ccsds.moims.mo.mp.structures.c_ExpressionList;
import org.ccsds.moims.mo.mp.structures.c_GeometricConstraint;
import org.ccsds.moims.mo.mp.structures.c_PointingConstraint;
import org.ccsds.moims.mo.mp.structures.c_Repetition;
import org.ccsds.moims.mo.mp.structures.c_RepetitionList;
import org.ccsds.moims.mo.mp.structures.c_ResourceConstraint;
import org.ccsds.moims.mo.mp.structures.c_ResourceDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.c_ResourceDefinitionDetailsList;
import org.ccsds.moims.mo.mp.structures.c_ResourceUpdateDetails;
import org.ccsds.moims.mo.mp.structures.c_ResourceUpdateDetailsList;
import org.ccsds.moims.mo.mp.structures.c_TemporalConstraint;
import org.ccsds.moims.mo.mp.structures.c_Trigger;
import org.ccsds.moims.mo.mp.structures.c_TriggerList;

/**
 * MPPolyFix contains helper methods to aid dealing with polymorphism fix objects
 * <p>
 * Encode methods convert a data structure (e.g. SimpleActivityDetails) into polymorphism fix structure (e.g. c_ActivityDetails)
 * <p>
 * Decode methods convert from polymorphism fix structure (e.g. c_ActivityDetails) back to normal data structure (e.g. SimpleActivityDetails)
 * <p>
 * Contains generic encode and decode methods and also typed methods for MP
 */
public class MPPolyFix {

    private static final Logger LOGGER = Logger.getLogger(MPPolyFix.class.getName());

    /**
     * Activity
     */

    public static c_ActivityDetails encode(SimpleActivityDetails simpleActivity) {
        return (c_ActivityDetails) encode(simpleActivity, c_ActivityDetails.class);
    }

    public static c_ActivityDetails encode(ActivityNode activityNode) {
        return (c_ActivityDetails) encode(activityNode, c_ActivityDetails.class);
    }

    public static c_ActivityDetailsList encodeActivityDetails(List<ActivityDetails> activityDetails) {
        return (c_ActivityDetailsList) encode(activityDetails, c_ActivityDetailsList.class);
    }

    public static ActivityDetails decode(c_ActivityDetails c_activityDetails) {
        return (ActivityDetails) decode((Object) c_activityDetails);
    }

    public static List<ActivityDetails> decode(c_ActivityDetailsList c_activityDetailsList) {
        return decode((ElementList) c_activityDetailsList);
    }

    /**
     * ArgDef
     */

    public static c_ArgDef encode(b_ArgDef argDef) {
        return (c_ArgDef) encode(argDef, c_ArgDef.class);
    }

    public static c_ArgDef encode(NumericArgDef numericArgDef) {
        return (c_ArgDef) encode(numericArgDef, c_ArgDef.class);
    }

    public static c_ArgDef encode(StringArgDef stringArgDef) {
        return (c_ArgDef) encode(stringArgDef, c_ArgDef.class);
    }

    public static c_ArgDef encode(StatusArgDef statusArgDef) {
        return (c_ArgDef) encode(statusArgDef, c_ArgDef.class);
    }

    public static c_ArgDefList encodeArgDefs(List<ArgDef> argDefs) {
        return (c_ArgDefList) encode(argDefs, c_ArgDefList.class);
    }

    public static ArgDef decode(c_ArgDef c_argDef) {
        return (ArgDef) decode((Object) c_argDef);
    }

    public static List<ArgDef> decode(c_ArgDefList c_argDefList) {
        return decode((ElementList) c_argDefList);
    }

    /**
     * Constraint
     */

    public static c_Constraint encode(c_ConditionalConstraint c_ConditionalConstraint) {
        return (c_Constraint) encode(c_ConditionalConstraint, c_Constraint.class);
    }

    public static c_Constraint encode(ConstraintNode constraintNode) {
        return (c_Constraint) encode(constraintNode, c_Constraint.class);
    }

    public static c_Constraint encode(c_Effect c_Effect) {
        return (c_Constraint) encode(c_Effect, c_Constraint.class);
    }

    public static c_Constraint encodeConstraint(ComplexConstraint complexConstraint) {
        return (c_Constraint) encode(complexConstraint, c_ConditionalConstraint.class, c_Constraint.class);
    }

    public static c_Constraint encodeConstraint(TimeConstraint timeConstraint) {
        return (c_Constraint) encode(timeConstraint, c_TemporalConstraint.class, c_ConditionalConstraint.class,
            c_Constraint.class);
    }

    public static c_Constraint encodeConstraint(TimeWindowConstraint timeWindowConstraint) {
        return (c_Constraint) encode(timeWindowConstraint, c_TemporalConstraint.class, c_ConditionalConstraint.class,
            c_Constraint.class);
    }

    public static c_Constraint encodeConstraint(DurationConstraint durationConstraint) {
        return (c_Constraint) encode(durationConstraint, c_TemporalConstraint.class, c_ConditionalConstraint.class,
            c_Constraint.class);
    }

    public static c_Constraint encodeConstraint(SequentialConstraint sequentialConstraint) {
        return (c_Constraint) encode(sequentialConstraint, c_ConditionalConstraint.class, c_Constraint.class);
    }

    public static c_Constraint encodeConstraint(ExclusionConstraint exclusionConstraint) {
        return (c_Constraint) encode(exclusionConstraint, c_ConditionalConstraint.class, c_Constraint.class);
    }

    public static c_Constraint encodeConstraint(b_ResourceConstraint resourceConstraint) {
        return (c_Constraint) encode(resourceConstraint, c_ResourceConstraint.class, c_ConditionalConstraint.class,
            c_Constraint.class);
    }

    public static c_Constraint encodeConstraint(ComplexResourceConstraint complexResourceConstraint) {
        return (c_Constraint) encode(complexResourceConstraint, c_ResourceConstraint.class,
            c_ConditionalConstraint.class, c_Constraint.class);
    }

    public static c_Constraint encodeConstraint(ArgumentConstraint argumentConstraint) {
        return (c_Constraint) encode(argumentConstraint, c_ConditionalConstraint.class, c_Constraint.class);
    }

    public static c_Constraint encodeConstraint(FunctionConstraint functionConstraint) {
        return (c_Constraint) encode(functionConstraint, c_ConditionalConstraint.class, c_Constraint.class);
    }

    public static c_Constraint encodeConstraint(DistanceConstraint distanceConstraint) {
        return (c_Constraint) encode(distanceConstraint, c_GeometricConstraint.class, c_ConditionalConstraint.class,
            c_Constraint.class);
    }

    public static c_Constraint encodeConstraint(AngleConstraint angleConstraint) {
        return (c_Constraint) encode(angleConstraint, c_GeometricConstraint.class, c_ConditionalConstraint.class,
            c_Constraint.class);
    }

    public static c_Constraint encodeConstraint(OrbitalConstraint orbitalConstraint) {
        return (c_Constraint) encode(orbitalConstraint, c_GeometricConstraint.class, c_ConditionalConstraint.class,
            c_Constraint.class);
    }

    public static c_Constraint encodeConstraint(RevolutionConstraint revolutionConstraint) {
        return (c_Constraint) encode(revolutionConstraint, c_GeometricConstraint.class, c_ConditionalConstraint.class,
            c_Constraint.class);
    }

    public static c_Constraint encodeConstraint(b_PointingConstraint pointingConstraint) {
        return (c_Constraint) encode(pointingConstraint, c_PointingConstraint.class, c_GeometricConstraint.class,
            c_ConditionalConstraint.class, c_Constraint.class);
    }

    public static c_Constraint encodeConstraint(InertialPointingConstraint pointingConstraint) {
        return (c_Constraint) encode(pointingConstraint, c_PointingConstraint.class, c_GeometricConstraint.class,
            c_ConditionalConstraint.class, c_Constraint.class);
    }

    public static c_Constraint encodeConstraint(SunPointingConstraint pointingConstraint) {
        return (c_Constraint) encode(pointingConstraint, c_PointingConstraint.class, c_GeometricConstraint.class,
            c_ConditionalConstraint.class, c_Constraint.class);
    }

    public static c_Constraint encodeConstraint(TrackWithInertialDirectionYawSteeringConstraint pointingConstraint) {
        return (c_Constraint) encode(pointingConstraint, c_PointingConstraint.class, c_GeometricConstraint.class,
            c_ConditionalConstraint.class, c_Constraint.class);
    }

    public static c_Constraint encodeConstraint(TrackWithPowerOptimizedYawSteeringConstraint pointingConstraint) {
        return (c_Constraint) encode(pointingConstraint, c_PointingConstraint.class, c_GeometricConstraint.class,
            c_ConditionalConstraint.class, c_Constraint.class);
    }

    public static c_Constraint encodeConstraint(NadirWithPowerOptimizedYawSteeringConstraint pointingConstraint) {
        return (c_Constraint) encode(pointingConstraint, c_PointingConstraint.class, c_GeometricConstraint.class,
            c_ConditionalConstraint.class, c_Constraint.class);
    }

    public static c_Constraint encodeConstraint(NadirWithGroundTrackAlignedYawSteeringConstraint pointingConstraint) {
        return (c_Constraint) encode(pointingConstraint, c_PointingConstraint.class, c_GeometricConstraint.class,
            c_ConditionalConstraint.class, c_Constraint.class);
    }

    public static c_Constraint encodeConstraint(NadirWithOrbitalPoleAlignedYawSteeringConstraint pointingConstraint) {
        return (c_Constraint) encode(pointingConstraint, c_PointingConstraint.class, c_GeometricConstraint.class,
            c_ConditionalConstraint.class, c_Constraint.class);
    }

    public static c_Constraint encodeConstraint(
        LimbPointingWithPowerOptimizedYawSteeringConstraint pointingConstraint) {
        return (c_Constraint) encode(pointingConstraint, c_PointingConstraint.class, c_GeometricConstraint.class,
            c_ConditionalConstraint.class, c_Constraint.class);
    }

    public static c_Constraint encodeConstraint(
        LimbPointingWithInertialDirectionYawSteeringConstraint pointingConstraint) {
        return (c_Constraint) encode(pointingConstraint, c_PointingConstraint.class, c_GeometricConstraint.class,
            c_ConditionalConstraint.class, c_Constraint.class);
    }

    public static c_Constraint encodeConstraint(
        VelocityPointingWithOrbitalPoleYawSteeringConstraint pointingConstraint) {
        return (c_Constraint) encode(pointingConstraint, c_PointingConstraint.class, c_GeometricConstraint.class,
            c_ConditionalConstraint.class, c_Constraint.class);
    }

    public static c_ConstraintList encodeConstraints(List<Constraint> constraints) {
        return (c_ConstraintList) encode(constraints, c_ConstraintList.class);
    }

    public static Constraint decode(c_Constraint c_constraint) {
        return (Constraint) decode((Object) c_constraint);
    }

    public static List<Constraint> decode(c_ConstraintList c_constraintList) {
        return decode((ElementList) c_constraintList);
    }

    /**
     * Expression
     */

    public static c_Expression encode(b_Expression expression) {
        return (c_Expression) encode(expression, c_Expression.class);
    }

    public static c_Expression encode(BooleanExpression booleanExpression) {
        return (c_Expression) encode(booleanExpression, c_Expression.class);
    }

    public static c_Expression encode(TimeExpression timeExpression) {
        return (c_Expression) encode(timeExpression, c_Expression.class);
    }

    public static c_Expression encode(DurationExpression durationExpression) {
        return (c_Expression) encode(durationExpression, c_Expression.class);
    }

    public static c_Expression encode(PositionExpression positionExpression) {
        return (c_Expression) encode(positionExpression, c_Expression.class);
    }

    public static c_Expression encode(RealExpression realExpression) {
        return (c_Expression) encode(realExpression, c_Expression.class);
    }

    public static c_Expression encode(IntegerExpression integerExpression) {
        return (c_Expression) encode(integerExpression, c_Expression.class);
    }

    public static c_Expression encode(DirectionExpression directionExpression) {
        return (c_Expression) encode(directionExpression, c_Expression.class);
    }

    public static c_Expression encode(ObjectExpression objectExpression) {
        return (c_Expression) encode(objectExpression, c_Expression.class);
    }

    public static c_ExpressionList encodeExpressions(List<Expression> expressions) {
        return (c_ExpressionList) encode(expressions, c_ExpressionList.class);
    }

    public static Expression decode(c_Expression c_expression) {
        return (Expression) decode((Object) c_expression);
    }

    public static List<Expression> decode(c_ExpressionList c_expressionList) {
        return decode((ElementList) c_expressionList);
    }

    /**
     * Effect
     */

    public static c_Effect encode(SimpleEffect simpleEffect) {
        return (c_Effect) encode(simpleEffect, c_Effect.class);
    }

    public static c_Effect encode(ComplexEffect complexEffect) {
        return (c_Effect) encode(complexEffect, c_Effect.class);
    }

    public static c_EffectList encodeEffects(List<Effect> effects) {
        return (c_EffectList) encode(effects, c_EffectList.class);
    }

    public static Effect decode(c_Effect c_effect) {
        return (Effect) decode((Object) c_effect);
    }

    public static List<Effect> decode(c_EffectList c_effectList) {
        return decode((ElementList) c_effectList);
    }

    /**
     * Repetition
     */

    public static c_Repetition encode(EventRepetition eventRepetition) {
        return (c_Repetition) encode(eventRepetition, c_Repetition.class);
    }

    public static c_Repetition encode(GeometricRepetition geometricRepetition) {
        return (c_Repetition) encode(geometricRepetition, c_Repetition.class);
    }

    public static c_Repetition encode(OrbitalRepetition orbitalRepetition) {
        return (c_Repetition) encode(orbitalRepetition, c_Repetition.class);
    }

    public static c_Repetition encode(RevolutionRepetition revolutionRepetition) {
        return (c_Repetition) encode(revolutionRepetition, c_Repetition.class);
    }

    public static c_Repetition encode(TemporalRepetition temporalRepetition) {
        return (c_Repetition) encode(temporalRepetition, c_Repetition.class);
    }

    public static c_RepetitionList encodeRepetitions(List<Repetition> repetitions) {
        return (c_RepetitionList) encode(repetitions, c_RepetitionList.class);
    }

    public static Repetition decode(c_Repetition c_repetition) {
        return (Repetition) decode((Object) c_repetition);
    }

    public static List<Repetition> decode(c_RepetitionList c_repetitionList) {
        return decode((ElementList) c_repetitionList);
    }

    /**
     * Resource Definition
     */

    public static c_ResourceDefinitionDetails encode(b_ResourceDefinitionDetails resourceDefinition) {
        return (c_ResourceDefinitionDetails) encode(resourceDefinition, c_ResourceDefinitionDetails.class);
    }

    public static c_ResourceDefinitionDetails encode(NumericResourceDefinitionDetails numericResourceDefinition) {
        return (c_ResourceDefinitionDetails) encode(numericResourceDefinition, c_ResourceDefinitionDetails.class);
    }

    public static c_ResourceDefinitionDetails encode(StringResourceDefinitionDetails stringResourceDefinition) {
        return (c_ResourceDefinitionDetails) encode(stringResourceDefinition, c_ResourceDefinitionDetails.class);
    }

    public static c_ResourceDefinitionDetails encode(StatusResourceDefinitionDetails statusResourceDefinition) {
        return (c_ResourceDefinitionDetails) encode(statusResourceDefinition, c_ResourceDefinitionDetails.class);
    }

    public static c_ResourceDefinitionDetailsList encodeResourceDefinitions(
        List<ResourceDefinitionDetails> resourceDefinitions) {
        return (c_ResourceDefinitionDetailsList) encode(resourceDefinitions, c_ResourceDefinitionDetailsList.class);
    }

    public static ResourceDefinitionDetails decode(c_ResourceDefinitionDetails c_resourceDefinition) {
        return (ResourceDefinitionDetails) decode((Object) c_resourceDefinition);
    }

    public static List<ResourceDefinitionDetails> decode(
        c_ResourceDefinitionDetailsList c_resourceDefinitionDetailsList) {
        return decode((ElementList) c_resourceDefinitionDetailsList);
    }

    /**
     * Resource Update
     */

    public static c_ResourceUpdateDetails encode(b_ResourceUpdateDetails resourceUpdate) {
        return (c_ResourceUpdateDetails) encode(resourceUpdate, c_ResourceUpdateDetails.class);
    }

    public static c_ResourceUpdateDetails encode(NumericResourceUpdateDetails numericResourceUpdate) {
        return (c_ResourceUpdateDetails) encode(numericResourceUpdate, c_ResourceUpdateDetails.class);
    }

    public static c_ResourceUpdateDetails encode(StringResourceUpdateDetails stringResourceUpdate) {
        return (c_ResourceUpdateDetails) encode(stringResourceUpdate, c_ResourceUpdateDetails.class);
    }

    public static c_ResourceUpdateDetails encode(StatusResourceUpdateDetails statusResourceUpdate) {
        return (c_ResourceUpdateDetails) encode(statusResourceUpdate, c_ResourceUpdateDetails.class);
    }

    public static c_ResourceUpdateDetailsList encodeResourceUpdates(List<ResourceUpdateDetails> resourceUpdates) {
        return (c_ResourceUpdateDetailsList) encode(resourceUpdates, c_ResourceUpdateDetailsList.class);
    }

    public static ResourceUpdateDetails decode(c_ResourceUpdateDetails c_resourceUpdate) {
        return (ResourceUpdateDetails) decode((Object) c_resourceUpdate);
    }

    public static List<ResourceUpdateDetails> decode(c_ResourceUpdateDetailsList c_resourceUpdateDetailsList) {
        return decode((ElementList) c_resourceUpdateDetailsList);
    }

    /**
     * Trigger
     */

    public static c_Trigger encode(EventTrigger eventTrigger) {
        return (c_Trigger) encode(eventTrigger, c_Trigger.class);
    }

    public static c_Trigger encode(OrbitTrigger orbitTrigger) {
        return (c_Trigger) encode(orbitTrigger, c_Trigger.class);
    }

    public static c_Trigger encode(PositionTrigger positionTrigger) {
        return (c_Trigger) encode(positionTrigger, c_Trigger.class);
    }

    public static c_Trigger encode(RevolutionTrigger revolutionTrigger) {
        return (c_Trigger) encode(revolutionTrigger, c_Trigger.class);
    }

    public static c_Trigger encode(TimeTrigger timeTrigger) {
        return (c_Trigger) encode(timeTrigger, c_Trigger.class);
    }

    public static c_TriggerList encodeTriggers(List<Trigger> triggers) {
        return (c_TriggerList) encode(triggers, c_TriggerList.class);
    }

    public static Trigger decode(c_Trigger c_trigger) {
        return (Trigger) decode((Object) c_trigger);
    }

    public static List<Trigger> decode(c_TriggerList c_triggerList) {
        return decode((ElementList) c_triggerList);
    }

    /**
     * Helper method to aid transforming an object to a polymorphic fix structure.
     * The polymorphism fix class is automatically detected.
     *
     * @param object to be encoded (e.g. ArgDef)
     * @return The encoded object with polymorphism fix applied
     */
    public static Object encode(Object object) {
        Class[] classes = getPolymorphismFixClasses(object);
        return encode(object, classes);
    }

    /**
     * Helper method to aid transforming an object to a polymorphic fix structure
     *
     * @param object to be encoded (e.g. ArgDef)
     * @param clazz of polymorphism fix class to be applied (e.g. c_ArgDef.class). Classes are applied recursively from left to right
     * @return The encoded object with polymorphism fix applied
     */
    public static Object encode(Object object, Class... clazz) {
        if (clazz.length == 0) {
            return object;
        }
        Object encodedObject = null;
        try {
            encodedObject = clazz[0].getConstructor().newInstance();
        } catch (InstantiationException |
                 IllegalAccessException |
                 InvocationTargetException |
                 NoSuchMethodException |
                 SecurityException e) {
            LOGGER.log(Level.WARNING, null, e);
            return null;
        }
        Field[] fields = encodedObject.getClass().getDeclaredFields();
        for (Field field : fields) {
            int modifier = field.getModifiers();
            if (!Modifier.isFinal(modifier) && field.getType() == object.getClass() && !field.isSynthetic()) {
                field.setAccessible(true);
                try {
                    field.set(encodedObject, object);
                } catch (IllegalAccessException e) {
                    LOGGER.log(Level.WARNING, null, e);
                    return null;
                }
            }
        }
        return encode(encodedObject, Arrays.copyOfRange(clazz, 1, clazz.length));
    }

    /**
     * Helper method to aid transforming an objects list to a polymorphic fix structure list
     *
     * @param objectList to be encoded (e.g List{@literal <}ArgDef{@literal >})
     * @param clazz of polymorphism fix to be applied (e.g. c_ArgDefList.class)
     * @return The encoded object with polymorphism fix applied
     */
    public static Object encode(List objectList, Class clazz) {
        ArrayList<Object> encodedObjectList = null;
        try {
            encodedObjectList = (ArrayList<Object>) clazz.getConstructor().newInstance();
        } catch (InstantiationException |
                 IllegalAccessException |
                 InvocationTargetException |
                 NoSuchMethodException |
                 SecurityException e) {
            LOGGER.log(Level.WARNING, null, e);
            return null;
        }

        for (Object object : objectList) {
            Class[] classes = getPolymorphismFixClasses(object);
            Object encodedObject = encode(object, classes);
            encodedObjectList.add(encodedObject);
        }
        return encodedObjectList;
    }

    /**
     * Helper method to aid decoding polymorphic fix object
     *
     * @param object polymorphic fix object (e.g. c_ArgDef)
     * @return A decoded object (e.g. ArgDef)
     */
    public static Object decode(Object object) {
        Class polymorphicClass = null;
        Object foundConcreteObject = null;
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(object);
            } catch (IllegalAccessException e) {
                LOGGER.log(Level.WARNING, null, e);
                return null;
            }
            int modifier = field.getModifiers();
            if (!Modifier.isFinal(modifier) && value != null && !field.isSynthetic()) {
                Class fieldSuperClass = value.getClass().getSuperclass();

                if (polymorphicClass == null) {
                    polymorphicClass = fieldSuperClass;
                }

                // Check if all fields have same polymorphic superclass
                if (polymorphicClass != fieldSuperClass) {
                    throw new IllegalArgumentException("Element does not have required structure");
                }
                if (foundConcreteObject == null) {
                    if (polymorphicClass == java.lang.Object.class) {
                        foundConcreteObject = decode(value);
                    } else {
                        foundConcreteObject = value;
                    }
                } else {
                    throw new IllegalArgumentException("Multiple non-null fields found");
                }
            }
        }
        return foundConcreteObject;
    }

    /**
     * Helper method to aid decoding polymorphic fix object list
     *
     * @param elementList polymorphic fix object list (e.g. c_ArgDefList)
     * @return A decoded object (e.g. List{@literal <}ArgDef{@literal >})
     */
    public static List decode(ElementList elementList) {
        List<Object> decodedList = new ArrayList<>();
        for (Object object : elementList) {
            decodedList.add(decode(object));
        }
        return decodedList;
    }

    private static Class[] getPolymorphismFixClasses(Object object) {
        List<Class> result = new ArrayList<>();

        // Four levels deep
        if (object instanceof PointingConstraint) {
            result.add(c_PointingConstraint.class);
        }

        // Three levels deep
        if (object instanceof GeometricConstraint) {
            result.add(c_GeometricConstraint.class);
        }
        if (object instanceof ResourceConstraint) {
            result.add(c_ResourceConstraint.class);
        }
        if (object instanceof TemporalConstraint) {
            result.add(c_TemporalConstraint.class);
        }

        // Two levels deep
        if (object instanceof ConditionalConstraint) {
            result.add(c_ConditionalConstraint.class);
        }
        if (object instanceof Effect) {
            result.add(c_Effect.class);
        }

        // Single level deep
        if (object instanceof ActivityDetails) {
            result.add(c_ActivityDetails.class);
        }
        if (object instanceof ArgDef) {
            result.add(c_ArgDef.class);
        }
        if (object instanceof Constraint) {
            result.add(c_Constraint.class);
        }
        if (object instanceof Expression) {
            result.add(c_Expression.class);
        }
        if (object instanceof Repetition) {
            result.add(c_Repetition.class);
        }
        if (object instanceof ResourceDefinitionDetails) {
            result.add(c_ResourceDefinitionDetails.class);
        }
        if (object instanceof ResourceUpdateDetails) {
            result.add(c_ResourceUpdateDetails.class);
        }
        if (object instanceof Trigger) {
            result.add(c_Trigger.class);
        }

        return result.toArray(new Class[result.size()]);
    }
}
