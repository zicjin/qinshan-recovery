#!/usr/bin/env python3
"""Read-only preparation and explicit publication of proof-bound local cleanup.

Private captures stay outside Git. SHA custody proves integrity and linkage, not
remote authenticity or absence of changes between sequential observations.
No command in this module starts, stops or recreates an application/database.
"""
from __future__ import annotations
import argparse
import base64
import csv
import datetime as dt
import hashlib
import importlib.util
import json
import os
from pathlib import Path
import tempfile
from types import SimpleNamespace

from source_binding import capture_binding, runtime_contract_digest
import local_schema_policy as policy

STORE_ENV = 'SUNWAY_LIFECYCLE_PROOF_STORE'
CONTEXT = ('batch_id', 'milestone_id', 'clone_identity', 'application_binding',
           'harness_binding', 'runtime_contract_binding', 'artifact_sha256')
REGISTRY = ('artifact_ref', 'sha256', 'batch_id', 'context_sha256', 'retention')


def verifier():
    spec = importlib.util.spec_from_file_location('lifecycle_verifier', Path(__file__).with_name('verify-local-runtime-milestone.py'))
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


def require(ok, reason):
    if not ok:
        raise ValueError(reason)


def digest(raw):
    return hashlib.sha256(raw).hexdigest()


def encoded(value):
    return json.dumps(value, sort_keys=True, separators=(',', ':'), ensure_ascii=True).encode()


def read(path):
    try:
        return verifier().read_regular(Path(path))
    except (OSError, ValueError) as error:
        raise ValueError('lifecycle input unavailable or unsafe') from error


def decode(raw):
    try:
        return verifier().decode(raw)
    except Exception as error:
        raise ValueError('invalid lifecycle JSON') from error


def pinned(ref, parent):
    require(isinstance(ref, dict) and isinstance(ref.get('path'), str), 'lifecycle file reference missing')
    path = Path(ref['path'])
    if not path.is_absolute():
        require(all(p not in ('', '.', '..') for p in ref['path'].split('/')), 'lifecycle reference escapes manifest')
        path = parent / path
    raw = read(path)
    require(digest(raw) == ref.get('sha256'), 'lifecycle file SHA256 mismatch')
    return path.absolute(), raw


def identity(observation):
    return {'network_id': observation['network']['id'],
            'container_ids': {role: row['id'] for role, row in observation['containers'].items()},
            'containers': observation['containers']}


def clean_binding(report):
    current = capture_binding(runtime_contract=report)
    require(current['implementation_dirty'] is False and report.get('implementation_dirty') is False,
            'lifecycle requires a clean implementation checkpoint')
    for surface in ('application', 'harness', 'planning'):
        for suffix in ('_binding',):
            require(report.get(surface + suffix) == current[surface + suffix], 'lifecycle source binding is stale')
        require(report.get(surface + '_dirty') is not True, 'lifecycle source binding is dirty')
    require(report.get('runtime_contract_digest') == runtime_contract_digest(report)
            and report.get('runtime_contract_binding') == current['runtime_contract_binding']
            and report.get('runtime_contract_dirty') is not True, 'lifecycle runtime binding mismatch')
    return current


