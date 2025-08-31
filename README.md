# Customer-Account-Microservices-task


## Overview

A Spring Boot microservices application that demonstrates banking customer and account management with event-driven architecture using Apache Kafka.

## Architecture

- **customer-management-service**: Manages bank customer information
- **Aaccount-management-service**: Handles customer bank accounts  
- **Apache Kafka**: Message broker for event-driven communication
- **PostgreSQL**: Relational database for data persistence
---

## ✅ Prerequisites
- Java 17+  
- Maven 3.6+  
- Docker  
- Apache Kafka (`localhost:9092`)  
- PostgreSQL (optional, H2 is default)  
- Postman (optional, for API testing)  
- GitHub account  

---

## 🚀 Quick Start

### 1️⃣ Clone or Initialize Repository
If you are starting fresh:
```bash
git init
git remote https://github.com/medhat-saleh/Customer-Account-Microservices-task.git
```

If you already cloned:
```bash
git clone https://github.com/medhat-saleh/Customer-Account-Microservices-task.git
cd banking-microservices
```
## Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/medhat-saleh/Customer-Account-Microservices-task.git

cd Customer-Account-Microservices-task

### 2️⃣ Start Kafka
Make sure Kafka is running on **localhost:9092**.  

```bash
# Example with Docker
docker run -p 9092:9092 -d apache/kafka:latest
```

Or use `docker-compose.yml` (recommended).

### 3️⃣ Build the Project
```bash
mvn clean install
```

### 4️⃣ Run the Services
```bash
# Terminal 1 - Customer Service
mvn spring-boot:run -pl customer-management-service

# Terminal 2 - Account Service
mvn spring-boot:run -pl Account-management-service 
```

---

## 📖 API Documentation
- **Customer Service** → [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)  
- **Account Service** → [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)  

---

## ✨ Features
- RESTful APIs (OpenAPI/Swagger)  
- Event-driven communication via **Kafka**  
- Data validation & business rules enforcement  
- Unit & integration tests with **JaCoCo coverage**  
- **Spring Security** (Basic Auth)  
- Spring Profiles (`dev`, `test`, `prod`)  

---

## 📂 API Endpoints

### 🔹 Customer Service
- `POST /api/customers` → Create new customer  
- `GET /api/customers` → Get all customers  
- `GET /api/customers/{id}` → Get customer by ID  
- `PUT /api/customers/{id}` → Update customer  
- `DELETE /api/customers/{id}` → Delete customer  

### 🔹 Account Service
- `POST /api/accounts` → Create new account  
- `GET /api/accounts` → Get all accounts  
- `GET /api/accounts/{id}` → Get account by ID  
- `GET /api/accounts/customer/{customerId}` → Get accounts by customer ID  
- `PUT /api/accounts/{id}` → Update account  
- `DELETE /api/accounts/{id}` → Delete account  

---

## ⚖️ Business Rules
- Customer ID = **7 digits**  
- Account number = **10 digits**, starts with customer ID  
- Each customer can have **up to 10 accounts**  
- Investment accounts → **min balance 10,000**  
- Retail customers → **only saving accounts**  
- Only **one salary account** per customer  

---

## 🧪 Testing
Run tests with coverage report:  

```bash
mvn test
mvn jacoco:report
```

---

## ⚙️ Configuration

### Profiles
- `dev` → H2 (default)  
- `prod` → PostgreSQL  
- `test` → Testing  

### Kafka (application.yml)
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092

---

## 🐳 Docker Support
Start all services with Docker Compose:  

```bash
docker-compose up -d
```

This spins up:
- Kafka  
- Zookeeper  
- Customer Service  
- Account Service  
- PostgreSQL (if configured)  

---

## 🔄 Event Flow
1. **Customer Created** → `CustomerCreatedEvent` published to Kafka  
2. **Account Service consumes** event → creates account reference  
3. Account operations → relevant events published  
4. Both services stay in sync via **event-driven updates**  

---

## 🧰 Postman Setup
1. Open Postman  
2. Import collection from: `postman/banking-microservices.postman_collection.json`  
3. Use environments:  
   - Customer Service → `http://localhost:8081`  
   - Account Service → `http://localhost:8082`  
4. Run sample requests (Create Customer, Create Account, etc.)  

---

## 🚀 Steps to Push to GitHub
1. Stage files  
```bash
git add .
```

2. Commit changes  
```bash
git commit -m "Initial commit - Banking Microservices with Kafka"
```

3. Push to GitHub  
```bash
git branch -M main
git push -u origin main
```

---

## 📜 License
This project is licensed under the MIT License.
