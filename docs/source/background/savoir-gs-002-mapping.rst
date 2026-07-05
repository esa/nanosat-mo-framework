=================================
SAVOIR-GS-002 Mapping to the NMF
=================================

This document maps the **SAVOIR Flight Computer Initialisation Sequence Generic Specification**
(SAVOIR-GS-002, Issue 2 Rev 2, 18/11/2021) to the NMF Bootloader specified in
:doc:`bootloader-specification`. The SAVOIR document is archived in this repository at
``docs/reference-documents/04-savoir-gs-002_SAVOIR_Flight_Computer_Initialisation_Sequence_Generic_Specification.pdf``.

An important scoping note: on a Linux payload computer, SAVOIR-GS-002 applies literally at the *platform*
layer, where the Boot SW is the platform bootloader (e.g. U-Boot) and the ASW image is the
operating-system image. That layer is mission scope, out of the NMF's hands. The NMF Bootloader applies
the SAVOIR pattern one layer up, at the application software layer: in strict SAVOIR terms it is not the
Boot SW (see IMP.460 in the compliance matrix) but the first component of the ASW, reproducing the Boot
SW's functions for the NMF's own updatable unit, the software baseline. The mapping below is therefore an
analogy, expressed within that application-layer reading; see the "Boot chain and layering" section of the
:doc:`bootloader-specification`.

The mapping follows the tailoring method defined in section 9 of the SAVOIR document: requirements whose
hardware *Assumptions* are not featured by the NMF execution environment are marked Not Applicable with a
justification, and the project-specific *Parameters* are fixed by the
:doc:`bootloader-specification` (Parameters section). The ``NMF.BOOT.*`` requirement identifiers referenced
throughout this document are defined there.

Terminology mapping
-------------------

The SAVOIR document targets software running close to the metal (SPARC OBCs, PROM boot memory). The NMF
Supervisor runs as a JVM process on Linux. The table below maps the SAVOIR terms to their NMF equivalents,
within the application-layer reading described above.

.. list-table::
   :header-rows: 1
   :widths: 30 70

   * - SAVOIR term
     - NMF equivalent
   * - Boot SW
     - NMF Bootloader (POSIX shell script, single entry point for starting the Supervisor). A functional
       analogue, not the Boot SW itself: strictly, the bootloader is the first component of the ASW (see
       IMP.460)
   * - Application SW (ASW)
     - NanoSat MO Supervisor (JVM process)
   * - ASW Image
     - Software baseline: the combination of a framework version, a mission version and a Java runtime
       (``jars-nmf/<version>/``, ``jars-mission/<version>/``, ``java/<version>/`` or the system Java).
       Analogous concept one layer up: the literal ASW image at the platform layer is the
       operating-system image.
   * - Application storage memory
     - Filesystem
   * - Working memory
     - JVM process memory (no explicit copy step exists; the JVM loads classes itself)
   * - Boot memory
     - The bootloader installation location, outside the versioned baseline directories
   * - Boot Report
     - Boot report file written step-by-step by the bootloader at each start
   * - Reconfiguration function
     - The bootloader's own fallback logic (software; see Deviations)
   * - Watchdog kick
     - Boot confirmation marker written by the Supervisor once its services are up
   * - Cold restart
     - Operating system boot
   * - Warm restart
     - Supervisor restart without an OS reboot
   * - Safeguard Memory (SGM)
     - Persistent state directory on disk
   * - Configuration data set by Ground (HPC-1)
     - The baseline manifest file, updated automatically by the Package Management service on upgrade and
       commandable directly through the Parameter service
   * - Standby mode
     - Not applicable as a boot mode; in-flight SW maintenance is provided at ASW level by the
       Package Management service
   * - Monitor mode
     - Not applicable as a boot mode; on-ground maintenance is provided by OS shell access

Environment assumptions
-----------------------

Per section 9.4 of SAVOIR-GS-002, a requirement does not apply when its Assumption is not featured.

Assumptions that hold in the NMF environment:

- **Application storage memory** — the filesystem stores multiple software baselines side by side.
- **Working memory** — provided by the OS/JVM.
- **Select ASW image from ground** — the baseline manifest is updated by the Package Management service on
  upgrade, and its fields are commandable directly through the Parameter service.
- **Protected resource retaining data when fault or power loss** — approximated by persistent disk storage
  (without the radiation-hardening guarantees of a true SGM).

Assumptions that do **not** hold (and the requirement groups they disable):

- **Boot memory (read-only PROM)** — no read-only memory device; the bootloader is a file on disk.
- **PM redundancy / inter-processor link / powering both PMs** — single computer; disables all Standby
  requirements.
