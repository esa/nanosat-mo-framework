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
<mal:service name="ProductRetrieval" number="1" comment="The Product Retrieval Service allows browsing and retrieval of existing products.">
```

- `number`: sequential integers starting at 1 within the area.
- `name`: PascalCase.
- `comment`: full sentence, starts with `"The <ServiceName> Service ..."`, ends with `.`.

### Area-level documentation

```xml
<mal:documentation name="Area-level Requirement" order="1">
    The instance identifier of an object shall not use the value of '0'...
</mal:documentation>
```

- Label is `"Area-level Requirement"`.
- `order` is sequential from 1.
- Text content on a new indented line.

### Service-level documentation

```xml
<mal:documentation name="Service-level Requirement" order="1">
    The Product Retrieval service provider may provide the capability to...
</mal:documentation>
```

- Label is `"Service-level Requirement"` (singular).
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
<mal:requestIP name="listProducts" number="1" comment="The listProducts operation lists the available products for a selected product filter.">
```

- `number`: sequential across **all** operations in the service (not reset per capability set).
- `name`: lowerCamelCase.
- `comment`: full sentence, starts with `"The <operationName> operation ..."`, ends with `.`.
- `supportInReplay`: **removed in MALv3 — never add this attribute.** Remove it whenever encountered in existing files.

### Operation documentation

```xml
<mal:documentation name="Requirement" order="1">
    A consumer shall specify the productFilter, and optionally, the time windows...
</mal:documentation>
```

- Label is `"Requirement"` (singular) for operation-level requirements.

#### Wording rules for `<mal:documentation>` requirement text

These rules apply to the text content of every `<mal:documentation>` element, at any level (area, service, operation). They do **not** apply to `comment` attributes on fields, composites, or operations — those follow the rules in their respective sections.

**Modal verbs** — per CCSDS conventions:
| Verb(s) | Meaning |
|---|---|
| `shall`, `must` | binding and verifiable normative requirement |
| `should` | optional but desirable |
| `may` | optional |
| `is`, `are`, `will` | statements of fact (informative, not normative) |

Prefer `shall` over `must` for consistency — the MPD reference file uses `shall` exclusively, and mixing both in the same document reduces readability. Reserve `must` only when it reads significantly more naturally in context.

**State the subject when it is not already clear from context.** The MAL book unambiguously defines which party sends which message in every interaction pattern, so using the message stage as subject is acceptable and often reads more naturally — *"The RESPONSE message shall contain..."* is unambiguously a provider obligation by definition. An explicit subject (`"A consumer shall..."`, `"The service provider shall..."`) is needed only when the responsible party cannot be inferred from the interaction pattern alone — for example, in service-level or area-level requirements that are not tied to a specific message stage.

**Active voice.** The subject must be the agent, not the thing being acted on:
- Wrong: `"An INVALID error shall be returned if the domain contains a wildcard."`
- Right: `"The service provider shall return an INVALID error if the domain contains a wildcard."`

This is consistent with the "message as subject" rule above: a message stage (RESPONSE, UPDATE, etc.) is an acceptable subject because the MAL interaction pattern unambiguously identifies the provider as the sender — it is not passive voice, it is a shorthand for the provider obligation.

**PUBSUB operations** follow a different pattern because the consumer is never obligated to subscribe in a particular way — subscribing is always optional. This changes which modal verb applies to each side:

- **Provider-side requirements** use `shall`: *"Upon [condition], the service provider shall publish..."*
- **Consumer-side requirements** use `may`: *"A service consumer may subscribe to [X] for a specific [field], or for all [X] with the use of the wildcard in the [field] field."*

In all other interaction patterns (REQUEST, INVOKE, PROGRESS, SUBMIT) consumer-side requirements use `shall`. In PUBSUB they always use `may`.

**Conditional requirements use `"If [condition], then [subject shall/should/may action]."`.** Always include the comma before `then`, and always state the subject after `then`:
```
If the creationDate is NULL, then the service provider shall not filter the products based on the creationDate.
```
Never drop `then` (`"If X, the provider shall..."`) and never omit the comma (`"If X then ..."`).

