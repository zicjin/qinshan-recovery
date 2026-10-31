# EncodingUtils contract

`EncodingUtils` is a dependency-closed leaf of the framework utility surface.
The owned implementation preserves the supplier's public `detect(String)`,
`detect(File)`, `detect(byte[])`, and `detect(URL)` entry points and its
frequency-table `BytesEncodingDetect` algorithm. It does not add a fallback
encoding heuristic.

`scripts/tests/test-encoding-utils.sh` extracts the hash-pinned
`gikam-core-common-14.1.5.jar` from the read-only run artifact, verifies its
recorded SHA-256, builds the owned framework with Java 8, and compares supplier
and owned classes with parentless class loaders. The fixture covers 145
observations: null and empty bytes, ASCII, UTF-8 with and without BOM,
UTF-16LE/BE, GBK, Big5, malformed and binary bytes, 128 deterministic random
byte arrays, file/path/URL reads, and missing-file error behavior. The result
is `encoding utils differential: PASS 145`.

This closes the encoding detector only. It does not close `StringUtils`, whose
`encodeChineseFileName` still depends on the unresolved servlet request/context
graph, and it does not establish application bootstrap or page behavior.
