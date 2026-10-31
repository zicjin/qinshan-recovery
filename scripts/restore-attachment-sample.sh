#!/usr/bin/env bash
# Restore the verified restricted attachment sample's metadata and page link
# into the isolated Oracle copy. The source file remains a read-only runtime
# mount under private/t06-attachment-sample/files/.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROPS="$ROOT/private/application.properties"
SAMPLE_ENV="$ROOT/private/t06-attachment-sample.env"
SERVICE="${SERVICE:-xepdb1}"

[ -f "$SAMPLE_ENV" ] || { echo "缺少受限样本清单: $SAMPLE_ENV" >&2; exit 1; }

required=(T06_FILE_PATH T06_FILE_SHA256 T06_FILE_ID T06_TARGET_ID T06_MD5_NAME
  T06_FILE_EXT T06_SCOPE T06_CREATED_TIME T06_CREATED_BY_ID T06_CREATED_BY_NAME
  T06_CREATED_BY_ORG_ID T06_CREATED_BY_ORG_NAME T06_FILE_SIZE T06_FILE_VERSION
  T06_FILE_NAME T06_EMP_ID T06_EMP_FILE_URL)

is_required_key() {
  local candidate="$1" required_key
  for required_key in "${required[@]}"; do
    [ "$candidate" = "$required_key" ] && return 0
  done
  return 1
}

