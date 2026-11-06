#!/usr/bin/env python3
"""Run a read-only, diagnostic API differential scan.

This runner is deliberately a screening tier.  It compares the supplier
baseline and the owned Spring application with the same request, but never
promotes a result to a strict route disposition.  Writes and state-changing
routes are excluded; only a selected inherited/resource read subset is sent.
Output defaults to the feature diagnostic area and cannot be written directly
to canonical evidence.
"""
from __future__ import annotations

import argparse
import base64
import datetime as dt
import hashlib
import json
import os
from pathlib import Path
import re
import subprocess
import sys
from typing import Any, Dict, Iterable, List, Mapping, Optional, Sequence, Tuple
import uuid
from urllib.error import HTTPError, URLError
from urllib.parse import quote, urlencode, urlsplit
from urllib.request import (build_opener, HTTPBasicAuthHandler, HTTPCookieProcessor,
                            ProxyHandler, Request)
import http.cookiejar
sys.path.insert(0, str(Path(__file__).resolve().parent))
from evidence_output import DIAGNOSTIC_ROOT, current_implementation_binding, guard_output_path
from evidence_fingerprint import (
    NORMALIZER_VERSION,
    STIMULUS_CONTRACT_VERSION,
    request_fingerprint,
    response_fingerprint,
)
from source_binding import binding_metadata
from source_binding import REPO_ROOT as DISCOVERED_REPO_ROOT

ROOT = Path(__file__).resolve().parents[1]
REPO_ROOT = DISCOVERED_REPO_ROOT
SAFE_METHODS = {"GET", "POST"}
READ_MARKERS = ("/queries", "/queries/raw", "/info/status", "/gikam-attrs/")
GET_READ_MARKERS = ("/queries", "/info/status", "/gikam-attrs", "/selectable", "/searchable", "/choosable", "/detail", "/columns/", "/action/print")
UNSAFE_MARKERS = (
    "/instant", "/tasks", "/withdraw", "/undo", "/complete",
    "/start-process", "/reject", "/transfer", "/import", "/export",
)
DYNAMIC_KEY = re.compile(r"(?:^|_)(?:id|ids|token|timestamp|time|date|nonce|uuid|session)(?:$|_)", re.I)
PATH_ID = re.compile(r"(?<![A-Za-z])\d{4,}(?![A-Za-z])")
PATH_TEMPLATE = re.compile(r"\{([^{}]+)\}")


class ProbeError(RuntimeError):
    pass


def validate_loopback_url(value: str, label: str) -> str:
    """Require a local URL so screening cannot accidentally reach production.

    The differential tier is intended for the two sss2 endpoints.  Callers
    may use an SSH loopback forward, but a production hostname/IP (or a URL
    carrying credentials) must be rejected before authentication starts.
    """
    parsed = urlsplit(value)
    if parsed.scheme not in {"http", "https"}:
        raise ProbeError(f"{label} must use http(s) loopback URL")
    if parsed.hostname not in {"127.0.0.1", "localhost", "::1"}:
        raise ProbeError(f"{label} must target loopback/sss2 tunnel")
    if parsed.username or parsed.password:
        raise ProbeError(f"{label} must not embed credentials")
    return value.rstrip("/")


def now() -> str:
    return dt.datetime.now(dt.timezone.utc).isoformat()


def git_value(*args: str) -> str:
    return subprocess.check_output(["git", "-C", str(REPO_ROOT), *args], text=True).strip()


def source_dirty(binding: Optional[Mapping[str, Any]] = None) -> bool:
    return bool((binding or current_implementation_binding())["implementation_dirty"])


def read_password(path: Path) -> str:
    if not path.is_file() or path.stat().st_mode & 0o077:
        raise ProbeError("password file must be a regular 0600 file")
    for line in path.read_text(encoding="utf-8").splitlines():
        if line.startswith("TEST_PASSWORD=") and line.split("=", 1)[1]:
            return line.split("=", 1)[1]
    raise ProbeError("password file has no TEST_PASSWORD entry")


def render_path_template(path: str, values: Optional[Mapping[str, str]] = None) -> str:
    """Render a read-only route template with deterministic no-match values.

    Differential probes must exercise the same concrete URI on supplier and
    owned applications.  Numeric identifiers therefore use ``0`` and
    non-identifying path values use a fixed impossible sentinel.  This function
    never selects a real business row and is called only after ``route_safe``
    has classified the route as a read-only query.
    """
    supplied = {str(key): str(value) for key, value in (values or {}).items()}

    def replacement(match: re.Match[str]) -> str:
        name = match.group(1)
        if name in supplied:
            value = supplied[name]
        else:
            normalized = name.lower()
            if normalized == "column" or normalized == "columnname":
                value = "id"
            elif normalized == "columnvalue":
                value = "__sunway_fast_empty__"
            elif "id" in normalized or normalized.endswith(("no", "nos")):
                value = "0"
            else:
                value = "__sunway_fast_empty__"
        return quote(value, safe="")

    return PATH_TEMPLATE.sub(replacement, path)


