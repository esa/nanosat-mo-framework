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
package esa.mo.nmf.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a class field as a Parameter and exposes it on MC Parameter
 * interface.
 *
 * Parameters are not allowed to be null upon initialization.
 *
 * <p>
 * Example:
 * <pre>
 * public class ExampleMCAdapter extends MonitorAndControlNMFAdapter
 * {
 *   <b>&#64;Parameter(</b>
 *       description = "The Magnetometer X component",
 *       rawUnit = "microTesla",
 *       generationEnabled = false,
 *       onGetFunction = "onGetMagneticField_X",
 *       readOnly = true,
 *       reportIntervalSeconds = 2)
 *   Float MagneticField_X = 0.0f;
 *   <b>&#64;Parameter(</b>
 *       description = "The Magnetometer Y component",
 *       rawUnit = "microTesla",
 *       generationEnabled = false,
 *       onGetFunction = "onGetMagneticField_Y",
 *       readOnly = true,
 *       reportIntervalSeconds = 2)
 *   Float MagneticField_Y = 0.0f;
 * }
 * </pre>
 *
 * @author Kevin Otto
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Parameter {

    /**
     * The name of this parameter
     *
     * default: ""
     *
     * @return The name of the Parameter.
     */
    String name() default "";

    /**
     * The description of this parameter
     *
     * default: ""
     *
     * @return The description of the Parameter.
     */
    String description() default "";

    /**
     * MAL Type of this parameter.
     *
     * default: auto generated: only possible when directly using Attribute as
     * field type (linke UInteger, Integer, UShort, ...) or build in java types
     * (int, double, String ...)
     *
     * @return The malType of the Parameter.
     */
    String malType() default "";

    /**
     * The raw unit of this parameter examples: rad, m/s ...
     *
     * default: ""
     *
     * @return The rawUnit of the Parameter.
     */
    String rawUnit() default "";

    /**
     * if generation should be enabled
     *
     * default: false
     *
     * @return True if the generation of the Parameter is enabled.
     */
    boolean generationEnabled() default true;

    /**
     * How often the parameter will be reported (in Seconds)
     *
     * default: every 0 seconds which disables automatic generation interval
     *
     * @return The reporting interval of the Parameter.
     */
    double reportIntervalSeconds() default 0.0;

    /**
     * Name of the field containing a ParameterExpression instance containing
     * the validity expression for this parameter
     *
     * default: no validity check
     *
     * @return The validity expression of the Parameter.
     */
    String validityExpressionFieldName() default "";

    /**
     * Name of the field containing a ParameterConversion instance containing
     * the conversion for this parameter
     *
     * default: no conversion
     *
     * @return The function name of the conversion of the Parameter.
     */
    String conversionFunctionName() default "";

    /**
     * If this parameter should be read only. Parameters which are final are
     * always read only!
     *
     * default: false
     *
     * @return True if the Parameter is read-only.
     */
    boolean readOnly() default false;

    /**
     * If this parameter should have its value restored from archive on startup.
     *
     * default: true
     *
     * @return True if the Parameter was restored.
     */
    boolean restored() default true;

    /**
     * The name of the function that will be called, every time the Parameter is
     * get (before its value is read).
     *
     * The functions is not allowed to have any parameters and all return
     * statements are ignored. The function needs to be public!
     *
     * default: no function is called
     *
     * @return The name of the onGet function of the Parameter.
     */
    String onGetFunction() default "";

    /**
     * array of Aggregation ids this parameter belongs to.
     *
     * @return The aggregations of the Parameter.
     */
    String[] aggregations() default {};

}
