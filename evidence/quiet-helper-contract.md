# QuietHelper contract evidence

The owned `QuietHelper` preserves both supplier-facing `call` overloads. Normal
supplier results are returned, supplier and runnable exceptions are suppressed,
and failing suppliers return `null`.

```text
bash scripts/tests/test-quiet-helper.sh
QuietHelper differential: PASS (supplier success/failure and runnable suppression)
```