class Session:
    def __init__(self, base_url: str, timeout: float,
                 role: str = "", organization: str = ""):
        self.base_url = base_url.rstrip("/")
        self.timeout = timeout
        self.role = role
        self.organization = organization
        self.jar = http.cookiejar.CookieJar()
        self.opener = build_opener(ProxyHandler({}), HTTPCookieProcessor(self.jar))

    def request(self, method: str, path: str, body: Any = None) -> Dict[str, Any]:
        url = self.base_url + path
        data = None
        headers = {"Accept": "application/json, text/plain, */*"}
        if body is not None:
            data = json.dumps(body, ensure_ascii=False).encode("utf-8")
            headers["Content-Type"] = "application/json"
        request = Request(url, data=data, headers=headers, method=method.upper())
        request_identity = {
            "method": method.upper(),
            "path_template": path,
            "body": body,
            "headers": headers,
            "role": self.role,
            "organization": self.organization,
            "auth": {
                "cookies": {
                    cookie.name: hashlib.sha256(cookie.value.encode("utf-8")).hexdigest()
                    for cookie in sorted(self.jar, key=lambda item: item.name)
                }
            },
        }
        request_id = request_fingerprint(request_identity)
        try:
            with self.opener.open(request, timeout=self.timeout) as response:
                payload = response.read()
                return self._observation(
                    response.status, response.headers, payload,
                    request_id=request_id,
                )
        except HTTPError as error:
            payload = error.read() if error.fp else b""
            return self._observation(
                error.code, error.headers, payload,
                request_id=request_id,
            )
        except (OSError, URLError, TimeoutError) as error:
            raise ProbeError(f"{method} {path}: {error}") from error

    @staticmethod
    def _observation(status: int, headers: Mapping[str, str], payload: bytes,
                     request_id: str = "") -> Dict[str, Any]:
        content_type = headers.get("Content-Type", "").split(";", 1)[0].strip().lower()
        result: Dict[str, Any] = {
            "status": status,
            "content_type": content_type,
            "body_bytes": len(payload),
            "body_sha256": hashlib.sha256(payload).hexdigest(),
            "request_fingerprint": request_id,
            "response_fingerprint": response_fingerprint(status, headers, payload),
            "stimulus_contract_version": STIMULUS_CONTRACT_VERSION,
            "normalizer_version": NORMALIZER_VERSION,
            "validation_mode": "executed",
        }
        try:
            result["json"] = json.loads(payload.decode("utf-8"))
        except (UnicodeDecodeError, json.JSONDecodeError):
            result["text_prefix"] = payload[:200].decode("utf-8", errors="replace")
        return result

    def login(self, user_id: str, password: str) -> Dict[str, Any]:
        # Keep RSA transport identical to the strict runner without sharing
        # its global cookie jar between supplier and owned sessions.
        # Public keys are base64 text and may exceed the observation prefix;
        # fetch the exact bytes through this session's cookie-aware opener.
        request = Request(self.base_url + "/open/security/public-key", method="GET")
        try:
            with self.opener.open(request, timeout=self.timeout) as response:
                if response.status != 200:
                    raise ProbeError(f"public key returned {response.status}")
                key_bytes = response.read().strip()
        except HTTPError as error:
            raise ProbeError(f"public key returned {error.code}") from error
        except (OSError, URLError, TimeoutError) as error:
            raise ProbeError(f"public key request failed: {error}") from error
        with __import__("tempfile").TemporaryDirectory(prefix="fast-rsa-") as temporary:
            temp = Path(temporary)
            der, pem = temp / "key.der", temp / "key.pem"
            der.write_bytes(base64.b64decode(key_bytes, validate=True))
            converted = subprocess.run(
                ["openssl", "rsa", "-pubin", "-inform", "DER", "-in", str(der), "-out", str(pem)],
                capture_output=True, check=False,
            )
            if converted.returncode:
                raise ProbeError("RSA public-key conversion failed")
            encrypted = subprocess.run(
                ["openssl", "pkeyutl", "-encrypt", "-pubin", "-inkey", str(pem),
                 "-pkeyopt", "rsa_padding_mode:pkcs1"],
                input=password.encode("utf-8"), capture_output=True, check=False,
            )
            if encrypted.returncode:
                raise ProbeError("RSA password encryption failed")
        body = urlencode({"id": user_id, "password": base64.b64encode(encrypted.stdout).decode("ascii")}).encode("ascii")
        request = Request(
            self.base_url + "/core/module/sys/login", data=body,
            headers={"Content-Type": "application/x-www-form-urlencoded",
                     "SECURITY-RSA-KEY": key_bytes.decode("ascii")}, method="POST",
        )
        try:
            with self.opener.open(request, timeout=self.timeout) as response:
                payload = response.read()
                return {"status": response.status, "body_sha256": hashlib.sha256(payload).hexdigest()}
        except HTTPError as error:
            return {"status": error.code, "body_sha256": hashlib.sha256(error.read() if error.fp else b"").hexdigest()}


