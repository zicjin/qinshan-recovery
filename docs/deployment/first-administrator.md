# One-time first-administrator installation

The source-only distribution includes `initialize-admin.sh`. It runs a command
in the application JAR and exits; it starts no Spring application or HTTP server.
Use it only after the reviewed structural and technical configuration phases
have been applied to a dedicated installation schema. The overall schema package
is still partial, so this command does not make a complete new-device restore
ready.

The operator supplies a private runtime properties file and private JSON on
stdin. Do not put credentials in command arguments, source control or a report:

```bash
export JAVA_HOME=/opt/jdk1.8.0_391
"$LIMS_DEPLOY_DIST/initialize-admin.sh" /private/runtime.properties \
  < /private/first-admin.json
```

The runtime file must explicitly contain `spring.datasource.url`,
`spring.datasource.username`, `spring.datasource.password` and
`sunway.user.password.encoder.type=SM3` or `MD5`, matching the application.
For the current Oracle 26 / JDBC 19.3 combination, provision the database login
with a compatible password no longer than 30 bytes; SQLPlus accepting a longer
password does not prove the shipped JDBC driver can authenticate it. This is
separate from the application's administrator password policy.

The JSON must contain exactly these fields:

| Field | Meaning |
| --- | --- |
| `expected_schema` | Exact uppercase connected Oracle user/schema; SYS and SYSTEM are refused |
| `user_id`, `user_name` | New login ID and display label; reserved built-in IDs are refused |
| `company_id`, `company_name` | New company identity; ID at most 16 ASCII bytes |
| `department_id`, `department_name` | Distinct department identity beneath the company |
| `role_name` | New installation administrator role label |
| `password` | Operator-chosen private application password, never a shipped default |
| `policy` | Explicit password policy object described below |

`policy` requires all fields: `min_length`, `max_length`, `min_number`,
`min_lowercase`, `min_uppercase`, `min_special_characters`, `composition`,
`max_failed_attempts`, `expiry_days`, `expiry_warning_days`, `history_length`,
`reuse_days`, `expired_policy` (`mandatory` or `optional`), and
`expired_max_uses`. Values are operator decisions, not production account values.
Both expiry and warning days must be positive and warning cannot exceed expiry;
the original service otherwise skips expiry checking for a zero warning.
The initializer invokes the original password policy service and configured
Java password encoder. SM3 uses the original implementation, not a replacement
hash algorithm.

The command verifies the target user, locks the identity tables without waiting,
requires them and the password-policy/history tables to be empty, and checks the
technical singleton/locale/menu prerequisites. It creates company, department,
one activated `sysAdmin`, policy 1, a role and membership, explicit full-menu
administrator grant, organization grants, password history and the project top
organization constant. Initial numeric IDs are allocated only in verified empty
identity tables. No production identity or grant is copied.

All installation writes use bind parameters in one transaction. Failure rolls
back the inserted identity records; a second run refuses existing identities.
The command performs no DDL, resets no sequence and overwrites no existing user.
It prints only a fixed status or safe rejection category. Keep the private input
and runtime configuration under operator access controls.

After installation, start the one application and perform real RSA login using
`GET /open/security/public-key` and `POST /core/module/sys/login`. A successful
login can return an empty body; read `/core/module/sys/logins/account` in the
same session to confirm principal, organization and roles. Verify real menus and
protected requests as well. Create subsequent accounts through the original
authenticated HTTP APIs, set their password and activate them; original user
creation deliberately starts accounts locked.

Give Redis an explicit `spring.redis.database` and matching URL. The observed
runtime ignored a URL-only `/15` selection and connected to DB 0. Installation
validation must use its own empty logical DB and confirm actual client DB; never
flush an existing application's DB. Oracle and Redis are reused, not restarted
or reimported for each check.

Wait for the actual public-key response to be nonempty and parseable as an RSA
X.509 key before attempting login. During ordered application-ready callbacks,
the LCDP registration filter can temporarily return an empty HTTP 200; status
alone is insufficient. Do not restart a still-initializing application because
of that observation. Retain startup diagnostics if a valid key never appears.
