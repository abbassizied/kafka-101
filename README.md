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

**Description:** Creates a new product in the Product Service. This will automatically publish a `product-events` Kafka message to synchronize data with other services.

**Request Body:**
```json
{
  "name": "iPhone 15 Pro",
  "price": 999.99,
  "quantity": 50
}
```

**Example Request:**
```bash
curl -X POST "http://localhost:8081/api/products" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15 Pro",
    "price": 999.99,
    "quantity": 50
  }'
```

**Response:**
```json
{
  "id": 1,
  "name": "iPhone 15 Pro",
  "price": 999.99,
  "quantity": 50,
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

**Kafka Event Published:** `product-events` topic with product data

---

## 👥 Customers

### Create a New Customer
**Endpoint:** `POST /api/customers`

**Description:** Registers a new customer in the Customer Service. This will automatically publish a `customer-events` Kafka message to synchronize data with other services.

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
curl -X POST "http://localhost:8082/api/customers" \
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
- `name` (string): Customer's full name (2-100 characters)
- `email` (string): Unique valid email address
- `phone` (string): Valid phone number format

**Optional Fields:**
- `shippingAddress` (object): Default shipping address
- `billingAddress` (object): Billing address

**Address Fields:**
- `street` (string): Street address
- `city` (string): City name
- `state` (string): State/Province code
- `postalCode` (string): ZIP/Postal code
- `country` (string): Country name

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

**Kafka Event Published:** `customer-events` topic with customer data

---

## 🛒 Orders

### Create a New Order
**Endpoint:** `POST /api/orders`

**Description:** Creates a new order using LOCAL data replicas. Order Service maintains replicas of customer and product data synchronized via Kafka events. No synchronous HTTP calls to other services.

**Request Body:**
```json
{
  "status": "CREATED",
  "customer": 1,
  "orderItems": [
    {
      "quantity": 2,
      "product": 1
    }
  ]
}
```

**Example Request:**
```bash
curl -X POST "http://localhost:8083/api/orders" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "CREATED",
    "customer": 1,
    "orderItems": [
      {
        "quantity": 2,
        "product": 1
      }
    ]
  }'
```

**Required Fields:**
- `status` (string): Order status (CREATED, CONFIRMED, PROCESSING, CANCELLED, COMPLETED)
- `customer` (number): Customer ID from local replica
- `orderItems` (array): List of order items

**Order Item Fields:**
- `quantity` (integer): Positive quantity of the product
- `product` (number): Product ID from local replica

**Response:**
```json
{
  "id": 1,
  "status": "CREATED",
  "customer": 1,
  "orderItems": [
    {
      "id": 1,
      "quantity": 2,
      "product": 1,
      "order": 1
    }
  ],
  "dateCreated": "2024-01-15T10:30:00Z",
  "lastUpdated": "2024-01-15T10:30:00Z"
}
```

---

## 🔄 Event-Driven Architecture Flow

### Data Synchronization:
1. **Product Created** → Product Service publishes `product-events` → Order Service updates local replica
2. **Customer Created** → Customer Service publishes `customer-events` → Order Service updates local replica


### Order Creation Process:
1. **Local Validation**: Check customer/product existence in local replicas
2. **Stock Validation**: Verify sufficient stock in local product replica
3. **Order Persistence**: Save order to local database
4. **Event Publishing**: Send Kafka events for other services to react(if needed, we don't in our case because we haven't inventory service)

---

## 🧪 Testing Workflow

### Step 1: Create Test Data
```bash
# Create product (will replicate to Order Service)
curl -X POST "http://localhost:8081/api/products" \
  -d '{"name": "Test Product", "price": 29.99, "quantity": 100}'

# Create customer (will replicate to Order Service)
curl -X POST "http://localhost:8082/api/customers" \
  -d '{"name": "Test Customer", "email": "test@example.com", "phone": "+1-555-TEST"}'

# Wait a few seconds for Kafka replication
sleep 3
```

### Step 2: Verify Data Replication
```bash
# Check product replica in Order Service
curl "http://localhost:8083/api/products/1"

# Check customer replica in Order Service  
curl "http://localhost:8083/api/customers/1"
```

### Step 3: Create Order (Uses Local Data)
```bash
# This uses LOCAL replicas only - no service calls
curl -X POST "http://localhost:8083/api/orders" \
  -d '{"status": "CREATED", "customer": 1, "orderItems": [{"quantity": 2, "product": 1}]}'
```

### Step 4: Monitor Events
```bash
# Check Kafka topics to verify events were published
# order-events: should contain order created event
# inventory-updates: should contain stock reservation
```

---

## 📊 Service Ports and Responsibilities

| Service | Port | Responsibility | Data Replicated |
|---------|------|----------------|-----------------|
| Product Service | 8081 | Product management | Products → Order Service |
| Customer Service | 8082 | Customer management | Customers → Order Service |
| Order Service | 8083 | Order processing | Local replicas of products & customers |

---

## ⚠️ Common Scenarios & Solutions

### Scenario: "Customer not found" when creating order
**Solution:** Wait for Kafka replication or check if customer was created successfully

### Scenario: "Product not found" when creating order  
**Solution:** Verify product exists and check Kafka connectivity

### Scenario: "Insufficient stock" error
**Solution:** Check product quantity in Product Service, may need restock

---

## 🎯 Testing Tips

1. **Check Kafka Health**: Verify all services are connected to Kafka
2. **Monitor Replication**: Use `curl http://localhost:8083/api/products/1` to check data sync
3. **Event Debugging**: Use Kafdrop or console consumer to monitor events
4. **Order Service Logs**: Check for local validation messages
5. **Timing**: Allow few seconds for Kafka replication between operations

---

## 🔍 Debugging Tools

```bash
# Check Kafka topics
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092

# Monitor order events
docker exec kafka kafka-console-consumer --topic order-events --bootstrap-server localhost:9092

# Monitor product events  
docker exec kafka kafka-console-consumer --topic product-events --bootstrap-server localhost:9092

# Check service health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health  
curl http://localhost:8083/actuator/health
```
