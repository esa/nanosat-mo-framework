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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.MALRequestOperation;
import org.ccsds.moims.mo.mal.MALSendOperation;
import org.ccsds.moims.mo.mal.MALSubmitOperation;
import org.ccsds.moims.mo.mal.OperationField;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;

/**
 * Turns the operations of a MO service into {@link McpTool} descriptors by
 * reading the service metadata ({@code <Service>Info.OPERATIONS}). Each
 * request/submit/send operation becomes one tool whose name, description and
 * argument schema are derived entirely from the operation and its fields.
 *
 * <p>
 * The v0 scope handles the SEND, SUBMIT and REQUEST interaction patterns.
 * PUB-SUB, INVOKE and PROGRESS operations, and operations using unsupported
 * argument types, are skipped.
 */
public final class OperationToolMapper {

    private static final Logger LOGGER = Logger.getLogger(OperationToolMapper.class.getName());

    private OperationToolMapper() {
    }

    /**
     * Builds the MCP tools for all supported operations of a service.
     *
     * @param consumer The MAL consumer used to dispatch the operations.
     * @param operations The operations of the service (from {@code OPERATIONS}).
     * @return The list of generated tools.
     */
    public static List<McpTool> toolsFor(MALConsumer consumer, MALOperation[] operations) {
        List<McpTool> tools = new ArrayList<>();
        for (MALOperation op : operations) {
            McpTool tool = toToolOrNull(consumer, op);
            if (tool != null) {
                tools.add(tool);
            }
        }
        return tools;
    }

    private static McpTool toToolOrNull(MALConsumer consumer, MALOperation op) {
        UOctet inputStage = inputStageFor(op);
        if (inputStage == null) {
            return null; // PUB-SUB, INVOKE or PROGRESS: not supported in v0
        }

        String name = op.getName().getValue();
        OperationField[] fields = op.getFieldsOnStage(inputStage);

        String schema;
        try {
            schema = buildInputSchema(fields);
        } catch (MoJsonConverter.UnsupportedTypeException ex) {
            LOGGER.log(Level.INFO, "Skipping operation ''{0}'': {1}",
                    new Object[]{name, ex.getMessage()});
            return null;
        }

        return new McpTool(name, op.getComment(), schema,
                args -> invoke(consumer, op, fields, args));
    }

    private static UOctet inputStageFor(MALOperation op) {
        if (op instanceof MALRequestOperation) {
            return MALRequestOperation.REQUEST_STAGE;
        }
        if (op instanceof MALSubmitOperation) {
            return MALSubmitOperation.SUBMIT_STAGE;
        }
        if (op instanceof MALSendOperation) {
            return MALSendOperation.SEND_STAGE;
        }
        return null;
    }

    private static String buildInputSchema(OperationField[] fields) {
        JsonObject schema = new JsonObject();
        schema.addProperty("type", "object");
        JsonObject properties = new JsonObject();
        JsonArray required = new JsonArray();
        for (OperationField field : fields) {
            JsonObject property = MoJsonConverter.schemaType(field.getTypeId());
            if (field.getComment() != null && !field.getComment().isEmpty()) {
                property.addProperty("description", field.getComment());
            }
            properties.add(field.getFieldName(), property);
            if (!field.isNullable()) {
                required.add(field.getFieldName());
            }
        }
        schema.add("properties", properties);
        schema.add("required", required);
        return schema.toString();
    }

    private static String invoke(MALConsumer consumer, MALOperation op,
            OperationField[] fields, Map<String, Object> args) {
        Object[] body = new Object[fields.length];
        for (int i = 0; i < fields.length; i++) {
            OperationField field = fields[i];
            body[i] = MoJsonConverter.toMalElement(field.getTypeId(),
                    args.get(field.getFieldName()));
        }

        String name = op.getName().getValue();
        try {
            if (op instanceof MALSubmitOperation) {
                consumer.submit((MALSubmitOperation) op, body);
                return "Operation '" + name + "' submitted successfully.";
            }
            if (op instanceof MALSendOperation) {
                consumer.send((MALSendOperation) op, body);
                return "Operation '" + name + "' sent.";
            }
            if (op instanceof MALRequestOperation) {
                MALMessageBody response = consumer.request((MALRequestOperation) op, body);
                return formatResponse((MALRequestOperation) op, response);
            }
            return "Operation '" + name + "' uses an unsupported interaction pattern.";
        } catch (MALInteractionException | MALException ex) {
            LOGGER.log(Level.WARNING, "Invocation of '" + name + "' failed", ex);
            return "Error invoking '" + name + "': " + ex.getMessage();
        }
    }

    private static String formatResponse(MALRequestOperation op, MALMessageBody response)
            throws MALException {
        OperationField[] fields = op.getFieldsOnStage(MALRequestOperation.REQUEST_RESPONSE_STAGE);
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            OperationField field = fields[i];
            Object element = response.getBodyElement(i,
                    MoJsonConverter.emptyElement(field.getTypeId()));
            text.append(field.getFieldName()).append(": ").append(element).append("\n");
        }
        return text.toString();
    }
}
