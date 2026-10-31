#!/usr/bin/env bash
# Production read-only filesystem metadata inventory; no byte copying or hashing.
set -euo pipefail
umask 077
export LC_ALL=C

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd -P)"
OUT_PARENT="$ROOT/private/t06-inventory"
die() { echo "[t06-filesystem-inventory] ERROR: $*" >&2; exit 1; }
usage() {
  echo "Usage: $0 --production-read-only --storage-snapshot-id ID [--host HOST] [--files-root PATH] [--equipt-root PATH]" >&2
  exit 2
}

readonly_flag=false
snapshot_id=""
host=sss
files_root=/data/LIMS/jiankeyuan/lims-czjky-emc/files
equipt_root=/data/LIMS/Equiptfile
while [ "$#" -gt 0 ]; do
  case "$1" in
    --production-read-only) readonly_flag=true; shift ;;
    --storage-snapshot-id) [ "$#" -ge 2 ] || usage; snapshot_id="$2"; shift 2 ;;
    --host) [ "$#" -ge 2 ] || usage; host="$2"; shift 2 ;;
    --files-root) [ "$#" -ge 2 ] || usage; files_root="$2"; shift 2 ;;
    --equipt-root) [ "$#" -ge 2 ] || usage; equipt_root="$2"; shift 2 ;;
    *) usage ;;
  esac
done
[ "$readonly_flag" = true ] || die "explicit --production-read-only is required"
[[ "$snapshot_id" =~ ^[A-Za-z0-9][A-Za-z0-9._:-]{2,127}$ ]] || die "storage snapshot id is malformed"
[[ "$host" =~ ^[A-Za-z0-9][A-Za-z0-9._-]{0,127}$ ]] || die "host is malformed"
for path in "$files_root" "$equipt_root"; do
  [[ "$path" =~ ^/[A-Za-z0-9._/:-]+$ ]] || die "root is malformed: $path"
done
command -v ssh >/dev/null 2>&1 || die "ssh is required"

[ ! -e "$OUT_PARENT" ] || [ ! -L "$OUT_PARENT" ] || die "inventory output parent must not be a symlink"
mkdir -p -m 700 "$OUT_PARENT"
[ "$(stat -c '%u' "$OUT_PARENT")" = "$(id -u)" ] || die "output parent owner mismatch"
(( (8#$(stat -c '%a' "$OUT_PARENT") & 0077) == 0 )) || die "output parent permissions are too broad"
timestamp="$(date -u +%Y%m%dT%H%M%SZ)"
out="$OUT_PARENT/$timestamp-filesystem"
[ ! -e "$out" ] || die "output already exists"
mkdir -m 700 "$out"

capture_root() {
  local name="$1" remote_root="$2" target="$out/$name.nul" nuls
  ssh -o BatchMode=yes -o ConnectTimeout=10 "$host" \
    "set -e; if [ ! -d '$remote_root' ] || [ -L '$remote_root' ]; then exit 1; fi; find -P '$remote_root' -xdev -type f -printf '%s\\0%T@\\0%p\\0'" \
    > "$target.tmp" 2> "$out/$name.stderr"
  mv "$target.tmp" "$target"
  nuls="$(tr -cd '\0' < "$target" | wc -c)"
  [ $((nuls % 3)) -eq 0 ] || die "remote metadata record framing is invalid: $name"
  printf '%s\n' $((nuls / 3)) > "$out/$name.count"
}

{
  printf 'format=lims-t06-filesystem-inventory-v1\n'
  printf 'started_utc=%s\n' "$(date -u +%Y-%m-%dT%H:%M:%SZ)"
  printf 'production_read_only=true\n'
  printf 'storage_snapshot_id=%s\n' "$snapshot_id"
  printf 'ssh_host=%s\n' "$host"
  printf 'files_root=%s\n' "$files_root"
  printf 'equipt_root=%s\n' "$equipt_root"
  printf 'find_options=-P -xdev -type f\n'
  printf 'record_format=bytes-nul-mtime_epoch-nul-absolute_path-nul\n'
  printf 'content_copy=false\ncontent_hash=false\n'
} > "$out/manifest.env"
capture_root files "$files_root"
capture_root equipt-acq "$equipt_root"
printf 'completed_utc=%s\n' "$(date -u +%Y-%m-%dT%H:%M:%SZ)" >> "$out/manifest.env"
echo "[t06-filesystem-inventory] PASS output=$out (restricted NUL metadata only)"
