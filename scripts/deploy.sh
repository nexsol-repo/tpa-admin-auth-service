#!/bin/bash

# 사용법: ./deploy.sh prod
TARGET_ENV=$1
APP_NAME="tpa-gateway"
ROUTE_PATH="/admin/" # Nginx에서 Gateway로 보낼 경로
BASE_PATH="/home/nex3/app/tpa-admin-gateway"

if [ "$TARGET_ENV" != "prod" ]; then
  echo "⚠️ 현재 설정은 main 브랜치(prod) 배포만 지원합니다."
  exit 1
fi

# Prod 환경 설정
ENV_FILE=".env.prod"
NGINX_CONF="/etc/nginx/conf.d/tpa-admin.conf" # [확인] 실제 conf 파일명
DEFAULT_PORT="8095" # Gateway Prod 포트 A

echo "🚀 ${APP_NAME} (PROD) 배포 시작..."

# 1. 환경 파일 준비
if [ -f "${BASE_PATH}/${ENV_FILE}" ]; then
  cp "${BASE_PATH}/${ENV_FILE}" "${BASE_PATH}/.env"
else
  echo "❌ .env.prod 파일이 없습니다. 서버에 파일을 생성해주세요."
  exit 1
fi

# 2. Blue-Green 포트 결정
CURRENT_PORT_FILE="${BASE_PATH}/current_port.txt"
if [ -f "$CURRENT_PORT_FILE" ]; then
    CURRENT_PORT=$(cat "$CURRENT_PORT_FILE")
else
    CURRENT_PORT="$DEFAULT_PORT"
fi

# 포트 스위칭 (8095 <-> 8096)
if [ "$CURRENT_PORT" == "8095" ]; then
    TARGET_PORT="8096"
else
    TARGET_PORT="8095"
fi
echo "🔄 Gateway 포트 스위칭: ${CURRENT_PORT} -> ${TARGET_PORT}"

# 3. 컨테이너 기동
export HOST_PORT=$TARGET_PORT
export TARGET_ENV="prod"
export AUTH_IMAGE="tpa-admin-auth:prod"
export GATEWAY_IMAGE="tpa-admin-gateway:prod"
export COMPOSE_PROJECT_NAME="${APP_NAME}-prod-${TARGET_PORT}"

echo "📦 컨테이너 세트 기동: ${COMPOSE_PROJECT_NAME}"
docker compose -f docker-compose.yml -p $COMPOSE_PROJECT_NAME up -d

# 4. Health Check
echo "🏥 Gateway 헬스체크: http://127.0.0.1:${TARGET_PORT}/actuator/health"
for i in {1..20}; do
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:${TARGET_PORT}/actuator/health)
  if [ "$STATUS" == "200" ]; then
    echo "✅ 헬스체크 성공!"
    break
  fi
  echo "⏳ 대기 중... ($i/20) - HTTP: $STATUS"
  sleep 5

  if [ $i -eq 20 ]; then
    echo "❌ 배포 실패. 롤백을 위해 신규 컨테이너를 제거합니다."
    docker logs ${COMPOSE_PROJECT_NAME}-gateway --tail 50
    docker compose -p $COMPOSE_PROJECT_NAME down
    exit 1
  fi
done

# 5. Nginx 트래픽 전환
echo "🔄 Nginx 설정을 업데이트합니다..."
# /admin/ 경로의 proxy_pass 포트 변경
sudo sed -i "/location ${ROUTE_PATH//\//\\/}/,/}/ s/127.0.0.1:[0-9]\{4\}/127.0.0.1:${TARGET_PORT}/" $NGINX_CONF
sudo nginx -t && sudo nginx -s reload

# 6. 구 버전 제거
OLD_PROJECT_NAME="${APP_NAME}-prod-${CURRENT_PORT}"
echo "🛑 이전 버전 제거: ${OLD_PROJECT_NAME}"
docker compose -p $OLD_PROJECT_NAME down || true

# 7. 포트 정보 저장
echo "$TARGET_PORT" > "$CURRENT_PORT_FILE"
echo "🎉 Prod 배포 완료! Gateway Port: ${TARGET_PORT}"