When both branches need to be specified, write two separate requirements with mirrored conditions — do not use `, else`:
```
If the domain is NULL, then the service provider shall not filter the standing orders based on the domain.
If the domain is not NULL, then the service provider shall compare the domain against the domain of the product filter.
```
This keeps each requirement independently testable and traceable.

**One requirement per element.** Each `<mal:documentation>` shall express exactly one normative statement. Signals that you have packed multiple requirements together:
- More than one `shall`/`should`/`may` in the same sentence (unless the second is a consequence of the first condition).
- Two independent sentences in the same element.
- An "and" joining two obligations.

Wrong — two requirements in one element:
```xml
<mal:documentation name="Requirement" order="6">
    The archiveQuery and queryFilter lists must be the same size unless queryFilter is NULL, otherwise an INVALID error shall be raised.
</mal:documentation>
```
Right — split into two:
```xml
<mal:documentation name="Requirement" order="6">
    If the queryFilter list is not NULL, then a consumer shall supply the same number of entries in the archiveQuery and queryFilter lists.
</mal:documentation>
<mal:documentation name="Requirement" order="7">
    If the queryFilter list is not NULL and the sizes of archiveQuery and queryFilter differ, then the service provider shall return an INVALID error.
</mal:documentation>
```

**Testable and verifiable.** Write so that a yes/no test can be derived directly from the requirement. Avoid vague words that cannot be objectively evaluated: `appropriate`, `reasonable`, `sufficient`, `properly`, `efficiently`, `in a timely manner`. Ask: "Can I write a test that definitively passes or fails this?" If not, rewrite until you can.

**Requirements shall not duplicate field descriptions.** If a requirement does nothing more than describe what a field represents, delete it — that belongs in the `comment` attribute of the `<mal:field>` element. Requirements shall only state behaviour, constraints, or consequences that cannot be expressed in a field comment.

**Requirements shall not duplicate error trigger conditions.** The `comment` attribute on `<mal:errorRef>` is itself normative — it states when the error is raised. A requirement that only restates that same trigger condition adds nothing and shall be deleted. If a requirement provides _more specific detail_ than the errorRef comment (e.g. naming the exact wildcard value), fold that detail into the errorRef comment instead of keeping it as a separate requirement.

Whenever a requirement mentions an error by name (UNKNOWN, INVALID, DUPLICATE, etc.), immediately look up the corresponding `<mal:errorRef>` in the same operation and compare. If the condition in the requirement is already covered by the errorRef comment, delete the requirement — do not just fix its formatting.

**`NULL`, `TRUE`, and `FALSE` are always capitalised** in requirement text and `comment` attributes — they are type values, not plain English words. This does not apply to XML attribute values such as `canBeNull="false"`, which are XML boolean literals and stay lowercase.

**Interaction pattern message stage names are always capitalised** when referenced in requirement text or `comment` attributes — they are protocol-level terms, not plain English words:

| Stage | Written as |
|---|---|
| Send | SEND message |
| Submit | SUBMIT message |
| Request / Response | REQUEST message, RESPONSE message |
| Invoke / Acknowledgement / Response | INVOKE message, ACKNOWLEDGEMENT message, RESPONSE message |
| Progress / Acknowledgement / Update / Response | PROGRESS message, ACKNOWLEDGEMENT message, UPDATE message, RESPONSE message |
| Publish-Subscribe | PUBLISH message, NOTIFY message |

**Unambiguous.** Every requirement must have exactly one valid interpretation:
- Name things explicitly — avoid `it`, `this`, `the above` when the antecedent is unclear.
- When a requirement refers to a message field, use the field name directly rather than a prose description: `the objInstIds field` not `the object instance identifier list`.
- Avoid vague quantifiers: `several`, `some`, `many`, `few`.
- Avoid undefined relative terms: `quickly`, `large`, `small`.

**Notes** inline at end of paragraph, used sparingly: `Note: ...` — no separate element.

