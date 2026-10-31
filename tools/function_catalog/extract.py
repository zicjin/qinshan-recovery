#!/usr/bin/env python3
"""Deterministic, offline static catalog. No code/config execution or network IO.

This is a lexical source index, not a Spring/MyBatis runtime interpreter.
Unresolved annotation expressions and inherited framework routes stay explicit.
"""
from __future__ import annotations
import argparse
import hashlib
import json
import re
import subprocess
from pathlib import Path
import xml.etree.ElementTree as ET

MAPPING = re.compile(r'@(RequestMapping|GetMapping|PostMapping|PutMapping|DeleteMapping|PatchMapping)(?![\w$])')
TYPE = re.compile(r'\b(class|interface|enum)\s+(\w+)([^;{]*)[;{]')
STRING = re.compile(r'"(?:\\.|[^"\\])*"')

def sha(data):
    return hashlib.sha256(data).hexdigest()

def strip_comments(text):
    # Preserve newlines and offsets; quoted Java strings are not comments.
    token = re.compile(r'"(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\'|//[^\n]*|/\*[\s\S]*?\*/')
    return token.sub(lambda m: re.sub(r'[^\n]', ' ', m[0]) if m[0].startswith(('//', '/*')) else m[0], text)

def line(text, offset):
    return text.count('\n', 0, offset) + 1

def close_paren(text, start):
    depth, i = 0, start
    while i < len(text):
        if text[i] in '\"\'':
            quote = text[i]; i += 1
            while i < len(text):
                if text[i] == '\\': i += 2; continue
                if text[i] == quote: break
                i += 1
        elif text[i] == '(': depth += 1
        elif text[i] == ')':
            depth -= 1
            if depth == 0: return i + 1
        i += 1
    raise ValueError('unclosed annotation')

def decode_literal(raw):
    try: return json.loads(raw)
    except ValueError: return raw[1:-1]  # Preserve uncommon Java escapes verbatim.

def mapping_args(name, args):
    # Attribute boundaries do not split arrays or quoted strings.
    attrs = list(re.finditer(r'\b(\w+)\s*=', STRING.sub(lambda m: ' ' * len(m[0]), args)))
    fields = {}
    if attrs:
        for i, match in enumerate(attrs):
            fields[match[1]] = args[match.end():attrs[i + 1].start() if i + 1 < len(attrs) else len(args)].strip().rstrip(',').strip()
    elif args.strip(): fields['value'] = args.strip()
    expr = fields.get('path', fields.get('value', ''))
    paths = [decode_literal(m[0]) for m in STRING.finditer(expr)]
    residue = STRING.sub('', expr).strip(' {},\n\t\r')
    unresolved = bool(residue)
    if not expr: paths = ['']
    methods = re.findall(r'(?:RequestMethod\.)?\b(GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS|TRACE)\b', fields.get('method', ''))
    if name != 'RequestMapping': methods = [name[:-7].upper()]
    return paths, methods or ['ANY'], unresolved

def next_method(text, offset):
    tail = text[offset:]
    # Ignore subsequent annotations and their argument lists.
    while True:
        found = re.search(r'\S', tail)
        if not found: return None
        off = found.start()
        if tail[off] != '@': break
        annotation = re.match(r'@[\w.]+\s*', tail[off:])
        if not annotation: break
        end = off + annotation.end()
        if end < len(tail) and tail[end] == '(': end = close_paren(tail, end)
        tail = tail[end:]
    head = re.split(r'[;{]', tail, maxsplit=1)[0]
    match = re.search(r'\b(\w+)\s*\(', head)
    return match[1] if match else None

