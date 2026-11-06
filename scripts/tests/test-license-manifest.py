#!/usr/bin/env python3
import json, re
from pathlib import Path
p=Path(__file__).parents[1].parent/'.scratch/sunway-supplier-exit-api-first/evidence-archive/license-removal/manifest.json'
r=json.loads(p.read_text())
assert r['status']=='static-manifest' and r['runtime_reachability']=='unknown'
policy=r['scanner_policy']
assert 'license.lic' in policy['never_forbid_globally']
assert 'vendor.lic' in policy['never_forbid_globally']
assert 'cell-license.xml' in policy['never_forbid_globally']
assert all(x not in policy['forbidden_filenames'] for x in ['license.lic','vendor.lic','cell-license.xml'])
assert all(set(['owner','name','descriptor']) <= set(x) for x in policy['forbidden_methods'])
assert not any(x['owner'].startswith(('com.aspose.','com.spire.','com.zhuozhengsoft.')) for x in policy['forbidden_methods'])
assert any(x.get('path')=='private/lic/vendor.lic' and 'PageOffice' in x['reason'] for x in r['sunway_config_and_host_inputs'])
assert any(x['classification']=='retain-third-party-Aspose' for x in r['retained_third_party_or_business'])
assert any(x['classification']=='retain-business-field' for x in r['retained_third_party_or_business'])
text=p.read_text()
assert not re.search(r'(?i)(password|passwd|secret|token)\s*[=:]\s*[^,}\n]{8,}', text)
assert not re.search(r'(?m)^\s*[A-Za-z0-9+/]{80,}={0,2}\s*$', text)
for route in ['/core/module/sys/licenses','/open/security/licenses']:
 assert route in policy['forbidden_routes']
print('license manifest scope and redaction test PASS')

# A long opaque authorization fixture must be detected without retaining its content.
opaque='A'*160
assert re.search(r'(?m)^\s*[A-Za-z0-9+/]{80,}={0,2}\s*$', opaque)
