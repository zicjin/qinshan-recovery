#!/usr/bin/env bash
# Create a code-regression receipt tied to an sss2 observation receipt.
set -euo pipefail
umask 077
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd -P)"
OBS="$ROOT/scripts/sss2-observe.sh"
usage() { echo "Usage: scripts/sss2-code-regression.sh <receipt-dir> <feature-id> <before-ref> <after-ref> <evidence-ref>" >&2; exit 2; }
[ "$#" = 5 ] || usage
receipt="$1"; feature="$2"; before="$3"; after="$4"; evidence="$5"
[[ "$before" != *..* && "$after" != *..* ]] || { echo "ref traversal is forbidden" >&2; exit 1; }
[[ "$before" != *password* && "$after" != *token* ]] || { echo "secret-like ref is forbidden" >&2; exit 1; }
[ -d "$receipt" ] || { echo "receipt directory missing" >&2; exit 1; }
"$OBS" record "$receipt" "$feature" write-flow-verified "$evidence" code-regression "before=$before,after=$after"
"$OBS" finalize "$receipt"
printf 'code-regression=recorded\nbefore_ref=%s\nafter_ref=%s\n' "$before" "$after"
