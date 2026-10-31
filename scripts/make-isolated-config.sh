#!/usr/bin/env bash
# 由生产配置生成隔离副本配置。
#
# 用法: scripts/make-isolated-config.sh [输出文件]
#   输入: private/application.properties（生产配置，受限材料）
#   输出: private/application-isolated.properties（含真实凭据，仍属受限材料）
#
# 除下方「隔离覆盖项」外，其余配置与生产逐字一致。覆盖项只做三件事：
#   1) 指向隔离副本的数据库与 Redis；
#   2) 把外部系统端点（OA、NC、客商、邮件、FTP、文件服务、帆软）改到容器内回环地址，使其无法触达真实外部系统；
#   3) 停用定时任务与设备文件采集（schedule、equipt-acq-file、ngtodo）。
# 生成结果由 scripts/isolation-check.sh 校验不含任何生产或外部地址。
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SRC="$ROOT/private/application.properties"
DST="${1:-$ROOT/private/application-isolated.properties}"

PRIVATE_ROOT="$(realpath -m "$ROOT/private")"
DST="$(realpath -m "$DST")"
case "$DST" in
  "$PRIVATE_ROOT"/*) ;;
  *) echo "隔离配置输出必须位于 private/ 受限目录内" >&2; exit 1 ;;
esac

[ -f "$SRC" ] || { echo "缺少生产配置: $SRC" >&2; exit 1; }

# 从生产配置读取最后一条未注释的 key=value；值中的等号保持不变。
prop() {
  local key="$1"
  awk -F= -v key="$key" '
    $0 !~ /^[[:space:]]*#/ {
      lhs = $1
      gsub(/^[[:space:]]+|[[:space:]]+$/, "", lhs)
      if (lhs == key) {
        value = substr($0, index($0, "=") + 1)
        sub(/\r$/, "", value)
      }
    }
    END { if (value != "") print value }
  ' "$SRC"
}

cp "$SRC" "$DST.tmp"
# Drop comments before applying overrides. Comments in the production file can
# contain alternate endpoints and credentials; they are not needed at runtime
# and must not survive into a handoff configuration or image layer.
sed -i -E '/^[[:space:]]*#/d' "$DST.tmp"

# 只改写第一条生效的 key= 行（兼容键名与 = 之间的空格）。
override() {
  local key="$1" val="$2"
  awk -v key="$key" -v val="$val" '
    BEGIN { done = 0; kr = key; gsub(/[.[\]*^$()+?{|]/, "\\\\&", kr) }
    !done && $0 ~ "^[ \t]*" kr "[ \t]*=" { print key "=" val; done = 1; next }
    { print }
    END { if (!done) print key "=" val }
  ' "$DST.tmp" > "$DST.tmp2"
  mv "$DST.tmp2" "$DST.tmp"
}

cat >> "$DST.tmp" <<'EOF'

#==========================================================#
# 隔离副本覆盖项（由 scripts/make-isolated-config.sh 生成）    #
# 生产与外部系统端点一律指向容器内回环地址；网络边界见          #
# scripts/isolation-check.sh。                                 #
#==========================================================#
EOF

# 1) 隔离副本依赖（Redis 口令与生产一致，从受限材料读取，不写入脚本）
REDIS_PW="$(prop spring.redis.url | sed -nE 's#^redis://:([^@[:space:]]+)@.*#\1#p')"
[ -n "$REDIS_PW" ] || { echo "生产配置缺少可用的 spring.redis.url" >&2; rm -f "$DST.tmp"; exit 1; }
override spring.profiles.active      "dev,oracle,audit-trail,logging,pageoffice"
override spring.datasource.url       "jdbc:oracle:thin:@oracle:1521/XEPDB1"
override spring.redis.url            "redis://:${REDIS_PW}@redis:6379"
# The private framework's global request-to-menu aspect cannot operate against
# this Oracle schema's CLOB URL column (it issues an equality comparison).
# Keep it disabled; the recoverable query routes use the local filter.
override sunway.api.check-access-permission "false"
# Use the framework's signed file URLs for the isolated copy. This protects
# direct /files access without redirecting file requests to production storage.
override sunway.security.file-access-permission.enabled "true"

# 2) 外部端点改到容器内回环地址（不存在的服务，连接立即失败，不会外发）
override sunway.config.cloud.file-server.url        "http://127.0.0.1:18081"
override sunway.config.file.server.url              "http://127.0.0.1:18081"
override sunway.config.fine.independent-deploy-server.url "http://127.0.0.1:18080/webroot"
override sunway.config.fine.independent-deploy-path "/data/LIMS/jiankeyuan/lims-czjky-emc/fineReport"
override sunway.emc.oa.token.url        "http://127.0.0.1:18055/oauth/token"
override sunway.emc.oa.org.url          "http://127.0.0.1:18055/api/MDM_HR_ORGLIST_LIMS"
override sunway.emc.oa.user.url         "http://127.0.0.1:18055/api/MDM/HR_StaffList"
override sunway.emc.oa.client.url       "http://127.0.0.1:18055/api/MDM/KSList"
override sunway.emc.oa.area.url         "http://127.0.0.1:18055/api/mdm/mapoa/region"
override sunway.emc.oa.equipt.url       "http://127.0.0.1:18055/api/stg/oa/zcxxb"
override sunway.emc.nc.jcrwaddservice   "http://127.0.0.1:18055/service/jcrwaddservice"
override sunway.emc.oa.new.client.url       "http://127.0.0.1:18055/prod/information-bi/mdm/selectMerchants"
override sunway.emc.oa.new.client.by-name.url "http://127.0.0.1:18055/prod/information-bi/mdm/selectMerchantsByCodeAndName"
override spring.mail.host               "127.0.0.1"
override spring.mail.port               "465"
override sunway.config.file.ftp.ip      "127.0.0.1"

mv "$DST.tmp" "$DST"
chmod 600 "$DST"
chmod 700 "$(dirname "$DST")"
echo "[make-isolated-config] 生成 $DST"
echo "[make-isolated-config] 生产与外部端点已改写为容器内回环地址；定时任务/文件采集 profile 已停用。"
