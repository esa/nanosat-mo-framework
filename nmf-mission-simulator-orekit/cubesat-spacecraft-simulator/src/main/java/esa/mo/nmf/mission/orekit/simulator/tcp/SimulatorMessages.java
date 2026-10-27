/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2026      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 *  You may not use this file except in compliance with the License.
 * 
 *  Except as expressly set forth in this License, the Software is provided to
 *  You on an "as is" basis and without warranties of any kind, including without
 *  limitation merchantability, fitness for a particular purpose, absence of
 *  defects or errors, accuracy or non-infringement of intellectual property rights.
 *  
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 *  ----------------------------------------------------------------------------
 */
package esa.mo.nmf.mission.orekit.simulator.tcp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import esa.mo.nmf.mission.orekit.simulator.util.ArgumentDescriptor;
import esa.mo.nmf.mission.orekit.simulator.util.CommandDescriptor;
import esa.mo.nmf.mission.orekit.simulator.util.CommandResult;
import esa.mo.nmf.mission.orekit.simulator.util.PlatformMessage;
import esa.mo.nmf.mission.orekit.simulator.util.SimulatorData;
import esa.mo.nmf.mission.orekit.simulator.util.SimulatorDeviceData;
import esa.mo.nmf.mission.orekit.simulator.util.SimulatorHeader;
import esa.mo.nmf.mission.orekit.simulator.util.SimulatorSchedulerPiece;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The messages that the simulator and its clients exchange over the socket,
 * as JSON: one message per line, {@code {"type": ..., "value": ...}}.
 * <p>
 * The type of each message is one of a fixed set of names, and the receiver
 * builds the message from it: nothing in a message names a class to be
 * created, so a client cannot have the simulator create anything but its own
 * messages, which is what made Java serialization dangerous here
 * (NMF-2026-001). A value of the simulator that may be of several types, such
 * as the argument of a command or what a command returns, is sent with its
 * kind, which is one of a closed list of plain types.
 * <p>
 * A line is at most {@value #MAX_LINE_BYTES} bytes, so that a peer cannot
 * have the other side hold an endless line in memory.
 */
public final class SimulatorMessages {

    private static final Logger LOGGER = Logger.getLogger(SimulatorMessages.class.getName());

    /** The longest line accepted: enough for a picture of the camera, in Base64. */
    public static final int MAX_LINE_BYTES = 64 * 1024 * 1024;

    private static final String TYPE = "type";
    private static final String VALUE = "value";
    private static final String KIND = "kind";

    private static final String TEXT = "Text";
    private static final String NUMBER = "Integer";
    private static final String PROPERTIES = "Properties";
    private static final String LIST = "List";

    /** The messages that are sent as an object of the simulator, by their name on the wire. */
    private static final Map<String, Class<?>> OBJECTS = new LinkedHashMap<>();

    static {
        OBJECTS.put("PlatformMessage", PlatformMessage.class);
        OBJECTS.put("Header", SimulatorHeader.class);
        OBJECTS.put("SimulatorData", SimulatorData.class);
        OBJECTS.put("Command", CommandDescriptor.class);
        OBJECTS.put("Result", CommandResult.class);
        OBJECTS.put("DeviceData", SimulatorDeviceData.class);
        OBJECTS.put("SchedulerPiece", SimulatorSchedulerPiece.class);
    }

    private static final Gson GSON = new GsonBuilder()
            // Constants and what is not meant to leave the process are not sent
            .excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.FINAL)
            .registerTypeAdapter(Date.class, new DateAdapter())
            .registerTypeAdapter(ArgumentDescriptor.class, new ArgumentAdapter())
            .registerTypeAdapter(CommandResult.class, new ResultAdapter())
            .serializeSpecialFloatingPointValues()
            .disableHtmlEscaping()
            .create();

    private SimulatorMessages() {
    }

    /** Something that is not a message of the simulator, to be sent or received. */
    public static final class NotAMessageException extends IOException {

        private static final long serialVersionUID = 1L;

        NotAMessageException(String message) {
            super(message);
        }

        NotAMessageException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Writes a message as one line of JSON.
     *
     * @param message the message
     * @param out where to write it
     * @throws NotAMessageException if the message is not one of the simulator's; nothing is written then
     * @throws IOException if it cannot be written
     */
    public static void write(final Object message, final OutputStream out) throws IOException {
        byte[] line = encode(message).getBytes(StandardCharsets.UTF_8);
        out.write(line);
        out.write('\n');
        out.flush();
    }

    /**
     * Reads the next message.
     *
     * @param in where to read it from
     * @return the message, or null if the other side closed the connection
     * @throws NotAMessageException if what was read is not a message of the
     * simulator, or the line is too long
     * @throws IOException if it cannot be read
     */
    public static Object read(final InputStream in) throws IOException {
        String line = readLine(in);
        return (line == null) ? null : decode(line);
    }

    /**
     * Returns a message as one line of JSON, without the end of the line.
     *
     * @param message the message
     * @return the JSON
     * @throws NotAMessageException if the message is not one of the simulator's
     */
    public static String encode(final Object message) throws NotAMessageException {
        return GSON.toJson(toJson(message));
    }

    /**
     * Returns the message that a line of JSON holds.
     *
     * @param line the line, without the end of the line
     * @return the message
     * @throws NotAMessageException if the line is not a message of the simulator
     */
    public static Object decode(final String line) throws NotAMessageException {
        try {
            return fromJson(JsonParser.parseString(line));
        } catch (JsonParseException | IllegalStateException | ClassCastException | UnsupportedOperationException
                | IllegalArgumentException | NullPointerException ex) {
            throw new NotAMessageException("Not a message of the simulator: " + ex.getMessage(), ex);
        }
    }

    private static JsonObject toJson(final Object message) throws NotAMessageException {
        JsonObject json = new JsonObject();
        if (message instanceof String) {
            json.addProperty(TYPE, TEXT);
            json.addProperty(VALUE, (String) message);
        } else if (message instanceof Integer) {
            json.addProperty(TYPE, NUMBER);
            json.addProperty(VALUE, (Integer) message);
        } else if (message instanceof Properties) {
            JsonObject entries = new JsonObject();
            Properties properties = (Properties) message;
            for (String key : properties.stringPropertyNames()) {
                entries.addProperty(key, properties.getProperty(key));
            }
            json.addProperty(TYPE, PROPERTIES);
            json.add(VALUE, entries);
        } else if (message instanceof List) {
            JsonArray items = new JsonArray();
            for (Object item : (List<?>) message) {
                items.add(toJson(item));
            }
            json.addProperty(TYPE, LIST);
            json.add(VALUE, items);
        } else {
            String type = typeOf(message);
            json.addProperty(TYPE, type);
            json.add(VALUE, GSON.toJsonTree(message, OBJECTS.get(type)));
        }
        return json;
    }

    private static String typeOf(final Object message) throws NotAMessageException {
        if (message != null) {
            for (Map.Entry<String, Class<?>> object : OBJECTS.entrySet()) {
                if (object.getValue() == message.getClass()) {
                    return object.getKey();
                }
            }
        }
        throw new NotAMessageException("Not a message of the simulator: "
                + (message == null ? "null" : message.getClass().getName()));
    }

    private static Object fromJson(final JsonElement element) throws NotAMessageException {
        if (!element.isJsonObject()) {
            throw new NotAMessageException("A message must be a JSON object");
        }
        JsonObject json = element.getAsJsonObject();
        JsonElement typeElement = json.get(TYPE);
        if (typeElement == null || !typeElement.isJsonPrimitive()) {
            throw new NotAMessageException("A message must have a type");
        }
        String type = typeElement.getAsString();
        JsonElement value = json.get(VALUE);
        if (value == null) {
            throw new NotAMessageException("A message of type " + type + " must have a value");
        }

        switch (type) {
            case TEXT:
                return value.getAsString();
            case NUMBER:
                return value.getAsInt();
            case PROPERTIES:
                Properties properties = new Properties();
                for (Map.Entry<String, JsonElement> entry : value.getAsJsonObject().entrySet()) {
                    properties.setProperty(entry.getKey(), entry.getValue().getAsString());
                }
                return properties;
            case LIST:
                LinkedList<Object> items = new LinkedList<>();
                for (JsonElement item : value.getAsJsonArray()) {
                    items.add(fromJson(item));
                }
                return items;
            default:
                Class<?> object = OBJECTS.get(type);
                if (object == null) {
                    throw new NotAMessageException("Unknown type of message: " + type);
                }
                return GSON.fromJson(value, object);
        }
    }

    /**
     * Reads one line, up to the end of the line, which is not returned.
     *
     * @return the line, or null at the end of the stream
     */
    private static String readLine(final InputStream in) throws IOException {
        ByteArrayOutputStream line = new ByteArrayOutputStream();
        int b;
        while ((b = in.read()) != -1) {
            if (b == '\n') {
                return line.toString(StandardCharsets.UTF_8);
            }
            if (line.size() >= MAX_LINE_BYTES) {
                throw new NotAMessageException("A message is longer than " + MAX_LINE_BYTES + " bytes");
            }
            line.write(b);
        }
        if (line.size() > 0) {
            throw new NotAMessageException("The connection ended in the middle of a message");
        }
        return null;
    }

    /**
     * Returns a value that may be of several types, with its kind. A value of
     * a type that is not on the list is sent as text, so that it can still be
     * shown.
     */
    static JsonElement valueWithKind(final Object value) {
        if (value == null) {
            return JsonNull.INSTANCE;
        }
        JsonObject json = new JsonObject();
        Kind kind = Kind.of(value);
        if (kind == null) {
            LOGGER.log(Level.WARNING, "A value of type {0} is sent as text", value.getClass().getName());
            kind = Kind.STRING;
            json.addProperty(KIND, kind.label);
            json.addProperty(VALUE, String.valueOf(value));
            return json;
        }
        json.addProperty(KIND, kind.label);
        json.add(VALUE, kind.toJson(value));
        return json;
    }

    /** Returns the value that {@link #valueWithKind} wrote. */
    static Object valueOfKind(final JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return null;
        }
        JsonObject json = element.getAsJsonObject();
        String label = json.get(KIND).getAsString();
        Kind kind = Kind.ofLabel(label);
        if (kind == null) {
            throw new JsonParseException("Unknown kind of value: " + label);
        }
        return kind.fromJson(json.get(VALUE));
    }

    /** The plain types that a value of several possible types may have. */
    private enum Kind {
        STRING("String", String.class),
        BOOLEAN("Boolean", Boolean.class),
        BYTE("Byte", Byte.class),
        SHORT("Short", Short.class),
        INTEGER("Integer", Integer.class),
        LONG("Long", Long.class),
        FLOAT("Float", Float.class),
        DOUBLE("Double", Double.class),
        BYTES("byte[]", byte[].class),
        SHORTS("short[]", short[].class),
        INTEGERS("int[]", int[].class),
        LONGS("long[]", long[].class),
        FLOATS("float[]", float[].class),
        DOUBLES("double[]", double[].class),
        BOOLEANS("boolean[]", boolean[].class),
        STRINGS("String[]", String[].class);

        private final String label;
        private final Class<?> type;

        Kind(String label, Class<?> type) {
            this.label = label;
            this.type = type;
        }

        static Kind of(final Object value) {
            for (Kind kind : values()) {
                if (kind.type == value.getClass()) {
                    return kind;
                }
            }
            return null;
        }

        static Kind ofLabel(final String label) {
            for (Kind kind : values()) {
                if (kind.label.equals(label)) {
                    return kind;
                }
            }
            return null;
        }

        JsonElement toJson(final Object value) {
            if (this == BYTES) {
                return new JsonPrimitive(Base64.getEncoder().encodeToString((byte[]) value));
            }
            return GSON.toJsonTree(value, type);
        }

        Object fromJson(final JsonElement value) {
            if (this == BYTES) {
                return Base64.getDecoder().decode(value.getAsString());
            }
            return GSON.fromJson(value, type);
        }
    }

    /** A date, as the milliseconds since the epoch. */
    private static final class DateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

        @Override
        public JsonElement serialize(Date date, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(date.getTime());
        }

        @Override
        public Date deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
            return new Date(json.getAsLong());
        }
    }

    /** An argument of a command, whose value and default value may be of several kinds. */
    private static final class ArgumentAdapter
            implements JsonSerializer<ArgumentDescriptor>, JsonDeserializer<ArgumentDescriptor> {

        @Override
        public JsonElement serialize(ArgumentDescriptor argument, Type type, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("name", argument.getName());
            json.add("type", valueWithKind(argument.getType()));
            json.addProperty("defaultName", argument.getDefaultName());
            json.add("defaultType", valueWithKind(argument.getDefaultType()));
            return json;
        }

        @Override
        public ArgumentDescriptor deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
            JsonObject object = json.getAsJsonObject();
            ArgumentDescriptor argument = new ArgumentDescriptor(valueOfKind(object.get("defaultType")),
                    stringOrNull(object.get("defaultName")));
            argument.setType(valueOfKind(object.get("type")));
            argument.setName(stringOrNull(object.get("name")));
            return argument;
        }
    }

    /** The result of a command, whose output may be of several kinds. */
    private static final class ResultAdapter implements JsonSerializer<CommandResult>, JsonDeserializer<CommandResult> {

        @Override
        public JsonElement serialize(CommandResult result, Type type, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.add("commandDescriptor", context.serialize(result.getCommandDescriptor(), CommandDescriptor.class));
            json.add("executionTime", context.serialize(result.getExecutionTime(), Date.class));
            json.add("simulatorTime", context.serialize(result.getSimulatorTime(), Date.class));
            json.add("output", valueWithKind(result.getOutput()));
            json.addProperty("commandFailed", result.isCommandFailed());
            return json;
        }

        @Override
        public CommandResult deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
            JsonObject object = json.getAsJsonObject();
            CommandResult result = new CommandResult(
                    context.deserialize(object.get("commandDescriptor"), CommandDescriptor.class),
                    context.deserialize(object.get("executionTime"), Date.class),
                    context.deserialize(object.get("simulatorTime"), Date.class));
            result.setOutput(valueOfKind(object.get("output")));
            JsonElement failed = object.get("commandFailed");
            result.setCommandFailed(failed != null && failed.getAsBoolean());
            return result;
        }
    }

    private static String stringOrNull(final JsonElement element) {
        return (element == null || element.isJsonNull()) ? null : element.getAsString();
    }
}
