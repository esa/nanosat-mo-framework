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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALInvokeOperation;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.MALProgressOperation;
import org.ccsds.moims.mo.mal.MALRequestOperation;
import org.ccsds.moims.mo.mal.MALSendOperation;
import org.ccsds.moims.mo.mal.MALSubmitOperation;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.OperationField;
import org.ccsds.moims.mo.mal.ServiceInfo;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.consumer.MALInteractionAdapter;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * Turns the operations of a MO service into {@link McpTool} descriptors by
 * reading the service metadata ({@code <Service>Info.OPERATIONS}). Each
 * operation becomes one tool whose name, description and argument schema are
 * derived entirely from the operation and its fields. The tool name is the
 * service name and the operation name, so that operations of the same name in
 * different services do not clash.
 *
 * <p>
 * The SEND, SUBMIT, REQUEST, INVOKE and PROGRESS interaction patterns are
 * mapped; an INVOKE or PROGRESS call waits for its final response, so it reads
 * as one call. PUB-SUB operations, and operations using unsupported argument
 * types, are skipped.
 */
public final class OperationToolMapper {

    private static final Logger LOGGER = Logger.getLogger(OperationToolMapper.class.getName());

    private OperationToolMapper() {
    }

    /**
     * Builds the MCP tools for all supported operations of a service. The
     * service name and its operations are both read from the service metadata.
     *
     * @param service The service metadata ({@code <Service>Info}).
     * @param consumer The MAL consumer used to dispatch the operations.
     * @return The list of generated tools.
     */
    public static List<McpTool> toolsFor(ServiceInfo service, MALConsumer consumer) {
        String serviceName = service.getName().getValue();
        List<McpTool> tools = new ArrayList<>();
        for (MALOperation op : service.getOperations()) {
            McpTool tool = toToolOrNull(serviceName, consumer, op);
            if (tool != null) {
                tools.add(tool);
            }
        }
        return tools;
    }

    private static McpTool toToolOrNull(String serviceName, MALConsumer consumer, MALOperation op) {
        UOctet inputStage = inputStageFor(op);
        if (inputStage == null) {
            return null; // PUB-SUB: does not map to a request/response tool
        }

        // The service name and the operation name, so that operations of the
        // same name in different services do not clash as MCP tools
        String name = serviceName + "_" + op.getName().getValue();
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
        if (op instanceof MALInvokeOperation) {
            return MALInvokeOperation.INVOKE_STAGE;
        }
        if (op instanceof MALProgressOperation) {
            return MALProgressOperation.PROGRESS_STAGE;
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

    private static McpTool.Result invoke(MALConsumer consumer, MALOperation op,
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
                return new McpTool.Result("Operation '" + name + "' submitted successfully.", false);
            }
            if (op instanceof MALSendOperation) {
                consumer.send((MALSendOperation) op, body);
                return new McpTool.Result("Operation '" + name + "' sent.", false);
            }
            if (op instanceof MALRequestOperation) {
                MALMessageBody response = consumer.request((MALRequestOperation) op, body);
                return new McpTool.Result(
                        formatBody(op, MALRequestOperation.REQUEST_RESPONSE_STAGE, response), false);
            }
            if (op instanceof MALInvokeOperation) {
                ResponseCollector collector = new ResponseCollector();
                consumer.invoke((MALInvokeOperation) op, collector, body);
                MALMessageBody response = collector.awaitResponse();
                return new McpTool.Result(
                        formatBody(op, MALInvokeOperation.INVOKE_RESPONSE_STAGE, response), false);
            }
            if (op instanceof MALProgressOperation) {
                ResponseCollector collector = new ResponseCollector();
                consumer.progress((MALProgressOperation) op, collector, body);
                MALMessageBody response = collector.awaitResponse();
                return new McpTool.Result(
                        collector.updatesText(op) + formatBody(op,
                                MALProgressOperation.PROGRESS_RESPONSE_STAGE, response), false);
            }
            return new McpTool.Result("Operation '" + name + "' uses an unsupported interaction pattern.", true);
        } catch (MALInteractionException | MALException ex) {
            LOGGER.log(Level.WARNING, "Invocation of '" + name + "' failed", ex);
            return new McpTool.Result("Error invoking '" + name + "': " + ex.getMessage(), true);
        }
    }

    /**
     * Formats the fields of a message body at the given stage of an operation
     * as a line of text per field.
     */
    private static String formatBody(MALOperation op, UOctet stage, MALMessageBody body)
            throws MALException {
        if (body == null) {
            return "";
        }
        OperationField[] fields = op.getFieldsOnStage(stage);
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            OperationField field = fields[i];
            Object element = body.getBodyElement(i,
                    MoJsonConverter.emptyElement(field.getTypeId()));
            text.append(field.getFieldName()).append(": ").append(element).append("\n");
        }
        return text.toString();
    }

    /**
     * Turns the two-way INVOKE and PROGRESS patterns into one blocking call: it
     * collects the UPDATE messages, waits for the final RESPONSE, and throws
     * the error of a failed stage, so that a tool call returns the outcome.
     */
    private static final class ResponseCollector extends MALInteractionAdapter {

        private static final long TIMEOUT_MINUTES = 10;

        private final CountDownLatch done = new CountDownLatch(1);
        private final AtomicReference<MALMessageBody> response = new AtomicReference<>();
        private final AtomicReference<MOErrorException> error = new AtomicReference<>();
        private final List<MALMessageBody> updates = new ArrayList<>();

        /**
         * Waits for the final RESPONSE and returns its body, or throws the
         * error of a stage that failed.
         */
        MALMessageBody awaitResponse() throws MALInteractionException, MALException {
            try {
                if (!done.await(TIMEOUT_MINUTES, TimeUnit.MINUTES)) {
                    throw new MALException("No response within " + TIMEOUT_MINUTES + " minutes");
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new MALException("Interrupted while waiting for the response", ex);
            }
            if (error.get() != null) {
                throw new MALInteractionException(error.get());
            }
            return response.get();
        }

        /** Formats the UPDATE messages collected before the RESPONSE. */
        String updatesText(MALOperation op) throws MALException {
            StringBuilder text = new StringBuilder();
            for (MALMessageBody update : updates) {
                text.append(formatBody(op, MALProgressOperation.PROGRESS_UPDATE_STAGE, update));
            }
            return text.toString();
        }

        private void fail(MALErrorBody body) {
            try {
                error.set(body.getError());
            } catch (MALException ex) {
                error.set(null);
            }
            done.countDown();
        }

        @Override
        public void invokeResponseReceived(MALMessageHeader header, MALMessageBody body, Map qos) {
            response.set(body);
            done.countDown();
        }

        @Override
        public void invokeAckErrorReceived(MALMessageHeader header, MALErrorBody body, Map qos) {
            fail(body);
        }

        @Override
        public void invokeResponseErrorReceived(MALMessageHeader header, MALErrorBody body, Map qos) {
            fail(body);
        }

        @Override
        public void progressUpdateReceived(MALMessageHeader header, MALMessageBody body, Map qos) {
            updates.add(body);
        }

        @Override
        public void progressResponseReceived(MALMessageHeader header, MALMessageBody body, Map qos) {
            response.set(body);
            done.countDown();
        }

        @Override
        public void progressAckErrorReceived(MALMessageHeader header, MALErrorBody body, Map qos) {
            fail(body);
        }

        @Override
        public void progressUpdateErrorReceived(MALMessageHeader header, MALErrorBody body, Map qos) {
            fail(body);
        }

        @Override
        public void progressResponseErrorReceived(MALMessageHeader header, MALErrorBody body, Map qos) {
            fail(body);
        }
    }
}
