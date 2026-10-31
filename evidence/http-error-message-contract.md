# HttpErrorMessage contract evidence

The owned web error payload preserves both `of` factories, status/error/message
fields, optional stacktrace, timestamp, JSON serialization, and mutable public
accessors. The parentless differential compares no-request, throwable, and an
owned thread-bound request path against the pinned supplier class.

```text
bash scripts/tests/test-http-error-message.sh
HttpErrorMessage differential: PASS (factories, fields, stacktrace, request path and JSON payload)
```

The request path uses the project-owned `OwnedRequestContext` bridge.
