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
package esa.mo.nmf.mcp;

import java.util.Map;
import java.util.function.Function;

/**
 * A Model Context Protocol tool, described independently of any MCP SDK type.
 * The {@link McpAdapterServer} binds these descriptors to the concrete MCP SDK,
 * keeping the tool generation in {@link OperationToolMapper} free of SDK churn.
 */
public final class McpTool {

    private final String name;
    private final String description;
    private final String inputSchema;
    private final Function<Map<String, Object>, String> handler;

    /**
     * Constructor.
     *
     * @param name The tool name.
     * @param description The tool description.
     * @param inputSchema The JSON Schema of the tool arguments.
     * @param handler The handler invoked with the parsed arguments, returning
     * the textual result.
     */
    public McpTool(String name, String description, String inputSchema,
            Function<Map<String, Object>, String> handler) {
        this.name = name;
        this.description = description;
        this.inputSchema = inputSchema;
        this.handler = handler;
    }

    /**
     * Returns the tool name.
     *
     * @return The tool name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the tool description.
     *
     * @return The tool description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the JSON Schema of the tool arguments.
     *
     * @return The JSON Schema of the tool arguments.
     */
    public String getInputSchema() {
        return inputSchema;
    }

    /**
     * Returns the handler invoked with the parsed arguments.
     *
     * @return The tool handler.
     */
    public Function<Map<String, Object>, String> getHandler() {
        return handler;
    }
}