def validate_prepared(report, *, archived=False):
    """Validate three before captures and operated distribution; no Docker calls.

    Caller supplies transient _manifest_path when reading a prepared manifest.
    Relative references resolve against that manifest, never the process cwd.
    """
    v = verifier()
    manifest_path = Path(report.get('_manifest_path', '.')).absolute()
    parent = manifest_path.parent
    clean_binding(report)
    require(isinstance(report.get('milestone_id'), str) and report['milestone_id']
            and isinstance(report.get('clone_identity'), str) and report['clone_identity'], 'prepared milestone identity missing')
    files = report.get('before_files')
    require(isinstance(files, dict) and set(files) == set(v.ROLES[:1] + ('baseline_before', 'runtime_before')),
            'prepared before capture coverage incomplete')
    snapshots = {role: decode(pinned(ref, parent)[1]) for role, ref in files.items()}
    before = snapshots['observation_before']
    try:
        v.validate_observation(before, report.get('schema_policy_id', policy.EQUAL))
        descriptor = policy.validate(before, report.get('schema_policy_id', policy.EQUAL))
        require(report.get('schema_policy_sha256') == descriptor['sha256'], 'prepared schema policy mismatch')
        for role, owner in zip(('baseline', 'runtime'), v.OWNERS):
            v.content_snapshot(snapshots[role + '_before'], owner, before['schemas'][owner])
    except (v.VerificationError, v.observer.ObservationError, policy.PolicyError) as error:
        raise ValueError('prepared snapshots invalid: ' + str(error)) from None
    require(report.get('runtime_identity') == identity(before), 'prepared runtime identity mismatch')
    require(all(row['running'] is True for row in before['containers'].values()), 'prepared application and services must run')
    expected = {'baseline_schema_hash': before['schemas'][v.OWNERS[0]]['column_metadata']['sha256'],
                'runtime_schema_hash_before': before['schemas'][v.OWNERS[1]]['column_metadata']['sha256'],
                'baseline_key_data_hash': snapshots['baseline_before']['schema_sha256'],
                'runtime_key_data_hash_before': snapshots['runtime_before']['schema_sha256']}
    require(all(report.get(k) == value for k, value in expected.items()), 'prepared before hashes mismatch')
    require(report.get('runtime_profile') == 'local-wsl', 'prepared profile mismatch')
    artifact_ref = report.get('operated_artifact', {})
    artifact_path = Path(artifact_ref.get('path', ''))
    if not artifact_path.is_absolute(): artifact_path = parent / artifact_path
    artifact_sha = artifact_ref.get('sha256')
    if not archived:
        artifact_path, artifact_raw = pinned(artifact_ref, parent)
        artifact_sha = digest(artifact_raw)
    _, observation_raw = pinned(report.get('artifact_observation'), parent)
    acquisition = decode(observation_raw)
    require(acquisition.get('kind') == 'local-operated-artifacts'
            and acquisition.get('runtime_identity') == report['runtime_identity']
            and acquisition.get('artifact_sha256') == report.get('artifact_sha256'), 'operated artifact acquisition identity mismatch')
    mount = acquisition.get('mount', {})
    require(mount.get('read_only') is True and mount.get('destination') == '/opt/current-owned'
            and Path(mount.get('source', '')).is_absolute(), 'operated distribution mount mismatch')
    source = Path(mount['source'])
    require(archived or source.resolve() == source, 'operated distribution mount must be resolved')
    rows = acquisition.get('files', [])
    require(isinstance(rows, list) and rows, 'operated distribution inventory missing')
    expected_paths = acquisition.get('classpath_files')
    require(isinstance(expected_paths, list) and len(expected_paths) >= 4
            and len(expected_paths) == len(set(expected_paths))
            and sorted(row.get('path', '') for row in rows) == sorted(expected_paths)
            and all(str(source / item) in expected_paths for item in ('app/lims-owned-application.jar','start.sh','lib/classpath.txt')),
            'operated classpath inventory mismatch')
    for row in rows:
        file_path = Path(row['path'])
        require(row.get('container_path') == '/opt/current-owned/' + str(file_path.relative_to(source)),
                'operated container artifact path mismatch')
        require(v.HEX.fullmatch(str(row.get('sha256', ''))) is not None, 'operated artifact invalid SHA256')
        if not archived:
            require(digest(read(file_path)) == row.get('sha256'), 'operated distribution artifact changed')
    require(str(artifact_path) == str(source / 'app/lims-owned-application.jar')
            and report['operated_artifact'].get('container_path') == '/opt/current-owned/app/lims-owned-application.jar'
            and any(row['path'] == str(artifact_path) and row['sha256'] == artifact_sha for row in rows),
            'operated application artifact mismatch')
    artifacts = report.get('artifact_sha256')
    require(isinstance(artifacts, dict) and artifacts
            and artifacts.get(str(artifact_path)) == artifact_sha, 'prepared artifact mapping missing operated application')
    for name, sha in artifacts.items():
        require(Path(name).is_absolute() and v.HEX.fullmatch(str(sha)) is not None, 'prepared artifact mapping mismatch')
        if not archived:
            require(digest(read(name)) == sha, 'prepared artifact mapping changed')
    result = {k: value for k, value in report.items() if k != '_manifest_path'}
    result['prepared_manifest'] = {'path': str(manifest_path), 'sha256': digest(read(manifest_path))}
    return result


