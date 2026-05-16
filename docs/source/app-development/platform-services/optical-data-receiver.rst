=====================
OpticalDataReceiver
=====================

.. contents:: Table of contents
   :local:

The OpticalDataReceiver service exposes the spacecraft's optical
communication receiver to apps.

Defined in ``area105-Platform.xml`` as service number 5. Implemented by
``OpticalDataReceiverProviderServiceImpl``; the adapter interface is
``OpticalDataReceiverAdapterInterface``.

Operations
----------

Operations include receiver configuration and data stream access. See
``area105-Platform.xml`` for details.

Consuming the service
---------------------

.. code-block:: java

   OpticalDataReceiverStub optrx = connector.getPlatformServices()
                                            .getOpticalDataReceiverService();

Adapter selection
-----------------

Selected via the ``optrx.adapter`` property in
``platformsim.properties``.
