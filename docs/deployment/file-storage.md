# Local attachment storage

The original `CoreFileManager` implementation uploads, downloads and copies
attachment bytes through FTP. The original `files/` tree is its local download
cache. An explicit local adapter now lets a new installation use that same tree
as its persistent attachment store inside the one Spring application:

```properties
owned.files.storage=local
```

Put the setting in the operator's private runtime properties file. With the
setting absent or set to `ftp`, the original FTP implementation remains selected.
With `local`, `LocalCoreFileManager` is the primary `CoreFileManager` bean in the
same application context; the original FTP bean remains present. No additional
server, route, HTTP bridge or FTP process is required. This is an explicit owned
storage adapter, not evidence that production used a local upload backend.

The production configuration-key capture contains
`sunway.config.file.ftp.ip`, `.port`, `.user`, `.password`,
`sunway.config.file.server.url` and `sunway.config.cloud.file-server.url`.
The reconstructed FTP properties also support `sunway.config.file.ftp.folder`.
Those keys do not select local storage. `sunway.equipt.acq.file.local.path` is
an equipment acquisition setting, not the general attachment storage root.
No production configuration values or endpoints are part of this document.

Use the stable writable application working directory documented in the
[clean-device deployment contract](clean-device-contract.md). Local attachment
bytes, multipart temporary files and original download handlers share
`${user.dir}/files`. Persist that directory across application refreshes; do not
place mutable files in an immutable distribution directory. Do not point
`user.dir` at a symlink. All existing parent directories and the storage root
must be regular directories controlled by the service's OS identity or trusted
operators. Mount the persistent directory directly rather than through a symlink.
The adapter rejects existing symbolic links and path traversal; its path-based
checks do not protect against a privileged or locally authorized process
replacing parent directories concurrently.

The adapter retains the original layout:
`files/{scope}/{timestamp}/{irregularName}/{fileName}`. Original three-argument
descriptors omit the timestamp; FineReport omits it regardless of the descriptor.
It preserves the original URL encoding, optional file-access signature and
token handling, static resource mapping, authentication and permission filters.
Setting `owned.files.storage` does not enable or disable
`sunway.security.file-access-permission.enabled`. Install the matching safe
`FILE-EXTENSIONS` and `FILE-SPACELIMIT` technical constants and configure the
original `spring.servlet.multipart.max-file-size`,
`spring.servlet.multipart.max-request-size` and
`spring.servlet.multipart.file-size-threshold` as required by the deployment.

Uploads and copies stage complete bytes in the destination directory and require
an atomic replacement. A failed input read leaves the previous file intact and
removes its temporary file. Missing or non-regular sources fail explicitly,
including requests whose source and destination are equal. Existing regular
same-file operations preserve the bytes. A confirmed missing local source raises
an explicit missing-file signal; the original file download filter maps only that
signal to HTTP 404. FTP connection failures, inaccessible/invalid paths and
nonregular files keep their original failure behavior. The filter does not catch
exceptions from subsequent security or resource handlers. No placeholder file is
created, and file-access signatures and permissions remain unchanged. The caller retains ownership of an
`InputStream` supplied directly to the manager.

The original upload service, metadata writes, file logs, events and API responses
continue to run. Original DELETE marks `TARGETID` with an `X` prefix and writes
its original logs/events; it does not physically remove stored bytes. This
adapter preserves that retention behavior. A later database rollback also does
not automatically undo a completed filesystem write. HTTP verification must
observe metadata, file logs/audit, bytes and explicit fixture cleanup separately.
Provision and restore existing attachment bytes as a separate installation
input; copying only metadata does not restore the files.

Run the focused regression after the source-only framework prerequisites have
been built, using the public Maven artifact
`org.springframework:spring-test:5.2.9.RELEASE` from Maven Central or the
controlled artifact store. The runner verifies SHA-256
`aa16626fc8a56e7c05a55735f73c92c4ae3793f347c6713eec3ff3615430b5ee`:

```bash
python3 scripts/tests/test-local-core-file-manager.py \
  --spring-test /path/to/spring-test-5.2.9.RELEASE.jar
```

`--prerequisite-root /path/to/existing/source-only/checkout` may reuse an existing
JDK, source-built framework JARs and controlled public dependency cache. Compilation
and real-file checks run only in a temporary directory and start no application,
database or FTP service. The suite checks storage bytes, failed writes, paths,
symbolic links, original signed URLs and Spring bean selection. It is not a
replacement for the new-device real HTTP upload, list/detail, download, delete,
audit and cleanup gate or full source-only packaging.
