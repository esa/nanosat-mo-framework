===================================
Mission and Spacecraft Designation
===================================

.. contents:: Table of contents
   :local:

The NMF on-board filesystem contains a ``mission.properties`` file in the ``etc/`` directory. This file
describes the spacecraft environment where the software is running and it includes:

- **Mission Name** — The name of the mission.
- **Spacecraft Name** — The name of this individual spacecraft.
- **Spacecraft Node** — This spacecraft's unit number within the mission.
- **Spacecraft SCID** — The spacecraft's CCSDS Spacecraft Identifier (SCID), if registered.
- **Organization Abbreviation** — The operating organization's short acronym.
- **Organization Name** — The operating organization's full name.

Note that a mission may consist of one or more satellites (e.g. a single spacecraft, or a constellation, a
swarm, or a formation). The Spacecraft Node field allows missions to define different spacecraft nodes
in this case.
The file does not contain information about the other satellites in the fleet.

The file is generated automatically by the ``nmf-linux-maven-plugin`` project at build time.

Fields
------

The file is a standard Java ``.properties`` file (``key = value``, one per line). The fields split into two
groups: **mission-wide** values that are identical on every unit of the mission, and **per-spacecraft**
values that differ from unit to unit.

.. list-table::
   :header-rows: 1
   :widths: 26 12 12 50

   * - Property
     - Scope
     - Required
     - Description
   * - ``mission.name``
     - mission
     - yes
     - The mission the spacecraft belongs to. Shared across every unit of the mission (constellation, swarm,
       fleet, or a single satellite). There is no CCSDS registry for missions, so this name is chosen by the
       operator — in practice it is the common prefix of the spacecraft names (e.g. ``Phi-Sat-2``,
       ``Sentinel-1``, ``Galileo``).
   * - ``spacecraft.name``
     - spacecraft
     - yes
     - The name of this individual spacecraft. Where the spacecraft is registered in the SANA Spacecraft
       Identifiers registry, this is its registered name (``GSAT0201``, ``Novaradar-X94``). Unique within the
       mission.
   * - ``spacecraft.node``
     - spacecraft
     - yes
     - The operator's unit number for this spacecraft within the mission — typically derived from
       ``spacecraft.name``. It is an operator-assigned integer (often sparse and non-contiguous, not a
       normalised ``1…N`` index), intended for programmatic fleet handling. Unique within the mission. This
       is an NMF convention, not a CCSDS identifier.
   * - ``spacecraft.scid``
     - spacecraft
     - no
     - The spacecraft's CCSDS **SCID** (Spacecraft Identifier) as registered in SANA, written as hexadecimal
       verbatim (``0x032A``). Globally unique. Metadata only — it does not take part in MO addressing. Many
       missions have no SCID (see :ref:`mission-properties-ccsds`); omit the field when there is none.
   * - ``organization.abbreviation``
     - mission
     - yes
     - The operating organization's short acronym as registered in the SANA Organizations registry
       (``ESA``, ``Novaradar``, ``Linkstar``). This is the token that takes part in MO addressing, so it must
       be short and free of spaces.
   * - ``organization.name``
     - mission
     - recommended
     - The operating organization's full name as registered in SANA (``European Space Agency``,
       ``Novaradar Ltd``, ``Linkstar Corp.``). Human-readable metadata only.

.. _mission-properties-ccsds:

Grounding in CCSDS / SANA
-------------------------

The fields are aligned with the CCSDS SANA registries where a standard exists, and are NMF conventions where
none does:

- **Spacecraft** — the `SANA Spacecraft Identifiers registry
  <https://sanaregistry.org/r/spacecraftid/>`_ assigns each registered spacecraft a unique numeric **SCID**
  and records its name and operating organization. The SCID is per-spacecraft, not per-mission, and its
  values are not ordered or grouped, so it cannot be used to infer mission membership.
- **Organization** — the `SANA Organizations registry <https://sanaregistry.org/r/organizations/>`_ records
  both a full **Name** and a short **Abbreviation** for each organization, for space agencies and commercial
  operators alike. This is the source of the two ``organization.*`` fields.
