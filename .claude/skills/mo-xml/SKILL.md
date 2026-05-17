---
name: mo-xml
description: Write and edit CCSDS MO service XML specification files following the established conventions of the mo-services-java project (ServiceSchema-v003). Use whenever creating or modifying files under core/mo-services-xml/.
---

# MO XML Service Specification — Authoring Conventions

## Authoritative sources

**Schema (structural ground truth):**
`https://github.com/esa/mo-services-java/blob/v14.0/xml-service-specifications/xml-ccsds-mo-standards/src/main/resources/xsd/ServiceSchema-v003.xsd`

Read this file whenever you are uncertain about which elements or attributes are allowed, whether something is required or optional, or what the valid content model is. The XSD enforces uniqueness constraints (area names/numbers, type names) and defines the exact attribute sets for every element type. It is more reliable than any convention listed below.

**Style reference (naming, wording, ordering conventions):**
`area009-v001-Mission-Product-Distribution.xml` from mo-services-java v14.0. The conventions below are derived from it.

---

## 1. File Header

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright European Space Agency, <YEAR>
-->
<mal:specification
    xmlns:mal = "http://www.ccsds.org/schema/ServiceSchema-v003"
    xmlns:svg = "http://www.w3.org/2000/svg"
    xmlns:xsi = "http://www.w3.org/2001/XMLSchema-instance" comment="">
```

- Replace `<YEAR>` with the current year at the time of file creation.
- Schema namespace is always `ServiceSchema-v003`.
- Note the spaces around `=` in the namespace declarations — keep them.
- The `comment=""` attribute on `<mal:specification>` is always present (empty string).

---

## 2. Area

```xml
<mal:area name="MPD" number="9" version="1" comment="Mission Operations services - Mission Product Distribution">
```

- `name`: short uppercase abbreviation or CamelCase.
- `number`: unique area number (check existing areas to avoid collision).
- `version`: integer, starts at 1.
- `comment`: format `"Mission Operations services - <Full Area Name>"`.

---

## 3. Services

```xml
<mal:service number="1" name="ProductRetrieval" comment="The Product Retrieval Service allows browsing and retrieval of existing products.">
```

- `number`: sequential integers starting at 1 within the area.
- `name`: PascalCase.
- `comment`: full sentence, starts with `"The <ServiceName> Service ..."`, ends with `.`.

### Service-level documentation

```xml
<mal:documentation name="Service-level Requirements" order="1">
    The Product Retrieval service provider may provide the capability to...
</mal:documentation>
```

- Label is `"Service-level Requirements"` (plural).
- `order` is sequential from 1.
- Text content on a new indented line. Notes are inline: `Note: ...` — no separate element.

---

## 4. Capability Sets

```xml
<mal:capabilitySet number="1" comment="">
```

- `number`: sequential from 1 within the service.
- `comment`: typically `""`. Only set it if there is a meaningful label.
- Each capability set groups related operations. One operation per capability set is the norm; only closely coupled operations share a set (e.g. submit + cancel).

---

## 5. Operations (Interaction Patterns)

### Element names by pattern

| Pattern | Element |
|---|---|
| SEND | `<mal:sendIP>` |
| SUBMIT | `<mal:submitIP>` |
| REQUEST | `<mal:requestIP>` |
| INVOKE | `<mal:invokeIP>` |
| PROGRESS | `<mal:progressIP>` |
| PUBSUB | `<mal:pubsubIP>` |

### Attributes

```xml
<mal:requestIP number="1" name="listProducts" comment="The listProducts operation lists the available products for a selected product filter.">
```

- `number`: sequential across **all** operations in the service (not reset per capability set).
- `name`: lowerCamelCase.
- `comment`: full sentence, starts with `"The <operationName> operation ..."`, ends with `.`.

### Operation documentation

```xml
<mal:documentation name="Requirement" order="1">
    A consumer shall specify the productFilter, and optionally, the time windows...
</mal:documentation>
```

- Label is `"Requirement"` (singular) for operation-level requirements.
- Use `"shall"` for normative requirements, `"should"` for recommendations, `"may"` for permissions.
- State the subject explicitly: `"A consumer shall..."` or `"The service provider shall..."`.
- Notes inline at end of paragraph can be used, but seldomly, only when it is really needed: `Note: ...`

---

## 6. Messages

### REQUEST

```xml
<mal:messages>
    <mal:request>
        <mal:field .../>
    </mal:request>
    <mal:response>
        <mal:field .../>
    </mal:response>