# Parse the restricted manifest as data, never as shell. Values use the
# checked-in format KEY='value'; only the allowlisted T06_* keys are accepted.
declare -A sample_values=()
while IFS= read -r line || [ -n "$line" ]; do
  line="${line%$'\r'}"
  [[ "$line" =~ ^[[:space:]]*$ || "$line" =~ ^[[:space:]]*# ]] && continue
  [[ "$line" =~ ^(T06_[A-Z0-9_]+)=\'([^\']*)\'[[:space:]]*$ ]] \
    || { echo "受限样本清单格式非法" >&2; exit 1; }
  key="${BASH_REMATCH[1]}"
  value="${BASH_REMATCH[2]}"
  is_required_key "$key" || { echo "受限样本清单包含未允许键: $key" >&2; exit 1; }
  [ -z "${sample_values[$key]+set}" ] || { echo "受限样本清单重复键: $key" >&2; exit 1; }
  sample_values[$key]="$value"
done < "$SAMPLE_ENV"

for key in "${required[@]}"; do
  [ -n "${sample_values[$key]:-}" ] || { echo "受限样本清单缺少 $key" >&2; exit 1; }
  printf -v "$key" '%s' "${sample_values[$key]}"
done

[[ "$T06_FILE_PATH" =~ ^files/[A-Za-z0-9._()/-]+$ && "$T06_FILE_PATH" != *..* ]] \
  || { echo "受限样本文件路径非法" >&2; exit 1; }
[[ "$T06_FILE_SHA256" =~ ^[0-9a-f]{64}$ && "$T06_FILE_ID" =~ ^[0-9]+$ \
  && "$T06_MD5_NAME" =~ ^[0-9a-f]{32}$ && "$T06_FILE_SIZE" =~ ^[0-9]+$ \
  && "$T06_FILE_VERSION" =~ ^[0-9]+$ && "$T06_EMP_ID" =~ ^[A-Za-z0-9_-]+$ ]] \
  || { echo "受限样本数值或哈希字段非法" >&2; exit 1; }
[[ "$T06_CREATED_TIME" =~ ^[0-9]{4}-[0-9]{2}-[0-9]{2}\ [0-9]{2}:[0-9]{2}:[0-9]{2}$ ]] \
  || { echo "受限样本时间字段非法" >&2; exit 1; }

SAMPLE_FILE="$ROOT/private/t06-attachment-sample/$T06_FILE_PATH"
[ -f "$SAMPLE_FILE" ] || { echo "缺少样本文件: $SAMPLE_FILE" >&2; exit 1; }
actual_sha="$(sha256sum "$SAMPLE_FILE" | cut -d' ' -f1)"
[ "$actual_sha" = "$T06_FILE_SHA256" ] || {
  echo "样本文件校验失败: $SAMPLE_FILE" >&2
  exit 1
}

prop() { grep -E "^$1=" "$PROPS" | tail -1 | cut -d= -f2- | tr -d '\r'; }
APP_USER="$(prop spring.datasource.username)"
APP_PASS="$(prop spring.datasource.password)"
[ -n "$APP_USER" ] && [ -n "$APP_PASS" ] || { echo "无法读取隔离数据库账号" >&2; exit 1; }
docker ps --format '{{.Names}}' | grep -qx lims-oracle || { echo "lims-oracle 未运行" >&2; exit 1; }

sql_string() {
  local value="${1//\'/\'\'}"
  printf "'%s'" "$value"
}

T06_TARGET_ID_SQL="$(sql_string "$T06_TARGET_ID")"
T06_MD5_NAME_SQL="$(sql_string "$T06_MD5_NAME")"
T06_FILE_EXT_SQL="$(sql_string "$T06_FILE_EXT")"
T06_SCOPE_SQL="$(sql_string "$T06_SCOPE")"
T06_CREATED_TIME_SQL="$(sql_string "$T06_CREATED_TIME")"
T06_CREATED_BY_ID_SQL="$(sql_string "$T06_CREATED_BY_ID")"
T06_CREATED_BY_NAME_SQL="$(sql_string "$T06_CREATED_BY_NAME")"
T06_CREATED_BY_ORG_ID_SQL="$(sql_string "$T06_CREATED_BY_ORG_ID")"
T06_CREATED_BY_ORG_NAME_SQL="$(sql_string "$T06_CREATED_BY_ORG_NAME")"
T06_FILE_NAME_SQL="$(sql_string "$T06_FILE_NAME")"
T06_EMP_ID_SQL="$(sql_string "$T06_EMP_ID")"
T06_EMP_FILE_URL_SQL="$(sql_string "$T06_EMP_FILE_URL")"

# Feed a generated statement from the restricted manifest. The manifest is
# controlled recovery material and does not enter the tracked repository.
SQL_FILE="$(mktemp)"
trap 'rm -f "$SQL_FILE"' EXIT
cat > "$SQL_FILE" <<SQL
whenever sqlerror exit sql.sqlcode
merge into t_core_file target
using (select $T06_FILE_ID id from dual) source
   on (target.id = source.id)
when matched then update set
  targetid=$T06_TARGET_ID_SQL, md5name=$T06_MD5_NAME_SQL, fileext=$T06_FILE_EXT_SQL,
  scope=$T06_SCOPE_SQL, times=0, createdtime=to_date($T06_CREATED_TIME_SQL,'YYYY-MM-DD HH24:MI:SS'),
  createdbyid=$T06_CREATED_BY_ID_SQL, createdbyname=$T06_CREATED_BY_NAME_SQL, permanent='1',
  downloadable='1', createdbyorgid=$T06_CREATED_BY_ORG_ID_SQL, "SIZE"=$T06_FILE_SIZE,
  version=$T06_FILE_VERSION, createdbyorgname=$T06_CREATED_BY_ORG_NAME_SQL, name=$T06_FILE_NAME_SQL
when not matched then insert (
  id,targetid,md5name,fileext,scope,times,createdtime,createdbyid,createdbyname,
  permanent,downloadable,createdbyorgid,"SIZE",version,createdbyorgname,name
) values (
  $T06_FILE_ID,$T06_TARGET_ID_SQL,$T06_MD5_NAME_SQL,$T06_FILE_EXT_SQL,$T06_SCOPE_SQL,0,
  to_date($T06_CREATED_TIME_SQL,'YYYY-MM-DD HH24:MI:SS'),$T06_CREATED_BY_ID_SQL,$T06_CREATED_BY_NAME_SQL,
  '1','1',$T06_CREATED_BY_ORG_ID_SQL,$T06_FILE_SIZE,$T06_FILE_VERSION,$T06_CREATED_BY_ORG_NAME_SQL,$T06_FILE_NAME_SQL
);
update t_emc_emp set electronicsignature=$T06_EMP_FILE_URL_SQL where id=$T06_EMP_ID_SQL;
commit;
select id,targetid,md5name,"SIZE",name from t_core_file where id=$T06_FILE_ID;
select id,electronicsignature from t_emc_emp where id=$T06_EMP_ID_SQL;
exit
SQL
docker cp "$SQL_FILE" lims-oracle:/tmp/t06-attachment-sample.sql
docker exec -i \
  -e "LIMS_APP_USER=$APP_USER" \
  -e "LIMS_APP_PASSWORD=$APP_PASS" \
  -e "LIMS_SERVICE=$SERVICE" \
  lims-oracle bash -c '
    export ORACLE_HOME=/opt/oracle/product/21c/dbhomeXE PATH="$ORACLE_HOME/bin:$PATH"
    sqlplus -s "$LIMS_APP_USER/$LIMS_APP_PASSWORD@//localhost/$LIMS_SERVICE"
  ' \
  < "$SQL_FILE"

echo "[restore-attachment-sample] 样本文件校验并关联到隔离人员详情: $T06_EMP_ID"