- **Mission** — CCSDS has no mission or constellation identifier; the grouping exists only in the spacecraft
  naming convention. ``mission.name`` therefore has no SANA source and is chosen by the operator.
- **Node** — there is no CCSDS "unit number within a mission" concept. ``spacecraft.node`` is an NMF
  convention. (The DTN Bundle Protocol *Node Number* is a network-routing identifier at a different layer
  and is intentionally not reused here.)

Not every spacecraft has a SANA SCID. The SANA SCID space is small — 10-bit (1 024 values) for versions 1–3
and 16-bit (65 536 values) for version 4, shared across all missions — so large commercial constellations
are typically not registered and use their catalogue identifiers (NORAD / COSPAR) instead. ``spacecraft.scid``
is therefore optional: omit it when the spacecraft has none. Its absence does not affect MO addressing, which
never depends on the SCID.

.. _mission-properties-examples:

Examples
--------

**OPS-SAT (single spacecraft).** ESA's OPS-SAT was the first NMF mission — a single spacecraft, so the
mission has just one unit:

.. code-block:: properties

    mission.name              = OPS-SAT
    spacecraft.name           = OPS-SAT
    spacecraft.node           = 1
    spacecraft.scid           = 0x032A                            # from the SANA registry
    organization.abbreviation = ESA
    organization.name         = European Space Agency

**ɸ-Sat-2 (single spacecraft, no SANA SCID).** Another single-satellite NMF mission. Its link protocol is not
CCSDS-based, so the spacecraft is not registered in SANA and the ``spacecraft.scid`` field is omitted. For a
single-satellite mission the mission and spacecraft names may coincide and the node is simply ``1``; the ɸ is
written as ``Phi`` to keep the value ASCII:

.. code-block:: properties

    mission.name              = Phi-Sat-2
    spacecraft.name           = Phi-Sat-2
    spacecraft.node           = 1
    organization.abbreviation = ESA
    organization.name         = European Space Agency

**Constellation, space agency (Galileo).** SANA registers Galileo satellites under ESA; the per-unit SCID is
looked up in the registry. The value here is the CCSDS SCID, not the Galileo SV ID (the ``E##`` / PRN
broadcast in the navigation signal):

.. code-block:: properties

    mission.name              = Galileo
    spacecraft.name           = GSAT0201
    spacecraft.node           = 201
    spacecraft.scid           = 0xNNNN                            # from the SANA registry
    organization.abbreviation = ESA
    organization.name         = European Space Agency

**Mission within a wider programme (Sentinel-1).** The Copernicus programme groups several missions —
Sentinel-1, Sentinel-2, and so on — and each mission is itself a small constellation (Sentinel-1A,
Sentinel-1B, …). ``mission.name`` is the mission, not the programme, which has no field. The unit letter
becomes an integer ``spacecraft.node`` (Sentinel-1A is ``1``, Sentinel-1B is ``2``):

.. code-block:: properties

    mission.name              = Sentinel-1
    spacecraft.name           = Sentinel-1B
    spacecraft.node           = 2
    spacecraft.scid           = 0xNNNN                            # from the SANA registry
    organization.abbreviation = ESA
    organization.name         = European Space Agency

**Constellation, commercial operator.** A commercial constellation whose units carry per-unit SANA SCIDs
(the names below are fictitious):

.. code-block:: properties

    mission.name              = Novaradar
    spacecraft.name           = Novaradar-X94
    spacecraft.node           = 94
    spacecraft.scid           = 0x0140
    organization.abbreviation = Novaradar
    organization.name         = Novaradar Ltd

**Mega-constellation, no SANA SCID.** A mission example that does not register a CCSDS SCID for each unit, so the
``spacecraft.scid`` field is omitted; the operator's catalogue number is used as ``spacecraft.node`` (the
names below are fictitious):

.. code-block:: properties

    mission.name              = Linkstar
    spacecraft.name           = Linkstar-30000
    spacecraft.node           = 30000
    organization.abbreviation = Linkstar
    organization.name         = Linkstar Corp.
