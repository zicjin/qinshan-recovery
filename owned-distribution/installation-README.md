# Oracle installation materials: partial and guarded

This directory carries the public source SQL, metadata and offline verifiers for
the selected Oracle installation plan. It is delivered with the matching owned
application distribution. It needs Python 3.10 or later and no parent checkout, private
acquisition files, supplier runtime JAR or historical SQL directory.

From the distribution root, check the shipped installation integrity:

```bash
python3 installation/scripts/verify-combined-oracle-installation.py --check-plan
```

The result must have `plan_valid: true`, `status: blocked`,
`deployment_ready: false` and `complete_external_domains: false`. Exit 0 for this
explicit integrity check does not authorize deployment. Without `--check-plan`,
the same command exits 2 and stops deployment. The normal SQL entry,
`installation/deployment/oracle/install.sql`, unconditionally raises ORA-20100
before any child SQL or DDL. Do not edit or bypass its guard.

The finite plan checks one dedicated empty connected user and captured database
character sets, then orders the external ten-table package and its verifier
before the core 739-table package. Core phases include the technical bootstrap
and its exact verifier before administrator initialization or application startup.
The final read-only verifier checks the exact combined logical object sets,
valid objects/indexes and no database links. The child package manifests and
installation manifest pin every required SQL/metadata input. The distribution
manifest separately hashes these manifests, verifier scripts and this document.

Complete external logical domains, grants, remaining configuration/workflow/LCDP
closure and full-system new-device restoration remain incomplete. Source-only
packaging and the installation integrity check do not establish application or
API acceptance. The included phase files support controlled disposable-schema
diagnostics only; they are not an upgrade or data-preserving migration. Oracle
DDL commits implicitly. Preserve SQLPlus logs and explicitly clean up failed
diagnostic users; transaction rollback does not undo DDL.

The distribution's sibling `initialize-admin.sh` command remains a separate
one-time command requiring the prepared schema and technical bootstrap. Private
environment properties, passwords and installer identity JSON are operator inputs
and are never included here. Startup uses the existing one Spring application,
Oracle and Redis; carrying these files changes no runtime behavior.

`deployment/oracle/workflow/` separately carries the symbolic workflow package:
`definitions.json`, its source-bound manifest and its finite instructions. The
workflow manifest remains partial and `deployment_ready=false`; its counts are
source material counts, not successful target installation or API acceptance.
The distribution manifest hashes all three files and records those exact counts.
The combined structural SQL plan does not install these workflow definitions.

The sibling `initialize-workflows.sh` launcher invokes the finite Java installer
from the application JAR. It requires a separately prepared schema and private
target identity mapping; candidate role/user IDs, display labels, organization
scopes and technical installer authorship are target inputs. Missing mappings
must reject before writes. Do not copy the private source-symbol map into the
bundle, and do not substitute the initial administrator for all candidates.
Seven latest source payloads remain explicitly blocked; older versions do not
replace them. The public launcher accepts runtime properties as its only argument and reads
private workflow inputs from stdin:

```bash
./initialize-workflows.sh /private/runtime.properties < /private/workflows.json
```

It resolves the bundled workflow manifest internally. Keep both private files
outside the distribution and source control. Follow the workflow package's
instructions and installer validation for the private JSON contract. Real HTTP
organization/candidate/notification/lifecycle/audit/rollback/cleanup acceptance
and complete workflow restoration remain outstanding.

`deployment/oracle/import-templates/` carries one reviewed header-only import
workbook, its original/delivered hash provenance and an explicit source-empty
template blocker. The matching core bootstrap seeds its template/config/file
metadata. Before application startup, install the bytes into the target's
existing persistent application working directory, using the application account:

```bash
python3 installation/scripts/install-import-template-assets.py --check-package
python3 installation/scripts/install-import-template-assets.py --workdir /var/lib/lims-owned
```

The command performs no SQL, refuses conflicting files and symbolic links, and
does not overwrite an existing application's uploads. Select
`owned.files.storage=local` for this local file path and keep the same working
directory across starts. Full file/bootstrap/business acceptance remains
separate; see the bundled import-template README.
