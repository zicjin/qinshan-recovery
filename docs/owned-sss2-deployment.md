# Application switch and rollback

Local development uses the existing owned application on 18087. The named
`sss2` owned application `lims-owned-sss2-18088-app` is a separate human acceptance
target; its port does not make it a disposable debug instance. Verify live
container IDs, mounts and distribution hashes before operating on either host.
Production is strictly read-only. Finish active HTTP batches and fixture cleanup
before switching an application.

## Prepared installation

Build a source-only distribution using the standalone README, and retain its
immutable directory and `manifest.json`. Container staging is an explicit
operator step: retain the current application, and prepare a stopped candidate
with the same image, command, environment, user, working directory, host settings
and mounts. Only the read-only bind at `/opt/current-owned` may select another
distribution. Neither container may contain a supplier run JAR as a runtime
dependency. Do not overwrite an active distribution in place.

Both versions must be compatible with the existing schema and persistent files.
Keep the matching private configuration unchanged. The work/files and report
engine mounts must point to the same persistent locations, with their original
read/write settings. Keep configuration and authorized third-party entitlement
material outside Git. The stopped target must have no connected networks; the
running application must use one internal Docker network. Preserve its exact
IP and aliases, published ports and any existing host transport. A separate
configuration, database or file migration needs its own reviewed recovery plan;
this switch entry rejects such differences.

Run `scripts/switch-owned-application.py` on the Docker host using Python 3 and
OpenSSL. It uses a local Docker Unix socket and rejects remote Docker settings.
Use full immutable container IDs obtained from local `docker inspect`, rather
than interpreting a port or mutable name as an identity. Set `CURRENT_APP_ID`,
`RETAINED_APP_ID` and `APP_NETWORK` from that inspection; the readiness URL must
reach this application through its existing loopback transport.

## Preview and execute

```bash
python3 scripts/switch-owned-application.py \
  --current "$CURRENT_APP_ID" --target "$RETAINED_APP_ID" \
  --network "$APP_NETWORK" \
  --readiness-url http://127.0.0.1:18087/open/security/public-key \
  --backup-name owned-app-before-switch \
  --receipt /private/path/switch-plan.json
```

The default is a dry run. It validates both manifests and every registered file
hash before stopping anything. A receipt path must be new and private. Review
the plan, then use the same arguments with `--execute` and a new receipt path.
The old application is stopped, retained under the backup name and disconnected
before the target receives its name and network address. No second application
runs alongside it. Oracle, Redis and other network peers are not restarted or
replaced.

The entry waits for a nonempty RSA X.509 public key at the readiness URL. Empty
HTTP 200 responses do not establish readiness. It checks the protected network
peer identities and writes a private receipt. Receipt metadata is diagnostic,
not strict API evidence. After switching, log in and read
`/core/module/sys/logins/account` in the same session, then read meaningful
application state through its original HTTP API. Compare configuration and
persistent file hashes with the pre-switch baseline. Keep fixture IDs and
responses private; use the original API for fixture creation and deletion.

## Reverse switch and failure recovery

To return to the retained container, exchange the two IDs and use a new backup
name and receipt path:

```bash
python3 scripts/switch-owned-application.py \
  --current "$RETAINED_APP_ID" --target "$CURRENT_APP_ID" \
  --network "$APP_NETWORK" \
  --readiness-url http://127.0.0.1:18087/open/security/public-key \
  --backup-name owned-app-retained-after-rollback \
  --receipt /private/path/rollback-plan.json
```

Preview first; add `--execute` with another new receipt path for the actual
rollback. Repeat identity and entity readback after rollback, then clean up the
dedicated fixtures. Do not rewind sequences or erase shared logs. Application
rollback does not undo business writes, schema migrations or uploaded files;
database/file recovery requires matching backups and a separate restore.

If activation fails after stopping the original application, the entry attempts
to stop and disconnect the target, restore the original name and network endpoint,
restart it and verify RSA readiness. It still exits with failure and records
whether restoration succeeded. Preserve both containers and failure receipts.
If restoration is incomplete, inspect actual container/network state before any
manual recovery; do not blindly repeat the activation command.

The older `refresh-owned-sss2-app.sh` and `rollback-owned-sss2-app.sh` are retained
for their historical layout references and tests. Their fixed remote defaults,
configuration edits and container recreation do not preserve this installation's
work/report mounts. Do not use them for candidate delivery. The reviewed switch
entry also does not provision a new server: complete installation remains
subject to the [clean-device contract](deployment/clean-device-contract.md).
