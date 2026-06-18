#!/bin/bash
echo "=========================================="
echo " Limpieza completa del proyecto Pasteleria"
echo "=========================================="

echo ""
echo "[1/4] Deteniendo y eliminando contenedores del proyecto..."
docker compose down --remove-orphans

echo ""
echo "[2/4] Eliminando imagenes Docker del proyecto (pasteleria-*)..."
IMAGES=$(docker images --filter "reference=pasteleria-*" -q)
if [ -n "$IMAGES" ]; then
    docker rmi -f $IMAGES
else
    echo "  No hay imagenes pasteleria-* para eliminar"
fi

echo ""
echo "[3/4] Eliminando carpetas target de cada microservicio..."
for service in api-gateway auth-service cliente-service producto-service pedido-service envio-service empleado-service asistencia-service; do
    if [ -d "$service/target" ]; then
        echo "  Borrando $service/target"
        rm -rf "$service/target"
    fi
done

echo ""
echo "[4/4] Eliminando volumenes huerfanos no utilizados..."
docker volume prune -f

echo ""
echo "=========================================="
echo " Limpieza completada. Listo para build-all.sh"
echo "=========================================="
