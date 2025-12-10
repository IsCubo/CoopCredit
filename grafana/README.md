# Grafana Configuration

Esta carpeta contiene la configuración de Grafana para la visualización de métricas de la aplicación CoopCredit.

## Estructura

```
grafana/
└── provisioning/
    └── datasources/
        └── datasources.yml
```

## Archivos

- **provisioning/datasources/datasources.yml**: Configuración automática del datasource de Prometheus

## Configuración

### Datasource Automático

El archivo `datasources.yml` configura automáticamente:

- **Nombre**: Prometheus
- **Tipo**: Prometheus
- **URL**: http://prometheus:9090
- **Por defecto**: Sí
- **Editable**: Sí

## Acceso

Una vez iniciado el stack con `docker-compose up -d`, accede a Grafana en:

**http://localhost:3000**

### Credenciales por Defecto

- **Usuario**: admin
- **Contraseña**: admin

## Cambiar Contraseña

```bash
docker exec -it coopcredit_grafana grafana-cli admin reset-admin-password nueva_contraseña
```

## Dashboard Pre-configurado

El dashboard **CoopCredit Application Metrics** se carga automáticamente con los siguientes paneles:

1. **HTTP Request Latency** - Latencia promedio de requests (5m)
2. **HTTP Request Rate** - Tasa de requests por segundo
3. **JVM Heap Memory Used** - Memoria heap usada por la JVM
4. **JVM Live Threads** - Número de threads activos

### Acceder al Dashboard

1. Accede a http://localhost:3000
2. Haz clic en **Dashboards** en el menú lateral
3. Busca **CoopCredit Application Metrics**
4. Haz clic para abrir

## Crear Dashboards Personalizados

1. Accede a http://localhost:3000
2. Haz clic en **Dashboards** → **New** → **New Dashboard**
3. Haz clic en **Add Panel**
4. Selecciona Prometheus como datasource
5. Escribe queries PromQL

## Ejemplos de Queries

```promql
# Tasa de requests por segundo
rate(http.server.requests_total[1m])

# Latencia P95
histogram_quantile(0.95, rate(http.server.requests_bucket[5m]))

# Memoria JVM usada
jvm_memory_used_bytes{area="heap"}

# Threads activos
jvm_threads_live
```

## Persistencia

Los datos de Grafana se persisten en el volumen `grafana_data` definido en `docker-compose.yml`.

Para limpiar todos los datos:

```bash
docker-compose down -v
```

## Modificar Configuración

Para agregar más datasources o cambiar la configuración:

1. Edita `provisioning/datasources/datasources.yml`
2. Reinicia el contenedor: `docker-compose restart grafana`

Los cambios se aplicarán automáticamente.