def route_safe(route: Mapping[str, Any]) -> Tuple[bool, str]:
    method = str(route.get("method", "")).upper()
    path = str(route.get("path", ""))
    kind = str(route.get("route_kind", ""))
    if method not in SAFE_METHODS:
        return False, "method-not-read-only"
    if kind == "web-controller" and method == "GET" and path == "/open/security/public-key":
        return True, "public-key-shape-only"
    if kind == "web-controller" and method == "GET" and path == "/core/module/sys/logins/account":
        return True, "authenticated-session-account-read"
    if kind not in {"inherited-api", "api-resource"}:
        return False, "route-kind-not-selected"
    lower_path = path.lower()
    # ``/action/print`` renders a read-only projection.  Other action routes
    # may mutate state and remain excluded from the fast tier.
    if "/action/" in lower_path and not lower_path.endswith("/action/print"):
        return False, "state-changing-path"
    # ``/queries/tasks`` is a read projection used by the secondary material
    # family.  Do not let the broad ``/tasks`` marker classify that query as a
    # state-changing task endpoint.
    unsafe_path = any(marker in lower_path for marker in UNSAFE_MARKERS)
    if unsafe_path and not (method == "POST" and "/queries/tasks" in lower_path):
        return False, "state-changing-path"
    has_path_template = bool(PATH_TEMPLATE.search(path))
    if method == "GET" and not any(marker in lower_path for marker in GET_READ_MARKERS) and not has_path_template:
        return False, "get-not-query"
    if method == "POST" and not any(marker in path.lower() for marker in READ_MARKERS):
        return False, "post-not-query"
    return True, "read-only-templated-query" if has_path_template else "read-only-query"


def request_profiles(route: Mapping[str, Any], enabled: Optional[Sequence[str]] = None) -> List[Dict[str, Any]]:
    """Return safe, deterministic request variants for one route family.

    Variants are deliberately generic and diagnostic.  They never perform a
    write: page-2 changes only the pagination cursor, ``empty`` uses an
    impossible search token, and ``malformed`` exercises the endpoint's
    validation/error envelope.  Path templates are rendered with impossible
    no-match values before profiles are created.
    """
    method = str(route.get("method", "")).upper()
    route_path = str(route.get("path", ""))
    path = render_path_template(route_path)
    requested = list(enabled or ("default", "page-2", "empty", "malformed"))
    requested_set = set(requested)
    if method == "GET":
        lower_path = path.lower()
        if lower_path.endswith("/action/print"):
            profile_id = "print"
        elif "/columns/" in lower_path:
            profile_id = "column"
        elif lower_path.endswith("/detail") or "/detail/" in lower_path:
            profile_id = "detail"
        elif any(marker in lower_path for marker in ("/selectable", "/searchable", "/choosable")):
            profile_id = "lookup"
        elif "/queries" in lower_path:
            profile_id = "default"
            def with_query(values: str) -> str:
                return path + ("&" if "?" in path else "?") + values
            profiles = {
                "default": {"profile": "default", "method": "GET", "path": path, "body": None},
                "page-2": {"profile": "page-2", "method": "GET", "path": with_query("page=2&size=10"), "body": None},
                "empty": {"profile": "empty", "method": "GET", "path": with_query("keyword=__sunway_fast_empty__"), "body": None},
                "malformed": {"profile": "malformed", "method": "GET", "path": with_query("page=invalid"), "body": None},
            }
            return [profiles[name] for name in requested if name in requested_set and name in profiles]
        else:
            profile_id = "default"
        return [{"profile": profile_id, "method": "GET", "path": path, "body": None}]
    if method == "POST" and "/queries" in path.lower():
        profiles = {
            "default": {"profile": "default", "method": "POST", "path": path, "body": {"p": {"n": 1, "s": 10}}},
            "page-2": {"profile": "page-2", "method": "POST", "path": path, "body": {"p": {"n": 2, "s": 10}}},
            # Use the mapper's actual filter envelope. A top-level q.keyword
            # was silently ignored by both applications, so it never tested filtering.
            "empty": {"profile": "empty", "method": "POST", "path": path, "body": {"p": {"n": 1, "s": 10, "f": {"id_SEQ": "-987654321"}}}, "assert_no_rows": True},
            "malformed": {"profile": "malformed", "method": "POST", "path": path, "body": {"p": {"n": "invalid", "s": 10}}},
            "ascending": {"profile": "ascending", "method": "POST", "path": path, "body": {"p": {"n": 1, "s": 10, "o": [{"id": "asc"}]}}, "assert_order": {"field": "id", "direction": "asc"}},
        }
        return [profiles[name] for name in requested if name in requested_set and name in profiles]
    return []


def request_profile(route: Mapping[str, Any]) -> Optional[Dict[str, Any]]:
    """Backward-compatible default profile accessor used by older callers."""
    profiles = request_profiles(route, ("default",))
    return profiles[0] if profiles else None