def java_file(path, root):
    raw = path.read_bytes(); text = raw.decode('utf-8-sig'); clean = strip_comments(text)
    package = re.search(r'\bpackage\s+([\w.]+)\s*;', clean)
    imports = {s.rsplit('.', 1)[-1]: s for s in re.findall(r'\bimport\s+([\w.]+)\s*;', clean)}
    typ = TYPE.search(clean)
    if not typ: return None
    fqcn = (package[1] + '.' if package else '') + typ[2]
    parents = []
    parents_expr = re.sub(r'<[^<>]*(?:<[^<>]*>[^<>]*)*>', '', typ[3])
    for group in re.findall(r'\b(?:extends|implements)\s+(.+?)(?=\b(?:extends|implements)\b|$)', parents_expr):
        for parent in group.split(','):
            name = parent.strip().split()[0] if parent.strip() else ''
            if name: parents.append(imports.get(name, (package[1] + '.' + name) if package and '.' not in name else name))
    ref = {'path': 'project/' + path.relative_to(root).as_posix(), 'line': line(text, typ.start()), 'sha256': sha(raw)}
    record = {'symbol': fqcn, 'kind': typ[1], 'parents': parents, 'source': ref, 'mappings': [], 'role': ('service' if '/service/' in path.as_posix() else 'mapper' if '/mapper/' in path.as_posix() else 'controller' if '/controller/' in path.as_posix() else 'resource' if '/resource/' in path.as_posix() else 'other')}
    for match in MAPPING.finditer(clean):
        start = match.end()
        while start < len(clean) and clean[start].isspace(): start += 1
        end = close_paren(clean, start) if start < len(clean) and clean[start] == '(' else start
        args = clean[start + 1:end - 1] if end > start else ''
        paths, methods, unresolved = mapping_args(match[1], args)
        scope = 'type' if match.start() < typ.start() else 'method'
        record['mappings'].append({'annotation': match[1], 'scope': scope, 'member': None if scope == 'type' else next_method(clean, end), 'declared_paths': paths, 'http_methods': methods, 'unresolved_expression': unresolved, 'source': {**ref, 'line': line(text, match.start())}})
    return record

def bases(symbol, types, seen=None):
    seen = set() if seen is None else set(seen)
    if symbol in seen: return [], ['cyclic inheritance: ' + symbol]
    seen.add(symbol)
    current = types.get(symbol)
    if current is None: return [], ['unresolved parent: ' + symbol]
    own = [m for m in current['mappings'] if m['scope'] == 'type']
    if own: return own, []
    found, gaps = [], []
    for parent in current['parents']:
        items, unknown = bases(parent, types, seen); found.extend(items); gaps.extend(unknown)
    return found, gaps

def join_route(base, suffix):
    return '/' + '/'.join(p.strip('/') for p in (base, suffix) if p.strip('/'))

def mapper_file(path, root):
    raw = path.read_bytes(); text = raw.decode('utf-8-sig')
    try: tree = ET.fromstring(text)
    except ET.ParseError as exc: return {'source': {'path': 'project/' + path.relative_to(root).as_posix(), 'line': 1, 'sha256': sha(raw)}, 'parse_error': str(exc)}
    if tree.tag != 'mapper': return None
    source = {'path': 'project/' + path.relative_to(root).as_posix(), 'line': line(text, text.find('<mapper')), 'sha256': sha(raw)}
    # Lexical table tokens are candidates only, not a parsed executable SQL AST.
    uncommented = re.sub(r'<!--[\s\S]*?-->', lambda m: re.sub(r'[^\n]', ' ', m[0]), text)
    tables = []
    for match in re.finditer(r'\bT_[A-Za-z0-9_$]+\b', uncommented, re.I):
        tables.append({'name': match[0], 'line': line(text, match.start()), 'basis': 'lexical-table-token'})
    statements = []
    for match in re.finditer(r'<(select|insert|update|delete|sql)\b[^>]*\bid\s*=\s*["\']([^"\']+)["\']', uncommented):
        statements.append({'kind': match[1], 'id': match[2], 'line': line(text, match.start())})
    return {'namespace': tree.attrib.get('namespace'), 'source': source, 'statements': statements, 'table_candidates': tables, 'dynamic_status': 'unknown'}

