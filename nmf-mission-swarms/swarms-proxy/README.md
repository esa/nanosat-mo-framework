Swarms Proxy
============

The ground-side services of the Swarms mission. The module contains two classes.

`ConstellationDirectory` is one Directory service for the whole constellation. It is given the Directory service of each spacecraft, queries them, and publishes their Supervisors together, so a consumer connects to a single address and finds every spacecraft. It is the main class of the module and runs in its own container; see the `Dockerfile` for how to build and run it.

`GroundMOProxySwarmsImpl` is a Ground MO Proxy for a single spacecraft. It extends the generic Ground MO Proxy of the NMF Core and adds a protocol bridge between the MAL-SPP transport binding, used between the ground and the spacecraft, and the MAL-TCP/IP transport binding, used by the ground applications. It also mirrors the COM Archive of every App on ground and re-routes the Action service of every App through the proxy, so that several consumers can share one ground-to-space connection.

All the Apps of a spacecraft synchronise their COM Archive with the same instance on ground. Two Apps that run at the same time therefore store their data in the same archive, which matters when their owners must not see each other's data.
