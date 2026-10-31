# Declarative matrix validation (2026-10-22)

The 39-case evidence matrix passed with route observation checks and negative controls:

```text
bash scripts/tests/test-vertical-slice-matrix.sh
vertical slice matrix: PASS (39 cases; evidence and cleanup references valid)
vertical slice matrix negative controls: PASS (route, role, mode, syntax and cleanup)
```

The negative controls reject a misspelled route, an unobserved role or mode, malformed secondary routes, and evidence with a failed cleanup flag.
