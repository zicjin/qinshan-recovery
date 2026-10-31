#!/usr/bin/env bash
set -euo pipefail
if [ "$#" -lt 1 ]; then echo "usage: $0 <artifact-or-directory>..." >&2; exit 2; fi
python3 - "$@" <<'PY'
import io, sys, zipfile
from pathlib import Path
TOKENS=(b'sunway-app.so',b'sunway.app.license',b'com/sunwayworld/module/sys/license',b'/core/module/sys/licenses',b'/open/security/licenses',b'com/sunwayworld/framework/security/bean/CoreLicenseDTO',b'com/sunwayworld/boot/thirdparty/sunwaylibrary/SunwayLibraryExtractor',b'getLicenseDeadline',b'getLicenseCountdownDays',b'getLoginUserLimitation',b'getNumberOfOnlineUsers',b'third-party/sunway-app/',b'licenseDeadline',b'licenseCountdown',b'loginUserLimitation',b'numberOfOnlineUsers',b'SYSTEM_MAC_ADDRESS_LIST',b'license-machine')
PRIVATE=('gikam-core-','private-sdk','sunway-','lims2-report-template-')
FORBIDDEN_FILES={'sunway-app.so','sunway-app.dll','sunway-app.o','license-machine.env'}
ARCHIVES={'.jar','.war','.zip','.ear'}
def reject(reason):
 print('supplier runtime or vendor restriction residue found: '+reason,file=sys.stderr); raise SystemExit(1)
def inspect_bytes(label,data):
 for token in TOKENS:
  if token in data: reject(label+' contains '+token.decode('ascii','replace'))
def inspect_archive(label,data):
 try: archive=zipfile.ZipFile(io.BytesIO(data))
 except zipfile.BadZipFile: reject(label+' is an invalid archive')
 for info in archive.infolist():
  name=info.filename; base=Path(name).name.lower()
  if base in FORBIDDEN_FILES: reject(label+' contains '+name)
  if any(marker in base for marker in PRIVATE) and base.endswith('.jar'): reject(label+' contains supplier jar '+name)
  member=archive.read(name); inspect_bytes(label+':'+name,member)
  if Path(name).suffix.lower() in ARCHIVES: inspect_archive(label+':'+name,member)
def inspect_path(path):
 if path.is_dir():
  for child in path.rglob('*'):
   if child.is_dir(): continue
   base=child.name.lower()
   if base in FORBIDDEN_FILES: reject(str(child))
   if any(marker in base for marker in PRIVATE) and base.endswith('.jar'): reject(str(child))
   data=child.read_bytes(); inspect_bytes(str(child),data)
   if child.suffix.lower() in ARCHIVES: inspect_archive(str(child),data)
 else:
  base=path.name.lower()
  if base in FORBIDDEN_FILES: reject(str(path))
  if any(marker in base for marker in PRIVATE) and base.endswith('.jar'): reject(str(path))
  data=path.read_bytes(); inspect_bytes(str(path),data)
  if path.suffix.lower() in ARCHIVES: inspect_archive(str(path),data)
for argument in sys.argv[1:]:
 target=Path(argument)
 if not target.exists(): print('missing scan target: '+argument,file=sys.stderr); raise SystemExit(2)
 inspect_path(target)
print('supplier runtime rejection scan passed: '+' '.join(sys.argv[1:]))
PY
