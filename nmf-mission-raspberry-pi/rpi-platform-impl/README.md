Platform services adapters for a Raspberry Pi
============

Uses the NMF Core implementation which includes the generic implementation of all the Platform services. The Platform services take advantage of the adapter pattern in order to support different units.


Camera service adapter
============

The Camera service adapter for a USB WebCam connected to the Raspberry Pi.

When the camera is functional and a consumer takes a picture, the adapter will double-check if the device is connected and then, send the instructions to configure it and take the picture. Finally, it copies the content of the image that is stored as an external memory device into a Picture object and sends it back to the consumer.