**HTML lists are permitted** in `<mal:documentation>` text when enumerating multiple capabilities or items. Use `<ol>`/`<li>` for ordered lists. Do not reformat these into prose or split them into separate elements:
```xml
<mal:documentation name="Service-level Requirement" order="1">The action service shall provide:
&lt;ol&gt;
  &lt;li&gt;the capability for monitoring the execution progress of actions;&lt;/li&gt;
  &lt;li&gt;the capability for submitting actions for execution;&lt;/li&gt;
&lt;/ol&gt;
</mal:documentation>
```

---

#### Review and correction process

When reviewing or fixing requirements:

1. **Check every rule against every element.** A single `<mal:documentation>` can violate multiple rules simultaneously — finding one violation does not mean the element is otherwise clean. After fixing one issue, re-read the same element against all other wording rules before moving on.

2. **Do exactly one final verification pass after all changes are applied.** For every modified `<mal:documentation>` element: re-read it against all wording rules, AND cross-check it against the `<mal:errorRef>` elements in the same operation to catch duplicate error trigger conditions. This pass is a single read-through — do not start another fix cycle from it. Report any remaining issues instead.

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
- `canBeNull`: `"true"` or `"false"`. Always present — even when `"true"` (the XSD default) — to be explicit.
- `comment`: full description ending with `.`. Describe the field directly — do not use filler verbs like "holds", "contains", "stores", or "represents". Wrong: `"The names field holds the list of names."` Right: `"The list of names."`
- When `canBeNull="true"`, the operation documentation shall state what NULL means.
- `<mal:type>` is a self-closing child element.

### Type references

Attribute ordering on `<mal:type>` is always `area`, `list`, `name`.

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
- **Errors are defined at area level only** — `ServiceSchema-v003` removed the ability to define errors at service level or inline within operations. Operations may only reference area-level errors via `<mal:errorRef>`.
- **When editing older NMF XML files**: service-level `<mal:errors>` blocks may exist (legacy of the old schema). Move any such error definitions up to the area-level `<mal:errors>` block and replace the original with `<mal:errorRef>` references.

### Error references in operations

```xml
<mal:errors>
    <mal:errorRef comment="When a field in the message contains an invalid value.">
        <mal:type area="MPD" list="false" name="Invalid"/>
        <mal:extraInformation comment="A textual description with the reason for invalidity.">
            <mal:type area="MAL" list="false" name="String"/>
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

The `<mal:dataTypes>` block appears **before** `<mal:errors>` at the end of `<mal:area>`. Order within it: all concrete composites that have no abstract parent (sorted by `shortFormPart`), then abstract composites immediately followed by their concrete subtypes, then enumerations.

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
- Exception: `<mal:item>` has no `name` — ordering is `nvalue`, `value`, `comment`.
- Exception: `<mal:error>` — ordering is `number`, `name`, `comment`.
- Multi-sentence `comment` values are allowed on a single line within the attribute.
- Text content of `<mal:documentation>` is on a new line, indented one level deeper than the tag.

---

## 12. Document-level Structure (top to bottom)

```
<?xml ...?>
<!-- copyright comment -->
<mal:specification ...>
  <mal:area ...>
    <mal:documentation .../>     (area-level requirements, label "Area-level Requirement")
    <mal:service ...>            (repeat per service, numbered from 1)
      <mal:documentation .../>   (service-level requirements, label "Service-level Requirement")
      <mal:capabilitySet ...>    (repeat per capability set, numbered from 1)
        <mal:XxxIP ...>          (operation)
          <mal:documentation .../> (operation requirements, label "Requirement")
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

The files in `core/mo-services-xml/` differ from the MPD reference in the following ways — match the existing file style when editing them:

- **Schema namespace**: currently `http://www.ccsds.org/schema/ServiceSchema` (no version suffix) — this is a known gap, pending upgrade to `ServiceSchema-v003`. Do not change the namespace until the XSD and `COMSchema.xsd` are also updated.
- **COM-area services**: carry `xsi:type="com:ExtendedServiceType"` on `<mal:service>` and require the `xmlns:com="http://www.ccsds.org/schema/COMSchema"` namespace declaration.
- **Area-level error numbers**: use sequential integers from 1, same as any other area. The NMF COM file historically used large values (`70000`, `70001`) — this was a design mistake, now corrected.

All other conventions (naming, numbering, documentation wording, data types, attribute ordering) are identical to the MPD reference.
