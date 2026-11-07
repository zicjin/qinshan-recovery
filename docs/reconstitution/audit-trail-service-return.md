# AuditTrailAspect service return reconstruction

The owned `auditTrailService` incorrectly cast every non-null business result to
`AuditTrailEntryData`. With the `audit-trail` profile active, a public service
returning `Long` failed after its audit inserts with `ClassCastException`. The
repair returns the unmodified result from inside the existing `try/finally`.
Audit writes, exception propagation and both thread-local removals are preserved.
No advisor-order annotation or runtime configuration is changed.

The controlled stock run JAR was read offline and its identity verified:

| Input | SHA-256 |
| --- | --- |
| stock run JAR | `4a720797810fd2f8cca6e22604657028cb87830ca0e24bb55a5f42f040f6ee9f` |
| nested `BOOT-INF/lib/gikam-core-common-14.1.5.jar` | `1075fda5ac05d4292b8c2efe2d9b3cc8379ce049c133221c27ba8f2cee7e1089` |
| `com/sunwayworld/framework/at/aspect/AuditTrailAspect.class` | `8d4c2606a7c9f7dc310b651fc33072f7202d67b35e6b8e3b4406d566671fd5cf` |

JDK 8u391 `javap -c -p` shows `proceed():Object` at offsets 167–173,
`insertAuditTrail` at 175–180, and direct object load/store/return at 183–205.
There is no `checkcast AuditTrailEntryData` on the return path. Both normal and
exceptional exits remove `RECORD_DATA` and `ENTRY_DATA` (191–200, 212–221).
The single/list `addRecordLine`, `insertAuditTrail`, and audit-record lambda were
also compared with the bytecode; the inspected collection casts, header/line
inserts and record-ID assignment agree. Request-header-dependent behavior was
not exercised by the fixture. The class originated from the recorded
CFR-to-Vineflower fallback; its transformation now records this bytecode-backed
repair. Extracted JAR/class material remains outside Git.

The executable regression compiles this exact tracked aspect plus its fixture
into a temporary directory. It uses real Spring AOP, transaction interception,
MyBatis Mappers, JDBC and disposable in-memory H2. Public audit service/DAO
adapters connect the aspect to fixture Mappers; production audit Mappers and
Oracle SQL are outside this fixture. The only private-state setup seeds the ID
allocator's worker in the disposable JVM to avoid contacting Redis.

```bash
python3 scripts/tests/test-audit-trail-service.py \
  --jdk /path/to/jdk1.8.0_391 \
  --public-deps /path/to/reconstituted-public-deps \
  --framework-classes /path/to/owned-core-common/target/classes \
  --h2 /path/to/h2-2.2.224.jar
```

Before the repair, `--case long` failed at the erroneous cast. After the repair,
221 checks pass across the default Spring transaction order, explicit
transaction-first order, and disabled audit profile. They verify Long/object/
list/null identity, nested entries retaining the outer annotation and both
sets of audit lines, cleanup through public helper behavior, original business
exception propagation, real audit-line constraint failures, full business/
audit rollback, and reuse of the same thread after success/failure.

In both active fixture contexts the observed chain was
`ExposeInvocationInterceptor → TransactionInterceptor → AspectJAroundAdvice`.
The regression asserts that transaction advice surrounds audit persistence.
This temporary-context observation does not establish the application's actual
advisor chain or live Oracle failure behavior. Root integration must still
validate the configured application's HTTP audit and rollback behavior.
No runtime service, Oracle, Redis or remote environment was touched. These
results are source/seam evidence and add zero HTTP strict passes.