</mal:messages>
```

### SUBMIT

```xml
<mal:messages>
    <mal:submit>
        <mal:field .../>
    </mal:submit>
</mal:messages>
```

### INVOKE

```xml
<mal:messages>
    <mal:invoke>
        <mal:field .../>
    </mal:invoke>
    <mal:acknowledgement>
    </mal:acknowledgement>
    <mal:response>
        <mal:field .../>
    </mal:response>
</mal:messages>
```

### PROGRESS

```xml
<mal:messages>
    <mal:progress>
        <mal:field .../>
    </mal:progress>
    <mal:acknowledgement>
    </mal:acknowledgement>
    <mal:update>
        <mal:field .../>
    </mal:update>
    <mal:response>
    </mal:response>
</mal:messages>
```

### PUBSUB

```xml
<mal:messages>
    <mal:subscriptionKeys>
        <mal:field .../>
    </mal:subscriptionKeys>
    <mal:publishNotify>
        <mal:field .../>
    </mal:publishNotify>
</mal:messages>
```

**Empty stages** (acknowledgement, response with no body) use an open/close tag pair with only whitespace content — **not** a self-closing tag:
```xml
<mal:acknowledgement>
</mal:acknowledgement>
```

---

## 7. Fields

```xml
<mal:field name="productFilter" canBeNull="false" comment="The product filter used to refine the selection of products.">
    <mal:type area="MPD" list="false" name="ProductFilter"/>
</mal:field>
```

- `name`: lowerCamelCase.
- `canBeNull`: `"true"` or `"false"`.
- `comment`: full description ending with `.`. Describe the field directly — do not use filler verbs like "holds", "contains", "stores", or "represents". Wrong: `"The names field holds the list of names."` Right: `"The list of names."`
- When `canBeNull="true"`, the operation documentation must state what NULL means.
- `<mal:type>` is a self-closing child element.

### Type references

| Usage | Example |
|---|---|
| Own-area type | `area="MPD" list="false" name="ProductFilter"` |
| MAL primitive | `area="MAL" list="false" name="String"` |
| List of own type | `area="MPD" list="true" name="StandingOrder"` |
| ~~Typed object reference~~ | ~~`area="MPD" list="true" name="ObjectRef(Product)"`~~ — **deprecated, do not use in new files** |
| Abstract/any element | `area="MAL" list="false" name="Element"` |

MAL primitive types: `Boolean`, `Integer`, `Long`, `String`, `Identifier`, `URI`, `Time`, `Duration`, `Blob`, `Attribute`, `Element`, `Composite`. ~~`Object`~~ — **deprecated, do not use in new files**.

---

## 8. Errors

### Area-level error definitions (end of `<mal:area>`, after `<mal:dataTypes>`)

```xml
<mal:errors>
    <mal:error number="1" name="Invalid" comment="A field in the message contains an invalid value. If there are multiple errors, the first invalid field is reported."/>
    <mal:error number="2" name="Delivery Failed" comment="An attempt to deliver a product file to the nominated address failed."/>
    <mal:error number="3" name="Unknown" comment="The referenced item does not exist."/>
</mal:errors>
```

- `name`: Title Case, may contain spaces (e.g. `"Delivery Failed"`, `"Too Many"`).
- `number`: sequential from 1 within the area.
- `comment`: full sentence ending with `.`.

### Error references in operations

```xml
<mal:errors>
    <mal:errorRef comment="When a field in the message contains an invalid value.">
        <mal:type name="Invalid" area="MPD"/>
        <mal:extraInformation comment="A textual description with the reason for invalidity.">
            <mal:type area="MAL" name="String" list="false"/>
        </mal:extraInformation>
    </mal:errorRef>
</mal:errors>
```

- `<mal:errors>` block comes **after** `<mal:messages>` inside the operation element.
- `errorRef comment`: starts with `"When ..."`.
- `<mal:extraInformation>` is present whenever the error carries extra data. Its `comment` describes what the extra data contains.
- The type inside `<mal:extraInformation>` is typically `MAL::String` (free text), `MAL::Integer` list (indexes), or `MAL::Long` (ID).

---

## 9. Data Types

The `<mal:dataTypes>` block appears **before** `<mal:errors>` at the end of `<mal:area>`. Order within it: concrete composites (by `shortFormPart`), abstract composites before their subtypes, then enumerations.

### Concrete composite

```xml
<mal:composite name="TimeWindow" shortFormPart="7" comment="The TimeWindow represents a specific period, between the start and end of a time window.">
    <mal:extends>
        <mal:type area="MAL" list="false" name="Composite"/>
    </mal:extends>
    <mal:field name="start" canBeNull="false" comment="The start time of the time window.">
        <mal:type area="MAL" list="false" name="Time"/>
    </mal:field>
    <mal:field name="end" canBeNull="false" comment="The end time of the time window.">
        <mal:type area="MAL" list="false" name="Time"/>
    </mal:field>
