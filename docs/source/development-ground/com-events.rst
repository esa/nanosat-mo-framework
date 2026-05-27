=====================
Retrieving COM events
=====================

.. contents:: Table of contents
   :local:

The COM Event service publishes asynchronous events generated anywhere in the system: parameter value updates,
action execution stages, alerts, and lifecycle changes. A ground application typically subscribes to events to
drive downstream behaviour.

Subscribing to events
---------------------

The Event service is reached through ``GroundMOAdapterImpl``:

.. code-block:: java

   EventStub events = gma.getCOMServices().getEventService();
   Subscription sub = buildSubscription();  // see below
   events.monitorEventRegister(sub, new EventReceivedAdapter() {
       @Override
       public void monitorEventNotifyReceived(
               MALMessageHeader header, Identifier subId,
               UpdateHeaderList updates, ObjectIdList objIds,
               ElementList elements, Map qos) {
           // handle each event
       }
   });

Subscriptions
-------------

A ``Subscription`` describes which events the consumer is interested in: the domain, object types, and
(optionally) entity filters. A catch-all subscription receives every published event; a narrower subscription
filters by service or by specific COM object type.

Use the helpers in ``HelperCOM`` and similar utility classes to build common subscription patterns without
writing the underlying MAL structures by hand.

Retrieving historical events
----------------------------

For events that have already been published, query the COM Archive through the Archive service rather than
subscribing. See the Reference section for the Archive query patterns.
