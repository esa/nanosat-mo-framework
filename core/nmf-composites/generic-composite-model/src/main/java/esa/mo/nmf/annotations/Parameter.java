/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
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
 *
 * @author Kevin Otto <Kevin@KevinOtto.de>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Parameter
{

  /**
   * The name of this parameter
   *
   * default: ""
   */
  public String name() default "";

  /**
   * The description of this parameter
   *
   * default: ""
   */
  public String description() default "";

  /**
   * MAL Type of this parameter.
   *
   * default: auto generated: only possible when directly using Attribute as field type (linke
   * UInteger, Integer, UShort, ...) or build in java types (int, double, String ...)
   */
  public String malType() default "";

  /**
   * The raw unit of this parameter examples: rad, m/s ...
   *
   * default: ""
   */
  public String rawUnit() default "";

  /**
   * if generation should be enabled
   *
   * default: false
   */
  public boolean generationEnabled() default true;

  /**
   * How often the parameter will be reported (in Seconds)
   *
   * default: every 0 seconds which disables automatic generation interval
   */
  public double reportIntervalSeconds() default 0.0;

  /**
   * Name of the field containing a ParameterExpression instance containing the validity expression
   * for this parameter
   *
   * default: no validity check
   */
  public String validityExpressionFieldName() default "";

  /**
   * Name of the field containing a ParameterConversion instance containing the conversion for this
   * parameter
   *
   * default: no conversion
   */
  public String conversionFunctionName() default "";

  /**
   * If this parameter should be read only. Parameters which are final are always read only!
   *
   * default: false
   */
  public boolean readOnly() default false;

  /**
   * The name of the function that will be called, every time the Parameter is get (before its value
   * is read).
   *
   * The functions is not allowed to have any parameters and all return statements are ignored. The
   * function needs to be public!
   *
   * default: no function is called
   */
  public String onGetFunction() default "";

}