def behavior_family_key(route: Mapping[str, Any], profile: Mapping[str, Any] | None = None) -> str:
    """Return a stable probe family for inherited/resource behavior.

    The key intentionally excludes route IDs and source file paths.  Generic
    inherited/resource mappings frequently expose the same HTTP behavior under
    duplicate ledger rows; a single normalized request is enough to screen the
    family, while the report maps the observation back to every route ID.
    Concrete numeric IDs are normalized so equivalent detail/status probes do
    not multiply requests merely because the inventory contains different
    examples.
    """
    method = str(route.get("method", "")).upper()
    path = PATH_ID.sub("{id}", render_path_template(str(route.get("path", ""))))
    profile = dict(profile or request_profile(route) or {})
    if "path" in profile:
        profile["path"] = PATH_ID.sub("{id}", str(profile["path"]))
    source_identity = str(route.get("class") or route.get("source_class") or route.get("class_base") or "unknown-source")
    handler_identity = str(route.get("handler") or route.get("source_method") or route.get("annotation") or "")
    payload = {"method": method, "path": path, "route_kind": str(route.get("route_kind", "")),
               "source_class": source_identity, "source_handler": handler_identity,
               "profile": profile}
    return hashlib.sha256(json.dumps(payload, ensure_ascii=False, sort_keys=True, separators=(",", ":")).encode()).hexdigest()[:16]


def normalize(value: Any, key: str = "") -> Any:
    if isinstance(value, dict):
        return {k: normalize(v, k) for k, v in sorted(value.items())}
    if isinstance(value, list):
        items = [normalize(v, key) for v in value]
        if key.lower() in {"rows", "data", "items", "results"}:
            return sorted(items, key=lambda v: json.dumps(v, ensure_ascii=False, sort_keys=True))
        return items
    if DYNAMIC_KEY.search(key):
        return "<NORMALIZED>"
    if key.lower() in {"logintoken", "logintime", "appuuid"}:
        return "<NORMALIZED_SESSION_VALUE>"
    if key.lower() == "roles" and isinstance(value, str):
        try:
            decoded = json.loads(value)
        except ValueError:
            return value
        if isinstance(decoded, list):
            # The original DTO encodes assignment records as a JSON string;
            # membership matters, while the SQL has no ordering contract.
            return sorted((normalize(item) for item in decoded),
                          key=lambda item: json.dumps(item, ensure_ascii=False, sort_keys=True))
    if key.lower() == "stacktrace" and isinstance(value, str):
        # Different Java implementations necessarily have different diagnostic
        # frames. Retain the envelope/status/error code; normalize frame bodies.
        return "<NORMALIZED_STACKTRACE>"
    if isinstance(value, str) and (len(value) > 24 and re.fullmatch(r"[0-9a-fA-F-]+", value)):
        return "<NORMALIZED>"
    if isinstance(value, str) and (
        "10.100." in value or "218.93." in value or re.search(r":8081(?:/|$)", value)
    ):
        return "<REDACTED_ENDPOINT>"
    return value


def summarize_json(value: Any, key: str = "") -> Any:
    """Summarize response JSON without retaining raw business rows."""
    if isinstance(value, dict):
        return {name: summarize_json(child, name) for name, child in sorted(value.items())}
    if isinstance(value, list):
        if key.lower() in {"rows", "data", "items", "results"}:
            fields = sorted({field for row in value if isinstance(row, dict) for field in row})
            projection = hashlib.sha256(
                json.dumps(normalize(value), ensure_ascii=False, sort_keys=True, separators=(",", ":")).encode("utf-8")
            ).hexdigest()
            return {"count": len(value), "fields": fields, "projection_sha256": projection}
        return [summarize_json(child, key) for child in value]
    return normalize(value, key)


DATA_PROJECTION_KEYS = {
    "rows", "data", "items", "results", "total", "totalPages",
    "numberOfElements", "totalMap", "first", "last",
}


def _projection_fields(value: Mapping[str, Any]) -> Tuple[set[str], set[str]]:
    """Return observed row fields and internal metadata fields.

    ``rows`` is returned by the real resources either as a list of objects or
    as the already-summarised ``{fields: [...]}`` shape.  Empty result pages
    have no row objects, so the response's ``columnTypeMap`` remains the only
    schema hint.  The second set is kept separate so helper SQL columns such
    as ``rn_`` can be ignored without hiding a real business-field drift.
    """
    fields: set[str] = set()
    metadata: set[str] = set()
    for name in ("rows", "data", "items", "results"):
        node = value.get(name)
        if isinstance(node, list):
            fields.update(
                str(field)
                for row in node
                if isinstance(row, Mapping)
                for field in row
            )
        elif isinstance(node, Mapping):
            listed = node.get("fields")
            if isinstance(listed, list):
                fields.update(str(field) for field in listed)
        if node is not None:
            break
    column_map = value.get("columnTypeMap")
    if isinstance(column_map, Mapping):
        metadata.update(str(field) for field in column_map)
    return fields, metadata


def _projection_names(values: Iterable[str]) -> set[str]:
    """Compare Bean field names with SQL metadata without case/underscore drift."""
    return {
        "".join(character for character in str(value).upper() if character.isalnum())
        for value in values
    }


def _projection_declared(fields: set[str], metadata: set[str]) -> bool:
    """Treat the Bean extension envelope as shape metadata, not a SQL column."""
    declared_fields = {value for value in fields if value != "ext$"}
    return _projection_names(declared_fields) <= _projection_names(metadata)


