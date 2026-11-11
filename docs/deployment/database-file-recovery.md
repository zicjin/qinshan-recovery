# Matched Oracle schema and local-file recovery

This is a maintenance runbook for an **already prepared, explicitly authorized
installation** using `owned.files.storage=local`. It backs up one application
schema and its matching `files/` tree, then restores them to a dedicated target.
It does not install a new device, complete the partial Oracle bootstrap, recover
an FTP backend or establish another-device replay. The normal installation
[guard](clean-device-contract.md) remains in force. These instructions are not a
record of a successful restore; retain an actual execution/readback record.

Use one Spring application at a time with the existing Oracle and Redis. Never
restart those services or run this procedure on production `sss`. Refuse a
restore over a long-lived runtime without its separate, explicit maintenance
restore authorization. A development drill must use a dedicated disposable
schema, files directory and unused Redis logical database; never use `SYS`,
`SYSTEM`, `LIMS_RUNTIME_LOCAL` or any baseline schema as its restore target.
Keep the original source schema and files intact when restoring to a new target.
An application-only [switch](../owned-sss2-deployment.md) and API compensation
are separate operations; neither restores a database or reverses filesystem
writes.

## Private inputs and preflight

The commands below assume Bash, GNU tar/coreutils, Python 3.10+, Docker and the
existing Oracle container's matching SQLPlus/Data Pump clients. Run them as the
authorized operator on that Docker host. Resolve all paths and container IDs
before starting. Keep credentials, connection values, SQL logs, dump contents,
file names and business observations outside Git and public reports. Disable
shell tracing; do not pass passwords in process arguments or print private files.
The commands do not discover targets or create private SQL/credential inputs;
the authorized operator must prepare and review those exact inputs first.

Prepare a mode-0700 private directory, with mode-0600 inputs supplied through
the approved secret channel. It must contain:

- `operator.env`: nonsecret shell assignments for the full Oracle/source-app/
  restore-app container IDs (`LIMS_ORACLE_CONTAINER`, `LIMS_SOURCE_APP_CONTAINER`,
  `LIMS_RESTORE_APP_CONTAINER`), source/target owner names (`LIMS_SOURCE_SCHEMA`,
  `LIMS_TARGET_SCHEMA`), reviewed database/PDB/owner identity equality
  (`LIMS_SAME_INSTALLATION=yes` or `no`), source/target stable working directories
  (`LIMS_SOURCE_WORKDIR`, `LIMS_TARGET_WORKDIR`), the Oracle DIRECTORY object's
  resolved container path (`LIMS_DP_DIRECTORY_PATH`), Oracle service OS identity
  (`LIMS_ORACLE_OS_USER`), unique export/import dump names (`LIMS_DUMP_NAME`,
  `LIMS_IMPORT_DUMP_NAME`) and recovery-set identifier (`LIMS_RECOVERY_SET`).
  Use absolute paths and simple identifiers; these are operator inputs, not
  repository defaults.
- `source-preflight.sql`, `target-preflight.sql`, `source-scn.sql`,
  `source-highwater.sql`, `target-highwater.sql` and `target-recreate.sql`:
  private SQLPlus input files, including their own approved connection setup.
  Use `set echo off verify off`, `whenever oserror exit failure` and
  `whenever sqlerror exit failure rollback`; finish with `exit`. The SQLPlus
  command below opens `/nolog`, so no credential appears in its arguments.
- `export.par`, `preview.par`, `import.par`: private Data Pump parameter files,
  including their own `userid` connection input or approved wallet connection.
  Do not build these by interpolating a password into a shell command.
- The matching immutable distribution, private runtime files, original
  installation/upgrade version, schema/configuration/identity/data/file version
  receipt and its controlled acquisition location. A developer's private
  directory is not the handoff location.

The read-only preflight SQL must identify `SESSION_USER`, `CURRENT_SCHEMA`,
`CON_NAME`, database identity/version/character sets, source and target owners,
tablespaces, the Data Pump DIRECTORY and required privileges. Capture original
user DDL, roles/system/object grants and tablespace quotas privately before any
target change; a recreated target must retain the reviewed equivalents, not an
invented broad grant set. Confirm the runtime datasource user/PDB agrees with
that identity, and that its built external access mode and reachable objects
match the backup. Check the actual app identity, mounts and working directory;
ports alone are insufficient. Complete active fixtures before stopping it.

