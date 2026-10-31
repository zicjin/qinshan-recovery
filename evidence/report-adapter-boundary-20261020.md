# Report adapter boundary

Historical source used for the bounded path:

- `project/src/main/java/com/sunwayworld/emc/module/sys/report/mapper/conf/emc-report-config-line-oracle-mapper.xml`
- `project/src/main/resources/templates/emc/module/index/workspace/emc-report.html`
- `project/src/main/resources/static/emc/module/sys/reports/emc-report-common-list.js`

Those materials establish report-line parameter mapping, active flag, user and
role grants, and the FanRuan/Reporting Service categories. The owned adapter
keeps the LIMS page and permission boundary while replacing the external call
with a source-marked, digest-addressed local replay. It does not claim
third-party service equivalence.

`OwnedReportReplayClient` accepts only `owned.report.replay.mode=replay` and an
explicit local replay root. The test runner uses no host port or bind mount for
Oracle and removes the disposable container, volume, network, and browser
session after each run.

Third-party authorization materials are retained as a separate boundary. No
Sunway license file, native library, license API, or credential is added to the
owned report module or its output.
