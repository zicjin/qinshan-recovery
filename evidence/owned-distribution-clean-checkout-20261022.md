# Independent owned distribution clean checkout (2026-10-22)

A detached clone with independent Git objects was checked out at the integration source commit and initialized with the fixed historical project submodule commit. The checkout started with no owned module target directories and no environment variables for `JAVA_HOME` or `LIMS_PROJECT_ROOT`.

`build-prep.sh` verified all 56 fixed materials, extracted JDK 8u391, rebuilt the offline 402-jar repository, and unpacked the pinned plugin seed. `owned-distribution/tests/test-package.py` then passed the deterministic package, supplier-runtime rejection, external configuration requirement, duplicate-class conflict, and clean-rebuild checks.

This is clean-build and packaging evidence only. It does not close full application startup, login, or full business coverage.
