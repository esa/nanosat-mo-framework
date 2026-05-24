=======================
ArtificialIntelligence
=======================

.. contents:: Table of contents
   :local:

The ArtificialIntelligence service exposes on-board AI inference
capabilities to apps. It allows an app to submit data to a deployed
model and retrieve predictions.

Defined in ``area105-Platform.xml`` as service number 9. Implemented by
``ArtificialIntelligenceProviderServiceImpl``; the adapter interface is
``AIAdapterInterface``.

Operations
----------

Operations include model selection, inference invocation, and result
retrieval. See ``area105-Platform.xml`` for the full operation list.

Consuming the service
---------------------

.. code-block:: java

   ArtificialIntelligenceStub ai = connector.getPlatformServices()
                                            .getArtificialIntelligenceService();
   // submit input, retrieve predictions

Adapter selection
-----------------

Selected via the ``ai.adapter`` property in ``platformsim.properties``.
A reference hardware adapter, ``AIMovidiusAdapter``, targets the Intel
Movidius VPU used on ɸ-Sat-2 for accelerated on-board inference.

Reference example
-----------------

``sdk/examples-space/edge-ai`` demonstrates on-board inference, and
the ɸ-Sat-2 mission deployed apps in this category for image
classification (see :doc:`../app-chaining` for the
cloud-tile filtering chain).
