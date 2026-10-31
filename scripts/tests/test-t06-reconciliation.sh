#!/usr/bin/env bash
# Contract test for the local-only T06 DB/files metadata reconciliation helper.
set -euo pipefail
umask 077

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd -P)"
HELPER="$ROOT/tools/t06-reconcile/reconcile.py"
[ -f "$HELPER" ] || { echo "t06 reconciliation test: helper is missing" >&2; exit 1; }
HELPER="$HELPER" python3 - <<'PY'
import os
from pathlib import Path

source = Path(os.environ["HELPER"]).read_text(encoding="utf-8")
compile(source, os.environ["HELPER"], "exec")
PY

case "$ROOT" in /home/ts/qinshan/lims-recovery) ;; *)
  echo "t06 reconciliation test: unexpected checkout root" >&2; exit 1 ;;
esac

tmp="$(mktemp -d "$ROOT/private/t06-reconcile-test.XXXXXX")"
trap 'rm -rf "$tmp"' EXIT
chmod 700 "$tmp"
db="$tmp/db"
fs="$tmp/fs"
out="$tmp/out"
mkdir -m 700 "$db" "$fs"

printf '%s\n' \
  'database_writes=false' \
  'content_lobs_selected=false' \
  'inventory_completed_utc=2026-09-29T00:00:00Z' \
  'consistency_mode=flashback-scn' \
  'flashback_scn=12345' \
  'consistency_reference=fixture-snapshot' > "$db/summary.txt"
printf '%s\n' \
  $'ID\tTARGETID\tSIZE\texpected_relative_path\ttarget_class' \
  $'1\tT_EMC_EMP$1\t10\tfiles/secure/20240101/a/ok.txt\tknown-table-prefix' \
  $'2\tT_EMC_EMP$2\t20\tfiles/secure/20240101/b/missing.txt\tknown-table-prefix' \
  $'3\tT_EMC_EMP$3\t30\tfiles/secure/20240101/c/mismatch.txt\tknown-table-prefix' \
  $'4\tT_EMC_EMP$4\t7\tfiles/secure/20240101/d/tab\\tand\\nline.txt\tknown-table-prefix' \
  $'5\tT_EMC_EMP$5\t\t\tknown-table-class' \
  $'6\tX_DELETED$6\t4\tfiles/secure/20240101/e/deleted.txt\tsoft-delete-marker' \
  $'7\tLEGACY$7\t5\tfiles/secure/20240101/f/legacy.txt\tunknown-or-legacy-prefix' > "$db/t_core_file.tsv"
printf '%s\n' \
  $'ID\tSOURCEFILENAME\tTARGETFILENAME\texpected_relative_path' \
  $'9\tinput.txt\tprocessed.txt\t' > "$db/t_emc_equipt_acq_file.tsv"
chmod 600 "$db/summary.txt" "$db/t_core_file.tsv" "$db/t_emc_equipt_acq_file.tsv"

printf '%s\n' \
  'format=lims-t06-filesystem-inventory-v1' \
  'production_read_only=true' \
  'storage_snapshot_id=fixture-snapshot' \
  'completed_utc=2026-09-29T00:00:00Z' \
  'files_root=/fixture/files' \
  'equipt_root=/fixture/equipt' \
  'find_options=-P -xdev -type f' \
  'record_format=bytes-nul-mtime_epoch-nul-absolute_path-nul' \
  'content_copy=false' \
  'content_hash=false' > "$fs/manifest.env"
printf '10\0%s\0%s\0' '1.0000000000' '/fixture/files/secure/20240101/a/ok.txt' > "$fs/files.nul"
printf '31\0%s\0%s\0' '2.0000000000' '/fixture/files/secure/20240101/c/mismatch.txt' >> "$fs/files.nul"
printf '7\0%s\0%s\0' '3.0000000000' $'/fixture/files/secure/20240101/d/tab\tand\nline.txt' >> "$fs/files.nul"
printf '4\0%s\0%s\0' '4.0000000000' '/fixture/files/secure/20240101/z/orphan.txt' >> "$fs/files.nul"
printf '4\0%s\0%s\0' '6.0000000000' '/fixture/files/secure/20240101/e/deleted.txt' >> "$fs/files.nul"
printf '5\0%s\0%s\0' '7.0000000000' '/fixture/files/secure/20240101/f/legacy.txt' >> "$fs/files.nul"
printf '8\0%s\0%s\0' '5.0000000000' '/fixture/equipt/raw-input.dat' > "$fs/equipt-acq.nul"
chmod 600 "$fs/manifest.env" "$fs/files.nul" "$fs/equipt-acq.nul"

python3 "$HELPER" --db-inventory "$db" --filesystem-inventory "$fs" --output "$out" >/dev/null

[ "$(stat -c '%a' "$out")" = 700 ] || { echo "t06 reconciliation test: output directory is not private" >&2; exit 1; }
for file in summary.env matches.tsv missing.tsv possible-orphans.tsv size-mismatches.tsv unresolved.tsv; do
  [ "$(stat -c '%a' "$out/$file")" = 600 ] || { echo "t06 reconciliation test: $file is not private" >&2; exit 1; }
done
grep -Fxq 'matched_references=3' "$out/summary.env"
grep -Fxq 'missing_db_references=1' "$out/summary.env"
grep -Fxq 'possible_orphan_files=1' "$out/summary.env"
grep -Fxq 'size_mismatches=1' "$out/summary.env"
grep -Fxq 'unresolved_mappings=7' "$out/summary.env"
grep -Fq $'secure/20240101/b/missing.txt\tT_CORE_FILE' "$out/missing.tsv"
grep -Fq $'secure/20240101/c/mismatch.txt\t/fixture/files/secure/20240101/c/mismatch.txt' "$out/size-mismatches.tsv"
grep -Fq $'secure/20240101/z/orphan.txt\t/fixture/files/secure/20240101/z/orphan.txt' "$out/possible-orphans.tsv"
grep -Fq $'tab\\tand\\nline.txt' "$out/matches.tsv"
grep -Fq 'T_EMC_EQUIPT_ACQ_FILE has no safe storage locator in v1' "$out/unresolved.tsv"
grep -Fq 'no-storage-mapping-contract' "$out/unresolved.tsv"
grep -Fq 'excluded-target-class' "$out/unresolved.tsv"
grep -Fq 'soft-delete-marker' "$out/unresolved.tsv"
grep -Fq 'unknown-or-legacy-prefix' "$out/unresolved.tsv"

bad="$tmp/bad"
mkdir -m 700 "$bad"
cp "$fs/manifest.env" "$bad/manifest.env"
printf '10\0bad\0' > "$bad/files.nul"
printf '' > "$bad/equipt-acq.nul"
chmod 600 "$bad/manifest.env" "$bad/files.nul" "$bad/equipt-acq.nul"
if python3 "$HELPER" --db-inventory "$db" --filesystem-inventory "$bad" --output "$tmp/bad-output" >/dev/null 2>&1; then
  echo "t06 reconciliation test: malformed NUL triples were accepted" >&2
  exit 1
fi

if rg -n -- '^(import|from) .*\b(socket|subprocess|urllib|requests)\b|\b(os\.(system|popen)|subprocess\.|socket\.)' "$HELPER" >/dev/null; then
  echo "t06 reconciliation test: helper contains an external-access primitive" >&2
  exit 1
fi

echo "t06 reconciliation test PASS"
