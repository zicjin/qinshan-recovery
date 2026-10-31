# Personnel auxiliary read slice

This slice preserves the historical `EmcEmpResource` read routes for the
personnel detail tabs:

- `POST /secure/emc/module/mdm/emp/emps/{employeeId}/quals/queries`
- `POST /secure/emc/module/mdm/emp/emps/{employeeId}/tests/queries`
- `POST /secure/emc/module/mdm/emp/emps/{employeeId}/credentials/queries`
- `POST /secure/emc/module/mdm/emp/emps/{certificateId}/methods/queries`

The owned adapter re-checks the current personnel menu grant and concrete
organization grants for every request. Qualification, visible-test, and
credential queries require the route employee to belong to a granted
organization. Credential-method queries join the certificate back to its
employee before selecting rows, so a caller cannot enumerate a certificate ID
from another organization. Writes remain fail closed through the existing
unsupported-operation boundary.

Validation was run serially with the digest-pinned Oracle Free image and an
internal, single-container network:

```text
export JAVA_HOME=/home/ts/qinshan/lims-recovery/build/jdk1.8.0_391
export LIMS_PROJECT_ROOT=/home/ts/qinshan/lims-recovery/project
python3 scripts/tests/test-personnel-query-oracle.py --http --output build/personnel-aux-local.json
```

Result: the personnel query service passed 15 checks, filter parity passed 19
checks, and the packaged Boot HTTP contract passed 92 checks. The HTTP fixture
also exercises separate `overdueFilter` and historical `remind=overdue` paths
against `T_EMC_EMP_QUAL` and `T_EMC_CERT`. The report recorded no Oracle errors;
the disposable container, anonymous volumes, and internal network were removed
after the run.

Training is deferred. Historical Java sources contain
`EmcEmpTrainBean`/`EmcEmpTrainResource`, but the recorded Oracle schema files
contain no `T_EMC_EMP_TRAIN` table definition or migration, and the historical
personnel detail JavaScript inspected for this slice defines qualification,
credential/method, visible-test, and file tabs without a training tab. Adding a
training query now would require inventing an unverified schema and UI
contract.