def validate_lifecycle_context(manifest, manifest_path, snapshots, lifecycle):
    context = manifest.get('context')
    require(isinstance(context, dict) and set(context) == set(CONTEXT)
            and all(context.values()), 'lifecycle context missing or incomplete')
    path, raw = pinned(manifest.get('prepared_runtime'), manifest_path.parent)
    prepared = decode(raw)
    prepared['_manifest_path'] = str(path)
    prepared = validate_prepared(prepared)
    for key in CONTEXT[1:]:
        require(context[key] == prepared.get(key), 'lifecycle prepared context mismatch')
    require(identity(snapshots['observation_before']) == prepared['runtime_identity'], 'lifecycle prepared runtime identity mismatch')
    for role, ref in prepared['before_files'].items():
        require(manifest['files'][role]['sha256'] == ref['sha256'], 'lifecycle before capture differs from prepared runtime')
    require(lifecycle.get('runtime_identity') == {k: prepared['runtime_identity'][k] for k in ('network_id', 'container_ids')},
            'lifecycle cleanup runtime mismatch')
    return prepared


def report_context(report):
    return {key: report.get(key) for key in CONTEXT}


def validate_pending(report):
    from evidence_output import strict_case_contract, route_execution_model_valid
    require(report.get('strict_evidence') is True and report.get('production_access') is False
            and report.get('real_http', True) is True, 'cleanup requires isolated real HTTP strict report')
    require(report.get('status') in ('executed-pass-pending-cleanup', 'executed-pass'), 'cleanup report did not execute successfully')
    require(report.get('proof_registration_pending') is not True, 'cleanup report has pending proof registration')
    require(route_execution_model_valid(report.get('route_execution_model')), 'cleanup report execution model invalid')
    contract = report.get('runtime_contract')
    require(isinstance(contract, dict), 'cleanup report runtime contract missing')
    require(report.get('implementation_dirty') is False and report.get('runtime_contract_dirty') is not True, 'cleanup report binding dirty')
    require(all(report.get(surface + '_binding') == contract.get(surface + '_binding')
                for surface in ('application','harness','planning')), 'cleanup report source binding differs from prepared runtime')
    clean_binding(contract)
    require(report.get('runtime_contract_binding') == contract.get('runtime_contract_binding')
            and report.get('runtime_contract_digest') == contract.get('runtime_contract_digest'), 'cleanup case runtime binding mismatch')
    require(all(report_context(report).values()), 'cleanup report identity incomplete')
    valid, reason = strict_case_contract(report, check_cleanup=False)
    require(valid, reason)
    for case in report['cases']:
        require(case.get('passed') is not False and case.get('failures', []) == [], 'cleanup report contains failed case')
        require(all(case.get(k) == report.get(k) for k in ('batch_id', 'application_binding', 'harness_binding', 'runtime_contract_binding')),
                'cleanup case identity mismatch')


def store_root():
    root = Path(os.environ.get(STORE_ENV, ''))
    require(root.is_absolute() and root.is_dir() and root.resolve() == root, 'lifecycle controlled artifact store is not configured')
    return root


def registry_rows(root):
    path = root / 'lifecycle-index.tsv'
    if not path.exists():
        return []
    require(not path.is_symlink(), 'lifecycle registry cannot be symlink')
    with path.open(newline='') as stream:
        reader = csv.DictReader(stream, delimiter='\t')
        require(reader.fieldnames == list(REGISTRY), 'lifecycle registry schema mismatch')
        return list(reader)


def registration(sha, context):
    return {'artifact_ref': 'sha256:' + sha, 'sha256': sha, 'batch_id': context['batch_id'],
            'context_sha256': digest(encoded(context)), 'retention': 'permanent'}


def write_private(path, raw):
    path = Path(path)
    path.parent.mkdir(parents=True, exist_ok=True, mode=0o700)
    fd = os.open(path, os.O_WRONLY | os.O_CREAT | os.O_EXCL | os.O_NOFOLLOW, 0o600)
    with os.fdopen(fd, 'wb') as stream:
        stream.write(raw)


