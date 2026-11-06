#!/usr/bin/env python3
"""Run declarative Spring Web API contracts against an owned loopback runtime.

This runner deliberately separates route disposition from source/class presence.
It never writes business rows through SQL: SQL fixture files are metadata for a
separate fixture controller, while every business operation is performed over
HTTP.  Debug runs may reuse a long-lived sss2 runtime and are diagnostic only;
strict runs require a clone identity, loopback URL, artifact hash and a complete
per-case fixture/audit/cleanup contract. Output is diagnostic/pending by
default; canonical publication is handled by the evidence registry promoter.
"""
from __future__ import annotations

import argparse
import datetime as dt
import hashlib
import http.client
import json
import os
from pathlib import Path
import re
import subprocess
import sys
import tempfile
import time
import uuid
from typing import Any, Dict, Iterable, List, Mapping, Optional, Tuple
from http.cookiejar import CookieJar
from urllib.error import HTTPError, URLError
from urllib.parse import urlsplit
from urllib.request import HTTPCookieProcessor, ProxyHandler, Request, build_opener

sys.path.insert(0, str(Path(__file__).resolve().parent))
from owned_runtime_contract import RuntimeContractError, validate_file
from api_fixture import FixtureError, execute_fixture, validate_sql_fixture
from evidence_output import (
    DIAGNOSTIC_ROOT,
    IMPLEMENTATION_SCOPE,
    cleanup_contract_complete,
    current_implementation_binding,
    guard_output_path,
)
from evidence_fingerprint import (
    NORMALIZER_VERSION,
    STIMULUS_CONTRACT_VERSION,
    transcript_fingerprints,
)
from source_binding import binding_metadata, capture_binding
from source_binding import REPO_ROOT as DISCOVERED_REPO_ROOT

ROOT = Path(__file__).resolve().parents[1]
REPO_ROOT = DISCOVERED_REPO_ROOT
WRITE_METHODS = {"POST", "PUT", "PATCH", "DELETE"}
ALLOWED_LOOPBACK = {"127.0.0.1", "localhost", "::1", "[::1]"}
COOKIE_JAR = CookieJar()
LOOPBACK_OPENER = build_opener(ProxyHandler({}), HTTPCookieProcessor(COOKIE_JAR))
SENSITIVE_HEADERS = {"authorization", "cookie", "proxy-authorization", "set-cookie"}


def now() -> str:
    return dt.datetime.now(dt.timezone.utc).isoformat()


def sha256_bytes(data: bytes) -> str:
    return hashlib.sha256(data).hexdigest()


