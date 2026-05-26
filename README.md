## Technical Domain Separation
The application separates concerns across two clear core contexts:
- **Loan Domain**: Manages loan creation, details retrieval, and balance auditing.
- **Payment Domain**: Intercepts repayments, validates balances to enforce overpayment guards, and triggers status updates.

### REST API Definition

#### 1. Loan Endpoints (`/loans`)
* **`POST /loans`** (Originate Loan)
  * Body: `application/json`
  * Payload:
    ```json
    {
      "loanAmount": 10000.0,
      "term": 12
    }
    ```
  * Response: `201 Created` with newly allocated ID, remaining balance tracking, and status defaulted to `ACTIVE`.

* **`GET /loans/{loanId}`** (Retrieve Loan Details)
  * Path Variable: String `loanId`
  * Response: `200 OK` or `404 Not Found` if missing. Contains amortized remaining balance and status.

#### 2. Payment Endpoints (`/payments`)
* **`POST /payments`** (Record Payment Transaction)
  * Body: `application/json`
  * Payload:
    ```json
    {
      "loanId": "LOAN-1001",
      "paymentAmount": 1250.0
    }
    ```
  * Response: `201 Created` containing payment ID, reducing the remaining balance, and transitioning status to `SETTLED` if fully balanced. Throws a `400 Bad Request` on overpayment conditions.

---

## 1. How to Build & Run the Spring Boot App

### Prerequisites
* **Java SDK 17** or higher matching standard JPA compliance.
* **Apache Maven 3.8+** or standard wrapped Maven installation.

### Configuration
The database persistence config is loaded under `src/main/resources/application.properties` pointing to an in-memory H2 database:
```properties
spring.datasource.url=jdbc:h2:mem:loansdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
```

### Installation & Run Steps
Change directory to the Spring Boot project folder:
```bash
cd springboot-project
```

Compile the modules and resolve packages:
```bash
mvn clean compile
```

Launch the Spring Boot microservice environment:
```bash
mvn spring-boot:run
```
The server will boot up and bind to host listener `http://localhost:8080`.

To view the visual **H2 Database Manager Console**, open your browser and navigate to:
* **H2 Console URL**: `http://localhost:8080/h2-console`
* **JDBC URL**: `jdbc:h2:mem:loansdb`
* **Username**: `sa`
* **Password**: `password`

---

## 2. Running JUnit Test Cases

Unit assertions target business logic behaviors (balance updates, status transitions, and exception rules).

To execute the full test suite (`LoanSystemApplicationTests`):
```bash
mvn test
```

### Assertions covered in JUnit classes:
1. `testLoanCreationSucceeds`: Asserts attributes match inputs, tracking state defaults, and generated outputs.
2. `testPaymentReducesBalanceCorrectly`: Registers valid loan and subsequent payment, asserting correct arithmetic deduction.
3. `testOverpaymentRaisesException`: Attempts overpayment, asserting that an error status code (`400`) and message are raised.
4. `testFullyPaidStatusMovesToSettled`: Repays a loan in full, asserting that status successfully transitions to `SETTLED`.

---
