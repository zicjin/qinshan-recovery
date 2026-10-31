"""Synthetic role/organization fixture shared by isolated permission replays.

Roles describe explicit grants, never an implicit administrator bypass. The
caller supplies the ephemeral credential digest and keeps generated SQL private.
"""
import re
from pathlib import Path

ROLES = {
    "reader": (101, ("20251000001",)),
    "manager": (102, ("101501",)),
    "admin": (103, ("101501", "100900")),
    "role-admin": (104, ("100900",)),
}
ORGANIZATIONS = {"org-a": ("a", "11", 23, 1), "org-b": ("b", "12", 1, 2)}


def actors():
    return [dict(role=role, role_id=rid, menus=list(menus), organization=org,
                 user_id="matrix-" + role + "-" + values[0], category=values[1],
                 visible_rows=values[2], visible_id=values[3])
            for role, (rid, menus) in ROLES.items()
            for org, values in ORGANIZATIONS.items()]


def grants(role):
    """Return fixture-control grants used only to restore a revoked role.

    The initial role/user/menu rows live in the SQL fixture.  This tiny
    restoration fragment is test control, never evidence of an API write.
    """
    rid, menus = ROLES[role]
    return "\n".join("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (%d,'T_CORE_MENU','%s');" %
                     (rid, menu) for menu in menus)


def seed(credential_digest):
    if not re.fullmatch(r"[0-9a-f]{32}", credential_digest):
        raise ValueError("synthetic credential digest must be MD5")
    fixture = Path(__file__).with_name("fixtures") / "role-menu-matrix.sql"
    sql = fixture.read_text(encoding="utf-8").replace("${credential_digest}", credential_digest)
    if "${credential_digest}" in sql:
        raise AssertionError("role fixture credential placeholder was not expanded")
    return sql
