@echo off
echo ==========================================
echo  Limpieza completa del proyecto Pasteleria
echo ==========================================

echo.
echo [1/4] Deteniendo y eliminando contenedores del proyecto...
docker compose down --remove-orphans

echo.
echo [2/4] Eliminando imagenes Docker del proyecto (pasteleria-*)...
FOR /f "tokens=*" %%i IN ('docker images --filter "reference=pasteleria-*" -q') DO docker rmi -f %%i

echo.
echo [3/4] Eliminando carpetas target de cada microservicio...
FOR %%s IN (api-gateway auth-service cliente-service producto-service pedido-service envio-service empleado-service asistencia-service) DO (
    IF EXIST "%%s\target" (
        echo   Borrando %%s\target
        rmdir /s /q "%%s\target"
    )
)

echo.
echo [4/4] Eliminando volumenes huerfanos no utilizados...
docker volume prune -f

echo.
echo ==========================================
echo  Limpieza completada. Listo para build-all.bat
echo ==========================================
