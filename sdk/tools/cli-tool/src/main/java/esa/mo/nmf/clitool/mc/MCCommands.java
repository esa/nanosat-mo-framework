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
package esa.mo.nmf.clitool.mc;

import picocli.CommandLine;

/**
 * @author marcel.mikolajko
 */
public class MCCommands {

    @CommandLine.Command(name = "aggregation", subcommands = {
        AggregationCommands.AggregationMonitorValue.class,
        AggregationCommands.AggregationEnableGeneration.class,
        AggregationCommands.AggregationDisableGeneration.class
    })
    public static class Aggregation {
    }

    @CommandLine.Command(name = "parameter", subcommands = {
        ParameterCommands.ParameterMonitorValue.class,
        ParameterCommands.ParameterEnableGeneration.class,
        ParameterCommands.ParameterDisableGeneration.class,
        ParameterCommands.GetParameters.class,
        ParameterCommands.ListParameters.class
    })
    public static class Parameter {
    }

    @CommandLine.Command(name = "action", subcommands = {
        ActionCommands.SubmitAction.class,
        ActionCommands.ListActions.class,})
    public static class Action {
    }
}
