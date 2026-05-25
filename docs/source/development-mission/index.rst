================================
Development Guide for Missions
================================

An **NMF Mission** is a concrete implementation of the NanoSat MO
Framework for a specific spacecraft. It sits between the generic NMF
Core and the NMF Apps: the core provides the reusable machinery; the
mission wraps the spacecraft's hardware into NMF-compatible services;
and the apps run on top without knowing which hardware they are
flying on.

.. figure:: ../images/nmf-missions-on-core.png
   :align: center
   :alt: Multiple NMF Missions built on top of a single NMF Core Implementation

.. note::

   Mission development is separate from App development and Ground
   software development. The same NMF Core can host any number of
   independent missions, and a correctly written NMF App is
   mission-agnostic — it runs unmodified on any mission that provides
   the platform services it needs.

   There is intentional overlap between this guide and the
   :doc:`../mission-integration/index` section.  That section focuses on
   *deploying* a finished image onto hardware; this guide focuses on
   *developing* the mission-specific software in the first place.

What a mission provides
-----------------------

A mission integration is responsible for three things:

1. **A concrete** ``NanoSatMOSupervisor`` **subclass** — the process
   that boots on the spacecraft, starts the NMF runtime, launches and
   supervises NMF Apps, and exposes all services to the ground.

2. **Platform service adapters** — hardware-specific implementations
   of the Platform service adapter interfaces (GPS, ADCS, Camera, …).
   These are the only code in the system that talks to real hardware;
   everything above them speaks MO services.

3. **An on-board filesystem layout** — the directory tree and startup
   scripts that the Supervisor and Apps expect to find on the
   spacecraft's file system.

A mission may additionally supply a custom transport binding (e.g.
MALSPP for a real space link) and integrate it with the Ground MO
Proxy for protocol bridging.

.. toctree::
   :maxdepth: 1
   :caption: Contents

   supervisor
   platform-services
   filesystem
   deployment
