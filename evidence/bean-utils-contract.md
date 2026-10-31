# BeanUtils and CopyConfig contract

`BeanUtils` and its nested `CopyConfig` are dependency-closed after the owned BeanProperty, Reflection, Convert, ObjectUtils, ArrayUtils, and domain leaves. The implementation follows the pinned supplier's public declarations and observed behavior for bean copy, prefix/suffix mapping, map copy and conversion, extension fields, `toMap`, `deeplyToMap`, property get/set, nulls, and nested CopyConfig accessors.

The parentless differential test extracts and hash-checks the supplier common JAR, uses separate parentless supplier and owned loaders, compares public declarations for both classes, and runs a 164-observation matrix. The matrix covers bean and map copy, all null/empty/error paths, six CopyConfig prefix/suffix variants, numeric and string conversion, `toMap` and `deeplyToMap`, property get/set and `ext$` fields, `match`, `emptyProperties`, `parse`, ServletRequest conversion, ResultSet metadata/value/SQL failures, listener proxies and changed-property copying, and Persistable targets. Every public `BeanUtils` method is exercised by the matrix.

```text
BeanUtils differential: PASS (164 matrix observations, every public method, copy config, request/resultset/listener/ext$/errors)
```

No context, security, license, dialect, supplier fallback, or production material is used.
