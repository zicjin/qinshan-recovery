# Personnel HTTP read prerequisite

Date: 2026-10-20 (Asia/Singapore)

The owned framework now declares the exact public generic contracts needed by
an original personnel resource/service/DAO adapter. A clean JDK 8u391 build
passes the recorded public-contract comparison for 248 classes, the owned
framework contract test, and supplier-runtime rejection:

```text
owned declared public contracts: PASS (248 classes)
supplier runtime rejection scan passed
owned framework build and contract test passed
```

`OwnedPersonnelDataHttpContractTest` is a red contract probe for the original
personnel data routes. It proves that RSA login can establish a real session
against the disposable Oracle fixture, then records the current gap:
`POST /secure/emc/module/mdm/emp/emps/queries/raw` returns HTTP 404 because no
production resource/service adapter is registered in the main runtime yet.
The prior run passed all 15 Oracle query checks before this HTTP assertion;
there were no ORA errors, and the internal container/network were removed.

This is prerequisite evidence. The unchanged `EmcEmpResource` and
`EmcEmpService` interfaces, their original resource implementation, and the
bounded adapter/configuration remain to be compiled from the recovered project
source into a main/runtime module. No competing controller or fixture-only
route is claimed, and browser/page acceptance remains open.
