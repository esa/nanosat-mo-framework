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

   ProviderList providers = GroundMOAdapterImpl.retrieveProvidersFromDirectory(new URI(directoryURI));
   GroundMOAdapterImpl gma = new GroundMOAdapterImpl(providers.get(0));

See :doc:`ground-mo-adapter` for full coverage.

Simple commanding
-----------------

The ``setValue`` and ``executeAction`` operations are sufficient for straightforward control of a remote app:

.. code-block:: java

   ParameterStub params = gma.getMCServices().getParameterService();
   // set a parameter
   ParameterRawValueList newValues = new ParameterRawValueList();
   newValues.add(new ParameterRawValue(parameterInstanceId, new Union("ON")));
   params.setValue(newValues);

   ActionStub actions = gma.getMCServices().getActionService();
   // invoke an action
   ExecutionRequest request = new ExecutionRequest(
           actionDefId, new AttributeValueList(), null);
   Long executionId = actions.executeAction(request);

Acquiring parameter values
--------------------------

A consumer that needs current values without subscribing can call ``getValue`` directly:

.. code-block:: java

   ParameterStub params = gma.getMCServices().getParameterService();
   ParameterValueDetailsList values = params.getValue(parameterInstanceIds);

For continuous updates, subscribe to the Parameter service's PubSub operation:

.. code-block:: java

   ParameterStub params = gma.getMCServices().getParameterService();
   params.monitorValueRegister(subscription,
           new ParameterAdapter() {
               @Override
               public void monitorValueNotifyReceived(
                       MALMessageHeader hdr, Identifier subId,
                       UpdateHeader updateHeader,
                       MonitorValueSubscriptionKeys keys,
                       ParameterValue newValue, Map qos) {
                   // handle the update
               }
           });

Each update arrives asynchronously through the adapter.

Listening to alerts
-------------------

The Alert service publishes via PubSub. The consumer pattern mirrors parameter subscription:

.. code-block:: java

   AlertStub alerts = gma.getMCServices().getAlertService();
   alerts.monitorAlertRegister(subscription, new AlertAdapter() {
       // override the notify callback to handle each alert
   });

See the generated API for the exact adapter and callback signatures.

Listening for action execution progress
---------------------------------------

When invoking a multi-stage action, register an ``ActionAdapter`` to receive progress updates. The Supervisor
publishes execution stages via the COM Event service, and the adapter demultiplexes them by action instance
identifier.

.. code-block:: java

   ActionStub actions = gma.getMCServices().getActionService();
   ActionAdapter monitor = new ActionAdapter() { /* ... */ };
   actions.monitorExecutionRegister(subscription, monitor);

Each stage reported by the app appears as a notification on the adapter.