def _data_projection_view(value: Mapping[str, Any]) -> Dict[str, Any]:
    """Drop data-baseline values while retaining response-contract shape.

    Counts, pagination flags, aggregate maps and row values are allowed to
    differ between the supplier snapshot and the owned clone.  Type metadata
    still participates in the comparison, except for SQL helper columns whose
    names end with ``_`` (for example ``rn_``), which are not part of the
    business row projection.
    """
    result = dict(value)
    for key in DATA_PROJECTION_KEYS:
        result.pop(key, None)
    column_map = result.get("columnTypeMap")
    if isinstance(column_map, Mapping):
        result["columnTypeMap"] = {
            str(key): value
            for key, value in column_map.items()
            if not str(key).endswith("_")
        }
    return normalize(result)


def compare(supplier: Mapping[str, Any], owned: Mapping[str, Any], path: str = "",
            profile: Optional[Mapping[str, Any]] = None) -> Tuple[str, Dict[str, Any]]:
    if supplier.get("status") != owned.get("status"):
        return "fast-mismatch", {"reason": "status", "supplier": supplier.get("status"), "owned": owned.get("status")}
    if path == "/open/security/public-key" and supplier.get("status") == owned.get("status") == 200:
        # Supplier content negotiation may label the same base64 key as JSON;
        # compare only the documented RSA wire shape.
        return "fast-equivalent", {"reason": "rsa-public-key-shape"}
    if supplier.get("content_type") != owned.get("content_type"):
        return "fast-mismatch", {"reason": "content_type", "supplier": supplier.get("content_type"), "owned": owned.get("content_type")}
    if "json" in supplier and "json" in owned:
        # Filtering and sorting are behavior, not data-baseline drift. Check
        # profile invariants before normalization drops IDs and sorts rows.
        for side, observation in (("supplier", supplier), ("owned", owned)):
            payload = observation["json"]
            if observation.get("status") != 200 or not isinstance(payload, Mapping):
                continue
            failure = None
            if profile and profile.get("assert_no_rows"):
                # ``empty`` uses a page filter for paged endpoints.  Some read
                # routes return a domain object or validation message instead
                # of a page envelope; the page assertion is not applicable when
                # neither ``rows`` nor ``total`` is exposed.
                paged = isinstance(payload.get("rows"), list) or "total" in payload
                if paged and (payload.get("total") != 0 or payload.get("rows") != []):
                    failure = "no-match-filter-not-applied"
            ordering = profile.get("assert_order") if profile else None
            if ordering:
                rows = payload.get("rows")
                field = ordering["field"]
                if not isinstance(rows, list) or any(not isinstance(row, Mapping) or field not in row for row in rows):
                    failure = "sort-projection-unavailable"
                else:
                    try:
                        values = [row[field] for row in rows]
                        if values != sorted(values, reverse=ordering.get("direction") == "desc"):
                            failure = "requested-sort-not-applied"
                    except TypeError:
                        failure = "sort-values-not-comparable"
            if failure:
                return ("blocked" if side == "supplier" else "fast-mismatch"), {
                    "reason": failure, "side": side,
                    "profile": profile.get("profile") if profile else None,
                }
        left, right = normalize(supplier["json"]), normalize(owned["json"])
        if left != right:
            if isinstance(left, dict) and isinstance(right, dict):
                # First separate envelope/schema drift from data projection
                # drift.  This prevents a clone's row count or ordering from
                # hiding a missing response field or pagination contract.
                left_keys, right_keys = set(left), set(right)
                if left_keys != right_keys:
                    return "fast-mismatch", {
                        "reason": "schema-mismatch",
                        "supplier_fields": sorted(left_keys),
                        "owned_fields": sorted(right_keys),
                    }
                left_fields, left_metadata = _projection_fields(left)
                right_fields, right_metadata = _projection_fields(right)
                if left_fields and right_fields and left_fields != right_fields:
                    if (_projection_names(left_metadata) == _projection_names(right_metadata)
                            and _projection_declared(left_fields, left_metadata)
                            and _projection_declared(right_fields, right_metadata)):
                        return "data-scope-mismatch", {
                            "reason": "nonnull-row-projection-drift",
                            "category": "data-scope-mismatch",
                            "supplier_total": left.get("total"),
                            "owned_total": right.get("total"),
                            "supplier_projection_fields": sorted(left_fields),
                            "owned_projection_fields": sorted(right_fields),
                            "supplier_metadata_fields": sorted(left_metadata),
                            "owned_metadata_fields": sorted(right_metadata),
                        }
                    return "fast-mismatch", {
                        "reason": "projection-schema-mismatch",
                        "supplier_fields": sorted(left_fields),
                        "owned_fields": sorted(right_fields),
                    }
                helper_left, helper_right = dict(left), dict(right)
                for view in (helper_left, helper_right):
                    if isinstance(view.get("columnTypeMap"), Mapping):
                        view["columnTypeMap"] = {k: v for k, v in view["columnTypeMap"].items()
                                                 if str(k).lower() != "rn_"}
                if helper_left == helper_right:
                    return "fast-equivalent", {"reason": "sql-helper-metadata"}
                # Pagination totals and row projections describe the observed
                # data baseline. Keep them out of the envelope/schema
                # comparison so an organization/data snapshot difference is
                # reported as diagnostic data-scope drift rather than a false
                # controller-contract mismatch. ``rn_`` and similar helper
                # columns are excluded by _data_projection_view.
                comparable = _data_projection_view(left)
                comparable_right = _data_projection_view(right)
                if comparable == comparable_right:
                    return "data-scope-mismatch", {"reason": "row-projection-drift", "category": "data-scope-mismatch",
                                                "supplier_total": left.get("total"), "owned_total": right.get("total"),
                                                "supplier_row_count": len(left.get("rows", [])) if isinstance(left.get("rows"), list) else 0,
                                                "owned_row_count": len(right.get("rows", [])) if isinstance(right.get("rows"), list) else 0,
                                                "supplier_projection_fields": sorted(left_fields),
                                                "owned_projection_fields": sorted(right_fields),
                                                "supplier_metadata_fields": sorted(left_metadata),
                                                "owned_metadata_fields": sorted(right_metadata)}
            return "fast-mismatch", {"reason": "normalized-json",
                                    "supplier": summarize_json(left),
                                    "owned": summarize_json(right)}
        return "fast-equivalent", {"reason": "normalized-json"}
    if supplier.get("body_sha256") == owned.get("body_sha256"):
        return "fast-equivalent", {"reason": "body-digest"}
    return "fast-mismatch", {"reason": "body-digest"}


