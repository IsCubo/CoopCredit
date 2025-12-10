# Prometheus Configuration

Esta carpeta contiene la configuración de Prometheus para el monitoreo de la aplicación CoopCredit.

## Archivos

- **prometheus.yml**: Configuración principal de Prometheus con los scrape configs para:
  - CoopCredit App (puerto 8081)
  - Risk Service (puerto 8082)

## Configuración

### Scrape Interval
- Global: 15 segundos
- Por job: 10 segundos

### Targets Monitoreados

1. **coopcredit-app**: http://coopcredit-app:8081/actuator/prometheus
2. **risk-service**: http://risk-service:8082/actuator/prometheus

## Uso

La configuración se monta automáticamente en el contenedor de Prometheus a través de `docker-compose.yml`:

```yaml
volumes:
  - ./prometheus/prometheus.yml:/etc/prometheus/prometheus.yml
```

## Acceso

Una vez iniciado el stack con `docker-compose up -d`, accede a Prometheus en:

**http://localhost:9090**

## Modificar Configuración

Para cambiar la configuración:

1. Edita `prometheus.yml`
2. Reinicia el contenedor: `docker-compose restart prometheus`

Los cambios se aplicarán automáticamente.
