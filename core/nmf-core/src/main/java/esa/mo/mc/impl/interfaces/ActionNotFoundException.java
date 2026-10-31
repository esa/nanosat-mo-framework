/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
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
package esa.mo.mc.impl.interfaces;

/**
 * Thrown by an {@link ActionInvocationListener} when it does not recognise the
 * requested action. It signals "this action is not mine" — distinct from an
 * {@code ExecutionFailedException}, which signals "this action is mine but its
 * execution failed". A composite listener uses this distinction to forward an
 * action to the next listener instead of treating it as a failure.
 */
public class ActionNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String actionName;

    /**
     * Constructs a new ActionNotFoundException with a default message.
     *
     * @param actionName The name of the action that was not found.
     */
    public ActionNotFoundException(String actionName) {
        this(actionName, "Action not found: " + actionName);
    }

    /**
     * Constructs a new ActionNotFoundException.
     *
     * @param actionName The name of the action that was not found.
     * @param message The detail message.
     */
    public ActionNotFoundException(String actionName, String message) {
        super(message);
        this.actionName = actionName;
    }

    /**
     * Returns the name of the action that was not found.
     *
     * @return The action name.
     */
    public String getActionName() {
        return actionName;
    }
}
