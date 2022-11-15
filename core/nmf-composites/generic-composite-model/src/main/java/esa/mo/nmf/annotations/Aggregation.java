/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package esa.mo.nmf.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Describes an Aggregation of MC Parameters exposed by an MC adapter.
 * Multiple Aggregation annotations can be present for the same class.
 *
 * <p>
 * Example:
 * <pre>
 * <b>&#64;Aggregation(</b>
 *   id = ExampleMCAdapter.AGGREGATION_MAG,
 *   description = "Aggregates Magnetometer components: X, Y, Z.",
 *   reportInterval = 10,
 *   sendUnchanged = true,
 *   sampleInterval = 3)
 * public class ExampleMCAdapter extends MonitorAndControlNMFAdapter
 * {
 *   public static final String AGGREGATION_MAG = "Magnetometer_Aggregation";
 *   <b>&#64;Parameter(</b>
 *       description = "The Magnetometer X component",
 *       rawUnit = "microTesla",
 *       generationEnabled = false,
 *       onGetFunction = "onGetMagneticField_X",
 *       readOnly = true,
 *       reportIntervalSeconds = 2,
 *       aggregations = {AGGREGATION_MAG})
 *   Float MagneticField_X = 0.0f;
 *   <b>&#64;Parameter(</b>
 *       description = "The Magnetometer Y component",
 *       rawUnit = "microTesla",
 *       generationEnabled = false,
 *       onGetFunction = "onGetMagneticField_Y",
 *       readOnly = true,
 *       reportIntervalSeconds = 2,
 *       aggregations = {AGGREGATION_MAG})
 *   Float MagneticField_Y = 0.0f;
 * }
 * </pre>
 *
 * @author Kevin Otto
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(Aggregations.class)
public @interface Aggregation {

    /**
     * unique id (name) of the Aggregation
     *
     * @return
     */
    String id();

    /**
     * description of the Aggregation. Defaults to ""
     *
     * @return
     */
    String description() default "";

    /**
     * category of the Aggregation. Defaults to GENERAL
     *
     * @return
     */
    int category() default 0;

    /**
     * Report interval in seconds. Defaults to 0 (non periodic)
     *
     * @return
     */
    double reportInterval() default 0;

    boolean sendUnchanged() default false;

    boolean sendDefinitions() default false;

    boolean filterEnabled() default false;

    int filterTimeout() default 0;

    boolean generationEnabled() default false;

    /**
     * Field of type ThresholdFilter
     *
     * @return
     */
    String thresholdFilterFieldName() default "";

    double sampleInterval() default 0;
}
