#!/bin/bash

# 사용법: ./deploy.sh [prod]
TARGET_ENV=$1
APP_NAME="tpa-gateway"
ROUTE_PATH="/admin/"
# CI/CD env.DEPLOY_PATH와 일치해야 함
BASE_PATH="/home/nex3/app/tpa-admin-auth-api"

if [ "$TARGET_ENV" != "prod" ]; then
  echo "⚠️ 현재 설정은 main 브랜치(prod) 배포만 지원합니다."
  exit 1
fi

# [수정 1] 작업 디렉토리로 확실하게 이동
cd $BASE_PATH || { echo "❌ 경로가 존재하지 않습니다: $BASE_PATH"; exit 1; }

# Prod 환경 설정
ENV_FILE=".env.prod"
NGINX_CONF="/etc/nginx/conf.d/tpa-admin-api.conf"
DEFAULT_PORT="8095"

echo "🚀 ${APP_NAME} (PROD) 배포 시작..."

# [수정 2] 순서 변경: 소스 코드를 제일 먼저 가져와야 함!
echo "🔄 [1] 최신 소스 코드 가져오기..."
git fetch --all
git reset --hard origin/main
git pull origin main

# 1. 환경 파일 준비
if [ -f "${ENV_FILE}" ]; then
  cp "${ENV_FILE}" ".env"
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
    echo "$DEFAULT_PORT" > "$CURRENT_PORT_FILE"
fi

if [ "$CURRENT_PORT" == "8095" ]; then
    TARGET_PORT="8096"
else
    TARGET_PORT="8095"
fi
echo "🔄 Gateway 포트 스위칭: ${CURRENT_PORT} -> ${TARGET_PORT}"

# 3. 컨테이너 기동 준비
export HOST_PORT=$TARGET_PORT
export TARGET_ENV="prod"
export AUTH_IMAGE="tpa-admin-auth:prod"
export GATEWAY_IMAGE="tpa-admin-gateway:prod"
export COMPOSE_PROJECT_NAME="${APP_NAME}-prod-${TARGET_PORT}"

echo "📦 컨테이너 세트 준비: ${COMPOSE_PROJECT_NAME}"

# [수정 3] Docker 이미지 빌드 (코드가 있으므로 이제 성공함)
echo "🔨 [2] Docker 이미지 새로 빌드 중..."

if [ -f "Dockerfile-auth" ]; then
    # --no-cache 옵션 추가 (확실한 갱신)
    docker build --no-cache -t ${AUTH_IMAGE} -f Dockerfile-auth .
else
    echo "❌ Dockerfile-auth 파일이 없습니다!"
    exit 1
fi

# (Gateway Dockerfile이 있다면 주석 해제)
# if [ -f "Dockerfile-gateway" ]; then
#    docker build --no-cache -t ${GATEWAY_IMAGE} -f Dockerfile-gateway .
# fi

echo "📦 [3] 컨테이너 기동..."
docker compose -f docker-compose.yml -p $COMPOSE_PROJECT_NAME up -d || {
    echo "❌ Docker Compose 실행 실패!"
    exit 1
}

# 4. Health Check
echo "🏥 Gateway 헬스체크: http://127.0.0.1:${TARGET_PORT}/actuator/health"
RETRIES=10
for i in $(seq 1 $RETRIES); do
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:${TARGET_PORT}/actuator/health)

  if [ "$STATUS" == "200" ]; then
    echo "✅ 헬스체크 성공!"
    break
  fi

  echo "⏳ 대기 중... ($i/$RETRIES) - HTTP: $STATUS"
  sleep 5

  if [ $i -eq $RETRIES ]; then
    echo "❌ 헬스체크 실패. 롤백을 위해 신규 컨테이너를 제거합니다."
    docker logs ${COMPOSE_PROJECT_NAME}-auth --tail 50
    docker compose -p $COMPOSE_PROJECT_NAME down || true
    exit 1
  fi
done

# 5. Nginx 트래픽 전환
echo "🔄 Nginx 설정을 업데이트합니다..."
sudo sed -i "/location ${ROUTE_PATH//\//\\/}/,/}/ s/127.0.0.1:[0-9]\{4\}/127.0.0.1:${TARGET_PORT}/" $NGINX_CONF
sudo sed -i "/location \/actuator\//,/}/ s/127.0.0.1:[0-9]\{4\}/127.0.0.1:${TARGET_PORT}/" $NGINX_CONF

sudo nginx -t && sudo nginx -s reload

# 6. 구 버전 제거
OLD_PROJECT_NAME="${APP_NAME}-prod-${CURRENT_PORT}"
echo "🛑 이전 버전 제거: ${OLD_PROJECT_NAME}"
docker compose -p $OLD_PROJECT_NAME down || true

# 7. 포트 정보 저장
echo "$TARGET_PORT" > "$CURRENT_PORT_FILE"
echo "🎉 Prod 배포 완료! Gateway Port: ${TARGET_PORT}"