Record sequence definitions and `USER_SEQUENCES.LAST_NUMBER` without calling
`NEXTVAL`, plus the relevant business-counter rows/highest issued numbers through
version-specific read-only SQL. `LAST_NUMBER` can be a cache allocation bound,
not the last issued value. Record both the backup-time and immediately
pre-restore high-water observations. A drill must avoid counter-issuing APIs.
If a sequence/counter advances, preserve the greatest previously issued value
through a reviewed version-specific step before resuming writes. Never reset
production counter seeds or accept an imported lower value as a rollback.

```bash
set -euo pipefail
set +x
umask 077
LIMS_RECOVERY_PRIVATE=/private/path/approved-recovery-set
source "$LIMS_RECOVERY_PRIVATE/operator.env"
test "$(stat -c %a "$LIMS_RECOVERY_PRIVATE")" = 700
for input in operator.env source-preflight.sql target-preflight.sql source-scn.sql \
  source-highwater.sql target-highwater.sql target-recreate.sql export.par preview.par import.par; do
  test "$(stat -c %a "$LIMS_RECOVERY_PRIVATE/$input")" = 600
done
case "$LIMS_TARGET_SCHEMA" in
  SYS|SYSTEM|LIMS_RUNTIME_LOCAL|*BASELINE*) exit 1 ;;
esac
[[ "$LIMS_SOURCE_SCHEMA" =~ ^[A-Z][A-Z0-9_]*$ ]]
[[ "$LIMS_TARGET_SCHEMA" =~ ^[A-Z][A-Z0-9_]*$ ]]
[[ "$LIMS_RECOVERY_SET" =~ ^[A-Za-z0-9_-]+$ ]]
[[ "$LIMS_DUMP_NAME" =~ ^[A-Za-z0-9_-]+\.dmp$ ]]
[[ "$LIMS_IMPORT_DUMP_NAME" =~ ^[A-Za-z0-9_-]+\.dmp$ ]]
test "$LIMS_DUMP_NAME" != "$LIMS_IMPORT_DUMP_NAME"
case "$LIMS_SAME_INSTALLATION" in
  yes)
    test "$LIMS_SOURCE_APP_CONTAINER" = "$LIMS_RESTORE_APP_CONTAINER"
    test "$LIMS_SOURCE_SCHEMA" = "$LIMS_TARGET_SCHEMA"
    test "$(realpath "$LIMS_SOURCE_WORKDIR")" = "$(realpath "$LIMS_TARGET_WORKDIR")"
    ;;
  no) test "$(realpath "$LIMS_SOURCE_WORKDIR")" != "$(realpath "$LIMS_TARGET_WORKDIR")" ;;
  *) exit 1 ;;
esac
for phase in source-preflight target-preflight; do
  docker exec -i "$LIMS_ORACLE_CONTAINER" sh -c \
    'exec "$ORACLE_HOME/bin/sqlplus" -s /nolog' \
    < "$LIMS_RECOVERY_PRIVATE/$phase.sql" \
    > "$LIMS_RECOVERY_PRIVATE/$phase.log" 2>&1
done
```

Read the private results and explicitly confirm the authorized target before
continuing. Schema-name checks are an additional refusal, not authorization.
Set `LIMS_SAME_INSTALLATION=yes` only for the same explicitly authorized dedicated
schema/PDB and app restored in place; source/restore IDs and workdirs then match.
A different database/PDB/owner identity requires separate files, even when owner
names match. Long-lived runtime restoration still needs its separate approval.
If source and target owners differ, prepare an explicit `REMAP_SCHEMA` in the
preview/import inputs and review schema-qualified dependencies. The target
private runtime file must connect to that target PDB/owner, use its intended
working directory and select a **new, empty, dedicated Redis logical database**
in both URL/host settings and `spring.redis.database`. Confirm emptiness using
the approved private Redis client channel. Never flush a shared database.

## Capture one matched recovery set

Through the original authenticated HTTP APIs, first create an identifiable
notice/state A and attachment file A in the authorized fixture scope. Record
the returned original IDs, state, metadata, file-log/audit observations and
download SHA-256 privately. Use the same IDs throughout the drill. This is a
bounded recovery check, not full API acceptance.

