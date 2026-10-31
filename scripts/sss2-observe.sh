#!/usr/bin/env bash
# Record redacted observations from an sss2 writable clone.
# This helper never connects to a host, starts a container, or executes an
# application command. It only writes a private, replayable receipt.
set -euo pipefail
umask 077
export LC_ALL=C

usage() { cat >&2 <<'EOF'
Usage:
  scripts/sss2-observe.sh init <instance-id> <receipt-dir> [baseline-label]
  scripts/sss2-observe.sh record <receipt-dir> <feature-id> <status> <evidence-ref> [role] [data-baseline]
  scripts/sss2-observe.sh finalize <receipt-dir>
  scripts/sss2-observe.sh verify <receipt-dir>

The receipt directory must describe a writable sss2/test clone. Values are
metadata only: credentials, production endpoints, request bodies and database
rows are rejected. Use source-tagged mock/replay references for integrations.
EOF
  exit 2
}
die() { echo "sss2-observe: ERROR: $*" >&2; exit 1; }
valid_id() { [[ "${1:-}" =~ ^[a-z][a-z0-9-]{0,23}$ ]] || die "invalid instance id"; [[ "$1" != *prod* && "$1" != *production* && "$1" != sss* ]] || die "production instance ids are forbidden"; }
valid_dir() { [ -d "$1" ] && [ ! -L "$1" ] || die "receipt directory is missing or symlinked: $1"; local mode; mode="$(stat -c '%a' "$1")"; (( (8#$mode & 0077) == 0 )) || die "receipt directory must be private (mode 700)"; }
valid_text() { local v="$1"; [[ "$v" != *$'\n'* && "$v" != *$'\r'* && "$v" != *$'\t'* ]] || die "metadata contains a control character"; [[ "$v" != *..* ]] || die "path traversal is forbidden"; [[ "$v" != *password* && "$v" != *token* && "$v" != *secret* && "$v" != *Authorization* ]] || die "credential-like metadata is forbidden"; [[ "$v" != *10.100.* && "$v" != *218.93.* && "$v" != *192.168.* ]] || die "production endpoint is forbidden"; }
valid_status() { case "$1" in static-present|menu-observed|page-opened|read-flow-verified|write-flow-verified|permission-checked|disabled-in-isolated-env|unknown) ;; *) die "invalid status: $1" ;; esac; }
set -f
cmd="${1:-}"; shift || true
case "$cmd" in
  init)
    [ "$#" -ge 2 ] && [ "$#" -le 3 ] || usage
    instance="$1"; out="$2"; baseline="${3:-unbound-baseline}"
    valid_id "$instance"; valid_text "$baseline"
    [ ! -e "$out" ] || die "receipt path already exists; choose a new rollback receipt"
    mkdir -m 700 -p "$out"
    printf 'format=lims-sss2-observation-v1\ninstance_id=%s\nenvironment=sss2-writable-clone\nproduction_read_only=true\nexternal_side_effects=blocked\nbaseline=%s\ncreated_utc=%s\n' "$instance" "$baseline" "$(date -u +%Y-%m-%dT%H:%M:%SZ)" > "$out/metadata.env"
    : > "$out/observations.tsv"; chmod 600 "$out/metadata.env" "$out/observations.tsv"
    echo "initialized sss2 receipt: $out"
    ;;
  record)
    [ "$#" -ge 4 ] && [ "$#" -le 6 ] || usage
    out="$1"; feature="$2"; status="$3"; evidence="$4"; role="${5:-unspecified}"; data="${6:-unspecified}"
    valid_dir "$out"; [ -f "$out/metadata.env" ] && [ -f "$out/observations.tsv" ] || die "not an initialized receipt"
    [[ "$feature" =~ ^[a-z][a-z0-9_.-]+$ ]] || die "invalid feature id"; valid_status "$status"; valid_text "$feature"; valid_text "$evidence"; valid_text "$role"; valid_text "$data"
    [[ "$evidence" == evidence/* || "$evidence" == replay/* || "$evidence" == fixture/* ]] || die "evidence must be a redacted repo, replay, or fixture reference"
    printf '%s\t%s\t%s\t%s\t%s\t%s\n' "$(date -u +%Y-%m-%dT%H:%M:%SZ)" "$feature" "$status" "$evidence" "$role" "$data" >> "$out/observations.tsv"
    chmod 600 "$out/observations.tsv"
    ;;
  finalize)
    [ "$#" = 1 ] || usage; out="$1"; valid_dir "$out"; [ -f "$out/metadata.env" ] && [ -f "$out/observations.tsv" ] || die "not an initialized receipt"
    awk -F '\t' 'NF != 6 { bad=1 } END { if (bad) exit 1 }' "$out/observations.tsv" || die "malformed observation row"
    tmp="$out/summary.env.tmp.$$"; { echo format=lims-sss2-observation-summary-v1; echo observation_count="$(wc -l < "$out/observations.tsv")"; for state in static-present menu-observed page-opened read-flow-verified write-flow-verified permission-checked disabled-in-isolated-env unknown; do printf '%s_count=%s\n' "$state" "$(awk -F '\t' -v s="$state" '$3 == s {n++} END {print n+0}' "$out/observations.tsv")"; done; echo receipt_sha256="$(sha256sum "$out/observations.tsv" | awk '{print $1}')"; } > "$tmp"; chmod 600 "$tmp"; mv "$tmp" "$out/summary.env"; echo "finalized sss2 receipt: $out/summary.env"
    ;;
  verify)
    [ "$#" = 1 ] || usage; out="$1"; valid_dir "$out"; [ -f "$out/metadata.env" ] && [ -f "$out/observations.tsv" ] && [ -f "$out/summary.env" ] || die "receipt is incomplete"; grep -Fxq 'environment=sss2-writable-clone' "$out/metadata.env" || die "receipt is not sss2"; grep -Fxq 'production_read_only=true' "$out/metadata.env" || die "read-only production guard missing"; grep -Fxq 'external_side_effects=blocked' "$out/metadata.env" || die "external side-effect guard missing"; awk -F '\t' 'NF != 6 { bad=1 } END { exit bad ? 1 : 0 }' "$out/observations.tsv" || die "malformed observation row"; expected="$(awk -F= '$1 == "receipt_sha256" {print $2}' "$out/summary.env")"; actual="$(sha256sum "$out/observations.tsv" | awk '{print $1}')"; [ "$expected" = "$actual" ] || die "receipt hash mismatch"; echo "sss2 receipt valid: $out"
    ;;
  *) usage ;;
esac
