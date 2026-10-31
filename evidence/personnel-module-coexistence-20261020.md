# Personnel and equipment module coexistence smoke

Date: 2026-10-20 (Asia/Singapore)

The packaged personnel JAR from this branch and the equipment JAR from its
runtime branch were loaded together with the recovered `GikamWpjcApplication`
and the owned framework. A loopback Boot smoke started the real application
without opening a database connection and checked nine properties:

- one Boot `DataSource` bean;
- one unchanged original personnel resource and one unchanged original
equipment resource;
- exactly one shared `OwnedReadWebConfiguration.WrapperArgumentResolver` and
one shared `JsonMessageConverter`;
- both original `/queries/raw` mappings;
- anonymous personnel and equipment query requests remain HTTP 403 under the
shared query guard.

The smoke passed. The equipment artifact used was
`lims-owned-equipment-1.0.0.jar`, SHA-256
`18dacb3ffd03f4f97bd31802d208480e9fa6d46e72f5226a9e81a00a721052ea`.
The packaged personnel and framework artifacts were built from the committed
runtime source and are recorded in
[personnel-runtime-oracle-20261020.json](personnel-runtime-oracle-20261020.json).
The smoke used no production endpoint, database, bind mount, or host port.
