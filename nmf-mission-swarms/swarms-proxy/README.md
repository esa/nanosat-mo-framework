Ground MO Proxy: OPS-SAT
============

The Ground MO Proxy application acts as a proxy that is deployed on the Ground segment for ESA's OPS-SAT mission. It extends the generic Ground MO Proxy of the NMF Core implementation and therefore most of the default behavior of the component is already present.

It is capable of acting as a protocol bridge and as a COM Archive mirror. As a result, multiple consumers can share the same ground-to-space connection to the spacecraft, and connect to independent NMF Apps simultaneously

The protocol bridge includes on one side the dedicated MAL-SPP transport binding developed for OPS-SAT and on the other side includes the MAL-TCP/IP transport binding.

The COM Archive mirror on ground has a single instance of the COM Archive implementation. If multiple NMF Apps are running simultaneously on the NanoSat segment, then they will synchronize with a single instance of the COM Archive on the Ground segment.

If one assumes that one single experiment runs on a certain timeslot, then the Ground MO Proxy application for OPS-SAT should be deployed as a new instance whenever a new experiment is started.

Two NMF Apps can run simultaneously however the COM Archive data for both of them is stored on the same instance on ground. This might generate privacy concerns for experimenters that want to have private data.

============

The Ground MO Proxy allows other NMF Ground applications to connect to any NMF App as this is part of the portability concept of the NMF. On one hand it means that an NMF Ground application is able to connect to other future NMF Apps, and on the other hand it means that NMF Apps developed for OPS-SAT can be reused by future spacecraft that have an NMF Mission in place.