def sha256_file(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def read_password_file(path: Path) -> str:
    if not path.is_file() or path.stat().st_mode & 0o077:
        raise ValueError("password file must be a regular 0600 file")
    for line in path.read_text(encoding="utf-8").splitlines():
        if line.startswith("TEST_PASSWORD="):
            value = line.split("=", 1)[1]
            if value:
                return value
    raise ValueError("password file has no TEST_PASSWORD entry")


def _authentication_request(opener: Any, request: Request, *, timeout: float,
                            attempts: int = 3) -> Tuple[int, bytes, Mapping[str, str]]:
    """Run an authentication request with bounded transport retries.

    The reused sss2 strict runtime is reached through two loopback relays. A
    relay can close an idle keep-alive connection while the application is
    still healthy. Authentication is safe to retry because it only creates a
    session; business requests are never retried here.
    """
    last_error: Optional[BaseException] = None
    for attempt in range(attempts):
        try:
            with opener.open(request, timeout=timeout) as response:
                return response.status, response.read(), response.headers
        except HTTPError as error:
            return error.code, error.read() if error.fp else b"", error.headers
        except (OSError, URLError, TimeoutError, http.client.RemoteDisconnected) as error:
            last_error = error
            if attempt + 1 < attempts:
                time.sleep(0.25 * (attempt + 1))
                continue
            raise RuntimeError("authentication transport failed after %d attempts: %s" % (attempts, error)) from error
    raise RuntimeError("authentication transport failed: %s" % last_error)


def rsa_login(base_url: str, username: str, password: str, *, timeout: float) -> Dict[str, Any]:
    # Use a fresh opener for each authentication boundary. The global opener
    # is intentionally retained for contract requests, but stale cookies or a
    # relay's half-closed keep-alive must not poison public-key/login.
    auth_jar = CookieJar()
    auth_opener = build_opener(ProxyHandler({}), HTTPCookieProcessor(auth_jar))
    key_request = Request(
        base_url.rstrip("/") + "/open/security/public-key",
        headers={"Connection": "close"}, method="GET",
    )
    key_status, key_body, _ = _authentication_request(auth_opener, key_request, timeout=timeout)
    if key_status != 200:
        raise RuntimeError("RSA public-key request returned %d" % key_status)
    try:
        import base64
        key_der = base64.b64decode(key_body.strip(), validate=True)
    except Exception as error:
        raise RuntimeError("RSA public-key response is not base64") from error
    with tempfile.TemporaryDirectory(prefix="owned-api-rsa-") as temporary:
        temporary = Path(temporary)
        der = temporary / "key.der"
        pem = temporary / "key.pem"
        der.write_bytes(key_der)
        convert = subprocess.run(
            ["openssl", "rsa", "-pubin", "-inform", "DER", "-in", str(der), "-out", str(pem)],
            capture_output=True, check=False,
        )
        if convert.returncode:
            raise RuntimeError("RSA public-key conversion failed")
        encrypted = subprocess.run(
            ["openssl", "pkeyutl", "-encrypt", "-pubin", "-inkey", str(pem),
             "-pkeyopt", "rsa_padding_mode:pkcs1"],
            input=password.encode("utf-8"), capture_output=True, check=False,
        )
        if encrypted.returncode:
            raise RuntimeError("RSA password encryption failed")
    import base64
    from urllib.parse import urlencode
    body = urlencode({"id": username, "password": base64.b64encode(encrypted.stdout).decode("ascii")}).encode("ascii")
    login_request = Request(
        base_url.rstrip("/") + "/core/module/sys/login",
        data=body,
        headers={"Content-Type": "application/x-www-form-urlencoded", "Connection": "close",
                 "SECURITY-RSA-KEY": key_body.decode("ascii").strip()},
        method="POST",
    )
    status, response_body, _ = _authentication_request(auth_opener, login_request, timeout=timeout)
    if status != 200:
        raise RuntimeError("RSA login returned %d" % status)
    # Transfer only the session cookies produced by this login into the
    # contract opener. No supplier/owned or prior-job cookies are reused.
    COOKIE_JAR.clear()
    for cookie in auth_jar:
        COOKIE_JAR.set_cookie(cookie)
    return {"status": status, "body_sha256": sha256_bytes(response_body), "authenticated": True}


def git_value(*args: str) -> str:
    result = subprocess.run(["git", "-C", str(REPO_ROOT), *args], capture_output=True, text=True, check=True)
    return result.stdout.strip()


def source_metadata(case_file: Path,
                    runtime_contract: Optional[Mapping[str, Any]] = None) -> Dict[str, Any]:
    # Contract documents are evidence inputs, not implementation scope. They
    # must never change the implementation binding merely because a case file
    # was regenerated or moved.
    # The implementation aggregate is a repository/application/harness
    # property. A strict runtime manifest may carry a different runtime
    # contract (for example an isolated schema name), so do not feed that
    # runtime-only value back into the aggregate and accidentally stale the
    # application binding. Keep the runtime surface orthogonal instead.
    binding = capture_binding()
    if runtime_contract is not None:
        runtime_binding = capture_binding(runtime_contract=runtime_contract)
        binding["runtime_contract_binding"] = runtime_binding["runtime_contract_binding"]
        binding["runtime_contract_digest"] = runtime_binding["runtime_contract_digest"]
    scope = list(binding["implementation_scope"])
    status = subprocess.run(
        ["git", "-C", str(REPO_ROOT), "status", "--porcelain", "--untracked-files=all", "--", *scope],
        capture_output=True, text=True, check=True,
    )
    # ``source_commit`` remains a compatibility/provenance field.  Strict
    # validity is decided by the implementation binding below.
    binding_dirty = bool(status.stdout.strip()) or bool(binding["implementation_dirty"])
    metadata = {
        "source_commit": binding["repository_head_at_capture"],
        "source_dirty": binding_dirty,
        "source_scope": scope,
        "implementation_dirty": binding_dirty,
    }
    metadata.update(binding_metadata(binding))
    metadata["implementation_scope"] = scope
    metadata["implementation_dirty"] = binding_dirty
    return metadata


def validate_base_url(base_url: str, *, strict: bool) -> None:
    parsed = urlsplit(base_url)
    if parsed.scheme != "http" or not parsed.netloc:
        raise ValueError("base URL must be an http URL")
    host = parsed.hostname or ""
    if host not in ALLOWED_LOOPBACK:
        raise ValueError("API runner accepts loopback URLs only; production and remote hosts are forbidden")
    if strict and parsed.port is None:
        raise ValueError("strict API runner requires an explicit loopback port")


def normalize_expected_status(case: Mapping[str, Any]) -> List[int]:
    expected = case.get("expected_status", case.get("expected", 200))
    if isinstance(expected, int):
        return [expected]
    if isinstance(expected, list) and expected and all(isinstance(value, int) for value in expected):
        return list(expected)
    raise ValueError("expected_status must be an integer or non-empty integer list")


def json_top_level_keys(body: bytes) -> Optional[List[str]]:
    try:
        value = json.loads(body.decode("utf-8"))
    except (UnicodeDecodeError, json.JSONDecodeError):
        return None
    return sorted(value.keys()) if isinstance(value, dict) else None


def redact_headers(headers: Mapping[str, str]) -> Dict[str, str]:
    return {
        key.lower(): "<REDACTED>" if key.lower() in SENSITIVE_HEADERS else str(value)
        for key, value in headers.items()
    }


def cookie_auth_context(jar: CookieJar) -> Dict[str, Any]:
    return {
        "cookies": {
            cookie.name: sha256_bytes(cookie.value.encode("utf-8"))
            for cookie in sorted(jar, key=lambda item: item.name)
        }
    }


def _fixture_plan(case: Mapping[str, Any], *, strict: bool, sql_executor_available: bool) -> Dict[str, Any]:
    fixture = case.get("fixture")
    if fixture is None or fixture == "none":
        return {"mode": "none", "setup_requests": [], "restore_requests": []}
    if not isinstance(fixture, dict):
        raise ValueError("fixture must be an object or 'none'")
    mode = fixture.get("mode", "api")
    if mode not in {"api", "sql-control"}:
        raise ValueError("fixture.mode must be api or sql-control")
    setup_requests = fixture.get("setup_requests", [])
    restore_requests = fixture.get("restore_requests", [])
    if not isinstance(setup_requests, list) or not isinstance(restore_requests, list):
        raise ValueError("fixture setup_requests and restore_requests must be lists")
    if not all(isinstance(item, dict) for item in setup_requests + restore_requests):
        raise ValueError("fixture HTTP requests must be objects")
    if strict and mode == "sql-control" and not sql_executor_available:
        raise ValueError("strict sql-control fixture requires an external fixture executor")
    if strict and case.get("operation") == "write" and mode == "api" and (
        not setup_requests or (not restore_requests and fixture.get("cleanup_via") != "case-http")
    ):
        raise ValueError("strict API write fixture requires setup_requests and restore_requests, or cleanup_via=case-http")
    result = {
        "id": fixture.get("id", case.get("id", "fixture")),
        "mode": mode,
        "setup": "pending" if setup_requests else "not-required",
        "restore": "pending" if restore_requests else "not-required",
        "setup_requests": setup_requests,
        "restore_requests": restore_requests,
        "sql_files": fixture.get("sql_files", []),
        "setup_sql_files": fixture.get("setup_sql_files", fixture.get("sql_files", [])),
        "restore_sql_files": fixture.get("restore_sql_files", fixture.get("sql_files", [])),
        "business_write_via": fixture.get("business_write_via", "http"),
        "cleanup_via": fixture.get("cleanup_via", "restore-http"),
    }
    if mode == "sql-control":
        files = result["sql_files"]
        if not isinstance(files, list) or not all(isinstance(item, str) for item in files):
            raise ValueError("sql-control fixture sql_files must be a list of paths")
    return result


_FIXTURE_REF = re.compile(r"^\$\{fixture\.([^.}]+)(?:\.([A-Za-z0-9_$.-]+))?\}$")
_FIXTURE_REF_EMBEDDED = re.compile(r"\$\{fixture\.([^.}]+)(?:\.([A-Za-z0-9_$.-]+))?\}")
_RUNTIME_REF = re.compile(r"^\$\{runtime\.([A-Za-z0-9_.-]+)\}$")
_RUNTIME_REF_EMBEDDED = re.compile(r"\$\{runtime\.([A-Za-z0-9_.-]+)\}")


def _expand_fixture_value(value: Any, captures: Mapping[str, Any],
                          runtime: Optional[Mapping[str, Any]] = None) -> Any:
    """Expand fixture captures and per-run ``${runtime.<field>}`` values."""
    runtime = runtime or {}
    if isinstance(value, list):
        return [_expand_fixture_value(item, captures, runtime) for item in value]
    if isinstance(value, dict):
        return {key: _expand_fixture_value(item, captures, runtime) for key, item in value.items()}
    if not isinstance(value, str):
        return value
    def resolve(request_id: str, field: Optional[str]) -> Any:
        if request_id not in captures:
            raise FixtureError("fixture response is unavailable: " + request_id)
        result = captures[request_id]
        if field is None:
            return result
        for part in field.split("."):
            if isinstance(result, dict) and part in result:
                result = result[part]
            elif isinstance(result, list) and part.isdigit() and int(part) < len(result):
                result = result[int(part)]
            else:
                raise FixtureError("fixture response field is unavailable: " + request_id + "." + field)
        return result

    match = _FIXTURE_REF.fullmatch(value)
    if match:
        return resolve(*match.groups())
    match = _RUNTIME_REF.fullmatch(value)
    if match:
        result: Any = runtime
        for part in match.group(1).split("."):
            if isinstance(result, Mapping) and part in result:
                result = result[part]
            else:
                raise FixtureError("runtime value is unavailable: " + match.group(1))
        return result
    if "${fixture." not in value:
        if "${runtime." not in value:
            return value

    def replace(match: re.Match[str]) -> str:
        request_id, field = match.groups()
        return str(resolve(request_id, field))

    value = _FIXTURE_REF_EMBEDDED.sub(replace, value)

    def replace_runtime(match: re.Match[str]) -> str:
        result: Any = runtime
        for part in match.group(1).split("."):
            if isinstance(result, Mapping) and part in result:
                result = result[part]
            else:
                raise FixtureError("runtime value is unavailable: " + match.group(1))
        return str(result)

    return _RUNTIME_REF_EMBEDDED.sub(replace_runtime, value)


def _run_fixture_requests(base_url: str, requests: Iterable[Mapping[str, Any]], *, timeout: float,
                          captures: Optional[Dict[str, Any]] = None,
                          runtime: Optional[Mapping[str, Any]] = None,
                          batch_id: str = "",
                          role: str = "",
                          organization: str = "") -> List[Dict[str, Any]]:
    results: List[Dict[str, Any]] = []
    for raw_request in requests:
        request = _expand_fixture_value(raw_request, captures if captures is not None else {}, runtime)
        request.setdefault("role", role)
        request.setdefault("organization", organization)
        try:
            observation = request_case(base_url, request, timeout=timeout, batch_id=batch_id)
        except Exception as error:
            raise FixtureError("fixture request %s %s failed: %s" %
                               (str(request.get("method", "GET")).upper(),
                                str(request.get("path", "")), error)) from error
        if captures is not None and (request.get("capture_json") or request.get("capture_text")):
            if request.get("capture_json"):
                captured = observation.pop("_captured_json", None)
                if captured is None:
                    raise FixtureError("fixture response is not a JSON object: " + str(request.get("id", "request")))
            else:
                captured = observation.pop("_captured_text", None)
                if captured is None:
                    raise FixtureError("fixture response is not text: " + str(request.get("id", "request")))
            captures[str(request.get("id", "request"))] = captured
        results.append(observation)
    return results


def _fixture_path(value: str) -> Path:
    path = Path(value)
    return path if path.is_absolute() else ROOT / "api-fixtures" / path


def _audit_observation(case: Mapping[str, Any], *, strict: bool) -> Dict[str, Any]:
    audit = case.get("audit", "not-required")
    if audit == "not-required":
        return {"required": False, "status": "not-required"}
    if not isinstance(audit, dict):
        raise ValueError("audit must be an object or 'not-required'")
    required = bool(audit.get("required", True))
    status = audit.get("status", "declared")
    if strict and required and status not in {"observed", "verified"}:
        raise ValueError("strict audit case must be observed or verified")
    return {
        "required": required,
        "status": status,
        "event": audit.get("event"),
        "outcome": audit.get("outcome"),
        "source": audit.get("source", "external-observation"),
    }


def _assert_response(case: Mapping[str, Any], status: int, body: bytes, headers: Mapping[str, str]) -> Dict[str, Any]:
    expected = normalize_expected_status(case)
    if status not in expected:
        detail = ""
        try:
            parsed_error = json.loads(body.decode("utf-8"))
            if isinstance(parsed_error, dict):
                safe_error = {key: parsed_error[key] for key in ("message", "code", "error")
                              if key in parsed_error}
                if safe_error:
                    detail = ", error_fields=" + json.dumps(safe_error, ensure_ascii=False)[:300]
        except (UnicodeDecodeError, json.JSONDecodeError):
            pass
        raise AssertionError("%s %s expected status %s, got %s (body_sha256=%s, body_bytes=%d)" %
                             (str(case.get("method", "GET")).upper(), str(case.get("path", "")),
                              expected, status, sha256_bytes(body), len(body)) + detail)
    assertions = case.get("assert", {})
    if not isinstance(assertions, dict):
        raise ValueError("assert must be an object")
    keys = json_top_level_keys(body)
    required = assertions.get("json_keys", [])
    if required:
        if keys is None or not set(required).issubset(keys):
            raise AssertionError("response JSON keys do not contain %s (keys=%s)" % (required, keys))
    forbidden = assertions.get("json_forbidden_keys", [])
    if forbidden and keys is not None and set(forbidden).intersection(keys):
        raise AssertionError("response contains forbidden JSON keys: %s" % sorted(set(forbidden).intersection(keys)))
    content_type = assertions.get("content_type")
    if content_type and content_type not in headers.get("content-type", ""):
        raise AssertionError("content type does not contain %s" % content_type)
    json_values = assertions.get("json_values", {})
    if json_values:
        try:
            parsed = json.loads(body.decode("utf-8"))
        except (UnicodeDecodeError, json.JSONDecodeError) as error:
            raise AssertionError("json_values requires a JSON response") from error
        if not isinstance(json_values, dict):
            raise ValueError("assert.json_values must be an object")
        for path, expected_value in json_values.items():
            value: Any = parsed
            for part in str(path).split("."):
                if not part:
                    continue
                if isinstance(value, dict) and part in value:
                    value = value[part]
                elif isinstance(value, list) and part.isdigit() and int(part) < len(value):
                    value = value[int(part)]
                else:
                    raise AssertionError("json_values path is unavailable: " + str(path))
            if value != expected_value:
                raise AssertionError("json_values mismatch at %s: expected %r, got %r" %
                                     (path, expected_value, value))
    contains = assertions.get("json_contains", [])
    if contains:
        try:
            parsed = json.loads(body.decode("utf-8"))
        except (UnicodeDecodeError, json.JSONDecodeError) as error:
            raise AssertionError("json_contains requires a JSON response") from error
        if not isinstance(contains, list):
            raise ValueError("assert.json_contains must be a list")
        for requirement in contains:
            if not isinstance(requirement, dict):
                raise ValueError("each json_contains item must be an object")
            value: Any = parsed
            for part in str(requirement.get("path", "")).split("."):
                if not part:
                    continue
                if isinstance(value, dict) and part in value:
                    value = value[part]
                else:
                    raise AssertionError("json_contains path is unavailable: " + str(requirement.get("path", "")))
            if not isinstance(value, list):
                raise AssertionError("json_contains path is not a list: " + str(requirement.get("path", "")))
            where = requirement.get("where", {})
            expected_values = requirement.get("values", {})
            if not isinstance(where, dict) or not isinstance(expected_values, dict):
                raise ValueError("json_contains where and values must be objects")
            matches = [item for item in value if isinstance(item, dict)
                       and all(item.get(key) == expected for key, expected in where.items())]
            if not matches:
                raise AssertionError("json_contains selector matched no row: " + json.dumps(where, ensure_ascii=False, sort_keys=True))
            if not any(all(item.get(key) == expected for key, expected in expected_values.items()) for item in matches):
                raise AssertionError("json_contains values did not match selector: " + json.dumps(where, ensure_ascii=False, sort_keys=True))
    return {
        "expected_status": expected,
        "json_top_level_keys": keys,
        "body_sha256": sha256_bytes(body),
        "body_bytes": len(body),
        "assertions": assertions,
    }


def _multipart_body(document: Mapping[str, Any]) -> Tuple[bytes, str]:
    """Encode a small declarative multipart request for HTTP fixtures.

    The runner only accepts in-memory fixture content here.  It never reads a
    production attachment or writes a business row outside the HTTP API.
    """
    if not isinstance(document, Mapping):
        raise ValueError("multipart must be an object")
    fields = document.get("fields", {})
    file_document = document.get("file")
    if not isinstance(fields, Mapping) or not isinstance(file_document, Mapping):
        raise ValueError("multipart requires fields and file objects")
    field_name = str(file_document.get("field", "file"))
    filename = str(file_document.get("filename", "fixture.bin"))
    content_type = str(file_document.get("content_type", "application/octet-stream"))
    if "content" in file_document and "content_base64" in file_document:
        raise ValueError("multipart file cannot contain both content and content_base64")
    if "content_base64" in file_document:
        import base64
        try:
            content = base64.b64decode(str(file_document["content_base64"]), validate=True)
        except Exception as error:
            raise ValueError("multipart content_base64 is invalid") from error
    else:
        value = file_document.get("content", "")
        if not isinstance(value, str):
            raise ValueError("multipart file content must be text or base64")
        content = value.encode("utf-8")
    boundary = "----ownedApiFixture" + uuid.uuid4().hex
    chunks: List[bytes] = []
    for key, value in fields.items():
        chunks.append(("--" + boundary + "\r\n" +
                       "Content-Disposition: form-data; name=\"" + str(key) + "\"\r\n\r\n" +
                       str(value) + "\r\n").encode("utf-8"))
    chunks.append(("--" + boundary + "\r\n" +
                   "Content-Disposition: form-data; name=\"" + field_name + "\"; filename=\"" +
                   filename.replace('\\', '_').replace('"', '_') + "\"\r\n" +
                   "Content-Type: " + content_type + "\r\n\r\n").encode("utf-8"))
    chunks.append(content)
    chunks.append(("\r\n--" + boundary + "--\r\n").encode("utf-8"))
    return b"".join(chunks), "multipart/form-data; boundary=" + boundary


def request_case(base_url: str, case: Mapping[str, Any], *, timeout: float,
                 batch_id: str = "") -> Dict[str, Any]:
    method = str(case.get("method", "GET")).upper()
    path = str(case.get("path", ""))
    if not path.startswith("/"):
        raise ValueError("case path must start with /")
    url = base_url.rstrip("/") + path
    headers = {str(key): str(value) for key, value in (case.get("headers") or {}).items()}
    headers.setdefault("Accept", "application/json, text/plain, */*")
    body_value = case.get("body")
    body: Optional[bytes] = None
    if case.get("multipart") is not None:
        if case.get("body_raw") is not None or body_value is not None:
            raise ValueError("multipart cannot be combined with body or body_raw")
        body, multipart_content_type = _multipart_body(case["multipart"])
        headers.setdefault("Content-Type", multipart_content_type)
    elif case.get("body_raw") is not None:
        raw = case.get("body_raw")
        if not isinstance(raw, str):
            raise ValueError("body_raw must be a string")
        body = raw.encode("utf-8")
        if case.get("content_type"):
            headers.setdefault("Content-Type", str(case["content_type"]))
    elif body_value is not None:
        body = json.dumps(body_value, ensure_ascii=False, separators=(",", ":")).encode("utf-8")
        headers.setdefault("Content-Type", "application/json")
    request_descriptor = dict(case)
    request_descriptor["headers"] = headers
    request_descriptor["path_template"] = case.get("path_template", path)
    request_descriptor["auth"] = cookie_auth_context(COOKIE_JAR)
    request = Request(url, data=body, headers=headers, method=method)
    started = time.perf_counter()
    try:
        with LOOPBACK_OPENER.open(request, timeout=timeout) as response:
            status = response.status
            response_headers = {key.lower(): value for key, value in response.headers.items()}
            response_body = response.read()
    except HTTPError as error:
        status = error.code
        response_headers = {key.lower(): value for key, value in error.headers.items()}
        response_body = error.read() if error.fp else b""
    except URLError as error:
        raise RuntimeError("HTTP request failed: " + str(error.reason)) from error
    observation = _assert_response(case, status, response_body, response_headers)
    observation.update({
        "id": case.get("id", path),
        "method": method,
        "path": path,
        "url": url,
        "status": status,
        "response_headers": redact_headers(response_headers),
        "request_headers": redact_headers(headers),
        "elapsed_ms": round((time.perf_counter() - started) * 1000, 3),
    })
    observation.update(transcript_fingerprints(
        request_descriptor,
        status,
        response_headers,
        response_body,
        batch_id=batch_id,
    ))
    if case.get("capture_json"):
        try:
            observation["_captured_json"] = json.loads(response_body.decode("utf-8"))
        except (UnicodeDecodeError, json.JSONDecodeError) as error:
            raise AssertionError("fixture response is not valid JSON") from error
    if case.get("capture_text"):
        try:
            captured_text = response_body.decode("utf-8")
            if case.get("capture_text_trim", True):
                captured_text = captured_text.strip()
            observation["_captured_text"] = captured_text
        except UnicodeDecodeError as error:
            raise AssertionError("fixture response is not UTF-8 text") from error
    return observation


def run_cases(case_document: Mapping[str, Any], *, base_url: str, mode: str,
              role: str, organization: str, clone_identity: Optional[str],
              artifacts: Iterable[Path], output: Path, timeout: float,
              cleanup_contract: Optional[Mapping[str, Any]] = None,
              runtime_report: Optional[Path] = None,
              sql_fixture_executor: Optional[Path] = None,
              defer_cleanup: bool = False,
              auth_user: Optional[str] = None,
              password_file: Optional[Path] = None) -> Dict[str, Any]:
    if mode not in {"debug", "strict"}:
        raise ValueError("mode must be debug or strict")
    batch_id = "batch-" + uuid.uuid4().hex
    # Each contract job is an independent authentication boundary.  The
    # orchestrator reuses this module across jobs; retaining the previous
    # CookieJar would let an anonymous/default-deny job inherit the prior
    # manager session and falsely pass authorization checks.
    COOKIE_JAR.clear()
    strict = mode == "strict"
    validate_base_url(base_url, strict=strict)
    if strict and runtime_report is None:
        raise RuntimeContractError("strict API evidence requires a clone runtime report")
    runtime_contract = validate_file(runtime_report, mode) if runtime_report else None
    cases = case_document.get("cases")
    if not isinstance(cases, list) or not cases:
        raise ValueError("case document must contain a non-empty cases list")
    if strict and not clone_identity:
        clone_identity = runtime_contract["runtime_identity"] if runtime_contract else None
    if strict and not clone_identity:
        raise ValueError("strict mode requires clone_identity")
    metadata = source_metadata(
        Path(case_document.get("case_file", output)),
        runtime_contract=runtime_contract,
    )
    if strict and runtime_contract:
        runtime_binding = runtime_contract.get("implementation_binding")
        if runtime_binding and runtime_binding != metadata["implementation_binding"]:
            raise RuntimeContractError("strict runtime implementation binding does not match current implementation")
        if not runtime_binding and runtime_contract.get("source_commit") and runtime_contract["source_commit"] != metadata["source_commit"]:
            raise RuntimeContractError("strict runtime source_commit does not match repository provenance")
    artifact_hashes = {}
    for artifact in artifacts:
        artifact = Path(artifact)
        if not artifact.is_file():
            raise ValueError("artifact does not exist: " + str(artifact))
        artifact_hashes[str(artifact)] = sha256_file(artifact)
    if strict and metadata.get("implementation_dirty", metadata.get("source_dirty")):
        raise ValueError("strict API evidence requires implementation_dirty=false")
    if strict and not artifact_hashes:
        raise ValueError("strict API evidence requires at least one artifact hash")
    cleanup_contract = cleanup_contract or case_document.get("cleanup_contract")
    cleanup_missing: List[str] = []
    if strict:
        if not isinstance(cleanup_contract, Mapping):
            if not defer_cleanup:
                raise ValueError("strict API evidence requires cleanup_contract")
            cleanup_contract = {"cleanup_pending": True}
        if not cleanup_contract_complete(cleanup_contract) and not defer_cleanup:
            raise ValueError("strict cleanup contract is incomplete")
    report: Dict[str, Any] = {
        "schema": 2,
        "status": "executed-fail",
        "execution": "strict" if strict else "diagnostic",
        "strict_evidence": strict,
        "batch_id": batch_id,
        "stimulus_contract_version": STIMULUS_CONTRACT_VERSION,
        "normalizer_version": NORMALIZER_VERSION,
        "validation_mode": "executed",
        "production_access": False,
        "scope": "clone-derived Spring Web API contract runner",
        "route_execution_model": {
            "application": "one owned Spring application per wave",
            "oracle": "persistent Oracle baseline/runtime schema",
            "redis": "reused sss2 Redis database 15",
            "boundary_route": "owned Java source-only route in the same Spring application",
            "boundary_process": False,
            "internal_http_bridge": False,
            "jni_replacement_process": False,
        },
        "started_at": now(),
        **metadata,
        "artifact_sha256": artifact_hashes,
        "clone_identity": clone_identity or "debug-runtime-reused",
        "runtime_report": str(runtime_report) if runtime_report else None,
        "runtime_contract": runtime_contract,
        "role": role,
        "organization": organization,
        "mode": mode,
        "base_url": base_url,
        "case_count": len(cases),
        "cases": [],
        "cleanup": dict(cleanup_contract) if strict else {
            "runtime_reused": True,
            "application_stopped": False,
            "oracle_stopped": False,
            "redis_stopped": False,
            "temporary_files_removed": True,
        },
        "cleanup_pending": bool(strict and not cleanup_contract_complete(cleanup_contract)),
        "phase_timings_ms": {},
        "authentication": {"attempted": bool(auth_user or password_file), "authenticated": False},
    }
    started = time.perf_counter()
    try:
        if bool(auth_user) != bool(password_file):
            raise ValueError("auth_user and password_file must be supplied together")
        if auth_user and password_file:
            authentication = rsa_login(base_url, auth_user, read_password_file(password_file), timeout=timeout)
            report["authentication"] = {"attempted": True, "authenticated": True, "status": authentication["status"]}
        for raw_case in cases:
            if not isinstance(raw_case, dict):
                raise ValueError("each case must be an object")
            case_id = raw_case.get("id")
            if not isinstance(case_id, str) or not case_id:
                raise ValueError("each case requires a non-empty id")
            method = str(raw_case.get("method", "GET")).upper()
            operation = str(raw_case.get("operation", "read"))
            if operation == "write" and method in WRITE_METHODS and raw_case.get("write_via", "http") != "http":
                raise ValueError("business writes must use HTTP API: " + case_id)
            fixture = _fixture_plan(raw_case, strict=strict, sql_executor_available=sql_fixture_executor is not None)
            runtime_values = {
                # T_EMC_RUN.ID is VARCHAR2(15) in the clone-derived schema.
                # Keep the generated fixture key within that production limit.
                "id": "r" + uuid.uuid4().hex[:12],
            }
            audit = _audit_observation(raw_case, strict=strict)
            setup_results: List[Dict[str, Any]] = []
            readback_results: List[Dict[str, Any]] = []
            restore_results: List[Dict[str, Any]] = []
            fixture_captures: Dict[str, Any] = {}
            case_error: Optional[BaseException] = None
            try:
                if fixture["mode"] == "sql-control":
                    if sql_fixture_executor is None:
                        raise FixtureError("sql-control fixture has no executor")
                    fixture["setup_sql"] = [execute_fixture(sql_fixture_executor, _fixture_path(item), "setup")
                                             for item in fixture["setup_sql_files"]]
                setup_results = _run_fixture_requests(
                    base_url, fixture["setup_requests"], timeout=timeout,
                    captures=fixture_captures, runtime=runtime_values,
                    batch_id=batch_id,
                    role=role, organization=organization,
                )
                fixture["setup"] = "executed" if fixture["setup_requests"] else "not-required"
                # Keep restore/setup declarations opaque until their own
                # request is executed.  Expanding the whole case here would
                # try to resolve a restore reference (for example the id
                # returned by the just-executed insert) before that insert
                # has happened.
                request_case_document = dict(raw_case)
                request_case_document.setdefault("role", role)
                request_case_document.setdefault("organization", organization)
                request_case_document.pop("fixture", None)
                # Readback requests are a sequential mini-workflow.  Keep
                # them opaque until after the operation so an earlier
                # readback can capture a URL/text/id used by the next one.
                # Expanding the whole document here would resolve every
                # readback reference before the first readback has run.
                readback_declarations = request_case_document.pop("readback_requests", [])
                expanded_case = _expand_fixture_value(request_case_document, fixture_captures, runtime_values)
                observation = request_case(
                    base_url, expanded_case, timeout=timeout, batch_id=batch_id,
                )
                # A business write may return the generated child-row id that
                # the restore phase needs.  Keep the capture local to this
                # case, just like fixture setup captures, so a subsequent
                # DELETE can restore the row through the real HTTP API rather
                # than querying or mutating business tables with SQL.
                captured_operation = observation.pop("_captured_json", None)
                if captured_operation is None:
                    captured_operation = observation.pop("_captured_text", None)
                if captured_operation is not None:
                    capture_id = str(raw_case.get("capture_as", case_id))
                    fixture_captures[capture_id] = captured_operation
                # Pass declarations to the request loop unchanged.  Each
                # request is expanded immediately before it runs, allowing
                # an earlier readback to populate captures for the next one.
                readback_requests = readback_declarations
                if readback_requests:
                    if not isinstance(readback_requests, list):
                        raise ValueError("readback_requests must be a list")
                readback_results = _run_fixture_requests(
                    base_url, readback_requests, timeout=timeout,
                    captures=fixture_captures, runtime=runtime_values,
                    batch_id=batch_id,
                    role=role, organization=organization,
                )
            except BaseException as failure:
                case_error = failure
                observation = None
            finally:
                try:
                    restore_results = _run_fixture_requests(
                        base_url, fixture["restore_requests"], timeout=timeout,
                        captures=fixture_captures, runtime=runtime_values,
                        batch_id=batch_id,
                        role=role, organization=organization,
                    )
                    if fixture["restore_requests"]:
                        fixture["restore"] = "restored"
                    elif fixture.get("cleanup_via") == "case-http":
                        fixture["restore"] = "case-http-operation"
                    else:
                        fixture["restore"] = "not-required"
                except BaseException as failure:
                    case_error = case_error or failure
                finally:
                    # Business state is restored through HTTP first.  Only
                    # after that may SQL fixture control remove a temporary
                    # schema object; reversing this order can make an API
                    # cleanup call fail because its fixture table vanished.
                    if fixture["mode"] == "sql-control" and sql_fixture_executor is not None:
                        try:
                            fixture["restore_sql"] = [execute_fixture(sql_fixture_executor, _fixture_path(item), "restore")
                                                      for item in fixture["restore_sql_files"]]
                        except BaseException as failure:
                            case_error = case_error or failure
            if case_error is not None:
                raise case_error
            assert observation is not None
            observation["route"] = {
                "method": method,
                "path": raw_case["path"],
                "operation": operation,
                "disposition": raw_case.get("disposition", "owned-pass"),
            }
            if raw_case.get("scope"):
                observation["route"]["scope"] = raw_case["scope"]
            if raw_case.get("route_id"):
                observation["route"]["route_id"] = raw_case["route_id"]
            if isinstance(raw_case.get("route_ids"), list) and raw_case.get("route_ids"):
                observation["route"]["route_ids"] = [str(item) for item in raw_case["route_ids"]]
            if raw_case.get("source_class"):
                observation["class"] = raw_case["source_class"]
            if raw_case.get("behavior_family"):
                observation["behavior_family"] = raw_case["behavior_family"]
            if raw_case.get("behavior_family_id"):
                observation["behavior_family_id"] = raw_case["behavior_family_id"]
            if raw_case.get("family_fingerprint"):
                observation["family_fingerprint"] = raw_case["family_fingerprint"]
            if raw_case.get("request_profile"):
                observation["request_profile"] = raw_case["request_profile"]
            observation["fixture"] = fixture
            observation["fixture"]["runtime_values"] = runtime_values
            observation["fixture"]["setup_http"] = setup_results
            observation["readback_http"] = readback_results
            observation["readback_required"] = bool(raw_case.get("readback_requests"))
            observation["fixture"]["restore_http"] = restore_results
            observation["audit"] = audit
            for field in (
                "application_binding", "harness_binding", "runtime_contract_binding",
                "planning_binding", "governance_binding", "implementation_binding",
                "implementation_tree_digest",
            ):
                observation[field] = metadata.get(field)
            if strict and (method in WRITE_METHODS or operation == "write"):
                observation["rollback"] = {
                    "status": "verified-via-fixture-restore",
                    "source": fixture.get("cleanup_via", "api-fixture-restore"),
                }
            report["cases"].append(observation)
        report["phase_timings_ms"]["http_cases"] = round((time.perf_counter() - started) * 1000, 3)
        report["status"] = ("executed-pass-pending-cleanup" if strict and report.get("cleanup_pending")
                             else "executed-pass" if strict else "diagnostic-pass")
    except Exception as failure:
        report["reason"] = str(failure)
        report["phase_timings_ms"]["http_cases"] = round((time.perf_counter() - started) * 1000, 3)
    report["finished_at"] = now()
    guard_output_path(output)
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    output.chmod(0o600)
    return report


def main(argv: Optional[List[str]] = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--base-url", required=True)
    parser.add_argument("--cases", type=Path, required=True)
    parser.add_argument("--output", type=Path,
                        help="diagnostic output path; canonical evidence requires explicit promotion")
    parser.add_argument("--mode", choices=("debug", "strict"), default="debug")
    parser.add_argument("--role", default="anonymous")
    parser.add_argument("--organization", default="none")
    parser.add_argument("--clone-identity")
    parser.add_argument("--artifact", type=Path, action="append", default=[])
    parser.add_argument("--cleanup-contract", type=Path)
    parser.add_argument("--runtime-report", type=Path)
    parser.add_argument("--sql-fixture-executor", type=Path)
    parser.add_argument("--auth-user")
    parser.add_argument("--password-file", type=Path)
    parser.add_argument("--defer-cleanup", action="store_true")
    parser.add_argument("--timeout", type=float, default=10.0)
    args = parser.parse_args(argv)
    if args.output is None:
        args.output = DIAGNOSTIC_ROOT / ("api-contract-" + args.cases.stem + ".json")
    document = json.loads(args.cases.read_text(encoding="utf-8"))
    document["case_file"] = str(args.cases.resolve())
    cleanup_contract = None
    if args.cleanup_contract:
        cleanup_contract = json.loads(args.cleanup_contract.read_text(encoding="utf-8"))
    report = run_cases(document, base_url=args.base_url, mode=args.mode,
                       role=args.role, organization=args.organization,
                       clone_identity=args.clone_identity, artifacts=args.artifact,
                       output=args.output, timeout=args.timeout,
                       cleanup_contract=cleanup_contract,
                       runtime_report=args.runtime_report,
                       sql_fixture_executor=args.sql_fixture_executor,
                       defer_cleanup=args.defer_cleanup,
                       auth_user=args.auth_user,
                       password_file=args.password_file)
    print("spring API contract runner: " + report["status"])
    if report.get("reason"):
        print(report["reason"])
    return 0 if report["status"] in {"diagnostic-pass", "executed-pass"} else 1


if __name__ == "__main__":
    raise SystemExit(main())
