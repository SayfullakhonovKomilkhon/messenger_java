#!/bin/sh
# На Railway Volume монтируется как root. Даём appuser права на запись в /app/uploads.
if [ -d /app/uploads ]; then
  chown -R appuser:appgroup /app/uploads 2>/dev/null || true
fi
exec su-exec appuser java $JAVA_OPTS -jar -Dserver.port=${PORT:-3000} app.jar
