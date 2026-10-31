# Owned GikamWpjcApplication entrypoint smoke

Date: 2026-10-20 (Asia/Singapore)

`scripts/tests/test-owned-gikam-entrypoint.sh` compiles the actual recovered
`com.sunwayworld.emc.projectfunction.GikamWpjcApplication` source and the
existing personnel controller against the project-owned framework and pinned
public dependencies, then starts its real `main` method through
`SunwayApplication.run`. The smoke uses a non-web Spring context and excludes
datasource, Redis, security, and transaction-manager auto configuration because
this check has no application configuration, database, or cache endpoint
attached.

The returned context is active and discovers exactly one
`EmcEmpController` bean. The owned framework JAR also passes the
supplier-runtime rejection scan. No supplier JAR,
`sunway-app.so`, license input, production configuration, or network endpoint
is used.

This proves that the recovered application entrypoint compiles, reaches the
owned bootstrap boundary, and discovers the personnel page controller. It does
not prove authentication, authorization, Oracle/Redis wiring, or personnel
resource/service/DAO behavior;
those remain blocked by the unresolved application configuration and personnel
framework dependency graph recorded in
`evidence/personnel-owned-slice-frontier.md`.
