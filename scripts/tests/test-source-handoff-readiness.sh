#!/usr/bin/env bash
# Exercise the static clean-source verifier using only temporary directories.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
VERIFY="${VERIFY_SOURCE_HANDOFF:-$ROOT/scripts/verify-source-handoff.py}"

tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT

make_tree() {
  local tree="$1"
  mkdir -p "$tree/docs" "$tree/project/src/main/java/example" \
    "$tree/reconstituted/framework" "$tree/scripts" "$tree/tools"
  printf '# recovered source\n' > "$tree/README.md"
  printf '# deployment\n' > "$tree/docs/owned-sss2-deployment.md"
  printf '<project/>\n' > "$tree/project/pom.xml"
  printf '<project/>\n' > "$tree/reconstituted/framework/pom.xml"
  printf 'class Example {}\n' > "$tree/project/src/main/java/example/Example.java"
  printf '#!/usr/bin/env bash\n' > "$tree/scripts/build-owned-distribution.sh"
  printf '#!/usr/bin/env bash\n' > "$tree/scripts/build-reconstituted-framework.sh"
  printf 'print("build")\n' > "$tree/scripts/build-source-only-application.py"
  printf 'print("checkpoint")\n' > "$tree/scripts/verify-source-only-checkpoint.py"
  printf 'print("native")\n' > "$tree/scripts/verify-owned-native-license-removal.py"
  printf '#!/usr/bin/env bash\n' > "$tree/scripts/refresh-owned-sss2-app.sh"
  printf '#!/usr/bin/env bash\n' > "$tree/scripts/rollback-owned-sss2-app.sh"
  printf 'print("inventory")\n' > "$tree/tools/source_inventory.py"
  chmod 755 "$tree/scripts/"*.sh
}

expect_fail() {
  local label="$1" expected="$2"
  shift 2
  local output status=0
  output="$("$@" 2>&1)" || status=$?
  if [ "$status" -eq 0 ]; then
    echo "source handoff test [$label]: expected failure" >&2
    exit 1
  fi
  if ! grep -Fq "$expected" <<<"$output"; then
    echo "source handoff test [$label]: expected '$expected'" >&2
    echo "$output" >&2
    exit 1
  fi
}

tree="$tmp/source"
receipt="$tmp/source-tree.attestation"
make_tree "$tree"

# Keep the verifier honest about its boundary: if it attempted to reach a
# runtime or production helper, these deliberately failing shims would abort.
fake_bin="$tmp/fake-bin"
mkdir "$fake_bin"
for command in docker ssh curl wget; do
  printf '#!/usr/bin/env bash\necho unexpected external command: %s >&2\nexit 99\n' "$command" > "$fake_bin/$command"
  chmod 755 "$fake_bin/$command"
done

before_tree="$(find "$tree" -printf '%y:%P:%m\n' | LC_ALL=C sort | sha256sum | awk '{print $1}')"

PATH="$fake_bin:$PATH" python3 "$VERIFY" --root "$tree" --emit-attestation > "$receipt"
chmod 600 "$receipt"
grep -Fxq 'FORMAT=lims-source-tree-v1' "$receipt"
grep -Eq '^ROOT_TREE_SHA256=sha256:[0-9a-f]{64}$' "$receipt"
grep -Eq '^PROJECT_TREE_SHA256=sha256:[0-9a-f]{64}$' "$receipt"
after_tree="$(find "$tree" -printf '%y:%P:%m\n' | LC_ALL=C sort | sha256sum | awk '{print $1}')"
[ "$before_tree" = "$after_tree" ]

output="$(python3 "$VERIFY" --root "$tree" --attestation "$receipt")"
grep -Fq 'source handoff readiness PASS' <<<"$output"

# The root digest deliberately includes the nested project tree, so either
# source layer is bound by the root receipt.  The nested digest prevents a
# replacement from being hidden by a separately altered root receipt.
printf 'class Changed {}\n' > "$tree/project/src/main/java/example/Example.java"
expect_fail "project changes invalidate root" \
  'root tree digest does not match the attestation' \
  python3 "$VERIFY" --root "$tree" --attestation "$receipt"

current="$tmp/current.attestation"
python3 "$VERIFY" --root "$tree" --emit-attestation > "$current"
awk -F= '
  $1 == "ROOT_TREE_SHA256" { print; next }
  $1 == "PROJECT_TREE_SHA256" { print old_project; next }
  { print }
' old_project="$(awk -F= '$1 == "PROJECT_TREE_SHA256" { print; exit }' "$receipt")" \
  "$current" > "$tmp/project-stale.attestation"
chmod 600 "$tmp/project-stale.attestation"
expect_fail "project receipt remains independent" \
  'project tree digest does not match the attestation' \
  python3 "$VERIFY" --root "$tree" --attestation "$tmp/project-stale.attestation"

make_tree "$tmp/with-git"
mkdir "$tmp/with-git/.git"
expect_fail "git metadata" 'forbidden handoff path component: .git' \
  python3 "$VERIFY" --root "$tmp/with-git" --emit-attestation

make_tree "$tmp/without-build"
rm "$tmp/without-build/scripts/build-owned-distribution.sh"
expect_fail "required source-only build" 'required source file is missing or not regular: scripts/build-owned-distribution.sh' \
  python3 "$VERIFY" --root "$tmp/without-build" --emit-attestation

make_tree "$tmp/with-private"
mkdir "$tmp/with-private/private"
expect_fail "private runtime inputs" 'forbidden handoff path component: private' \
  python3 "$VERIFY" --root "$tmp/with-private" --emit-attestation

make_tree "$tmp/with-config"
printf 'password=not-a-handoff-input\n' > "$tmp/with-config/application.properties"
expect_fail "runtime configuration" 'runtime configuration must not be handed off' \
  python3 "$VERIFY" --root "$tmp/with-config" --emit-attestation

make_tree "$tmp/with-link"
ln -s README.md "$tmp/with-link/README-link.md"
expect_fail "symbolic link" 'symbolic links are not allowed in a source handoff' \
  python3 "$VERIFY" --root "$tmp/with-link" --emit-attestation

make_tree "$tmp/with-writable-dir"
chmod 775 "$tmp/with-writable-dir/project/src"
expect_fail "shared writable source directory" 'source directory is group/other writable' \
  python3 "$VERIFY" --root "$tmp/with-writable-dir" --emit-attestation

chmod 644 "$receipt"
expect_fail "receipt mode" 'attestation must have mode 0600' \
  python3 "$VERIFY" --root "$tree" --attestation "$receipt"

make_tree "$tmp/inside-receipt"
inside_receipt="$tmp/inside-receipt/tree.attestation"
python3 "$VERIFY" --root "$tmp/inside-receipt" --emit-attestation > "$inside_receipt"
chmod 600 "$inside_receipt"
expect_fail "receipt cannot travel inside candidate" 'attestation must be outside the candidate source tree' \
  python3 "$VERIFY" --root "$tmp/inside-receipt" --attestation "$inside_receipt"

echo "source handoff readiness test PASS"
