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
 * Annotates a Java method as an MC Action.
 * Every method annotated as an action has to have the following arguments before the
 * parameters (not annotated with @ActionParameter):
 * <p>
 * <code>Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction</code>
 * <p>
 * Every argument after these, has to be annotated with @ActionParameter
 *
 * <p>
 * Example:
 * <pre>
 * <b>&#64;Action(name = "Clock.setTimeUsingDeltaMilliseconds",
 *   description = "Sets the clock using a diff between the on-board time and the desired time.")</b>
 * public UInteger setTimeUsingDeltaMilliseconds(
 *     Long actionInstanceObjId,
 *     boolean reportProgress,
 *     MALInteraction interaction,
 *     <b>&#64;ActionParameter(name = "delta", rawUnit = "milliseconds") Long delta</b>) {
 *   String str = (new SimpleDateFormat(DATE_PATTERN)).format(new Date(System.currentTimeMillis() + delta));

 *   ShellCommander shell = new ShellCommander();
 *   shell.runCommand("date -s \"" + str + " UTC\" | hwclock --systohc");
 *   return null;
 * }
 * </pre>
 * @author Kevin Otto
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Action {

    String name() default "";

    String description() default "";

    short category() default 0;

    int stepCount() default 0;

    String rawUnit() default "";
}