Stop the sole source application and verify it is stopped. Prevent other writers
to its schema and files for the whole capture; Oracle and Redis remain running.

```bash
docker stop "$LIMS_SOURCE_APP_CONTAINER" \
  > "$LIMS_RECOVERY_PRIVATE/source-stop.log"
test "$(docker inspect --format '{{.State.Running}}' "$LIMS_SOURCE_APP_CONTAINER")" = false
for phase in source-scn source-highwater; do
  docker exec -i "$LIMS_ORACLE_CONTAINER" sh -c \
    'exec "$ORACLE_HOME/bin/sqlplus" -s /nolog' \
    < "$LIMS_RECOVERY_PRIVATE/$phase.sql" \
    > "$LIMS_RECOVERY_PRIVATE/$phase.log" 2>&1
done
```

`source-scn.sql` must select a single `CURRENT_SCN` from `V$DATABASE` in the
confirmed source PDB. Put that exact numeric result in `export.par`; retain it
with the private receipt. The export parameters must include:

```text
SCHEMAS=<confirmed source owner>
DIRECTORY=<confirmed Oracle DIRECTORY object>
DUMPFILE=<unique dump name>
LOGFILE=<unique export log name>
FLASHBACK_SCN=<the one captured SCN>
EXCLUDE=DB_LINK
PARALLEL=1
METRICS=YES
```

The angle-bracket fields describe private file contents, not a command to paste.
Choose a supported Data Pump `VERSION` only when needed for the target version;
record that choice. `DIRECTORY` is an Oracle object, not a client filesystem
path. Its server directory must be writable by Oracle and readable for the
approved copy. No DB link or source connection secret may become a runtime input.

Feed the completed private parameter file through stdin. The temporary container
file is mode 0600 and removed on normal exit or a trapped signal; no password is
placed on the command line. If the container/host dies, remove that exact task
file through the private operator channel before ending the operation.

```bash
docker exec -i "$LIMS_ORACLE_CONTAINER" sh -eu -c '
  umask 077
  par=$(mktemp /tmp/lims-expdp.XXXXXX)
  trap '\''rm -f -- "$par"'\'' 0
  trap '\''exit 130'\'' HUP INT TERM
  cat > "$par"
  chmod 600 "$par"
  "$ORACLE_HOME/bin/expdp" "parfile=$par"
' < "$LIMS_RECOVERY_PRIVATE/export.par" \
  > "$LIMS_RECOVERY_PRIVATE/export-client.log" 2>&1
docker cp "$LIMS_ORACLE_CONTAINER:$LIMS_DP_DIRECTORY_PATH/$LIMS_DUMP_NAME" \
  "$LIMS_RECOVERY_PRIVATE/$LIMS_DUMP_NAME"
```

Retain and inspect both client and Data Pump server logs; a dump file's existence
is not success. Stop on a nonzero exit, incomplete job or unexplained Oracle
error. Copy its server log privately too. With the app still stopped, capture
the local bytes. The trusted directory/parent requirements in
[file-storage.md](file-storage.md) apply; reject symlinks and special files.

```bash
test -d "$LIMS_SOURCE_WORKDIR/files"
test ! -L "$LIMS_SOURCE_WORKDIR/files"
test -z "$(find "$LIMS_SOURCE_WORKDIR/files" ! -type d ! -type f -printf x -quit)"
( cd "$LIMS_SOURCE_WORKDIR/files"
  find . -type f -print0 | LC_ALL=C sort -z | xargs -0 -r sha256sum \
    > "$LIMS_RECOVERY_PRIVATE/files.sha256"
  find . -printf '%m %U %G %y %P\0' | LC_ALL=C sort -z \
    > "$LIMS_RECOVERY_PRIVATE/files-stat.nul"
  tar --create --gzip --file "$LIMS_RECOVERY_PRIVATE/files.tar.gz" .
)
( cd "$LIMS_RECOVERY_PRIVATE"
  sha256sum "$LIMS_DUMP_NAME" files.tar.gz files.sha256 files-stat.nul > payload.sha256
)
chmod 600 "$LIMS_RECOVERY_PRIVATE/$LIMS_DUMP_NAME" \
  "$LIMS_RECOVERY_PRIVATE/files.tar.gz" "$LIMS_RECOVERY_PRIVATE/files.sha256" \
  "$LIMS_RECOVERY_PRIVATE/files-stat.nul" "$LIMS_RECOVERY_PRIVATE/payload.sha256"
```

