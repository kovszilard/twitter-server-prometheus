#!/usr/bin/env bash

HEALTH_ENDPOIT=http://localhost:9990/health
APP_ENDPOIT=http://localhost:8080
METRICS_ENDPOINT=http://localhost:9990/metrics
SHUTDOWN_ENDPOINT=http://localhost:9990/admin/shutdown

SCALA_VERSION=$1
# If SCALA_VERSION not provided
if [ -z "$SCALA_VERSION" ]
then
  SCALA_VERSION=2.12.10
fi

sbt "project example" "++$SCALA_VERSION run" &

# Block until admin is up
STATUS_CODE=$(curl --write-out %{http_code} --silent --output /dev/null $HEALTH_ENDPOIT)

while [[ "$STATUS_CODE" != "200" ]]
do
  echo "Waiting for server to start..."
  sleep 2
  STATUS_CODE=$(curl --write-out %{http_code} --silent --output /dev/null $HEALTH_ENDPOIT)
done

# Increase counter to 1.0
curl --silent --output /dev/null ${APP_ENDPOIT}

COUNTER=$(curl --silent ${METRICS_ENDPOINT} | grep "prometheus_demo_http_requests" | tail -n1 | awk '{print $NF}')

if [ "$COUNTER" != "1.0" ]
then
    curl --silent --output /dev/null -X POST ${SHUTDOWN_ENDPOINT}
    sleep 5
    echo "Counter should be 1.0 and not $COUNTER (scala version: $SCALA_VERSION)"
    exit 1
fi

curl --silent --output /dev/null -X POST ${SHUTDOWN_ENDPOINT}
sleep 5
echo "Test passed on scala $SCALA_VERSION, exiting..."
