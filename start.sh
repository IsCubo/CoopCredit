#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=== CoopCredit Startup Script ===${NC}"
echo ""

# Step 1: Levanta PostgreSQL con docker-compose
echo -e "${BLUE}1. Levantando PostgreSQL...${NC}"
docker-compose -f docker-compose-local.yml down -v 2>/dev/null || true
sleep 2
docker-compose -f docker-compose-local.yml up -d postgres

# Wait for PostgreSQL to be healthy
echo -e "${BLUE}2. Esperando a que PostgreSQL esté listo...${NC}"
for i in {1..30}; do
    if docker-compose -f docker-compose-local.yml exec -T postgres pg_isready -U root > /dev/null 2>&1; then
        echo -e "${GREEN}✓ PostgreSQL está listo${NC}"
        break
    fi
    echo "Intento $i/30..."
    sleep 2
done

# Step 2: Compilar la aplicación
echo ""
echo -e "${BLUE}3. Compilando la aplicación...${NC}"
mvn clean compile -DskipTests -q

# Step 3: Ejecutar Spring Boot
echo ""
echo -e "${BLUE}4. Iniciando Spring Boot...${NC}"
echo -e "${GREEN}✓ Aplicación iniciada en http://localhost:8081${NC}"
echo -e "${GREEN}✓ Swagger UI disponible en http://localhost:8081/swagger-ui/index.html${NC}"
echo ""

mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
