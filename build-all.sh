#!/bin/bash
# Limpia contenedores e imágenes previas (opcional, comentar si no se desea)
# docker rm -f $(docker ps -aq)
# docker rmi -f $(docker images -aq)

set -e

for service in api-gateway auth-service cliente-service empleado-service producto-service pedido-service asistencia-service envio-service; do
  echo "==> Compilando $service"
  (cd "$service" && ./mvnw clean package -DskipTests)
done

cd "$(dirname "$0")"
docker compose up --build