Bind these hashes, SCN, source PDB/owner, distribution/configuration/installation
versions, external access mode and the file manifest into one private recovery
receipt. Store the set through the approved controlled backup location and
verify hashes after copying. The dump and files must always be acquired/restored
as that pair. Do not claim this receipt or its availability has been verified
until another operator can actually retrieve it.

## Preview the acquired dump

For a drill, resume only the prepared application, change notice A to state B
and replace its attachment through real HTTP. Read back B and require its download
SHA-256 to differ from A. Record the file row ID/association; if replacement
creates a new descriptor, record both associations so restoration can prove A's
original association. Then stop the app again. Keep all original bytes and
pre-restore high-water observations; finish any unrelated active fixtures first. No file swap or target
DDL is permitted while an application using either affected target is running.
Do not resume the source for B until the complete A recovery set has been copied
and hash-verified at its controlled backup location.

Verify `payload.sha256` from the acquired private set. Prepare `preview.par` and
`import.par` with the same source `SCHEMAS`, DIRECTORY, unique import dump name
(`LIMS_IMPORT_DUMP_NAME`) and target connection. Both must contain
`EXCLUDE=USER,DB_LINK` and `TRANSFORM=OID:N`, plus the reviewed `REMAP_SCHEMA`
only when owners differ. Use unique log names.
`preview.par` additionally contains `SQLFILE=<unique preview name>.sql`;
`import.par` must omit `SQLFILE`. Do not use `TABLE_EXISTS_ACTION`, `FULL=YES`
or an import over existing application tables.

```bash
( cd "$LIMS_RECOVERY_PRIVATE"; sha256sum --check --strict payload.sha256 ) \
  > "$LIMS_RECOVERY_PRIVATE/payload-check.log" 2>&1
test "$(docker inspect --format '{{.State.Running}}' "$LIMS_SOURCE_APP_CONTAINER")" = false
test "$(docker inspect --format '{{.State.Running}}' "$LIMS_RESTORE_APP_CONTAINER")" = false
docker exec -i --user "$LIMS_ORACLE_OS_USER" "$LIMS_ORACLE_CONTAINER" sh -eu -c '
  umask 077
  set -C
  cat > "$1"
  sha256sum "$1"
' -- "$LIMS_DP_DIRECTORY_PATH/$LIMS_IMPORT_DUMP_NAME" \
  < "$LIMS_RECOVERY_PRIVATE/$LIMS_DUMP_NAME" \
  > "$LIMS_RECOVERY_PRIVATE/import-server-dump.sha256"
python3 - "$LIMS_RECOVERY_PRIVATE/$LIMS_DUMP_NAME" \
  "$LIMS_RECOVERY_PRIVATE/import-server-dump.sha256" <<'PY'
import hashlib, pathlib, sys
digest = hashlib.sha256()
with open(sys.argv[1], "rb") as stream:
    for chunk in iter(lambda: stream.read(1024 * 1024), b""):
        digest.update(chunk)
if digest.hexdigest() != pathlib.Path(sys.argv[2]).read_text().split()[0]:
    raise SystemExit("server dump hash mismatch")
PY
docker exec -i "$LIMS_ORACLE_CONTAINER" sh -eu -c '
  umask 077
  par=$(mktemp /tmp/lims-impdp-preview.XXXXXX)
  trap '\''rm -f -- "$par"'\'' 0
  trap '\''exit 130'\'' HUP INT TERM
  cat > "$par"
  chmod 600 "$par"
  "$ORACLE_HOME/bin/impdp" "parfile=$par"
' < "$LIMS_RECOVERY_PRIVATE/preview.par" \
  > "$LIMS_RECOVERY_PRIVATE/preview-client.log" 2>&1
```