def extract(root):
    root = root.resolve()
    main = root / 'src/main'
    if not (main / 'java').is_dir(): raise ValueError('source root must contain src/main/java')
    files = sorted(p for p in main.rglob('*') if p.is_file() and not p.is_symlink())
    manifests = [{'path': 'project/' + p.relative_to(root).as_posix(), 'sha256': sha(p.read_bytes())} for p in files]
    types = {}
    for path in files:
        if path.suffix == '.java':
            item = java_file(path, root)
            if item:
                if item['symbol'] in types: raise ValueError('duplicate Java symbol: ' + item['symbol'])
                types[item['symbol']] = item
    endpoints = []
    declarations = []
    for symbol, item in sorted(types.items()):
        inherited, gaps = bases(symbol, types)
        for mapping in item['mappings']:
            declarations.append({'symbol': symbol, **mapping, 'dynamic_status': 'unknown'})
            if mapping['scope'] != 'method': continue
            candidates = []
            for base in inherited:
                for prefix in base['declared_paths']:
                    for suffix in mapping['declared_paths']:
                        candidates.append({'path': join_route(prefix, suffix), 'base_source': base['source'], 'basis': 'declared-type-and-method'})
            if not inherited and not item['parents']:
                candidates = [{'path': join_route('', suffix), 'base_source': None, 'basis': 'method-without-type-base'} for suffix in mapping['declared_paths']]
            endpoints.append({'symbol': symbol, 'member': mapping['member'], 'source': mapping['source'], 'annotation': mapping['annotation'], 'declared_paths': mapping['declared_paths'], 'http_methods': mapping['http_methods'], 'candidate_routes': candidates, 'unresolved_expression': mapping['unresolved_expression'], 'unresolved_inheritance': gaps, 'dynamic_status': 'unknown'})
    interfaces = [{k: v for k, v in item.items() if k != 'mappings'} | {'dynamic_status': 'unknown'} for _, item in sorted(types.items()) if item['kind'] == 'interface' and item['role'] in {'service', 'mapper'}]
    templates = [{'source': {'path': 'project/' + p.relative_to(root).as_posix(), 'line': 1, 'sha256': sha(p.read_bytes())}, 'view_name': p.relative_to(main / 'resources/templates').with_suffix('').as_posix(), 'dynamic_status': 'unknown'} for p in files if (main / 'resources/templates') in p.parents]
    mappers = [item for p in files if p.suffix == '.xml' and '/mapper/' in p.as_posix() and (item := mapper_file(p, root)) is not None]
    try: commit = subprocess.run(['git', '-C', str(root), 'rev-parse', 'HEAD'], check=True, capture_output=True, text=True).stdout.strip()
    except (OSError, subprocess.CalledProcessError): commit = None
    try: dirty = bool(subprocess.run(['git', '-C', str(root), 'status', '--porcelain', '--untracked-files=no'], check=True, capture_output=True, text=True).stdout)
    except (OSError, subprocess.CalledProcessError): dirty = None
    return {'format': 'lims-static-function-catalog-v1', 'scope': 'recovered-business-source-static', 'source_commit': commit, 'source_worktree_dirty': dirty, 'source_tree_sha256': sha(json.dumps(manifests, ensure_ascii=False, sort_keys=True).encode()), 'limitations': ['Static declarations are not runtime reachability or user-capability equivalence.', 'Inherited framework routes absent from this source remain unresolved.', 'Route composition is a source candidate, not a verified Spring registration.', 'Mapper table candidates are lexical tokens and may include aliases or dynamic SQL tokens.', 'No production menu export, database records, private configuration or network is read.'], 'counts': {'source_files': len(files), 'mapping_declarations': len(declarations), 'method_mappings': len(endpoints), 'templates': len(templates), 'service_mapper_interfaces': len(interfaces), 'mapper_xml': len(mappers)}, 'mapping_declarations': declarations, 'endpoints': endpoints, 'templates': templates, 'interfaces': interfaces, 'mapper_xml': mappers, 'source_manifest': manifests}

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--source-root', required=True, type=Path)
    parser.add_argument('--output', required=True, type=Path)
    args = parser.parse_args()
    try: catalog = extract(args.source_root)
    except (OSError, UnicodeError, ValueError) as exc: parser.exit(1, f'extraction failed: {exc}\n')
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(catalog, ensure_ascii=False, indent=2, sort_keys=True) + '\n', encoding='utf-8')
    print(json.dumps(catalog['counts'], sort_keys=True))
if __name__ == '__main__': main()
