#!/usr/bin/env python3
"""Deterministic stimulus and response fingerprints for evidence records.

The functions hash normalized request semantics and response shape. Raw
credential values and business rows are never emitted; a raw response is
represented by its SHA-256 and shape/projection metadata.
"""
from __future__ import annotations

import base64
import hashlib
import json
from pathlib import Path
from typing import Any, Mapping
from urllib.parse import parse_qsl, urlsplit


STIMULUS_CONTRACT_VERSION = "3"
NORMALIZER_VERSION = "2"
DEFAULT_ACCEPT = "application/json, text/plain, */*"
SENSITIVE_HEADERS = {
    "authorization",
    "cookie",
    "proxy-authorization",
    "security-rsa-key",
    "set-cookie",
    "x-api-key",
    "x-auth-token",
    "x-csrf-token",
}
ROW_KEYS = ("rows", "data", "items", "results")
PAGINATION_KEYS = (
    "total", "totalPages", "numberOfElements", "first", "last",
    "pageSize", "pageNumber",
)


def _digest(value: Any) -> str:
    payload = json.dumps(value, ensure_ascii=False, sort_keys=True, separators=(",", ":"))
    return hashlib.sha256(payload.encode("utf-8")).hexdigest()


def _sha256_bytes(value: bytes) -> str:
    return hashlib.sha256(value).hexdigest()


def _safe_value(value: Any) -> Any:
    if isinstance(value, Mapping):
        return {str(key): _safe_value(item) for key, item in sorted(value.items())}
    if isinstance(value, list):
        return [_safe_value(item) for item in value]
    if isinstance(value, bytes):
        return {"kind": "bytes", "sha256": _sha256_bytes(value), "bytes": len(value)}
    return value


def _body_descriptor(value: Mapping[str, Any]) -> dict[str, Any]:
    if value.get("multipart") is not None:
        multipart = value["multipart"]
        if not isinstance(multipart, Mapping):
            raise TypeError("multipart must be a mapping")
        fields = multipart.get("fields", {})
        file_value = multipart.get("file", {})
        content = file_value.get("content", "") if isinstance(file_value, Mapping) else ""
        if isinstance(file_value, Mapping) and file_value.get("content_base64") is not None:
            try:
                content_bytes = base64.b64decode(str(file_value["content_base64"]), validate=True)
            except Exception:
                content_bytes = str(file_value["content_base64"]).encode("utf-8")
        else:
            content_bytes = str(content).encode("utf-8")
        return {
            "kind": "multipart",
            "fields": _safe_value(fields),
            "file": {
                "field": file_value.get("field", "file") if isinstance(file_value, Mapping) else "file",
                "filename": file_value.get("filename", "fixture.bin") if isinstance(file_value, Mapping) else "fixture.bin",
                "content_type": file_value.get("content_type", "application/octet-stream") if isinstance(file_value, Mapping) else "application/octet-stream",
                "content_sha256": _sha256_bytes(content_bytes),
                "content_bytes": len(content_bytes),
            },
        }
    if value.get("body_raw") is not None:
        raw = str(value["body_raw"]).encode("utf-8")
        return {"kind": "raw", "sha256": _sha256_bytes(raw), "bytes": len(raw)}
    if value.get("body") is not None:
        return {"kind": "json", "value": _safe_value(value["body"])}
    return {"kind": "empty"}


def _query_descriptor(path: str, query: Any) -> list[list[str]]:
    parsed = urlsplit(path)
    pairs = [(str(key), str(item)) for key, item in parse_qsl(parsed.query, keep_blank_values=True)]
    if isinstance(query, Mapping):
        for key, value in query.items():
            if isinstance(value, list):
                pairs.extend((str(key), str(item)) for item in value)
            else:
                pairs.append((str(key), str(value)))
    elif isinstance(query, list):
        pairs.extend((str(key), str(value)) for key, value in query)
    return [list(item) for item in sorted(pairs)]


def _header_descriptor(headers: Any) -> dict[str, str]:
    values = {str(key).lower(): str(value) for key, value in (headers or {}).items()}
    values.setdefault("accept", DEFAULT_ACCEPT)
    result: dict[str, str] = {}
    for key, value in sorted(values.items()):
        if key in SENSITIVE_HEADERS:
            result[key] = "sha256:" + _sha256_bytes(value.encode("utf-8"))
        else:
            result[key] = value
    return result


