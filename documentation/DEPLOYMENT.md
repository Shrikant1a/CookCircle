# Deployment, Environment Configuration & Setup Guide
## CookCircle - Operational & Infrastructure Runbook

---

### 1. Prerequisites & System Requirements

- **Operating System:** Windows, macOS, or Linux
- **Runtime Environment:**
  - Java JDK 17 or higher (tested with Oracle JDK 18 / OpenJDK 21)
  - Node.js 18.x or 20+ and npm 9+
  - MySQL 8.0+ (or Docker Engine 24+)
- **Build Tools:**
  - Maven 3.8+ (or Maven Wrapper `./mvnw`)
  - Vite 5+ (managed via npm)

---

### 2. Environment Variables Specification

#### 2.1 Backend (`backend/src/main/resources/application.properties` or system env)
```properties
# Server
server.port=8080
server.servlet.context-path=/

# Database (MySQL 8)
spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:cookcircle_db}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=${DB_USER:cookcircle_user}
spring.datasource.password=${DB_PASS:cookcircle_pass}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# JWT Security
jwt.secret=${JWT_SECRET:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}
jwt.expiration.ms=86400000

# Razorpay Test Gateway
razorpay.key.id=${RAZORPAY_KEY_ID:rzp_test_CookCircle2026}
razorpay.key.secret=${RAZORPAY_KEY_SECRET:test_secret_cookcircle_xyz789}
```

#### 2.2 Frontend (`frontend/.env`)
```properties
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_RAZORPAY_KEY_ID=rzp_test_CookCircle2026
```

---

### 3. Step-by-Step Local Deployment

#### Step 3.1: Start MySQL Database via Docker Compose
```bash
docker-compose up -d
```
Verify container status:
```bash
docker ps
```

#### Step 3.2: Build and Run Spring Boot Backend
```bash
cd backend
./mvnw clean install -DskipTests
./mvnw spring-boot:run
```
The API documentation and Swagger UI will be available at:
`http://localhost:8080/swagger-ui.html`

#### Step 3.3: Install and Launch React Frontend
```bash
cd frontend
npm install
npm run dev
```
Open your browser at `http://localhost:5173`.

