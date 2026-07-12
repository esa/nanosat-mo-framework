================================
Background & Reference Documents
================================

This section indexes the formal documents and background reading that accompany the NMF. The documents
themselves are PDFs in the repository; this page is the index.

.. note::

   These documents are historical reference. The Sphinx pages in
   :doc:`../reference/index` are the live, maintained reference.
   The PDFs are preserved for users who need the formal documents
   produced under ESA's ECSS-E-ST-40C software lifecycle.

.. toctree::
   :maxdepth: 1
   :caption: Specifications

   bootloader-specification
   savoir-gs-002-mapping

ESA standards
-------------

Located under ``docs/reference-documents/``:

- **SAVOIR Flight Computer Initialisation Sequence Generic Specification** (SAVOIR-GS-002, Issue 2 Rev 2) —
  ``04-savoir-gs-002_SAVOIR_Flight_Computer_Initialisation_Sequence_Generic_Specification.pdf``. The basis
  for the :doc:`bootloader-specification`; the tailoring is documented in :doc:`savoir-gs-002-mapping`.

ECSS deliverables
-----------------

Located under ``docs/reference-documents/NMF_Design_Documents/``:

- **NMF Software Requirements Specification (SRS)** — ``NMF_SRS.pdf``. No longer maintained.
- **NMF Software Design Document (SDD)** — ``NMF_SDD.pdf``. No longer maintained.

MO Service Specifications
-------------------------

The authoritative definitions of every MO service exposed by the NMF live in the MO XML files under
``core/mo-services-xml/``:

- ``area001-MAL.xml`` — Message Abstraction Layer.
- ``area051-COM.xml`` — Common Object Model (Event, Archive, ArchiveSync, Directory, Login, Configuration).
- ``area052-Monitor-and-Control.xml`` — MC services (Parameter, Action, Aggregation, Alert, Conversion).
- ``area053-Software-Management.xml`` — SM services (AppsLauncher, PackageManagement, Heartbeat,
  CommandExecutor).
- ``area054-Platform.xml`` — Platform services (Camera, GPS, AutonomousADCS, SoftwareDefinedRadio,
  OpticalDataReceiver, PowerControl, Clock, ArtificialIntelligence, FPGA).

The Java API JARs in ``core/mo-services-apis/`` are generated from these XML files; the XML is the single
source of truth. For a narrative overview of the service categories, see :doc:`../reference/services` and
:doc:`../development-app/platform-services`.

User-facing guides (historical)
-------------------------------

Located under ``docs/reference-documents/``:

- **NMF Quick Start Guide** — ``NMF_Quick_Start_Guide.pdf``. Superseded by :doc:`../quickstart/index`.
- **Development Guide — NMF Apps** — ``Development_Guide_NMF_Apps.pdf``. Superseded by
  :doc:`../development-app/index`.
- **Development Guide — NMF Ground Applications** — ``Development_Guide_NMF_Ground_applications.pdf``.
  Superseded by :doc:`../development-ground/index`.

Background reading
------------------

Located under ``docs/reference-documents/``:

- **PhD Dissertation (Cesar Coelho)** — ``Dissertation__Cesar_Coelho.pdf``. The foundational text describing
  the NMF's design and motivation.
- **Software Simulator MSc Thesis** — ``Software_Simulator_Master_Thesis.pdf``. The simulator's design.
- **Software Simulator User Manual** — ``Software_Simulator_Software_User_Manual.pdf``. Superseded by
  :doc:`../tooling/simulator`.

Presentations
-------------

Located under ``docs/reference-documents/``:

- **NMF SDK presentation** — ``NMF_SDK_presentation.pptx``. Overview slides.