def safe_observation(observation: Mapping[str, Any]) -> Dict[str, Any]:
    """Keep evidence useful while removing raw business rows/endpoints.

    Row payloads are represented by count, field set and a normalized
    projection hash.  This preserves differential value without copying
    supplier business data into diagnostic evidence.
    """
    result = {key: value for key, value in observation.items() if key != "json"}
    if "json" in observation:
        result["json"] = summarize_json(observation["json"])
    if "text_prefix" in result:
        result["text_prefix"] = normalize(result["text_prefix"], "text")
    return result


def validate_execution_plan(plan: Mapping[str, Any], *, head: str,
                           binding: Mapping[str, Any], route_ids: set[str]) -> Dict[str, str]:
    """Validate the planning input without promoting any route behavior."""
    if plan.get("implementation_tree_digest"):
        if plan.get("implementation_tree_digest") != binding["implementation_tree_digest"]:
            raise ProbeError("execution plan implementation binding is stale")
        if plan.get("implementation_binding") != binding["binding_id"]:
            raise ProbeError("execution plan implementation_binding does not match current implementation")
        if plan.get("implementation_dirty") is not False or binding["implementation_dirty"]:
            raise ProbeError("execution plan implementation scope must be clean")
    elif plan.get("source_commit") != head or plan.get("source_dirty") is not False:
        raise ProbeError("legacy execution plan source binding is stale or dirty")
    if plan.get("route_count") != len(route_ids):
        raise ProbeError("execution plan route_count does not cover ledger")
    model = plan.get("route_execution_model")
    if not isinstance(model, dict):
        raise ProbeError("execution plan route_execution_model is missing")
    if model.get("boundary_process") is not False or model.get("internal_http_bridge") is not False:
        raise ProbeError("execution plan declares a boundary process or internal HTTP bridge")
    assignment = plan.get("assignment")
    if not isinstance(assignment, dict) or set(assignment) != route_ids:
        raise ProbeError("execution plan assignment does not cover ledger routes")
    groups = {str(group.get("group_id")): group for group in plan.get("groups", [])}
    if set(assignment.values()) - set(groups):
        raise ProbeError("execution plan assignment references an unknown group")
    return {route_id: str(groups[group_id].get("wave_runtime_id", ""))
            for route_id, group_id in assignment.items()}