Copy the SQLFILE and server log from the confirmed DIRECTORY privately. Inspect
the preview for intended owner/tablespaces, grants, source-qualified references,
external dependencies and absence of DB links. **Do not execute the SQLFILE**;
it previews import DDL and does not perform the restore.
The staging command refuses to replace an existing server dump and writes as
the Oracle OS identity. Use a fresh task-owned name; never overwrite another
job's dump. Both preview and import must read that exact hash-checked file.

## Validate matching files before target changes

Stage next to the target `files/` directory on the same filesystem. Validate and
hash-check the entire archive **before target DDL**; do not extract over live
bytes. The target parent and existing files directory must be trusted regular
directories, not symlinks or mount roots that cannot be renamed. A new target
must have its own empty, owned `files/` directory prepared first. This example
runs as the service OS identity, which must own the staged tree.

```bash
test "$(docker inspect --format '{{.State.Running}}' "$LIMS_SOURCE_APP_CONTAINER")" = false
test "$(docker inspect --format '{{.State.Running}}' "$LIMS_RESTORE_APP_CONTAINER")" = false
LIMS_FILES_STAGE=$(mktemp -d "$LIMS_TARGET_WORKDIR/.files-restore-$LIMS_RECOVERY_SET.XXXXXX")
python3 - "$LIMS_RECOVERY_PRIVATE/files.tar.gz" <<'PY'
import pathlib, sys, tarfile
seen = set()
with tarfile.open(sys.argv[1], "r:gz") as archive:
    for member in archive:
        path = pathlib.PurePosixPath(member.name)
        if (path.is_absolute() or ".." in path.parts or "\\" in member.name
                or not (member.isdir() or member.isreg()) or member.issparse()):
            raise SystemExit("unsafe archive member")
        key = str(path)
        if key in seen or (key == "." and not member.isdir()):
            raise SystemExit("duplicate or invalid archive member")
        seen.add(key)
PY
tar --extract --gzip --file "$LIMS_RECOVERY_PRIVATE/files.tar.gz" \
  --directory "$LIMS_FILES_STAGE" --no-same-owner --no-same-permissions
( cd "$LIMS_FILES_STAGE"
  find . -type f -print0 | LC_ALL=C sort -z | xargs -0 -r sha256sum \
    > "$LIMS_RECOVERY_PRIVATE/staged-files.sha256"
)
cmp -s "$LIMS_RECOVERY_PRIVATE/files.sha256" "$LIMS_RECOVERY_PRIVATE/staged-files.sha256"
( cd "$LIMS_FILES_STAGE"
  find . -printf '%m %U %G %y %P\0' | LC_ALL=C sort -z \
    > "$LIMS_RECOVERY_PRIVATE/staged-files-stat.nul"
)
test ! -L "$LIMS_TARGET_WORKDIR/files"
test -d "$LIMS_TARGET_WORKDIR/files"
test "$(stat -c %d "$LIMS_FILES_STAGE")" = "$(stat -c %d "$LIMS_TARGET_WORKDIR/files")"
LIMS_FILES_RETAINED="$LIMS_TARGET_WORKDIR/files.before-$LIMS_RECOVERY_SET"
test ! -e "$LIMS_FILES_RETAINED"
test ! -L "$LIMS_FILES_RETAINED"
```

Privately compare staged owner/group/modes with `files-stat.nul` and the target
service identity. Extraction deliberately does not restore source numeric
ownership and applies the trusted umask. Apply only reviewed per-path ownership
or mode corrections needed for this target, then capture the staged stat manifest
again and approve it before DDL. Do not apply blanket recursive `chmod`/`chown`.
Verify the service can traverse directories and read the original files, and can
write its required local-storage directories before starting it.

## Restore only the approved schema

Only after the dump preview and complete file validation, run the private
`source-highwater.sql`, `target-highwater.sql` and approved
`target-recreate.sql` through the same `/nolog` stdin channel used above, keeping
separate logs. The latter explicitly drops/recreates **only the authorized
target owner**, with the privately captured/reviewed grants, quotas and password.
It must check the expected PDB/owner before destructive DDL. If it is a new
target, it must not drop the source owner. Oracle DDL commits implicitly; a later
failure does not roll back `DROP USER ... CASCADE`.

