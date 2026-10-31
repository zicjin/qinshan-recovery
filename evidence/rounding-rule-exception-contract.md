# RoundingRuleException contract

`RoundingRuleException` is a dependency-closed runtime exception with the supplier's two constructors: `RoundingRuleException(Throwable)` delegates to `RuntimeException(Throwable)`, and `RoundingRuleException(String)` delegates to `RuntimeException(String)`. It retains supplier serialVersionUID `-6762796249760976132L` and `@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)`. No `NumberUtils` or rounding implementation is included.

The parentless test loads the hash-pinned supplier common JAR and owned classes in separate class loaders. It compares null/empty/text messages, checked and unchecked causes, cause/message accessors, response-status annotation, fixed serialVersionUID, serialized bytes, and deserialized class/message behavior. The differential test passes. The owned framework build declaration gate and supplier-runtime residue scan pass.
