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

import com.google.gson.JsonObject;
import java.util.List;
import org.ccsds.moims.mo.mal.TypeId;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperAttributes;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.IntegerList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.StringList;

/**
 * Converts between JSON (as exchanged with MCP clients) and MAL data types,
 * driven by the {@link TypeId} taken from each operation field. Reuses the
 * existing {@link HelperAttributes} / {@link Attribute} machinery rather than
 * duplicating type tables.
 *
 * <p>
 * The v0 scope covers MAL attribute scalars and lists of them. Abstract,
 * Composite and Enumeration types are rejected so the caller can skip the tool.
 */
public final class MoJsonConverter {

    private MoJsonConverter() {
    }

    /**
     * Thrown when a field type is not (yet) supported by the converter.
     */
    public static final class UnsupportedTypeException extends RuntimeException {

        public UnsupportedTypeException(String message) {
            super(message);
        }
    }

    /**
     * Builds the JSON Schema type fragment for a field, e.g. {@code {"type":"integer"}}
     * or {@code {"type":"array","items":{"type":"string"}}}.
     *
     * @param typeId The absolute type short form of the field. Must not be null.
     * @return The JSON Schema fragment describing the field type.
     * @throws UnsupportedTypeException If the type is not a MAL attribute or a
     * list of MAL attributes.
     */
    public static JsonObject schemaType(Long typeId) {
        if (typeId == null) {
            throw new UnsupportedTypeException("Abstract field type is not supported");
        }
        TypeId id = new TypeId(typeId);
        if (id.isList()) {
            int elementSfp = id.generateTypeIdPositive().getSFP();
            JsonObject schema = new JsonObject();
            schema.addProperty("type", "array");
            schema.add("items", scalarSchema(elementSfp));
            return schema;
        }
        return scalarSchema(id.getSFP());
    }

    private static JsonObject scalarSchema(int sfp) {
        JsonObject schema = new JsonObject();
        schema.addProperty("type", jsonScalarType(sfp));
        return schema;
    }

    private static String jsonScalarType(int sfp) {
        switch (sfp) {
            case Attribute._BOOLEAN_TYPE_SHORT_FORM:
                return "boolean";
            case Attribute._FLOAT_TYPE_SHORT_FORM:
            case Attribute._DOUBLE_TYPE_SHORT_FORM:
                return "number";
            case Attribute._OCTET_TYPE_SHORT_FORM:
            case Attribute._SHORT_TYPE_SHORT_FORM:
            case Attribute._USHORT_TYPE_SHORT_FORM:
            case Attribute._INTEGER_TYPE_SHORT_FORM:
            case Attribute._UINTEGER_TYPE_SHORT_FORM:
            case Attribute._LONG_TYPE_SHORT_FORM:
            case Attribute._ULONG_TYPE_SHORT_FORM:
                return "integer";
            case Attribute._BLOB_TYPE_SHORT_FORM:
            case Attribute._DURATION_TYPE_SHORT_FORM:
            case Attribute._IDENTIFIER_TYPE_SHORT_FORM:
            case Attribute._STRING_TYPE_SHORT_FORM:
            case Attribute._TIME_TYPE_SHORT_FORM:
            case Attribute._FINETIME_TYPE_SHORT_FORM:
            case Attribute._URI_TYPE_SHORT_FORM:
                return "string";
            default:
                throw new UnsupportedTypeException("Unsupported attribute short form: " + sfp);
        }
    }

    /**
     * Converts a JSON argument value into the MAL element expected for the
     * field, ready to be passed in a MAL message body.
     *
     * @param typeId The absolute type short form of the field. Must not be null.
     * @param value The JSON value (scalar, or a list for array types).
     * @return The MAL element (a MAL attribute, or a typed {@link ElementList}).
     * @throws UnsupportedTypeException If the type is not supported.
     */
    public static Object toMalElement(Long typeId, Object value) {
        if (typeId == null) {
            throw new UnsupportedTypeException("Abstract field type is not supported");
        }
        TypeId id = new TypeId(typeId);
        if (id.isList()) {
            int elementSfp = id.generateTypeIdPositive().getSFP();
            return toMalList(elementSfp, (List<?>) value);
        }
        return toMalScalar(id.getSFP(), value);
    }

    /**
     * Builds an empty MAL element to be used as a decoding template when reading
     * a response body. Supported for lists of MAL attributes (v0 scope).
     *
     * @param typeId The absolute type short form of the field. Must not be null.
     * @return An empty MAL element template.
     * @throws UnsupportedTypeException If the type is not supported.
     */
    public static Object emptyElement(Long typeId) {
        if (typeId == null) {
            throw new UnsupportedTypeException("Abstract field type is not supported");
        }
        TypeId id = new TypeId(typeId);
        if (id.isList()) {
            return toMalList(id.generateTypeIdPositive().getSFP(), null);
        }
        String name = HelperAttributes.typeShortForm2attributeName(id.getSFP());
        return HelperAttributes.attributeName2object(name);
    }

    private static Object toMalScalar(int sfp, Object value) {
        if (value == null) {
            return null;
        }
        String name = HelperAttributes.typeShortForm2attributeName(sfp);
        Object instance = HelperAttributes.attributeName2object(name);
        return HelperAttributes.string2attribute(instance, String.valueOf(value));
    }

    private static ElementList<?> toMalList(int elementSfp, List<?> values) {
        switch (elementSfp) {
            case Attribute._LONG_TYPE_SHORT_FORM: {
                LongList list = new LongList();
                if (values != null) {
                    for (Object v : values) {
                        list.add(((Number) v).longValue());
                    }
                }
                return list;
            }
            case Attribute._INTEGER_TYPE_SHORT_FORM: {
                IntegerList list = new IntegerList();
                if (values != null) {
                    for (Object v : values) {
                        list.add(((Number) v).intValue());
                    }
                }
                return list;
            }
            case Attribute._BOOLEAN_TYPE_SHORT_FORM: {
                BooleanList list = new BooleanList();
                if (values != null) {
                    for (Object v : values) {
                        list.add((Boolean) v);
                    }
                }
                return list;
            }
            case Attribute._STRING_TYPE_SHORT_FORM: {
                StringList list = new StringList();
                if (values != null) {
                    for (Object v : values) {
                        list.add(String.valueOf(v));
                    }
                }
                return list;
            }
            case Attribute._IDENTIFIER_TYPE_SHORT_FORM: {
                IdentifierList list = new IdentifierList();
                if (values != null) {
                    for (Object v : values) {
                        list.add(new Identifier(String.valueOf(v)));
                    }
                }
                return list;
            }
            default:
                throw new UnsupportedTypeException(
                        "Unsupported list element short form: " + elementSfp);
        }
    }
}
