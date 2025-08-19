# Kafka 101 - Microservices with Kafka and MySQL

A comprehensive example project demonstrating microservices architecture with Apache Kafka, MySQL, and Spring Boot. This project includes multiple services communicating through Kafka events with a centralized database setup.

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Product       │    │   Customer      │    │   Order         │
│   Service       │    │   Service       │    │   Service       │
│   (8081)        │    │   (8082)        │    │   (8083)        │
└────────┬────────┘    └────────┬────────┘    └────────┬────────┘
         │                      │                      │
         └───────────┐   ┌──────┴───────┐   ┌─────────┘
                     │   │              │   │
                 ┌───▼───▼──────────────▼───▼───┐
                 │        Apache Kafka          │
                 │        (9092)                │
                 └──────────────┬───────────────┘
                                │
                         ┌──────▼──────┐
                         │   MySQL     │
                         │   (3306)    │
                         └─────────────┘
```

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose
- Java 21+
- Maven

### 1. Start Infrastructure
```bash
# Clone the repository
git clone https://github.com/abbassizied/kafka-101.git
cd kafka-101

# Start all services
docker compose up -d
```

### 2. Verify Services
- **Kafka UI (Kafdrop)**: http://localhost:9000
- **MySQL Admin (phpMyAdmin)**: http://localhost:8077
- **Zookeeper**: localhost:2181
- **Kafka Broker**: localhost:9092

### 3. Build and Run Microservices
```bash
# Build all services
mvn clean package

# Run individual services
java -jar product-service/target/*.jar
java -jar customer-service/target/*.jar  
java -jar order-service/target/*.jar
```

## 📊 Service Ports

| Service | Port | Description |
|---------|------|-------------|
| Product Service | 8081 | Manages product catalog |
| Customer Service | 8082 | Handles customer data |
| Order Service | 8083 | Processes orders |
| phpMyAdmin | 8077 | Database administration |
| Kafdrop | 9000 | Kafka topic viewer |

## 🗄️ Database Configuration

### Running Services Locally (on Host Machine)
When your Spring Boot applications run on your local machine (outside Docker), use:
```properties
spring.datasource.url=jdbc:mysql://host.docker.internal:3306/kafka_101_productdb
spring.datasource.url=jdbc:mysql://host.docker.internal:3306/kafka_101_customerdb  
spring.datasource.url=jdbc:mysql://host.docker.internal:3306/kafka_101_orderdb
```

### Running Services in Docker Containers
When services run as Docker containers, use the service name:
```properties
spring.datasource.url=jdbc:mysql://mysql:3306/kafka_101_productdb
spring.datasource.url=jdbc:mysql://mysql:3306/kafka_101_customerdb
spring.datasource.url=jdbc:mysql://mysql:3306/kafka_101_orderdb
```

## ⚙️ Environment Variables

Configure your services using these environment variables:

```bash
# Database Configuration
JDBC_DATABASE_URL=jdbc:mysql://mysql:3306/your_database
JDBC_DATABASE_USERNAME=root
JDBC_DATABASE_PASSWORD=rootpass

# Kafka Configuration  
KAFKA_BOOTSTRAP_SERVERS=kafka:29092
```

## 📋 Available Databases

The MySQL instance contains these databases:
- `kafka_101_productdb` - Product catalog data, Default database (created by Docker)
- `kafka_101_customerdb` - Customer information  
- `kafka_101_orderdb` - Order processing data 

## 🔧 Troubleshooting

### Common Issues

1. **Connection Refused Errors**
   - Ensure all Docker containers are running: `docker ps`
   - Check MySQL health: `docker logs mysql`

2. **Kafka Connection Issues**
   - Verify Kafka is healthy: `docker logs kafka`
   - Check Kafdrop: http://localhost:9000

3. **Database Initialization**
   - Ensure `docker/init-db` directory exists
   - Check SQL files are properly formatted

### Useful Commands

```bash
# View logs
docker compose logs -f

# Restart services
docker compose restart

# Full reset
docker compose down -v
docker compose up -d
```

## 📚 API Documentation

Each service provides Swagger UI documentation:
- Product Service: http://localhost:8081/swagger-ui.html
- Customer Service: http://localhost:8082/swagger-ui.html  
- Order Service: http://localhost:8083/swagger-ui.html

## 🎯 Key Features

- ✅ Microservices architecture with Spring Boot
- ✅ Apache Kafka for event-driven communication
- ✅ MySQL multi-database setup
- ✅ Docker containerization
- ✅ Health checks and monitoring
- ✅ API documentation with Springdoc OpenAPI
- ✅ Centralized logging and error handling

## 📦 Project Structure

```
kafka-101/
├── product-service/     # Product management microservice
├── customer-service/    # Customer management microservice  
├── order-service/       # Order processing microservice
├── docker/             # Docker configuration files
│   ├── init-db/        # Database initialization scripts
│   └── mysql_data/     # MySQL data volume (ignored by git)
└── docker-compose.yml  # Infrastructure definition
```

---