def _auth_descriptor(value: Any) -> dict[str, Any]:
    if not value:
        return {"present": False}
    return {
        "present": True,
        "keys": sorted(str(key) for key in value) if isinstance(value, Mapping) else ["opaque"],
        "sha256": _digest(_safe_value(value)),
    }


def request_fingerprint(value: Mapping[str, Any]) -> str:
    """Hash method, path template, normalized query/body, headers and role."""
    if not isinstance(value, Mapping):
        raise TypeError("request descriptor must be a mapping")
    path_template = str(value.get("path_template") or value.get("path") or "")
    path_only = urlsplit(path_template).path
    payload = {
        "stimulus_contract_version": STIMULUS_CONTRACT_VERSION,
        "method": str(value.get("method", "GET")).upper(),
        "path_template": path_only,
        "query": _query_descriptor(path_template, value.get("query")),
        "body": _body_descriptor(value),
        "headers": _header_descriptor(value.get("headers")),
        "role": str(value.get("role", "")),
        "organization": str(value.get("organization", "")),
        "auth": _auth_descriptor(value.get("auth")),
    }
    return "req-" + _digest(payload)


def _json_shape(value: Any) -> Any:
    if isinstance(value, Mapping):
        return {str(key): _json_shape(item) for key, item in sorted(value.items())}
    if isinstance(value, list):
        return {"type": "array", "length": len(value), "items": [_json_shape(item) for item in value[:3]]}
    return type(value).__name__


def _projection_descriptor(value: Any) -> dict[str, Any]:
    if not isinstance(value, Mapping):
        return {}
    rows = None
    row_key = None
    for key in ROW_KEYS:
        if isinstance(value.get(key), list):
            rows = value[key]
            row_key = key
            break
    if rows is None:
        return {}
    fields = sorted({
        str(field)
        for row in rows
        if isinstance(row, Mapping)
        for field in row
    })
    normalized_rows = _safe_value(rows)
    return {
        "key": row_key,
        "count": len(rows),
        "fields": fields,
        "projection_sha256": _digest(normalized_rows),
    }


def _pagination_descriptor(value: Any) -> dict[str, Any]:
    if not isinstance(value, Mapping):
        return {}
    return {
        key: _safe_value(value[key])
        for key in PAGINATION_KEYS
        if key in value
    }


def response_fingerprint(status: int, headers: Mapping[str, str], body: bytes) -> str:
    """Hash status, content type, raw body and normalized response shape."""
    header_values = {str(key).lower(): str(value) for key, value in (headers or {}).items()}
    content_type = header_values.get("content-type", "").split(";", 1)[0].strip().lower()
    parsed: Any = None
    is_json = "json" in content_type
    if is_json:
        try:
            parsed = json.loads(body.decode("utf-8"))
        except (UnicodeDecodeError, json.JSONDecodeError):
            is_json = False
    payload = {
        "normalizer_version": NORMALIZER_VERSION,
        "status": int(status),
        "content_type": content_type,
        "body_sha256": _sha256_bytes(body),
        "body_bytes": len(body),
        "shape": _json_shape(parsed) if is_json else ("text" if body else "empty"),
        "projection": _projection_descriptor(parsed) if is_json else {},
        "pagination": _pagination_descriptor(parsed) if is_json else {},
    }
    return "res-" + _digest(payload)


def transcript_fingerprints(
    request: Mapping[str, Any],
    response_status: int,
    response_headers: Mapping[str, str],
    response_body: bytes,
    *,
    batch_id: str = "",
    validation_mode: str = "executed",
) -> dict[str, str]:
    """Return the per-case fingerprint fields required by strict records."""
    return {
        "request_fingerprint": request_fingerprint(request),
        "stimulus_contract_version": STIMULUS_CONTRACT_VERSION,
        "response_fingerprint": response_fingerprint(
            response_status, response_headers, response_body,
        ),
        "normalizer_version": NORMALIZER_VERSION,
        "validation_mode": validation_mode,
        "batch_id": batch_id,
    }


if __name__ == "__main__":
    raise SystemExit("import evidence_fingerprint from a runner or test")
