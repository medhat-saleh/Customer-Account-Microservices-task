# Customer-Account-Microservices-task


## Overview

A Spring Boot microservices application that demonstrates banking customer and account management with event-driven architecture using Apache Kafka.

## Architecture

- **customer-management-service**: Manages bank customer information
- **Aaccount-management-service**: Handles customer bank accounts  
- **Apache Kafka**: Message broker for event-driven communication
- **PostgreSQL**: Relational database for data persistence

## Prerequisites

- Java 17+
- Maven 3.6+
- Docker
- Apache Kafka (running on localhost:9092)
- PostgreSQL (optional, H2 used by default)

## Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/medhat-saleh/Customer-Account-Microservices-task.git

cd Customer-Account-Microservices-task
### 2 Start Kafka
Ensure you have Kafka running on localhost:9092
# If using Docker (example)
docker run -p 9092:9092 -d apache/kafka:latest
### 3. Build the Project
mvn clean install
###4. Run the Services
# Terminal 1 - Customer Service
mvn spring-boot:run -pl customer-management-service

# Terminal 2 - Account-management-service 
mvn spring-boot:run -pl account-service

2️⃣ Start Kafka

Make sure Kafka is running on localhost:9092.