- **Monitor interface (EGSE serial link)** — disables all Monitor requirements.
- **Reconfiguration function in hardware** — absent; see Deviations.
- **Essential telemetry (HPTM)** — no hardware-generated telemetry path.
- **EDAC, FPU/IU/MMU registers, SPARC traps, multi-core reset control** — processor initialisation is owned
  by the OS and the JVM.
- **Hardware watchdog** — absent; the confirmation-marker pattern replaces the watchdog kick (section
  4.1.1 of SAVOIR-GS-002 explicitly describes the lack of watchdog kick as the means to detect ASW start-up
  failure, which is the pattern adopted here in software).

Compliance matrix
-----------------

Status legend: **C** = Compliant, **A** = Adapted (requirement kept, wording adapted to the NMF
environment), **N/A** = Not Applicable (SAVOIR Assumption not featured; per SAVOIR-GS-002 section 9.4).

.. list-table::
   :header-rows: 1
   :widths: 24 12 64

   * - SAVOIR.BOOTSW
     - Status
     - NMF requirement / justification
   * - BEF.05 (modes)
     - A
     - Collapses to the Nominal Sequence (NMF.BOOT.BEF.02): Fast Boot Path, Standby and Monitor are N/A,
       leaving a single mode and therefore no mode support to require.
   * - BEF.10 (execution on reset)
     - A
     - NMF.BOOT.BEF.01. Executed on every Supervisor start instead of processor reset.
   * - BEF.15, BEF.22 (Fast Boot Path)
     - N/A
     - The ``FastBootPath`` parameter is answered with "not supported" (a choice section 9.3.1 leaves to
       the project): the Supervisor is not in the spacecraft's reconfiguration chain, so no boot-time
       criticality justifies skipping tests, and the dominant boot cost (JVM start-up) cannot be skipped
       anyway.
   * - BEF.20 (nominal sequence)
     - A
     - NMF.BOOT.BEF.02. Steps 5–6 (copy to working memory, re-test) omitted: no copy step exists in a JVM
       context. The note's "untouched image for contingency" is implemented by the factory baseline
       (NMF.BOOT.BMM.03).
   * - BEF.25 (image selection)
     - A
     - NMF.BOOT.BEF.03. Autonomous fallback selection adopted per the BEF.25 note (missions with long
       periods without ground contact).
   * - BEF.27 (selection data control)
     - A
     - NMF.BOOT.BMM.02. Ground control through the Package Management service (automatic, on upgrade) and
       the Parameter service (direct override) instead of HPC-1 relay status.
   * - BEF.30 (sequence when failure)
     - C
     - NMF.BOOT.BEF.04.
   * - BEF.40–67 (Monitor)
     - N/A
     - Assumption *Monitor interface* not featured. On-ground maintenance is provided by OS shell access.
   * - BEF.70–97 (Standby)
     - N/A
     - Assumptions *PM redundancy* and *inter-processor link* not featured. In-flight SW maintenance is
       provided at ASW level by the Package Management service.
   * - BIN.180–245 (processor init)
     - N/A
     - IU/FPU/MMU/on-chip/trap/EDAC/interrupt/core initialisation is owned by the OS and the JVM.
   * - BIN.250 (final configuration)
     - A
     - NMF.BOOT.BIN.01.
   * - BTE.260, BTE.270 (self-tests)
     - A
     - NMF.BOOT.BTE.01, BTE.03. Environment self-tests and Java runtime launch check instead of
       processor-module self-tests.
   * - BTE.265 (HW BIT)
     - N/A
     - No hardware Built-In Test.
   * - BTE.280 (Boot SW self-integrity)
     - N/A
     - Assumption *Boot memory* (read-only) not featured; bootloader file integrity is delegated to the
       filesystem and mission-level provisions.
   * - BTE.290, BTE.295 (memory tests)
     - N/A
     - Volatile-memory testing is owned by the OS/hardware.
   * - BTE.300 (image integrity)
     - C
     - NMF.BOOT.BTE.02.
   * - BTE.310 (copied image integrity)
     - N/A
     - No copy-to-working-memory step.
   * - BTE.320 (EDAC during tests)
     - N/A
     - No EDAC control from software.
   * - BTE.330 (single algorithm)
     - C
     - NMF.BOOT.BTE.04.
   * - BAA.340 (after-test memory reset)
     - N/A
     - Process memory lifecycle is owned by the OS.
   * - BAA.350 (critical functions)
     - A
     - NMF.BOOT.BAA.01.
   * - BAA.360 (non-critical functions)
     - A
     - NMF.BOOT.BAA.02.
   * - BAA.370, BAA.390 (boot report, progress)
     - C
     - NMF.BOOT.BAA.03.
   * - BMM.375 (boot report in telemetry)
     - A
     - The Boot Report is a file retrievable through the Supervisor's services once running.
   * - BAA.380, BAA.400 (HPTM summary)
     - N/A
     - Assumption *Essential telemetry* not featured.
   * - BAA.385 (boot report content)
     - A
     - NMF.BOOT.BAA.04.
   * - BAA.410 (aligned copies)
     - N/A
     - Single Boot Report storage area.
   * - BAA.420 (boot report integrity)
     - C
     - NMF.BOOT.BAA.05.
   * - BAA.430 (multiple reports)
     - C
     - NMF.BOOT.BAA.06. This also answers the ``MultiBootReport`` strategy parameter of section 9.3: all
       reports are retained as timestamped sections of daily files, and the bootloader never erases them.
   * - BAA.440 (report slot selection)
     - A
     - NMF.BOOT.BAA.06: reports keyed by boot timestamp instead of SGM slot driven by the reconfiguration
       count.
   * - BPF.450 (WCET)
     - A
     - Bootloader overhead (self-tests + SHA-256 over the baseline) shall remain a small fraction of the
       mission boot-time budget. The Supervisor is not in the spacecraft's reconfiguration chain, so the
       budget is soft.
   * - IF.610–700 (PUS services in Standby)
     - N/A
     - No Standby mode. Equivalent maintenance functions are provided by the MO services at ASW level.
   * - BMM.100, BMM.110 (boot SW storage)
     - A
     - NMF.BOOT.BMM.01.
   * - BMM.120 (image independence)
     - C
     - NMF.BOOT.BMM.04.
   * - BMM.130, BMM.140, BMM.170 (report storage)
     - A
     - NMF.BOOT.BMM.05.
   * - BMM.150, BMM.160 (redundant storage)
     - N/A
     - No redundant protected resource.
   * - IMP.460 (OS independence)
     - N/A
     - Definitionally unsatisfiable at the application layer: the NMF Bootloader is OS-hosted by design,
       which is precisely what places it inside the ASW rather than in the Boot SW. Its spirit — simple,
       sequential, no runtime machinery — is preserved by NMF.BOOT.IMP.01.
   * - IMP.470 (minimal resources)
     - C
     - NMF.BOOT.IMP.01.
   * - STD.480, STD.490, STD.510 (ECSS)
     - N/A
     - ECSS compliance and criticality categorisation are mission-level activities, out of scope for the
       open-source framework.
   * - STD.500 (coding standard)
     - A
     - Shell coding standard; the bootloader is kept ``shellcheck``-clean.

