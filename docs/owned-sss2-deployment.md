# Owned application deployment on `sss2`

`sss2` hosts the long-lived supplier `18081`, owned `18087`, debug `18088`,
Oracle and Redis boundaries. Production `sss:8081` is read-only.

## App-only refresh

Build the source-only distribution first, then refresh only the owned
application:

```bash
export JAVA_HOME=/opt/jdk1.8.0_391
export LIMS_PUBLIC_DEPENDENCY_DIR=/opt/lims-public-dependencies
export LIMS_EXTERNAL_ARTIFACT_ROOT=/opt/lims-build-artifacts
bash scripts/build-owned-distribution.sh
bash scripts/refresh-owned-sss2-app.sh
```

The refresh script transfers the distribution to `sss2`, replaces only
`lims-owned-sss2-app`, and verifies readiness through the public-key endpoint.
It does not restart or replace Oracle, Redis, supplier `18081` or debug
`18088`. The generated `app-refresh-report.json` is diagnostic deployment
metadata, not strict API evidence.

## Rollback

Retain the previous complete distribution outside Git, then redeploy it
through the same app-only path:

```bash
bash scripts/rollback-owned-sss2-app.sh /path/to/previous/distribution
```

The rollback entry validates the source-only distribution boundary before
calling the refresh path. It does not delete or recreate database containers.

## Runtime boundary

- one Spring application;
- one long-lived Oracle and one Redis;
- `boundary route != boundary process`;
- no compatibility process, internal HTTP bridge, JNI daemon or second
  application/database runtime;
- external configuration and controlled build inputs remain outside Git and
  outside the distribution.

Business create/update/delete must use real HTTP APIs. SQL is limited to
fixture control, sequence/audit reads, rollback checks and cleanup.
