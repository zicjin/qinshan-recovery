# ClassUtils recovery evidence

The owned `com.sunwayworld.framework.utils.ClassUtils` now implements the full 23-entry public/protected contract recorded in `evidence/framework-contract/classes.jsonl` (supplier class SHA-256 `3263ef1947271eb12605225280241d670302ce75566eb55c515052edba025b8b`). The implementation includes the two dependency leaves used by this family, `UnexpectedException` and `ZipException`, with the supplier constructor surface.

Validation uses the hash-pinned `gikam-core-common-14.1.5.jar` extracted from `materials/gikam-third-czjky-14.1.5-run.jar`; no machine-specific supplier jar is used by the committed test. The isolated differential covers primitive and wrapper maps, constructor selection and exception boundaries, typed constructors, proxy/raw-type handling, TCCL and no-initialization class loading, array-name quirks, interface traversal, cache identity and mutability, classpath directory and JAR scans, the supplier's excluded `EncodingUtils$BytesEncodingDetect` inner class, resource predicates and I/O wrappers. It records 494 observations with the supplier and owned implementation matching. The inherited annotation traversal loop is reproduced and checked in separate three-second subprocesses so the compatibility check cannot hang the test process.

Run from `lims-recovery`:

```sh
JAVA_HOME=/home/ts/qinshan/lims-recovery/build/jdk1.8.0_391 \
  bash scripts/tests/test-class-utils.sh
```

The script rebuilds the owned framework, runs the exact declaration gate, loads supplier and owned utilities through separate parentless URL class loaders, and rejects supplier runtime content from the owned artifact.