def bundle_inputs(lifecycle_path, report):
    v = verifier()
    raw_lifecycle = read(lifecycle_path)
    lifecycle = decode(raw_lifecycle)
    require(lifecycle.get('status') == 'diagnostic-lifecycle-verified' and lifecycle.get('lifecycle_verified') is True
            and lifecycle.get('runtime_dirty') is False, 'lifecycle verification did not succeed')
    manifest_path, raw_manifest = pinned(lifecycle.get('manifest_ref'), Path(lifecycle_path).parent)
    manifest = decode(raw_manifest)
    snapshots, pins, paths, _ = v.read_inputs(manifest_path)
    require(lifecycle.get('inputs') == pins and lifecycle.get('manifest_sha256') == digest(raw_manifest), 'lifecycle snapshot pins mismatch')
    require(lifecycle.get('context') == report_context(report) == manifest.get('context'), 'lifecycle report context mismatch')
    prepared_path, prepared_raw = pinned(manifest.get('prepared_runtime'), manifest_path.parent)
    prepared = decode(prepared_raw)
    acquisition_path, acquisition_raw = pinned(prepared.get('artifact_observation'), prepared_path.parent)
    files = {str(manifest_path): raw_manifest, str(prepared_path): prepared_raw,
             str(acquisition_path): acquisition_raw}
    files.update({str(path.absolute()): read(path) for path in paths.values()})
    # Retain every JSON capture by raw digest; file paths are private store metadata.
    return {'schema': 1, 'kind': 'registered-local-lifecycle', 'context': report_context(report),
            'lifecycle_raw': base64.b64encode(raw_lifecycle).decode(), 'lifecycle_sha256': digest(raw_lifecycle),
            'files': {path: base64.b64encode(raw).decode() for path, raw in files.items()}}


