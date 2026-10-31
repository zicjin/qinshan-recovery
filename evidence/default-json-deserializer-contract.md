# DefaultJsonDeserializer contract

The owned JSON deserializer preserves the supplier enum's `INSTANCE`,
Fastjson `ObjectDeserializer` surface, and fast-match token while delegating
conversion to the owned `ConvertUtils` implementation. Its differential test
uses isolated supplier and owned class loaders and performs no application or
native initialization.
