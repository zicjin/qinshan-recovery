# Owned runtime HTTP bootstrap

`test-owned-runtime-http.sh` starts a real embedded Tomcat process using
`SunwayApplication.run` and the owned `GikamCoreApplication` base. It serves
`GET /owned-runtime/health` from a controller in the project-owned internal
smoke package and verifies the response is `owned-runtime`.

The process uses only the owned framework classes and pinned public
dependencies. The test also runs the supplier-runtime rejection scan against
the owned artifact and its public dependency directory. This is bootstrap
evidence only; it does not claim that the full LIMS business application has
been migrated or accepted.