def replay_bundle(bundle, report):
    v = verifier()
    require(bundle.get('schema') == 1 and bundle.get('kind') == 'registered-local-lifecycle'
            and bundle.get('context') == report_context(report), 'registered lifecycle context mismatch')
    lifecycle_raw = base64.b64decode(bundle['lifecycle_raw'], validate=True)
    require(digest(lifecycle_raw) == bundle.get('lifecycle_sha256'), 'lifecycle report raw SHA256 mismatch')
    lifecycle = decode(lifecycle_raw)
    require(lifecycle.get('context') == report_context(report), 'lifecycle raw context mismatch')
    require(lifecycle.get('status') == 'diagnostic-lifecycle-verified' and lifecycle.get('lifecycle_verified') is True,
            'registered lifecycle failed')
    # Restore private captures under a temporary root; remap only verified path
    # references, then run the actual content/cleanup verifier again offline.
    original = {path: base64.b64decode(raw, validate=True) for path, raw in bundle['files'].items()}
    manifest_path = Path(lifecycle['manifest_ref']['path'])
    require(digest(original[str(manifest_path)]) == lifecycle['manifest_ref']['sha256'], 'registered manifest SHA256 mismatch')
    manifest = decode(original[str(manifest_path)])
    require(digest(original[str(manifest_path)]) == lifecycle['manifest_sha256'], 'registered manifest pin mismatch')
    require(manifest.get('context') == report_context(report), 'registered manifest context mismatch')
    # Raw custody permits historical replay after original private paths expire.
    prepared_path = Path(manifest['prepared_runtime']['path'])
    if not prepared_path.is_absolute(): prepared_path = manifest_path.parent / prepared_path
    require(digest(original[str(prepared_path)]) == manifest['prepared_runtime']['sha256'], 'registered prepared manifest pin mismatch')
    prepared = decode(original[str(prepared_path)])
    require(prepared.get('runtime_manifest_schema') == 'local-prepared-v1'
            and prepared.get('lifecycle_phase') == 'prepared', 'registered prepared phase mismatch')
    with tempfile.TemporaryDirectory(prefix='lifecycle-replay-') as temporary:
        root = Path(temporary)
        mapped = {}
        for index, (path, raw) in enumerate(original.items()):
            target = root / (str(index) + '.json')
            target.write_bytes(raw)
            mapped[path] = target
        # Validate the unchanged raw captures through the same verifier functions.
        snapshots = {}
        for role in v.ROLES:
            ref = manifest['files'][role]
            path = manifest_path.parent / ref['path']
            raw = original[str(path)]
            require(digest(raw) == ref['sha256'] == lifecycle['inputs'][role]['sha256'], 'registered snapshot raw SHA256 mismatch')
            snapshots[role] = decode(raw)
        # Pin prepared refs to archived bytes, retain operated local file paths.
        checked_prepared = dict(prepared)
        checked_prepared['_manifest_path'] = str(mapped[str(prepared_path)])
        checked_prepared['before_files'] = {}
        for role, ref in prepared['before_files'].items():
            path = Path(ref['path'])
            if not path.is_absolute(): path = prepared_path.parent / path
            require(digest(original[str(path)]) == ref['sha256'], 'registered prepared snapshot SHA256 mismatch')
            checked_prepared['before_files'][role] = {'path': str(mapped[str(path)]), 'sha256': ref['sha256']}
        ref = prepared['artifact_observation']
        path = Path(ref['path'])
        if not path.is_absolute(): path = prepared_path.parent / path
        require(digest(original[str(path)]) == ref['sha256'], 'registered artifact observation SHA256 mismatch')
        checked_prepared['artifact_observation'] = {'path': str(mapped[str(path)]), 'sha256': ref['sha256']}
        if not Path(prepared['operated_artifact']['path']).is_absolute():
            checked_prepared['operated_artifact'] = {**prepared['operated_artifact'], 'path':str(prepared_path.parent / prepared['operated_artifact']['path'])}
        valid = validate_prepared(checked_prepared, archived=True)
        require(all(report.get(k) == valid.get(k) for k in CONTEXT[1:]), 'registered prepared report identity mismatch')
        require(report.get('runtime_contract', {}).get('prepared_manifest') == {'path':str(prepared_path), 'sha256':digest(original[str(prepared_path)])},
                'report does not reference registered prepared runtime')
        require(identity(snapshots['observation_before']) == valid['runtime_identity'], 'registered runtime identity mismatch')
        # A local temporary manifest runs every original snapshot/content guard.
        replay_manifest = {'schema_version':1, 'kind':'local-runtime-milestone-inputs', 'files':{}}
        for role in v.ROLES:
            target = root / (role + '.json')
            raw = encoded(snapshots[role])
            target.write_bytes(raw)
            replay_manifest['files'][role] = {'path':target.name, 'sha256':digest(raw)}
        target = root / 'replay.json'
        target.write_bytes(encoded(replay_manifest))
        checked = {}
        v.verify(SimpleNamespace(manifest=target, schema_policy=prepared['schema_policy_id']), checked,
                 recorded_cleanup=lifecycle['cleanup_inspections'])
        for key in ('runtime_identity', 'cleanup_observation', 'comparisons', 'baseline_schema_immutable',
                    'runtime_schema_restored', 'application_stopped', 'oracle_reused', 'redis_reused'):
            require(checked[key] == lifecycle.get(key), 'registered lifecycle observation mismatch')
        before = v.validate_observation(snapshots['observation_before'], prepared['schema_policy_id'])
        after = v.validate_observation(snapshots['observation_after'], prepared['schema_policy_id'])
        require(before <= dt.datetime.fromisoformat(report['started_at']) <= dt.datetime.fromisoformat(report['finished_at']) <= after,
                'report execution outside lifecycle observations')
        require(after <= dt.datetime.fromisoformat(lifecycle['observed_at']), 'lifecycle cleanup precedes after observation')
    return lifecycle


def export_cleanup(report, lifecycle_path):
    import fcntl
    validate_pending(report)
    bundle = bundle_inputs(lifecycle_path, report)
    replay_bundle(bundle, report)
    raw = encoded(bundle)
    sha = digest(raw)
    root = store_root()
    object_dir = root / 'sha256'
    require(not object_dir.is_symlink(), 'lifecycle objects cannot be symlink')
    object_dir.mkdir(mode=0o700, exist_ok=True)
    target = object_dir / sha
    if target.exists(): require(read(target) == raw, 'lifecycle stored object conflict')
    else: write_private(target, raw)
    lock = root / '.lifecycle-publication.lock'
    require(not lock.is_symlink(), 'lifecycle lock cannot be symlink')
    with lock.open('a') as stream:
        fcntl.flock(stream, fcntl.LOCK_EX)
        rows = registry_rows(root)
        entry = registration(sha, report_context(report))
        matching = [row for row in rows if row['artifact_ref'] == entry['artifact_ref']]
        require(not matching or matching == [entry], 'lifecycle registration conflict')
        if not matching: rows.append(entry)
        temporary = root / ('.lifecycle-registry-' + str(os.getpid()))
        with temporary.open('x', newline='') as output:
            os.chmod(temporary, 0o600)
            writer = csv.DictWriter(output, fieldnames=REGISTRY, delimiter='\t')
            writer.writeheader(); writer.writerows(rows)
        os.replace(temporary, root / 'lifecycle-index.tsv')
    return {'cleanup_mode':'persistent-oracle', 'lifecycle_proof': {'schema':1, 'storage':'artifact',
            'artifact_ref':'sha256:' + sha, 'sha256':sha}, 'application_stopped':True,
            'oracle_reused':True, 'redis_preserved':True, 'oracle_preserved':True,
            'baseline_schema_immutable':True, 'runtime_schema_restored':True, 'runtime_dirty':False,
            'network_preserved':True, 'volume_preserved':True,
            'private_capture_retention':'registered-permanent-proof',
            'temporary_files_removed':False}


