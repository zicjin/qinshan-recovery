#!/usr/bin/env bash
# 核查隔离副本中的验收账号：找出可用口令摘要匹配的账号及其角色，供 AC-05 登录与权限验收使用。
#
# 用法: scripts/check-test-account.sh（口令取自受限材料 private/test-account.env）
#   口令默认取 private/test-account.env 中的 TEST_PASSWORD（受限材料），否则用 612（初始化数据默认口令）。
#
# 口令摘要规则来自前端与隔离初始化数据比对：MD5(口令，无盐)。本脚本只输出用户名与角色，
# 不输出任何口令或摘要值；账号与口令属于受限材料，记录在 private/。
# 仅查询隔离副本，不访问生产。
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROPS="$ROOT/private/application.properties"

prop() { grep -E "^$1=" "$PROPS" | tail -1 | cut -d= -f2- | tr -d '\r'; }
PROD_USER="$(prop spring.datasource.username)"
PROD_PASS="$(prop spring.datasource.password)"
SERVICE="${SERVICE:-xepdb1}"

TEST_PASSWORD="${TEST_PASSWORD:-}"
if [ -z "$TEST_PASSWORD" ] && [ -f "$ROOT/private/test-account.env" ]; then
  TEST_PASSWORD="$(grep -E '^TEST_PASSWORD=' "$ROOT/private/test-account.env" | tail -1 | cut -d= -f2- | tr -d '\r')"
fi
if [ -z "$TEST_PASSWORD" ]; then
  echo "缺少验收口令：请在受限材料 private/test-account.env 中设置 TEST_PASSWORD，或以参数传入" >&2
  exit 1
fi
DIGEST="$(printf '%s' "$TEST_PASSWORD" | md5sum | cut -d' ' -f1)"

docker exec -i \
  -e "LIMS_APP_USER=$PROD_USER" \
  -e "LIMS_APP_PASSWORD=$PROD_PASS" \
  -e "LIMS_SERVICE=$SERVICE" \
  lims-oracle bash -c '
    export ORACLE_HOME=/opt/oracle/product/21c/dbhomeXE PATH="$ORACLE_HOME/bin:$PATH"
    sqlplus -s "$LIMS_APP_USER/$LIMS_APP_PASSWORD@//localhost/$LIMS_SERVICE"
  ' <<SQL
set heading on feedback off pages 200 lines 200
col username format a30
col roles format a60
prompt === 口令摘要匹配的账号 ===
select u.username,
       u.lastsuspendedflag,
       listagg(r.rolename, ', ') within group (order by r.rolename) as roles
  from t_core_user u
  left join t_core_role_user ru on ru.userid = u.id
  left join t_core_role r on r.id = ru.roleid
 where upper(u.password) = upper('$DIGEST')
 group by u.username, u.lastsuspendedflag
 order by u.username;

prompt === 账号总量与角色分布（不含口令信息）===
select count(*) as total_users from t_core_user;
select r.rolename, count(*) as user_count
  from t_core_role r join t_core_role_user ru on ru.roleid = r.id
 group by r.rolename order by user_count desc;
exit
SQL