</mal:composite>
```

- `name`: PascalCase.
- `shortFormPart`: sequential integer within the area's data types, starting at 1. **Never reuse or skip a number.**
- `comment`: full sentence, usually starts with `"A/The <TypeName> ..."`, with a preference for `"The <TypeName> ..."`.
- `<mal:extends>` is always present. All types extend `MAL::Composite`. ~~`MAL::Object`~~ is deprecated — do not use it.

### Abstract composite (no `shortFormPart`)

```xml
<mal:composite name="AttributeFilter" comment="An AttributeFilter is an abstract data structure that enables...">
    <mal:extends>
        <mal:type area="MAL" list="false" name="Composite"/>
    </mal:extends>
    <mal:field .../>
</mal:composite>
```

- **Abstract composites have no `shortFormPart` attribute.** Omit it entirely.
- Concrete subtypes reference them in `<mal:extends>`:
  ```xml
  <mal:extends>
      <mal:type area="MPD" list="false" name="AttributeFilter"/>
  </mal:extends>
  ```

### Enumeration

```xml
<mal:enumeration name="DeliveryMethodEnum" shortFormPart="12" comment="The DeliveryMethodEnum enumeration defines the delivery method to be used for delivery of mission data products.">
    <mal:item nvalue="1" value="SERVICE_COMPLETE" comment="The mission data product is to be delivered via service messages with both the metadata and the product body."/>
    <mal:item nvalue="2" value="SERVICE_JUST_METADATA" comment="..."/>
    <mal:item nvalue="3" value="FILETRANSFER" comment="..."/>
</mal:enumeration>
```

- Enum type names end with `Enum` (e.g. `DeliveryMethodEnum`).
- `value`: UPPER_SNAKE_CASE.
- `nvalue`: sequential from 1.
- `comment` on each item ends with `.`.

---

## 10. Naming Summary

| Element | Convention | Example |
|---|---|---|
| Area | Short CAPS or PascalCase | `MPD`, `COM`, `MC` |
| Service | PascalCase | `ProductRetrieval` |
| Operation | lowerCamelCase | `listProducts`, `cancelStandingOrder` |
| Composite type | PascalCase | `ProductMetadata`, `TimeWindow` |
| Enumeration type | PascalCase + `Enum` suffix | `DeliveryMethodEnum` |
| Field | lowerCamelCase | `productFilter`, `deliverTo` |
| Enum item value | UPPER_SNAKE_CASE | `SERVICE_COMPLETE`, `FILETRANSFER` |
| Error name | Title Case (spaces allowed) | `Invalid`, `Delivery Failed`, `Too Many` |

---

## 11. Indentation and Formatting

- **1 tab** per nesting level.
- Namespace declarations in the root element are indented 1 tab and have spaces around `=`.
- Attribute ordering on elements: `name` first, then `number`/`shortFormPart`, then remaining attributes, `comment` last.
- Multi-sentence `comment` values are allowed on a single line within the attribute.
- Text content of `<mal:documentation>` is on a new line, indented one level deeper than the tag.

---

## 12. Document-level Structure (top to bottom)

```
<?xml ...?>
<!-- copyright comment -->
<mal:specification ...>
  <mal:area ...>
    <mal:service ...>            (repeat per service, numbered from 1)
      <mal:documentation .../>   (service-level requirements)
      <mal:capabilitySet ...>    (repeat per capability set, numbered from 1)
        <mal:XxxIP ...>          (operation)
          <mal:documentation .../> (operation requirements)
          <mal:messages>
            ...
          </mal:messages>
          <mal:errors>           (only if operation has errors)
            ...
          </mal:errors>
        </mal:XxxIP>
      </mal:capabilitySet>
    </mal:service>
    <mal:dataTypes>              (all composites and enumerations)
      ...
    </mal:dataTypes>
    <mal:errors>                 (area-level error definitions)
      ...
    </mal:errors>
  </mal:area>
</mal:specification>
```

---

## 13. NMF Project-specific Notes

The files in `core/mo-services-xml/` use an older schema (`ServiceSchema` without version suffix) and the `com:ExtendedServiceType` extension for COM-area services. When editing those files, match the existing style in that file rather than the MPD reference above. The conventions for naming, numbering, documentation wording, and data types are the same.
