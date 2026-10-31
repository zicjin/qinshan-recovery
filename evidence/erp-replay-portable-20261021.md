# ERP replay portable test invocation

The ERP replay contract test now follows the repository's other build helpers:
when `JAVA_HOME` or `LIMS_PROJECT_ROOT` is unset it uses the pinned JDK
`build/jdk1.8.0_391` and the checked-out `project/` directory. This removes an
unnecessary shell-environment dependency from the isolated replay check.

Validation from commit `4227959`:

```text
env -u JAVA_HOME -u LIMS_PROJECT_ROOT ./scripts/tests/test-owned-erp-replay.sh
ERP replay contract: PASS (18 checks; loopback fixture only)
```

The test uses a loopback-only fixture transport and does not contact production
ERP or write production files.
