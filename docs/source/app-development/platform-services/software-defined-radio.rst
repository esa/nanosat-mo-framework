======================
SoftwareDefinedRadio
======================

.. contents:: Table of contents
   :local:

The SoftwareDefinedRadio (SDR) service exposes the spacecraft's
software-defined radio payload to apps. It allows configuring the
radio's RF parameters and streaming samples.

Defined in ``area105-Platform.xml`` as service number 4. Implemented by
``SoftwareDefinedRadioProviderServiceImpl``; the adapter interface is
``SoftwareDefinedRadioAdapterInterface``.

Operations
----------

Operations include configuration of frequency, bandwidth, and gain, and
sample streaming. See ``area105-Platform.xml`` for the full operation
list.

Consuming the service
---------------------

.. code-block:: java

   SoftwareDefinedRadioStub sdr = connector.getPlatformServices()
                                           .getSoftwareDefinedRadioService();
   // configure and stream

Adapter selection
-----------------

Selected via the ``sdr.adapter`` property in
``platformsim.properties``. Mission-specific adapters target the actual
SDR hardware (e.g. the OPS-SAT SDR).

Reference example
-----------------

The ``sdk/examples-space/echo`` example demonstrates simple SDR
interaction.
