==========================
Mission Development Guide
==========================

This guide describes how to integrate the NanoSat MO Framework into a
specific mission — that is, how to build and configure the
mission-specific Supervisor that manages on-board apps, provides
platform services, and exposes the NMF runtime environment for a
particular spacecraft.

A mission integration typically:

- Implements a ``NanoSatMOSupervisor`` subclass that wires up the
  mission's hardware platform services (GPS, ADCS, camera, etc.).
- Configures the NMF Linux filesystem layout using the
  ``nmf-linux-maven-plugin``, which generates the directory structure
  and ``fresh_install.sh`` deployment script.
- Bundles the Supervisor and all NMF dependencies into a deployable
  image for the target hardware.

.. note::

   There is intentional overlap between this guide and the
   :doc:`../mission-integration/index` section.  That section focuses on
   *deploying* a finished image onto hardware; this guide focuses on
   *developing* the mission-specific software in the first place.

.. toctree::
   :maxdepth: 1
   :caption: Contents

   supervisor
   platform-services
   filesystem
   deployment