```bash
test "$(docker inspect --format '{{.State.Running}}' "$LIMS_SOURCE_APP_CONTAINER")" = false
test "$(docker inspect --format '{{.State.Running}}' "$LIMS_RESTORE_APP_CONTAINER")" = false
for phase in source-highwater target-highwater target-recreate; do
  docker exec -i "$LIMS_ORACLE_CONTAINER" sh -c \
    'exec "$ORACLE_HOME/bin/sqlplus" -s /nolog' \
    < "$LIMS_RECOVERY_PRIVATE/$phase.sql" \
    > "$LIMS_RECOVERY_PRIVATE/$phase.pre-restore.log" 2>&1
done
docker exec -i "$LIMS_ORACLE_CONTAINER" sh -eu -c '
  umask 077
  par=$(mktemp /tmp/lims-impdp.XXXXXX)
  trap '\''rm -f -- "$par"'\'' 0
  trap '\''exit 130'\'' HUP INT TERM
  cat > "$par"
  chmod 600 "$par"
  "$ORACLE_HOME/bin/impdp" "parfile=$par"
' < "$LIMS_RECOVERY_PRIVATE/import.par" \
  > "$LIMS_RECOVERY_PRIVATE/import-client.log" 2>&1
```

Inspect both import logs, object validity, expected schema/configuration/data
state, grants/quotas and resolved external access before starting the app. Run
the private target high-water query again and compare against the backup, source
pre-restore and target pre-restore observations:

```bash
docker exec -i "$LIMS_ORACLE_CONTAINER" sh -c \
  'exec "$ORACLE_HOME/bin/sqlplus" -s /nolog' \
  < "$LIMS_RECOVERY_PRIVATE/target-highwater.sql" \
  > "$LIMS_RECOVERY_PRIVATE/target-highwater.post-import.log" 2>&1
```

Any unexplained import error or sequence/counter rewind stops recovery; retain
the failed attempt. Apply a reviewed high-water-preserving
step if needed, then verify it. Do not reseed the restored bootstrap or initialize
another first administrator over restored identities.

## Swap matching files and verify HTTP recovery

Only after the schema/import/high-water checks succeed, swap the already
validated staging directory:

```bash
test "$(docker inspect --format '{{.State.Running}}' "$LIMS_SOURCE_APP_CONTAINER")" = false
test "$(docker inspect --format '{{.State.Running}}' "$LIMS_RESTORE_APP_CONTAINER")" = false
mv -T -- "$LIMS_TARGET_WORKDIR/files" "$LIMS_FILES_RETAINED"
mv -T -- "$LIMS_FILES_STAGE" "$LIMS_TARGET_WORKDIR/files"
```

Each rename occurs with the application stopped. The original tree remains
byte-preserved under `files.before-…`; retain it and the backup until acceptance
and the approved retention period finish. If the second rename fails, keep the
app stopped and restore the retained directory name before reassessing. A new
target must not rename or overwrite the source installation's files.

Start only the already prepared restore application whose immutable distribution,
private datasource/configuration, working-directory mount, file owner/modes and
unused Redis logical database have been checked. If restoring the same dedicated
installation, this starts the same app ID that was stopped:

```bash
test "$(docker inspect --format '{{.State.Running}}' "$LIMS_SOURCE_APP_CONTAINER")" = false
docker start "$LIMS_RESTORE_APP_CONTAINER" > "$LIMS_RECOVERY_PRIVATE/restore-start.log"
```

Wait for a nonempty parseable RSA key at `/open/security/public-key`, then log in
and read `/core/module/sys/logins/account` in that session. Through actual HTTP,
read the original notice/file IDs and verify state A, matching metadata, expected
file association/logs/audit and download SHA-256 A; B must not remain as the
restored current state. Record status, assertions and hashes privately, together with source/
target identity, input versions, SQL/Data Pump logs, high-water comparison and
cleanup. For a new target, the original source must remain at B with its bytes
unchanged by target recovery. Retain shared logs and issued sequence values.
Clean only the named fixtures through the approved original APIs. Source cleanup
for a different target must wait until the restore application is stopped and only the source is
resumed; keep one application running at a time and record both cleanup results.

A failed readback remains a failed recovery even if later cleanup succeeds.
An HTTP edit back to A is compensation, not proof of database rollback. A
successful bounded restore proves this matched input/target exercise only;
complete installation, transaction rollback, API closure and another-device
independent replay retain their own acceptance requirements.
