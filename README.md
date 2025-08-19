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

### Start Infrastructure
```bash
# Clone the repository
git clone https://github.com/abbassizied/kafka-101.git
cd kafka-101

# Start all services
docker compose up -d
```

### Verify Services
- **Kafka UI (Kafdrop)**: http://localhost:9000
- **MySQL Admin (phpMyAdmin)**: http://localhost:8077
- **Zookeeper**: localhost:2181
- **Kafka Broker**: localhost:9092

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
└── compose.yml  # Infrastructure definition
```

---

# API Documentation - POST Requests

## 📦 Products

### Create a New Product
**Endpoint:** `POST /api/products`

**Description:** Creates a new product in the inventory system.

**Request Body:**
```json
{
  "name": "iPhone 15 Pro",
  "description": "Latest Apple smartphone with advanced camera features",
  "sku": "IPHONE15-PRO-256GB",
  "price": 999.99,
  "currentStock": 50,
  "minStockThreshold": 10,
  "category": "ELECTRONICS"
}
```

**Example Request:**
```bash
curl -X POST "http://localhost:8080/api/products" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15 Pro",
    "description": "Latest Apple smartphone with advanced camera features",
    "sku": "IPHONE15-PRO-256GB",
    "price": 999.99,
    "currentStock": 50,
    "minStockThreshold": 10,
    "category": "ELECTRONICS"
  }'
```

**Required Fields:**
- `name` (string): Product name
- `sku` (string): Unique stock keeping unit
- `price` (number): Product price
- `currentStock` (integer): Initial stock quantity

**Optional Fields:**
- `description` (string): Product description
- `minStockThreshold` (integer): Low stock alert threshold (default: 5)
- `category` (string): Product category

**Response:**
```json
{
  "id": 1,
  "name": "iPhone 15 Pro",
  "description": "Latest Apple smartphone with advanced camera features",
  "sku": "IPHONE15-PRO-256GB",
  "price": 999.99,
  "currentStock": 50,
  "minStockThreshold": 10,
  "category": "ELECTRONICS",
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

---

## 👥 Customers

### Create a New Customer
**Endpoint:** `POST /api/customers`

**Description:** Registers a new customer in the system.

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phone": "+1-555-0123",
  "shippingAddress": {
    "street": "123 Main Street",
    "city": "New York",
    "state": "NY",
    "postalCode": "10001",
    "country": "USA"
  },
  "billingAddress": {
    "street": "456 Business Ave",
    "city": "New York",
    "state": "NY",
    "postalCode": "10002",
    "country": "USA"
  }
}
```

**Example Request:**
```bash
curl -X POST "http://localhost:8080/api/customers" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com",
    "phone": "+1-555-0123",
    "shippingAddress": {
      "street": "123 Main Street",
      "city": "New York",
      "state": "NY",
      "postalCode": "10001",
      "country": "USA"
    },
    "billingAddress": {
      "street": "456 Business Ave",
      "city": "New York",
      "state": "NY",
      "postalCode": "10002",
      "country": "USA"
    }
  }'
```

**Required Fields:**
- `name` (string): Customer's full name
- `email` (string): Unique email address
- `phone` (string): Phone number

**Optional Fields:**
- `shippingAddress` (object): Default shipping address
- `billingAddress` (object): Billing address (if different from shipping)

**Address Fields:**
- `street` (string): Street address
- `city` (string): City
- `state` (string): State/Province code
- `postalCode` (string): ZIP/Postal code
- `country` (string): Country

**Response:**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phone": "+1-555-0123",
  "shippingAddress": {
    "street": "123 Main Street",
    "city": "New York",
    "state": "NY",
    "postalCode": "10001",
    "country": "USA"
  },
  "billingAddress": {
    "street": "456 Business Ave",
    "city": "New York",
    "state": "NY",
    "postalCode": "10002",
    "country": "USA"
  },
  "dateCreated": "2024-01-15T10:30:00Z",
  "lastUpdated": "2024-01-15T10:30:00Z"
}
```

---

## ⚠️ Common Error Responses

**400 Bad Request:**
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": ["Email must be valid", "Price must be positive"]
}
```

**409 Conflict:**
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Customer with email already exists"
}
```

**500 Internal Server Error:**
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred"
}
```

---

## 🔄 Validation Rules

### Product Validation:
- `name`: 2-100 characters, required
- `sku`: Unique, 3-50 characters, required
- `price`: Positive number, required
- `currentStock`: Non-negative integer, required
- `minStockThreshold`: Non-negative integer, optional

### Customer Validation:
- `name`: 2-100 characters, required
- `email`: Valid email format, unique, required
- `phone`: Valid phone format, unique, required
- Address fields: All optional, but if provided must follow format

---

## 🎯 Tips for Testing

1. **Use Swagger UI:** Navigate to `http://localhost:8080/swagger-ui.html` for interactive testing
2. **Start with minimal data:** Omit optional fields initially
3. **Check uniqueness:** Ensure email and phone are unique for customers, SKU for products
4. **Validate responses:** Check for proper status codes (201 Created for success)