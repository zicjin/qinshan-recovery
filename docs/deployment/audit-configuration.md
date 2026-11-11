# Original audit configuration

The finite core bootstrap includes the four current-production audit
configuration projections. These are deployment configuration, separate from
the workflow `T_CORE_MENU_EXT` rows whose type is `audit_def`.

| Table | Captured rows | Enabled | Disabled |
|---|---:|---:|---:|
| `T_CORE_AT_CONFIG` | 186 | 4 | 182 |
| `T_CORE_AT_CONFIG_LINE` | 3,038 | 3,037 | 1 |
| `T_CORE_AUDIT_CONFIG` | 6 | 2 | 4 |
| `T_CORE_AUDIT_CONFIG_COLUMN` | 106 | 0 | 106 |

All selected fields and source IDs are preserved. The three SQL configuration
fields are NULL in the captured source. No audit record contents, source users,
organizations, created-by fields or timestamps are installed. Original duplicate
field targets and stale table/column/parent references remain unchanged and are
classified in `bootstrap-provenance.json`; the installer does not invent their
missing targets or enable additional fields.

`70-bootstrap.sql` requires the selected configuration tables to be empty and
commits the seed once. Run `80-bootstrap-verify.sql` immediately afterwards,
before initializing identities or starting the application. These files belong
to the existing combined installation plan. Do not replay the bootstrap over a
running system: runtime configuration can legitimately change after startup.
The normal installer continues to reject the incomplete full-system package.

The two original Java audit paths have different controls:

- Old audit trail requires the `audit-trail` Spring profile, the corresponding
  `sunway.audit.insert`, `sunway.audit.update` or `sunway.audit.delete` operation
  switch, an enabled `T_CORE_AT_CONFIG` table and its enabled field switches.
  The operation must also run through an original `@AuditTrailEntry` service
  boundary. The public classpath defaults enable the three operation switches;
  private deployment properties can override them.
- New audit logs require the `audit` profile, operation switches, an actual
  `@Audit` context and their separate table/column configuration. Reading a
  configured service label from the audit root endpoint does not prove that an
  audit event or field change was recorded.

The captured production external configuration enables `audit-trail` and does
not enable `audit`; it does not override the three operation switches. Restoring
the configuration preserves this distinction. Adding the `audit` profile or
enabling new fields would be an explicit target policy change, not production
reproduction. No runtime profile is changed by this installation package.

Business acceptance must inspect real HTTP writes and entity readback, then the
matching audit records, actual principal/organization and field old/new values.
API compensation is not transaction rollback. Neither installing these rows nor
reading an audit root establishes full permissions, rollback, lifecycle, cleanup
or strict API acceptance.
