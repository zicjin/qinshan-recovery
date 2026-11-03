#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat >&2 <<'EOF'
Usage: inspect-sunway-app-static.sh --input /path/to/sunway-app.so --output /path/to/dossier.md

This helper performs offline ELF inspection only. It never loads or executes the
input library and it does not read private configuration or license files.
EOF
}

input_path=""
output_path=""
expected_sha256="dc06e05d55252211e44b3a177267b68efc970b095d74c7ebd815057f5393d40a"
while (($#)); do
  case "$1" in
    --input)
      [[ $# -ge 2 ]] || { usage; exit 2; }
      input_path=$2
      shift 2
      ;;
    --output)
      [[ $# -ge 2 ]] || { usage; exit 2; }
      output_path=$2
      shift 2
      ;;
    --expected-sha256)
      [[ $# -ge 2 ]] || { usage; exit 2; }
      expected_sha256=$2
      shift 2
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      usage
      exit 2
      ;;
  esac
done

[[ -n "$input_path" && -n "$output_path" ]] || { usage; exit 2; }
[[ -f "$input_path" ]] || { echo "input is not a regular file: $input_path" >&2; exit 1; }
[[ "$expected_sha256" =~ ^[0-9a-fA-F]{64}$ ]] || { echo "expected SHA-256 is invalid" >&2; exit 2; }
command -v sha256sum >/dev/null || { echo "sha256sum is required" >&2; exit 1; }
command -v file >/dev/null || { echo "file is required" >&2; exit 1; }
command -v readelf >/dev/null || { echo "readelf is required" >&2; exit 1; }
command -v nm >/dev/null || { echo "nm is required" >&2; exit 1; }
command -v objdump >/dev/null || { echo "objdump is required" >&2; exit 1; }
command -v strings >/dev/null || { echo "strings is required" >&2; exit 1; }

output_dir=$(dirname "$output_path")
mkdir -p "$output_dir"
tmp_dir=$(mktemp -d "${TMPDIR:-/tmp}/sunway-app-static.XXXXXX")
trap 'rm -rf "$tmp_dir"' EXIT

sha256=$(sha256sum "$input_path" | awk '{print $1}')
if [[ "${sha256,,}" != "${expected_sha256,,}" ]]; then
  echo "input SHA-256 does not match the controlled artifact" >&2
  exit 1
fi
file_identity=$(file -b "$input_path")
grep -Eq 'ELF 64-bit LSB (shared object|x86-64)' <<<"$file_identity" || {
  echo "input is not the controlled ELF64 x86-64 shared object" >&2
  exit 1
}

{
  echo "# sunway-app.so static seam dossier"
  echo
  echo "This dossier is derived from offline inspection of one external binary. It is not runtime evidence and does not authorize loading or redistributing the binary."
  echo
  echo "## Target identity"
  echo
  printf '%s\n' "- Input identity: external controlled artifact (caller path redacted)"
  printf '%s\n' "- SHA-256: \`$sha256\`"
  printf '%s\n' "- File identity: \`$file_identity\`"
  printf '%s\n' '- Analysis mode: read-only `file`, `readelf`, `nm`, `objdump`, and filtered `strings`; the library was not executed.'
  echo
  echo "## ELF and loader facts"
  echo
  echo '```text'
  readelf -h "$input_path"
  echo '--- dynamic dependencies ---'
  readelf -d "$input_path" | grep -E 'NEEDED|SONAME|RPATH|RUNPATH|BIND_NOW|FLAGS' || true
  echo '--- program hardening markers ---'
  readelf -W -l "$input_path" | grep -E 'GNU_STACK|GNU_RELRO|LOAD' || true
  echo '--- sections ---'
  readelf -S "$input_path" | grep -E '\.(text|rodata|data|bss|eh_frame|dynsym|rela|init|fini)' || true
  echo '--- build note ---'
  readelf -n "$input_path" | grep -A2 -E 'Build ID|NT_GNU_BUILD_ID' || true
  echo '```'
  echo
  echo "The observed GNU stack segment is RW without execute permission and a GNU_RELRO segment is present. No BIND_NOW/DFLAGS marker was observed in the dynamic table; this is a static mitigation observation only."
  echo
  echo "## Export inventory"
  echo
  nm -D --defined-only "$input_path" > "$tmp_dir/exports.txt"
  export_count=$(wc -l < "$tmp_dir/exports.txt")
  jni_count=$(awk '$3 ~ /^Java_/' "$tmp_dir/exports.txt" | wc -l)
  echo "- Dynamic defined exports observed: **$export_count**"
  echo "- JNI-style exports observed: **$jni_count**"
  echo
  echo "Selected JNI exports (names are symbol identifiers, not proof of invocation):"
  echo
  echo '```text'
  awk '$3 ~ /^Java_(com_sunwayworld_boot_ApplicationConfiguration_(dataSource|redisTemplate|sqlSessionFactory)|com_sunwayworld_boot_ApplicationMvcConfiguration_(addResourceHandlers|addInterceptors)|com_sunwayworld_framework_tenant_TenantManager_dataSource|com_sunwayworld_framework_security_authentication_AuthenticationHelper_logout)$/' "$tmp_dir/exports.txt"
  echo '```'
  echo
  echo "## Filtered framework strings"
  echo
  echo "The following lines are category-level static indicators. License/MAC values and license bodies are intentionally redacted."
  echo
  echo '```text'
    strings -a "$input_path" \
    | grep -Ei 'Mac Address|globalLicense|DataSource|Redis|SqlSession|ResourceHandler|Interceptor|AuthenticationHelper|preLogout|loginLog|spring\.datasource' \
    | sed -E 's/.*([Ll][Ii][Cc][Ee][Nn][Ss][Ee]|[Mm][Aa][Cc][[:space:]]+[Aa][Dd][Dd][Rr][Ee][Ss][Ss]).*/[redacted security\/license token]/' \
    | sort -u
  echo '```'
  echo
  echo "## Focused disassembly excerpts"
  echo
  echo "The excerpts show JNI setup/call patterns only. They are not recovered source code. Addresses are valid for this exact SHA-256 and should not be reused for another binary."
  echo
  for spec in \
    "ApplicationConfiguration_dataSource:0xbffc:0xc176" \
    "ApplicationConfiguration_redisTemplate:0x5282:0x53c2" \
    "ApplicationConfiguration_sqlSessionFactory:0x60ac:0x61ef" \
    "ApplicationMvcConfiguration_addResourceHandlers:0xe11c:0xe25d" \
    "ApplicationMvcConfiguration_addInterceptors:0xfb8f:0xfccf" \
    "AuthenticationHelper_logout:0x15844:0x15a20" \
    "TenantManager_dataSource:0x168bb:0x168fc"; do
    name=${spec%%:*}
    range=${spec#*:}
    start=${range%%:*}
    stop=${range##*:}
    echo "### $name ($start..$stop)"
    echo
    echo '```asm'
    objdump -d -M intel --start-address="$start" --stop-address="$stop" "$input_path" \
      | sed -n '1,180p'
    echo '```'
    echo
  done
  echo "## Safety and interpretation limits"
  echo
  echo "- This report did not execute the shared object, attach a JVM, call JNI, or contact any application, database, supplier, or production endpoint."
  echo "- No complete native call graph was recovered; indirect calls, reflection, class-loader paths, and exception paths remain unknown."
  echo "- Static names such as license/MAC markers are deletion-boundary indicators, not business behavior proof."
  echo "- The binary is external provenance only. It must not enter the standalone owned distribution or become a supplier run-time dependency."
} > "$output_path"

chmod 0600 "$output_path"
echo "wrote $output_path"