def validate_cleanup(cleanup, report):
    from evidence_output import cleanup_contract_complete
    require(cleanup_contract_complete(cleanup), 'lifecycle cleanup contract incomplete')
    validate_pending(report)
    ref = cleanup.get('lifecycle_proof', {})
    require(ref.get('schema') == 1 and ref.get('storage') == 'artifact'
            and ref.get('artifact_ref') == 'sha256:' + str(ref.get('sha256')), 'registered lifecycle proof required')
    sha = ref['sha256']
    require(verifier().HEX.fullmatch(sha) is not None, 'invalid lifecycle proof digest')
    root = store_root()
    require([row for row in registry_rows(root) if row['artifact_ref'] == ref['artifact_ref']]
            == [registration(sha, report_context(report))], 'lifecycle registration missing, duplicate or mismatched')
    raw = read(root / 'sha256' / sha)
    require(digest(raw) == sha, 'registered lifecycle artifact SHA256 mismatch')
    return replay_bundle(decode(raw), report)


def prepare(input_path, output_path, *, docker='docker', timeout=30):
    """Acquire operated hashes through a fixed read-only local Docker boundary."""
    v = verifier()
    seed = decode(read(input_path))
    parent = Path(input_path).absolute().parent
    files = seed['before_files']
    _, raw_before = pinned(files['observation_before'], parent)
    before = decode(raw_before)
    v.validate_observation(before, seed.get('schema_policy_id', policy.EQUAL))
    schema_policy = policy.validate(before, seed.get('schema_policy_id', policy.EQUAL))
    source = Path(seed['distribution_dir']).resolve(strict=True)
    require(source.is_dir(), 'prepared distribution directory missing')
    reader = v.observer.Observer(SimpleNamespace(docker=docker, timeout=timeout))
    network, containers = reader.identities()
    require(network == before['network'] and containers == before['containers'], 'prepared live runtime differs from before observation')
    mounts = decode(reader.command(['inspect', '--type', 'container', '--format', '{{json .Mounts}}', v.observer.CONTAINERS['app']]))
    selected = [row for row in mounts if row.get('Destination') == '/opt/current-owned']
    require(len(selected) == 1 and selected[0].get('Type') == 'bind' and selected[0].get('RW') is False
            and selected[0].get('Source') == str(source), 'prepared app does not mount exact distribution read-only')
    classpath = read(source / 'lib/classpath.txt').decode('utf-8').splitlines()
    require(classpath and len(classpath) == len(set(classpath)) and all(Path(name).name == name and name.endswith('.jar') for name in classpath), 'invalid distribution classpath')
    paths = [source / 'app/lims-owned-application.jar', source / 'start.sh', source / 'lib/classpath.txt'] + [source / 'lib' / name for name in classpath]
    require((source / 'lib').is_dir() and len(paths) > 2, 'prepared distribution lacks public classpath')
    rows = []
    for path in paths:
        expected = digest(read(path))
        container_path = '/opt/current-owned/' + str(path.relative_to(source))
        line = reader.command(['exec', v.observer.CONTAINERS['app'], 'sha256sum', '--', container_path]).strip()
        require(line == expected + '  ' + container_path, 'prepared operated artifact hash mismatch')
        rows.append({'path':str(path), 'container_path':container_path, 'sha256':expected})
    require(reader.identities() == (network, containers), 'prepared runtime changed during artifact acquisition')
    artifacts = {str(source / 'app/lims-owned-application.jar'): rows[0]['sha256']}
    artifact_observation = {'kind':'local-operated-artifacts', 'runtime_identity':identity(before),
        'observed_at':dt.datetime.now(dt.timezone.utc).isoformat(), 'artifact_sha256':artifacts,
        'mount':{'source':str(source),'destination':'/opt/current-owned','read_only':True}, 'files':rows,'classpath_files':[str(path) for path in paths]}
    observation_path = output_path.absolute().with_name(output_path.name + '.artifacts.json')
    raw = encoded(artifact_observation)
    write_private(observation_path, raw)
    contents = {role:decode(pinned(files[role], parent)[1]) for role in ('baseline_before','runtime_before')}
    report = {'runtime_manifest_schema':'local-prepared-v1','status':'local-prepared-unverified',
        'execution':'local-prepared-unverified','lifecycle_phase':'prepared','strict_evidence':False,
        'production_access':False,'existing_application_preserved':True,'runtime_profile':'local-wsl','execution_location':'local-wsl-docker',
        'milestone_id':seed['milestone_id'],'clone_identity':seed['clone_identity'], 'loopback_url':seed['loopback_url'],
        'runtime_identity':identity(before), 'schema_policy_id':schema_policy['id'], 'schema_policy_sha256':schema_policy['sha256'],
        'oracle_instance_id':v.observer.CONTAINERS['oracle'],'oracle_container':v.observer.CONTAINERS['oracle'],
        'redis_container':v.observer.CONTAINERS['redis'],'oracle_reused':True,'redis_reused':True,
        'baseline_schema':v.OWNERS[0],'runtime_schema':v.OWNERS[1],
        'baseline_schema_hash':before['schemas'][v.OWNERS[0]]['column_metadata']['sha256'],
        'runtime_schema_hash_before':before['schemas'][v.OWNERS[1]]['column_metadata']['sha256'],
        'baseline_key_data_hash':contents['baseline_before']['schema_sha256'],
        'runtime_key_data_hash_before':contents['runtime_before']['schema_sha256'],
        'schema_copy_method':'supplied-runtime-before-content-restore-target','baseline_refreshed':False,'runtime_schema_reused':True,
        'runtime_schema_reset':False,'artifact_sha256':artifacts,
        'operated_artifact':{'path':str(source/'app/lims-owned-application.jar'),'sha256':rows[0]['sha256'],'container_path':'/opt/current-owned/app/lims-owned-application.jar'},
        'artifact_observation':{'path':str(observation_path),'sha256':digest(raw)},
        'before_files':{role:{'path':str(pinned(ref,parent)[0]),'sha256':ref['sha256']} for role,ref in files.items()},
        'route_execution_model':{'boundary_process':False,'internal_http_bridge':False,'jni_replacement_process':False}}
    for field in ('runtime_schema_hash_after','runtime_key_data_hash_after','baseline_schema_immutable','runtime_schema_restored','runtime_dirty'):
        report[field] = None
    binding = capture_binding()
    local_binding = capture_binding(runtime_contract=report)
    binding.update({key:local_binding[key] for key in ('runtime_contract_binding','runtime_contract_digest','runtime_contract_dirty','runtime_contract_scope')})
    require(binding['implementation_dirty'] is False, 'prepare requires clean implementation')
    report.update({key:value for key,value in binding.items() if key != 'runtime_contract'})
    report['source_commit'] = binding['repository_head_at_capture']
    report['source_dirty'] = False
    write_private(output_path, encoded(report))
    validate_prepared({**report,'_manifest_path':str(output_path.absolute())})
    return report


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--export-cleanup', action='store_true')
    parser.add_argument('--prepare-input', type=Path)
    parser.add_argument('--docker', default='docker')
    parser.add_argument('--timeout', type=verifier().observer.positive_timeout, default=30)
    parser.add_argument('--report', type=Path)
    parser.add_argument('--lifecycle', type=Path)
    parser.add_argument('--output', type=Path, required=True)
    args = parser.parse_args(argv)
    require(bool(args.prepare_input) != args.export_cleanup, 'select prepare or explicit cleanup export')
    from evidence_output import guard_output_path
    guard_output_path(args.output)
    if args.prepare_input:
        prepare(args.prepare_input, args.output, docker=args.docker, timeout=args.timeout)
        return 0
    require(args.report and args.lifecycle, 'cleanup export requires report and lifecycle')
    report = decode(read(args.report))
    cleanup = export_cleanup(report, args.lifecycle)
    write_private(args.output, encoded(cleanup))
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
