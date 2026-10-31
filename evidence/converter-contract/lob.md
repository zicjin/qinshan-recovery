# JDBC LOB and byte/character converter leaves

Restored nine supplier leaves covering primitive bytes/chars, strings, JDBC
`Blob`/`Clob`, and reverse extraction. The Java 8 differential test uses only
`SerialBlob`/`SerialClob` and an isolated supplier common jar; no database
connection is opened. Null, empty, UTF-8 payloads, character payloads, and
source/target public descriptors are exercised. The full owned declaration gate
and supplier residue scan run through the shared build.
