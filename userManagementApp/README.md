User Management System with RBAC – Backend Engineer Code Assignment
Objective

Design and implement a User Management System with Role-Based Access Control (RBAC) using Spring Boot and MySQL. The system is secure, modular, and production-ready, demonstrating the ability to build scalable backend architectures.

Functional Endpoints & Example curl Commands
1. User Registration

Endpoint: POST /api/users/register

Request Body:

{
"username": "testuser",
"email": "test@example.com",
"password": "password123"
}


Example curl:

curl -X POST http://localhost:8080/api/users/register \
-H "Content-Type: application/json" \
-d '{
"username": "testuser",
"email": "test@example.com",
"password": "password123"
}'

2. User Login

Endpoint: POST /api/users/login

Request Body:

{
"email": "test@example.com",
"password": "password123"
}


Example curl:

curl -X POST http://localhost:8080/api/users/login \
-H "Content-Type: application/json" \
-d '{
"email": "test@example.com",
"password": "password123"
}'


Sample Response:

{
"token": "<JWT_TOKEN>",
"id": 1,
"username": "testuser",
"email": "test@example.com",
"roles": ["USER"]
}

3. View Current User Profile (Cached)

Endpoint: GET /api/users/me

Requires JWT token in Authorization header.

Example curl:

curl -X GET http://localhost:8080/api/users/me \
-H "Authorization: Bearer <JWT_TOKEN>"


Response:

{
"id": 1,
"username": "testuser",
"email": "test@example.com",
"roles": ["USER"]
}

4. Create Role (Admin Only)

Endpoint: POST /api/roles

Requires Admin JWT token

Request Body:

{
"name": "ADMIN"
}


Example curl:

curl -X POST http://localhost:8080/api/roles \
-H "Content-Type: application/json" \
-H "Authorization: Bearer <ADMIN_JWT_TOKEN>" \
-d '{
"name": "ADMIN"
}'


Response:

{
"id": 2,
"name": "ADMIN"
}

5. Assign Roles to User (Admin Only)

Endpoint: POST /api/users/{userId}/roles

Request Body:

{
"roleNames": ["USER", "ADMIN"]
}


Example curl:

curl -X POST http://localhost:8080/api/users/1/roles \
-H "Content-Type: application/json" \
-H "Authorization: Bearer <ADMIN_JWT_TOKEN>" \
-d '{
"roleNames": ["USER", "ADMIN"]
}'


Response:

{
"userId": 1,
"roles": ["USER", "ADMIN"]
}

6. Admin Stats (Admin Only)

Endpoint: GET /api/admin/stats

Example curl:

curl -X GET http://localhost:8080/api/admin/stats \
-H "Authorization: Bearer <ADMIN_JWT_TOKEN>"


Sample Response:

{
"totalUsers": 10,
"lastLogins": [
{"userId": 1, "lastLogin": "2025-12-08T18:34:29"},
{"userId": 2, "lastLogin": "2025-12-08T18:32:11"}
]
}

Technical Requirements

Framework: Spring Boot 3.x
Security: Spring Security, JWT, BCrypt
Database: MySQL with Spring Data JPA & Hibernate
Caching: Spring Cache on /api/users/me endpoint
Validation: Spring Validation (JSR-380 / Bean Validation)
DTO Mapping: MapStruct for UserResponse mapping
Error Handling: Global with @ControllerAdvice
Authentication: Stateless JWT-based authentication via Authorization header

Docker Setup
Dockerfile (Spring Boot app)

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/user-management-app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]


docker-compose.yml
version: '3.8'

services:
mysql:
image: mysql:8.0
command: --default-authentication-plugin=mysql_native_password
environment:
MYSQL_ROOT_PASSWORD: root
MYSQL_DATABASE: user_management
ports:
- "3306:3306"
volumes:
- mysql-data:/var/lib/mysql
healthcheck:
test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
interval: 10s
timeout: 5s
retries: 10

kafka:
image: confluentinc/cp-kafka:7.4.0
container_name: kafka
ports:
- "9092:9092"
environment:
# KRaft mode settings (no Zookeeper)
KAFKA_NODE_ID: 1
KAFKA_PROCESS_ROLES: broker,controller
KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093
KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
#KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT
KAFKA_CONTROLLER_QUORUM_VOTERS: 1@kafka:9093
KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
KAFKA_AUTO_CREATE_TOPICS_ENABLE: 'true'
# Generate this once and keep it:
CLUSTER_ID: cp4-Z4MhSyS56YNPTyQmHw   # ← Run: docker run --rm confluentinc/cp-kafka:7.4.0 kafka-storage random-uuid
    healthcheck:
      test: ["CMD", "kafka-broker-api-versions", "--bootstrap-server", "localhost:9092"]
      interval: 10s
      timeout: 5s
      retries: 15
    volumes:
      - kafka-data:/var/lib/kafka/data

user-management-system:
build: .
ports:
- "8080:8080"
environment:
SPRING_PROFILES_ACTIVE: docker
depends_on:
mysql:
condition: service_healthy
kafka:
condition: service_healthy

volumes:
mysql-data:
kafka-data:

Running the Application
Locally
./mvnw clean install
./mvnw spring-boot:run

Using Docker
docker-compose up --build


Spring Boot app available at http://localhost:8080

MySQL: localhost:3306
Kafka: localhost:9092
Authentication & JWT
All secure endpoints require Authorization: Bearer <JWT_TOKEN> header.

Default admin credentials:
Email: admin@example.com
Password: Admin@123

Notes

📝 Notes & Known Issues
🔹 Swagger UI

Swagger UI is enabled at:
/swagger-ui.html

⚠️ Currently not working properly with JWT Authorization header.
Swagger does not pass the Bearer token correctly due to Spring Security config.

👉 Recommendation: Use Postman or cURL for all authenticated requests until Swagger integration is fixed.
🔹 Spring Cache (Performance Optimization)

The endpoint GET /api/users/me is cached to reduce database reads.

@Cacheable(value = "user-profile", key = "#authentication.name")
When user data is updated, the cache is invalidated:
@CacheEvict(value = "user-profile", key = "#user.email")

Ensures faster responses and reduced DB load for repeated profile fetches.