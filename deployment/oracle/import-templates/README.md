# Finite production-derived import template installation

This package carries one reviewed header-only `reportSendInfo` XLSX and its
source-bound file path. It also records the original `grjl` definition as
`source-no-file-and-no-mapped-columns`; no replacement workbook is invented.
The normal full-system SQL installer remains partial and guarded.

The matching core `70-bootstrap.sql` supplies the two original template
definitions, eleven column configuration rows and one technical `T_CORE_FILE`
row. Unmapped source configuration rows remain unmapped. The file row copies
no production authors, organization or download URL; its `TIMES=0` follows the
original fresh upload contract. Its technical creation date is retained because
the original file manager derives the on-disk date directory from it. `SIZE`
records the sanitized delivered asset size, rather than the original package
size. These are first-installation inputs, not an existing-schema upgrade.

The source workbook contains nine header cells, no filled business rows,
formulas, macros, hidden data or external relationships. The transformation
removes original authors/times, custom properties, original save path and
document/edit identifiers. Worksheet cells, styles, shared strings and theme
retain their source bytes except the explicitly removed sheet root edit-ID
attribute. `manifest.json` records original and delivered hashes plus each
part's disposition. Standard style extension identifiers are preserved.

From the standalone root, check the exact asset without private captures or
Oracle:

```bash
python3 scripts/install-import-template-assets.py --check-package
```

After applying and verifying the matching technical bootstrap, stop the target
application and choose its existing, absolute, writable working directory. For
the explicit local backend, set `owned.files.storage=local` in its private
runtime properties and persist that working directory's `files/` tree. Copy
the exact template into the location declared by the file metadata:

```bash
python3 scripts/install-import-template-assets.py --workdir /var/lib/lims-owned
```

The distribution carries the same script under `installation/scripts/` and
this package under `installation/deployment/oracle/import-templates/`. Run it
from any directory using an absolute script path. No parent checkout, private
capture, production file server, FTP credentials or supplier JAR is required.

The installer verifies the package before writing, refuses symbolic-link paths
and different existing bytes, and treats identical installed bytes as unchanged.
It executes no SQL and changes no file metadata in Oracle. Copying the file does
not populate a missing database bootstrap. It does not overwrite an existing
application's uploaded files or implement FTP migration.

Start the same application with that stable workdir. Use a real authenticated
session to query `POST /secure/core/module/item/import-tmpls/queries`, then
`POST /core/module/item/files/queries` with the selected template's `TARGETID`.
Download the returned, freshly signed `downloadUrl` in the same session and
verify the actual XLSX bytes. Do not store the URL in SQL or configuration.
Template visibility, a list 200 or an installed asset is not acceptance of
business import/export, permissions, audit, restoration or complete deployment.
