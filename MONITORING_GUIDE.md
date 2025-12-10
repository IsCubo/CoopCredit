# Guía de Monitoreo con Prometheus y Grafana

## Descripción General

Este proyecto incluye integración completa con **Prometheus** para recopilación de métricas y **Grafana** para visualización de datos. El sistema monitorea automáticamente:

- Salud de la aplicación
- Métricas de rendimiento HTTP
- Métricas de base de datos
- Métricas de JVM
- Logs estructurados

## Componentes

### Prometheus
- **Puerto**: 9090
- **URL**: http://localhost:9090
- **Función**: Recopila y almacena métricas de las aplicaciones

### Grafana
- **Puerto**: 3000
- **URL**: http://localhost:3000
- **Usuario**: admin
- **Contraseña**: admin
- **Función**: Visualiza métricas en dashboards

### Aplicación CoopCredit
- **Puerto**: 8081
- **Endpoint de Métricas**: http://localhost:8081/actuator/prometheus

### Risk Service
- **Puerto**: 8082
- **Endpoint de Métricas**: http://localhost:8082/actuator/prometheus

## Endpoints de Actuator Disponibles

```
GET /actuator/health              - Estado de salud de la aplicación
GET /actuator/health/liveness     - Liveness probe (¿está viva?)
GET /actuator/health/readiness    - Readiness probe (¿está lista?)
GET /actuator/info                - Información de la aplicación
GET /actuator/metrics             - Lista de todas las métricas disponibles
GET /actuator/metrics/{metric}    - Detalle de una métrica específica
GET /actuator/prometheus          - Métricas en formato Prometheus
GET /actuator/loggers             - Configuración de loggers
GET /actuator/threaddump          - Dump de threads
GET /actuator/heapdump            - Dump de heap (descarga archivo)
```

## Métricas Principales Monitoreadas

### Métricas HTTP
- `http.server.requests` - Duración y cantidad de requests
- `http.server.requests.count` - Total de requests por endpoint
- `http.server.requests.max` - Máxima duración de request

### Métricas de JVM
- `jvm.memory.used` - Memoria JVM usada
- `jvm.memory.max` - Máxima memoria JVM
- `jvm.threads.live` - Threads activos
- `jvm.gc.memory.allocated` - Memoria asignada por GC

### Métricas de Base de Datos
- `jdbc.connections.active` - Conexiones activas
- `jdbc.connections.idle` - Conexiones inactivas
- `jdbc.connections.pending` - Conexiones pendientes

### Métricas de Seguridad
- `spring.security.authentication.failures` - Fallos de autenticación
- `spring.security.authentication.successes` - Autenticaciones exitosas

## Iniciar el Stack Completo

```bash
# Desde el directorio raíz del proyecto
docker-compose up -d

# Verificar que todos los servicios estén corriendo
docker-compose ps
```

## Acceder a las Interfaces

1. **Prometheus**: http://localhost:9090
   - Explorar métricas disponibles
   - Ejecutar queries PromQL
   - Ver estado de targets

2. **Grafana**: http://localhost:3000
   - Crear dashboards personalizados
   - Configurar alertas
   - Visualizar datos de Prometheus

3. **Swagger UI**: http://localhost:8081/swagger-ui.html
   - Documentación interactiva de APIs

## Configurar Grafana

### Paso 1: Login
1. Acceder a http://localhost:3000
2. Usuario: `admin`
3. Contraseña: `admin`

### Paso 2: Dashboard Pre-configurado
El dashboard **CoopCredit Application Metrics** se carga automáticamente con:
- HTTP Request Latency
- HTTP Request Rate
- JVM Heap Memory Used
- JVM Live Threads

Para acceder:
1. Ir a **Dashboards** en el menú lateral
2. Buscar **CoopCredit Application Metrics**
3. Hacer clic para abrir

### Paso 3: Verificar Datasource
1. Ir a **Configuration** → **Data Sources**
2. Verificar que "Prometheus" esté configurado
3. URL debe ser: `http://prometheus:9090`

### Paso 4: Crear Dashboards Personalizados
1. Ir a **Dashboards** → **New** → **New Dashboard**
2. Hacer clic en **Add Panel**
3. Seleccionar Prometheus como datasource
4. Escribir queries PromQL

### Ejemplos de Queries PromQL

```promql
# Tasa de requests por segundo
rate(http.server.requests_total[1m])

# Latencia promedio de requests
histogram_quantile(0.95, rate(http.server.requests_bucket[5m]))

# Memoria JVM usada
jvm_memory_used_bytes{area="heap"}

# Threads activos
jvm_threads_live

# Requests por endpoint
sum(rate(http.server.requests_total[5m])) by (uri)

# Errores HTTP (5xx)
sum(rate(http.server.requests_total{status=~"5.."}[5m])) by (uri)
```

## Estructura de Carpetas

```
CoopCredit/
├── prometheus/
│   └── prometheus.yml          # Configuración de Prometheus
├── grafana/
│   └── provisioning/
│       └── datasources/
│           └── datasources.yml # Configuración de datasources
└── docker-compose.yml          # Orquestación de servicios
```

## Configuración de Prometheus

El archivo `prometheus/prometheus.yml` define:

```yaml
global:
  scrape_interval: 15s      # Intervalo de recopilación
  evaluation_interval: 15s  # Intervalo de evaluación de reglas

scrape_configs:
  - job_name: 'coopcredit-app'
    static_configs:
      - targets: ['coopcredit-app:8081']
    metrics_path: '/actuator/prometheus'
```

## Cambiar Contraseña de Grafana

```bash
# Acceder al contenedor de Grafana
docker exec -it coopcredit_grafana bash

# Cambiar contraseña
grafana-cli admin reset-admin-password nueva_contraseña
```

## Logs Estructurados

La aplicación genera logs en formato JSON para mejor integración con sistemas de monitoreo:

```bash
# Ver logs de la aplicación
docker logs coopcredit_app

# Ver logs de Prometheus
docker logs coopcredit_prometheus

# Ver logs de Grafana
docker logs coopcredit_grafana
```

## Troubleshooting

### Prometheus no recopila métricas
1. Verificar que la aplicación esté corriendo: `docker ps`
2. Verificar conectividad: `docker exec coopcredit_prometheus curl http://coopcredit-app:8081/actuator/prometheus`
3. Revisar logs: `docker logs coopcredit_prometheus`

### Grafana no conecta con Prometheus
1. Ir a Configuration → Data Sources
2. Hacer clic en "Prometheus"
3. Verificar URL: `http://prometheus:9090`
4. Hacer clic en "Test"

### Métricas no aparecen en Grafana
1. Esperar 30-60 segundos para que Prometheus recopile datos
2. En Grafana, seleccionar un rango de tiempo reciente
3. Verificar que el datasource esté correctamente configurado

## Detener el Stack

```bash
docker-compose down

# Detener y eliminar volúmenes (limpia datos)
docker-compose down -v
```

## Referencias

- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
- [Micrometer Prometheus](https://micrometer.io/docs/registry/prometheus)
