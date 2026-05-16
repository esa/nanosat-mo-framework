==============================
The main class
==============================

.. contents:: Table of contents
    :local:

This chapter describes the behaviour of the main class of the Sobel app. As mentioned previously, the purpose of the main class (:java:type:`~esa.mo.nmf.apps.SobelApp`) is to set up communication with the NMF and its services, and to ensure that the app starts correctly.
Starting from the copied ``SnapNMF`` class, replace ``SnapNMF`` with ``SobelApp`` and ``MCSnapNMFAdapter`` with ``SobelMCAdapter``:

.. code-block:: java
   :linenos:

    public class SobelApp
    {
        private final NanoSatMOConnectorImpl connector;

        public SobelApp()
        {
            SobelMCAdapter adapter = new SobelMCAdapter();
            connector = new NanoSatMOConnectorImpl();
            adapter.setNMF(connector);
            connector.init(adapter);
        }
        /*
        Main command line entry point.
        @param args the command line arguments
        @throws java.lang.Exception If there is an error
        */
        public static void main(final String args[]) throws Exception
        {
            SobelApp demo = new SobelApp();
        }
    }


Communication with the NMF
--------------------------
Two components handle communication with the NMF. The first is the :java:type:`~esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl` (referred to as ``connector`` throughout the code and this guide).
The ``connector`` is used to request services from the NMF (in this example, the camera and iADCS services) and to push results back to the NMF for forwarding to the ground.
Alongside the ``connector``, an ``adapter`` is required to handle responses received from the ``connector`` and to forward requests and results to it.
For this reason, the ``connector`` is supplied to the ``adapter`` via ``adapter.setNMF(connector)``, and the ``adapter`` is supplied to the ``connector`` via ``connector.init(adapter)`` at startup.

Communication with the Supervisor or another app
------------------------------------------------
A space app can also consume services directly from the Supervisor or from another space app, using the ``forNMFSupervisor`` or ``forNMFApp`` methods of ``SpaceMOAdapterImpl``. For example, to obtain a GPS consumer for receiving GPS parameter values:

.. code-block:: java
   :linenos:

   SpaceMOApdapterImpl gpsSMA = SpaceMOApdapterImpl.forNMFApp(connector.readCentralDirectoryServiceURI(), "gps");

Complete examples are available among the space app examples:

- The space-to-space-supervisor app connects to the Supervisor, subscribes to its parameters, and logs the received values.
- The space-to-space app connects to the GPS app and logs the GPS parameter values as they are received.

Instantiating the app
---------------------
Since every operation of significance in this example is dispatched to the adapter and the connections are established in the constructor, the only remaining step is to create an instance of :java:type:`~esa.mo.nmf.apps.SobelApp` from the ``main`` method.

Continue to :doc:`adapter` for the next step.
