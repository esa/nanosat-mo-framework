====================================
Consuming Monitor & Control services
====================================

.. contents:: Table of contents
   :local:

This section shows the consumer-side patterns for the four most common M&C interactions: reading parameter
values, invoking actions, and subscribing to alerts and action progress.

The Ground MO Adapter
---------------------

The code blocks below assume a ``GroundMOAdapterImpl`` instance named ``gma`` — the consumer-side handle for
one remote provider, with typed accessors per service category (``gma.getMCServices()``,
``gma.getCOMServices()``, ``gma.getSMServices()``).

.. code-block:: java

   GroundMOAdapterImpl gma = GroundMOAdapterImpl.forApp(directoryURI, "my-app");

See :doc:`ground-mo-adapter` for full coverage.

Simple commanding
-----------------

The ``setValue`` and ``submitAction`` operations are sufficient for straightforward control of a remote app:

.. code-block:: java

   ParameterStub params = gma.getMCServices().getParameterService();
   // set a parameter
   params.setValue(new ParameterRawValueList(new ParameterRawValue(
           parameterInstanceId, new Union("ON"))));

   ActionStub actions = gma.getMCServices().getActionService();
   // invoke an action
   ActionInstanceDetails action = new ActionInstanceDetails(
           actionDefId, false, false, false,
           new AttributeValueList(), null);
   actions.submitAction(0L, action);

Acquiring parameter values
--------------------------

A consumer that needs current values without subscribing can call ``getValue`` directly:

.. code-block:: java

   ParameterStub params = gma.getMCServices().getParameterService();
   ParameterValueList values = params.getValue(parameterInstanceIds);

For continuous updates, subscribe to the Parameter service's PubSub operation:

.. code-block:: java

   ParameterStub params = gma.getMCServices().getParameterService();
   params.monitorValueRegister(subscription,
           new ParameterMonitorValueAdapter() {
               @Override
               public void monitorValueNotifyReceived(
                       MALMessageHeader hdr, Identifier subId,
                       UpdateHeaderList updates,
                       ObjectIdList objIds,
                       ParameterValueList values, Map qos) {
                   // handle the update
               }
           });

Each update arrives asynchronously through the adapter.

Listening to alerts
-------------------

The Alert service publishes via PubSub. The consumer pattern mirrors parameter subscription:

.. code-block:: java

   AlertStub alerts = gma.getMCServices().getAlertService();
   alerts.monitorEventRegister(subscription, new AlertMonitorEventAdapter() {
       // override the notify callback to handle each alert
   });

See the generated API for the exact adapter and callback signatures.

Listening for action execution progress
---------------------------------------

When invoking a multi-stage action, register an ``ActionMonitorAdapter`` to receive progress updates. The
Supervisor publishes execution stages via the COM Event service, and the adapter demultiplexes them by action
instance identifier.

.. code-block:: java

   ActionStub actions = gma.getMCServices().getActionService();
   ActionMonitorAdapter monitor = new ActionMonitorAdapter() { /* ... */ };
   actions.monitorExecutionRegister(subscription, monitor);

Each stage reported by the app appears as a notification on the adapter.
