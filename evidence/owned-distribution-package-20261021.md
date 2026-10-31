# Owned distribution package evidence (2026-10-21)

The standalone distribution was rebuilt from commit `83d673c` with both
`JAVA_HOME` and `LIMS_PROJECT_ROOT` unset:

```text
env -u JAVA_HOME -u LIMS_PROJECT_ROOT python3 owned-distribution/tests/test-package.py
owned distribution package: PASS
```

The check rebuilt all owned modules, rejected a synthetic differing duplicate
class, required external configuration before startup, and reproduced the
packaged files byte-for-byte after a second clean rebuild. It did not contact
production or change `sss2`.
