# sss2 owned bootstrap smoke deployment

Date: 2026-10-20 (Asia/Singapore)

An ephemeral image built only from the owned framework classes, pinned public
dependencies, and the smoke controller was loaded on `sss2`. The image passed
the supplier-runtime rejection scan before transfer. It ran as
`lims-owned-bootstrap-s1` on the existing `internal=true` network
`lims-replay-demo-s1-net`.

Observed runtime facts:

- image digest: `sha256:8417c0e70d1b9541b3d46a8c2501d1d19378b673e6b2ecfb4985f796eb4fccce`
- container IP: `10.87.0.5`
- direct request from the `sss2` host to `http://10.87.0.5:18082/owned-runtime/health`
  returned HTTP 200 with body `owned-runtime`
- the existing application, Oracle, and Redis containers remained running

The smoke container and image were removed immediately after the read-only
HTTP check. This proves owned bootstrap execution inside the `sss2` isolated
network; it is not LIMS business, authentication, database, or page acceptance.