def run(args: argparse.Namespace) -> Dict[str, Any]:
    batch_id = "batch-" + uuid.uuid4().hex
    supplier_base_url = validate_loopback_url(args.supplier_base_url, "supplier_base_url")
    owned_base_url = validate_loopback_url(args.owned_base_url, "owned_base_url")
    ledger = json.loads(args.ledger.read_text(encoding="utf-8"))
    head = git_value("rev-parse", "HEAD")
    plan_path = getattr(args, "plan", None)
    # Ledger/plan JSON are generated evidence inputs, not implementation
    # scope. Their regeneration must not invalidate behavior evidence.
    binding = current_implementation_binding()
    binding_fields = binding_metadata(binding)
    if ledger.get("implementation_tree_digest"):
        if ledger.get("implementation_tree_digest") != binding["implementation_tree_digest"]:
            raise ProbeError("ledger implementation binding does not match current implementation")
        if ledger.get("implementation_dirty") is not False or binding["implementation_dirty"]:
            raise ProbeError("ledger implementation scope is dirty")
    elif ledger.get("source_commit") != head or ledger.get("source_dirty") is not False:
        raise ProbeError("legacy ledger source binding is stale or dirty")
    execution_model = ledger.get("route_execution_model")
    if not isinstance(execution_model, dict):
        raise ProbeError("ledger route_execution_model is missing")
    if execution_model.get("boundary_process") is not False or execution_model.get("internal_http_bridge") is not False:
        raise ProbeError("ledger declares a boundary process or internal HTTP bridge")
    routes = list(ledger.get("routes", [])) + list(ledger.get("boundary_routes", []))
    plan_runtime: Dict[str, str] = {}
    plan_assignment: Dict[str, str] = {}
    plan = None
    if plan_path is not None:
        plan = json.loads(Path(args.plan).read_text(encoding="utf-8"))
        plan_runtime = validate_execution_plan(
            plan, head=head, binding=binding,
            route_ids={str(route.get("id")) for route in routes})
        plan_assignment = {str(key): str(value) for key, value in plan["assignment"].items()}
    selected: List[Tuple[Mapping[str, Any], Dict[str, Any]]] = []
    selected_profiles: Dict[str, Dict[str, Any]] = {}
    selected_route_ids: Dict[str, List[str]] = {}
    skipped: Dict[str, int] = {}
    wanted_kinds = set(args.route_kind.split(",")) if args.route_kind else {"inherited-api", "api-resource"}
    wanted_families = set(args.family.split(",")) if args.family else None
    enabled_profiles = tuple(getattr(args, "profiles", "default,page-2,empty,malformed").split(","))
    template_mode = getattr(args, "path_templates", "include")
    for route in routes:
        if wanted_families and str(route.get("family")) not in wanted_families:
            continue
        if str(route.get("route_kind")) not in wanted_kinds:
            continue
        has_path_template = bool(PATH_TEMPLATE.search(str(route.get("path", ""))))
        if template_mode == "only" and not has_path_template:
            continue
        if template_mode == "exclude" and has_path_template:
            skipped["path-template-excluded"] = skipped.get("path-template-excluded", 0) + 1
            continue
        safe, reason = route_safe(route)
        if not safe:
            skipped[reason] = skipped.get(reason, 0) + 1
            continue
        profiles = request_profiles(route, enabled_profiles)
        if not profiles:
            skipped["no-request-profile"] = skipped.get("no-request-profile", 0) + 1
            continue
        for profile in profiles:
            family_id = behavior_family_key(route, profile)
            is_new_probe = family_id not in selected_profiles
            if is_new_probe:
                selected.append((route, profile))
                selected_profiles[family_id] = profile
                selected_route_ids[family_id] = []
            selected_route_ids[family_id].append(str(route.get("id")))
            if args.limit and is_new_probe and len(selected) >= args.limit:
                break
        if args.limit and len(selected) >= args.limit:
            break
    supplier = Session(supplier_base_url, args.timeout, args.role, args.organization)
    owned = Session(owned_base_url, args.timeout, args.role, args.organization)
    password = read_password(args.password_file)
    try:
        auth = {"supplier": supplier.login(args.auth_user, password), "owned": owned.login(args.auth_user, password)}
    except ProbeError as error:
        # Authentication is a batch precondition.  Preserve the diagnostic
        # failure as a report instead of emitting a traceback or pretending
        # that any route was probed.  Per-route connection failures are
        # handled below and become blocked cases.
        auth = {"supplier": {"status": 0}, "owned": {"status": 0}, "error": str(error)}
    if any(
        int(value.get("status", 0)) < 200 or int(value.get("status", 0)) >= 300
        for value in auth.values() if isinstance(value, Mapping)
    ):
        return {
            "schema": 2, "status": "diagnostic-blocked", "execution": "fast-differential",
            "strict_evidence": False, "production_access": False, "source_commit": head,
            "source_dirty": binding["implementation_dirty"],
            "batch_id": batch_id,
            "stimulus_contract_version": STIMULUS_CONTRACT_VERSION,
            "normalizer_version": NORMALIZER_VERSION,
            "validation_mode": "diagnostic",
            **binding_metadata(binding),
            "generated_at": now(),
            "scope": "read-only supplier-vs-owned Spring API screening",
            "route_execution_model": {"application": "same Spring application for owned routes", "boundary_process": False, "internal_http_bridge": False},
            "supplier_base_url": supplier_base_url, "owned_base_url": owned_base_url,
            "execution_plan": str(plan_path) if plan_path is not None else None,
            "execution_plan_id": plan.get("plan_id") if plan is not None else None,
            "role": args.role, "organization": args.organization, "authentication": auth,
            "selection": {"route_kind": sorted(wanted_kinds), "family": sorted(wanted_families) if wanted_families else None,
                           "selected": 0, "probe_families": 0, "deduplicated": 0,
                           "skipped": {"authentication-failed": len(auth)}, "not_safe_to_probe": 0},
            "counts": {"blocked": 1}, "cases": [],
            "limitations": ["authentication failed; no route requests were sent"],
        }
    cases: List[Dict[str, Any]] = []
    for route, profile in selected:
        # ``selected`` is one representative per (route, profile) family;
        # aliases are retained in selected_route_ids for result fan-out.
        family_id = behavior_family_key(route, profile)
        covered_ids = selected_route_ids[family_id]
        group_ids = sorted({plan_assignment.get(route_id) for route_id in covered_ids if plan_assignment.get(route_id)})
        runtime_ids = sorted({plan_runtime.get(route_id) for route_id in covered_ids if plan_runtime.get(route_id)})
        try:
            request_path = str(profile.get("path") or route["path"])
            left = supplier.request(profile["method"], request_path, profile["body"])
            right = owned.request(profile["method"], request_path, profile["body"])
            classification, diff = compare(left, right, request_path, profile=profile)
        except ProbeError as error:
            left = right = {"error": str(error)}
            classification, diff = "blocked", {"reason": str(error)}
        for observed in (left, right):
            if isinstance(observed, dict):
                observed["batch_id"] = batch_id
        cases.append({
            "batch_id": batch_id,
            **binding_fields,
            "route_id": route.get("id") if len(selected_route_ids[family_id]) == 1 else None,
            "route_ids": selected_route_ids[family_id],
            "behavior_family": family_id,
            "method": route.get("method"), "path": route.get("path"),
            "request_path": profile.get("path") or route.get("path"),
            "source": route.get("source"), "class": route.get("class"), "family": route.get("family"),
            "route_kind": route.get("route_kind"), "role": args.role, "organization": args.organization,
            "execution_group": group_ids[0] if len(group_ids) == 1 else ("multiple" if group_ids else None),
            "execution_groups": group_ids,
            "wave_runtime_id": runtime_ids[0] if len(runtime_ids) == 1 else ("multiple" if runtime_ids else None),
            "request": profile, "supplier": safe_observation(left), "owned": safe_observation(right),
            "classification": classification, "diff": diff,
        })
    counts: Dict[str, int] = {
        "fast-equivalent": 0,
        "data-scope-mismatch": 0,
        "fast-mismatch": 0,
        "blocked": 0,
        "not-safe-to-probe": 0,
    }
    for case in cases:
        counts[case["classification"]] = counts.get(case["classification"], 0) + 1
    counts["not-safe-to-probe"] = sum(skipped.values())
    return {
        "schema": 2, "status": "diagnostic-pass", "execution": "fast-differential",
        "strict_evidence": False, "production_access": False, "source_commit": head,
        "source_dirty": binding["implementation_dirty"],
        "batch_id": batch_id,
        "stimulus_contract_version": STIMULUS_CONTRACT_VERSION,
        "normalizer_version": NORMALIZER_VERSION,
        "validation_mode": "diagnostic",
        **binding_metadata(binding),
        "generated_at": now(),
        "scope": "read-only supplier-vs-owned Spring API screening",
        "route_execution_model": {"application": "same Spring application for owned routes", "boundary_process": False, "internal_http_bridge": False},
        "supplier_base_url": supplier_base_url, "owned_base_url": owned_base_url,
        "execution_plan": str(plan_path) if plan_path is not None else None,
        "execution_plan_id": plan.get("plan_id") if plan is not None else None,
        "role": args.role, "organization": args.organization, "authentication": auth,
        "selection": {"route_kind": sorted(wanted_kinds), "family": sorted(wanted_families) if wanted_families else None,
                       # A route may be covered by several read profiles.  Keep
                       # route coverage and HTTP probe count separate so a
                       # profile expansion cannot inflate the inventory count.
                       "selected": len({route_id for ids in selected_route_ids.values() for route_id in ids}),
                       "probe_families": len(selected),
                       "profile_probe_count": len(selected),
                       "deduplicated": len(selected) - len({route_id for ids in selected_route_ids.values() for route_id in ids}),
                       "skipped": skipped,
                       # Skipped inventory rows are intentionally retained as
                       # an explicit safety disposition; they are not HTTP
                       # results and can never count as strict passes.
                       "not_safe_to_probe": sum(skipped.values())},
        "counts": counts, "cases": cases,
        "limitations": ["diagnostic screening only; fast-equivalent never promotes strict pass", "writes and state-changing routes excluded"],
    }


