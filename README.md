# Customer-Account-Microservices-task


## Overview

A Spring Boot microservices application that demonstrates banking customer and account management with event-driven architecture using Apache Kafka.

## Architecture

- **Customer Service**: Manages bank customer information
- **Account Service**: Handles customer bank accounts  
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
git clone <repository-url>
cd Customer-Account-Microservices-task
