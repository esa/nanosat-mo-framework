COM services implementation
============

The implementation of the COM services includes all the 3 services: Activity Tracking, Event and Archive service.

The Activity Tracking service does not include any operations because it relies on the Event service for emitting the Activity Tracking COM Events. Therefore, the implementation of the service simply includes methods to allow the release of Activity Tracking COM Events when the provider needs to.

The Event service includes a single operation on its service interface that is used to emit COM Events of other services. In this implementation there are a set of methods to generate, store and publish COM Events that can be used by other services.

The Archive service implementation data is managed using a relational database management system that is based on Structured Query Language (SQL).

============

The Archive service interface includes a set of operations to retrieve, query, count, store, update, and delete. This interface is connected to a database transactions processor responsible for holding a queue of transactions to be performed with the actual database in a sequentially ordered manner. The selected implementation uses the SQLite JDBC Driver implementation for accessing and creating SQLite database files.

There are optimizations on the MO Archive service interface layer. For example, the generation of the object instance identifier is actually faster than if a value is assigned directly because the service interface layer does not have to check in the database if it exists.

SQLite is an in-process library that implements a self-contained, serverless, zero-configuration, transactional SQL database engine. SQLite is an embedded SQL database engine which means that unlike others, it does not have a separate server process. SQLite reads and writes directly to ordinary disk files, and for example, a complete SQL database with multiple tables, indices, triggers, and views, is contained in a single disk file. For the Archive service implementation, the filename is “comArchive.db”.

The COMObjectEntity table holds the COM Objects of the Archive service implementation. The table's primary key is composed by the fields: objectTypeId, objId, and domainId. It includes other fields such as network, objBody, providerURI, relatedLink, sourceLinkDomainId, sourceLinkObjId, sourceLinkObjectTypeId, timestampArchiveDetails. Some of these are linking to the remaining tables mentioned before.

The COM services were the first services to be implemented. Optimizing the COM Archive implementation was set as high priority and therefore it has been continuously improved throughout the research and development. Originally, the backend was using the embedded Apache Derby driver but this was later changed to SQLite in order attempt to improve performance. Additionally, the “database transactions processor” was included to decouple the service interface calls from the actual insertions into the database by having a single thread executor dedicated to execute the transaction with the database. This makes the store operation to be much faster during runtime because the call will not block waiting for the actual insertion in the database to be completed. Additionally, another executor with 2 fixed threads was included in order to speed up the initialization process of the Archive service and to handle the generation of COM Events of the Archive service. The Archive service was also extended to support a special PaginationFilter for its queries.

A bespoke service was included in this implementation for the synchronization of the COM Archive between space and ground. This is not originally part of the standard.