def main(argv: Optional[Sequence[str]] = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--ledger", type=Path, required=True)
    parser.add_argument("--plan", type=Path, required=True,
                        help="current-HEAD execution plan used for group/wave binding")
    parser.add_argument("--supplier-base-url", required=True)
    parser.add_argument("--owned-base-url", required=True)
    parser.add_argument("--auth-user", required=True)
    parser.add_argument("--password-file", type=Path, required=True)
    parser.add_argument("--output", type=Path,
                        help="diagnostic output path; canonical evidence requires explicit promotion")
    parser.add_argument("--family", default="")
    parser.add_argument("--route-kind", default="inherited-api,api-resource")
    parser.add_argument("--profiles", default="default,page-2,empty,malformed",
                        help="comma-separated read profiles (default,page-2,empty,malformed,ascending)")
    parser.add_argument("--path-templates", choices=("include", "exclude", "only"), default="include",
                        help="include or isolate templated routes using deterministic no-match path values")
    parser.add_argument("--role", default="manager")
    parser.add_argument("--organization", default="org-a")
    parser.add_argument("--limit", type=int, default=20)
    parser.add_argument("--timeout", type=float, default=10.0)
    args = parser.parse_args(argv)
    if args.output is None:
        args.output = DIAGNOSTIC_ROOT / ("fast-differential-" + Path(args.ledger).stem + ".json")
    report = run(args)
    guard_output_path(args.output)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    args.output.chmod(0o600)
    print("fast differential API runner:", json.dumps({"selected": report["selection"]["selected"], "counts": report["counts"], "skipped": report["selection"]["skipped"]}, ensure_ascii=False, sort_keys=True))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
