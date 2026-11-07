# Invoice insert identity repair, 2026-11-07

`EmcInvoiceServiceImpl.insert(RestJsonWrapperBean)` copied the submitted invoice
into a new `insertBean`, allocated its sequence and primary key, inserted that
bean, then returned the submitted invoice's ID. A normal create therefore
returned null after a successful write; copying an existing invoice returned
the old record's ID. The parent task observed the create symptom through real
HTTP: POST returned HTTP 200 with an empty body, the query API found one record
with a generated ID, then the delete and query APIs confirmed cleanup. That
observation is diagnostic, not strict closure.

The correction changes only the final return to `insertBean.getId()`, after the
DAO insert completes. Identity allocation, sequence lookup using table and
current year/month, `COPYFLAG=1`, property copying and the existing copy-specific
clearing of amount/contract/project fields remain unchanged. DAO exceptions
continue to propagate. The return value identifies the actual DAO write argument.

## Original artifact verification

This defect exists in the approved stock application bytecode. It is not a
decompiler error or an introduced reconstructed framework behavior:

- Stock `gikam-third-czjky-14.1.5-run.jar` SHA256:
  `4a720797810fd2f8cca6e22604657028cb87830ca0e24bb55a5f42f040f6ee9f`.
- Member
  `BOOT-INF/classes/com/sunwayworld/emc/module/mdm/invoices/service/impl/EmcInvoiceServiceImpl.class`
  SHA256: `841418107edd8c23cd2d7942d5f3023025b4f291515ba494e75a796060384ae3`.
- Read-only JDK 8u391 `javap -c -p` inspection identifies local slot 2 as the
  parsed input bean (`astore_2` at 31), and slot 3 as the fresh copy (`astore_3`
  at 39). At offsets 171/172/175, slot 3 receives `getNextIdentity()` via `setId`.
  At 182/183 the DAO receives slot 3 in `insert(Persistable)`. At 188/189/192
  the method executes `aload_2`, input `getId()`, `areturn`.
- Tracked input source at standalone
  `e9835a069936973999073b5bb84a76df60dec4e8` SHA256:
  `b98228990cb60fad5d8945926e9f2a492f0750d1ea9132116950af6530f28d5a`.
- Repaired service source SHA256:
  `05ed528799dbd05a1c8e432da357c700b9e245b60318558ddf15adeec572438f`.

Stock material was read from the external analysis input and its single class
was inspected in a temporary directory; neither is committed or used at runtime.
This is an owned correction to original application source and intentionally
changes the erroneous stock return value.

## Regression and limits

`scripts/tests/test-invoice-insert-identity.py` compiles the current service,
invoice bean and invoice DAO/service interfaces with JDK 8 into a temporary
directory. It places those outputs before existing source-only application and
framework outputs and public dependencies. A real Spring annotation context
injects the DAO and contract/sequence collaborators. The public service interface
receives actual JSON through the reconstructed `RestJsonWrapperBean` parser;
the real Spring `BeanUtils` and reconstructed Snowflake generator execute.

The DAO is an explicitly declared capturing adapter. It accepts one actual
insert argument and requires a fresh nonnull key; it neither allocates IDs nor
reimplements the invoice transformation. The sequence fixture verifies table,
quantity and current year/month key. The test seeds the generator's worker
through reflection only in its disposable JVM to avoid Redis allocation. A
temporary logging configuration prevents application output paths being used.

Four scenarios pass:

- New invoice: returned ID equals the captured record ID; amount and references
  survive, as do name, sequence and copy flag.
- Copy with input ID 41: returned ID equals the new captured ID and differs
  from 41; the existing amount/contract/project clearing behavior is preserved.
- Empty body: the existing default-bean insertion returns its captured ID.
- DAO rejection: the exact exception escapes the service, no success ID is
  returned, and no row is captured.

Before the repair, new and copy scenarios were individually observed red at
the identity assertion (`got null` and `got 41`). After the repair all four
scenarios passed. `git diff --check` passed.

```bash
python3 scripts/tests/test-invoice-insert-identity.py \
  --prerequisite-root /path/to/prebuilt-source-only-checkout
```

The prerequisite checkout supplies JDK 8u391, the public dependency directory,
source-only application classes and seven reconstructed framework class
directories; it is read-only. The regression does not start an application,
database, Redis, build, listener or remote process, and performs no SQL. It
establishes the service/DAO contract, not MyBatis persistence, HTTP serialization,
transaction rollback or audit acceptance. Full source-only build/package and
real Oracle/HTTP follow-up belong to the parent checkpoint. No strict pass,
closure or evidence registry is changed by this repair.
