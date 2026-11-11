# Targeted configuration upgrades

These scripts upgrade an existing **owned test or deployment schema**. They do
not bypass the partial new-device installer and never target production.
Back up the target first. Stop active verification and refresh the application
after the transaction commits so its language cache reloads the committed rows.
Oracle and Redis need not restart; no data import or Redis flush is required.

`20261111-complete-i18n.sql` restores the reviewed production-derived technical
language configuration in `../core-schema/i18n-runtime-source.json`. It matches
definitions by exact code and messages by exact code plus locale, preserves all
existing nonempty translations and definition metadata, and fills only absent
or empty messages. Existing locale configuration must already exist. Conflicting
source translations and the removed supplier-license capability are excluded
in the source manifest; no arbitrary winner or invented English text is added.

Run SQL*Plus as the intended application schema using private connection input:

```sql
@deployment/oracle/upgrades/20261111-complete-i18n.sql
```

The script locks both language tables before allocating IDs. The definition
allocation ceiling includes existing message references, so old orphan messages
cannot become attached to new definitions. Source IDs are provenance; new
runtime IDs are allocated above the target's occupied IDs without resetting a
sequence or copying production identities. Ambiguous target code/locale rows
fail the entire transaction. Re-running changes no already populated values.
Only the two language tables receive writes. No business entity, permission,
user, role, workflow instance or audit record is copied or modified.

The upgrade is part of the standalone checkout. Fresh empty installations use
the source-pinned `70-bootstrap.sql` and exact `80-bootstrap-verify.sql` instead.
This language repair does not establish complete installation or API closure.
