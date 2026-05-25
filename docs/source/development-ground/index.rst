=====================================
Development Guide for Ground Software
=====================================

This guide describes how to develop a Ground Application that connects
to one or more NMF Apps and consumes their MO services. It mirrors the
:doc:`../development-app/index` on the space side.

A ground application typically:

- Connects to the Supervisor's Directory Service to discover running
  apps.
- Acquires consumer stubs for the services it needs.
- Subscribes to parameters, alerts, and action progress events.
- Invokes actions on behalf of the operator or automation logic.

.. toctree::
   :maxdepth: 1
   :caption: Contents

   project-setup
   ground-mo-adapter
   consuming-services
   com-events
   testing
   ground-mo-proxy
   faq
