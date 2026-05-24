============
App Chaining
============

.. contents:: Table of contents
   :local:

Apps running on the same spacecraft can consume each other's services
through the Supervisor's Directory Service. This is the foundation for
the *app chaining* pattern, in which one app's output drives another's
behaviour.

Obtaining a consumer for another app
------------------------------------

The ``SpaceMOAdapterImpl`` provides factory methods for consuming
services from peer apps:

.. code-block:: java

   SpaceMOAdapterImpl peerSMA = SpaceMOAdapterImpl.forNMFApp(
           connector.readCentralDirectoryServiceURI(),
           "<peer-app-name>");

The returned ``SpaceMOAdapterImpl`` exposes typed accessors for the
peer's MC services (parameters, actions, alerts). To consume services
from the Supervisor itself rather than a peer app, use
``SpaceMOAdapterImpl.forNMFSupervisor(...)``.

App chaining example
--------------------

The ɸ-Sat-2 mission used app chaining to split image processing across
two apps:

1. The first app classified each tile of a captured image as cloudy or
   clear.
2. The second app processed only the clear tiles, skipping the cloudy
   ones to save downlink bandwidth and on-board compute time.

The second app reached the first by name through ``forNMFApp``,
subscribed to the classification parameter, and triggered its
processing on each new value.

This pattern lets payload computations be modular and re-orderable.
Apps can be added, removed, or replaced without recompiling their
peers, since the Directory Service handles discovery at runtime.

Reference examples
------------------

- ``sdk/examples-space/space-to-space`` — connects to a peer app
  (``gps``) and logs received parameter values.
- ``sdk/examples-space/space-to-space-supervisor`` — connects to the
  Supervisor and logs received parameter values.