Deviations
----------

1. **Application-layer scope.** On a Linux payload computer, SAVOIR-GS-002's literal scope is the platform
   boot layer (platform bootloader + operating-system image), which remains the mission's responsibility.
   This specification transposes the pattern to the application software layer, where the NMF's updatable
   unit — the software baseline — lives. In strict SAVOIR terms the NMF Bootloader is the first component
   of the ASW, not part of the Boot SW (SAVOIR.BOOTSW.IMP.460 requires OS independence); section 4.1.1 is
   kept as precedent for staged boot logic on writable storage, whose warning is addressed by
   NMF.BOOT.BMM.01.
2. **The bootloader absorbs a minimal Reconfiguration-function role.** In SAVOIR the autonomous recovery
   (image switching after failed boots) belongs to a hardware Reconfiguration Module, and the Boot SW is
   deliberately free of FDIR. The NMF has no such hardware, so the bootloader implements the minimal subset
   needed for the fallback ladder (requirements REC.01–05) and nothing more; all other FDIR remains with
   the Supervisor.
3. **Autonomous baseline selection.** The default SAVOIR approach is ground-commanded image selection; the
   note of SAVOIR.BOOTSW.BEF.25 sanctions autonomous selection for missions with long periods without
   ground contact, which is the typical nanosat operational context.
4. **ASW-confirmed start-up.** The Supervisor writes a boot confirmation marker in place of the hardware
   watchdog kick; SAVOIR-GS-002 section 4.1.1 describes exactly this pattern (lack of watchdog kick as
   the detection means for ASW start-up failure).
5. **Runs on an operating system.** IMP.460 assumes the Boot SW predates any OS; the NMF bootloader runs
   on Linux by design and keeps the requirement's intent through implementation minimalism.
