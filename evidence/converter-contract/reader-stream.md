# Reader and input stream converter factories

The owned framework restores `StringToReaderConverterFactory` for the supplier's
five supported reader targets (`BufferedReader`, `CharArrayReader`,
`InputStreamReader`, `LineNumberReader`, and `StringReader`) and
`StringToInputStreamConverterFactory` for its four supported stream targets
(`ByteArrayInputStream`, `BufferedInputStream`, `DataInputStream`, and
`PushbackInputStream`). Unsupported target classes raise the owned
`ConverterNotFoundException`; null source values return null.

`scripts/tests/test-string-reader-stream-converters.sh` compares 262 observations
through the public `ConverterFactory`/`Converter` seams in isolated supplier and
owned loaders. It includes UTF-8, long and surrogate-containing strings, empty
and null sources, unsupported targets, bridge wrong-type calls, source/target
metadata, assignability and enum identity. The supplier common JAR is extracted
from the pinned run artifact by hash; the owned loader contains no supplier URL.
